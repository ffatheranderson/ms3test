package sapronov.pavel.managementrestapi.resource_assemblers;

import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.ResourceAssembler;
import sapronov.pavel.managementrestapi.controllers.IdentificationController;
import sapronov.pavel.managementrestapi.entities.Identification;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class IdentificationResourceAssembler implements ResourceAssembler<Identification, Resource<Identification>> {

    @Override
    public Resource<Identification> toResource(Identification entity) {
        return new Resource<>(entity,
                linkTo(methodOn(IdentificationController.class).getIdentification(entity.getId())).withSelfRel(),
                linkTo(methodOn(IdentificationController.class).getAll()).withRel("identifications"));
    }
}
