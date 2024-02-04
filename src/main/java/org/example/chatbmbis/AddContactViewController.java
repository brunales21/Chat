package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddContactViewController extends Controller {

    @FXML
    private Button addButton;
    @FXML
    private TextField nicknameTextField;
    private Mediator mediator;


    public AddContactViewController() {

    }

    @FXML
    private void onClickAdd() {
        mediator.createContactItem(nicknameTextField.getText());
        Stage stageToClose = (Stage) this.addButton.getScene().getWindow();
        stageToClose.close();
    }

    public void setPromptText(String prompText) {
        nicknameTextField.setPromptText(prompText);
    }

    public Button getAddButton() {
        return addButton;
    }


    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
