module com.example.dechivejavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires opencsv; // Adiciona a dependência para a biblioteca OpenCSV

    opens com.example.dechivejavafx to javafx.fxml;
    opens com.example.dechivejavafx.gui to javafx.fxml;
    opens com.example.dechivejavafx.model.entities to javafx.base;

    exports com.example.dechivejavafx.application;
    exports com.example.dechivejavafx.gui;
    exports com.example.dechivejavafx.Validacoes;
    exports com.example.dechivejavafx.application.Testes;
}
