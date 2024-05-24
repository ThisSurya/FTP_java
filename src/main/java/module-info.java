module surya.project.eb_ftpjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.apache.commons.net;
    requires jdk.compiler;
    requires java.desktop;

    opens surya.project.eb_ftpjava to javafx.fxml;
    opens surya.project.components to javafx.fxml;
    exports surya.project.eb_ftpjava;
    exports surya.project.components;
}