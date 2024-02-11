package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ItemContactController {

    @FXML
    Label nicknameLabel;
    @FXML
    private HBox item;


    private Callback callback = () -> {
    };

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

    public void setNicknameLabel(Label nicknameLabel) {
        this.nicknameLabel = nicknameLabel;
    }

    public HBox getItem() {
        return item;
    }

    public void setItem(HBox item) {
        this.item = item;
    }

    public Callback getCallback() {
        return callback;
    }


}
