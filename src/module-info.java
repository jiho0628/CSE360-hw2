module FoundationCode {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.base;
	
	opens application to javafx.graphics, javafx.fxml, javafx.base;
}
