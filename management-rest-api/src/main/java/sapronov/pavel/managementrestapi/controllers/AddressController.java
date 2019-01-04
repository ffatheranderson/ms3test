package sapronov.pavel.managementrestapi.controllers;

import jdk.jshell.spi.ExecutionControl;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.coyote.Response;
import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sapronov.pavel.managementrestapi.entities.Address;
import sapronov.pavel.managementrestapi.entities.Identification;
import sapronov.pavel.managementrestapi.repositories.AddressRepository;
import sapronov.pavel.managementrestapi.repositories.IdentificationRepository;
import sapronov.pavel.managementrestapi.resource_assemblers.AddressAssembler;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("identifications/{identId}/addresses")
public class AddressController {

    @Autowired
    private IdentificationRepository identRepo;
    @Autowired
    private AddressRepository addrRepo;
    @Autowired
    private AddressAssembler addressAsm;

    private static final String IDENTIFICATION_HAS_NOT_FOUND = "Identification with id: %s has not found.";
    private static final String ADDRESS_HAS_NOT_FOUND = "Address with id: %s has not found.";

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Resources<Resource<Address>>> getAddresses(@PathVariable Long identId) {
        Set<Resource<Address>> addresses =
                identRepo.findById(identId).map(Identification::getAddresses).orElse(Collections.emptySet()).stream()
                         .map(addressAsm::toResource).collect(Collectors.toSet());


        if (!addresses.isEmpty())
            return ResponseEntity.ok(new Resources<>(addresses,
                    linkTo(methodOn(AddressController.class).getAddresses(identId)).withSelfRel()));
        else return ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addAddressToIdentification(@PathVariable Long identId, @RequestBody Address newAddress) {

        Optional<Identification> identificationOpt = identRepo.findById(identId);
        if (identificationOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(String.format(IDENTIFICATION_HAS_NOT_FOUND, identId));
        else {
            Identification identification = identificationOpt.get();
            newAddress.setIdentification(identification);
            Address createdAddress = addrRepo.save(newAddress);

            return ResponseEntity
                    .created(linkTo(methodOn(AddressController.class).getAddress(identId, createdAddress.getId()))
                            .toUri())
                    .build();
        }
    }

    @GetMapping(value = "/{addrId}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Resource<Address>> getAddress(@PathVariable Long identId, @PathVariable Long addrId) {
        Optional<Resource<Address>> addressResource =
                identRepo.findById(identId).map(Identification::getAddresses).orElse(Collections.emptySet()).stream()
                         .filter(a -> a.getId() == addrId).findAny().map(addressAsm::toResource);

        return ResponseEntity.of(addressResource);
    }

    @RequestMapping(value = "/{addrId}", method = {RequestMethod.PUT, RequestMethod.PATCH}
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity putAddress(@PathVariable Long identId,
                                     @PathVariable Long addrId,
                                     @RequestBody Address updatedAddress,
                                     HttpServletRequest request) {
        Optional<Identification> identOpt = identRepo.findById(identId);

        if (identOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(String.format(IDENTIFICATION_HAS_NOT_FOUND, identId));
        else {
            Optional<Address> addrOpt =
                    identOpt.get().getAddresses().stream().filter(a -> a.getId().equals(addrId)).findAny();

            if (addrOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(String.format(ADDRESS_HAS_NOT_FOUND, addrId));
            else {
                Address address = addrOpt.get();

                if (equalsIgnoreCase("PUT", request.getMethod()))
                    address.setAllPrimitiveFieldsFrom(updatedAddress);
                else address.setAllPrimitiveNotNullFieldsFrom(updatedAddress);

                addrRepo.save(address);
                return ResponseEntity.status(HttpStatus.OK).header("location",
                        linkTo(methodOn(AddressController.class).getAddress(identId, addrId)).toUri().toASCIIString())
                                     .build();
            }
        }
    }

    @DeleteMapping(value = "/{addrId}")
    public ResponseEntity deleteAddress(@PathVariable Long identId, @PathVariable Long addrId) {
        Optional<Identification> identOpt = identRepo.findById(identId);

        if (identOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(String.format(IDENTIFICATION_HAS_NOT_FOUND, identId));
        else {
            Identification identification = identOpt.get();
            Set<Address> addressesAfterFiltering =
                    identification.getAddresses().stream().filter(a -> !a.getId().equals(addrId))
                                  .collect(Collectors.toSet());

            if (identification.getAddresses().size() > addressesAfterFiltering.size()) {
                identification.setAddresses(addressesAfterFiltering);
                identRepo.save(identification);
                return ResponseEntity.ok().build();
            } else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format(ADDRESS_HAS_NOT_FOUND, addrId));

        }
    }

}
