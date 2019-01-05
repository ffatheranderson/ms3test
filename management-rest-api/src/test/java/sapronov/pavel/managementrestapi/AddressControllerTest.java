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
import sapronov.pavel.managementrestapi.entities.Address;
import sapronov.pavel.managementrestapi.repositories.AddressRepository;
import sapronov.pavel.managementrestapi.repositories.IdentificationRepository;
import sapronov.pavel.managementrestapi.resource_assemblers.AddressAssembler;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sapronov.pavel.managementrestapi.ControllersFixture.asJsonString;
import static sapronov.pavel.managementrestapi.ControllersFixture.bobMarley;

@RunWith(SpringRunner.class)
@WebMvcTest(AddressController.class)
public class AddressControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IdentificationRepository identRepo;
    @MockBean
    private AddressRepository addrRepo;
    @MockBean
    AddressAssembler addrAsm;

    @Before
    public void setUpBeforeClass() {
        given(addrAsm.toResource(any())).willCallRealMethod();
    }

    @Test
    public void testGetAddressesSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(get("/identifications/5/addresses").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$._embedded.addressList", hasSize(2)))
           .andExpect(jsonPath("$._embedded.addressList[0].type", equalTo("Work")))
           .andExpect(jsonPath("$._embedded.addressList[1].type", equalTo("Home")));
    }

    @Test
    public void testGetAddressesNotFound() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(get("/identifications/5/addresses").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound());
    }


    @Test
    public void testAddAddressToIdentificationSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));
        given(addrRepo.save(any())).willAnswer(invocation -> {
            Address addr = invocation.getArgument(0);
            assertThat(addr.getIdentification().getId(), equalTo(5L));
            addr.setId(11L);
            return addr;
        });

        mvc.perform(post("/identifications/5/addresses")
                .accept(MediaType.TEXT_HTML_VALUE)
                .content(asJsonString(
                        Address.builder().city("Novovoronezh")
                               .state("VoronezhskayaOblast").build()))
                .contentType(MediaType.APPLICATION_JSON)
        )
           .andDo(print())
           .andExpect(status().isCreated())
           .andExpect(header().exists("Location"))
           .andExpect(header().string("Location", containsString("identifications/5/addresses/11")));
        verify(addrRepo).save(any());
    }

    @Test
    public void testAddAddressToIdentificationIdentificationNotFound() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(post("/identifications/5/addresses")
                .accept(MediaType.TEXT_HTML_VALUE)
                .content(asJsonString(
                        Address.builder().city("Novovoronezh")
                               .state("VoronezhskayaOblast").build()))
                .contentType(MediaType.APPLICATION_JSON)
        )
           .andDo(print())
           .andExpect(status().isNotFound())
           .andExpect(content().string("Identification with id: 5 has not found."));

        verify(addrRepo, times(0)).save(any());
    }

    @Test
    public void testGetAddressSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(get("/identifications/5/addresses/1").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.type", equalTo("Home")))
           .andExpect(jsonPath("$._links.self", notNullValue()))
           .andExpect(jsonPath("$._links.addresses", notNullValue()));
    }

    @Test
    public void testGetAddressNotFoundWhenIdentificationIsNotExisted() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(get("/identifications/5/addresses/1").accept(MediaTypes.HAL_JSON_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound());
        verify(identRepo, times(1)).findById(5L);
    }

    @Test
    public void testGetAddressNotFoundWhenAddressIsNotExisted() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(get("/identifications/5/addresses/3").accept(MediaTypes.HAL_JSON_VALUE)
                                                         .contentType(MediaType.TEXT_HTML_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound());
        verify(identRepo, times(1)).findById(5L);
    }

    @Test
    public void testPutAddressSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));
        when(addrRepo.save(any())).thenAnswer(invoc -> {
            Address addrForSave = invoc.getArgument(0);
            assertThat(addrForSave.getCity(), equalTo("Novovoronezh"));
            assertThat(addrForSave.getNumber(), equalTo(13));
            assertThat(addrForSave.getId(), equalTo(1L));
            assertThat(addrForSave.getState(), nullValue());
            assertThat(addrForSave.getStreet(), nullValue());
            assertThat(addrForSave.getType(), nullValue());
            assertThat(addrForSave.getUnit(), nullValue());
            assertThat(addrForSave.getZipCode(), nullValue());
            assertThat(addrForSave.getIdentification().getId(), equalTo(5L));

            return addrForSave;
        });


        mvc.perform(put("/identifications/5/addresses/1").accept(MediaType.TEXT_HTML_VALUE)
                                                         .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(Address.builder().city("Novovoronezh").number(13).build())))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(header().string("Location", containsString("/identifications/5/addresses/1")));

        verify(addrRepo, times(1)).save(any());
    }

    @Test
    public void testPatchAddressSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));
        when(addrRepo.save(any())).thenAnswer(invoc -> {
            Address addrForSave = invoc.getArgument(0);
            assertThat(addrForSave.getCity(), equalTo("Novovoronezh"));
            assertThat(addrForSave.getNumber(), equalTo(13));
            assertThat(addrForSave.getId(), equalTo(1L));
            assertThat(addrForSave.getState(), notNullValue());
            assertThat(addrForSave.getStreet(), notNullValue());
            assertThat(addrForSave.getType(), notNullValue());
            assertThat(addrForSave.getUnit(), notNullValue());
            assertThat(addrForSave.getZipCode(), notNullValue());
            assertThat(addrForSave.getIdentification().getId(), equalTo(5L));

            return addrForSave;
        });


        mvc.perform(patch("/identifications/5/addresses/1").accept(MediaType.TEXT_HTML_VALUE)
                                                           .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(Address.builder().city("Novovoronezh").number(13).build())))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(header().string("Location", containsString("/identifications/5/addresses/1")));

        verify(addrRepo, times(1)).save(any());
    }

    @Test
    public void testPutAddressNotFoundWhenIdentificationNotExisted() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(put("/identifications/11/addresses/1").accept(MediaType.TEXT_HTML_VALUE)
                                                          .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(Address.builder().city("Novovoronezh").number(13).build())))
           .andDo(print())
           .andExpect(content().contentType(MediaType.TEXT_HTML))
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("Identification with id: 11 has not found.")));

        verify(addrRepo, times(0)).save(any());
    }

    @Test
    public void testPutAddressNotFoundWhenAddressNotExisted() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(put("/identifications/5/addresses/3").accept(MediaType.TEXT_HTML_VALUE)
                                                         .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(Address.builder().city("Novovoronezh").number(13).build())))
           .andDo(print())
           .andExpect(content().contentType(MediaType.TEXT_HTML))
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("Address with id: 3 has not found.")));

        verify(addrRepo, times(0)).save(any());
    }

    @Test
    public void testDeleteAddressSuccess() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(delete("/identifications/5/addresses/2").accept(MediaType.TEXT_HTML_VALUE)
                                                            .contentType(MediaType.TEXT_HTML_VALUE))
           .andDo(print())
           .andExpect(status().isOk());

        verify(identRepo, times(1)).findById(5L);
        verify(addrRepo, times(1)).delete(any());
    }

    @Test
    public void testDeleteAddressNotFoundWhenIdentificationIsNotExisted() throws Exception {
        given(identRepo.findById(any())).willReturn(Optional.empty());

        mvc.perform(delete("/identifications/5/addresses/2").accept(MediaType.TEXT_HTML_VALUE)
                                                            .contentType(MediaType.TEXT_HTML_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("Identification with id: 5 has not found.")));

        verify(identRepo, times(1)).findById(5L);
        verify(addrRepo, times(0)).delete(any());
    }

    @Test
    public void testDeleteAddressNotFoundWhenAddressIsNotExisted() throws Exception {
        given(identRepo.findById(5L)).willReturn(Optional.of(bobMarley));

        mvc.perform(delete("/identifications/5/addresses/3").accept(MediaType.TEXT_HTML_VALUE)
                                                            .contentType(MediaType.TEXT_HTML_VALUE))
           .andDo(print())
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("Address with id: 3 has not found.")));

        verify(identRepo, times(1)).findById(5L);
        verify(addrRepo, times(0)).delete(any());
    }

}
