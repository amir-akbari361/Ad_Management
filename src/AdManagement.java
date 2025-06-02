import DAL.DatabaseConnection;
import Model.*;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdManagement extends Application {
    private GridPane adsGrid = new GridPane();
    private List<User> users = new ArrayList<>();
    private List<Ad> ads = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private User loggedInUser;


    @Override
    public void init() throws Exception {
        users = DatabaseConnection.loadUsers();
        ads = DatabaseConnection.loadAds();
        messages = DatabaseConnection.loadMessages(users);

        System.out.println("User:" + users.size() + " " + "ad:" + ads.size() + " " + "message:" + messages.size());
    }

    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        HBox searchBar = new HBox(15);
        searchBar.setPadding(new Insets(15));
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setStyle("-fx-background-color: #34495e;");
//سرچ بار
        TextField searchField = new TextField();
        searchField.setPromptText("Search for ads...");
        searchField.setPrefWidth(350);
        searchField.setStyle("-fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        searchButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        searchButton.setOnAction(e -> {
            filterAdsByTitle(searchField.getText());
        });
        Button advancedSearchButton = new Button("Advanced Search");
        advancedSearchButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-border-radius: 5;");
        advancedSearchButton.setOnAction(e -> {
            showAdvancedSearchForm();
        });

        HBox authButtons = new HBox(10);
        authButtons.setAlignment(Pos.CENTER_RIGHT);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-border-radius: 5;");
        loginButton.setOnAction(e -> showLoginForm());

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-border-radius: 5;");
        registerButton.setOnAction(e -> showRegisterForm());

        authButtons.getChildren().addAll(loginButton, registerButton);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchBar.getChildren().addAll(searchField, searchButton, advancedSearchButton, spacer, authButtons);

