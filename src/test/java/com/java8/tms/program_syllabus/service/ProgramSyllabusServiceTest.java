package com.java8.tms.program_syllabus.service;

import com.java8.tms.common.entity.Syllabus;
import com.java8.tms.common.entity.TrainingProgram;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.meta.SyllabusStatus;
import com.java8.tms.common.meta.TrainingProgramStatus;
import com.java8.tms.common.repository.ProgramSyllabusRepository;
import com.java8.tms.common.repository.SyllabusRepository;
import com.java8.tms.common.repository.TrainingProgramRepository;
import com.java8.tms.common.repository.UserRepository;
import com.java8.tms.program_syllabus.dto.*;
import com.java8.tms.program_syllabus.exception.InvalidCreateProgramException;
import com.java8.tms.program_syllabus.jdbc.ProgramSyllabusJDBC;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Log4j2
class ProgramSyllabusServiceTest {

    // set up data input for search Syllabus
    private final String keyName = "Testing";
    private final String keyUpName = "TEstIng";
    private final String keyNameVersion = "Testing 1.";
    private final String keyNotFound = "Bye Word";
    // set up data input for save
    private final String nameProgram = "Fresher Unity";
    private final String nameDuplicate = "Professional Mobile Programing Developer (Android platform)";
    private final String nameProgramWithID = "Fullstack Java Web Developer"; // name of program 3
    // set up user ID
    private final UUID userA = UUID.randomUUID(); // user create program
    private final UUID userB = UUID.randomUUID(); // user create syllabus
    private final UUID userC = UUID.randomUUID(); // non
    // set up program ID
    private final UUID program1 = UUID.randomUUID(); // 1,2
    private final UUID program2 = UUID.randomUUID(); // 1
    private final UUID program3 = UUID.randomUUID(); // 1
    private final UUID program4 = UUID.randomUUID(); // 1 not draft
    private final UUID programEdit = UUID.randomUUID(); // use for delete and save program
    private final UUID programNotFound = UUID.randomUUID(); // use for case not found
    // set up syllabus ID
    private final UUID syllabus1 = UUID.randomUUID();
    private final UUID syllabus2 = UUID.randomUUID();
    private final UUID syllabus3 = UUID.randomUUID();
    private final UUID syllabus4 = UUID.randomUUID(); // not active
    private final UUID syllabus5 = UUID.randomUUID(); // duplicate name
    private final UUID syllabusNotFound = UUID.randomUUID();
    @MockBean
    private TrainingProgramRepository trainingProgramRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ProgramSyllabusRepository programSyllabusRepository;
    @MockBean
    private SyllabusRepository syllabusRepository;
    @MockBean
    private ProgramSyllabusJDBC programSyllabusJDBC;
    @Autowired
    private ProgramSyllabusService programSyllabusService;

