package sapronov.pavel.managementrestapi.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "identification", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    Set<Address> addresses;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "identification", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    Set<Communication> communications;

    public enum Gender {
        M, F;
    }

    public Identification(String firstName, String lastName, LocalDate dob,
                          Gender gender, String title,
                          Set<Address> addresses,
                          Set<Communication> communications) {
        this(null, firstName, lastName, dob, gender, title, addresses, communications);
    }
}
