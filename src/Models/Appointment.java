package Models;

import Data.DBAPTtype;
import Data.DBAppointment;
import Exceptions.ExceptionAppt;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.time.*;
import java.util.Calendar;
import java.util.Date;

public class Appointment {
    private IntegerProperty apt_id = new SimpleIntegerProperty();
    private IntegerProperty pt_id = new SimpleIntegerProperty();
    private IntegerProperty cr_id = new SimpleIntegerProperty();
    private IntegerProperty apt_type_id = new SimpleIntegerProperty();
    private StringProperty notes = new SimpleStringProperty();
    private ZonedDateTime start;
    private LocalDateTime createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdateBy;
    private Patient patient = new Patient();
    private String state;

    public Appointment() {
    }

    /**
     * GETTERS
     */

    public final int getAppointmentID() {
        return apt_id.get();
    }

    public final int getPatientID() {
        return pt_id.get();
    }

    public final int getCounselorID() {
        return cr_id.get();
    }

    public final int getApptTypeID() {
        return apt_type_id.get();
    }

    public String getNotes() {
        return notes.get();
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getApptType() {
        return DBAPTtype.lookupApptType(apt_type_id.get()).getDescription();
    }

    public String getState() {
        return state;
    }

    /**
     * SETTERS
     */

    public final void setAppointmentID(int apt_id) {
        this.apt_id.set(apt_id);
    }

    public final void setPatientID(int pt_id) {
        this.pt_id.set(pt_id);
    }

    public final void setCounselorID(int c_id) {
        this.cr_id.set(c_id);
    }

    public final void setApt_type_id(int apt_type_id) {
        this.apt_type_id.set(apt_type_id);
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * PROPERTIES
     */

    public StringProperty notesProperty() {
        return notes;
    }


    /**
     * VALIDATION
     */

    public boolean isValidInput() throws ExceptionAppt {
        if (this.patient == null) {
            throw new ExceptionAppt("A patient must be selected.");
        }
        if (this.notes.get().equals("")) {
            throw new ExceptionAppt("A note must be entered.");
        }
        isValidTime();
        return true;
    }

    public boolean isValidTime() throws ExceptionAppt {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate apptStartDate = this.start.toLocalDate();
        LocalTime apptStartTime = this.start.toLocalTime();
        int weekDay = apptStartDate.getDayOfWeek().getValue();

        if (weekDay == 6 || weekDay == 7) {
            throw new ExceptionAppt("Appointments can only be scheduled for weekdays.");
        }
        if (apptStartTime.isBefore(midnight.plusHours(9))) {
            throw new ExceptionAppt("An appointment can only be scheduled during office hours. (Start time must fall between 9am - 9pm)");
        }
        if (apptStartTime.isAfter(midnight.plusHours(21))) {
            throw new ExceptionAppt("An appointment can only be scheduled during office hours. (Start time must fall between 9am - 9pm)");
        }
        if (apptStartDate.isBefore(LocalDate.now()) || apptStartTime.isBefore(midnight)) {
            throw new ExceptionAppt("An appointment cannot be scheduled for the past.");
        }
        if (isHoliday(vetsDay(apptStartDate.getYear()), apptStartDate)) {
            throw new ExceptionAppt("The office is closed on Veterans Day.");
        }
        if (isHoliday(MLKday(apptStartDate.getYear()), apptStartDate)) {
            throw new ExceptionAppt("The office is closed on MLK Day.");
        }
        if (isHoliday(MemorialDay(apptStartDate.getYear()), apptStartDate)) {
            throw new ExceptionAppt("The office is closed on Memorial Day.");
        }
        if (isHoliday(thanksgivingDay(apptStartDate.getYear()), apptStartDate)) {
            throw new ExceptionAppt("The office is closed on Thanksgiving Day.");
        }
        if (isHoliday(thanksgivingDayAfter(apptStartDate.getYear()), apptStartDate)) {
            throw new ExceptionAppt("The office is closed on the day after Thanksgiving.");
        }
        return true;
    }

    public boolean doesNotConflict() throws ExceptionAppt {
        ObservableList<Appointment> conflictCounselorAppt = DBAppointment.findCounselorConflicts(this.start.toLocalDateTime(), this);
        ObservableList<Appointment> conflictPatientAppt = DBAppointment.findPatientConflicts(this.start.toLocalDateTime(), this);
        int conflictCount = conflictCounselorAppt.size() + conflictPatientAppt.size();
        if (conflictCount > 2) {
            throw new ExceptionAppt("This appointment conflicts with a prior appointment.");
        }
        return true;
    }

    public boolean isHoliday(Calendar cal, LocalDate date) {
        return calToDate(cal).toString().equals(date.toString());
    }

    public LocalDate calToDate(Calendar cal) {
        Date input = cal.getTime();
        return input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    //INVALID DATES
    public static Calendar vetsDay(int year) {
        int month = Calendar.NOVEMBER;

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 11);
        return cal;
    }

    public Calendar MLKday(int year) {
        int switchCTL;
        int month = Calendar.JANUARY;
        Calendar cal;

        cal = Calendar.getInstance();
        cal.set(year, month, 1);
        switchCTL = cal.get(Calendar.DAY_OF_WEEK);

        switch (switchCTL) {
            case 1:
                cal = Calendar.getInstance();
                cal.set(year, month, 16);
                return cal;

            case 2:
                cal = Calendar.getInstance();
                cal.set(year, month, 15);
                return cal;

            case 3:
                cal = Calendar.getInstance();
                cal.set(year, month, 21);
                return cal;

            case 4:
                cal = Calendar.getInstance();
                cal.set(year, month, 20);
                return cal;

            case 5:
                cal = Calendar.getInstance();
                cal.set(year, month, 19);
                return cal;

            case 6:
                cal = Calendar.getInstance();
                cal.set(year, month, 18);
                return cal;

            default:
                cal = Calendar.getInstance();
                cal.set(year, month, 17);
                return cal;

        }
    }

    public static Calendar MemorialDay(int year) {
        int switchCTL;
        int month = Calendar.MAY;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 31);

        switchCTL = cal.get(Calendar.DAY_OF_WEEK);

        switch (switchCTL) {
            case 1:
                cal = Calendar.getInstance();
                cal.set(year, month, 25);
                return cal;
            case 2:
                cal = Calendar.getInstance();
                cal.set(year, month, 31);
                return cal;
            case 3:
                cal = Calendar.getInstance();
                cal.set(year, month, 30);
                return cal;
            case 4:
                cal = Calendar.getInstance();
                cal.set(year, month, 29);
                return cal;
            case 5:
                cal = Calendar.getInstance();
                cal.set(year, month, 28);
                return cal;
            case 6:
                cal = Calendar.getInstance();
                cal.set(year, month, 27);
                return cal;
            default:
                cal = Calendar.getInstance();
                cal.set(year, month, 26);
                return cal;
        }
    }

    public static Calendar thanksgivingDay(int year) {
        int switchCTL;
        int month = Calendar.NOVEMBER;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);

        switchCTL = cal.get(Calendar.DAY_OF_WEEK);
        switch (switchCTL) {

            case 1:
                cal = Calendar.getInstance();
                cal.set(year, month, 26);
                return cal;
            case 2:
                cal = Calendar.getInstance();
                cal.set(year, month, 25);
                return cal;
            case 3:
                cal = Calendar.getInstance();
                cal.set(year, month, 24);
                return cal;
            case 4:
                cal = Calendar.getInstance();
                cal.set(year, month, 23);
                return cal;
            case 5:
                cal = Calendar.getInstance();
                cal.set(year, month, 22);
                return cal;
            case 6:
                cal = Calendar.getInstance();
                cal.set(year, month, 28);
                return cal;
            default:
                cal = Calendar.getInstance();
                cal.set(year, month, 27);
                return cal;
        }
    }

    public static Calendar thanksgivingDayAfter(int year) {
        int switchCTL;
        int month = Calendar.NOVEMBER;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);

        switchCTL = cal.get(Calendar.DAY_OF_WEEK);
        switch (switchCTL) {

            case 1:
                cal = Calendar.getInstance();
                cal.set(year, month, 27);
                return cal;
            case 2:
                cal = Calendar.getInstance();
                cal.set(year, month, 26);
                return cal;
            case 3:
                cal = Calendar.getInstance();
                cal.set(year, month, 25);
                return cal;
            case 4:
                cal = Calendar.getInstance();
                cal.set(year, month, 24);
                return cal;
            case 5:
                cal = Calendar.getInstance();
                cal.set(year, month, 23);
                return cal;
            case 6:
                cal = Calendar.getInstance();
                cal.set(year, month, 29);
                return cal;
            default:
                cal = Calendar.getInstance();
                cal.set(year, month, 28);
                return cal;
        }
    }
}