    @BeforeEach
    void setUp() throws Exception {
        // set up List program , size = 4
        List<TrainingProgram> programs = new ArrayList<>(List.of(

                TrainingProgram.builder().id(program1).name("Fresher Java Developer")
                        .status(TrainingProgramStatus.DRAFT).version(null).createdBy(userA).build(),

                TrainingProgram.builder().id(program2).name("Fresher Android Developer")
                        .status(TrainingProgramStatus.DRAFT).version(null).createdBy(userA).build(),

                TrainingProgram.builder().id(program3).name("Fullstack Java Web Developer")
                        .status(TrainingProgramStatus.DRAFT).version(null).createdBy(userA).build(),

                TrainingProgram.builder().id(program4)
                        .name("Professional Mobile Programing Developer (Android platform)")
                        .status(TrainingProgramStatus.ACTIVE).version("1.0").createdBy(userA).build()

        ));

        // set up List Syllabus, size = 5
        List<Syllabus> syllabuses = new ArrayList<>(List.of(

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

        // set up list users , size = 3
        List<User> users = new ArrayList<>(List.of(User.builder().id(userA).fullname("Luu").build(),

                User.builder().id(userB).fullname("Thanh").build(),

                User.builder().id(userC).fullname("Huy").build()

        ));

        // set up Optional for Program
        Optional<TrainingProgram> oProgram1 = Optional.ofNullable(programs.get(0));
        Optional<TrainingProgram> oProgram2 = Optional.ofNullable(programs.get(1));
        Optional<TrainingProgram> oProgram3 = Optional.ofNullable(programs.get(2));
        Optional<TrainingProgram> oProgram4 = Optional.ofNullable(programs.get(3));

        // Mockito to Program
        Mockito.when(trainingProgramRepository.findById(program1)).thenReturn(oProgram1);
        Mockito.when(trainingProgramRepository.findById(program2)).thenReturn(oProgram2);
        Mockito.when(trainingProgramRepository.findById(program3)).thenReturn(oProgram3);
        Mockito.when(trainingProgramRepository.findById(program4)).thenReturn(oProgram4);

        // set up Optional for Syllabus
        Optional<Syllabus> oSyllabus1 = Optional.ofNullable(syllabuses.get(0));
        Optional<Syllabus> oSyllabus2 = Optional.ofNullable(syllabuses.get(1));
        Optional<Syllabus> oSyllabus3 = Optional.ofNullable(syllabuses.get(2));
        Optional<Syllabus> oSyllabus4 = Optional.ofNullable(syllabuses.get(3));
        Optional<Syllabus> oSyllabus5 = Optional.ofNullable(syllabuses.get(4));

        // Mockito to Syllabus
        Mockito.when(syllabusRepository.findById(syllabus1)).thenReturn(oSyllabus1);
        Mockito.when(syllabusRepository.findById(syllabus2)).thenReturn(oSyllabus2);
        Mockito.when(syllabusRepository.findById(syllabus3)).thenReturn(oSyllabus3);
        Mockito.when(syllabusRepository.findById(syllabus4)).thenReturn(oSyllabus4);
        Mockito.when(syllabusRepository.findById(syllabus5)).thenReturn(oSyllabus5);

        // set up Optional for User
        Optional<User> oUserA = Optional.ofNullable(users.get(0));
        Optional<User> oUserB = Optional.ofNullable(users.get(1));
        Optional<User> oUserC = Optional.ofNullable(users.get(2));

        // Mockito to User
        Mockito.when(userRepository.findById(userA)).thenReturn(oUserA);
        Mockito.when(userRepository.findById(userB)).thenReturn(oUserB);
        Mockito.when(userRepository.findById(userC)).thenReturn(oUserC);

        // set up and Mockito for Test Get Draft Program
        List<Syllabus> pro1_syll = new ArrayList<>(List.of(syllabuses.get(0), syllabuses.get(1)));

        Mockito.when(syllabusRepository.findAllSyllabusByProgramID(program1.toString())).thenReturn(pro1_syll);

        // set up and Mockito for Test Get List Draft Program
        List<TrainingProgram> programWithUser = new ArrayList<>(
                List.of(programs.get(0), programs.get(1), programs.get(2)));

        Mockito.when(trainingProgramRepository.findAllByCreatedByAndStatusOrderByCreatedDateDesc(userA, TrainingProgramStatus.DRAFT))
                .thenReturn(programWithUser);

        List<Syllabus> pro2_syll = new ArrayList<>(List.of(syllabuses.get(2)));

        Mockito.when(syllabusRepository.findAllSyllabusByProgramID(program2.toString())).thenReturn(pro2_syll);

        List<Syllabus> pro3_syll = new ArrayList<>(List.of(syllabuses.get(3)));

        Mockito.when(syllabusRepository.findAllSyllabusByProgramID(program3.toString())).thenReturn(pro3_syll);

        // set up and Mockito for Test Search syllabus
        // keyword: Testing
        List<Syllabus> syllabusesWithKey = new ArrayList<>(List.of(syllabuses.get(2), syllabuses.get(4)));
        Mockito.when(syllabusRepository.searchSyllabusByKeyword(keyName, SyllabusStatus.ACTIVE))
                .thenReturn(syllabusesWithKey);

        // keyword: TEstIng
        Mockito.when(syllabusRepository.searchSyllabusByKeyword(keyUpName, SyllabusStatus.ACTIVE))
                .thenReturn(syllabusesWithKey);

        // keyword: Testing 1.
        List<Syllabus> syllabusesWithNameAndVersion = new ArrayList<>(List.of(syllabuses.get(2)));
        Mockito.when(syllabusRepository.searchSyllabusByKeyword(keyNameVersion, SyllabusStatus.ACTIVE))
                .thenReturn(syllabusesWithNameAndVersion);

    }

    // --------------------------------------------------------------
    // Download template file
    @Test
    void test_getTemplateResource_When_None_Expect_DownloadSuccessful() {
        ByteArrayResource file = programSyllabusService.getTemplateResource();
        assertNotEquals(null, file);
    }

    // --------------------------------------------------------------
    // Get Draft Program
    @Test
    void test_getDraftProgram_When_ValidProgramID_Expect_GetDraftProgramSuccessful() {
        // Input program1, userA
        String expectedProgramName = "Fresher Java Developer";
        try {
            DetailDraftProgramResponse programResponse = programSyllabusService.getDraftProgram(program1, userA);
            assertEquals(expectedProgramName, programResponse.getName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_getDraftProgram_When_InvalidProgramID_Expect_ProgramNotFound() {
        // input programNotFound , userA
        String expectedError = "Program Not Found";

        try {
            DetailDraftProgramResponse programResponse = programSyllabusService.getDraftProgram(programNotFound, userA);
            if (Objects.equals(programResponse, null)) {
                fail("Get Failed");
            }
            fail("Get successful");
        } catch (Exception e) {
            assertEquals(expectedError, e.getMessage());
        }

    }

    @Test
    void test_getDraftProgram_When_InvalidProgramID_Expect_ProgramCreatedByOther() {
        // input program1 , userB
        String expectedError = "Program is created by other user";

        try {
            DetailDraftProgramResponse programResponse = programSyllabusService.getDraftProgram(program1, userB);
            if (Objects.equals(programResponse, null)) {
                fail("Get Failed");
            }
            fail("Get successful");
        } catch (Exception e) {
            assertEquals(expectedError, e.getMessage());
        }
    }

    @Test
    void test_getDraftProgram_When_InvalidProgramID_Expect_ProgramNotDraft() {
        // input program4 , userA
        String expectedError = "Program Status is not Draft";

        try {
            DetailDraftProgramResponse programResponse = programSyllabusService.getDraftProgram(program4, userA);
            if (Objects.equals(programResponse, null)) {
                fail("Get Failed");
            }
            fail("Get successful");
        } catch (Exception e) {
            assertEquals(expectedError, e.getMessage());
        }
    }

    // --------------------------------------------------------------
    // Get List Draft Program
    @Test
    void test_getListDraftProgramByUserID_When_None_Expect_GetListSuccessful() {
        // input userA
        int expectedSize = 3;

        assertEquals(expectedSize, programSyllabusService.getListDraftProgramByUserID(userA).size());
    }

    @Test
    void test_getListDraftProgramByUserID_When_None_Expect_Null() {
        // input userC
        assertEquals(Collections.emptyList(), programSyllabusService.getListDraftProgramByUserID(userC));
    }

    // --------------------------------------------------------------
    // Search Syllabus
    @Test
    void test_searchSyllabusByKeyword_When_SyllabusName_Expect_GetListSyllabus() {
        // input keyword (syllabus name)
        int expectedSize = 2;
        assertEquals(expectedSize, programSyllabusService.searchSyllabusByKeyword(keyName).size());
    }

    @Test
    void test_searchSyllabusByKeyword_When_SyllabusUppercaseName_Expect_GetListSyllabus() {
        // input keyword (syllabus Uppercase name)
        int expectedSize = 2;
        assertEquals(expectedSize, programSyllabusService.searchSyllabusByKeyword(keyUpName).size());
    }

    @Test
    void test_searchSyllabusByKeyword_When_SyllabusNameVersion_Expect_GetListSyllabus() {
        // input keyword (syllabus name + version)
        int expectedSize = 1;
        assertEquals(expectedSize, programSyllabusService.searchSyllabusByKeyword(keyNameVersion).size());
    }

    @Test
    void test_searchSyllabusByKeyword_When_SyllabusName_Expect_Null() {
        // input keyword (wrong value)

        assertEquals(Collections.emptyList(), programSyllabusService.searchSyllabusByKeyword(keyNotFound));
    }

    // --------------------------------------------------------------
    // Save Program
    // Save Program - create new
    @Test
    void test_saveCompleteProgram_When_ValidProgramRequestAndNotHaveDraft_Expect_SaveSuccessful() {
        // input
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus1, syllabus2)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            assertEquals(null, response);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_InvalidProgramRequestAndNotHaveDraft_Expect_SyllabusNotActive() {
        // input syllabus4 not active
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus1, syllabus4)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        String expectedMessage = "Syllabus is not Active";

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            String actualMessage = response.getSyllabusData().getSyllabusError().get(0).getMessageError();
            assertEquals(expectedMessage, actualMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_InvalidProgramRequestAndNotHaveDraft_Expect_SyllabusNotFound() {
        // input syllabusNotFound not found
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus1, syllabusNotFound)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);
        // test
        String expectedMessage = "Syllabus not found with ID: " + syllabusNotFound;

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_InvalidProgramRequestAndNotHaveDraft_Expect_SyllabusDuplicateName() {
        // input syllabus5 duplicate name
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus3, syllabus5)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test

        Syllabus syllabusDupl = Syllabus.builder().id(syllabus3).name("JUnit Testing").version("1.0").build();
        String expectedMessage = "Duplicate syllabus " + syllabusDupl.getName() + " version " + syllabusDupl.getVersion();


        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            System.out.println("duplicate Name: " + e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_InvalidProgramRequestAndNotHaveDraft_Expect_SyllabusDuplicateID() {
        // input syllabus1 duplicate
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus1, syllabus1)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test

        Syllabus syllabusDupl = Syllabus.builder().id(syllabus1).name(".Net Basic Program").version("1.0").build();
        String expectedMessage = "Duplicate syllabus " + syllabusDupl.getName() + " version " + syllabusDupl.getVersion();

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            System.out.println("duplicate id " + e.getMessage());
            fail(e.getMessage());
        }
    }

    // Save Program - modify draft
    @Test
    void test_saveCompleteProgram_When_ValidProgramRequestAndModifyDraft_Expect_SaveSuccessful() {
        // input
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus2)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            assertEquals(null, response);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_InvalidProgramRequestAndModifyDraft_Expect_SyllabusNotActive() {
        // input syllabus4 not active
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus4)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        String expectedMessage = "Syllabus is not Active";

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            String actualMessage = response.getSyllabusData().getSyllabusError().get(0).getMessageError();
            assertEquals(expectedMessage, actualMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_InvalidProgramRequestAndModifyDraft_Expect_SyllabusNotFound() {
        // input syllabusNotFound not found
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabusNotFound)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);
        // test
        String expectedMessage = "Syllabus not found with ID: " + syllabusNotFound;

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_InvalidProgramRequestAndModifyDraft_Expect_SyllabusDuplicateName() {
        // input syllabus5 duplicate name
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus3, syllabus5)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        Syllabus syllabusDupl = Syllabus.builder().id(syllabus3).name("JUnit Testing").version("1.0").build();
        String expectedMessage = "Duplicate syllabus " + syllabusDupl.getName() + " version " + syllabusDupl.getVersion();

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            System.out.println("duplicate Name: " + e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_InvalidProgramRequestAndModifyDraft_Expect_SyllabusDuplicateID() {
        // input syllabus1 duplicate
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus1)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test

        Syllabus syllabusDupl = Syllabus.builder().id(syllabus1).name(".Net Basic Program").version("1.0").build();
        String expectedMessage = "Duplicate syllabus " + syllabusDupl.getName() + " version " + syllabusDupl.getVersion();

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            System.out.println("duplicate id " + e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_ValidProgramRequestAndModifyDraft_Expect_ProgramNotFound() {
        // input
        ProgramRequest requestProgram = ProgramRequest.builder().id(programNotFound).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus2)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        String expectedMessage = "Program Not Found";

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            if (Objects.equals(response, null)) {
                fail("Save Successful");
            }
            fail("Save Failed");
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_ValidProgramRequestAndModifyDraft_Expect_ProgramCreatedByOther() {
        // input
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus2)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        String expectedMessage = "Program is created by other user";

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userC);
            if (Objects.equals(response, null)) {
                fail("Save Successful");
            }
            fail("Save Failed");
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void test_saveCompleteProgram_When_ValidProgramRequestAndModifyDraft_Expect_ProgramNotDraft() {
        // input
        ProgramRequest requestProgram = ProgramRequest.builder().id(program4).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus2)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program4).name(nameProgramWithID)
                .status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        String expectedMessage = "Program Status is not Draft";

        try {
            SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
            if (Objects.equals(response, null)) {
                fail("Save Successful");
            }
            fail("Save Failed");
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

//	@Test
//	void test_saveCompleteProgram_When_ValidProgramRequestAndModifyDraft_Expect_ProgramNameNotSame() {
//		// input
//		ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameDuplicate) 
//				.syllabuses(List.of(syllabus1, syllabus2)).build();
//		// set up and Mockito
//		TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
//				.status(TrainingProgramStatus.INACTIVE).createdBy(userA).build();
//		Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);
//
//		// test
//		String expectedMessage = "Program Name is not same";
//
//		try {
//			SaveProgramResponse response = programSyllabusService.saveCompleteProgram(requestProgram, userA);
//			if (Objects.equals(response, null)) {
//				fail("Save Successful");
//			}
//			fail("Save Failed");
//		} catch (Exception e) {
//			assertEquals(expectedMessage, e.getMessage());
//		}
//	}

    // --------------------------------------------------------------
    // Save Draft Program - create new
    @Test
    void test_saveDraftProgram_When_ValidProgramRequestAndNotHaveDraft_Expect_SaveSuccessful() {
        // input
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus1, syllabus2)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            assertEquals(null, response);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_InvalidProgramRequestAndNotHaveDraft_Expect_SyllabusNotActive() {
        // input syllabus4 not active
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus1, syllabus4)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        String expectedMessage = "Syllabus is not Active";

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            String actualMessage = response.getSyllabusData().getSyllabusError().get(0).getMessageError();
            assertEquals(expectedMessage, actualMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_InvalidProgramRequestAndNotHaveDraft_Expect_SyllabusNotFound() {
        // input syllabusNotFound not found
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus1, syllabusNotFound)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);
        // test
        String expectedMessage = "Syllabus not found with ID: " + syllabusNotFound;

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_InvalidProgramRequestAndNotHaveDraft_Expect_SyllabusDuplicateName() {
        // input syllabus5 duplicate name
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus3, syllabus5)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        Syllabus syllabusDupl = Syllabus.builder().id(syllabus3).name("JUnit Testing").version("1.0").build();
        String expectedMessage = "Duplicate syllabus " + syllabusDupl.getName() + " version " + syllabusDupl.getVersion();

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            System.out.println("duplicate Name: " + e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_InvalidProgramRequestAndNotHaveDraft_Expect_SyllabusDuplicateID() {
        // input syllabus1 duplicate
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus1, syllabus1)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        Syllabus syllabusDupl = Syllabus.builder().id(syllabus1).name(".Net Basic Program").version("1.0").build();
        String expectedMessage = "Duplicate syllabus " + syllabusDupl.getName() + " version " + syllabusDupl.getVersion();

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            System.out.println("duplicate id " + e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_ValidProgramRequestAndNotHaveDraft_Expect_OverTenDrafts() {
        // input syllabus1 duplicate
        ProgramRequest requestProgram = ProgramRequest.builder().name(nameProgram)
                .syllabuses(List.of(syllabus1, syllabus1)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(programEdit).name(nameProgram)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // set up for get number of drafts 10
        List<TrainingProgram> listdrafts = new ArrayList<>(List.of(programSave, programSave, programSave, programSave,
                programSave, programSave, programSave, programSave, programSave, programSave));

        Mockito.when(trainingProgramRepository.findAllByCreatedByAndStatusOrderByCreatedDateDesc(userA, TrainingProgramStatus.DRAFT))
                .thenReturn(listdrafts);

        // test
        String expectedMessage = " Admin has more than 10 drafts, you can not create more.";

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            if (Objects.equals(response, null)) {
                fail("Save Succesfful");
            }
            fail("Save Failed");
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    // Save Draft Program - modify draft
    @Test
    void test_saveDraftProgram_When_ValidProgramRequestAndModifyDraft_Expect_SaveSuccessful() {
        // input
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus2)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            assertEquals(null, response);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_InvalidProgramRequestAndModifyDraft_Expect_SyllabusNotActive() {
        // input syllabus4 not active
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus4)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        String expectedMessage = "Syllabus is not Active";

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            String actualMessage = response.getSyllabusData().getSyllabusError().get(0).getMessageError();
            assertEquals(expectedMessage, actualMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_InvalidProgramRequestAndModifyDraft_Expect_SyllabusNotFound() {
        // input syllabusNotFound not found
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabusNotFound)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);
        // test
        String expectedMessage = "Syllabus not found with ID: " + syllabusNotFound;

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_InvalidProgramRequestAndModifyDraft_Expect_SyllabusDuplicateName() {
        // input syllabus5 duplicate name
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus3, syllabus5)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        Syllabus syllabusDupl = Syllabus.builder().id(syllabus3).name("JUnit Testing").version("1.0").build();
        String expectedMessage = "Duplicate syllabus " + syllabusDupl.getName() + " version " + syllabusDupl.getVersion();

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            System.out.println("duplicate Name: " + e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_InvalidProgramRequestAndModifyDraft_Expect_SyllabusDuplicateID() {
        // input syllabus1 duplicate
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus1)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        Syllabus syllabusDupl = Syllabus.builder().id(syllabus1).name(".Net Basic Program").version("1.0").build();
        String expectedMessage = "Duplicate syllabus " + syllabusDupl.getName() + " version " + syllabusDupl.getVersion();

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            assertEquals(expectedMessage, response.getSyllabusData().getSyllabusError().get(0).getMessageError());
        } catch (Exception e) {
            System.out.println("duplicate id " + e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_ValidProgramRequestAndModifyDraft_Expect_ProgramNotFound() {
        // input
        ProgramRequest requestProgram = ProgramRequest.builder().id(programNotFound).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus2)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        String expectedMessage = "Program Not Found";

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            if (Objects.equals(response, null)) {
                fail("Save Successful");
            }
            fail("Save Failed");
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_ValidProgramRequestAndModifyDraft_Expect_ProgramCreatedByOther() {
        // input
        ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus2)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        String expectedMessage = "Program is created by other user";

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userC);
            if (Objects.equals(response, null)) {
                fail("Save Successful");
            }
            fail("Save Failed");
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void test_saveDraftProgram_When_ValidProgramRequestAndModifyDraft_Expect_ProgramNotDraft() {
        // input
        ProgramRequest requestProgram = ProgramRequest.builder().id(program4).name(nameProgramWithID)
                .syllabuses(List.of(syllabus1, syllabus2)).build();
        // set up and Mockito
        TrainingProgram programSave = TrainingProgram.builder().id(program4).name(nameProgramWithID)
                .status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
        Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);

        // test
        String expectedMessage = "Program Status is not Draft";

        try {
            SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
            if (Objects.equals(response, null)) {
                fail("Save Successful");
            }
            fail("Save Failed");
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

//	@Test
//	void test_saveDraftProgram_When_ValidProgramRequestAndModifyDraft_Expect_ProgramNameNotSame() {
//		// input
//		ProgramRequest requestProgram = ProgramRequest.builder().id(program3).name(nameDuplicate) 
//				.syllabuses(List.of(syllabus1, syllabus2)).build();
//		// set up and Mockito
//		TrainingProgram programSave = TrainingProgram.builder().id(program3).name(nameProgramWithID)
//				.status(TrainingProgramStatus.DRAFT).createdBy(userA).build();
//		Mockito.when(trainingProgramRepository.save(programSave)).thenReturn(programSave);
//
//		// test
//		String expectedMessage = "Program Name is not same";
//
//		try {
//			SaveProgramResponse response = programSyllabusService.saveDraftProgram(requestProgram, userA);
//			if (Objects.equals(response, null)) {
//				fail("Save Successful");
//			}
//			fail("Save Failed");
//		} catch (Exception e) {
//			assertEquals(expectedMessage, e.getMessage());
//		}
//	}

    // --------------------------------------------------------------
    // Delete Draft Program
    @Test
    void test_deleteDraftProgram_When_ValidProgramID_Expect_DeleteSuccessful() {
        // input programID, userID
        boolean expectedCase = true;
        try {
            boolean actualCase = programSyllabusService.deleteDraftProgram(userA, program1);
            assertEquals(expectedCase, actualCase);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_deleteDraftProgram_When_InvalidProgramID_Expect_ProgramNotFound() {
        // input programNotFound, userA
        String expectedMessager = "Program Not Found";
        try {
            programSyllabusService.deleteDraftProgram(userA, programNotFound);
            fail("Delete Successful");
        } catch (Exception e) {
            assertEquals(expectedMessager, e.getMessage());
        }
    }

    @Test
    void test_deleteDraftProgram_When_InvalidProgramID_Expect_ProgramNotDraft() {
        // input program4, userA
        String expectedMessager = "Program Status is not Draft";
        try {
            programSyllabusService.deleteDraftProgram(userA, program4);
            fail("Delete Successful");
        } catch (Exception e) {
            assertEquals(expectedMessager, e.getMessage());
        }
    }

    @Test
    void test_deleteDraftProgram_When_InvalidProgramID_Expect_ProgramCreatedByOther() {
        // input program1, userC
        String expectedMessager = "Program is created by other user";
        try {
            programSyllabusService.deleteDraftProgram(userC, program1);
            fail("Delete Successful");
        } catch (Exception e) {
            assertEquals(expectedMessager, e.getMessage());
        }
    }


    /**
     * <p>
     * Import training program by file
     * Test import file when provide wrong file type
     * </p>
     *
     * @author Nguyen Quoc Bao
     */
    @Test
    void test_saveListOfTrainingProgram_When_Provide_Incorect_File_Format() {
        String expectedMessager = "Invalid file input, file input must be under 1 MB and corect file excel format(xlsx)";
        String fileType = "MediaType.TEXT_PLAIN_VALUE";
        byte[] fileSize = new byte[1024];
        String fileName = "fileTest";
        String fullFileName = "fileTest.txt";
        try {
            MultipartFile file = new MockMultipartFile(fileName, fullFileName, fileType, fileSize);
            programSyllabusService.readTrainingProgram(file);
        } catch (Exception e) {
            assertEquals(expectedMessager, e.getMessage());
        }

    }


    /**
     * <p>
     * Test import file when provide file size > 1MB
     * </p>
     *
     * @author Nguyen Quoc Bao
     */
    @Test
    void test_saveListOfTrainingProgram_When_Provide_File_Size_More_Than_1MB() {
        String expectedMessager = "Invalid file input, file input must be under 1 MB and corect file excel format(xlsx)";
        byte[] fileSize = new byte[1024 * 1024 * 1];
        String fileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String fileName = "fileTest";
        String fullFileName = "fileTest.xlsx";
        try {
            MultipartFile file = new MockMultipartFile(fileName, fullFileName, fileType, fileSize);
            programSyllabusService.readTrainingProgram(file);
        } catch (Exception e) {
            assertEquals(expectedMessager, e.getMessage());
        }
    }


    /**
     * <p>
     * Test import file when provide file with correct format
     * </p>
     *
     * @author Nguyen Quoc Bao
     */
    @Test
    void test_saveListOfTrainingProgram_When_Provide_File_With_Correct_Format() {
        byte[] fileSize = new byte[1024 * 10];
        String fileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String fileName = "fileTest";
        String fullFileName = "fileTest.xlsx";
        // configure SaveProgramResponse Object

        // add syllabus data ok object, 11 object
        List<SyllabusResponse> syllabusOk = List.of(
                SyllabusResponse.builder().id(program1).name("program1").days(2).hours(10).build(),
                SyllabusResponse.builder().id(program2).name("program2").days(2).hours(10).build(),
                SyllabusResponse.builder().id(program3).name("program3").days(2).hours(10).build(),
                SyllabusResponse.builder().id(program4).name("program4").days(2).hours(10).build(),
                SyllabusResponse.builder().id(program1).name("program5").days(2).hours(10).build());

        List<SyllabusErrorResponse> syllabusDataError = new ArrayList<>();
        SyllabusAndError syllabusData = new SyllabusAndError();
        syllabusData.setSyllabusOk(syllabusOk);
        syllabusData.setSyllabusError(syllabusDataError);
        SaveProgramResponse programTest = SaveProgramResponse.builder().id(program1).name("ProgramImportByFile")
                .nameCreatedBy(userA.toString()).createdOn(new Date()).syllabusData(syllabusData)
                .status(TrainingProgramStatus.INACTIVE.toString()).build();
        MultipartFile file = new MockMultipartFile(fileName, fullFileName, fileType, fileSize);

        try {

            Mockito.when(programSyllabusService.saveTrainingProgram(programTest)).thenReturn(programTest);

            SaveProgramResponse programResponse = programSyllabusService.readTrainingProgram(file);
            assertEquals(programTest.getId().toString(), programResponse.getId().toString());
            assertEquals(programTest.getName(), programResponse.getName());
            assertEquals(0, programResponse.getSyllabusData().getSyllabusError().size());
            assertEquals(syllabusOk.size(), programResponse.getSyllabusData().getSyllabusOk().size());
            assertEquals(programTest.getStatus(), programResponse.getStatus());

        } catch (Exception e) {

            log.error(e.getMessage());

        }
    }


    /**
     * <p>
     * Test if file import has more than 10 syllabus
     * </p>
     *
     * @throws IOException
     * @throws InvalidCreateProgramException
     * @author Nguyen Quoc Bao
     */
    @Test
    void test_saveListOfTrainingProgram_When_Provide_File_With_More_than_10_Syllabus() throws IOException, InvalidCreateProgramException {

        String expectedMsg = "Training Program can not has more than 10 syllabus";

        // set up List Syllabus, size = 11
        List<SyllabusResponse> syllabusOk = new ArrayList<>(List.of(

                SyllabusResponse.builder().id(syllabus1).name(".Net Basic Program").code("NBP").days(2)
                        .hours(10).version("1.0").build(),

                SyllabusResponse.builder().id(syllabus2).name("Azure DevOps").code("ADO").days(2)
                        .hours(10).version("1.0").build(),

                SyllabusResponse.builder().id(syllabus3).name("JUnit Testing").code("FSJ").days(2)
                        .hours(10).version("1.0").build(),

                SyllabusResponse.builder().id(syllabus4).name("JUnit Testing").code("JUT").days(2)
                        .hours(10).version("1.1").build(),

                SyllabusResponse.builder().id(syllabus1).name("JUnit Testing").code("JUT").days(2)
                        .hours(10).version("3.0").build(),

                SyllabusResponse.builder().id(syllabus2).name("JUnit Testing").code("JUT").days(2)
                        .hours(10).version("3.0").build(),

                SyllabusResponse.builder().id(syllabus3).name("JUnit Testing").code("JUT").days(2)
                        .hours(10).version("3.0").build(),

                SyllabusResponse.builder().id(syllabus4).name("JUnit Testing").code("JUT").days(2)
                        .hours(10).version("3.0").build(),

                SyllabusResponse.builder().id(syllabus5).name("JUnit Testing").code("JUT").days(2)
                        .hours(10).version("3.0").build(),

                SyllabusResponse.builder().id(syllabus1).name("JUnit Testing").code("JUT").days(2)
                        .hours(10).version("3.0").build(),

                SyllabusResponse.builder().id(syllabus2).name("JUnit Testing").code("JUT").days(2)
                        .hours(10).version("3.0").build()

        ));

        List<SyllabusErrorResponse> syllabusDataError = new ArrayList<>();
        SyllabusAndError syllabusData = new SyllabusAndError();
        syllabusData.setSyllabusOk(syllabusOk);
        syllabusData.setSyllabusError(syllabusDataError);
        SaveProgramResponse programTest = SaveProgramResponse.builder().id(program1).name("ProgramImportByFile")
                .nameCreatedBy(userA.toString()).createdOn(new Date()).syllabusData(syllabusData)
                .status(TrainingProgramStatus.INACTIVE.toString()).build();

        try {

            programSyllabusService.saveTrainingProgram(programTest);

        } catch (InvalidCreateProgramException e) {

            assertEquals(expectedMsg, e.getMessage());

        }
    }


    /**
     * <p>
     * Test import file when provide file with correct format, and training program
     * name not has number character from 5 to 100
     * </p>
     *
     * @author ADMIN
     */
    @Test
    void test_saveListOfTrainingProgram_When_TrainingProgram_Given_Wrong_Name_Length() {

        String expectedMsg = "Invalid name, name of training program must be from 5 to 100 character";
        List<SyllabusErrorResponse> syllabusDataError = new ArrayList<>();
        SyllabusAndError syllabusData = new SyllabusAndError();
        List<SyllabusResponse> syllabusOk = new ArrayList<>();
        syllabusData.setSyllabusOk(syllabusOk);
        syllabusData.setSyllabusError(syllabusDataError);
        SaveProgramResponse programTest = SaveProgramResponse.builder().id(program1).name("abcd")
                .nameCreatedBy(userA.toString()).createdOn(new Date()).syllabusData(syllabusData)
                .status(TrainingProgramStatus.INACTIVE.toString()).build();
        try {
            programSyllabusService.saveTrainingProgram(programTest);


        } catch (InvalidCreateProgramException e) {
            assertEquals(expectedMsg, e.getMessage());
        }
    }


    /**
     * <p>
     * Test import file when provide file with correct format, and training program
     * wrong syllabus list was provided
     * </p>
     *
     * @author Nguyen Quoc Bao
     */
    @Test
    void test_saveListOfTrainingProgram_When_TrainingProgram_Not_Found_Syllabus() {
        String fullFileName = "file_test_for_can_not_found_syllabus_available.xlsx";
        String fileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        File templateFile = new File("src\\test\\resources\\TemplateFileForTestCase\\" + fullFileName);
        Path path = Paths.get(templateFile.getAbsolutePath());
        String fileAddress = path.toAbsolutePath().toString();
        String expectedMsg = "Cannot find available syllabus";

        MultipartFile file = null;
        FileOutputStream outputStream = null;
        FileInputStream fis = null;
        XSSFWorkbook workbook = null;

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("fileAddress");
            // custom for file import
            // set up field name of training program
            XSSFRow rowStoreNameVal = sheet.createRow(0);
            XSSFCell cellStoreNameVal = rowStoreNameVal.createCell(0);
            cellStoreNameVal.setCellValue("Kali linux for hacker");
            // set up field header of training program
            XSSFRow rowHeader = sheet.createRow(1);
            XSSFCell cellSyllabusNameHeader = rowHeader.createCell(0);
            XSSFCell cellSyllabusCodeHeader = rowHeader.createCell(1);
            XSSFCell cellVersionHeader = rowHeader.createCell(2);
            cellSyllabusNameHeader.setCellValue("Syllabus Name");
            cellSyllabusCodeHeader.setCellValue("Syllabus Code");
            cellVersionHeader.setCellValue("Version");

            // set up data
            XSSFRow rowData = sheet.createRow(2);
            XSSFCell cellSyllabusNameData = rowData.createCell(0);
            XSSFCell cellSyllabusCodeData = rowData.createCell(1);
            XSSFCell cellVersionData = rowData.createCell(2);
            cellSyllabusNameData.setCellValue("Kali linux command");
            cellSyllabusCodeData.setCellValue("Code");
            cellVersionData.setCellValue("");


            outputStream = new FileOutputStream(fileAddress);
            workbook.write(outputStream);
            fis = new FileInputStream(fileAddress);
            file = new MockMultipartFile(fileAddress, fileAddress, fileType, fis);
            SaveProgramResponse testProgram = programSyllabusService.readTrainingProgram(file);

            assertEquals(expectedMsg, testProgram.getSyllabusData().getSyllabusError().get(0).getMessageError());

            outputStream.close();
            workbook.close();
            fis.close();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    /**
     * <p>
     * Test import file when provide file with correct format, and training program
     * wrong syllabus list was provided
     * </p>
     *
     * @author Nguyen Quoc Bao
     */
    @Test
    void test_saveListOfTrainingProgram_When_TrainingProgram_Duplicated_Syllabus() {
        String fullFileName = "file_test_for_can_not_found_syllabus_available.xlsx";
        String fileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        File templateFile = new File("src\\test\\resources\\TemplateFileForTestCase\\" + fullFileName);
        Path path = Paths.get(templateFile.getAbsolutePath());
        String fileAddress = path.toAbsolutePath().toString();


        MultipartFile file = null;
        FileOutputStream outputStream = null;
        FileInputStream fis = null;
        XSSFWorkbook workbook = null;

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("fileAddress");
            // custom for file import
            // set up field name of training program
            XSSFRow rowStoreNameVal = sheet.createRow(0);
            XSSFCell cellStoreNameVal = rowStoreNameVal.createCell(0);
            cellStoreNameVal.setCellValue("Kali linux for hacker");
            // set up field header of training program
            XSSFRow rowHeader = sheet.createRow(1);
            XSSFCell cellSyllabusNameHeader = rowHeader.createCell(0);
            XSSFCell cellSyllabusCodeHeader = rowHeader.createCell(1);
            XSSFCell cellVersionHeader = rowHeader.createCell(2);
            cellSyllabusNameHeader.setCellValue("Syllabus Name");
            cellSyllabusCodeHeader.setCellValue("Syllabus Code");
            cellVersionHeader.setCellValue("Version");

            // set up data
            XSSFRow rowData = sheet.createRow(2);
            XSSFCell cellSyllabusNameData = rowData.createCell(0);
            XSSFCell cellSyllabusCodeData = rowData.createCell(1);
            XSSFCell cellVersionData = rowData.createCell(2);
            cellSyllabusNameData.setCellValue("Kali linux command");
            cellSyllabusCodeData.setCellValue("Code");
            cellVersionData.setCellValue("");

            // set up data
            XSSFRow rowDataDuplicated = sheet.createRow(3);
            XSSFCell cellSyllabusNameDataDuplicated = rowDataDuplicated.createCell(0);
            XSSFCell cellSyllabusCodeDataDuplicated = rowDataDuplicated.createCell(1);
            XSSFCell cellVersionDataDuplicated = rowDataDuplicated.createCell(2);
            cellSyllabusNameDataDuplicated.setCellValue("Kali linux command");
            cellSyllabusCodeDataDuplicated.setCellValue("Code");
            cellVersionDataDuplicated.setCellValue("");

            // write data test into file
            outputStream = new FileOutputStream(fileAddress);
            workbook.write(outputStream);
            fis = new FileInputStream(fileAddress);
            outputStream.close();
            workbook.close();

            // testing
            file = new MockMultipartFile(fileAddress, fileAddress, fileType, fis);
            Syllabus data = Syllabus.builder().name("Kali linux command")
                    .code("Code").status(SyllabusStatus.ACTIVE).version("1.0")
                    .build();
            // expected message
            String expectedMsg = "[Duplicated syllabus " + "'" + data.getName() + "'" + " at version: " + data.getVersion() + "]";
            Mockito.when(programSyllabusJDBC.findAvailableSyllabus(any())).thenReturn(Optional.of(data));

            SaveProgramResponse testProgram = programSyllabusService.readTrainingProgram(file);

            assertEquals(1, testProgram.getSyllabusData().getSyllabusError().size());
            assertEquals(expectedMsg, testProgram.getSyllabusData().getSyllabusError().get(0).getMessageError());
            fis.close();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * <p>
     * Test import traing program when syllabus provided is has Status = DEACTIVE
     * </p>
     *
     * @author Nguyen Quoc Bao
     */

    @Test
    void test_saveListOfTrainingProgram_When_Syllabus_Input_Is_DEACTICE() {
        String fullFileName = "file_test_for_if_syllabus_deactive.xlsx";
        String fileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        File templateFile = new File("src\\test\\resources\\TemplateFileForTestCase\\" + fullFileName);
        Path path = Paths.get(templateFile.getAbsolutePath());
        String fileAddress = path.toAbsolutePath().toString();
        String expectedMsg = "[Syllabus status is inactive]";

        MultipartFile file = null;
        FileOutputStream outputStream = null;
        FileInputStream fis = null;
        XSSFWorkbook workbook = null;

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("fileAddress");
            // custom for file import
            // set up field name of training program
            XSSFRow rowStoreNameVal = sheet.createRow(0);
            XSSFCell cellStoreNameVal = rowStoreNameVal.createCell(0);
            cellStoreNameVal.setCellValue("Kali linux for hacker");
            // set up field header of training program
            XSSFRow rowHeader = sheet.createRow(1);
            XSSFCell cellSyllabusNameHeader = rowHeader.createCell(0);
            XSSFCell cellSyllabusCodeHeader = rowHeader.createCell(1);
            XSSFCell cellVersionHeader = rowHeader.createCell(2);
            cellSyllabusNameHeader.setCellValue("Syllabus Name");
            cellSyllabusCodeHeader.setCellValue("Syllabus Code");
            cellVersionHeader.setCellValue("Version");

            // set up data
            XSSFRow rowData = sheet.createRow(2);
            XSSFCell cellSyllabusNameData = rowData.createCell(0);
            XSSFCell cellSyllabusCodeData = rowData.createCell(1);
            XSSFCell cellVersionData = rowData.createCell(2);
            cellSyllabusNameData.setCellValue("Kali linux command");
            cellSyllabusCodeData.setCellValue("Code");
            cellVersionData.setCellValue("");


            //set value of mock data
            Syllabus data = Syllabus.builder().name("Kali linux command")
                    .code("Code").status(SyllabusStatus.DEACTIVE).version("1.0")
                    .build();
            outputStream = new FileOutputStream(fileAddress);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            fis = new FileInputStream(fileAddress);
            file = new MockMultipartFile(fileAddress, fileAddress, fileType, fis);
            Mockito.when(programSyllabusJDBC.findAvailableSyllabus(any())).thenReturn(Optional.of(data));
            SaveProgramResponse testProgram = programSyllabusService.readTrainingProgram(file);

            assertEquals(expectedMsg, testProgram.getSyllabusData().getSyllabusError().get(0).getMessageError());


            fis.close();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
