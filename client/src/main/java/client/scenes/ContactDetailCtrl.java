/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Participant;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

public class ContactDetailCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField ibanField;

    @FXML
    private TextField bicField;

    private Participant participant;

    @Inject
    public ContactDetailCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Inject
    public ContactDetailCtrl(ServerUtils server, MainCtrl mainCtrl, Participant participant) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.participant = participant;
        initFields();
    }

    //TODO also add the BIC field
    public void initFields(){
        this.nameField.setText(participant.getName());
        this.emailField.setText(participant.getEmail());
        this.ibanField.setText(participant.getIban());
        this.bicField.setText(null);
    }

    public void abort() {
        clearFields();
        mainCtrl.showOverview();
    }

    //TODO Maybe we can create custom exceptions?
    public void ok() {
        try {
            if(!validateInput())
                throw new WebApplicationException("Invalid input!");
            if(participant == null){
                server.addParticipant(getParticipant());
            }else {
                server.editParticipant(participant, getParticipant());
            }
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        clearFields();
        mainCtrl.showOverview();
    }

    //TODO also add the BIC field
    private Participant getParticipant() {
        return new Participant(nameField.getText(),
                emailField.getText(),
                ibanField.getText());
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        ibanField.clear();
        bicField.clear();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                ok();
                break;
            case ESCAPE:
                abort();
                break;
            default:
                break;
        }
    }

    public boolean validateInput(){
        if(!nameField.getText().matches("[A-Z][a-z]+"))
            return false;
        if(!emailField.getText().matches("[A-Za-z.]+@[A-za-z.]+\\.[a-z]+"))
            return false;
        // The format is NL01_BANK_2345_6789_10
        if(!ibanField.getText().matches("[A-Z]{2}[0-9]{2} [A-Z]{4} ([0-9]{4} )* [0-9]{0,4}"))
            return false;
        if(!bicField.getText().equals(ibanField.getText().substring(5, 9)))
            return false;
        return true;
    }
}