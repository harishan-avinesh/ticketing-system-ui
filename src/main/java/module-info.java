module ticketing.system {
    // JavaFX module dependencies
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;

    // Export packages to make them accessible
    exports application to javafx.graphics;
    exports core to javafx.base;

    // Open packages for reflection and runtime modification
    opens application to javafx.graphics;
    opens core to javafx.base;
}