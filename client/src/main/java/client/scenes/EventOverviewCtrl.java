package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Expense;
import commons.Participant;
import commons.Quote;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class EventOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObservableList<Quote> data;

    @FXML
    private HBox participants;
    @FXML
    private ComboBox eventPayers;
    @FXML
    private TableView table;
    @FXML
    private TableColumn<Expense, String> expense;
//    @FXML
//    private TableColumn<Quote, String> colLastName;
//    @FXML
//    private TableColumn<Quote, String> colQuote;

    @Inject
    public EventOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        participants.setCellFactory(q -> new SimpleStringProperty( q.getName()));
//        eventPayers.setCellFactory(q -> new SimpleStringProperty( q.getValue().getName()));
//
//        expense.setCellValueFactory(q -> new SimpleStringProperty( q.getValue().getPayer()));
//        colLastName.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().person.lastName));
//        colQuote.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().quote));
    }

    public void addParticipant() {
        mainCtrl.showContactDetailAdd();
    }

    public void editParticipant() {
        mainCtrl.showContactDetailEdit(new Participant("name", "email", "iibun"));
    }
}