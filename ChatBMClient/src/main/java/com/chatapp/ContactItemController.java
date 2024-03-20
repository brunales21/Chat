package com.chatapp;

import com.chatapp.constants.Commands;
import com.chatapp.mediator.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ContactItemController extends Controller {
    private Mediator mediator;
    @FXML
    Label nicknameLabel;
    @FXML
    private HBox item;
    @FXML
    private Parent view;
    @FXML
    private ImageView notificationImg;

    private Callback callback = () -> {
    };

    public void onBorrarSubMenu(ActionEvent actionEvent) {
        if (getNicknameLabelText().startsWith("#")) {
            mediator.sendMessage(Commands.PART.name() + " " + getNicknameLabelText());
        } else {
            mediator.sendMessage(Commands.DELETE.name() + " " + getNicknameLabelText());
        }
        mediator.deleteContactItem(getNicknameLabelText());
    }

    public void showNotificationImg(boolean b) {
        notificationImg.setVisible(b);
    }

    @FXML
    public void onClickItem(MouseEvent event) {
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

    public ImageView getNotificationImg() {
        return notificationImg;
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
}
