package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddContactViewController extends Controller {

    @FXML
    private TextField nicknameTextField;
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    private Mediator mediator;


    public AddContactViewController() {

    }

    @FXML
    private void onClickButtonLeft() {
        if (!nicknameTextField.getText().isEmpty()) {
            if (nicknameTextField.getPromptText().equals("Nombre canal")) {
                mediator.sendMessage("CREATE #" + nicknameTextField.getText());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (mediator.actionApproved()) {
                    mediator.addContactItem(mediator.getChatController().getvBoxGroup(), "#"+nicknameTextField.getText());
                }
            } else if (nicknameTextField.getPromptText().equals("Nombre usuario")) {
                mediator.sendMessage("CREATE " + nicknameTextField.getText());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (mediator.actionApproved()){
                    mediator.addContactItem(mediator.getChatController().getvBoxPrivate(), nicknameTextField.getText());
                }
            }
            nicknameTextField.setText("");
        }
        Stage stageToClose = (Stage) this.button1.getScene().getWindow();
        stageToClose.close();
    }

    @FXML
    public void onClickButtonRight() {
        if (!nicknameTextField.getText().isEmpty()) {
            if (nicknameTextField.getPromptText().equals("Nombre canal")) {
                mediator.sendMessage("JOIN #"+nicknameTextField.getText());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (mediator.actionApproved()){
                    mediator.addContactItem(mediator.getChatController().getvBoxGroup(), "#"+nicknameTextField.getText());
                }

            } else if (nicknameTextField.getPromptText().equals("Nombre usuario")) {
                mediator.sendMessage("DELETE "+nicknameTextField.getText());
                try {
                    // Este hilo espera para que al servidor le de tiempo a enviar la respuesta para validar la acción.
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (mediator.actionApproved()){
                    mediator.deleteContactItem(nicknameTextField.getText());
                }
            }
            nicknameTextField.setText("");
        }
        Stage stageToClose = (Stage) this.button1.getScene().getWindow();
        stageToClose.close();
    }

    public void setPromptText(String promptText) {
        nicknameTextField.setPromptText(promptText);
    }

    public Button getButton1() {
        return button1;
    }

    public Button getButton2() {
        return button2;
    }

    public void setButton2(Button button2) {
        this.button2 = button2;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
