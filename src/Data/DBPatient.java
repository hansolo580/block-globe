package Data;

import Models.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static Controllers.Login.activeCounselor;
import static Data.DBConnection.DB_CONNECTION;

public class DBPatient {

    public static ObservableList<Patient> findAllPatients() {
        ObservableList<Patient> allPatients = FXCollections.observableArrayList();
        String findAllPatientsQuery = "SELECT * FROM patient";

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(findAllPatientsQuery);
            ResultSet patientResult = connectionStatement.executeQuery();

            while (patientResult.next()) {
                Patient patient = new Patient();
                patient.setPatientID(patientResult.getInt("pt_id"));
                patient.setPatientName(patientResult.getString("pt_name"));
                patient.setAddressID(patientResult.getInt("address_id"));
                patient.setInsurance(patientResult.getString("INS_PR"));
                allPatients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allPatients;
    }

    public static Patient lookupPatient(int pt_id) {
        String lookupPatientQuery = "SELECT * FROM patient WHERE pt_id = ?";
        Patient foundPatient = new Patient();

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(lookupPatientQuery);
            connectionStatement.setInt(1, pt_id);
            ResultSet patientResult = connectionStatement.executeQuery();

            if (patientResult.next()) {
                foundPatient.setPatientID(patientResult.getInt("pt_id"));
                foundPatient.setPatientName(patientResult.getString("pt_name"));
                foundPatient.setAddressID(patientResult.getInt("address_id"));
                foundPatient.setLastUpdate(patientResult.getTimestamp("updated_at"));
                foundPatient.setLastUpdateBy(patientResult.getString("updated_by"));
                foundPatient.setInsurance(patientResult.getString("INS_PR"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundPatient;
    }

    private static int findMaxPatientID() {
        int maxPatientID = 0;
        String findMaxPatientIDQuery = "SELECT MAX(pt_id) FROM patient";

        try {
            Statement connectionStatement = DB_CONNECTION.createStatement();
            ResultSet patientResult = connectionStatement.executeQuery(findMaxPatientIDQuery);

            if (patientResult.next()) {
                maxPatientID = patientResult.getInt(1);
            }
        } catch (SQLException ignored) {
        }
        return maxPatientID + 1;
    }

    public static Patient addPatient(Patient newPatient) {
        String insertPatientQuery = String.join(" ", "INSERT INTO patient (pt_id," +
                " pt_name, address_id, INS_PR, created_at, created_by, updated_at, updated_by, address_address_id)", "VALUES (?, ?, ?," +
                " ?, NOW(), ?, NOW(), ?, ?)");

        int pt_id = findMaxPatientID();
        try {

            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(insertPatientQuery);
            connectionStatement.setInt(1, pt_id);
            connectionStatement.setString(2, newPatient.getPatientName());
            connectionStatement.setInt(3, newPatient.getAddressID());
            connectionStatement.setString(4, newPatient.getInsurance());
            connectionStatement.setString(5, activeCounselor.getCounselorName());
            connectionStatement.setString(6, activeCounselor.getCounselorName());
            connectionStatement.setInt(7, newPatient.getAddressID());
            connectionStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newPatient;
    }

    public static void modifyPatient(Patient selectedPatient) {
        String modifyPatientQuery = String.join(" ", "UPDATE patient", "SET pt_name=?, " +
                "address_id=?, INS_PR=?, updated_at=NOW(), updated_by=?", "WHERE pt_id = ?");

        try {

            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(modifyPatientQuery);
            connectionStatement.setString(1, selectedPatient.getPatientName());
            connectionStatement.setInt(2, selectedPatient.getAddressID());
            connectionStatement.setString(3, selectedPatient.getInsurance());
            connectionStatement.setString(4, activeCounselor.getCounselorName());
            connectionStatement.setInt(5, selectedPatient.getPatientID());
            connectionStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePatient(Patient selectedPatient) {
        String removePatientQuery = "DELETE FROM patient WHERE pt_id = ?";

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(removePatientQuery);
            connectionStatement.setInt(1, selectedPatient.getPatientID());
            connectionStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
