package exercise1;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class PlayerRegistrationForm extends Application {

    @Override
    public void start(Stage primaryStage) {
        Database database = new Database();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        Label playerInformationLabel = new Label("Player Information:");
        playerInformationLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        playerInformationLabel.setPrefWidth(110);
        grid.add(playerInformationLabel, 0, 0);

        Label firstNameLabel = new Label("First Name:");
        grid.add(firstNameLabel, 0, 1);
        TextField firstNameField = new TextField();
        grid.add(firstNameField, 1, 1);

        Label lastNameLabel = new Label("Last Name:");
        grid.add(lastNameLabel, 0, 2);
        TextField lastNameField = new TextField();
        grid.add(lastNameField, 1, 2);

        Label addressLabel = new Label("Address:");
        grid.add(addressLabel, 0, 3);
        TextField addressField = new TextField();
        grid.add(addressField, 1, 3);

        Label provinceLabel = new Label("Province:");
        grid.add(provinceLabel, 0, 4);
        TextField provinceField = new TextField();
        grid.add(provinceField, 1, 4);

        Label postalCodeLabel = new Label("Postal Code:");
        grid.add(postalCodeLabel, 0, 5);
        TextField postalCodeField = new TextField();
        grid.add(postalCodeField, 1, 5);

        Label phoneNumberLabel = new Label("Phone Number:");
        grid.add(phoneNumberLabel, 0, 6);
        TextField phoneNumberField = new TextField();
        grid.add(phoneNumberField, 1, 6);

        Label updatePlayerIdLabel = new Label("Update Player by ID:");
        updatePlayerIdLabel.setPrefWidth(120);
        grid.add(updatePlayerIdLabel, 3, 0);
        TextField updatePlayerIdField = new TextField();
        grid.add(updatePlayerIdField, 4, 0);

        Label gameInformationLabel = new Label("Game Information:");
        gameInformationLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        grid.add(gameInformationLabel, 3, 3,2,1);

        Label gameTitleLabel = new Label("Game Title:");
        grid.add(gameTitleLabel, 3, 4);
        TextField gameTitleField = new TextField();
        grid.add(gameTitleField, 4, 4);

        Label gameScoreLabel = new Label("Game Score:");
        grid.add(gameScoreLabel, 3, 5);
        TextField gameScoreField = new TextField();
        grid.add(gameScoreField, 4, 5);

        Label datePlayedLabel = new Label("Date Played:");
        grid.add(datePlayedLabel, 3, 6);
        DatePicker datePlayedField = new DatePicker();
        grid.add(datePlayedField, 4, 6);

        Button createPlayerButton = new Button("Create Player");
        createPlayerButton.setPrefWidth(200);
        createPlayerButton.setOnAction(event -> {
            if(firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || addressField.getText().isEmpty() || postalCodeField.getText().isEmpty() || provinceField.getText().isEmpty() || phoneNumberField.getText().isEmpty() || gameTitleField.getText().isEmpty() || gameScoreField.getText().isEmpty() || datePlayedField.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all the required fields.");
                Label contentText = (Label) alert.getDialogPane().lookup(".content.label");
                contentText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                alert.showAndWait();
            } else {
                boolean ready = false;
                try {
                    Long.parseLong(phoneNumberField.getText());
                    Integer.parseInt(postalCodeField.getText());
                    Integer.parseInt(gameScoreField.getText());
                    ready = true;
                } catch(NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter only digits in PhoneNumber|Postal code|gameScore ");
                    Label contentText = (Label) alert.getDialogPane().lookup(".content.label");
                    contentText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                    alert.showAndWait();
                }
                if(ready) {
                    int gameId = database.getGameId(gameTitleField.getText());
                    int playerId = database.insertPlayer(firstNameField.getText(),lastNameField.getText(),addressField.getText(),postalCodeField.getText(),provinceField.getText(),phoneNumberField.getText());
                    int playerGameId = database.insertPlayerAndGame(playerId,gameId, Date.valueOf(datePlayedField.getValue()),Integer.parseInt(gameScoreField.getText()));
                    if(playerGameId>=1) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Successful");
                        alert.setHeaderText(null);
                        alert.setContentText("Player and game Details Added");
                        Label contentText = (Label) alert.getDialogPane().lookup(".content.label");
                        contentText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                        alert.showAndWait();
                    }
                    firstNameField.setText("");
                    lastNameField.setText("");
                    addressField.setText("");
                    postalCodeField.setText("");
                    provinceField.setText("");
                    phoneNumberField.setText("");
                    gameTitleField.setText("");
                    gameScoreField.setText("");
                    updatePlayerIdField.setText("");
                    datePlayedField.setValue(null);
                }
            }
        });
        grid.add(createPlayerButton, 4, 9);

        Button updatePlayerIdButton = new Button("Update");
        updatePlayerIdButton.setPrefWidth(120);
        updatePlayerIdButton.setOnAction(event -> {
            boolean result;
            try {
                Integer.parseInt(updatePlayerIdField.getText());
                result = false;
            } catch(NumberFormatException e) {
                result = true;
            }
            if (updatePlayerIdField.getText().isEmpty() || result || !database.playerExists(Integer.parseInt(updatePlayerIdField.getText()))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid Player Id");
                Label contentText = (Label) alert.getDialogPane().lookup(".content.label");
                contentText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                alert.showAndWait();
            } else {
                int updatePlayerId = Integer.parseInt(updatePlayerIdField.getText());
                String firstName = firstNameField.getText().equals("") ? null : firstNameField.getText();
                String lastName = lastNameField.getText().equals("") ? null : lastNameField.getText();
                String address = addressField.getText().equals("") ? null : addressField.getText();
                String postalCode = postalCodeField.getText().equals("") ? null : postalCodeField.getText();
                String province = provinceField.getText().equals("") ? null : provinceField.getText();
                String phoneNumber = phoneNumberField.getText().equals("") ? null : phoneNumberField.getText();
                String gameTitle = gameTitleField.getText().equals("") ? null : gameTitleField.getText();
                int gameScore = gameScoreField.getText().equals("") ? -1 : Integer.parseInt(gameScoreField.getText());
                Date date = datePlayedField.getValue() == null ? null : Date.valueOf(datePlayedField.getValue());
                database.updateData(updatePlayerId,firstName,lastName,address,postalCode,province,phoneNumber,gameTitle,date,gameScore);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Updated");
                alert.setHeaderText(null);
                alert.setContentText("Player and Game details Updated");
                Label contentText = (Label) alert.getDialogPane().lookup(".content.label");
                contentText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                alert.showAndWait();
                firstNameField.setText("");
                lastNameField.setText("");
                addressField.setText("");
                postalCodeField.setText("");
                provinceField.setText("");
                phoneNumberField.setText("");
                gameTitleField.setText("");
                gameScoreField.setText("");
                updatePlayerIdField.setText("");
                datePlayedField.setValue(null);
            }
        });
        grid.add(updatePlayerIdButton, 5, 0);

        Button displayAllPlayersButton = new Button("Display All Players");
        displayAllPlayersButton.setPrefWidth(150);
        displayAllPlayersButton.setOnAction(event -> {
            Stage stage = new Stage();
            stage.setTitle("All Players");

            TableView<Player> table = new TableView<>();
            table.setEditable(false);

            TableColumn<Player, Integer> idColumn = new TableColumn<>("ID");
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Player, String> nameColumn = new TableColumn<>("NAME");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Player, String> addressColumn = new TableColumn<>("ADDRESS");
            addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

            TableColumn<Player, String> postalCodeColumn = new TableColumn<>("POSTAL CODE");
            postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

            TableColumn<Player, String> provinceColumn = new TableColumn<>("PROVINCE");
            provinceColumn.setCellValueFactory(new PropertyValueFactory<>("province"));

            TableColumn<Player, String> phoneNumberColumn = new TableColumn<>("PHONE NUMBER");
            phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

            TableColumn<Player, String> gameTitleColumn = new TableColumn<>("GAME TITLE");
            gameTitleColumn.setCellValueFactory(new PropertyValueFactory<>("gameTitle"));

            TableColumn<Player, Integer> scoreColumn = new TableColumn<>("SCORE");
            scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

            TableColumn<Player, LocalDate> datePlayedColumn = new TableColumn<>("DATE PLAYED");
            datePlayedColumn.setCellValueFactory(new PropertyValueFactory<>("datePlayed"));

            table.getColumns().addAll(idColumn, nameColumn, addressColumn, postalCodeColumn, provinceColumn, phoneNumberColumn, gameTitleColumn, scoreColumn, datePlayedColumn);

            ObservableList<Player> data = database.getData();
            table.setItems(data);

            Scene scene = new Scene(table);
            stage.setScene(scene);
            stage.show();
        });
        grid.add(displayAllPlayersButton, 5, 9);

        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Player Registration Form");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

