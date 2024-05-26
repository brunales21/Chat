module com.chatapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.chatapp to javafx.fxml;
    exports com.chatapp.utils;
    opens com.chatapp.utils to javafx.fxml;
    exports com.chatapp.internationalization;
    opens com.chatapp.internationalization to javafx.fxml;
    exports com.chatapp.daos;
    opens com.chatapp.daos to javafx.fxml;
    exports com.chatapp.model;
    opens com.chatapp.model to javafx.fxml;
    exports com.chatapp.mediator;
    opens com.chatapp.mediator to javafx.fxml;
    exports com.chatapp.controllers;
    opens com.chatapp.controllers to javafx.fxml;
    exports com.chatapp.daos.impl;
    opens com.chatapp.daos.impl to javafx.fxml;
    exports com.chatapp.app;
    opens com.chatapp.app to javafx.fxml;
    exports com.chatapp.callbacks;
    opens com.chatapp.callbacks to javafx.fxml;
}