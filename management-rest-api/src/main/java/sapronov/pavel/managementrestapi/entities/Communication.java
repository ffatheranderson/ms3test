package sapronov.pavel.managementrestapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Communication {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    String type;
    String value;
    Boolean preferred;

    @JsonIgnore
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Identification identification;


    public Communication(Long id, String type, String value, Boolean preferred) {
        this(id, type, value, preferred, null);
    }

    public Communication(String type, String value, Boolean preferred) {
        this(null, type, value, preferred, null);
    }
}
