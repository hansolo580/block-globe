package Controllers;

import Data.DBAppointment;
import Models.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Report implements Initializable {

    @FXML
    private RadioButton radioAppointmentType;

    @FXML
    private RadioButton radioCounselorAppointments;

    @FXML
    private RadioButton radioStateAppointments;

    @FXML
    private TextArea outputReport;

    @FXML
    private Button buttonQuit;

    @FXML
    void loadAppointmentTypes(ActionEvent event) {

        if (radioAppointmentType.isSelected()) {
            try {
                outputReport.clear();
                ObservableList<Appointment> yearAppointments = DBAppointment.fetchYearlyAppointments();
                Integer counter = 1;

                Map<Integer, Integer> hashMap = new HashMap<>();
                for (Appointment i : yearAppointments) {
                    if (hashMap.containsKey(i.getApptTypeID())) {
                        hashMap.put(i.getApptTypeID(), hashMap.get(i.getApptTypeID()) + 1);
                    } else {
                        hashMap.put(i.getApptTypeID(), counter);
                    }
                }
                for (Integer i : hashMap.keySet()) {
                    if (i == 1) {
                        outputReport.appendText("General" + ": " + hashMap.get(i) + " appointment(s) this year." + "\n");
                    } else if (i == 2) {
                        outputReport.appendText("Group" + ": " + hashMap.get(i) + " appointment(s) this year." + "\n");
                    } else if (i == 3) {
                        outputReport.appendText("Other" + ": " + hashMap.get(i) + " appointment(s) this year." + "\n");
                    } else {
                        outputReport.appendText("No Type Indicated" + ": " + hashMap.get(i) + " appointment(s) this year." + "\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void loadStateAppointments(ActionEvent event) {
        if (radioStateAppointments.isSelected()) {
            try {
                outputReport.clear();
                ObservableList<Appointment> stateAppointments = DBAppointment.fetchYearlyAppointmentsState();
                Integer counter = 1;

                Map<String, Integer> hashMap = new HashMap<>();
                for (Appointment i : stateAppointments) {
                    if (hashMap.containsKey(i.getState())) {
                        hashMap.put(i.getState(), hashMap.get(i.getState()) + 1);
                    } else {
                        hashMap.put(i.getState(), counter);
                    }
                }
                for (String i : hashMap.keySet()) {
                    outputReport.appendText(i + ": " + hashMap.get(i) + " appointment(s) this year." + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void loadCounselorAppointments(ActionEvent event) {
        if (radioCounselorAppointments.isSelected()) {
            try {
                outputReport.clear();
                ObservableList<Appointment> counselorAppointments = DBAppointment.fetchYearlyAppointments();
                Integer counter = 1;

                Map<Integer, Integer> hashMap = new HashMap<>();
                for (Appointment i : counselorAppointments) {
                    if (hashMap.containsKey(i.getCounselorID())) {
                        hashMap.put(i.getCounselorID(), hashMap.get(i.getCounselorID()) + 1);
                    } else {
                        hashMap.put(i.getCounselorID(), counter);
                    }
                }
                for (Integer i : hashMap.keySet()) {
                    if (i == 1) {
                        outputReport.appendText("Admin: " + hashMap.get(i) + " appointments(s) this year." + "\n");
                    } else {
                        outputReport.appendText("No counselor assigned: " + hashMap.get(i) + " appointments(s) this year." + "\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void quit(ActionEvent event) {
        AddPatient.quitAlert(buttonQuit);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
