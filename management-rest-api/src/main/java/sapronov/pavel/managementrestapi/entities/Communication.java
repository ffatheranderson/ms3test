package sapronov.pavel.managementrestapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sapronov.pavel.managementrestapi.utils.PatchAndPutReady;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Communication implements PatchAndPutReady<Communication> {
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

    @Override
    public Communication patch(Communication that) {
        CommunicationBuilder b = this.toBuilder();
        if (that.type != null)
            b.type(that.type);
        if (that.value != null)
            b.value(that.value);
        if (that.preferred != null)
            b.preferred(that.preferred);
        return b.build();
    }

    @Override
    public Communication put(Communication that) {
        return this.toBuilder()
                   .type(that.type)
                   .value(that.value)
                   .preferred(that.preferred)
                   .build();
    }
}
