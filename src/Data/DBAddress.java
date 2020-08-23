package Data;

import Models.Address;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static Controllers.Login.activeCounselor;
import static Data.DBConnection.DB_CONNECTION;

public class DBAddress {

    public static Address lookupAddress(int address_id) {
        String lookupAddressQuery = "SELECT * FROM address WHERE address_id = ?";
        Address foundAddress = new Address();

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(lookupAddressQuery);
            connectionStatement.setInt(1, address_id);
            ResultSet addressResult = connectionStatement.executeQuery();

            if (addressResult.next()) {
                foundAddress.setAddressID(addressResult.getInt("address_id"));
                foundAddress.setAddress(addressResult.getString("addressline_1"));
                foundAddress.setAddress2(addressResult.getString("addressline_2"));
                foundAddress.setState(addressResult.getString("state"));
                foundAddress.setCity(addressResult.getString("city"));
                foundAddress.setZipCode(addressResult.getString("postal_code"));
                foundAddress.setPhone(addressResult.getString("phone"));
            }
        } catch (SQLException ignored) {
        }
        return foundAddress;
    }

    private static int getMaxAddressID() {
        int maxAddressID = 0;
        String maxAddressIDQuery = "SELECT MAX(address_id) FROM address";

        try {
            Statement connectionStatement = DB_CONNECTION.createStatement();
            ResultSet addressResult = connectionStatement.executeQuery(maxAddressIDQuery);

            if (addressResult.next()) {
                maxAddressID = addressResult.getInt(1);
            }
        } catch (SQLException ignored) {
        }
        return maxAddressID + 1;
    }

    public static int newAddress(Address address) {
        String newAddressQuery = String.join(" ", "INSERT INTO address (address_id, addressline_1, " +
                        "addressline_2, city, state, postal_code, phone, created_at, created_by, updated_at, updated_by)",
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)");

        int address_id = getMaxAddressID();

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(newAddressQuery);
            connectionStatement.setInt(1, address_id);
            connectionStatement.setString(2, "Not required");
            connectionStatement.setString(3, "not required");
            connectionStatement.setString(4, "not required");
            connectionStatement.setString(5, address.getState());
            connectionStatement.setString(6, "not required");
            connectionStatement.setString(7, address.getPhone());
            connectionStatement.setString(8, activeCounselor.getCounselorName());
            connectionStatement.setString(9, activeCounselor.getCounselorName());
            connectionStatement.executeUpdate();
        } catch (SQLException ignored) {
        }
        return address_id;
    }

    public static void modifyAddress(Address address) {
        String modifyAddressQuery = String.join(" ", "UPDATE address", "SET addressline_1=?, addressline_2=?, " +
                "city=?, state=?, postal_code=?, phone=?, updated_at=NOW(), updated_by=?", "WHERE address_id=?");

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(modifyAddressQuery);
            connectionStatement.setString(1, "Not required");
            connectionStatement.setString(2, "Not required");
            connectionStatement.setString(3, "Not required");
            connectionStatement.setString(4, address.getState());
            connectionStatement.setString(5, "Not required");
            connectionStatement.setString(6, address.getPhone());
            connectionStatement.setString(7, activeCounselor.getCounselorName());
            connectionStatement.setInt(8, address.getAddressID());
            connectionStatement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    public void removeAddress(Address address) {
        String removeAddressQuery = "DELETE FROM address WHERE address_id = ?";

        try {
            PreparedStatement connectionStatement = DB_CONNECTION.prepareStatement(removeAddressQuery);
            connectionStatement.setInt(1, address.getAddressID());
            connectionStatement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
