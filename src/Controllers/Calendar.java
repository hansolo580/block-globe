package Controllers;

import Data.DBAppointment;
import Models.Appointment;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static Data.DBAppointment.fetchMonthlyAppointments;
import static Data.DBAppointment.fetchWeeklyAppointments;
import static Data.DBAppointment.fetchBiWeekAppointments;


public class Calendar implements Initializable {

    @FXML
    private Button buttonAddAppt;

    @FXML
    private Button buttonEditAppt;

    @FXML
    private Button buttonAddPatient;

    @FXML
    private Button buttonEditPatient;

    @FXML
    private Button buttonRemovePatient;

    @FXML
    private Tab tabWeekAppts;

    @FXML
    private TableView<Appointment> tableViewWeeklyAppts;

    @FXML
    private TableColumn<Appointment, String> tableColumnWeeklyPatientName;

    @FXML
    private TableColumn<Appointment, String> tableColumnWeeklyApptType;

    @FXML
    private TableColumn<Appointment, String> tableColumnWeeklyApptNotes;

    @FXML
    private TableColumn<Appointment, String> tableColumnWeeklyApptStart;

    @FXML
    private Tab tabBiWeekAppts;

    @FXML
    private TableView<Appointment> tableViewBiWeekAppts;

    @FXML
    private TableColumn<Appointment, String> tableColumnBiWeekPatientName;

    @FXML
    private TableColumn<Appointment, String> tableColumnBiWeekApptType;

    @FXML
    private TableColumn<Appointment, String> tableColumnBiWeekApptStart;

    @FXML
    private TableColumn<Appointment, String> tableColumnBiWeekApptNotes;

    @FXML
    private Tab tabMonthAppts;

    @FXML
    private TableView<Appointment> tableViewMonthAppts;

    @FXML
    private TableColumn<Appointment, String> tableColumnMonthPatientName;

    @FXML
    private TableColumn<Appointment, String> tableColumnMonthApptType;

    @FXML
    private TableColumn<Appointment, String> tableColumnMonthApptNotes;

    @FXML
    private TableColumn<Appointment, String> tableColumnMonthApptStart;

    @FXML
    private final DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a z");

    @FXML
    public static Appointment currentAppt;

    @FXML
    public static boolean isEdit;

