package br.gov.df.economia.sistemaauditoriaoraclehive.application.Testes;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SSHConnectorLerArquivos {
    public static void main(String[] args) {
        // Configurações básicas de conexão SSH
        String host = "10.69.22.71";  // Endereço do host SSH
        String user = System.getenv("HIVE_USERNAME");  // Nome de usuário SSH
        String password = System.getenv("HIVE_PASSWORD");  // Senha do SSH
        String directoryPath = "/datalake/raw/DIMP_ORIGINAL/TXT/";  // Caminho do diretório no servidor remoto

        // Verifica se as variáveis de ambiente necessárias estão configuradas
        if (user == null || password == null) {
            System.err.println("As variáveis de ambiente HIVE_USERNAME e HIVE_PASSWORD não estão configuradas.");
            return;
        }

        List<String> allLines = new ArrayList<>();  // Lista para armazenar todas as linhas lidas dos arquivos
        String outputPath = "X:\\Dados\\DIMP\\reg_9900.txt";  // Caminho do arquivo de saída
        String processedFilesPath = "X:\\Dados\\DIMP\\processed_files.txt";  // Caminho do arquivo que registra os arquivos processados

        int numThreads = 10;  // Número de threads para processamento paralelo
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);  // Pool de threads
        AtomicInteger processedFilesCount = new AtomicInteger(0);  // Contador atômico para a quantidade de arquivos processados

        try {
            // Carregar arquivos já processados
            Set<String> processedFiles = loadProcessedFiles(processedFilesPath);

            // Configuração da sessão SSH principal para listar arquivos
            JSch jsch = new JSch();
            Session mainSession = jsch.getSession(user, host, 22);  // Cria uma nova sessão SSH
            mainSession.setPassword(password);  // Define a senha da sessão SSH

            // Configurações adicionais da sessão SSH
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");  // Desativa a verificação de chave do host
            config.put("GSSAPIAuthentication", "no");  // Desativa a autenticação GSSAPI (Kerberos)
            config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");  // Define os métodos de autenticação preferidos
            mainSession.setConfig(config);

            // Conecta à sessão SSH principal
            try {
                mainSession.connect();
                System.out.println("Conectado ao SSH");
            } catch (JSchException e) {
                System.err.println("Falha ao conectar a sessão principal: " + e.getMessage());
                return;
            }

            // Listar todos os arquivos no diretório
            List<String> files = listFiles(mainSession, directoryPath);

            // Submeter cada arquivo para processamento paralelo
            for (String filePath : files) {
                if (processedFiles.contains(filePath)) {
                    continue; // Pular arquivos já processados
                }

                // Submeter tarefa de processamento para o pool de threads
                executor.submit(() -> {
                    Session session = null;
                    ChannelExec channel = null;
                    try {
                        // Criar uma nova sessão SSH para a thread
                        session = jsch.getSession(user, host, 22);
                        session.setPassword(password);
                        session.setConfig(config);
                        try {
                            session.connect();
                        } catch (JSchException e) {
                            System.err.println("Falha ao conectar sessão para " + filePath + ": " + e.getMessage());
                            return;
                        }

                        // Comando para ler e descompactar o arquivo no servidor remoto
                        String command = "hadoop fs -cat " + filePath + " | bzip2 -dc";
                        List<String> lines = new ArrayList<>();
                        channel = (ChannelExec) session.openChannel("exec");
                        channel.setCommand(command);

                        InputStream commandOutput = channel.getInputStream();
                        InputStream errorStream = channel.getErrStream();
                        channel.connect();

                        // Extrair o nome do arquivo sem as extensões .bz2 e .txt
                        String fileName = new File(filePath).getName().replace(".txt.bz2", "");

                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(commandOutput));
                             BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {

                            String line;
                            // Ler linhas do arquivo e adicionar à lista se começar com "|9900|"
                            while ((line = reader.readLine()) != null) {
                                if (line.startsWith("|9900|")) {
                                    lines.add("|" + fileName + line);
                                }
                            }

                            // Ler e imprimir qualquer mensagem de erro
                            String errorLine;
                            while ((errorLine = errorReader.readLine()) != null) {
                                System.err.println("Erro do comando: " + errorLine);
                            }
                        }

                        // Adicionar linhas lidas à lista principal de forma sincronizada
                        synchronized (allLines) {
                            allLines.addAll(lines);
                            if (allLines.size() >= 10000) { // Salvar a cada 10000 linhas
                                saveLinesToFile(new ArrayList<>(allLines), outputPath);
                                allLines.clear();
                            }
                        }
                        // Atualizar contador de arquivos processados
                        int count = processedFilesCount.incrementAndGet();
                        if (count % 500 == 0) {
                            System.out.println("Quantidade de arquivos processados: " + count);
                        }

                        // Adicionar arquivo processado à lista e salvar
                        synchronized (processedFiles) {
                            processedFiles.add(filePath);
                            saveProcessedFiles(processedFiles, processedFilesPath);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // Desconectar canal e sessão SSH
                        if (channel != null && channel.isConnected()) {
                            channel.disconnect();
                        }
                        if (session != null && session.isConnected()) {
                            session.disconnect();
                        }
                    }
                });
            }

            // Aguardar a conclusão de todas as tarefas submetidas
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            // Escrever qualquer conteúdo restante no arquivo de saída
            if (!allLines.isEmpty()) {
                saveLinesToFile(allLines, outputPath);
            }

            // Imprimir a quantidade total de arquivos processados
            System.out.println("Quantidade total de arquivos processados: " + processedFilesCount.get());

            // Desconectar a sessão SSH principal
            mainSession.disconnect();
        } catch (Exception e) {
            System.err.println("Erro ao executar SSH: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para listar arquivos no diretório remoto
    private static List<String> listFiles(Session session, String directoryPath) throws Exception {
        List<String> files = new ArrayList<>();
        String command = "hadoop fs -ls " + directoryPath;  // Comando para listar arquivos

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        InputStream commandOutput = channel.getInputStream();
        channel.connect();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(commandOutput))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(".txt.bz2")) {
                    // Extrair o caminho completo do arquivo
                    String[] parts = line.split("\\s+");
                    String filePath = parts[parts.length - 1];
                    files.add(filePath);
                }
            }
        }

        commandOutput.close();
        channel.disconnect();

        return files;
    }

    // Método para executar um comando SSH
    private static void executeCommand(Session session, String command, String filePath, List<String> allLines) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        InputStream commandOutput = channel.getInputStream();
        InputStream errorStream = channel.getErrStream();
        channel.connect();

        // Extrair o nome do arquivo sem as extensões .bz2 e .txt
        String fileName = new File(filePath).getName().replace(".txt.bz2", "");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(commandOutput));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("|9900|")) {
                    allLines.add("|" + fileName + line);
                }
            }

            // Ler e imprimir qualquer mensagem de erro
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.err.println("Erro do comando: " + errorLine);
            }
        }

        commandOutput.close();
        channel.disconnect();
    }

    // Método para salvar linhas em um arquivo
    private static void saveLinesToFile(List<String> lines, String outputPath) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    // Método para carregar arquivos já processados
    private static Set<String> loadProcessedFiles(String filePath) {
        Set<String> processedFiles = new HashSet<>();
        File file = new File(filePath);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processedFiles.add(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return processedFiles;
    }

    // Método para salvar arquivos processados
    private static void saveProcessedFiles(Set<String> processedFiles, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String file : processedFiles) {
                writer.write(file);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
