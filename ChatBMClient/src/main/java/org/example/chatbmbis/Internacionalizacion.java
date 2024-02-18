package org.example.chatbmbis;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Locale;
import java.util.ResourceBundle;

public class Internacionalizacion {

    public static void convertIntoOtherLenguaje(String text,Node node){

        //Coger idioma del sistema
        Locale locale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("bundle.messages", locale);

        //Pasar idioma por parametros
        /*Locale.setDefault(new Locale("en"));
        ResourceBundle bundle = ResourceBundle.getBundle("bundle.messages");*/


        String message = bundle.getString(text);

        // Aplicar la internacionalización al nodo según su tipo
        if (node instanceof Label) {
            ((Label) node).setText(message);
        } else if (node instanceof Button) {
            ((Button) node).setText(message);
        } else if (node instanceof TextField){
            ((TextField) node).setPromptText(message);
        }else {


        }

    }
}
