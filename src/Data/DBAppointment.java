package Data;

import Models.Appointment;
import Models.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static Controllers.Login.activeCounselor;
import static Data.DBConnection.DB_CONNECTION;

public class DBAppointment {
    private static ZoneId timeZoneID = ZoneId.systemDefault();

    public static ObservableList<Appointment> fetchWeeklyAppointments() {
        ObservableList<Appointment> weeklyAppointments = FXCollections.observableArrayList();
        String fetchWeeklyAppointmentsQuery = "SELECT patient.*, appointment.* FROM patient RIGHT JOIN appointment ON " +
                "patient.pt_id = appointment.pt_id WHERE start_datetime BETWEEN NOW() AND (SELECT ADDDATE(NOW(), " +
                "INTERVAL 7 DAY))";

        return loadApptData(weeklyAppointments, fetchWeeklyAppointmentsQuery);
    }

    private static ObservableList<Appointment> loadApptData(ObservableList<Appointment> weeklyAppointments, String fetchWeeklyAppointmentsQuery) {
        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(fetchWeeklyAppointmentsQuery);
            ResultSet appointmentResult = connectionStatement.executeQuery();

            while (appointmentResult.next()) {
                Patient assignedPatient = DBPatient.lookupPatient(appointmentResult.getInt("pt_id"));
                Appointment addAppointment = new Appointment();
                addAppointment.setPatient(assignedPatient);
                addAppointment.setAppointmentID(appointmentResult.getInt("apt_id"));
                addAppointment.setPatientID(appointmentResult.getInt("pt_id"));
                addAppointment.setCounselorID(appointmentResult.getInt("cr_id"));
                addAppointment.setNotes(appointmentResult.getString("notes"));
                addAppointment.setApt_type_id(appointmentResult.getInt("apt_type_id"));

                LocalDateTime UTCstart = appointmentResult.getTimestamp("start_datetime").toLocalDateTime();
                ZonedDateTime localStart = ZonedDateTime.ofInstant(UTCstart.toInstant(ZoneOffset.UTC), timeZoneID);

                addAppointment.setStart(localStart);
                weeklyAppointments.add(addAppointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weeklyAppointments;
    }

    public static ObservableList<Appointment> fetchBiWeekAppointments() {
        ObservableList<Appointment> biWeekAppointments = FXCollections.observableArrayList();
        String fetchBiWeekAppointmentsQuery = "SELECT patient.*, appointment.* FROM patient RIGHT JOIN appointment ON " +
                "patient.pt_id = appointment.pt_id WHERE start_datetime BETWEEN NOW() AND (SELECT ADDDATE(NOW(), " +
                "INTERVAL 14 DAY))";

        return loadApptData(biWeekAppointments, fetchBiWeekAppointmentsQuery);
    }

    public static ObservableList<Appointment> fetchMonthlyAppointments() {
        ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();
        String fetchMonthlyAppointmentsQuery = "SELECT patient.*, appointment.* FROM patient RIGHT JOIN appointment " +
                "ON patient.pt_id = appointment.pt_id WHERE start_datetime BETWEEN NOW() AND (SELECT LAST_DAY(NOW()))";

        return loadApptData(monthlyAppointments, fetchMonthlyAppointmentsQuery);
    }

    public static ObservableList<Appointment> fetchYearlyAppointments() {
        ObservableList<Appointment> yearlyAppointments = FXCollections.observableArrayList();
        String fetchYearlyAppointmentsQuery = "SELECT patient.*, appointment.* FROM patient RIGHT JOIN appointment " +
                "ON patient.pt_id = appointment.pt_id WHERE YEAR(start_datetime) = YEAR(CURDATE())";

        return loadApptData(yearlyAppointments, fetchYearlyAppointmentsQuery);
    }

    public static ObservableList<Appointment> fetchYearlyAppointmentsState() {
        ObservableList<Appointment> yearlyAppointments = FXCollections.observableArrayList();
        String fetchYearlyAppointmentsQuery = "SELECT patient.*, appointment.*, address.* FROM patient RIGHT JOIN appointment " +
                "ON patient.pt_id = appointment.pt_id RIGHT JOIN address ON patient.address_id = address.address_id WHERE YEAR(start_datetime) = YEAR(CURDATE())";

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(fetchYearlyAppointmentsQuery);
            ResultSet appointmentResult = connectionStatement.executeQuery();

            while (appointmentResult.next()) {
                Patient assignedPatient = DBPatient.lookupPatient(appointmentResult.getInt("pt_id"));
                Appointment addAppointment = new Appointment();
                addAppointment.setPatient(assignedPatient);
                addAppointment.setAppointmentID(appointmentResult.getInt("apt_id"));
                addAppointment.setPatientID(appointmentResult.getInt("pt_id"));
                addAppointment.setCounselorID(appointmentResult.getInt("cr_id"));
                addAppointment.setNotes(appointmentResult.getString("notes"));
                addAppointment.setApt_type_id(appointmentResult.getInt("apt_type_id"));
                addAppointment.setState(appointmentResult.getString("state"));

                LocalDateTime UTCstart = appointmentResult.getTimestamp("start_datetime").toLocalDateTime();
                ZonedDateTime localStart = ZonedDateTime.ofInstant(UTCstart.toInstant(ZoneOffset.UTC), timeZoneID);

                addAppointment.setStart(localStart);
                yearlyAppointments.add(addAppointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return yearlyAppointments;
    }

    public static ObservableList<Appointment> getNext4HoursAppts() {
        String fetchNextApptQuery = "SELECT patient.pt_name, appointment.* FROM appointment JOIN patient ON " +
                "appointment.pt_id = patient.pt_id WHERE (start_datetime BETWEEN ? AND ADDTIME(?, '04:00:00'))";

        ObservableList<Appointment> upcomingAppts = FXCollections.observableArrayList();

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(fetchNextApptQuery);
            ZonedDateTime currentLocalTime = ZonedDateTime.now(timeZoneID);
            ZonedDateTime currentUDTTime = currentLocalTime.withZoneSameInstant(ZoneId.of("UTC"));
            LocalDateTime localDateTime = currentUDTTime.toLocalDateTime();
            connectionStatement.setTimestamp(1, Timestamp.valueOf(localDateTime));
            connectionStatement.setTimestamp(2, Timestamp.valueOf(localDateTime));
            ResultSet appointmentResult = connectionStatement.executeQuery();
            ResultSetMetaData rsmd = appointmentResult.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (appointmentResult.next()) {
/*                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = appointmentResult.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }*/
                Appointment nextAppt = new Appointment();
/*
                System.out.println("");
*/
                Patient currentPatient = new Patient();
                currentPatient.setPatientName(appointmentResult.getString("pt_name"));
                nextAppt.setPatient(currentPatient);
                nextAppt.setAppointmentID(appointmentResult.getInt("apt_id"));
                nextAppt.setPatientID(appointmentResult.getInt("pt_id"));
                nextAppt.setCounselorID(appointmentResult.getInt("cr_id"));
                nextAppt.setNotes(appointmentResult.getString("notes"));
                nextAppt.setApt_type_id(appointmentResult.getInt("apt_type_id"));
                LocalDateTime UTCstart = appointmentResult.getTimestamp("start_datetime").toLocalDateTime();
                ZonedDateTime localStart = ZonedDateTime.ofInstant(UTCstart.toInstant(ZoneOffset.UTC), timeZoneID);
                nextAppt.setStart(localStart);
                LocalDateTime localCreate = appointmentResult.getTimestamp("created_at").toLocalDateTime();
                nextAppt.setCreateDate(localCreate);
                nextAppt.setCreatedBy(appointmentResult.getString("created_by"));
                Timestamp localUpdate = appointmentResult.getTimestamp("updated_at");
                nextAppt.setLastUpdate(localUpdate);
                nextAppt.setLastUpdateBy(appointmentResult.getString("updated_by"));
                upcomingAppts.add(nextAppt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return upcomingAppts;
    }

    public static String printApptDetails(Appointment apptToPrint) {
        String patient = apptToPrint.getPatient().getPatientName();
        LocalDateTime startTime = apptToPrint.getStart().toLocalDateTime();
        String apptType = apptToPrint.getApptType();
        String notes = apptToPrint.getNotes();
        String printLine = "Patient: " + patient + "\n" +
                "Appt Type: " + apptType + "\n" +
                "Start Time: " + startTime + "\n" +
                "Notes: " + notes + "\n" + "\n";
        return printLine;
    }

    public static ObservableList<Appointment> findCounselorConflicts(LocalDateTime startTime, Appointment appt) {
        ObservableList<Appointment> fetchedConflictingAppointments = FXCollections.observableArrayList();
        String fetchConflictingAppointmentsQuery = "SELECT * FROM appointment WHERE (start_datetime = ? AND cr_id = ? AND apt_id != ?)";

        try {
            LocalDateTime localStartTime = startTime.atZone(timeZoneID).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(fetchConflictingAppointmentsQuery);
            connectionStatement.setTimestamp(1, Timestamp.valueOf(localStartTime));
            connectionStatement.setObject(2, appt.getCounselorID());
            loadAppointmentsFromQuery(appt, fetchedConflictingAppointments, connectionStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fetchedConflictingAppointments;
    }

    private static void loadAppointmentsFromQuery(Appointment appt, ObservableList<Appointment> fetchedConflictingAppointments, PreparedStatement connectionStatement) throws SQLException {
        connectionStatement.setObject(3, appt.getAppointmentID());
        ResultSet appointmentResult = connectionStatement.executeQuery();

        while (appointmentResult.next()) {
            Appointment conflictingAppointment = new Appointment();
            conflictingAppointment.setAppointmentID(appointmentResult.getInt("apt_id"));
            conflictingAppointment.setNotes(appointmentResult.getString("notes"));
            LocalDateTime UTCstart = appointmentResult.getTimestamp("start_datetime").toLocalDateTime();
            ZonedDateTime localStart = ZonedDateTime.ofInstant(UTCstart.toInstant(ZoneOffset.UTC), timeZoneID);
            conflictingAppointment.setStart(localStart);
            fetchedConflictingAppointments.add(conflictingAppointment);
        }
    }

    public static ObservableList<Appointment> findPatientConflicts(LocalDateTime startTime, Appointment appt) {
        ObservableList<Appointment> fetchedConflictingAppointments = FXCollections.observableArrayList();
        String fetchConflictingAppointmentsQuery = "SELECT * FROM appointment WHERE (start_datetime = ? AND pt_id = ? AND apt_id != ?)";

        try {
            LocalDateTime localStartTime = startTime.atZone(timeZoneID).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(fetchConflictingAppointmentsQuery);
            connectionStatement.setTimestamp(1, Timestamp.valueOf(localStartTime));
            connectionStatement.setObject(2, appt.getPatientID());
            loadAppointmentsFromQuery(appt, fetchedConflictingAppointments, connectionStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fetchedConflictingAppointments;
    }

    private static int findMaxApptID() {
        int maxApptID = 0;
        String findMaxApptIDQuery = "SELECT MAX(apt_id) FROM appointment";

        try {
            Statement connectionStatement = DB_CONNECTION.createStatement();
            ResultSet appointmentResult = connectionStatement.executeQuery(findMaxApptIDQuery);

            if (appointmentResult.next()) {
                maxApptID = appointmentResult.getInt(1);
            }
        } catch (SQLException ignored) {
        }
        return maxApptID + 1;
    }

    public static Appointment addAppointment(Appointment newAppointment) {
        String addAppointmentQuery = String.join(" ", "INSERT INTO appointment (apt_id, " +
                "pt_id, cr_id, apt_type_id, notes, start_datetime, created_at, created_by, updated_at, updated_by, patient_pt_id, " +
                "counselor_c_id, APTtype_APTtype_id)", "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?)");

        int apt_id = findMaxApptID();
        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(addAppointmentQuery);
            connectionStatement.setInt(1, apt_id);
            connectionStatement.setInt(2, newAppointment.getPatientID());
            connectionStatement.setObject(3, newAppointment.getCounselorID());
            connectionStatement.setObject(4, newAppointment.getApptTypeID());
            connectionStatement.setObject(5, newAppointment.getNotes());
            ZonedDateTime start = newAppointment.getStart().withZoneSameInstant(ZoneId.of("UTC"));
            connectionStatement.setTimestamp(6, Timestamp.valueOf(start.toLocalDateTime()));
            connectionStatement.setObject(7, newAppointment.getCreatedBy());
            connectionStatement.setString(8, activeCounselor.getCounselorName());
            connectionStatement.setInt(9, newAppointment.getPatientID());
            connectionStatement.setInt(10, newAppointment.getCounselorID());
            connectionStatement.setInt(11, newAppointment.getApptTypeID());
            connectionStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newAppointment;
    }

    public static void modifyAppointment(Appointment selectedAppointment) {
        String modifyAppointmentQuery = String.join(" ", "UPDATE appointment", "SET pt_id=?, " +
                "cr_id=?, apt_type_id=?, notes=?, start_datetime=?, updated_at=NOW(), " +
                "updated_by=?, patient_pt_id=?", "WHERE apt_id=?");

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(modifyAppointmentQuery);
            connectionStatement.setObject(1, selectedAppointment.getPatientID());
            connectionStatement.setObject(2, selectedAppointment.getCounselorID());
            connectionStatement.setObject(3, selectedAppointment.getApptTypeID());
            connectionStatement.setObject(4, selectedAppointment.getNotes());
            ZonedDateTime start = selectedAppointment.getStart().withZoneSameInstant(ZoneId.of("UTC"));
            connectionStatement.setTimestamp(5, Timestamp.valueOf(start.toLocalDateTime()));
            connectionStatement.setString(6, activeCounselor.getCounselorName());
            connectionStatement.setInt(7, selectedAppointment.getPatientID());
            connectionStatement.setInt(8, selectedAppointment.getAppointmentID());
            connectionStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeAppointment(Appointment selectedAppointment) {
        String removeAppointmentQuery = "DELETE FROM appointment WHERE apt_id = ?";

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(removeAppointmentQuery);
            connectionStatement.setObject(1, selectedAppointment.getAppointmentID());
            connectionStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
