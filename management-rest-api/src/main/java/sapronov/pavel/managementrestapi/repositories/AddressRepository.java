package sapronov.pavel.managementrestapi.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import sapronov.pavel.managementrestapi.entities.Address;

public interface AddressRepository extends PagingAndSortingRepository<Address, Long> {
}
