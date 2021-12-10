module retea.reteadesocializare {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens retea.reteadesocializare to javafx.fxml;
    exports retea.reteadesocializare;
}