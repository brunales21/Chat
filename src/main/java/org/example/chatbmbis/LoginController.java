package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController extends Controller {

    @FXML
    Button acceder;
    @FXML
    TextField userName,password;
    private Mediator mediator;

    public LoginController (){

    }
    @FXML
    private void ingresar(){
        mediator.createChatView(userName.getText());
        Stage stage2 = (Stage) this.acceder.getScene().getWindow();
        stage2.close();
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
