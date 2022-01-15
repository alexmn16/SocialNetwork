module retea.reteadesocializare {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.pdfbox;

    opens retea.reteadesocializare to javafx.fxml;
    exports retea.reteadesocializare;
}