package sapronov.pavel.managementrestapi.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Identification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    String firstName;
    String lastName;
    LocalDate dob;
    Gender gender;
    String title;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    Set<Address> addresses;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    Set<Communication> communications;

    public enum Gender {
        M, F;
    }

    public Identification(String firstName, String lastName, LocalDate dob,
                          Gender gender, String title,
                          Set<Address> addresses,
                          Set<Communication> communications) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.title = title;
        this.addresses = addresses;
        this.communications = communications;
    }
}
