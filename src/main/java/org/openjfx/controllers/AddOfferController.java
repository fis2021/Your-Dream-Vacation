package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.ObjectRepository;
import org.openjfx.model.User;
import org.openjfx.services.OfferService;
import org.openjfx.services.UserService;

import java.io.IOException;

public class AddOfferController {
    private static String username,id;
    private static String nameOfAgency;
    private static Stage stage = new Stage();

    @FXML
    private Button saveButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button bookListButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField nameOfOffer;
    @FXML
    private TextField destination;
    @FXML
    private TextField hotelName;
    @FXML
    private TextField meals;
    @FXML
    private TextField nights;
    @FXML
    private TextField noOfClients;
    @FXML
    private TextField price;

    @FXML
    public void handleSaveOffer(){
        try {
            String id = NitriteId.newId().toString();
            OfferService.addOffer(id,nameOfAgency,nameOfOffer.getText(),destination.getText(),hotelName.getText(),meals.getText(),nights.getText(),noOfClients.getText(),price.getText());
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("addOfferPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) (saveButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    @FXML
    public void handleClose(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("travelAgentPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
    @FXML
    public void handleDelete(){
        try {
            Parent root= FXMLLoader.load(getClass().getClassLoader().getResource("deleteOfferPage.fxml"));
            Stage stage = (Stage) (deleteButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
    @FXML
    public void handleLogout() {
        try {
            Parent root= FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml"));
            Stage stage = (Stage) (logoutButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    @FXML
    public void handleEdit() throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("editOfferPage.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        Stage primaryStage = (Stage) editButton.getScene().getWindow();
        primaryStage.close();
    }

    @FXML
    public void handleBookingList() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("bookingsPage.fxml"));
            Parent root = loader.load();
            Stage bookList = (Stage) bookListButton.getScene().getWindow();
            bookList.close();
            Stage stage1 = new Stage();
            stage1.setScene(new Scene(root));
            stage1.show();

    }

    public static void setUsername(String username) {
        AddOfferController.username = username;
    }

    public static void setNameOfAgency(String nameOfAgency) {
        AddOfferController.nameOfAgency = nameOfAgency;
    }

    public static Stage getStage() {
        return stage;
    }

}
