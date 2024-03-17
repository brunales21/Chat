package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.chatbmbis.constants.Commands;
import org.example.chatbmbis.utils.ThreadUtils;

import java.util.Locale;
import java.util.ResourceBundle;

public class AddContactViewController extends Controller {

    @FXML
    private TextField nicknameTextField;
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    private Mediator mediator;

    private ResourceBundle bundle = ResourceBundle.getBundle("bundle.messages", Locale.getDefault());
    private String prompChannelFile = bundle.getString("promChannel");
    private String prompPrivFile = bundle.getString("promPriv");

    public AddContactViewController() {

    }

    @FXML
    private void onClickButtonLeft() {
        String name = nicknameTextField.getText();
        if (!name.isEmpty()) {
            StringBuilder channelName = new StringBuilder("#").append(name);
            if (isCreateChannelBtn()) {
                mediator.sendMessage(Commands.CREATE.name() + " " + channelName);
                ThreadUtils.sleep(100);
                if (mediator.successfulAction()) {
                    mediator.addContactItem(mediator.getChatController().getvBoxChannels(), channelName.toString());
                }
            } else if (isCreateContactBtn()) {
                if (!name.equals(mediator.getUser().getNickname())) {
                    mediator.sendMessage(Commands.CREATE.name() + " " + name);
                    ThreadUtils.sleep(100);
                    if (mediator.successfulAction()) {
                        mediator.addContactItem(mediator.getChatController().getvBoxContacts(), name);
                    }
                }
            }
            nicknameTextField.setText("");
        }
        Stage stageToClose = (Stage) this.button1.getScene().getWindow();
        stageToClose.close();
    }

    @FXML
    public void onClickButtonRight() {
        String chatName = "#" + nicknameTextField.getText();
        if (!chatName.replaceAll("#", "").isEmpty() && !mediator.getUser().containsContact(chatName)) {
            if (nicknameTextField.getPromptText().equals(prompChannelFile)) {
                mediator.sendMessage(Commands.JOIN.name() + " " + chatName);
                ThreadUtils.sleep(100);
                if (mediator.successfulAction()) {
                    mediator.addContactItem(mediator.getChatController().getvBoxChannels(), chatName);
                }
            }
        }
        nicknameTextField.setText("");
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

    public boolean isCreateChannelBtn() {
        return nicknameTextField.getPromptText().equals(prompChannelFile);
    }

    public boolean isCreateContactBtn() {
        return nicknameTextField.getPromptText().equals(prompPrivFile);
    }
}
