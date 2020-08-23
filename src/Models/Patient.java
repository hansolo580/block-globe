package Models;

import Exceptions.ExceptionPatient;
import javafx.beans.property.*;
import java.sql.Timestamp;

public class Patient {
    private IntegerProperty pt_id = new SimpleIntegerProperty();
    private StringProperty pt_name = new SimpleStringProperty();
    private IntegerProperty address_id = new SimpleIntegerProperty();
    private String INS_PR;
    private Timestamp lastUpdate;
    private String lastUpdateBy;

    public Patient() {
    }

    /**
     * GETTERS
     */

    public int getPatientID() {
        return pt_id.get();
    }

    public String getPatientName() {
        return pt_name.get();
    }

    public int getAddressID() {
        return address_id.get();
    }

    public String getInsurance() {
        return INS_PR;
    }

    /**
     * SETTERS
     */

    public void setPatientID(int pt_id) {
        this.pt_id.set(pt_id);
    }

    public void setPatientName(String pt_name) {
        this.pt_name.set(pt_name);
    }

    public void setAddressID(int address_id) {
        this.address_id.set(address_id);
    }

    public void setInsurance(String INS_PR) {
        this.INS_PR = INS_PR;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    /**
     * PROPERTIES
     */

    public StringProperty pt_nameProperty() {
        return pt_name;
    }

    /**
     * VALIDATION
     */

    public static boolean isValid(Patient newPatient, String state, String phone, String insurance) throws ExceptionPatient {
        if (newPatient.getPatientName().equals("")) {
            throw new ExceptionPatient("Patient is required.");
        }
        if (state.equals("")) {
            throw new ExceptionPatient("State is required.");
        }
        if (phone.equals("")) {
            throw new ExceptionPatient("Phone number is required.");
        }
        if (insurance.equals("")) {
            throw new ExceptionPatient("Insurance is required.");
        }
        return true;
    }
}
