package sapronov.pavel.managementrestapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
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

    @JsonIgnore
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Identification identification;

    public Address(String type, Integer number, String street, String unit, String city, String state,
                   String zipCode, Identification identification) {
        this(null, type, number, street, unit, city, state, zipCode, identification);
    }

    public Address(Long id, String type, Integer number, String street, String unit, String city, String state,
                   String zipCode) {
        this(id, type, number, street, unit, city, state, zipCode, null);
    }

    public Address(String type, Integer number, String street, String unit, String city, String state,
                   String zipCode) {
        this(null, type, number, street, unit, city, state, zipCode, null);
    }

    public void setAllPrimitiveFieldsFrom(Address that) {
        this.type = that.type;
        this.number = that.number;
        this.street = that.street;
        this.unit = that.unit;
        this.city = that.city;
        this.state = that.state;
        this.zipCode = that.zipCode;
    }

    public void setAllPrimitiveNotNullFieldsFrom(Address that) {
        if (that.type != null)
            this.type = that.type;
        if (that.number != null)
            this.number = that.number;
        if (that.street != null)
            this.street = that.street;
        if (that.unit != null)
            this.unit = that.unit;
        if (that.city != null)
            this.city = that.city;
        if (that.state != null)
            this.state = that.state;
        if (that.zipCode != null)
            this.zipCode = that.zipCode;
    }

}
