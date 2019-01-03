package sapronov.pavel.managementrestapi.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import sapronov.pavel.managementrestapi.entities.Communication;

public interface CommunicationRepository extends PagingAndSortingRepository<Communication, Long> {
}
