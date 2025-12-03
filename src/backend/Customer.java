package backend;

import java.io.Serializable;

public class Customer implements Serializable {

    public static final CustomerRepository OBJECTS = new CustomerRepository();

    private final String id;
    private String phoneNumber;
    private String name;
    private String email;
    private String address;

    protected String generateCode() {
        return String.format("%06d", new java.util.Random().nextInt(1_000_000));
    }

    public Customer(String name, String email, String phoneNumber, String address) {
        this.id = generateCode();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void save() {
        OBJECTS.add(this);
    }
}
