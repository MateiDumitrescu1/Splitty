package client.scenes;

import client.utils.LanguageUtils;
import client.utils.ServerUtils;
import commons.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.google.inject.Inject;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StarterPageCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
//    @FXML
//    private Button languageButtonStart;
    @FXML
    private Button createButton;
    @FXML
    private Button joinButton;
    @FXML
    private Button deleteHistoryButton;
    @FXML
    private Label createNewEventLabel;
    @FXML
    private Label joinEventLabel;
    @FXML
    private Label recentlyViewedEventsLabel;
    @FXML
    private Button changeServerButton;

    @FXML
    private TextField createNewEvent;


    @FXML
    private TextField joinEvent;

    @FXML
    private ListView<Event> listView;

//    @FXML
//    private ImageView flagView;
    @FXML
    private Label serverLabel;
    @FXML
    private List<ContextMenu> contextMenuList;
    @FXML
    private Button undoButton;
    private String eventName;
    private List<Event> eventList;
    private Stack<Event> deletedEventsStack = new Stack<>();
    @FXML
    private ComboBox<Label> languageComboBox;

    private Label lastSelectedLanguage;

//    private String en;
//    private List<String> languages = List.of("en", "nl"); // add languages here

    @Inject
    public StarterPageCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.eventList = new ArrayList<>();
        this.listView = new ListView<>();
        this.contextMenuList = new ArrayList<>();
    }

