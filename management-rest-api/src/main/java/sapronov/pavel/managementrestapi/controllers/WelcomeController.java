package sapronov.pavel.managementrestapi.controllers;

import lombok.Value;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/")
public class WelcomeController {

    @GetMapping
    public ResponseEntity<SimpleHref> welcome() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                             .body(new SimpleHref(
                                     linkTo(methodOn(IdentificationController.class).getIdentifications()).toString()));
    }

    @Value
    private class SimpleHref {
        String href;
    }

}
