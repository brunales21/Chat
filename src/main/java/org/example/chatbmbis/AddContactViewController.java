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
        if (nicknameTextField.getText().isEmpty()) {
            Stage stageToClose = (Stage) this.addButton.getScene().getWindow();
            stageToClose.close();
            return;
        }
        if (nicknameTextField.getPromptText().equals("Nombre grupo")) {
            mediator.createContactItem("#"+nicknameTextField.getText());
            mediator.sendHeader("CREATE #" + nicknameTextField.getText());
        } else if (nicknameTextField.getPromptText().equals("Nombre del grupo")) {
            mediator.createContactItem("#"+nicknameTextField.getText());
            mediator.sendHeader("JOIN #"+nicknameTextField.getText());
        } else {
            mediator.createContactItem(nicknameTextField.getText());
            mediator.sendHeader("CREATE " + nicknameTextField.getText());

        }
        nicknameTextField.setText("");
        Stage stageToClose = (Stage) this.addButton.getScene().getWindow();
        stageToClose.close();
    }

    public void setPromptText(String promptText) {
        nicknameTextField.setPromptText(promptText);
    }

    public Button getAddButton() {
        return addButton;
    }


    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
