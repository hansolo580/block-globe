package Data;

import Models.Counselor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Data.DBConnection.DB_CONNECTION;

public class DBCounselor {


    public DBCounselor() {
    }

    public static ObservableList<Counselor> loadAllCounselors() {
        ObservableList<Counselor> currentCounselors = FXCollections.observableArrayList();
        String loadAllCounselorsQuery = "SELECT * FROM counselor";

        try {
            PreparedStatement preparedStatement = DB_CONNECTION.prepareStatement(loadAllCounselorsQuery);
            ResultSet counselorResult = preparedStatement.executeQuery();

            while (counselorResult.next()) {
                Counselor currentCounselor = new Counselor();
                currentCounselor.setCounselorID(counselorResult.getInt("c_id"));
                currentCounselor.setCounselorName(counselorResult.getString("c_name"));
                currentCounselor.setPassword(counselorResult.getString("c_password"));
                currentCounselor.setPin(counselorResult.getString("c_pin"));
                currentCounselors.add(currentCounselor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentCounselors;
    }

}
