package sapronov.pavel.managementrestapi.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import sapronov.pavel.managementrestapi.entities.Identification;

@Repository
public interface IdentificationRepository extends PagingAndSortingRepository<Identification, Long> {
}
