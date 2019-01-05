package sapronov.pavel.managementrestapi.resource_assemblers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import sapronov.pavel.managementrestapi.controllers.AddressController;
import sapronov.pavel.managementrestapi.controllers.IdentificationController;
import sapronov.pavel.managementrestapi.entities.Identification;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class IdentificationAssembler implements ResourceAssembler<Identification, Resource<Identification>> {
    @Override
    public Resource<Identification> toResource(Identification entity) {
        return new Resource<>(entity,
                linkTo(methodOn(IdentificationController.class).getIdentification(entity.getId())).withSelfRel(),
                linkTo(methodOn(AddressController.class).getAddresses(entity.getId())).withRel("addresses"),
                linkTo(methodOn(IdentificationController.class).getIdentifications()).withRel("identifications"));
    }
}
