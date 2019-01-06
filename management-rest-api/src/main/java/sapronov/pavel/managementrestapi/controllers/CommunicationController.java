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
import sapronov.pavel.managementrestapi.entities.Communication;
import sapronov.pavel.managementrestapi.entities.Identification;
import sapronov.pavel.managementrestapi.repositories.CommunicationRepository;
import sapronov.pavel.managementrestapi.repositories.IdentificationRepository;
import sapronov.pavel.managementrestapi.resource_assemblers.CommunicationAssembler;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static sapronov.pavel.managementrestapi.controllers.IdentificationController.IDENTIFICATION_HAS_NOT_FOUND;

@RestController
@RequestMapping("identifications/{identId}/communications")
public class CommunicationController {

    @Autowired
    private IdentificationRepository identRepo;
    @Autowired
    private CommunicationRepository commRepo;
    @Autowired
    private CommunicationAssembler commAsm;

    private static final String COMMUNICATION_HAS_NOT_FOUND = "Communication with id: %s has not found.";


    @GetMapping(produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Resources<Resource<Communication>>> getCommunications(@PathVariable Long identId) {

        Set<Resource<Communication>> communications =
                identRepo.findById(identId).map(Identification::getCommunications)
                         .orElse(Collections.emptySet()).stream()
                         .map(commAsm::toResource).collect(Collectors.toSet());

        if (!communications.isEmpty())
            return ResponseEntity.ok(new Resources<>(communications,
                    linkTo(methodOn(CommunicationController.class).getCommunications(identId)).withSelfRel()));
        else return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{commId}", produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Resource<Communication>> getCommunication(@PathVariable Long identId,
                                                                    @PathVariable Long commId) {

        Optional<Resource<Communication>> communication =
                identRepo.findById(identId).map(Identification::getCommunications).orElse(Collections.emptySet())
                         .stream()
                         .filter(c -> c.getId() == commId).findAny().map(commAsm::toResource);

        return ResponseEntity.of(communication);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity createNewAddress(@PathVariable Long identId, @RequestBody Communication newCommunication) {

        Optional<Identification> identificationOpt = identRepo.findById(identId);
        if (identificationOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(String.format(IDENTIFICATION_HAS_NOT_FOUND, identId));
        else {
            Identification identification = identificationOpt.get();
            newCommunication.setIdentification(identification);
            Communication createdCommunication = commRepo.save(newCommunication);

            return ResponseEntity
                    .created(linkTo(methodOn(CommunicationController.class).getCommunication(identId
                            , createdCommunication.getId())).toUri()).build();
        }
    }

    @RequestMapping(value = "/{commId}"
            , method = {RequestMethod.PUT, RequestMethod.PATCH}
            , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}
            , produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity updateCommunication(@PathVariable Long identId
            , @PathVariable Long commId
            , @RequestBody Communication updatedCommunication
            , HttpServletRequest request) {

        Optional<Identification> identOpt = identRepo.findById(identId);

        if (identOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_HTML)
                                 .body(String.format(IDENTIFICATION_HAS_NOT_FOUND, identId));
        else {
            Optional<Communication> commOpt =
                    identOpt.get().getCommunications().stream().filter(c -> c.getId().equals(commId)).findAny();

            if (commOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_HTML)
                                     .body(String.format(COMMUNICATION_HAS_NOT_FOUND, commId));
            else {
                Communication communication = commOpt.get();

                Communication newCommunicationClone =
                        communication.patchOrPut(updatedCommunication, HttpMethod.resolve(request.getMethod()));

                Communication savedCommunication = commRepo.save(newCommunicationClone);
                return ResponseEntity.status(HttpStatus.OK).header("Location",
                        linkTo(methodOn(CommunicationController.class)
                                .getCommunication(identId, savedCommunication.getId())).toUri().toASCIIString())
                                     .build();
            }
        }
    }

    @DeleteMapping(value = "/{commId}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity deleteCommunication(@PathVariable Long identId, @PathVariable Long commId) {

        Optional<Identification> identOpt = identRepo.findById(identId);

        if (identOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .contentType(MediaType.TEXT_HTML)
                                 .body(String.format(IDENTIFICATION_HAS_NOT_FOUND, identId));
        else {
            Identification identification = identOpt.get();
            Optional<Communication> commForRemove =
                    identification.getCommunications().stream().filter(c -> c.getId().equals(commId)).findAny();

            if (commForRemove.isPresent()) {
                Identification clone =
                        identification.toBuilder()
                                      .communications(new HashSet<>(identification.getCommunications())).build();
                clone.getCommunications().remove(commForRemove.get());
                identRepo.save(clone);
                return ResponseEntity.ok().build();
            } else
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .contentType(MediaType.TEXT_HTML)
                                     .body(String.format(COMMUNICATION_HAS_NOT_FOUND, commId));

        }
    }


}
