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
@NoArgsConstructor
@AllArgsConstructor
public class Communication {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    String type;
    String value;
    Boolean preferred;

    public Communication(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Communication(String type, String value, Boolean preferred) {
        this.type = type;
        this.value = value;
        this.preferred = preferred;
    }
}
