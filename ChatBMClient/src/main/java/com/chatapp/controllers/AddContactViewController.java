package com.chatapp.controllers;

import com.chatapp.constants.Commands;
import com.chatapp.constants.Constants;
import com.chatapp.constants.ErrorTypes;
import com.chatapp.mediator.Mediator;
import com.chatapp.utils.NodeUtils;
import com.chatapp.utils.ThreadUtils;
import com.chatapp.utils.WarningWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class AddContactViewController extends Controller {

    private final ResourceBundle bundle = ResourceBundle.getBundle(Constants.BUNDLE_MESSAGES, Locale.getDefault());
    private final String channelPrompt = bundle.getString(Constants.CHANNEL_NAME);
    private final String contactPrompt = bundle.getString(Constants.NICKNAME);
    @FXML
    private HBox hbox;
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
        String name = nicknameTextField.getText().toLowerCase();
        if (!name.isEmpty()) {
            if (isCreateChannelBtn()) {
                StringBuilder channelName = new StringBuilder("#").append(name);
                if (mediator.getUser().containsContact(channelName.toString())) {
                    WarningWindow.instanceWarningWindow(ErrorTypes.CHAT_REPEATED_EXCEPTION);
                    return;
                }
                mediator.sendMessage(Commands.CREATE.name() + " " + channelName);
                ThreadUtils.sleep(100);
                if (mediator.successfulAction()) {
                    mediator.addContactItem(mediator.getChatController().getvBoxChannels(), channelName.toString());
                }
            } else if (isCreateContactBtn()) {
                if (mediator.getUser().containsContact(name)) {
                    WarningWindow.instanceWarningWindow(ErrorTypes.CHAT_REPEATED_EXCEPTION);
                    return;
                }
                if (!name.equals(mediator.getUser().getNickname())) {
                    mediator.sendMessage(Commands.CREATE.name() + " " + name);
                    ThreadUtils.sleep(100);
                    if (mediator.successfulAction()) {
                        mediator.addContactItem(mediator.getChatController().getvBoxContacts(), name);
                    }
                }
            }
            NodeUtils.cleanTextField(nicknameTextField);
        }
        Stage stageToClose = (Stage) this.button1.getScene().getWindow();
        stageToClose.close();
    }

    @FXML
    public void onClickButtonRight() {
        String chatName = "#" + nicknameTextField.getText();
        if (!chatName.replaceAll("#", "").isEmpty() && !mediator.getUser().containsContact(chatName)) {
            if (nicknameTextField.getPromptText().equals(channelPrompt)) {
                mediator.sendMessage(Commands.JOIN.name() + " " + chatName);
                ThreadUtils.sleep(100);
                if (mediator.successfulAction()) {
                    mediator.addContactItem(mediator.getChatController().getvBoxChannels(), chatName);
                }
            }
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

    public boolean isCreateChannelBtn() {
        return nicknameTextField.getPromptText().equals(channelPrompt);
    }

    public boolean isCreateContactBtn() {
        return nicknameTextField.getPromptText().equals(contactPrompt);
    }

    public HBox getHbox() {
        return hbox;
    }

    public Stage getStage() {
        return (Stage) this.button1.getScene().getWindow();
    }
}
