package org.chatapp.constants;

import java.nio.file.FileSystems;

public class Constants {

    public static final String MSGS_FOLDER_NAME = "messages";
    public static final String FILE_SUFIX = "-messages.csv";
    public static final String LINE_SEPARATOR = FileSystems.getDefault().getSeparator();
    public static final String CHANNEL_PREFIX = "#";
    public static final String PREFIX = "- ";
    public static final String INVALID_CHARACTERS = "-/\\<>";
}
