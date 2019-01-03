package sapronov.pavel.managementrestapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    String type;
    Integer number;
    String street;
    String unit;
    String city;
    String state;
    String zipCode;

    public Address(String type, Integer number, String street, String unit, String city, String state,
                   String zipCode) {
        this.type = type;
        this.number = number;
        this.street = street;
        this.unit = unit;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
