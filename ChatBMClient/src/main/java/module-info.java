module org.example.chatbmbis {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.chatapp to javafx.fxml;
    exports com.chatapp;
    exports com.chatapp.utils;
    opens com.chatapp.utils to javafx.fxml;
    exports com.chatapp.internationalization;
    opens com.chatapp.internationalization to javafx.fxml;
    exports com.chatapp.dao;
    opens com.chatapp.dao to javafx.fxml;
    exports com.chatapp.conversation;
    opens com.chatapp.conversation to javafx.fxml;
    exports com.chatapp.mediator;
    opens com.chatapp.mediator to javafx.fxml;
}