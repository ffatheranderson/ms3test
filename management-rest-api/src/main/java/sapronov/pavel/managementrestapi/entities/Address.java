package sapronov.pavel.managementrestapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sapronov.pavel.managementrestapi.utils.PatchAndPutReady;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Address implements PatchAndPutReady<Address> {
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

    @Override
    public Address put(Address that) {
        return this.toBuilder()
                   .type(that.type)
                   .number(that.number)
                   .street(that.street)
                   .unit(that.unit)
                   .city(that.city)
                   .state(that.state)
                   .zipCode(that.zipCode)
                   .build();
    }

    @Override
    public Address patch(Address that) {
        AddressBuilder b = this.toBuilder();
        if (that.type != null)
            b.type(that.type);
        if (that.number != null)
            b.number(that.number);
        if (that.street != null)
            b.street(that.street);
        if (that.unit != null)
            b.unit(that.unit);
        if (that.city != null)
            b.city(that.city);
        if (that.state != null)
            b.state(that.state);
        if (that.zipCode != null)
            b.zipCode(that.zipCode);
        return b.build();
    }

}
