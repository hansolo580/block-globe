package Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class AppointmentType {
    private IntegerProperty APTtype_id = new SimpleIntegerProperty();
    private String description;

    public AppointmentType() {

    }

    /**
     * GETTERS
     */

    public final int getAPTtype_id() {
        return APTtype_id.get();
    }

    public String getDescription() {
        return description;
    }

    /**
     * SETTERS
     */

    public final void setAPTtype_id(int APTtype_id) {
        this.APTtype_id.set(APTtype_id);
    }

    public void setDescription(String description) {
        this.description = description;
    }


}


