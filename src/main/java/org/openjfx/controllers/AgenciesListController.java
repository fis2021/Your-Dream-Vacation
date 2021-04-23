package org.openjfx.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.openjfx.model.User;
import org.openjfx.services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.dizitart.no2.FindOptions.sort;
import static org.dizitart.no2.objects.filters.ObjectFilters.eq;

public class AgenciesListController implements Initializable {
    private static final ObjectRepository<User> REPOSITORY = UserService.getUserRepository();
    private static Cursor<User> agents = REPOSITORY.find(eq("role","Travel Agent"),sort("nameOfAgency", SortOrder.Ascending));
    private static ArrayList<String> listOfAgencies = new ArrayList<String>();
    private ObservableList<String> agencies = FXCollections.observableArrayList(listOfAgencies);
    private static String selectedAgency;
    private static Stage stage = new Stage();

    @FXML
    private Button bookListButton;
    @FXML
    private Button offersButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ListView agenciesList;
    @FXML
    private TextField agencyName;

    public static void getAllAgencies(){
        for(User agent : agents){
            String agencyName= agent.getNameOfAgency();
            if(!listOfAgencies.contains(agencyName)){
                listOfAgencies.add(agencyName);
            }
        }
    }

    public static Stage getStage() {
        return stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int ROW_HEIGHT=24;
        agenciesList.maxHeightProperty().bind(Bindings.size(agencies).multiply(ROW_HEIGHT));
        agenciesList.setItems(agencies);
        TextFields.bindAutoCompletion(agencyName,agencies);
    }

    @FXML
    public void handleOffers(){
        if(agencyName.getText().equals("")==false)
            selectedAgency=agencyName.getText();
        else
            selectedAgency=(String)(agenciesList.getSelectionModel().getSelectedItem());
        try{
            OffersPageController.setSelectedAgency(selectedAgency);
            Parent root= FXMLLoader.load(getClass().getClassLoader().getResource("offersPage.fxml"));
            stage = (Stage) (offersButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    @FXML
    public void handleLogout() {
        try{
            Parent root= FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml"));
            Stage stage = (Stage) (logoutButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}