package org.openjfx.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.openjfx.model.Offer;
import org.openjfx.services.OfferService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.dizitart.no2.FindOptions.sort;
import static org.dizitart.no2.objects.filters.ObjectFilters.eq;

public class OffersPageController implements Initializable {
    private static String selectedAgency;
    private static String selectedOffer;
    private static String clientUsername;
    private static final ObjectRepository<Offer> OFFER_REPOSITORY = OfferService.getOfferRepository();
    private static Stage stage = new Stage();

    @FXML
    private Button agencyListButton;
    @FXML
    private Button bookListButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ListView offersList;
    @FXML
    private Text agencyName;
    @FXML
    private Text noOfferExists;

    public static void setSelectedAgency(String selectedAgency) {
        OffersPageController.selectedAgency = selectedAgency;
    }

    public static String getSelectedAgency() {
        return selectedAgency;
    }

    public static String getSelectedOffer() {
        return selectedOffer;
    }

    public static String getClientUsername() {
        return clientUsername;
    }

    public static void setClientUsername(String clientUsername) {
        OffersPageController.clientUsername = clientUsername;
    }

    public static Stage getStage() {
        return stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Cursor<Offer> offers = OFFER_REPOSITORY.find(eq("nameOfAgency", selectedAgency), sort("nameOfOffer", SortOrder.Ascending));
        ArrayList<String> listOfOffers = new ArrayList<String>();
        for (Offer offer : offers) {
            listOfOffers.add(offer.getNameOfOffer());
        }
        ObservableList<String> observableOffers = FXCollections.observableArrayList(listOfOffers);
        agencyName.setText(selectedAgency);
        int ROW_HEIGHT = 24;
        if (listOfOffers.isEmpty()) {
            offersList.setOpacity(0);
            noOfferExists.setText("No offer has been added yet.");
        } else {
            offersList.maxHeightProperty().bind(Bindings.size(observableOffers).multiply(ROW_HEIGHT));
            offersList.setItems(observableOffers);
            offersList.setOnMouseClicked(click -> {
                if (click.getClickCount() == 2) {
                    selectedOffer = (String) offersList.getSelectionModel()
                            .getSelectedItem();
                    AgenciesListController.getStage().close();
                    try {
                        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("offerDetailsPage.fxml"));
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        System.out.println("Error");
                    }
                }
            });
        }
    }

    @FXML
    public void handleAgencyList() {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("travelAgenciesList.fxml"));
            Stage stage = (Stage) (agencyListButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    @FXML
    public void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml"));
            Stage stage = (Stage) (logoutButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}