module org.chatapp {
    requires java.net.http;
    requires org.json;
    requires okhttp3;
    requires java.sql;

    exports org.chatapp.exceptions;
    exports org.chatapp.utils;
    exports org.chatapp.db;
    exports org.chatapp.constants;
    exports org.chatapp.model;
    exports org.chatapp.server;
    exports org.chatapp.security;
}