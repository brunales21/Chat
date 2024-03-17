module org.example.chatbmbis {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
    requires okhttp3;


    opens org.example.chatbmbis to javafx.fxml;
    exports org.example.chatbmbis;
    exports org.example.chatbmbis.exceptions;
    opens org.example.chatbmbis.exceptions to javafx.fxml;
}