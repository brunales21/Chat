module org.example.chatbmbis {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.chatbmbis to javafx.fxml;
    exports org.example.chatbmbis;
}