package sapronov.pavel.managementrestapi.resource_assemblers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import sapronov.pavel.managementrestapi.controllers.AddressController;
import sapronov.pavel.managementrestapi.controllers.IdentificationController;
import sapronov.pavel.managementrestapi.entities.Address;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class AddressAssembler implements ResourceAssembler<Address, Resource<Address>> {

    @Override
    public Resource<Address> toResource(Address address) {

        return new Resource<>(address,
                linkTo(methodOn(AddressController.class)
                        .getAddress(address.getIdentification().getId(), address.getId())).withSelfRel(),
                linkTo(methodOn(IdentificationController.class).getIdentification(address.getIdentification().getId()))
                        .withRel("identification"),
                linkTo(methodOn(AddressController.class).getAddresses(address.getIdentification().getId()))
                        .withRel("addresses"));
    }
}