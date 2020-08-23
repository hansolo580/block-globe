package Controllers;

import Data.DBPatient;
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

import static Controllers.Calendar.isEdit;

public class SelectPatient implements Initializable {

    @FXML
    private ListView<Patient> listViewPatient;

    @FXML
    private Button buttonQuit;

    @FXML
    private Button buttonSelect;

    @FXML
    public static Patient currentPatient;

    @FXML
    void quit(ActionEvent event) {
        AddPatient.quitAlert(buttonQuit);
    }

    @FXML
    void select(ActionEvent event) {
        if (listViewPatient.getSelectionModel().getSelectedItem() == null) {
            Alert emptyAlert = new Alert(Alert.AlertType.ERROR);
            emptyAlert.setTitle("Error");
            emptyAlert.setHeaderText("Unable to edit patient.");
            emptyAlert.setContentText("A patient must be selected.");
            emptyAlert.showAndWait();
        } else if (isEdit) {
            Alert editAlert = new Alert(Alert.AlertType.CONFIRMATION);
            editAlert.setTitle("Edit Patient");
            editAlert.setHeaderText("Would you like to modify this patient?");
            editAlert.setContentText("Select OK to continue. Otherwise, select cancel.");
            editAlert.showAndWait();
            if (editAlert.getResult() == ButtonType.OK) {
                try {
                    currentPatient = listViewPatient.getSelectionModel().getSelectedItem();
                    FXMLLoader editPatientLoader = new FXMLLoader(EditPatient.class.getResource("/Views/UpdatePatientView.fxml"));
                    Parent editPatientScreen = editPatientLoader.load();
                    Scene editPatientScene = new Scene(editPatientScreen);
                    Stage editPatientStage = new Stage();
                    editPatientStage.setTitle("Edit Patient");
                    editPatientStage.setScene(editPatientScene);
                    editPatientStage.show();
                    Stage patientSelectionStage = (Stage) buttonSelect.getScene().getWindow();
                    patientSelectionStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Alert removeAlert = new Alert(Alert.AlertType.CONFIRMATION);
            removeAlert.setTitle("Remove Patient");
            removeAlert.setHeaderText("Would you like to remove the patient?");
            removeAlert.setContentText("Select OK to remove the patient. Otherwise, select cancel.");
            removeAlert.showAndWait();
            if (removeAlert.getResult() == ButtonType.OK) {
                currentPatient = listViewPatient.getSelectionModel().getSelectedItem();
                try {
                    DBPatient.removePatient(currentPatient);
                    loadPatients();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void convertToString() {
        listViewPatient.setCellFactory(data -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patientToModify, boolean empty) {
                super.updateItem(patientToModify, empty);

                if (empty || patientToModify.getPatientName() == null) {
                    setText("");
                } else {
                    setText(patientToModify.getPatientName());
                }
            }
        });
    }

    public void loadPatients() {
        listViewPatient.setItems(DBPatient.findAllPatients());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPatients();
        convertToString();
    }
}
