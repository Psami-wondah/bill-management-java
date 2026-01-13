package backend.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import backend.repositories.CustomerRepository;

public class Customer implements BaseModel {

    public static final CustomerRepository objects = new CustomerRepository();

    private final String id;
    private String phoneNumber;
    private String name;
    private String email;
    private String address;

    @JsonCreator
    public Customer(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("email") String email, @JsonProperty("phoneNumber") String phoneNumber,
            @JsonProperty("address") String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Customer(String name, String email, String phoneNumber, String address) {
        this.id = objects.generateId();
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
        objects.add(this);
    }
}
