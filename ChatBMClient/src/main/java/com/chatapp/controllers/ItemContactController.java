package com.chatapp.controllers;

import com.chatapp.callbacks.Callback;
import com.chatapp.constants.Commands;
import com.chatapp.mediator.Mediator;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class ItemContactController extends Controller {

    @FXML
    Label nicknameLabel;
    private Mediator mediator;
    @FXML
    private HBox item;
    @FXML
    private Parent view;
    @FXML
    private ImageView notificationImg;
    @FXML
    private ImageView profilePicture;

    private Callback callback = () -> {
    };

    public void onBorrarSubMenu() {
        if (getNicknameLabelText().startsWith("#")) {
            mediator.sendMessage(Commands.PART.name() + " " + getNicknameLabelText());
        } else {
            mediator.sendMessage(Commands.DELETE.name() + " " + getNicknameLabelText());
        }
        mediator.deleteContactItem(getNicknameLabelText());
        mediator.getChatController().getChatLabelPicture().setVisible(false);
    }

    public void showNotificationImg(boolean b) {
        notificationImg.setVisible(b);
    }

    @FXML
    public void onClickItem() {
        callback.run();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public String getNicknameLabelText() {
        return nicknameLabel.getText();
    }

    public void setNicknameLabelText(String nickNameLabel) {
        this.nicknameLabel.setText(nickNameLabel);
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    public Parent getView() {
        return view;
    }

    public void setView(Parent view) {
        this.view = view;
    }

    public void setChannelProfilePicture() {
        profilePicture.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/channel_picture2.png"))));
    }

    public void setUserProfilePicture(char letter) {
        profilePicture.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/letters/letra-" + letter + ".png".toLowerCase()))));
    }

    public void setAIProfilePicture() {
        profilePicture.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ai.png"))));
    }
}