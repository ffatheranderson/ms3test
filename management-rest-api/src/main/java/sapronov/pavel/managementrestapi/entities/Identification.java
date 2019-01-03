package sapronov.pavel.managementrestapi.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
//@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Identification {
    //    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    String firstName;
    String lastName;
    LocalDate dob;
    Gender gender;
    String title;

    @JsonIgnore
    @OneToMany
    Set<Address> addresses;
    @JsonIgnore
    @OneToMany
    Set<Communication> communications;

    public enum Gender {
        M, F;
    }
}
