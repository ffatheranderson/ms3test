package sapronov.pavel.managementrestapi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sapronov.pavel.managementrestapi.controllers.IdentificationController;
import sapronov.pavel.managementrestapi.entities.Identification;
import sapronov.pavel.managementrestapi.repositories.IdentificationRepository;
import sapronov.pavel.managementrestapi.resource_assemblers.IdentificationAssembler;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sapronov.pavel.managementrestapi.ControllersFixture.*;

@RunWith(SpringRunner.class)
@WebMvcTest(IdentificationController.class)
public class IdentificationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IdentificationRepository identRepo;

    @MockBean
    private IdentificationAssembler identAsm;

    @Before
    public void setUpBeforeClass() {
        given(identAsm.toResource(any())).willCallRealMethod();
    }

    @Test
    public void testGetIdentificationsSuccess() throws Exception {
        given(identRepo.findAll()).willReturn(new ArrayList<>(List.of(bobMarley, jackieChan)));

        mvc.perform(get("/identifications").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$._embedded.identificationList", hasSize(2)))
           .andExpect(jsonPath("$._embedded.identificationList[0].firstName", notNullValue()))
           .andExpect(jsonPath("$._embedded.identificationList[1].firstName", notNullValue()))
           .andExpect(jsonPath("$._links", notNullValue()))
           .andExpect(jsonPath("$._links.self", notNullValue()));

        verify(identRepo).findAll();
    }

    @Test
    public void testGetIdentificationsNotFound() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(get("/identifications").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound());

        verify(identRepo).findAll();
    }

    @Test
    public void testGetIdentificationSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(get("/identifications/5").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.firstName", notNullValue()))
           .andExpect(jsonPath("$.lastName", notNullValue()))
           .andExpect(jsonPath("$.dob", notNullValue()))
           .andExpect(jsonPath("$.gender", notNullValue()))
           .andExpect(jsonPath("$.title", notNullValue()))
           .andExpect(jsonPath("$._links", notNullValue()))
           .andExpect(jsonPath("$._links.self", notNullValue()))
           .andExpect(jsonPath("$._links.addresses", notNullValue()))
           .andExpect(jsonPath("$._links.identifications", notNullValue()))
           .andExpect(jsonPath("$._links.communications", notNullValue()));

        verify(identRepo).findById(5L);
    }

    @Test
    public void testPostNewIdentificationSuccess() throws Exception {
        given(identRepo.save(any())).willAnswer(inv -> {
            Identification newIdent = inv.getArgument(0);
            assertThat(newIdent.getId(), nullValue());
            assertThat(newIdent.getFirstName(), equalTo("Harry"));
            assertThat(newIdent.getLastName(), equalTo("Potter"));
            newIdent.setId(11L);
            return newIdent;
        });

        mvc.perform(post("/identifications")
                .accept(MediaType.TEXT_HTML_VALUE)
                .content(asJsonString(Identification.builder()
                                                    .firstName("Harry")
                                                    .lastName("Potter")
                                                    .build()))
                .contentType(MediaType.APPLICATION_JSON)
        )
           .andDo(print())
           .andExpect(status().isCreated())
           .andExpect(header().exists("Location"))
           .andExpect(header().string("Location", containsString("identifications/11")));

        verify(identRepo).save(any());
    }

    @Test
    public void testPatchAddressSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        when(identRepo.save(any())).thenAnswer(invoc -> {
            Identification updatedIdentification = invoc.getArgument(0);

            assertThat(updatedIdentification.getFirstName(), equalTo("Bobby"));
            assertThat(updatedIdentification.getLastName(), equalTo("Marleyy"));
            assertThat(updatedIdentification.getTitle(), equalTo("SuperSinger"));
            assertThat(updatedIdentification.getDob()
                    , equalTo(LocalDate.of(1998, Month.FEBRUARY, 1)));
            assertThat(updatedIdentification.getAddresses(), notNullValue());
            assertThat(updatedIdentification.getCommunications(), notNullValue());

            return updatedIdentification;
        });


        mvc.perform(patch("/identifications/5").accept(MediaType.TEXT_HTML_VALUE)
                                               .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(new Identification().toBuilder()
                                                         .firstName("Bobby")
                                                         .lastName("Marleyy")
                                                         .title("SuperSinger")
                                                         .dob(LocalDate.of(1998, Month.FEBRUARY, 1))
                                                         .build()))
                                               .characterEncoding("UTF-8"))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(header().string("Location", containsString("/identifications/5")));

        verify(identRepo).save(any());
    }

    @Test
    public void testPutAddressSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        when(identRepo.save(any())).thenAnswer(invoc -> {
            Identification updatedIdentification = invoc.getArgument(0);

            assertThat(updatedIdentification.getFirstName(), nullValue());
            assertThat(updatedIdentification.getLastName(), equalTo("Marleyy"));
            assertThat(updatedIdentification.getTitle(), nullValue());
            assertThat(updatedIdentification.getDob()
                    , equalTo(LocalDate.of(1998, Month.FEBRUARY, 1)));
            assertThat(updatedIdentification.getAddresses(), notNullValue());
            assertThat(updatedIdentification.getCommunications(), notNullValue());

            return updatedIdentification;
        });


        mvc.perform(put("/identifications/5").accept(MediaType.TEXT_HTML_VALUE)
                                             .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(new Identification().toBuilder()
                                                         .lastName("Marleyy")
                                                         .dob(LocalDate.of(1998, Month.FEBRUARY, 1))
                                                         .build()))
                                             .characterEncoding("UTF-8"))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(header().string("Location", containsString("/identifications/5")));


        verify(identRepo).findById(5L);
        verify(identRepo).save(any());
    }

    @Test
    public void testPutAddressNotFoundWhenIdentificationIsNotExisted() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(put("/identifications/5").accept(MediaType.TEXT_HTML_VALUE)
                                             .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(new Identification().toBuilder()
                                                         .lastName("Marleyy")
                                                         .dob(LocalDate.of(1998, Month.FEBRUARY, 1))
                                                         .build()))
                                             .characterEncoding("UTF-8"))
           .andDo(print())
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("Identification with id: 5 has not found.")));

        verify(identRepo).findById(5L);
        verify(identRepo, times(0)).save(any());
    }

    @Test
    public void testDeleteIdentificationSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(delete("/identifications/5").accept(MediaType.TEXT_HTML_VALUE)
                                                .contentType(MediaType.TEXT_HTML_VALUE))
           .andDo(print())
           .andExpect(status().isOk());

        verify(identRepo, times(1)).findById(5L);
        verify(identRepo, times(1)).delete(any());
    }

    @Test
    public void testDeleteAddressNotFoundWhenIdentificationIsNotExisted() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(delete("/identifications/5").accept(MediaType.TEXT_HTML_VALUE)
                                                .contentType(MediaType.TEXT_HTML_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("Identification with id: 5 has not found.")));

        verify(identRepo).findById(5L);
        verify(identRepo, times(0)).delete(any());
    }

}