//سایدبار و کتگوری ها
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: #f7f9fb; -fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label categoryLabel = new Label("Categories");
        categoryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        categoryLabel.setTextFill(Color.web("#2c3e50"));

        Button electronicsButton = createCategoryButton("General");
        electronicsButton.setOnAction(e -> {
            filterAdsByCategory("General");
        });
        Button furnitureButton = createCategoryButton("Electronics");
        furnitureButton.setOnAction(e -> {
                    filterAdsByCategory("Electronics");
                }
        );
        Button vehiclesButton = createCategoryButton("Vehicles");
        vehiclesButton.setOnAction(e ->
                {
                    filterAdsByCategory("Vehicle");
                }
        );
        Button othersButton = createCategoryButton("All");
        othersButton.setOnAction(e ->
                {
                    List<Ad> filterdList = new ArrayList<>(ads);
                    updateAdsGrid(filterdList);
                }
        );

        Label actionsLabel = new Label("Manage Ads");
        actionsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        actionsLabel.setTextFill(Color.web("#2c3e50"));

        Button addAdButton = new Button("Add Ad");
        addAdButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        addAdButton.setOnAction(e -> {
            if (loggedInUser == null) {
                showAlert("error", "You are not logged in!", Alert.AlertType.ERROR);
            } else if (loggedInUser != null) {
                showCategorySelection(primaryStage);
            }
        });

        Button editAdButton = new Button("Edit Ad");
        editAdButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        editAdButton.setOnAction(e -> {
                    if (loggedInUser == null) {
                        showAlert("error", "You are not logged in!", Alert.AlertType.ERROR);
                    } else if (loggedInUser != null) {
                        showUserAdsForEditing();
                    }
                }
        );
        Button deleteAdButton = new Button("Delete Ad");
        deleteAdButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteAdButton.setOnAction(e ->
        {
            if (loggedInUser == null) {
                showAlert("error", "You are not logged in!", Alert.AlertType.ERROR);
            } else if (loggedInUser != null) {
                showUserAdsForDeletion();
            }
        });

        Button messageButton = new Button("View Messages");
        messageButton.setStyle("-fx-background-color: #3c64e8; -fx-text-fill: white; -fx-font-weight: bold;");
        messageButton.setOnAction(e ->
        {
            if (loggedInUser == null) {
                showAlert("error", "You are not logged in!", Alert.AlertType.ERROR);
            } else if (loggedInUser != null) {
                showUserMessages();
            }
        });


        sidebar.getChildren().addAll(categoryLabel, electronicsButton, furnitureButton, vehiclesButton, othersButton, new Separator(), actionsLabel, addAdButton, editAdButton, deleteAdButton, messageButton);

        adsGrid.setHgap(25);
        adsGrid.setVgap(25);
        adsGrid.setPadding(new Insets(20));
        adsGrid.setStyle("-fx-background-color: #ecf0f1;");
        updateAdsGrid(ads);
        ScrollPane scrollPane = new ScrollPane(adsGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: #34495e;");

        Label footerText = new Label("Ad Management System (Amirmahdi Akbari)");
        footerText.setTextFill(Color.WHITE);
        footerText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        footer.getChildren().add(footerText);

        root.setTop(searchBar);
        root.setLeft(sidebar);
        root.setCenter(scrollPane);
        root.setBottom(footer);

        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Ad Management System");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAdvancedSearchForm() {
        Stage advancedSearchStage = new Stage();
        advancedSearchStage.setTitle("Advanced Search");

        VBox formLayout = new VBox(15);
        formLayout.setPadding(new Insets(20));
        formLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Advanced Search");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField titleField = new TextField();
        titleField.setPromptText("Enter ad title");

        TextField minPriceField = new TextField();
        minPriceField.setPromptText("Minimum price");

        TextField maxPriceField = new TextField();
        maxPriceField.setPromptText("Maximum price");

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("All", "Electronics", "Vehicle", "General");
        categoryBox.setValue("All");
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5;");
        searchButton.setOnAction(_ -> {
            List<Ad> filterdList = new ArrayList<>(ads);
            if (!categoryBox.getValue().equals("All")) {
                filterdList.removeIf(ad -> !ad.getCategory().equalsIgnoreCase(categoryBox.getValue()));
            }
            if (!titleField.getText().isEmpty()) {
                filterdList.removeIf(ad -> !ad.getTitle().toLowerCase().contains(titleField.getText().toLowerCase()));
            }
            if (!(minPriceField.getText().isEmpty() || maxPriceField.getText().isEmpty())) {
                filterdList.removeIf(ad -> ad.getPrice() < Double.parseDouble(minPriceField.getText()) || ad.getPrice() > Double.parseDouble(maxPriceField.getText()));
            }
            if (!filterdList.isEmpty()) {
                updateAdsGrid(filterdList);
            } else {
                showAlert("error", "No results found", Alert.AlertType.ERROR);
            }
            advancedSearchStage.close();
        });

        formLayout.getChildren().addAll(titleLabel, titleField, minPriceField, maxPriceField, categoryBox, searchButton);

        Scene advancedSearchScene = new Scene(formLayout, 400, 400);
        advancedSearchStage.setScene(advancedSearchScene);
        advancedSearchStage.show();
    }

    private void filterAdsByCategory(String category) {
        List<Ad> filteredAds = ads.stream().filter(ad -> ad.getCategory().equalsIgnoreCase(category)).toList();
        updateAdsGrid(filteredAds);
    }

    private void filterAdsByTitle(String title) {
        List<Ad> filteredAds = ads.stream().filter(ad -> ad.getTitle().toLowerCase().contains(title.toLowerCase())).toList();
        updateAdsGrid(filteredAds);
    }

    private void updateAdsGrid(List<Ad> filteredAds) {
        adsGrid.getChildren().clear();
        for (int i = 0; i < filteredAds.size(); i++) {
            VBox adCard = createAdCard(filteredAds.get(i).getTitle(), filteredAds.get(i).getPrice(), filteredAds.get(i).getImagePath(), filteredAds.get(i).getDescription(), filteredAds.get(i).getPhone(), filteredAds.get(i));
            adsGrid.add(adCard, i % 3, i / 3);
        }
    }


    private void showUserAdsForEditing() {
        Stage userAdsStage = new Stage();
        userAdsStage.setTitle("Your Ads");

        VBox userAdsLayout = new VBox(15);
        userAdsLayout.setPadding(new Insets(20));
        userAdsLayout.setAlignment(Pos.CENTER);
        userAdsLayout.setStyle("""
                    -fx-background-color: #ffffff;
                    -fx-border-color: #dcdcdc;
                    -fx-border-radius: 10;
                    -fx-padding: 15;
                    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 2);
                """);

        Label titleLabel = new Label("Select an Ad to Edit");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#34495e"));
        titleLabel.setStyle("-fx-padding: 0 0 15 0;");

        List<Ad> userAds = ads.stream().filter(ad -> ad.getOwner().equals(loggedInUser.getEmail())).collect(Collectors.toList());

        if (userAds.isEmpty()) {
            Label noAdsLabel = new Label("You have no ads to edit.");
            noAdsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noAdsLabel.setTextFill(Color.web("#e74c3c"));
            noAdsLabel.setStyle("-fx-padding: 10 0 0 0;");
            noAdsLabel.setWrapText(true);

            userAdsLayout.getChildren().addAll(titleLabel, noAdsLabel);
        } else {
            ComboBox<Ad> adsComboBox = new ComboBox<>();
            adsComboBox.getItems().addAll(userAds);
            adsComboBox.setPromptText("Select an Ad");
            adsComboBox.setMaxWidth(300);
            adsComboBox.setStyle("""
                        -fx-background-color: #ecf0f1;
                        -fx-border-color: #bdc3c7;
                        -fx-border-radius: 5;
                        -fx-padding: 5;
                        -fx-font-size: 14px;
                    """);

            Button editButton = new Button("Edit Selected Ad");
            editButton.setStyle("""
                        -fx-background-color: #3498db;
                        -fx-text-fill: white;
                        -fx-font-weight: bold;
                        -fx-padding: 10 20;
                        -fx-border-radius: 5;
                        -fx-cursor: hand;
                    """);
            editButton.setOnMouseEntered(e -> editButton.setStyle("""
                        -fx-background-color: #2980b9;
                        -fx-text-fill: white;
                        -fx-font-weight: bold;
                        -fx-padding: 10 20;
                        -fx-border-radius: 5;
                        -fx-cursor: hand;
                    """));
            editButton.setOnMouseExited(e -> editButton.setStyle("""
                        -fx-background-color: #3498db;
                        -fx-text-fill: white;
                        -fx-font-weight: bold;
                        -fx-padding: 10 20;
                        -fx-border-radius: 5;
                        -fx-cursor: hand;
                    """));
            editButton.setOnAction(e -> {
                Ad selectedAd = adsComboBox.getValue();
                if (selectedAd != null) {
                    userAdsStage.close();
                    showEditAdForm(selectedAd);
                } else {
                    showAlert("Error", "Please select an ad to edit.", Alert.AlertType.ERROR);
                }
            });

            userAdsLayout.getChildren().addAll(titleLabel, adsComboBox, editButton);
        }
        Scene userAdsScene = new Scene(userAdsLayout, 400, 300);
        userAdsStage.setScene(userAdsScene);
        userAdsStage.show();
    }

    private void showUserAdsForDeletion() {
        Stage userAdsStage = new Stage();
        userAdsStage.setTitle("Your Ads");

        VBox userAdsLayout = new VBox(15);
        userAdsLayout.setPadding(new Insets(20));
        userAdsLayout.setAlignment(Pos.CENTER);
        userAdsLayout.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-padding: 15;");

        Label titleLabel = new Label("Select an Ad to Delete");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#34495e"));

        List<Ad> userAds = ads.stream()
                .filter(ad -> ad.getOwner().equals(loggedInUser.getEmail()))
                .collect(Collectors.toList());

        if (userAds.isEmpty()) {
            Label noAdsLabel = new Label("You have no ads to delete.");
            noAdsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noAdsLabel.setTextFill(Color.web("#e74c3c"));
            userAdsLayout.getChildren().addAll(titleLabel, noAdsLabel);
        } else {
            ComboBox<Ad> adsComboBox = new ComboBox<>();
            adsComboBox.getItems().addAll(userAds);
            adsComboBox.setPromptText("Select an Ad");
            adsComboBox.setMaxWidth(300);

            Button deleteButton = new Button("Delete Selected Ad");
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5;");
            deleteButton.setOnAction(e -> {
                Ad selectedAd = adsComboBox.getValue();
                if (selectedAd != null) {
                    ads.remove(selectedAd);
                    DatabaseConnection.deleteAd(selectedAd.getId());
                    updateAdsGrid(ads);
                    userAdsStage.close();
                    showAlert("Success", "Ad deleted successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Please select an ad to delete.", Alert.AlertType.ERROR);
                }
            });

            userAdsLayout.getChildren().addAll(titleLabel, adsComboBox, deleteButton);
        }

        Scene userAdsScene = new Scene(userAdsLayout, 400, 300);
        userAdsStage.setScene(userAdsScene);
        userAdsStage.show();
    }


    private void showEditAdForm(Ad ad) {
        Stage editStage = new Stage();
        editStage.setTitle("Edit Ad");

        VBox formLayout = new VBox(15);
        formLayout.setPadding(new Insets(20));
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-padding: 15;");

        TextField titleField = new TextField(ad.getTitle());
        TextField priceField = new TextField(String.valueOf(ad.getPrice()));
        TextField phoneField = new TextField(ad.getPhone());
        TextArea descriptionField = new TextArea(ad.getDescription());
        descriptionField.setWrapText(true);

        Label imagePathLabel = new Label(ad.getImagePath());
        Button uploadImageButton = new Button("Upload New Image");
        uploadImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(editStage);
            if (selectedFile != null) {
                imagePathLabel.setText(selectedFile.getAbsolutePath());
            }
        });

        VBox categorySpecificFields = new VBox(10);

        if (ad instanceof ElectronicsAd electronicsAd) {
            TextField modelField = new TextField(electronicsAd.getModel());
            TextField colorField = new TextField(electronicsAd.getColor());
            CheckBox tradeCheckBox = new CheckBox("Trade?");
            tradeCheckBox.setSelected(electronicsAd.isTrade());

            categorySpecificFields.getChildren().addAll(new Label("Model:"), modelField,
                    new Label("Color:"), colorField, tradeCheckBox);

            Button saveButton = new Button("Save");
            saveButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            saveButton.setOnAction(e -> {
                try {

                    electronicsAd.edit(titleField.getText(), descriptionField.getText(),
                            Double.parseDouble(priceField.getText()), phoneField.getText(),
                            imagePathLabel.getText(), modelField.getText(), colorField.getText(),
                            tradeCheckBox.isSelected());

                    DatabaseConnection.updateAd(electronicsAd);
                    updateAdsGrid(ads);
                    showAlert("Success", "Ad successfully updated!", Alert.AlertType.INFORMATION);
                    editStage.close();
                } catch (Exception ex) {
                    showAlert("Error", "Failed to update ad: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });

            formLayout.getChildren().addAll(titleField, priceField, phoneField, descriptionField, categorySpecificFields,
                    uploadImageButton, imagePathLabel, saveButton);
        } else if (ad instanceof VehicleAd) {
            VehicleAd vehicle = (VehicleAd) ad;

            TextField yearField = new TextField(String.valueOf(vehicle.getYear()));
            TextField mileageField = new TextField(String.valueOf(vehicle.getMileage()));
            CheckBox accidentCheckBox = new CheckBox("Is the vehicle accidented?");
            accidentCheckBox.setSelected(vehicle.isAccidented());

            categorySpecificFields.getChildren().addAll(yearField, mileageField, accidentCheckBox);

            Button saveButton = new Button("Save Changes");
            saveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5;");
            saveButton.setOnAction(e -> {
                try {
                    vehicle.edit(titleField.getText(), descriptionField.getText(), Double.parseDouble(priceField.getText()), phoneField.getText(), imagePathLabel.getText(), Integer.parseInt(yearField.getText()), Integer.parseInt(mileageField.getText()), accidentCheckBox.isSelected());

                    DatabaseConnection.updateAd(vehicle);
                    updateAdsGrid(ads);
                    showAlert("Success", "Ad successfully updated!", Alert.AlertType.INFORMATION);
                    editStage.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            formLayout.getChildren().addAll(titleField, priceField, phoneField, descriptionField, uploadImageButton, imagePathLabel, categorySpecificFields, saveButton);
        } else {
            Button saveButton = new Button("Save Changes");
            saveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5;");
            saveButton.setOnAction(e -> {
                ad.edit(titleField.getText(), descriptionField.getText(), Double.parseDouble(priceField.getText()), phoneField.getText(), imagePathLabel.getText());

                try {
                    DatabaseConnection.updateAd(ad);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                updateAdsGrid(ads);
                showAlert("Success", "Ad successfully updated!", Alert.AlertType.INFORMATION);
                editStage.close();
            });

            formLayout.getChildren().addAll(titleField, priceField, phoneField, descriptionField, uploadImageButton, imagePathLabel, saveButton);
        }

        Scene editScene = new Scene(formLayout, 400, 600);
        editStage.setScene(editScene);
        editStage.show();
    }


    private void showUserMessages() {
        Stage messagesStage = new Stage();
        messagesStage.setTitle("Your Messages");

        //با این که loggedInUser و username باهم برابرند ولی درست فیلتر نمیشه و فالس برمیگردونه
        /*for (Model.Message msg : messages) {
            System.out.println("Recipient: " + msg.getRecipient().getUsername() + " | Logged-in user: " + loggedInUser.getUsername());
            System.out.println("Equality check: " + msg.getRecipient().equals(loggedInUser));
        }*/


        VBox messagesLayout = new VBox(15);
        messagesLayout.setPadding(new Insets(20));
        messagesLayout.setAlignment(Pos.CENTER);
        messagesLayout.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-padding: 15;");

        Label titleLabel = new Label("Messages Sent to You");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#34495e"));

        //List<Message> userMessages = messages.stream().filter(message -> message.getRecipient().equals(loggedInUser)).collect(Collectors.toList());
        //کد جایگزین(equal درست کار نمیکرد)
        //با این که دو مقدار برابر بود رفرنس فرق میکرد و فقط پیام های جدید که رفرنس یکسان داشتند نشون داده میشند
        List<Message> userMessages = messages.stream().filter(message -> message.getRecipient().getUsername().equals(loggedInUser.getUsername())).collect(Collectors.toList());


        if (userMessages.isEmpty()) {
            Label noMessagesLabel = new Label("You have no messages.");
            noMessagesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noMessagesLabel.setTextFill(Color.web("#e74c3c"));
            messagesLayout.getChildren().addAll(titleLabel, noMessagesLabel);
        } else {
            ListView<Message> messagesListView = new ListView<>();
            messagesListView.getItems().addAll(userMessages);

            messagesListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Message message, boolean empty) {
                    super.updateItem(message, empty);
                    if (empty || message == null) {
                        setText(null);
                    } else {
                        setText("From: " + message.getSender().getUsername() + "\nMessage: " + message.getContent());
                    }
                }
            });

            Button replyButton = new Button("Reply");
            replyButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5;");
            replyButton.setOnAction(e -> {
                Message selectedMessage = messagesListView.getSelectionModel().getSelectedItem();
                if (selectedMessage != null) {
                    showReplyDialog(selectedMessage);
                } else {
                    showAlert("Error", "Please select a message to reply to.", Alert.AlertType.ERROR);
                }
            });

            messagesLayout.getChildren().addAll(titleLabel, messagesListView, replyButton);
        }

        Scene messagesScene = new Scene(messagesLayout, 400, 400);
        messagesStage.setScene(messagesScene);
        messagesStage.show();
    }

    private void showReplyDialog(Message originalMessage) {
        Stage replyStage = new Stage();
        replyStage.setTitle("Reply to Message");

        VBox replyLayout = new VBox(15);
        replyLayout.setPadding(new Insets(20));
        replyLayout.setAlignment(Pos.CENTER);
        replyLayout.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-padding: 15;");

        Label recipientLabel = new Label("To: " + originalMessage.getSender().getUsername());
        recipientLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextArea replyContent = new TextArea();
        replyContent.setPromptText("Write your reply here...");
        replyContent.setWrapText(true);

        Button sendReplyButton = new Button("Send Reply");
        sendReplyButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5;");
        sendReplyButton.setOnAction(e -> {
            String replyText = replyContent.getText();
            if (replyText.isEmpty()) {
                showAlert("Error", "Reply content cannot be empty.", Alert.AlertType.ERROR);
            } else {
                Message replyMessage = new Message(loggedInUser, originalMessage.getSender(), replyText);
                messages.add(replyMessage);
                try {
                    DatabaseConnection.saveMessage(replyMessage);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                showAlert("Success", "Your reply has been sent.", Alert.AlertType.INFORMATION);
                replyStage.close();
            }
        });

        replyLayout.getChildren().addAll(recipientLabel, replyContent, sendReplyButton);

        Scene replyScene = new Scene(replyLayout, 400, 300);
        replyStage.setScene(replyScene);
        replyStage.show();
    }


    private VBox createAdCard(String title, double price, String imageUrl, String description, String contact, Ad ad) {
        VBox adCard = new VBox(10);
        adCard.setPadding(new Insets(15));
        adCard.setAlignment(Pos.CENTER);
        adCard.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        ImageView adImage;
        try {
            File file = new File(imageUrl);
            adImage = new ImageView(new Image(file.toURI().toString()));
        } catch (Exception e) {
            adImage = new ImageView(new Image(getClass().getResource("/resources/default.png").toExternalForm()));
            System.err.println("Can't open img" + e.getMessage());
        }


        adImage.setFitWidth(200);
        adImage.setFitHeight(150);
        adImage.setStyle("-fx-border-radius: 10; -fx-background-radius: 10;");

        Label adTitle = new Label(title);
        adTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        adTitle.setTextFill(Color.web("#2c3e50"));

        Label adPrice = new Label("Price: $" + price);
        adPrice.setFont(Font.font("Arial", 14));
        adPrice.setTextFill(Color.web("#16a085"));

        Button viewDetails = new Button("View Details");
        viewDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15;");

        viewDetails.setOnAction(e -> showAdDetails(title, price, imageUrl, description, contact, ad));

        adCard.getChildren().addAll(adImage, adTitle, adPrice, viewDetails);
        return adCard;
    }


    private void showLoginForm() {
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");

        StackPane root = new StackPane();

        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(getClass().getResource("/resources/login.jpg").toExternalForm(), 800, 600, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT
        );

        root.setBackground(new Background(backgroundImage));

        VBox loginCard = new VBox(20);
        loginCard.setPadding(new Insets(20));
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        Label titleLabel = new Label("Welcome Back sir");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#34495e"));

        HBox emailFieldContainer = new HBox(10);
        emailFieldContainer.setAlignment(Pos.CENTER);
        ImageView emailIcon = new ImageView(new Image(getClass().getResource("/resources/email.png").toExternalForm()));
        emailIcon.setFitWidth(20);
        emailIcon.setFitHeight(20);
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle("-fx-padding: 10; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-background-radius: 5;");
        emailFieldContainer.getChildren().addAll(emailIcon, emailField);

        HBox passwordFieldContainer = new HBox(10);
        passwordFieldContainer.setAlignment(Pos.CENTER);
        ImageView passwordIcon = new ImageView(new Image(getClass().getResource("/resources/password.png").toExternalForm()));
        passwordIcon.setFitWidth(20);
        passwordIcon.setFitHeight(20);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-padding: 10; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-background-radius: 5;");
        passwordFieldContainer.getChildren().addAll(passwordIcon, passwordField);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 5;");
        loginButton.setOnAction(e -> {
            if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("All fields are required!");
                alert.showAndWait();
            } else {
                boolean isLoggedIn = false;
                for (User user : users) {
                    if (user.getEmail().equals(emailField.getText()) && user.checkPassword(passwordField.getText())) {
                        loggedInUser = user;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Login Successful");
                        alert.setHeaderText(null);
                        alert.setContentText("Welcome back" + " " + user.getUsername());
                        alert.showAndWait();
                        isLoggedIn = true;
                        loginStage.close();
                    }
                }
                if (!isLoggedIn) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Email or Password");
                    alert.setHeaderText(null);
                    alert.setContentText("Try again!");
                    alert.showAndWait();
                }
            }
        });
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 5;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 5;"));

        loginCard.getChildren().addAll(titleLabel, emailFieldContainer, passwordFieldContainer, loginButton);

        root.getChildren().add(loginCard);
        StackPane.setAlignment(loginCard, Pos.CENTER);

        Scene loginScene = new Scene(root, 500, 300);
        loginStage.setScene(loginScene);
        loginStage.show();
    }

    private void showRegisterForm() {
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up");

        StackPane root = new StackPane();

        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(getClass().getResource("/resources/signup.jpg").toExternalForm(), 800, 600, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT
        );
        root.setBackground(new Background(backgroundImage));


        VBox signUpCard = new VBox(20);
        signUpCard.setPadding(new Insets(20));
        signUpCard.setAlignment(Pos.CENTER);
        signUpCard.setStyle(" -fx-border-color: #dcdcdc; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        Label titleLabel = new Label("Sign Up");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#34495e"));

        HBox usernameFieldContainer = new HBox(10);
        usernameFieldContainer.setAlignment(Pos.CENTER);
        ImageView usernameIcon = new ImageView(new Image(getClass().getResource("/resources/user.png").toExternalForm()));

        usernameIcon.setFitWidth(20);
        usernameIcon.setFitHeight(20);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("-fx-padding: 10; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-background-radius: 5;");
        usernameFieldContainer.getChildren().addAll(usernameIcon, usernameField);

        HBox emailFieldContainer = new HBox(10);
        emailFieldContainer.setAlignment(Pos.CENTER);
        ImageView emailIcon = new ImageView(new Image(getClass().getResource("/resources/email.png").toExternalForm()));
        emailIcon.setFitWidth(20);
        emailIcon.setFitHeight(20);
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle("-fx-padding: 10; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-background-radius: 5;");
        emailFieldContainer.getChildren().addAll(emailIcon, emailField);

        HBox passwordFieldContainer = new HBox(10);
        passwordFieldContainer.setAlignment(Pos.CENTER);
        ImageView passwordIcon = new ImageView(new Image(getClass().getResource("/resources/password.png").toExternalForm()));
        passwordIcon.setFitWidth(20);
        passwordIcon.setFitHeight(20);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-padding: 10; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-background-radius: 5;");
        passwordFieldContainer.getChildren().addAll(passwordIcon, passwordField);


        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 5;");
        signUpButton.setOnAction(e -> {

            if (usernameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("All fields are required!");
                alert.showAndWait();
            }
            else if(!isValidEmail(emailField.getText())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid email");
                alert.setHeaderText(null);
                alert.setContentText("Invalid Email!");
                alert.showAndWait();

            }
            else  {
                User user = new User(usernameField.getText(), emailField.getText(), passwordField.getText());
                users.add(user);
                loggedInUser = user;
                DatabaseConnection.saveUsers(user);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful");
                alert.setHeaderText(null);
                alert.setContentText("Welcome, " + usernameField.getText() + "!");
                alert.showAndWait();
                signUpStage.close();
            }
        });
        signUpButton.setOnMouseEntered(e -> signUpButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 5;"));
        signUpButton.setOnMouseExited(e -> signUpButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 5;"));

        signUpCard.getChildren().addAll(titleLabel, usernameFieldContainer, emailFieldContainer, passwordFieldContainer, signUpButton);

        root.getChildren().add(signUpCard);
        StackPane.setAlignment(signUpCard, Pos.CENTER);

        Scene signUpScene = new Scene(root, 500, 400);
        signUpStage.setScene(signUpScene);
        signUpStage.show();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email != null && email.matches(emailRegex);
    }

    private void showAdDetails(String title, double price, String imageUrl, String description, String contact, Ad ad) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Ad Details");

        BorderPane detailsLayout = new BorderPane();
        detailsLayout.setPadding(new Insets(20));
        detailsLayout.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #d1d9e6; -fx-border-radius: 20; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 12);");

        VBox contentLayout = new VBox(20);
        contentLayout.setPadding(new Insets(20));
        contentLayout.setAlignment(Pos.TOP_CENTER);
        Label sellerRatingLabel = new Label("Seller Rating:");
        sellerRatingLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        sellerRatingLabel.setTextFill(Color.web("#34495e"));

        User owner = null;
        for (User user : users) {
            if (user.getEmail().equals(ad.getOwner())) {
                owner = user;
            }
        }
        if (owner != null) {
            try (Connection con = DatabaseConnection.getConnection()) {
                DatabaseConnection.getUserRatingDetails(owner);
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to load seller rating.", Alert.AlertType.ERROR);
            }
        }

        double sellerRating = owner != null ? owner.getAverageRating() : 0.0;

        Label ratingValue = new Label(String.format("%.1f", sellerRating));
        ratingValue.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        ratingValue.setTextFill(Color.web("#f39c12"));

        Label stars = new Label(getStarsFromRating(sellerRating));
        stars.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        stars.setTextFill(Color.web("#f1c40f"));

        HBox sellerRatingBox = new HBox(10, sellerRatingLabel, stars, ratingValue);
        sellerRatingBox.setAlignment(Pos.CENTER);
        sellerRatingBox.setPadding(new Insets(10));


        ImageView adImage;
        try {
            File file = new File(imageUrl);
            adImage = new ImageView(new Image(file.toURI().toString()));
        } catch (Exception e) {
            adImage = new ImageView(new Image(getClass().getResource("/resources/default.jpg").toExternalForm()));
            System.err.println("Can't open img" + e.getMessage());
        }
        adImage.setFitWidth(500);
        adImage.setFitHeight(300);

        Circle clip = new Circle(adImage.getFitWidth() / 2, adImage.getFitHeight() / 2, 285);
        adImage.setClip(clip);

        adImage.setStyle("-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); -fx-clip-radius: 15;");


        adImage.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), adImage);
        fadeIn.setToValue(1);
        fadeIn.play();

        Label adTitle = new Label(title);
        adTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        adTitle.setTextFill(Color.web("#2c3e50"));

        Label adPrice = new Label("Price: $" + price);
        adPrice.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        adPrice.setTextFill(Color.web("#27ae60"));

        Label adDescription = new Label(description);
        adDescription.setFont(Font.font("Verdana", 18));
        adDescription.setWrapText(true);
        adDescription.setPadding(new Insets(15));
        adDescription.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e6ed; -fx-border-radius: 10; -fx-padding: 15; -fx-background-radius: 10;");

        Label adContact = new Label("Contact: " + contact);
        adContact.setFont(Font.font("Arial", 18));
        adContact.setTextFill(Color.web("#2980b9"));

        HBox actionButtons = new HBox(25);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setPadding(new Insets(20));

        Button buyButton = new Button("Buy Now");
        buyButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 12 45; -fx-border-radius: 10;");
        buyButton.setOnAction(e -> {
            if (loggedInUser == null) {
                showAlert("error", "You are not logged in!", Alert.AlertType.ERROR);
            } else if (!loggedInUser.getEmail().equals(ad.getOwner())) {
                ads.remove(ad);
                DatabaseConnection.deleteAd(ad.getId());
                updateAdsGrid(ads);
                showAlert("congratulation", "Ad purchased successfully", Alert.AlertType.INFORMATION);

                showRatingDialog(ad);
                detailsStage.close();
            } else {
                showAlert("error", "You can't rate your self!", Alert.AlertType.ERROR);
            }
        });
        buyButton.setOnMouseEntered(e -> buyButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 12 45; -fx-border-radius: 10;"));
        buyButton.setOnMouseExited(e -> buyButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 12 45; -fx-border-radius: 10;"));

        Button messageButton = new Button("Contact Seller");
        messageButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 12 45; -fx-border-radius: 10;");
        messageButton.setOnAction(e -> {
            if (loggedInUser == null) {
                showAlert("error", "You are not logged in!", Alert.AlertType.ERROR);
            } else {
                VBox messageBox = new VBox(20);
                messageBox.setPadding(new Insets(20));
                messageBox.setAlignment(Pos.CENTER);
                messageBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e6ed; -fx-border-radius: 10; -fx-padding: 20;");

                Label messageLabel = new Label("Send a message to the seller:");
                messageLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
                messageLabel.setTextFill(Color.web("#34495e"));

                TextArea messageArea = new TextArea();
                messageArea.setPromptText("Write your message here...");
                messageArea.setWrapText(true);
                messageArea.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-padding: 10; -fx-font-size: 16px;");

                Button sendButton = new Button("Send Message");
                sendButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 12 30; -fx-border-radius: 10;");
                sendButton.setOnAction(event -> {
                    User recipient_user = null;
                    for (User user : users) {
                        if (user.getEmail().equals(ad.getOwner())) {
                            recipient_user = user;
                        }
                    }
                    String recipient_email = ad.getOwner();
                    Message message = new Message(loggedInUser, recipient_user, messageArea.getText());
                    messages.add(message);
                    try {
                        DatabaseConnection.saveMessage(message);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message Sent");
                    alert.setHeaderText(null);
                    alert.setContentText("Your message has been sent: " + messageArea.getText());
                    alert.showAndWait();

                });
                messageBox.getChildren().addAll(messageLabel, messageArea, sendButton);
                Scene messageScene = new Scene(messageBox, 500, 400);
                Stage messageStage = new Stage();
                messageStage.setTitle("Contact Seller");
                messageStage.setScene(messageScene);
                messageStage.show();
            }
        });
        messageButton.setOnMouseEntered(e -> messageButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 12 45; -fx-border-radius: 10;"));
        messageButton.setOnMouseExited(e -> messageButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 12 45; -fx-border-radius: 10;"));


        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 12 45; -fx-border-radius: 10;");
        closeButton.setOnAction(e -> detailsStage.close());

        actionButtons.getChildren().addAll(buyButton, messageButton, closeButton);

        contentLayout.getChildren().addAll(adImage, adTitle, adPrice, adDescription, adContact, sellerRatingBox);
        detailsLayout.setCenter(contentLayout);
        detailsLayout.setBottom(actionButtons);

        Scene detailsScene = new Scene(detailsLayout, 650, 750);
        detailsStage.setScene(detailsScene);
        detailsStage.show();
    }

    private String getStarsFromRating(double rating) {
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;
        StringBuilder stars = new StringBuilder();

        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        if (hasHalfStar) {
            stars.append("☆");
        }
        int remainingStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        for (int i = 0; i < remainingStars; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }


    private void showRatingDialog(Ad ad) {
        Stage ratingStage = new Stage();
        ratingStage.setTitle("Rate the Seller");

        VBox ratingLayout = new VBox(20);
        ratingLayout.setPadding(new Insets(20));
        ratingLayout.setAlignment(Pos.CENTER);
        ratingLayout.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 5);");

        Label ratingLabel = new Label("Rate the Seller");
        ratingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        ratingLabel.setTextFill(Color.web("#34495e"));

        Label instructionLabel = new Label("Please choose a rating between 1 and 5");
        instructionLabel.setFont(Font.font("Arial", 14));
        instructionLabel.setTextFill(Color.web("#7f8c8d"));

        Slider ratingSlider = new Slider(1, 5, 3);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setMinorTickCount(0);
        ratingSlider.setBlockIncrement(1);
        ratingSlider.setSnapToTicks(true);
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setShowTickMarks(true);
        ratingSlider.setStyle("-fx-pref-width: 250;");

        Button submitRatingButton = new Button("Submit Rating");
        submitRatingButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20; -fx-border-radius: 8;");
        submitRatingButton.setOnAction(e -> {
            int rating = (int) ratingSlider.getValue();

            String sellerEmail = ad.getOwner();
            String buyerEmail = loggedInUser.getEmail();
            DatabaseConnection.addRating(sellerEmail, buyerEmail, rating);
            try {
                DatabaseConnection.updateUserRatings(sellerEmail);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            User updatedSeller = null;
            try {
                updatedSeller = DatabaseConnection.getUserByEmail(sellerEmail);
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            if (updatedSeller != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rating Submitted");
                alert.setHeaderText(null);
                alert.setContentText("Thank you! You rated the seller: " + rating + " stars.");
                alert.showAndWait();
                ratingStage.close();
            }
        });

        HBox sliderContainer = new HBox(ratingSlider);
        sliderContainer.setAlignment(Pos.CENTER);
        sliderContainer.setPadding(new Insets(10));

        ratingLayout.getChildren().addAll(ratingLabel, instructionLabel, sliderContainer, submitRatingButton);

        Scene ratingScene = new Scene(ratingLayout, 400, 250);
        ratingStage.setScene(ratingScene);
        ratingStage.show();
    }

    private void showCategorySelection(Stage primaryStage) {
        Stage categoryStage = new Stage();
        categoryStage.setTitle("Select Category");

        VBox categoryLayout = new VBox(15);
        categoryLayout.setPadding(new Insets(20));
        categoryLayout.setAlignment(Pos.CENTER);
        categoryLayout.setStyle("""
                    -fx-background-color: #ffffff;
                    -fx-border-color: #dcdcdc;
                    -fx-border-radius: 10;
                    -fx-padding: 15;
                    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 10, 0, 0, 0);
                """);

        Label titleLabel = new Label("Select a Category");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#34495e"));
        titleLabel.setStyle("-fx-padding: 0 0 10 0;");

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Electronics", "Vehicles", "General");
        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.setMaxWidth(300);
        categoryComboBox.setStyle("""
                    -fx-background-color: #ecf0f1;
                    -fx-border-color: #bdc3c7;
                    -fx-border-radius: 5;
                    -fx-padding: 5;
                    -fx-font-size: 14px;
                """);

        Button proceedButton = new Button("Proceed");
        proceedButton.setStyle("""
                    -fx-background-color: #27ae60;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-padding: 10 20;
                    -fx-border-radius: 5;
                    -fx-cursor: hand;
                """);
        proceedButton.setOnMouseEntered(e -> proceedButton.setStyle("""
                    -fx-background-color: #2ecc71;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-padding: 10 20;
                    -fx-border-radius: 5;
                    -fx-cursor: hand;
                """));
        proceedButton.setOnMouseExited(e -> proceedButton.setStyle("""
                    -fx-background-color: #27ae60;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-padding: 10 20;
                    -fx-border-radius: 5;
                    -fx-cursor: hand;
                """));
        proceedButton.setOnAction(e -> {
            String selectedCategory = categoryComboBox.getValue();
            if (selectedCategory == null) {
                showAlert("Error", "Please select a category.", Alert.AlertType.ERROR);
            } else {
                categoryStage.close();
                showAddAdForm(primaryStage, selectedCategory);
            }
        });

        categoryLayout.getChildren().addAll(titleLabel, categoryComboBox, proceedButton);

        Scene categoryScene = new Scene(categoryLayout, 400, 200);
        categoryStage.setScene(categoryScene);
        categoryStage.show();
    }

    private void showAddAdForm(Stage primaryStage, String category) {
        Stage addAdStage = new Stage();
        addAdStage.setTitle("Add New Ad - " + category);

        VBox formLayout = new VBox(15);
        formLayout.setPadding(new Insets(20));
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-padding: 15;");

        Label titleLabel = new Label("Add New Advertisement");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#34495e"));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        titleField.setMaxWidth(300);

        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.setMaxWidth(300);

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                priceField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setMaxWidth(300);

        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setWrapText(true);
        descriptionField.setMaxWidth(300);
        descriptionField.setPrefHeight(100);

        VBox categorySpecificFields = new VBox(10);
        categorySpecificFields.setAlignment(Pos.CENTER);

        if (category.equals("Vehicles")) {
            TextField yearField = new TextField();
            yearField.setPromptText("year");
            yearField.setMaxWidth(300);

            TextField mileageField = new TextField();
            mileageField.setPromptText("Mileage (km)");
            mileageField.setMaxWidth(300);

            yearField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    yearField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
            mileageField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    mileageField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            CheckBox accidentCheckBox = new CheckBox("Is the vehicle accidented?");

            categorySpecificFields.getChildren().addAll(yearField, mileageField, accidentCheckBox);
        } else if (category.equals("Electronics")) {
            TextField modelField = new TextField();
            modelField.setPromptText("Your device model");
            modelField.setMaxWidth(300);

            TextField colorField = new TextField();
            colorField.setPromptText("Your device color");
            colorField.setMaxWidth(300);

            CheckBox tradeCheckBox = new CheckBox("Do you want to trade?");

            categorySpecificFields.getChildren().addAll(modelField, colorField, tradeCheckBox);
        }
        Button uploadImageButton = new Button("Upload Image");
        Label imagePathLabel = new Label("No file selected");
        uploadImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                imagePathLabel.setText(selectedFile.getAbsolutePath());
            }
        });

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5;");
        saveButton.setOnAction(e -> {
            String title = titleField.getText();

            String priceText = priceField.getText();
            String phone = phoneField.getText();
            String description = descriptionField.getText();
            String imagePath = imagePathLabel.getText();

            if (title.isEmpty() || priceText.isEmpty() || phone.isEmpty() || description.isEmpty() || imagePath.equals("No file selected")) {
                showAlert("Error", "All fields must be filled out.", Alert.AlertType.ERROR);
                return;
            }

            double price;

            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                showAlert("Error", "Price must be a valid number.", Alert.AlertType.ERROR);
                return;
            }

            if (category.equals("General")) {
                Ad ad = new Ad(title, description, price, "General", loggedInUser.getEmail(), phone, imagePath);
                ads.add(ad);
                DatabaseConnection.saveAds(ad);
            } else if (category.equals("Vehicles")) {
                String yearText = ((TextField) categorySpecificFields.getChildren().get(0)).getText();
                String mileageText = ((TextField) categorySpecificFields.getChildren().get(1)).getText();
                boolean accidentStatus = ((CheckBox) categorySpecificFields.getChildren().get(2)).isSelected();

                int year;
                int mileage;

                try {
                    year = Integer.parseInt(yearText);


                    mileage = Integer.parseInt(mileageText);
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Year and mileage must be valid numbers.", Alert.AlertType.ERROR);
                    return;
                }
                if (yearText.isEmpty() || mileageText.isEmpty()) {
                    showAlert("Error", "All fields must be filled out.", Alert.AlertType.ERROR);
                    return;
                }
                Ad ad = new VehicleAd(title, description, price, "Vehicle", loggedInUser.getEmail(), phone, imagePath, year, mileage, accidentStatus);
                ads.add(ad);
                DatabaseConnection.saveAds(ad);

            } else if (category.equals("Electronics")) {
                String model = ((TextField) categorySpecificFields.getChildren().get(0)).getText();
                String color = ((TextField) categorySpecificFields.getChildren().get(1)).getText();
                boolean tradeStatus = ((CheckBox) categorySpecificFields.getChildren().get(2)).isSelected();
                if (model.isEmpty() || color.isEmpty()) {
                    showAlert("Error", "All fields must be filled out.", Alert.AlertType.ERROR);
                    return;
                }
                Ad ad = new ElectronicsAd(title, description, price, "Electronics", loggedInUser.getEmail(), phone, imagePath, model, color, tradeStatus);
                DatabaseConnection.saveAds(ad);
                ads.add(ad);
            }
            updateAdsGrid(ads);
            showAlert("Success", "Ad successfully added!", Alert.AlertType.INFORMATION);
            addAdStage.close();
        });

        formLayout.getChildren().addAll(titleLabel, titleField, priceField, phoneField, descriptionField, categorySpecificFields, uploadImageButton, imagePathLabel, saveButton);

        Scene addAdScene = new Scene(formLayout, 400, 600);
        addAdStage.setScene(addAdScene);
        addAdStage.show();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Button createCategoryButton(String category) {
        Button button = new Button(category);
        button.setPrefWidth(150);
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10;");
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}