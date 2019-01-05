package sapronov.pavel.managementrestapi.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import sapronov.pavel.managementrestapi.entities.Address;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address, Long> {
}
