package sapronov.pavel.managementrestapi.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import sapronov.pavel.managementrestapi.entities.Communication;

@Repository
public interface CommunicationRepository extends PagingAndSortingRepository<Communication, Long> {
}
