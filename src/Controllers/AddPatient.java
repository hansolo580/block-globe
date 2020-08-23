package Controllers;

import Data.DBAddress;
import Data.DBPatient;
import Exceptions.ExceptionPatient;
import Models.Address;
import Models.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddPatient implements Initializable {

    @FXML
    private TextField inputPatientName;

    @FXML
    private TextField inputPatientPhone;

    @FXML
    private TextField inputPatientState;

    @FXML
    private TextField inputInsurance;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonQuit;

    @FXML
    private Patient newPatient = new Patient();
    private Address patientAddress = new Address();

    @FXML
    void quit(ActionEvent event) {
        quitAlert(buttonQuit);
    }

    static void quitAlert(Button buttonQuit) {
        Alert quitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        quitAlert.setTitle("Quit?");
        quitAlert.setHeaderText("Would you like to quit?");
        quitAlert.setContentText("Select OK to quit. Otherwise, select Cancel.");
        quitAlert.showAndWait();
        if (quitAlert.getResult() == ButtonType.OK) {
            try {
                actionConfirmed(buttonQuit);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            quitAlert.close();
        }
    }

    static void actionConfirmed(Button buttonQuit) throws IOException {
        FXMLLoader apptCalendarLoader = new FXMLLoader(Calendar.class.getResource("/Views/HomeView.fxml"));
        Parent apptCalendarScreen = apptCalendarLoader.load();
        Scene apptCalendarScene = new Scene(apptCalendarScreen);
        Stage apptCalendarStage = new Stage();
        apptCalendarStage.setTitle("Calendar");
        apptCalendarStage.setScene(apptCalendarScene);
        apptCalendarStage.show();
        Stage newPatientStage = (Stage) buttonQuit.getScene().getWindow();
        newPatientStage.close();
    }

    @FXML
    void save(ActionEvent event) {
        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
        saveAlert.setTitle("Save Changes");
        saveAlert.setHeaderText("Would you like to save your changes?");
        saveAlert.setContentText("Select OK to save changes. Otherwise, select Cancel.");
        saveAlert.showAndWait();
        if (saveAlert.getResult() == ButtonType.OK) {
            try {
                loadPatientData();
                if (Patient.isValid(newPatient, inputPatientState.getText(), inputPatientPhone.getText(), inputInsurance.getText())) {
                    try {
                        newPatient.setAddressID(DBAddress.newAddress(patientAddress));
                        DBPatient.addPatient(newPatient);
                        actionConfirmed(buttonSave);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ExceptionPatient e) {
                Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
                exceptionAlert.setTitle("Error");
                exceptionAlert.setHeaderText("An error occurred.");
                exceptionAlert.setContentText(e.getMessage());
                exceptionAlert.showAndWait().filter(response -> response == ButtonType.OK);
            }
        } else {
            saveAlert.close();
        }
    }

    public void loadPatientData() {
        try {
            newPatient.setPatientName(inputPatientName.getText());
            patientAddress.setPhone(inputPatientPhone.getText());
            patientAddress.setState(inputPatientState.getText());
            newPatient.setInsurance(inputInsurance.getText());
        } catch (NullPointerException e) {
            Alert emptyAlert = new Alert(Alert.AlertType.ERROR);
            emptyAlert.setTitle("Error");
            emptyAlert.setHeaderText("Unable to add patient.");
            emptyAlert.setContentText("Mandatory field incomplete.");
            emptyAlert.showAndWait();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
