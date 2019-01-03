package sapronov.pavel.managementrestapi.utils;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sapronov.pavel.managementrestapi.entities.Address;
import sapronov.pavel.managementrestapi.entities.Communication;
import sapronov.pavel.managementrestapi.entities.Identification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Set;

import static java.time.Month.FEBRUARY;
import static java.util.Calendar.APRIL;
import static sapronov.pavel.managementrestapi.entities.Identification.Gender.M;

@Repository
public class DataBaseInitializer {

    @PersistenceContext
    private EntityManager em;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(DataBaseInitializer.class).initDb();
    }

    @Transactional
    public void initDb() {
        Address bobMarleysAddress1 = new Address("Home", 1, "Some street",
                "1a", "Boston", "CI", "123");
        Address bobMarleysAddress2 = new Address("Work", 2, "Some street for work",
                "32f", "Boston", "CI", "12223");
        Communication bobMarleyComm1 = new Communication("Cell phone", "123", true);
        Communication bobMarleyComm2 = new Communication("email", "bobmarley@haven.org", false);


        em.merge(new Identification("Bob", "Marley",
                LocalDate.of(1945, FEBRUARY, 6),
                M, "Singer",
                Set.of(bobMarleysAddress1, bobMarleysAddress2),
                Set.of(bobMarleyComm1, bobMarleyComm2)));


        Address jackieChansAddress1 = new Address("Home", 2, "Jackie's Some street",
                "33g", "Malvern", "PA", "444");
        Address jackieChansAddress2 = new Address("Work", 33, "Jackie's Some street for work",
                "WorkWork2", "Malvern", "PA", "12211");
        Communication jackieChansComm1 = new Communication("Cell phone", "345", false);
        Communication jackieChansComm2 = new Communication("email", "jackiechan@china.gov", true);

        em.merge(new Identification("Jackie", "Chan",
                LocalDate.of(1954, APRIL, 7),
                M, "Actor",
                Set.of(jackieChansAddress1, jackieChansAddress2),
                Set.of(jackieChansComm1, jackieChansComm2)));
    }

}
