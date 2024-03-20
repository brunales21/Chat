module org.example.chatbmbis {
    requires java.net.http;
    requires org.json;
    requires okhttp3;

    exports org.example.chatbmbis;
    exports org.example.chatbmbis.exceptions;
    exports org.example.chatbmbis.Utils;
}