module de.dojodev.royalrangermanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens de.dojodev.royalrangermanager to javafx.fxml;
    opens de.dojodev.royalrangermanager.controller to javafx.fxml;
    exports de.dojodev.royalrangermanager;
}