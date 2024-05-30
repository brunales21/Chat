package com.chatapp.utils;

import com.chatapp.constants.ErrorTypes;
import javafx.scene.control.TextField;

public class FieldValidator {

    public static boolean validateCredentials(TextField nicknameField, TextField passwordField1, TextField passwordField2) {
        if (invalidName(nicknameField.getText())) {
            WarningWindow.instanceWarningWindow(ErrorTypes.FORBIDDEN_CHARS);
            return false;
        }
        if (nicknameField.getText().isBlank() || passwordField1.getText().isBlank() || (passwordField2 != null && passwordField2.getText().isBlank())){
            WarningWindow.instanceWarningWindow(ErrorTypes.EMPTY_FIELD);
            return false;
        }
        if (passwordField2 != null && !passwordsMatch(passwordField1.getText(), passwordField2.getText())) {
            WarningWindow.instanceWarningWindow(ErrorTypes.PASSWORDS_MISMATCH);
            return false;
        }
        return true;

    }
    public static boolean invalidName(String nickname) {
        return nickname.contains("/") || nickname.contains("\\") || nickname.contains("<") || nickname.contains(">");
    }

    public static boolean passwordsMatch(String password, String password2) {
        return password.equals(password2);
    }
}
