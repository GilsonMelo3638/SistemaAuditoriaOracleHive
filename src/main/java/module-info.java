module com.example.dechivejavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires opencsv;
    requires jsch; // Adiciona a dependência para a biblioteca OpenCSV

    opens br.gov.df.economia.sistemaauditoriaoraclehive to javafx.fxml;
    opens br.gov.df.economia.sistemaauditoriaoraclehive.gui to javafx.fxml;
    opens br.gov.df.economia.sistemaauditoriaoraclehive.model.entities to javafx.base;

    exports br.gov.df.economia.sistemaauditoriaoraclehive.application;
    exports br.gov.df.economia.sistemaauditoriaoraclehive.gui;
    exports br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes;
    exports br.gov.df.economia.sistemaauditoriaoraclehive.application.Testes;
    exports br.gov.df.economia.sistemaauditoriaoraclehive.application.Testes.Sped;
}
