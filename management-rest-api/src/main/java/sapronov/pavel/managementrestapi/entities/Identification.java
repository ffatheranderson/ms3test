package sapronov.pavel.managementrestapi.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sapronov.pavel.managementrestapi.utils.PatchAndPutReady;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Identification implements PatchAndPutReady<Identification> {
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

    @Override
    public Identification patch(Identification that) {
        IdentificationBuilder b = this.toBuilder();
        if (that.firstName != null)
            b.firstName(that.firstName);
        if (that.lastName != null)
            b.lastName(that.lastName);
        if (that.dob != null)
            b.dob(that.dob);
        if (that.gender != null)
            b.gender(that.gender);
        if (that.title != null)
            b.title(that.title);

        return b.build();
    }

    @Override
    public Identification put(Identification that) {
        return this.toBuilder()
                   .firstName(that.firstName)
                   .lastName(that.lastName)
                   .dob(that.dob)
                   .gender(that.gender)
                   .title(that.title)
                   .build();
    }

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
