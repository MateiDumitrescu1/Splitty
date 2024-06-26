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
import commons.Debt;
import commons.Event;
import commons.Participant;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import java.util.List;


public class ContactDetailCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Event event;
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField ibanField;

    @FXML
    private TextField bicField;

    @FXML
    private Button okButton;
    @FXML
    private Button abortButton;
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label ibanLabel;
    @FXML
    private Label bicLabel;
    @FXML
    private Label title;

    private Participant participant;

    @Inject
    public ContactDetailCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /*@Inject
    public ContactDetailCtrl(ServerUtils server, MainCtrl mainCtrl, Participant participant) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.participant = participant;
    }*/

    public void initialize(Event event) {
        this.event = event;
    }

    public void initialize(Event event, Participant participant) {
        this.event = server.getEvent(event.getId());
        this.participant = participant;

        this.nameField.setText(participant.getName());
        this.emailField.setText(participant.getEmail());
        this.ibanField.setText(participant.getIban());
        this.bicField.setText(participant.getBic());
    }

    public void abort() {
        clearFields();
        participant = null;
        mainCtrl.showEventOverview(event);
    }

    //TODO Maybe we can create custom exceptions?
    public void ok() {
        try {
            if(!validateInput())
                throw new WebApplicationException(mainCtrl.getString("invalidInput"));
            if(participant == null){
                Participant par = server.addParticipant(getParticipant());
                for(Participant friend : event.getParticipants()){
                    Debt debt = new Debt(event, par, friend, 0);
                    server.addDebt(debt);
                }

                event.setLastAction("added participant");

                event.addParticipant(par);
                server.editEvent(event);
            } else {
                Participant newParticipant = getParticipant();
                participant.setBic(newParticipant.getBic());
                participant.setEmail(newParticipant.getEmail());
                participant.setIban(newParticipant.getIban());
                participant.setName(newParticipant.getName());
                server.editParticipant(participant);

                List<Participant> listP = server.getAllParticipantsFromEvent(event);
                for(Participant p : listP) if(p.getName().equals(participant.getName())) p = participant;
                event.setParticipants(listP);
                server.editEvent(event);

                event.setLastAction("edited participant");

                participant = null;
            }

        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        clearFields();
        mainCtrl.showEventOverview(event);
    }

    private Participant getParticipant() {
        return new Participant(this.event,
                nameField.getText(),
                emailField.getText(),
                ibanField.getText(),
                bicField.getText());
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
//        return true;
        if(!nameField.getText().matches("([A-Za-z0-9])+"))
            return false;
        if(!emailField.getText().matches("([A-Za-z0-9@.])+"))
            return false;
        if(!ibanField.getText().matches("([A-Za-z0-9])+"))
            return false;
        if(!bicField.getText().matches("([A-Za-z0-9])+"))
            return false;
//        if(!emailField.getText().matches("[A-Za-z.]+@[A-za-z.]+\\.[a-z]+"))
//            return false;
//        // The format is NL01_BANK_2345_6789_10
//        if(!ibanField.getText().matches("[A-Z]{2}[0-9]{2} [A-Z]{4} ([0-9]{4} )* [0-9]{0,4}"))
//            return false;
//        // The bic is substring of iban. For example, if the iban is NL01_BANK_2345_6789_10, the bic is BANK
//        if(!bicField.getText().equals(ibanField.getText().substring(4, 9)))
//            return false;
        return true;
    }
    /*
    public void language(){
        if(en.equals("en")) en();
        else if(en.equals("nl")) nl();
    }
    public void en(){
        nameLabel.setText("Name");
        emailLabel.setText("Email");
        ibanLabel.setText("IBAN");
        bicLabel.setText("BIC");
        okButton.setText("Ok");
        abortButton.setText("Abort");
        title.setText("Participant Details");
    }
    public void nl(){
        nameLabel.setText("Naam");
        emailLabel.setText("Email");
        ibanLabel.setText("IBAN");
        bicLabel.setText("BIC");
        okButton.setText("Ok");
        abortButton.setText("Afbreken");
        title.setText("Deelnemer Details");

    }
     */
}