    @FXML
    void fetchNewAppt(ActionEvent event) {
        Alert newAlert = new Alert(Alert.AlertType.CONFIRMATION);
        newAlert.setTitle("New Appointment");
        newAlert.setHeaderText("Would you like to add a new appointment?");
        newAlert.setContentText("To continue, select OK. Press cancel to go back.");
        newAlert.showAndWait();
        if (newAlert.getResult() == ButtonType.OK) {
            try {
                FXMLLoader newApptLoader = new FXMLLoader(AddAppointment.class.getResource("/Views/AddAppointmentView.fxml"));
                Parent newApptScreen = newApptLoader.load();
                Scene newApptScene = new Scene(newApptScreen);
                Stage newApptStage = new Stage();
                newApptStage.setTitle("New Appointment");
                newApptStage.setScene(newApptScene);
                newApptStage.show();
                Stage apptCalendarStage = (Stage) buttonAddAppt.getScene().getWindow();
                apptCalendarStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void editAppt(ActionEvent event) {
        if (tabWeekAppts.isSelected()) {
            editApptAlert(tableViewWeeklyAppts);
        } else if (tabBiWeekAppts.isSelected()) {
            editApptAlert(tableViewBiWeekAppts);
        } else if (tabMonthAppts.isSelected()) {
            editApptAlert(tableViewMonthAppts);
        }
    }

    private void editApptAlert(TableView<Appointment> tableViewWeeklyAppts) {
        currentAppt = tableViewWeeklyAppts.getSelectionModel().getSelectedItem();
        if (currentAppt == null) {
            Alert noneSelectedAlert = new Alert(Alert.AlertType.ERROR);
            noneSelectedAlert.setTitle("Cannot Edit Appointment");
            noneSelectedAlert.setHeaderText("No appointment was selected to edit.");
            noneSelectedAlert.showAndWait();
        } else {
            Alert editAlert = new Alert(Alert.AlertType.CONFIRMATION);
            editAlert.setTitle("Edit Appointment");
            editAlert.setHeaderText("Would you like to edit this appointment?");
            editAlert.setContentText("Select OK to edit the appointment. Cancel to go back.");
            editAlert.showAndWait();
            if (editAlert.getResult() == ButtonType.OK) {
                try {
                    FXMLLoader editApptLoader = new FXMLLoader(EditAppointment.class.getResource("/Views/EditAppointmentView.fxml"));
                    Parent editApptScreen = editApptLoader.load();
                    Scene editApptScene = new Scene(editApptScreen);
                    Stage editApptStage = new Stage();
                    editApptStage.setTitle("Edit Appointment");
                    editApptStage.setScene(editApptScene);
                    editApptStage.show();
                    Stage apptCalendarStage = (Stage) buttonEditAppt.getScene().getWindow();
                    apptCalendarStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void removeAppt(ActionEvent event) {
        if (tabWeekAppts.isSelected()) {
            removeApptAlert(tableViewWeeklyAppts);
        } else if (tabBiWeekAppts.isSelected()) {
            removeApptAlert(tableViewBiWeekAppts);
        } else if (tabMonthAppts.isSelected()) {
            removeApptAlert(tableViewMonthAppts);
        }
    }

    private void removeApptAlert(TableView<Appointment> tableViewMonthAppts) {
        Alert removeAlert = new Alert(Alert.AlertType.CONFIRMATION);
        removeAlert.setTitle("Remove Appointment");
        removeAlert.setHeaderText("Remove appointment?");
        removeAlert.setContentText("To remove the appointment, select OK. Otherwise, select Cancel.");
        removeAlert.showAndWait();
        if (removeAlert.getResult() == ButtonType.OK) {
            try {
                Appointment apptToRemove = tableViewMonthAppts.getSelectionModel().getSelectedItem();
                DBAppointment.removeAppointment(apptToRemove);
                loadAppointments();
            } catch (NullPointerException e) {
                Alert noneSelectedAlert = new Alert(Alert.AlertType.ERROR);
                noneSelectedAlert.setTitle("Cannot Remove Appointment");
                noneSelectedAlert.setHeaderText("No appointment was selected to remove.");
                noneSelectedAlert.showAndWait();
            }
        } else {
            removeAlert.close();
        }
    }

    @FXML
    void newPatient(ActionEvent event) {
        Alert newAlert = new Alert(Alert.AlertType.CONFIRMATION);
        newAlert.setTitle("Add Patient");
        newAlert.setHeaderText("Would you like to add a patient?");
        newAlert.setContentText("Select OK to add a patient. Otherwise, select Cancel.");
        newAlert.showAndWait();
        if (newAlert.getResult() == ButtonType.OK) {
            try {
                FXMLLoader newPatientLoader = new FXMLLoader(AddPatient.class.getResource("/Views/AddPatientView.fxml"));
                Parent newPatientScreen = newPatientLoader.load();
                Scene newPatientScene = new Scene(newPatientScreen);
                Stage newPatientStage = new Stage();
                newPatientStage.setTitle("New Patient");
                newPatientStage.setScene(newPatientScene);
                newPatientStage.show();
                Stage apptCalendarStage = (Stage) buttonAddPatient.getScene().getWindow();
                apptCalendarStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void editPatient(ActionEvent event) {
        Alert editAlert = new Alert(Alert.AlertType.CONFIRMATION);
        editAlert.setTitle("Edit Patient");
        editAlert.setHeaderText("Would you like to edit a patient?");
        editAlert.setContentText("Select OK to edit a patient, or select Cancel to return.");
        editAlert.showAndWait();
        if (editAlert.getResult() == ButtonType.OK) {
            try {
                isEdit = true;
                editPatientPopup(buttonEditPatient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            editAlert.close();
        }
    }

    private void editPatientPopup(Button buttonEditPatient) throws IOException {
        FXMLLoader selectPatientLoader = new FXMLLoader(SelectPatient.class.getResource("/Views/SelectPatientView.fxml"));
        Parent selectPatientScreen = selectPatientLoader.load();
        Scene selectPatientScene = new Scene(selectPatientScreen);
        Stage selectPatientStage = new Stage();
        selectPatientStage.setTitle("Select Patient");
        selectPatientStage.setScene(selectPatientScene);
        selectPatientStage.show();
        Stage apptCalendarStage = (Stage) buttonEditPatient.getScene().getWindow();
        apptCalendarStage.close();
    }

    @FXML
    void removePatient(ActionEvent event) {
        Alert removeAlert = new Alert(Alert.AlertType.CONFIRMATION);
        removeAlert.setTitle("Remove Patient");
        removeAlert.setHeaderText("Would you like to delete a patient?");
        removeAlert.setContentText("Select OK to continue. Otherwise, select Cancel.");
        removeAlert.showAndWait();
        if (removeAlert.getResult() == ButtonType.OK) {
            try {
                isEdit = false;
                editPatientPopup(buttonRemovePatient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            removeAlert.close();
        }
    }

    @FXML
    void loadReport(ActionEvent event) {
        try {
            FXMLLoader reportLoader = new FXMLLoader(Report.class.getResource("/Views/ReportView.fxml"));
            Parent reportScreen = reportLoader.load();
            Scene reportScene = new Scene(reportScreen);
            Stage reportStage = new Stage();
            reportStage.setTitle("Reports");
            reportStage.setScene(reportScene);
            reportStage.show();
            Stage apptCalendarStage = (Stage) buttonEditPatient.getScene().getWindow();
            apptCalendarStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void loadSuccessLog(ActionEvent event) {
        try {
            ProcessBuilder logBuilder = new ProcessBuilder("Notepad.exe", "successfulLogins.txt");
            logBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void loadFailLog(ActionEvent event) {
        try {
            ProcessBuilder logBuilder = new ProcessBuilder("Notepad.exe", "failedLogins.txt");
            logBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void quitAction(ActionEvent event) {
        Alert quitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        quitAlert.setTitle("Quit");
        quitAlert.setHeaderText("Would you like to quit the program?");
        quitAlert.setContentText("Select OK to quit. Otherwise, select Cancel.");
        quitAlert.showAndWait();
        if (quitAlert.getResult() == ButtonType.OK) {
            Stage homeScreen = (Stage) ((Node) event.getSource()).getScene().getWindow();
            homeScreen.close();
        } else {
            quitAlert.close();
        }
    }

    public void loadAppointments() {
        //Used a lambda statement to iterate through data for load into week, biweek, and month tabs.
        //WEEKLY
        loadApptToColumn(tableColumnWeeklyPatientName, tableColumnWeeklyApptNotes, tableColumnWeeklyApptStart, tableColumnWeeklyApptType);
        tableViewWeeklyAppts.setItems(fetchWeeklyAppointments());
        //MONTHLY
        loadApptToColumn(tableColumnMonthPatientName, tableColumnMonthApptNotes, tableColumnMonthApptStart, tableColumnMonthApptType);
        tableViewMonthAppts.setItems(fetchMonthlyAppointments());
        //BIWEEKLY
        loadApptToColumn(tableColumnBiWeekPatientName, tableColumnBiWeekApptNotes, tableColumnBiWeekApptStart, tableColumnBiWeekApptType);
        tableViewBiWeekAppts.setItems(fetchBiWeekAppointments());
    }

    private void loadApptToColumn(TableColumn<Appointment, String> tableColumnWeeklyPatientName, TableColumn<Appointment, String> tableColumnWeeklyApptNotes, TableColumn<Appointment, String> tableColumnWeeklyApptStart, TableColumn<Appointment, String> tableColumnWeeklyApptType) {
        tableColumnWeeklyPatientName.setCellValueFactory(data -> {
            return data.getValue().getPatient().pt_nameProperty();
        });
        tableColumnWeeklyApptNotes.setCellValueFactory(data -> {
            return data.getValue().notesProperty();
        });
        tableColumnWeeklyApptStart.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStart().format(formattedTime)));
        tableColumnWeeklyApptType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApptType()));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAppointments();
    }
}
