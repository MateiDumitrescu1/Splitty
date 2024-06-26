package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class ChangeServerCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField serverField;
    @FXML
    private Label title;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    @Inject
    public ChangeServerCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void initialize(){
        serverField.setText(server.getServer());
    }

    public void save(){
        String savedServer = this.serverField.getText();
        String current = server.getServer();

        try {
            server.changeServer(savedServer);
        } catch (Exception e) {
            /*
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Server does not exist or is offline. Please try another one/n/n" + e.getMessage());
            alert.showAndWait();
             */
            ErrorMessage.showError(mainCtrl.getString("invalidServerMessage")+"\n\n" + e.getMessage(), mainCtrl);
            server.changeServer(current);
            return;
        }
        mainCtrl.getStarterPageCtrl().getServerLabel().setText(server.getServer());
        this.serverField.setText("");
        mainCtrl.showStarterPage();
    }
    public void cancel(){
        this.serverField.setText("");
        mainCtrl.showStarterPage();
    }
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                save();
                break;
            case ESCAPE:
                cancel();
                break;
            default:
                break;
        }
    }
    /*
    public void language(String en){
        if(en.equals("en")) en();
        else if(en.equals("nl")) nl();
    }
    public void en(){
        saveButton.setText("Save");
        cancelButton.setText("Cancel");
        title.setText("Change Server");
    }
    public void nl(){
        saveButton.setText("Redden");
        cancelButton.setText("Annuleren");
        title.setText("Server Wijzigen");
    }
     */
}
