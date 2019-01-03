package sapronov.pavel.managementrestapi.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import sapronov.pavel.managementrestapi.entities.Identification;

public interface IdentificationRepository extends PagingAndSortingRepository<Identification, Long> {
}
