package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.openjfx.model.Booking;
import org.openjfx.model.Offer;
import org.openjfx.services.BookingService;
import org.openjfx.services.OfferService;

import java.io.IOException;
import java.net.URL;
import java.text.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import static org.dizitart.no2.objects.filters.ObjectFilters.and;
import static org.dizitart.no2.objects.filters.ObjectFilters.eq;

public class OfferDetailsController implements Initializable {
    private static final ObjectRepository<Offer> OFFER_REPOSITORY = OfferService.getOfferRepository();
    private static final ObjectRepository<Booking> BOOKING_REPOSITORY = BookingService.getBookingRepository();
    private static String selectedAgency;
    private static String selectedOffer;
    private static String clientUsername;
    private Offer offerSelected;
    private Booking existingBooking;
    private Stage anotherStage;

    @FXML
    private Button agencyListButton;
    @FXML
    private Button bookListButton;
    @FXML
    private Button makeBookingButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label nameLabel;
    @FXML
    private Label destinationLabel;
    @FXML
    private Label hotelLabel;
    @FXML
    private Label mealsLabel;
    @FXML
    private Label nightsLabel;
    @FXML
    private Label clientsLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private TextField numberOfPersons;
    @FXML
    private DatePicker checkInDate;
    @FXML
    private DatePicker checkOutDate;
    @FXML
    private Text messageText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedAgency = OffersPageController.getSelectedAgency();
        selectedOffer = OffersPageController.getSelectedOffer();
        clientUsername = OffersPageController.getClientUsername();
        showOfferDetails(selectedOffer);
        existingBooking = BOOKING_REPOSITORY.find(and(eq("nameOfAgency", selectedAgency),eq("nameOfOffer",selectedOffer),eq("clientUsername",clientUsername))).firstOrDefault();
        if(existingBooking!=null){
            numberOfPersons.setText(existingBooking.getNumberOfPersons());
            totalPriceLabel.setText(existingBooking.getTotalPrice());
            String inDate = existingBooking.getCheckInDate();
            String localInDate= inDate.substring(6,10)+"-"+inDate.substring(3,5)+"-"+inDate.substring(0,2);
            checkInDate.setValue(LocalDate.parse(localInDate));
            checkOutDate.setValue(checkInDate.getValue().plusDays(Integer.parseInt(offerSelected.getNights())));
            messageText.setText(existingBooking.getMessage());
            if(existingBooking.getMessage().contains("deadline"))
            {
                numberOfPersons.setDisable(true);
                checkInDate.setDisable(true);
            }
        }
        numberOfPersons.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                if(!numberOfPersons.getText().equals("")){
                    double price = Integer.parseInt(numberOfPersons.getText())*Double.parseDouble(priceLabel.getText());
                    int intPrice=(int) price;
                    if(price==intPrice){
                        totalPriceLabel.setText(String.valueOf(intPrice));
                    }
                    else{
                        totalPriceLabel.setText(String.valueOf(price));
                    }
                }
            }
        });
        checkInDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
        checkInDate.valueProperty().addListener((ov, oldValue, newValue) -> {
            checkOutDate.setValue(newValue.plusDays(Integer.parseInt(nightsLabel.getText())));
        });
    }
    @FXML
    public void handleAgencyList() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("travelAgenciesList.fxml"));
            Parent root = loader.load();
            AgenciesListController controller = loader.getController();
            controller.setAnotherStage(anotherStage);
            Stage stage = (Stage) (agencyListButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
    @FXML
    public void handleClose(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("offersPage.fxml"));
            Parent root = loader.load();
            OffersPageController controller = loader.getController();
            controller.setAnotherStage(anotherStage);
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    @FXML
    public void handleMakeBooking(){
        try {
            if(existingBooking!=null){
                BOOKING_REPOSITORY.remove(and(eq("nameOfAgency", selectedAgency),eq("nameOfOffer",selectedOffer),eq("clientUsername",clientUsername)));
            }
            String id = NitriteId.newId().toString();
            if(checkInDate.getValue()!=null){
                BookingService.addBooking(id,clientUsername,selectedAgency,selectedOffer,numberOfPersons.getText(),totalPriceLabel.getText(),
                        checkInDate.getValue().format(DateTimeFormatter.ofPattern("dd-MM-YYYY")),checkOutDate.getValue().format(DateTimeFormatter.ofPattern("dd-MM-YYYY")),
                        "Your booking hasn't been approved/rejected yet.","0");
            }
            else{
                offerSelected = OFFER_REPOSITORY.find(and(eq("nameOfAgency", selectedAgency),eq("nameOfOffer",selectedOffer))).firstOrDefault();
                int days = 15 + Integer.parseInt(offerSelected.getNights());
                String outDate = days + "-06-2021";
                BookingService.addBooking(id,clientUsername,selectedAgency,selectedOffer,numberOfPersons.getText(),totalPriceLabel.getText(),
                        "15-06-2021",outDate,"Your booking hasn't been approved/rejected yet.","0");
            }
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("offersPage.fxml"));
            Parent root = loader.load();
            OffersPageController controller = loader.getController();
            controller.setAnotherStage(anotherStage);
            Stage stage = (Stage) makeBookingButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    @FXML
    public void handleHistory() throws IOException {
        Stage stage = (Stage) bookListButton.getScene().getWindow();
        stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("historyBooking.fxml"));
        Parent root = loader.load();
        HistoryBookingController controller = loader.getController();
        anotherStage = (Stage) bookListButton.getScene().getWindow();
        controller.setStage(anotherStage);
        stage.setScene(new Scene(root));
        stage.show();
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
    public void showOfferDetails(String offerName){
        offerSelected = OFFER_REPOSITORY.find(and(eq("nameOfAgency", selectedAgency),eq("nameOfOffer",selectedOffer))).firstOrDefault();
        nameLabel.setText(offerSelected.getNameOfOffer());
        destinationLabel.setText(offerSelected.getDestination());
        hotelLabel.setText(offerSelected.getHotelName());
        mealsLabel.setText(offerSelected.getMeals());
        nightsLabel.setText(offerSelected.getNights());
        clientsLabel.setText(offerSelected.getNoOfClients());
        priceLabel.setText(offerSelected.getPrice());
    }

    public void setAnotherStage(Stage anotherStage) {
        this.anotherStage = anotherStage;
    }

    public static void setSelectedAgency(String selectedAgency) {
        OfferDetailsController.selectedAgency = selectedAgency;
    }

    public static void setSelectedOffer(String selectedOffer) {
        OfferDetailsController.selectedOffer = selectedOffer;
    }

    public static String getSelectedAgency() {
        return selectedAgency;
    }

    public static String getSelectedOffer() {
        return selectedOffer;
    }

}

