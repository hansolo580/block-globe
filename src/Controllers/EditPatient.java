package Controllers;

import Data.DBAddress;
import Data.DBPatient;
import Exceptions.ExceptionPatient;
import Models.Address;
import Models.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static Controllers.SelectPatient.currentPatient;

public class EditPatient implements Initializable {

    @FXML
    private TextField inputPatientName;

    @FXML
    private TextField inputPatientState;

    @FXML
    private TextField inputPatientPhone;

    @FXML
    private TextField inputInsurance;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonQuit;

    @FXML
    private Address patientAddress = DBAddress.lookupAddress(currentPatient.getAddressID());

    @FXML
    void quit(ActionEvent event) {
        AddPatient.quitAlert(buttonQuit);
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
                modifyPatientData();
                if (Patient.isValid(currentPatient, inputPatientState.getText(), inputPatientPhone.getText(), inputInsurance.getText())) {
                    try {
                        DBPatient.modifyPatient(currentPatient);
                        DBAddress.modifyAddress(patientAddress);
                        AddPatient.actionConfirmed(buttonSave);
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

    public void modifyPatientData() {
        try {
            currentPatient.setPatientName(inputPatientName.getText());
            patientAddress.setPhone(inputPatientPhone.getText());
            patientAddress.setState(inputPatientState.getText());
            currentPatient.setInsurance(inputInsurance.getText());
        } catch (NullPointerException e) {
            Alert emptyAlert = new Alert(Alert.AlertType.ERROR);
            emptyAlert.setTitle("Cannot Add Patient");
            emptyAlert.setHeaderText("Patient cannot be added with incomplete fields.");
            emptyAlert.setContentText(e.getCause().toString());
            emptyAlert.showAndWait();
        }
    }

    public void loadPatientData() {
        inputPatientName.setText(currentPatient.getPatientName());
        inputInsurance.setText(currentPatient.getInsurance());
        inputPatientPhone.setText(patientAddress.getPhone());
        inputPatientState.setText(patientAddress.getState());
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPatientData();
    }
}
