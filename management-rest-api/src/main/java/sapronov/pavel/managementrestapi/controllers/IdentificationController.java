package sapronov.pavel.managementrestapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sapronov.pavel.managementrestapi.entities.Identification;
import sapronov.pavel.managementrestapi.repositories.IdentificationRepository;
import sapronov.pavel.managementrestapi.resource_assemblers.IdentificationAssembler;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("identifications")
public class IdentificationController {


    @Autowired
    private IdentificationRepository identRepo;

    @Autowired
    private IdentificationAssembler identAsm;

    public static final String IDENTIFICATION_HAS_NOT_FOUND = "Identification with id: %s has not found.";

    @GetMapping(produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Resources<Resource<Identification>>> getIdentifications() {

        Set<Resource<Identification>> identifications =
                StreamSupport.stream(identRepo.findAll().spliterator(), false).map(identAsm::toResource)
                             .collect(Collectors.toSet());

        if (!identifications.isEmpty())
            return ResponseEntity.ok(new Resources<>(identifications,
                    linkTo(methodOn(IdentificationController.class).getIdentifications()).withSelfRel()));
        else return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{identId}", produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Resource<Identification>> getIdentification(@PathVariable Long identId) {
        Optional<Resource<Identification>> identification = identRepo.findById(identId).map(identAsm::toResource);

        return ResponseEntity.of(identification);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity createNewIdentification(@RequestBody Identification newIdentification) {

        Identification createdIdentification = identRepo.save(newIdentification);

        return ResponseEntity
                .created(linkTo(methodOn(IdentificationController.class)
                        .getIdentification(createdIdentification.getId())).toUri()).build();
    }


    @RequestMapping(value = "/{identId}"
            , method = {RequestMethod.PUT, RequestMethod.PATCH}
            , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}
            , produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity updateIdentification(@PathVariable Long identId
            , @RequestBody Identification updatedIdentification
            , HttpServletRequest request) {

        Optional<Identification> identOpt = identRepo.findById(identId);

        if (identOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_HTML)
                                 .body(String.format(IDENTIFICATION_HAS_NOT_FOUND, identId));
        else {
            Identification existedIdent = identOpt.get();

            Identification newIdentificationClone =
                    existedIdent.patchOrPut(updatedIdentification, HttpMethod.resolve(request.getMethod()));

            Identification savedIdentification = identRepo.save(newIdentificationClone);

            return ResponseEntity.status(HttpStatus.OK).header("Location",
                    linkTo(methodOn(IdentificationController.class)
                            .getIdentification(savedIdentification.getId())).toUri().toASCIIString()).build();

        }
    }

    @DeleteMapping(value = "/{identId}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity deleteIdentification(@PathVariable Long identId) {
        Optional<Identification> identOpt = identRepo.findById(identId);

        if (identOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .contentType(MediaType.TEXT_HTML)
                                 .body(String.format(IDENTIFICATION_HAS_NOT_FOUND, identId));
        else {
            Identification identification = identOpt.get();
            identRepo.delete(identification);
            return ResponseEntity.ok().build();
        }
    }

}