//    public StarterPageCtrl(ServerUtils server, List<Event> list) {
//        this.server = server;
//        this.eventList = list;
//    }

    @FXML
    public void initialize() {
        // Set mouse click event listener for the ListView
        listView.setOnMouseClicked(this::handleListViewClick);
        listView.setOnKeyPressed(this::handleListViewButton);
        initLangComboBox();

        rebindUI();

        this.serverLabel.setText(server.getServer());
        //server.registerForUpdates(e -> {
        //    System.out.println("THIISSSSSSS");
        //    updateEvents(e);
        //});
    }

    public void stop() {
        server.stop();
    }

    private void addToDeletedStack(Event event) {
        deletedEventsStack.push(event);
        if (deletedEventsStack.size() > 5) {
            deletedEventsStack.remove(0);
        }
    }

    public void initLangComboBox(){
        List<Label> labels = new ArrayList<>();
        for (Locale locale : LanguageUtils.getSupportedLocales()) {
            Label label = new Label(locale.getLanguage().toUpperCase());
            ImageView flag = new ImageView(LanguageUtils.getFlag(locale));
            flag.setFitHeight(15);
            flag.setFitWidth(22);
            label.setGraphic(flag);
            labels.add(label);
        }
        labels.add(new Label(mainCtrl.getString("addLanguage")));
        languageComboBox.setItems(FXCollections.observableArrayList(labels));
        languageComboBox.setCellFactory(param -> new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getText());
                    if (item.getGraphic() != null) {
                        ImageView flag = new ImageView(((ImageView)(item.getGraphic())).getImage());
                        flag.setFitHeight(15);
                        flag.setFitWidth(22);
                        setGraphic(flag);
                    }
                }
            }
        });
        languageComboBox.setOnAction(null);
        languageComboBox.getSelectionModel().select(labels.stream().filter(l ->
                l.getText().equals(LanguageUtils.getLocale().getLanguage().toUpperCase())).findFirst().orElse(null));
        languageComboBox.setOnAction(e -> {
            switchLanguageComboBox();
        });
        lastSelectedLanguage = languageComboBox.getSelectionModel().getSelectedItem();
    }

    public void switchLanguageComboBox() {
        Label selected = languageComboBox.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getText().equals(mainCtrl.getString("addLanguage"))) {
                // the button will revert to the lastly selected language
                String dir = System.getProperty("user.dir");
                if(dir.endsWith("client")) {
                    dir = dir.substring(0, dir.length() - 7);
                }
                Path fileToCopy = Paths.get(dir.concat("/client/src/main/resources/language/AddLanguageKit.zip"));
                Path target = Paths.get(dir.concat("/AddLanguageKit.zip"));
                try {
                    Files.copy(fileToCopy, target);
                    InfoMessage.showInfo(mainCtrl.getString("addLanguageMessage"), mainCtrl);
                } catch (IOException ex) {
                    if(ex.getClass().equals(FileAlreadyExistsException.class))
                        ErrorMessage.showError(mainCtrl.getString("addLanguageErrorExists"), mainCtrl);
                    else
                        ErrorMessage.showError(mainCtrl.getString("addLanguageError"), mainCtrl);
                } finally {
                    languageComboBox.setOnAction(null);
                    languageComboBox.getSelectionModel().select(lastSelectedLanguage);
                    languageComboBox.setOnAction(e -> {
                        switchLanguageComboBox();
                    });
                }
            } else {
                if(LanguageUtils.getSupportedLocales().contains(new Locale(selected.getText().toLowerCase()))) {
                    LanguageUtils.setLocale(new Locale(selected.getText().toLowerCase()));
                    lastSelectedLanguage = languageComboBox.getSelectionModel().getSelectedItem();
                    rebindUI();
                } else {
                    ErrorMessage.showError(mainCtrl.getString("languageNotSupported"), mainCtrl);
                }
            }
        }
    }

    public void undoDelete() {
        if (!deletedEventsStack.isEmpty()) {
            Event lastDeletedEvent = deletedEventsStack.pop();
            eventList.add(lastDeletedEvent);
            updateHistory();
        }
    }

    private void handleListViewButton(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Event selectedEvent = listView.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                mainCtrl.showEventOverview(selectedEvent);
                listView.getSelectionModel().clearSelection();
            }
        }
    }

    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.setTitle(mainCtrl.getString("confirmation"));
        alert.initOwner(listView.getScene().getWindow());
        alert.showAndWait();
        return alert.getResult() == ButtonType.OK;
    }


    private void handleListViewClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) { // Right-click
            Event selectedEvent = listView.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                // Create ContextMenu
                if (!contextMenuList.isEmpty()) {
                    contextMenuList.get(0).hide();
                    contextMenuList.remove(0);
                }
                ContextMenu contextMenu = new ContextMenu();

                MenuItem deleteMenuItem = new MenuItem(mainCtrl.getString("delete"));
                deleteMenuItem.setOnAction(e -> {
                    if (showConfirmationDialog(mainCtrl.getString("confirmationMessageDelete"))) {
                        eventList.remove(selectedEvent);
                        addToDeletedStack(selectedEvent);
                        updateHistory();
                    }
                });
                contextMenu.getItems().add(deleteMenuItem);
                contextMenuList.add(contextMenu);

                // Display ContextMenu
                contextMenu.show(listView, event.getScreenX(), event.getScreenY());
            }
        }
        if (event.getButton() == MouseButton.PRIMARY) { // Left-click
            Event selectedEvent = listView.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                eventList.remove(selectedEvent);
                eventList.add(selectedEvent);
                updateHistory();
                mainCtrl.showEventOverview(selectedEvent);
                listView.getSelectionModel().clearSelection();
            }
        }
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public List<Event> lastSearch() {
        List<Event> searches = new ArrayList<>();
        for (int i = eventList.size() - 1; i >= 0 && searches.size() < 4; i--)
            searches.add(eventList.get(i));
        return searches;
    }

    public boolean isHistoryEmpty() {
        return eventList.size() == 0;
    }

    public boolean historyContains(String name) {
        return eventList.contains(name);
    }

    public ObservableList<Event> reverseObservableList(ObservableList<Event> originalList) {
        ObservableList<Event> reversedList = FXCollections.observableArrayList();
        for (int i = originalList.size() - 1; i >= 0; i--) {
            reversedList.add(originalList.get(i));
        }
        return reversedList;
    }

    public void updateHistory() {
        ObservableList<Event> observableEventList = FXCollections.observableArrayList(eventList);
        ObservableList<Event> reversedList = reverseObservableList(observableEventList);
        listView.setItems(FXCollections.observableList(reversedList));
        listView.refresh();
    }

    public void updateEvents(Event event) {
        // updating all the events and their expenses and participants
        List<Event> updatedList = new ArrayList<Event>(eventList.size());
        for (Event i : eventList) {
            Event e = server.getEvent(i.getId());
            //e.setExpenses(server.getAllExpensesFromEvent(e));
            //e.setParticipants(server.getAllParticipantsFromEvent(e));
            updatedList.add(e);
        }
        this.eventList = updatedList;
        updateHistory();
    }

    public void createNewEvent() {
        eventName = createNewEvent.getText();
        Event newEvent = new Event(eventName);

        Event repEvent = server.addEvent(newEvent);
        ObservableList<Event> observableEventList = FXCollections.observableArrayList(eventList);
        listView.setItems(FXCollections.observableList(observableEventList));
        listView.refresh();
        eventList.add(repEvent);
        createNewEvent.clear();
        updateHistory();

        mainCtrl.showEventOverview(repEvent);
    }

    public void joinEvent() {
        try {
            long eventId = Long.parseLong(joinEvent.getText());
            Event event = server.getEvent(eventId);

            if (event != null) {
                for (Event e : eventList) {
                    if (e.getId() == event.getId()) {
                        eventList.remove(e);
                    }
                }
                eventList.add(event);
                joinEvent.clear();
                updateHistory();
                mainCtrl.showEventOverview(event);
            }
        } catch (jakarta.ws.rs.BadRequestException e) {
            // Handle the HTTP 400 exception
            ErrorMessage.showError(mainCtrl.getString("inexistentCodeMessage"), mainCtrl);
        } catch (java.lang.NumberFormatException e) {
            // Handle the number format exception
            ErrorMessage.showError(mainCtrl.getString("invalidCode"), mainCtrl);
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.isControlDown() && e.getCode() == KeyCode.Z) {
            undoDelete();
        } else if (e.getSource() instanceof TextField) {
            TextField source = (TextField) e.getSource();
            if (e.getCode() == KeyCode.ENTER) {
                if (source == createNewEvent) {
                    createNewEvent();
                } else if (source == joinEvent) {
                    joinEvent();
                }
            }
        }
    }

