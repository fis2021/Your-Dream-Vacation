package org.openjfx.controllers;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.time.DateUtils;
import org.controlsfx.control.Rating;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.openjfx.model.Booking;
import org.openjfx.services.BookingService;
import org.w3c.dom.Text;

import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static org.dizitart.no2.objects.filters.ObjectFilters.eq;

public class HistoryBookingController {
    private static String username;
    private final ObjectRepository<Booking> BOOKING_REPOSITORY = BookingService.getBookingRepository();
    private static ObservableList<Booking> bookings;
    private static Booking selectedBooking;
    private static Stage stage = new Stage();
    private Stage anotherStage;

    @FXML
    public TableColumn<Booking, String> offerNameColumn;
    @FXML
    public TableColumn<Booking, String> agencyColumn;
    @FXML
    public TableColumn<Booking, String> message;
    @FXML
    public TableColumn<Booking, String> ratingTableColumn;
    @FXML
    public TableView<Booking> bookingTableView;
    @FXML
    private Button travelAgenciesButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button closeButton;


    @FXML
    public void initialize() {
        getAllBookings();

        Platform.runLater(() -> {
            offerNameColumn.setCellValueFactory(new PropertyValueFactory<>("nameOfOffer"));
            agencyColumn.setCellValueFactory(new PropertyValueFactory<>("nameOfAgency"));
            message.setCellValueFactory(new PropertyValueFactory<>("message"));
            ratingTableColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
            bookingTableView.setItems(bookings);

            bookingTableView.setRowFactory(e -> {
                TableRow<Booking> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    LocalDate now = LocalDate.now();
                    String date1 = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    Date d1,d2;

                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        d1 = formatter.parse(date1);

                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        selectedBooking = bookingTableView.getSelectionModel().getSelectedItem();
                        d2 = formatter.parse(selectedBooking.getCheckOutDate());
                        System.out.println(d1.toString()+" " +d2.toString());
                        if(d2.compareTo(d1) > 0) {
                            row.setDisable(true);
                        } else {
                            anotherStage.close();
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ratingPage.fxml"));
                                Parent root = loader.load();
                                RatingController controller = loader.getController();
                                controller.setAnotherStage(anotherStage);
                                stage.setScene(new Scene(root));
                                stage.show();
                            } catch (Exception ee) {
                                System.out.println("eroare");
                            }
                        }
                    }
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                });
                return row;
            });
        });
    }

    @FXML
    public void handleClose() {

    }

    @FXML
    public void handleLogout() {
        try {
            Parent root= FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml"));
            Stage primaryStage = (Stage) (logoutButton.getScene().getWindow());
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    public void getAllBookings(){
        Cursor<Booking> cursor = BOOKING_REPOSITORY.find(eq("clientUsername",username));
        bookings = FXCollections.observableArrayList();
        for(Booking b : cursor) {
            if(b.getMessage().contains("Accepted") || b.getMessage().contains("Rejected"))
                bookings.add(b);
        }
    }

    public static void setUsername(String username) {
        HistoryBookingController.username = username;
    }

    public static Booking getSelectedBooking() {
        return selectedBooking;
    }

    public void setStage(Stage astage) {
        this.anotherStage = astage;
    }
}