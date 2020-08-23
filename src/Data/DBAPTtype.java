package Data;

import Models.AppointmentType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static Data.DBConnection.DB_CONNECTION;

public class DBAPTtype {

    public static ObservableList<AppointmentType> loadAllApptTypes() {
        ObservableList<AppointmentType> allApptTypes = FXCollections.observableArrayList();
        String findAllApptTypesQuery = "SELECT * FROM APTtype";

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(findAllApptTypesQuery);
            ResultSet apptTypesResult = connectionStatement.executeQuery();

            while (apptTypesResult.next()) {
                AppointmentType selectedApptType = new AppointmentType();
                selectedApptType.setAPTtype_id(apptTypesResult.getInt("APTtype_id"));
                selectedApptType.setDescription(apptTypesResult.getString("description"));
                allApptTypes.add(selectedApptType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allApptTypes;
    }

    public static AppointmentType lookupApptType(int APTtype_id) {
        String lookupApptTypeQuery = "SELECT * FROM APTtype WHERE APTtype_id = ?";
        AppointmentType foundApptType = new AppointmentType();

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(lookupApptTypeQuery);
            connectionStatement.setInt(1, APTtype_id);
            ResultSet ApptTypeResult = connectionStatement.executeQuery();

            if (ApptTypeResult.next()) {
                foundApptType.setAPTtype_id(ApptTypeResult.getInt("APTtype_id"));
                foundApptType.setDescription(ApptTypeResult.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundApptType;
    }
}



