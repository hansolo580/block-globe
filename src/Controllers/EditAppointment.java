package Controllers;

import Data.DBAPTtype;
import Data.DBAppointment;
import Data.DBPatient;
import Exceptions.ExceptionAppt;
import Models.Appointment;
import Models.AppointmentType;
import Models.Patient;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static Controllers.Calendar.currentAppt;
import static Controllers.Login.activeCounselor;

public class EditAppointment implements Initializable {

    @FXML
    private ComboBox<Patient> comboPatient;

    @FXML
    private ComboBox<AppointmentType> comboApptType;

    @FXML
    private TextField inputNotes;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonCancel;

    @FXML
    private DatePicker dateInputStart;

    @FXML
    private Spinner<LocalTime> timeInputStart;

    @FXML
    public static Appointment appointmentToModify = new Appointment();

    @FXML
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @FXML
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private static ZoneId timeZone = ZoneId.systemDefault();

    @FXML
    void Quit(ActionEvent event) {
        AddAppointment.quitPopup(buttonCancel);
    }

    @FXML
    void Save(ActionEvent event) throws Exception {
        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
        saveAlert.setTitle("Save Changes");
        saveAlert.setHeaderText("Would you like to save your changes?");
        saveAlert.setContentText("Select OK to save changes. Otherwise, select Cancel.");
        saveAlert.showAndWait();
        if (saveAlert.getResult() == ButtonType.OK) {
            try {
                modifyAppointmentData();
                appointmentToModify.isValidInput();
                appointmentToModify.doesNotConflict();
                if (appointmentToModify.isValidInput() && appointmentToModify.doesNotConflict()) {
                    DBAppointment.modifyAppointment(appointmentToModify);
                    AddAppointment.loadCalendar(buttonSave);
                }
            } catch (ExceptionAppt e) {
                Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
                exceptionAlert.setTitle("Error");
                exceptionAlert.setHeaderText("An error has occurred.");
                exceptionAlert.setContentText(e.getMessage());
                exceptionAlert.showAndWait().filter(response -> response == ButtonType.OK);
            }
        } else {
            saveAlert.close();
        }
    }

    private ObservableList<Patient> allPatients = DBPatient.findAllPatients();

    public void loadPatients() {
        comboPatient.setItems(allPatients);
    }

    private ObservableList<AppointmentType> apptTypes = DBAPTtype.loadAllApptTypes();

    public void loadApptTypes() {
        comboApptType.setItems(apptTypes);
    }

    public void displayPatientName() {
        comboPatient.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient object) {
                return object.getPatientName();
            }

            @Override
            public Patient fromString(String string) {
                return comboPatient.getValue();
            }
        });
    }

    public void displayApptType() {
        comboApptType.setConverter(new StringConverter<AppointmentType>() {
            @Override
            public String toString(AppointmentType object) {
                return object.getDescription();
            }

            @Override
            public AppointmentType fromString(String string) {
                return comboApptType.getValue();
            }
        });
    }

    public void readSelectedApptData() {
        comboPatient.setValue(currentAppt.getPatient());
        comboApptType.setValue(DBAPTtype.lookupApptType(currentAppt.getApptTypeID()));
        inputNotes.setText(currentAppt.getNotes());
        dateInputStart.setValue(currentAppt.getStart().toLocalDate());
        timeInputStart.setValueFactory(startSVF);
        startSVF.setValue(currentAppt.getStart().toLocalTime());
    }

    public void modifyAppointmentData() {
        AddAppointment.setData(appointmentToModify, comboPatient, inputNotes, comboApptType, dateInputStart, timeInputStart, timeFormat, timeZone);
        appointmentToModify.setAppointmentID(currentAppt.getAppointmentID());
    }

    SpinnerValueFactory startSVF = new SpinnerValueFactory<LocalTime>() {
        {
            setConverter(new LocalTimeStringConverter(timeFormat, null));
        }

        @Override
        public void decrement(int steps) {
            LocalTime time = (LocalTime) getValue();
            setValue(time.minusHours(steps));
            setValue(time.minusMinutes(16 - steps));
        }

        @Override
        public void increment(int steps) {
            LocalTime time = (LocalTime) getValue();
            setValue(time.plusHours(steps));
            setValue(time.plusMinutes(steps + 14));
        }
    };

    SpinnerValueFactory endSVF = new SpinnerValueFactory<LocalTime>() {
        {
            setConverter(new LocalTimeStringConverter(timeFormat, null));
        }

        @Override
        public void decrement(int steps) {
            LocalTime time = (LocalTime) getValue();
            setValue(time.minusHours(steps));
            setValue(time.minusMinutes(16 - steps));
        }

        @Override
        public void increment(int steps) {
            LocalTime time = (LocalTime) getValue();
            setValue(time.plusHours(steps));
            setValue(time.plusMinutes(steps + 14));
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadApptTypes();
        loadPatients();
        readSelectedApptData();
        displayPatientName();
        displayApptType();
    }

}
