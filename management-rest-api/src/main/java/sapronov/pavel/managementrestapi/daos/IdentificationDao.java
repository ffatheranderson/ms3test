package sapronov.pavel.managementrestapi.daos;

import org.springframework.stereotype.Repository;
import sapronov.pavel.managementrestapi.entities.Address;
import sapronov.pavel.managementrestapi.entities.Communication;
import sapronov.pavel.managementrestapi.entities.Identification;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.Month.FEBRUARY;
import static java.util.Calendar.APRIL;
import static sapronov.pavel.managementrestapi.entities.Identification.Gender.M;

@Repository
public class IdentificationDao {

    public static final AtomicLong idProvider = new AtomicLong(0);

    public static long getId() {
        return idProvider.incrementAndGet();
    }

    private static final Set<Identification> REPO;

    static {
        REPO = new HashSet<>();

        Address bobMarleysAddress1 = new Address(getId(), "Home", 1, "Some street",
                "1a", "Boston", "CI", "123");
        Address bobMarleysAddress2 = new Address(getId(), "Work", 2, "Some street for work",
                "32f", "Boston", "CI", "12223");
        Communication bobMarleyComm1 = new Communication(getId(), "Cell phone", "123", true);
        Communication bobMarleyComm2 = new Communication(getId(), "email", "bobmarley@haven.org", false);

        REPO
                .add(new Identification(getId(), "Bob", "Marley",
                        LocalDate.of(1945, FEBRUARY, 6),
                        M, "Singer",
                        Set.of(bobMarleysAddress1, bobMarleysAddress2),
                        Set.of(bobMarleyComm1, bobMarleyComm2)));

        Address jackieChansAddress1 = new Address(getId(), "Home", 2, "Jackie's Some street",
                "33g", "Malvern", "PA", "444");
        Address jackieChansAddress2 = new Address(getId(), "Work", 33, "Jackie's Some street for work",
                "WorkWork2", "Malvern", "PA", "12211");
        Communication jackieChansComm1 = new Communication(getId(), "Cell phone", "345", false);
        Communication jackieChansComm2 = new Communication(getId(), "email", "jackiechan@china.gov", true);

        REPO
                .add(new Identification(getId(), "Jackie", "Chan",
                        LocalDate.of(1954, APRIL, 7),
                        M, "Actor",
                        Set.of(jackieChansAddress1, jackieChansAddress2),
                        Set.of(jackieChansComm1, jackieChansComm2)));

    }

    public Set<Identification> getAll() {
        return REPO;
    }

    public Optional<Identification> getById(Long id) {
        return REPO.stream().filter(i -> i.getId().equals(id)).findFirst();
    }

}
