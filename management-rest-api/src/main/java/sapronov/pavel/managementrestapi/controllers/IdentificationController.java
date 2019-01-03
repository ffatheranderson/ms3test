package sapronov.pavel.managementrestapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sapronov.pavel.managementrestapi.daos.IdentificationDao;
import sapronov.pavel.managementrestapi.entities.Identification;
import sapronov.pavel.managementrestapi.resource_assemblers.IdentificationResourceAssembler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/identifications")
public class IdentificationController {

    @Autowired
    private IdentificationDao idenDao;
    @Autowired
    private IdentificationResourceAssembler idenAsm;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public Resources<Resource<Identification>> getAll() {
        List<Resource<Identification>> identifications =
                idenDao.getAll().stream().map(idenAsm::toResource).collect(Collectors.toList());
        return new Resources<>(identifications,
                linkTo(methodOn(IdentificationController.class).getAll()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> getIdentification(@PathVariable Long id) {
        Optional<Resource<Identification>> identification =
                idenDao.getById(id).map(idenAsm::toResource);

        return ResponseEntity.of(identification);
    }


}
