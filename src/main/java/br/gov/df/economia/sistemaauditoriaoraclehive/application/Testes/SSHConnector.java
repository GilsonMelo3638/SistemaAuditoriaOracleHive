package br.gov.df.economia.sistemaauditoriaoraclehive.application.Testes;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class SSHConnector {
    public static void main(String[] args) {
        String host = "10.69.22.71";
        String user = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");
        String command = "hadoop fs -ls /datalake/raw/DIMP_ORIGINAL/TXT/";

        int verySmall = 0;
        int small = 0;
        int medium = 0;
        int large = 0;
        long totalSize = 0;

        try {
            // Configura o JSch e a sessão SSH
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);

            // Evita a verificação de chave de host (não recomendado para produção)
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // Conecta a sessão SSH
            session.connect();

            // Abre o canal de execução
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            // Obtem a saída do comando
            BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            // Exibe a saída do comando
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(msg);
                // Processa a linha para obter o tamanho do arquivo
                String[] parts = msg.split("\\s+");
                if (parts.length >= 6) {
                    try {
                        long fileSize = Long.parseLong(parts[4]);

                        // Soma o tamanho do arquivo ao total
                        totalSize += fileSize;

                        // Classifica o tamanho do arquivo
                        if (fileSize < 1024) {
                            verySmall++;
                        } else if (fileSize < 1024 * 1024) {
                            small++;
                        } else if (fileSize < 1024 * 1024 * 1024) {
                            medium++;
                        } else {
                            large++;
                        }
                    } catch (NumberFormatException e) {
                        // Ignora linhas que não contêm informações de tamanho de arquivo válidas
                    }
                }
            }

            // Fecha os recursos
            in.close();
            channel.disconnect();
            session.disconnect();

            // Exibe a contagem de arquivos e o tamanho total
            System.out.println("Arquivos muito pequenos: " + verySmall);
            System.out.println("Arquivos pequenos: " + small);
            System.out.println("Arquivos médios: " + medium);
            System.out.println("Arquivos grandes: " + large);
            System.out.println("Tamanho total dos arquivos: " + totalSize + " bytes");
        } catch (Exception e) {
            System.err.println("Erro ao executar comando SSH: " + e.getMessage());
        }
    }
}
