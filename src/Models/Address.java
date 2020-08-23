package Models;

public class Address {
    private int address_id;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String phone;


    public Address() {
    }

    /**
     * GETTERS
     */

    public int getAddressID() {
        return address_id;
    }

    public String getPhone() {
        return phone;
    }

    public String getState() {
        return state;
    }

    /**
     * SETTERS
     */

    public void setAddressID(int address_id) {
        this.address_id = address_id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setState(String state) {
        this.state = state;
    }

}
