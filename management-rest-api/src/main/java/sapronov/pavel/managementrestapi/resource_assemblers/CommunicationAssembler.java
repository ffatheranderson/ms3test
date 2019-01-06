package sapronov.pavel.managementrestapi.resource_assemblers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import sapronov.pavel.managementrestapi.controllers.AddressController;
import sapronov.pavel.managementrestapi.controllers.CommunicationController;
import sapronov.pavel.managementrestapi.controllers.IdentificationController;
import sapronov.pavel.managementrestapi.entities.Address;
import sapronov.pavel.managementrestapi.entities.Communication;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class CommunicationAssembler implements ResourceAssembler<Communication, Resource<Communication>> {
    @Override
    public Resource<Communication> toResource(Communication entity) {
        return new Resource<>(entity,
                linkTo(methodOn(CommunicationController.class)
                        .getCommunication(entity.getIdentification().getId(), entity.getId())).withSelfRel(),
                linkTo(methodOn(IdentificationController.class).getIdentification(entity.getIdentification().getId()))
                        .withRel("identification"),
                linkTo(methodOn(CommunicationController.class).getCommunications(entity.getIdentification().getId()))
                        .withRel("communications"));
    }
}
