package sapronov.pavel.managementrestapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import sapronov.pavel.managementrestapi.entities.Address;
import sapronov.pavel.managementrestapi.entities.Communication;
import sapronov.pavel.managementrestapi.entities.Identification;

import java.time.LocalDate;
import java.util.Set;

import static java.time.Month.FEBRUARY;
import static java.util.Calendar.APRIL;
import static sapronov.pavel.managementrestapi.entities.Identification.Gender.M;

public class ControllersFixture {

    static final Address bobMarleysAddress1 = new Address(1L, "Home", 1, "Some street",
            "1a", "Boston", "CI", "123");

    static final Address bobMarleysAddress2 = new Address(2L, "Work", 2, "Some street for work",
            "32f", "Boston", "CI", "12223");
    static final Communication bobMarleyComm1 = new Communication(3L, "Cell phone", "123", true);

    static final Communication bobMarleyComm2 = new Communication(4L, "email", "bobmarley@haven.org", false);

    static final Identification bobMarley = new Identification(5L, "Bob", "Marley",
            LocalDate.of(1945, FEBRUARY, 6),
            M, "Singer",
            Set.of(bobMarleysAddress1, bobMarleysAddress2),
            Set.of(bobMarleyComm1, bobMarleyComm2));

    static final Address jackieChansAddress1 = new Address(6L, "Home", 2, "Jackie's Some street",
            "33g", "Malvern", "PA", "444");
    static final Address jackieChansAddress2 = new Address(7L, "Work", 33, "Jackie's Some street for work",
            "WorkWork2", "Malvern", "PA", "12211");
    static final Communication jackieChansComm1 = new Communication(8L, "Cell phone", "345", false);
    static final Communication jackieChansComm2 = new Communication(9L, "email", "jackiechan@china.gov", true);

    static final Identification jackieChan = new Identification(10L, "Jackie", "Chan",
            LocalDate.of(1954, APRIL, 7),
            M, "Actor",
            Set.of(jackieChansAddress1, jackieChansAddress2),
            Set.of(jackieChansComm1, jackieChansComm2));

    static {
        bobMarleysAddress1.setIdentification(bobMarley);
        bobMarleysAddress2.setIdentification(bobMarley);
        jackieChansAddress1.setIdentification(jackieChan);
        jackieChansAddress2.setIdentification(jackieChan);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
