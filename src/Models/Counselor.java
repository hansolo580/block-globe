package Models;

public class Counselor {
    private Integer c_id;
    private String c_name;
    private String password;
    private String pin;

    public Counselor() {
    }

    /**
     * GETTERS
     */

    public Integer getCounselorID() {
        return c_id;
    }

    public String getCounselorName() {
        return c_name;
    }

    public String getPassword() {
        return password;
    }

    public String getPin() {
        return pin;
    }

    /**
     * SETTERS
     */

    public void setCounselorID(int c_id) {
        this.c_id = c_id;
    }

    public void setCounselorName(String c_name) {
        this.c_name = c_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

}
