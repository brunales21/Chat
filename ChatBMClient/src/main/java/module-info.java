module org.example.chatbmbis {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.chatbmbis to javafx.fxml;
    exports org.example.chatbmbis;
    exports org.example.chatbmbis.utils;
    opens org.example.chatbmbis.utils to javafx.fxml;
}