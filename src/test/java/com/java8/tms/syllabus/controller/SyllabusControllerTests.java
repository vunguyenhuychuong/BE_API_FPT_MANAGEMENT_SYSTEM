package com.java8.tms.syllabus.controller;

import com.java8.tms.common.dto.Pagination;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.Syllabus;
import com.java8.tms.common.exception.model.ResourceNotFoundException;
import com.java8.tms.common.meta.SyllabusStatus;
import com.java8.tms.syllabus.dto.RequestForListOfSyllabus;
import com.java8.tms.syllabus.dto.ResponseForListOfSyllabus;
import com.java8.tms.syllabus.dto.SyllabusDTO;
import com.java8.tms.syllabus.service.impl.SyllabusServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SyllabusControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private SyllabusServiceImpl service;


    private final UUID userA = UUID.randomUUID();
    private final UUID userB = UUID.randomUUID();
    private final UUID userC = UUID.randomUUID();
    private final UUID syllabus1 = UUID.randomUUID();
    private final UUID syllabus2 = UUID.randomUUID();
    private final UUID syllabus3 = UUID.randomUUID();
    private final UUID syllabus4 = UUID.randomUUID();
    private final UUID syllabus5 = UUID.randomUUID();
    private final UUID syllabusDraft1 = UUID.randomUUID();
    private final UUID syllabusDraft2 = UUID.randomUUID();
    private final UUID syllabusNotFound = UUID.randomUUID();

    private List<Syllabus> syllabuses = new ArrayList<>(List.of(
            Syllabus.builder().id(syllabus1).name(".Net Basic Program").code("NBP").createdBy(userB).days(2)
                    .hours(10).status(SyllabusStatus.ACTIVE).updatedBy(userB).version("1.0").build(),
            Syllabus.builder().id(syllabus2).name("Azure DevOps").code("ADO").createdBy(userB).days(2).hours(10)
                    .status(SyllabusStatus.ACTIVE).updatedBy(userB).version("1.0").build(),
            Syllabus.builder().id(syllabus3).name("JUnit Testing").code("FSJ").createdBy(userB).days(2).hours(10)
                    .status(SyllabusStatus.ACTIVE).updatedBy(userB).version("1.0").build(),
            Syllabus.builder().id(syllabus4).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .status(SyllabusStatus.DEACTIVE).updatedBy(userB).version("1.1").build(),
            Syllabus.builder().id(syllabus5).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .status(SyllabusStatus.ACTIVE).updatedBy(userB).version("3.0").build()
    ));

    private final List<Syllabus> syllabusesDraft = new ArrayList<>(List.of(
            Syllabus.builder().id(syllabusDraft1).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .status(SyllabusStatus.DRAFT).updatedBy(userB).version("3.0").build(),
            Syllabus.builder().id(syllabusDraft2).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .status(SyllabusStatus.DRAFT).updatedBy(userB).version("3.0").build()
    ));

    @Before
    public void setUp() throws ResourceNotFoundException {
        //this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        Mockito.when(service.deactiveSyllabus(syllabus1)).thenReturn(new ResponseObject(HttpStatus.OK.toString(), "Syllabus is deactivated successfully!",
                new Pagination(), null));
        Mockito.when(service.deleteSyllabus(syllabus2)).thenReturn(new ResponseObject(HttpStatus.OK.toString(), "Syllabus is deleted successfully!",
                new Pagination(), null));
        Mockito.when(service.deactiveSyllabus(syllabusNotFound)).thenReturn(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                "Syllabus " + syllabusNotFound + " is not found!", new Pagination(), null));
        Mockito.when(service.deleteSyllabus(syllabusNotFound)).thenReturn(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                "Syllabus " + syllabusNotFound + " is not found!", new Pagination(), null));

        List<String> suggestions = new ArrayList<>(List.of(".NET Basic Program", "Azure DevOps"));

        Mockito.when(service.getSuggestions("a")).thenReturn(ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "Total " + suggestions.size() + " element(s)",
                        null, suggestions)));
    }

    private List<ResponseForListOfSyllabus> map(){
        List<SyllabusDTO> results = syllabuses.stream().map(syllabus -> modelMapper.map(syllabus, SyllabusDTO.class))
                .collect(Collectors.toList());
        List<ResponseForListOfSyllabus> responseData = results.stream().map(syllabus -> modelMapper.map(syllabus, ResponseForListOfSyllabus.class))
                .collect(Collectors.toList());
        return responseData;
    }

    @Test
    @WithMockUser(authorities = "VIEW_SYLLABUS")
    @DisplayName("List All Syllabuses")
    public void testListOfSyllabusControllerWhenNoParamExpectStatusIsOk() throws Exception {
        List<ResponseForListOfSyllabus> results= map();
        int total = ((int) (results.size() % 10 == 0 ? (results.size() / 10) : (results.size() / 10)));
        Mockito.when(service.getAllSyllabuses(any(RequestForListOfSyllabus.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(HttpStatus.OK.toString(), "Total 5 element(s) in page 1",
                new Pagination(1, 10, total), results)));

        mockMvc.perform(get("/api/v1/syllabus")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "VIEW_SYLLABUS")
    @DisplayName("List All Syllabuses And Expect Right Size")
    public void testListOfSyllabusControllerWhenNoParamExpectFiveElementsInList() throws Exception {
        List<ResponseForListOfSyllabus> results= map();
        int total = ((int) (results.size() % 10 == 0 ? (results.size() / 10) : (results.size() / 10)));
        Mockito.when(service.getAllSyllabuses(any(RequestForListOfSyllabus.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(HttpStatus.OK.toString(), "Total 5 element(s) in page 1",
                new Pagination(1, 10, total), results)));

        mockMvc.perform(get("/api/v1/syllabus")).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Total 5 element(s) in page 1"));
    }

    @Test
    @WithMockUser(authorities = "MODIFY_SYLLABUS")
    @DisplayName("Test Deactivate A Syllabus With Valid Id")
    public void testDeactivateSyllabusControllerWhenValidIdExpectStatusIdOk() throws Exception {
        mockMvc.perform(put("/api/v1/syllabus/de-active/{id}", syllabus1)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "MODIFY_SYLLABUS")
    @DisplayName("Test Deactivate A Syllabus With Valid Id And Return Right Syllabus Id In Message")
    public void testDeactivateSyllabusControllerWhenValidIdExpectDeleteRightSyllabus() throws Exception {
        mockMvc.perform(put("/api/v1/syllabus/de-active/{id}", syllabus1)).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Syllabus is deactivated successfully!"));
    }

    @Test
    @WithMockUser(authorities = "MODIFY_SYLLABUS")
    @DisplayName("Test Delete A Syllabus With Valid Id")
    public void testDeleteSyllabusControllerWhenValidIdExpectStatusIsOk() throws Exception {
        mockMvc.perform(delete("/api/v1/syllabus/{id}", syllabus2)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "MODIFY_SYLLABUS")
    @DisplayName("Test Delete A Syllabus With Valid Id And Return Right Syllabus Id In Message")
    public void testDeleteSyllabusControllerWhenValidIdExpectDeleteRightSyllabus() throws Exception {
        mockMvc.perform(delete("/api/v1/syllabus/{id}", syllabus2)).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Syllabus is deleted successfully!"));
    }

    @Test
    @WithMockUser(authorities = "MODIFY_SYLLABUS")
    @DisplayName("Test Deactivate A Syllabus With Invalid Id")
    public void testDeactivateSyllabusControllerWhenInvalidIdExpectStatusIsNotFound() throws Exception {
        mockMvc.perform(put("/api/v1/syllabus/de-active/{id}", syllabusNotFound)).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("404 NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = "MODIFY_SYLLABUS")
    @DisplayName("Test Delete A Syllabus With Invalid Id")
    public void testDeleteSyllabusControllerWhenInvalidIdExpectStatusIsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/syllabus/{id}", syllabusNotFound)).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("404 NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = "VIEW_SYLLABUS")
    @DisplayName("Test Get All Draft By User Id")
    public void testListOfDraftControllerWhenNoParamExpectStatusIsOk() throws Exception {
        mockMvc.perform(get("/api/v1/syllabus/drafts")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "VIEW_SYLLABUS")
    @DisplayName("List All Draft And Expect Right Size")
    public void testListOfDraftControllerWhenNoParamExpectRightElementsInList() throws Exception {
        List<SyllabusDTO> resultsDraft = syllabusesDraft.stream().map(syllabus -> modelMapper.map(syllabus, SyllabusDTO.class))
                .collect(Collectors.toList());
        int total = (resultsDraft.size() / 10);
        Mockito.when(service.getAllDraftByUserId( "DESC", 1, 10)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(HttpStatus.OK.toString(), "Total 2 element(s) in page 1",
                new Pagination(1, 10, total), resultsDraft)));
        mockMvc.perform(get("/api/v1/syllabus/drafts")).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Total 2 element(s) in page 1"));

    }

    @Test
    @WithMockUser(authorities = "VIEW_SYLLABUS")
    @DisplayName("Test Get List Of Suggestions With Valid Keyword")
    public void testGetListOfSuggestionsControllerWhenValidKeywordExpectStatusIsOk() throws Exception {
        mockMvc.perform(get("/api/v1/syllabus/suggest").param("keyword", "a")).andDo(print())
                .andExpect(status().isOk());
    }
}
