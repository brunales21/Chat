package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddViewController extends Controller{

    @FXML
    private Button addOrcreate;
    @FXML
    private TextField nickname;
    private Mediator mediator;


    public AddViewController() {

    }

    @FXML
    private void onClickAdd(){
        mediator.getChatController().createItemChat(nickname.getText());
        Stage stageToClose = (Stage) this.addOrcreate.getScene().getWindow();
        stageToClose.close();

    }

    public void setPrompText(String prompText){
        nickname.setPromptText(prompText);
    }
    public Button getAddOrcreate() {
        return addOrcreate;
    }

    public void setAddOrcreate(Button addOrcreate) {
        this.addOrcreate = addOrcreate;
    }

    public TextField getNickname() {
        return nickname;
    }

    public void setNickname(TextField nickname) {
        this.nickname = nickname;
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
