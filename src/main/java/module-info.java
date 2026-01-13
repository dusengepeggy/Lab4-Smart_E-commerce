module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jbcrypt;
    requires org.postgresql.jdbc;



    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports  com.example.demo.model;
    exports com.example.demo.dao;
    exports  com.example.demo.utils;
    exports com.example.demo.dbConnection;
    exports  com.example.demo.controllers;

    opens com.example.demo.launcher to javafx.fxml;
    opens com.example.demo.controllers to javafx.fxml;
}