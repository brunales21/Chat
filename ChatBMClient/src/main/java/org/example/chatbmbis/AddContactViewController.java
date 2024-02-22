package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
            StringBuilder channelName;
            if (nicknameTextField.getPromptText().equals(prompChannelFile)) {
                channelName = new StringBuilder("#");
                channelName.append(name);
                mediator.sendMessage("CREATE " + channelName);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (mediator.actionApproved()) {
                    mediator.getUser().getChatMessagesMap().put(channelName.toString(), null);
                    mediator.addContactItem(mediator.getChatController().getvBoxGroup(), channelName.toString());
                }
            } else if (nicknameTextField.getPromptText().equals(prompPrivFile)) {
                if (!name.equals(mediator.getUser().getNickname())) {
                    mediator.sendMessage("CREATE " + name);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (mediator.actionApproved()) {
                        mediator.getUser().getChatMessagesMap().put(name, null);
                        mediator.getUser().getContacts().add(name);
                        mediator.addContactItem(mediator.getChatController().getvBoxPrivate(), name);
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
        String chatName = nicknameTextField.getText();
        if (!chatName.isEmpty()) {
            if (nicknameTextField.getPromptText().equals(prompChannelFile)) {
                mediator.sendMessage("JOIN #" + chatName);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (mediator.actionApproved()) {
                    mediator.addContactItem(mediator.getChatController().getvBoxGroup(), "#" + chatName);
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
