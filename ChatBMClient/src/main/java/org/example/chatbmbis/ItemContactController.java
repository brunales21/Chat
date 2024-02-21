package org.example.chatbmbis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ItemContactController {

    private Mediator mediator;
    @FXML
    Label nicknameLabel;
    @FXML
    private HBox item;


    private Callback callback = () -> {
    };

    public void onBorrarSubMenu(ActionEvent actionEvent) {
        if (getNicknameLabelText().startsWith("#")) {
            // si es un canal lo queremos borrar de local (de momento nadie tiene permisos de eliminar canal)
            mediator.deleteContact(getNicknameLabelText());
        } else {
            // si es un chatprivado lo borramos de servidor tambien
            mediator.sendMessage("DELETE " + getNicknameLabelText());
            try {
                // Este hilo espera para que al servidor le de tiempo a enviar la respuesta para validar la acción.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (mediator.actionApproved()) {
                mediator.deleteContact(getNicknameLabelText());
            }
        }

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

    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
