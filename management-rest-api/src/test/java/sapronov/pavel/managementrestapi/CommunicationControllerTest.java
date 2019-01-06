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
import sapronov.pavel.managementrestapi.controllers.AddressController;
import sapronov.pavel.managementrestapi.controllers.CommunicationController;
import sapronov.pavel.managementrestapi.entities.Address;
import sapronov.pavel.managementrestapi.entities.Communication;
import sapronov.pavel.managementrestapi.repositories.AddressRepository;
import sapronov.pavel.managementrestapi.repositories.CommunicationRepository;
import sapronov.pavel.managementrestapi.repositories.IdentificationRepository;
import sapronov.pavel.managementrestapi.resource_assemblers.AddressAssembler;
import sapronov.pavel.managementrestapi.resource_assemblers.CommunicationAssembler;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sapronov.pavel.managementrestapi.ControllersFixture.asJsonString;
import static sapronov.pavel.managementrestapi.ControllersFixture.bobMarley;

@RunWith(SpringRunner.class)
@WebMvcTest(CommunicationController.class)
public class CommunicationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IdentificationRepository identRepo;
    @MockBean
    private CommunicationRepository commRepo;
    @MockBean
    CommunicationAssembler commAsm;

    @Before
    public void setUpBeforeClass() {
        given(commAsm.toResource(any())).willCallRealMethod();
    }

    @Test
    public void testGetCommunicationsSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(get("/identifications/5/communications").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$._embedded.communicationList", hasSize(2)))
           .andExpect(jsonPath("$._embedded.communicationList[0].type", notNullValue()))
           .andExpect(jsonPath("$._embedded.communicationList[1].type", notNullValue()))
           .andExpect(jsonPath("$._links", notNullValue()))
           .andExpect(jsonPath("$._links.self", notNullValue()));

        verify(identRepo).findById(5L);
    }

    @Test
    public void testGetCommunicationNotFound() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(get("/identifications/5/communications").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound());
    }


    @Test
    public void testCreateCommunicationSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        given(commRepo.save(any())).willAnswer(invocation -> {
            Communication comm = invocation.getArgument(0);
            assertThat(comm.getIdentification().getId(), equalTo(5L));
            comm.setId(11L);
            return comm;
        });

        mvc.perform(post("/identifications/5/communications")
                .accept(MediaType.TEXT_HTML_VALUE)
                .content(asJsonString(
                        Communication.builder().type("email")
                                     .value("hh@hh.com").build()))
                .contentType(MediaType.APPLICATION_JSON)
        )
           .andDo(print())
           .andExpect(status().isCreated())
           .andExpect(header().exists("Location"))
           .andExpect(header().string("Location", containsString("identifications/5/communications/11")));

        verify(commRepo).save(any());
    }

    @Test
    public void testCreateCommunicationWhenIdentificationNotFound() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(post("/identifications/5/communications")
                .accept(MediaType.TEXT_HTML_VALUE)
                .content(asJsonString(
                        Communication.builder().type("email")
                                     .value("hh@hh.com").build()))
                .contentType(MediaType.APPLICATION_JSON)
        )
           .andDo(print())
           .andExpect(status().isNotFound())
           .andExpect(content().string("Identification with id: 5 has not found."));

        verify(commRepo, times(0)).save(any());
    }

    @Test
    public void testGetCommunicationSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(get("/identifications/5/communications/3").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.type", equalTo("Cell phone")))
           .andExpect(jsonPath("$._links.self", notNullValue()))
           .andExpect(jsonPath("$._links.communications", notNullValue()));

        verify(identRepo).findById(5L);
    }

    @Test
    public void testGetCommunicationNotFoundWhenIdentificationIsNotFound() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(get("/identifications/5/communications/3").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound());

        verify(identRepo).findById(5L);
    }

    @Test
    public void testGetCommunicationNotFoundWhenCommunicationIsNotExisted() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(get("/identifications/5/communications/5").accept(MediaTypes.HAL_JSON_VALUE)
                                                              .contentType(MediaType.TEXT_HTML_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound());

        verify(identRepo).findById(5L);
    }

    @Test
    public void testPutCommunicationSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        when(commRepo.save(any())).thenAnswer(invoc -> {
            Communication commForSave = invoc.getArgument(0);
            assertThat(commForSave.getId(), equalTo(3L));
            assertThat(commForSave.getPreferred(), nullValue());
            assertThat(commForSave.getType(), nullValue());
            assertThat(commForSave.getValue(), equalTo("testPut"));
            assertThat(commForSave.getIdentification().getId(), equalTo(5L));

            return commForSave;
        });

        mvc.perform(put("/identifications/5/communications/3").accept(MediaType.TEXT_HTML_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(Communication.builder().value("testPut").build())))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(header().string("Location", containsString("/identifications/5/communications/3")));

        verify(commRepo).save(any());
    }

    @Test
    public void testPatchCommunicationSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        when(commRepo.save(any())).thenAnswer(invoc -> {
            Communication commForSave = invoc.getArgument(0);
            assertThat(commForSave.getId(), equalTo(3L));
            assertThat(commForSave.getPreferred(), equalTo(true));
            assertThat(commForSave.getType(), equalTo("Cell phone"));
            assertThat(commForSave.getValue(), equalTo("testPut"));
            assertThat(commForSave.getIdentification().getId(), equalTo(5L));

            return commForSave;
        });

        mvc.perform(patch("/identifications/5/communications/3").accept(MediaType.TEXT_HTML_VALUE)
                                                                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(Communication.builder().value("testPut").build())))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(header().string("Location", containsString("/identifications/5/communications/3")));

        verify(commRepo).save(any());
    }


    @Test
    public void testPutCommunicationNotFoundWhenIdentificationNotExisted() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(put("/identifications/11/communications/3").accept(MediaType.TEXT_HTML_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(Communication.builder().value("testPut").build())))
           .andDo(print())
           .andExpect(content().contentType(MediaType.TEXT_HTML))
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("Identification with id: 11 has not found.")));

        verify(identRepo).findById(11L);
        verify(commRepo, times(0)).save(any());
    }

    @Test
    public void testPutCommunicationNotFoundWhenCommunicationNotExisted() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(put("/identifications/5/communications/5").accept(MediaType.TEXT_HTML_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(Communication.builder().value("testPut").build())))
           .andDo(print())
           .andExpect(content().contentType(MediaType.TEXT_HTML))
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("Communication with id: 5 has not found.")));

        verify(identRepo).findById(5L);
        verify(commRepo, times(0)).save(any());
    }

    @Test
    public void testDeleteCommunicationSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(delete("/identifications/5/communications/3").accept(MediaType.TEXT_HTML_VALUE)
                                                                 .contentType(MediaType.TEXT_HTML_VALUE))
           .andDo(print())
           .andExpect(status().isOk());

        verify(identRepo).findById(5L);
        verify(identRepo).save(any());
    }

    @Test
    public void testDeleteCommunicationNotFoundWhenIdentificationIsNotExisted() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(delete("/identifications/11/communications/3").accept(MediaType.TEXT_HTML_VALUE)
                                                                  .contentType(MediaType.TEXT_HTML_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("Identification with id: 11 has not found.")));

        verify(identRepo).findById(11L);
        verify(commRepo, times(0)).delete(any());
    }

    @Test
    public void testDeleteCommunicationNotFoundWhenCommunicationIsNotExisted() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(delete("/identifications/5/communications/5").accept(MediaType.TEXT_HTML_VALUE)
                                                                .contentType(MediaType.TEXT_HTML_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("Communication with id: 5 has not found.")));

        verify(identRepo).findById(5L);
        verify(commRepo, times(0)).delete(any());
    }
}
