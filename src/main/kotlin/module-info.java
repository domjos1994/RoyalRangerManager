module de.dojodev.royalrangermanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires org.mybatis;
    requires org.xerial.sqlitejdbc;
    requires java.sql;
    requires org.slf4j;
    requires java.prefs;

    opens de.dojodev.royalrangermanager to javafx.fxml;
    opens de.dojodev.royalrangermanager.controller to javafx.fxml;
    opens de.dojodev.royalrangermanager.db.model;
    opens de.dojodev.royalrangermanager.db.mapper;
    exports de.dojodev.royalrangermanager;
}