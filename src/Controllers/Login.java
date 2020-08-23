package Controllers;

import Data.DBAppointment;
import Data.DBCounselor;
import Models.Appointment;
import Models.Counselor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.TimeZone;

public class Login implements Initializable {
    ResourceBundle loginResource;
    Locale counselorLocale;
    Logger successfulLog = Logger.getLogger("successfulLogins.txt");
    Logger failedLog = Logger.getLogger("failedLogins.txt");

    public static Counselor activeCounselor = new Counselor();

    @FXML
    private Label labelPopUp;

    @FXML
    private Label displayEasternTime;

    @FXML
    private Label displayLocalTime;

    @FXML
    private Label languageDisplay;

    @FXML
    private TextField inputPassword;

    @FXML
    private TextField inputCounselorname;

    @FXML
    private TextField inputPin;

    @FXML
    private Label labelCounselorname;

    @FXML
    private Label labelPassword;

    @FXML
    private Button buttonLogin;

    @FXML
    private Button buttonQuit;

    @FXML
    void actionQuit(ActionEvent quitButton) {
        Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
        popup.setTitle("Quit Confirmation");
        popup.setHeaderText("Do you want to quit?");
        popup.showAndWait();
        if (popup.getResult() == ButtonType.OK) {
            Stage winHome = (Stage) ((javafx.scene.Node) quitButton.getSource()).getScene().getWindow();
            winHome.close();
        } else {
            popup.close();
        }
    }

    @FXML
    void actionLogin() throws Exception {
        String c_name = inputCounselorname.getText();
        String password = inputPassword.getText();
        String pin = inputPin.getText();
        activeCounselor.setCounselorName(c_name);
        activeCounselor.setPassword(password);
        activeCounselor.setPin(pin);

        FileHandler successfulLogHandler = new FileHandler("successfulLogins.txt", true);
        SimpleFormatter counselorLogFormatter = new SimpleFormatter();
        successfulLogHandler.setFormatter(counselorLogFormatter);
        successfulLog.addHandler(successfulLogHandler);
        successfulLog.setLevel(Level.INFO);

        FileHandler failedLogHandler = new FileHandler("failedLogins.txt", true);
        failedLogHandler.setFormatter(counselorLogFormatter);
        failedLog.addHandler(failedLogHandler);
        failedLog.setLevel(Level.WARNING);

        try {
            ObservableList<Counselor> counselorLoginData = DBCounselor.loadAllCounselors();

            //Iterates through observable list of active counselors by utilizing a lambda
            counselorLoginData.forEach((i) -> {
                try {
                    int count = 0;
                    if (activeCounselor.getCounselorName().equals(i.getCounselorName())) {
                        count++;
                    }
                    if (activeCounselor.getPassword().equals(i.getCounselorName())) {
                        count++;
                    }
                    if (activeCounselor.getPin().equals(i.getPin())) {
                        count++;
                    }
                    if (count >= 3) {
                        activeCounselor.setCounselorID(i.getCounselorID());
                        System.out.println("Credentials verified");
                        try {
                            ObservableList<Appointment> next4HoursAppts = DBAppointment.getNext4HoursAppts();
                            {
                                Alert apptPopup = new Alert(Alert.AlertType.INFORMATION);
                                apptPopup.setTitle("Appointment Warning");
                                apptPopup.setHeaderText("The following appointments are coming up in the next 4 hours:");
                                ObservableList<String> apptDetails = FXCollections.observableArrayList();
                                if(!next4HoursAppts.isEmpty()) {
                                    next4HoursAppts.forEach(appointment -> {
                                        apptDetails.add(DBAppointment.printApptDetails(appointment));
                                    });
                                    apptPopup.setContentText(apptDetails.toString());
                                }
                                else {
                                    apptPopup.setContentText("No appointments in the next 4 hours.");
                                    }
                                apptPopup.showAndWait();
                                if (apptPopup.getResult() == ButtonType.OK) {
                                    successfulLog.log(Level.INFO, "{0} logged in.", activeCounselor.getCounselorName());
                                    Stage loginScreen = (Stage) buttonLogin.getScene().getWindow();
                                    loginScreen.close();
                                    AddAppointment.save();
                                } else {
                                    apptPopup.close();
                                }
                            };
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        this.labelPopUp.setText(this.loginResource.getString("labelLoginError") + ".");
                        failedLog.log(Level.WARNING, "Invalid login for counselor: {0}", activeCounselor.getCounselorName());
                    }
                } catch (AssertionError e) {
                    System.out.println(e.getMessage());
                    this.labelPopUp.setText(this.loginResource.getString("labelLoginError") + ".");
                    failedLog.log(Level.WARNING, "Invalid login for counselor: {0}", activeCounselor.getCounselorName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle loginResource) {

        TimeZone mainOfficeTime = TimeZone.getTimeZone("America/New_York");
        this.counselorLocale = Locale.getDefault();
        this.loginResource = ResourceBundle.getBundle("Localization/rb", this.counselorLocale);
        this.labelCounselorname.setText(this.loginResource.getString("counselorname") + ":");
        this.labelPassword.setText(this.loginResource.getString("password") + ":");
        this.inputCounselorname.setPromptText(this.loginResource.getString("counselornameRequest"));
        this.inputPassword.setPromptText(this.loginResource.getString("passwordRequest"));
        this.buttonLogin.setText(this.loginResource.getString("buttonLoginText"));
        this.buttonQuit.setText(this.loginResource.getString("buttonQuitText"));
        this.languageDisplay.setText("Detected region: " + this.counselorLocale.toString());
        this.displayEasternTime.setText(java.time.ZonedDateTime.now(mainOfficeTime.toZoneId()).format(DateTimeFormatter.ofPattern("dd-M-yyyy hh:mm:ss")));
        this.displayLocalTime.setText(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-M-yyyy hh:mm:ss")));
    }
}
