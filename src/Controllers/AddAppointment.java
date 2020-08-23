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

import static Controllers.Login.activeCounselor;

public class AddAppointment implements Initializable {

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
    public static Appointment appointmentToAdd = new Appointment();

    @FXML
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private static ZoneId timeZone = ZoneId.systemDefault();

    @FXML
    void Quit(ActionEvent event) {
        quitPopup(buttonCancel);
    }

    static void quitPopup(Button buttonCancel) {
        Alert quitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        quitAlert.setTitle("Quit");
        quitAlert.setHeaderText("Would you like to quit?");
        quitAlert.setContentText("Select OK to quit. Otherwise, select Cancel.");
        quitAlert.showAndWait();
        if (quitAlert.getResult() == ButtonType.OK) {
            try {
                loadCalendar(buttonCancel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            quitAlert.close();
        }
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
                loadConfirmData();
                appointmentToAdd.isValidInput();
                appointmentToAdd.doesNotConflict();
                if (appointmentToAdd.isValidInput() && appointmentToAdd.doesNotConflict()) {
                    DBAppointment.addAppointment(appointmentToAdd);
                    loadCalendar(buttonSave);
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

    static void loadCalendar(Button buttonSave) throws IOException {
        save();
        Stage newAppointmentStage = (Stage) buttonSave.getScene().getWindow();
        newAppointmentStage.close();
    }

    static void save() throws IOException {
        FXMLLoader apptCalendarLoader = new FXMLLoader(Calendar.class.getResource("/Views/HomeView.fxml"));
        Parent apptCalendarScreen = apptCalendarLoader.load();
        Scene apptCalendarScene = new Scene(apptCalendarScreen);
        Stage apptCalendarStage = new Stage();
        apptCalendarStage.setTitle("Appointments");
        apptCalendarStage.setScene(apptCalendarScene);
        apptCalendarStage.show();
    }

    public void loadConfirmData() throws ExceptionAppt {
        try {
            setData(appointmentToAdd, comboPatient, inputNotes, comboApptType, dateInputStart, timeInputStart, timeFormat, timeZone);
        } catch (NullPointerException e) {
            Alert emptyAlert = new Alert(Alert.AlertType.ERROR);
            emptyAlert.setTitle("Unable to Add Appointment");
            emptyAlert.setHeaderText("Adding appointment failed");
            emptyAlert.setContentText("A patient must be assigned to the appointment.");
            emptyAlert.showAndWait();
        }
    }

    static void setData(Appointment appointmentToAdd, ComboBox<Patient> comboPatient, TextField inputNotes, ComboBox<AppointmentType> comboApptType, DatePicker dateInputStart, Spinner<LocalTime> timeInputStart, DateTimeFormatter timeFormat, ZoneId timeZone) {
        appointmentToAdd.setPatientID(comboPatient.getValue().getPatientID());
        appointmentToAdd.setCounselorID(activeCounselor.getCounselorID());
        appointmentToAdd.setNotes(inputNotes.getText());
        appointmentToAdd.setApt_type_id(comboApptType.getValue().getAPTtype_id());
        appointmentToAdd.setStart(ZonedDateTime.of(LocalDate.parse(dateInputStart.getValue().toString()),
                LocalTime.parse(timeInputStart.getValue().toString(), timeFormat), timeZone));
    }

    public void loadPatients() {
        ObservableList<Patient> Patients = DBPatient.findAllPatients();
        comboPatient.setItems(Patients);
        comboPatient.setPromptText("Choose a patient:");
    }

    public void loadApptTypes() {
        ObservableList<AppointmentType> AppointmentTypes = DBAPTtype.loadAllApptTypes();
        comboApptType.setItems(AppointmentTypes);
        comboApptType.setPromptText("Choose an appointment type:");
    }

    public void convertString() {
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

    public void readDateTime() {
        dateInputStart.setValue(LocalDate.now());
        timeInputStart.setValueFactory(startSVF);
        startSVF.setValue(LocalTime.of(8, 00));
        endSVF.setValue(LocalTime.of(17, 00));
    }

    SpinnerValueFactory startSVF = new SpinnerValueFactory<LocalTime>() {
        {
            setConverter(new LocalTimeStringConverter(timeFormat, null));
        }

        @Override
        public void decrement(int steps) {
            LocalTime time = getValue();
            setValue(time.minusHours(steps));
            setValue(time.minusMinutes(16 - steps));
        }

        @Override
        public void increment(int steps) {
            LocalTime time = getValue();
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
            LocalTime time = getValue();
            setValue(time.minusHours(steps));
            setValue(time.minusMinutes(16 - steps));
        }

        @Override
        public void increment(int steps) {
            LocalTime time = getValue();
            setValue(time.plusHours(steps));
            setValue(time.plusMinutes(steps + 14));
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPatients();
        loadApptTypes();
        convertString();
        readDateTime();
    }
}
