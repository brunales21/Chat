package com.chatapp.internationalization;

import com.chatapp.constants.Constants;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Locale;
import java.util.ResourceBundle;

public class Internationalization {

    public static void convertIntoOtherLanguage(String text, Node node) {
        ResourceBundle bundle = ResourceBundle.getBundle(Constants.BUNDLE_MESSAGES, Locale.getDefault());
        String message = bundle.getString(text);
        if (node instanceof Label) {
            ((Label) node).setText(message);
        } else if (node instanceof Button) {
            ((Button) node).setText(message);
        } else if (node instanceof TextField) {
            ((TextField) node).setPromptText(message);
        }
    }
}