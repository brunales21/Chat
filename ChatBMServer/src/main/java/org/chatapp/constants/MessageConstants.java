package org.chatapp.constants;

public class MessageConstants {

    public static final String NO_CHANNELS_MSG = Constants.PREFIX + "No existen canales en el servidor.";
    public static final String ONLINE_USERS = "Usuarios en linea:";
    public static final String OFFLINE_USERS = "Usuarios desconectados:";
    public static final String AVAILABLE_CHATBOTS = "Chatbots disponibles:";
    public static final String EMPTY = Constants.PREFIX + "Empty";
    public static final String CHAT_BM_VERSION = "ChatBM version ";
    public static final String STARTUP_MESSAGE = "Listo para chatear. Puede usar el comando " + Commands.HELP + " como ayuda.";

    public static String getWarningMessage1(String subject) {
        return "Para enviar un mensaje a " + subject + " primero debes unirte.";
    }
}