//    public void languageSwitch() {
//        mainCtrl.changeLanguage();
//
//        rebindUI();
//    }

    private void rebindUI() {
        mainCtrl.setPrimaryStageTitle(mainCtrl.getString("starterPage"));
//        LanguageUtils.update(languageButtonStart, "LG");
        LanguageUtils.update(createButton, "create");
        LanguageUtils.update(joinButton, "join");
        LanguageUtils.update(deleteHistoryButton, "deleteHistory");
        LanguageUtils.update(createNewEventLabel, "createNewEvent");
        LanguageUtils.update(joinEventLabel, "joinEvent");
        LanguageUtils.update(recentlyViewedEventsLabel, "recentlyViewedEvents");
        LanguageUtils.update(changeServerButton, "changeServer");
//        LanguageUtils.update(flagView);
        LanguageUtils.update(undoButton, "undo");
        initLangComboBox();
    }

    /*
        public void language(){
            if(en.equals("en")){
                en();
            }
            else if(en.equals("nl")){
                nl();
            }
        }
        public void en(){
            languageButtonStart.setText("EN");
            createButton.setText("Create");
            joinButton.setText("Join");
            deleteHistoryButton.setText("Delete history");
            createNewEventLabel.setText("Create New Event");
            joinEventLabel.setText("Join Event");
            recentlyViewedEventsLabel.setText("Recently viewed events");
            changeServerButton.setText("Change Server");
        }
        public void nl(){
            languageButtonStart.setText("NL");
            createButton.setText("Cre\u00ebren");
            joinButton.setText("Meedoen");
            deleteHistoryButton.setText("Verwijder geschiedenis");
            createNewEventLabel.setText("Nieuw evenement maken");
            joinEventLabel.setText("Doe mee aan evenement");
            recentlyViewedEventsLabel.setText("Recent bekeken evenementen");
            changeServerButton.setText("Server Wijzigen");
        }
    */
    public void refresh() {
        ObservableList<Event> observableEventList = FXCollections.observableArrayList(eventList);
        listView.setItems(observableEventList);
    }

    public void clear() {
        createNewEvent.clear();
        joinEvent.clear();
        eventList.clear();
        listView.getItems().clear();
    }

    public void deleteHistory() {
        if (showConfirmationDialog(mainCtrl.getString("confirmationMessageDelete"))) {
            deletedEventsStack.addAll(eventList);
            eventList.clear();
            listView.getItems().clear();
        }
    }

    private void updateUndoButtonState() {
        undoButton.setDisable(deletedEventsStack.isEmpty());
    }

    public void admin() {
        mainCtrl.showAdminLogin();
    }

    public void changeServer() {
        mainCtrl.showChangeServer();
    }

    public Label getServerLabel() {
        return serverLabel;
    }

}
