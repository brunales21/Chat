package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ItemContactController {

    @FXML
    Label nickNameLabel;
    @FXML
    private HBox item;

    private Callback callback = ()->{};

    @FXML
    public void onClickItem(MouseEvent event){
        callback.run();
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }
    public String getNickNameLabel() {
        return nickNameLabel.getText();
    }

    public void setNickNameLabel(String nickNameLabel) {
        this.nickNameLabel.setText(nickNameLabel);
    }

}
