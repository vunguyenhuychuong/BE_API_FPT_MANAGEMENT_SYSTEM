package com.java8.tms.syllabus.service;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.*;
import com.java8.tms.common.meta.MaterialStatus;
import com.java8.tms.common.meta.SyllabusDayStatus;
import com.java8.tms.common.meta.SyllabusStatus;
import com.java8.tms.common.repository.*;
import com.java8.tms.syllabus.dto.FormSyllabusDTO;
import com.java8.tms.syllabus.dto.RequestForListOfSyllabus;
import com.java8.tms.syllabus.dto.ResponseForListOfSyllabus;
import com.java8.tms.syllabus.dto.SyllabusDTO;
import com.java8.tms.syllabus.dto.*;
import com.java8.tms.syllabus.jdbc.SyllabusJdbc;
import com.java8.tms.syllabus.service.impl.SyllabusServiceImpl;
import com.java8.tms.syllabus.service.impl.SyllabusServiceUtil;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Log4j2
public class SyllabusServiceTest {
    // Setup metadata
    private final UUID syllabusLevelId1 = UUID.randomUUID();
    private final UUID syllabusLevelId2 = UUID.randomUUID();
    private final UUID syllabusLevelId3 = UUID.randomUUID();
    private final UUID outputStandardId1 = UUID.randomUUID();
    private final UUID outputStandardId2 = UUID.randomUUID();
    private final UUID outputStandardId3 = UUID.randomUUID();
    private final UUID deliveryTypeId1 = UUID.randomUUID();
    private final UUID deliveryTypeId2 = UUID.randomUUID();
    private final UUID deliveryTypeId3 = UUID.randomUUID();
    private final UUID deliveryTypeId4 = UUID.randomUUID();
    private final UUID deliveryTypeId5 = UUID.randomUUID();
    private final UUID deliveryTypeId6 = UUID.randomUUID();

    // Set up user ID
    private final UUID userB = UUID.randomUUID(); // user create syllabus
    private final UUID userA = UUID.randomUUID();
    private final UUID userC = UUID.randomUUID();
    private final UUID syllabus1 = UUID.randomUUID();
    private final UUID syllabus2 = UUID.randomUUID();
    private final UUID syllabus3 = UUID.randomUUID();
    private final UUID syllabus4 = UUID.randomUUID();
    private final UUID syllabus5 = UUID.randomUUID();
    private final UUID syllabusDeleted = UUID.randomUUID();
    private final UUID syllabusNotFound = UUID.randomUUID();
    private final UUID syllabusDraft = UUID.randomUUID();
    private final UUID syllabusDraft2 = UUID.randomUUID();
    private final List<Syllabus> syllabuses = new ArrayList<>(List.of(
            Syllabus.builder().id(syllabus1).name(".Net Basic Program").code("NBP").createdBy(userB).days(2)
                    .createdDate(new Date(2020, 11, 11)).hours(10).status(SyllabusStatus.ACTIVE).updatedBy(userB).version("1.0").build(),
            Syllabus.builder().id(syllabus2).name("Azure DevOps").code("ADO").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2020, 12, 20)).status(SyllabusStatus.ACTIVE).updatedBy(userB).version("1.0").build(),
            Syllabus.builder().id(syllabus3).name("JUnit Testing").code("FSJ").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2019, 10, 10)).status(SyllabusStatus.ACTIVE).updatedBy(userB).version("1.0").build(),
            Syllabus.builder().id(syllabus4).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2011, 20, 20)).status(SyllabusStatus.DEACTIVE).updatedBy(userB).version("1.1").build(),
            Syllabus.builder().id(syllabus5).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2017, 9, 8)).status(SyllabusStatus.ACTIVE).updatedBy(userB).version("3.0").build()
    ));


    //    private OutputStandard outputStandard = new OutputStandard(UUID.randomUUID(), "Coding convention", "H4SD", "None", null);
//    private DeliveryType deliveryType = new DeliveryType(UUID.randomUUID(), "Assignment/Lab", "None", null);
    @MockBean
    private SyllabusRepository syllabusRepository;
    @Autowired
    private ModelMapper modelMapper;
    @MockBean
    private SyllabusLevelRepository syllabusLevelRepository;
    @MockBean
    private OutputStandardRepository outputStandardRepository;
    @MockBean
    private DeliveryTypeRepository deliveryTypeRepository;
    @Autowired
    private SyllabusServiceImpl syllabusService;
    @MockBean
    private SyllabusJdbc syllabusJdbc;
    @MockBean
    private SyllabusServiceUtil syllabusServiceUtil;
    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        List<SyllabusLevel> syllabusLevels = new ArrayList<>(List.of(
                SyllabusLevel.builder().id(syllabusLevelId1).name("Basic").build(),
                SyllabusLevel.builder().id(syllabusLevelId2).name("Advanced").build(),
                SyllabusLevel.builder().id(syllabusLevelId3).name("Intermediate").build()
        ));

        List<OutputStandard> outputStandards = new ArrayList<>(List.of(
                OutputStandard.builder().id(outputStandardId1).code("H4SD").name("Coding convention").description("None").build(),
                OutputStandard.builder().id(outputStandardId2).code("K4SD").name("Comment convention").description("None").build(),
                OutputStandard.builder().id(outputStandardId3).code("K5SD").name("Testing convention").description("None").build()
        ));

        List<DeliveryType> deliveryTypes = new ArrayList<>(List.of(
                DeliveryType.builder().id(deliveryTypeId1).name("Assignment/Lab").description("None").build(),
                DeliveryType.builder().id(deliveryTypeId2).name("Concept/Lecture").description("None").build(),
                DeliveryType.builder().id(deliveryTypeId3).name("Guide/Review").description("None").build(),
                DeliveryType.builder().id(deliveryTypeId4).name("Test/Quiz").description("None").build(),
                DeliveryType.builder().id(deliveryTypeId5).name("Exam").description("None").build(),
                DeliveryType.builder().id(deliveryTypeId6).name("Seminar/Workshop").description("None").build()
        ));

        // Setup Optional for syllabus level
        Optional<SyllabusLevel> oSyllabusLevel1 = Optional.ofNullable(syllabusLevels.get(0));
        Optional<SyllabusLevel> oSyllabusLevel2 = Optional.ofNullable(syllabusLevels.get(1));
        Optional<SyllabusLevel> oSyllabusLevel3 = Optional.ofNullable(syllabusLevels.get(2));

        // Mockito to syllabus level
        Mockito.when(syllabusLevelRepository.findById(syllabusLevelId1)).thenReturn(oSyllabusLevel1);
        Mockito.when(syllabusLevelRepository.findById(syllabusLevelId2)).thenReturn(oSyllabusLevel2);
        Mockito.when(syllabusLevelRepository.findById(syllabusLevelId3)).thenReturn(oSyllabusLevel3);


        // Setup Optional for output standard
        Optional<OutputStandard> oOutputStandard1 = Optional.ofNullable(outputStandards.get(0));
        Optional<OutputStandard> oOutputStandard2 = Optional.ofNullable(outputStandards.get(1));
        Optional<OutputStandard> oOutputStandard3 = Optional.ofNullable(outputStandards.get(2));

        // Mockito to output standard
        Mockito.when(outputStandardRepository.findById(outputStandardId1)).thenReturn(oOutputStandard1);
        Mockito.when(outputStandardRepository.findById(outputStandardId2)).thenReturn(oOutputStandard2);
        Mockito.when(outputStandardRepository.findById(outputStandardId3)).thenReturn(oOutputStandard3);


        // Setup Optional for delivery type
        Optional<DeliveryType> oDeliveryType1 = Optional.ofNullable(deliveryTypes.get(0));
        Optional<DeliveryType> oDeliveryType2 = Optional.ofNullable(deliveryTypes.get(1));
        Optional<DeliveryType> oDeliveryType3 = Optional.ofNullable(deliveryTypes.get(2));
        Optional<DeliveryType> oDeliveryType4 = Optional.ofNullable(deliveryTypes.get(3));
        Optional<DeliveryType> oDeliveryType5 = Optional.ofNullable(deliveryTypes.get(4));
        Optional<DeliveryType> oDeliveryType6 = Optional.ofNullable(deliveryTypes.get(5));

        // Mockito to delivery type
        Mockito.when(deliveryTypeRepository.findById(deliveryTypeId1)).thenReturn(oDeliveryType1);
        Mockito.when(deliveryTypeRepository.findById(deliveryTypeId2)).thenReturn(oDeliveryType2);
        Mockito.when(deliveryTypeRepository.findById(deliveryTypeId3)).thenReturn(oDeliveryType3);
        Mockito.when(deliveryTypeRepository.findById(deliveryTypeId4)).thenReturn(oDeliveryType4);
        Mockito.when(deliveryTypeRepository.findById(deliveryTypeId5)).thenReturn(oDeliveryType5);
        Mockito.when(deliveryTypeRepository.findById(deliveryTypeId6)).thenReturn(oDeliveryType6);

        List<User> users = new ArrayList<>(List.of(
                User.builder().id(userA).fullname("Luu").build(),

                User.builder().id(userB).fullname("Thanh").build(),

                User.builder().id(userC).fullname("Huy").build()
        ));

        Mockito.when(syllabusRepository.findSyllabusById(syllabus1)).thenReturn(syllabuses.get(0));
        Mockito.when(syllabusRepository.findSyllabusById(syllabus2)).thenReturn(syllabuses.get(1));
        Mockito.when(syllabusRepository.findSyllabusById(syllabus3)).thenReturn(syllabuses.get(2));
        Mockito.when(syllabusRepository.findSyllabusById(syllabus4)).thenReturn(syllabuses.get(3));
        Mockito.when(syllabusRepository.findSyllabusById(syllabus5)).thenReturn(syllabuses.get(4));

        Optional<User> userOptionalA = Optional.ofNullable(users.get(0));
        Optional<User> userOptionalB = Optional.ofNullable(users.get(1));
        Optional<User> userOptionalC = Optional.ofNullable(users.get(2));
        Mockito.when(userRepository.findById(userA)).thenReturn(userOptionalA);
        Mockito.when(userRepository.findById(userB)).thenReturn(userOptionalB);
        Mockito.when(userRepository.findById(userC)).thenReturn(userOptionalC);
    }

    // --------------------------------------------------------------
    // Create new syllabus
    @Test
    void test_createSyllabus_Expect_CreateSuccessful() {
        //input

        Material material = Material.builder().name("Material").materialStatus(MaterialStatus.ACTIVE).createdDate(new Date())
                .updatedDate(new Date()).createdBy(UUID.randomUUID()).updatedBy(UUID.randomUUID()).build();
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        SyllabusUnitChapter syllabusUnitChapter1 = SyllabusUnitChapter.builder().isOnline(true).materials(materialList)
                .outputStandard(outputStandardRepository.findById(outputStandardId1).get())
                .deliveryType(deliveryTypeRepository.findById(deliveryTypeId1).get()).build();
        List<SyllabusUnitChapter> syllabusUnitChapterList = new ArrayList<>();
        syllabusUnitChapterList.add(syllabusUnitChapter1);

        SyllabusUnit syllabusUnit1 = SyllabusUnit.builder().unitNo(1).duration(10).syllabusUnitChapters(syllabusUnitChapterList).build();
        List<SyllabusUnit> syllabusUnitList = new ArrayList<>();
        syllabusUnitList.add(syllabusUnit1);

        SyllabusDay syllabusDay1 = SyllabusDay.builder().dayNo(1).status(SyllabusDayStatus.AVAILABLE).syllabusUnits(syllabusUnitList).build();
        List<SyllabusDayDTOWithoutId> syllabusDayList = new ArrayList<>();
        syllabusDayList.add(modelMapper.map(syllabusDay1, SyllabusDayDTOWithoutId.class));

        AssessmentSchemeDTO assessmentScheme1 = AssessmentSchemeDTO.builder().assignment(15.0).quiz(15.0).gpa(70.0)
                .finalPoint(70.0).finalTheory(40.0).finalPractice(60.0).build();

        FormSyllabusDTOWithoutId requestSyllabus = FormSyllabusDTOWithoutId.builder().name(".NET Programming Language").code("NPL")
                .syllabusDays(syllabusDayList).days(2).hours(10).version("1.0").assessmentScheme(assessmentScheme1).attendeeNumber(20)
                .syllabusLevel(modelMapper.map(syllabusLevelRepository.findById(syllabusLevelId1).get(), SyllabusLevelDTO.class)).build();

        // test
        try {
            SyllabusDTO response = syllabusService.createSyllabus(requestSyllabus);
            assertNotNull(response, "Response not null");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_createSyllabus_Expect_RequestResponseIsTheSame() {
        //input

        Material material = Material.builder().name("Material").materialStatus(MaterialStatus.ACTIVE).createdDate(new Date())
                .updatedDate(new Date()).createdBy(UUID.randomUUID()).updatedBy(UUID.randomUUID()).build();
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        SyllabusUnitChapter syllabusUnitChapter1 = SyllabusUnitChapter.builder().isOnline(true).materials(materialList)
                .outputStandard(null)
                .deliveryType(null).build();
        List<SyllabusUnitChapter> syllabusUnitChapterList = new ArrayList<>();
        syllabusUnitChapterList.add(syllabusUnitChapter1);

        SyllabusUnit syllabusUnit1 = SyllabusUnit.builder().unitNo(1).duration(10).syllabusUnitChapters(syllabusUnitChapterList).build();
        List<SyllabusUnit> syllabusUnitList = new ArrayList<>();
        syllabusUnitList.add(syllabusUnit1);

        SyllabusDay syllabusDay1 = SyllabusDay.builder().dayNo(1).status(SyllabusDayStatus.AVAILABLE).syllabusUnits(syllabusUnitList).build();
        List<SyllabusDayDTOWithoutId> syllabusDayList = new ArrayList<>();
        List<SyllabusDay> syllabusDays = new ArrayList<>();
        syllabusDayList.add(modelMapper.map(syllabusDay1, SyllabusDayDTOWithoutId.class));
        syllabusDays.add(syllabusDay1);

        AssessmentSchemeDTO assessmentScheme1 = AssessmentSchemeDTO.builder().assignment(15.0).quiz(15.0).gpa(70.0)
                .finalPoint(70.0).finalTheory(40.0).finalPractice(60.0).build();

        FormSyllabusDTOWithoutId requestSyllabus = FormSyllabusDTOWithoutId.builder().name(".NET Programming Language").code("NPL")
                .syllabusDays(syllabusDayList).days(2).hours(10).version("1.0").assessmentScheme(assessmentScheme1).attendeeNumber(20)
                .syllabusLevel(modelMapper.map(syllabusLevelRepository.findById(syllabusLevelId1).get(), SyllabusLevelDTO.class)).build();

        Syllabus syllabus = Syllabus.builder().name(".NET Programming Language").code("NPL")
                .syllabusDays(syllabusDays).days(2).hours(10).status(SyllabusStatus.PENDING).createdBy(userB)
                .updatedBy(userB).version("1.0").assessmentScheme(modelMapper.map(assessmentScheme1, AssessmentScheme.class)).attendeeNumber(20)
                .syllabusLevel(syllabusLevelRepository.findById(syllabusLevelId1).get())
                .createdDate(new Date()).updatedDate(new Date()).build();

        Mockito.when(syllabusRepository.save(syllabus)).thenReturn(syllabus);

        // test
        try {
            SyllabusDTO response = syllabusService.createSyllabus(requestSyllabus);
            assertEquals(syllabus, response);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_createSyllabus_Expect_NotFoundSyllabusUnit() {
        //input

//        Material material = Material.builder().name("Material").materialStatus(MaterialStatus.ACTIVE).createdDate(new Date())
//                .updatedDate(new Date()).createdBy(UUID.randomUUID()).updatedBy(UUID.randomUUID()).build();
//        List<Material> materialList = new ArrayList<>();
//        materialList.add(material);
//
//        SyllabusUnitChapter syllabusUnitChapter1 = SyllabusUnitChapter.builder().isOnline(true).materials(materialList)
//                .outputStandard(outputStandardRepository.findById(outputStandardId1).get())
//                .deliveryType(deliveryTypeRepository.findById(deliveryTypeId1).get()).build();
//        List<SyllabusUnitChapter> syllabusUnitChapterList = new ArrayList<>();
//        syllabusUnitChapterList.add(syllabusUnitChapter1);
//
//        SyllabusUnit syllabusUnit1 = SyllabusUnit.builder().unitNo(1).duration(10).syllabusUnitChapters(syllabusUnitChapterList).build();
//        List<SyllabusUnit> syllabusUnitList = new ArrayList<>();
//        syllabusUnitList.add(syllabusUnit1);

        SyllabusDay syllabusDay1 = SyllabusDay.builder().dayNo(1).status(SyllabusDayStatus.AVAILABLE).syllabusUnits(null).build();
        List<SyllabusDayDTOWithoutId> syllabusDayList = new ArrayList<>();
        syllabusDayList.add(modelMapper.map(syllabusDay1, SyllabusDayDTOWithoutId.class));

        AssessmentSchemeDTO assessmentScheme1 = AssessmentSchemeDTO.builder().assignment(15.0).quiz(15.0).gpa(70.0)
                .finalPoint(70.0).finalTheory(40.0).finalPractice(60.0).build();

        FormSyllabusDTOWithoutId requestSyllabus = FormSyllabusDTOWithoutId.builder().name(".NET Programming Language").code("NPL")
                .syllabusDays(syllabusDayList).days(2).hours(10).version("1.0").assessmentScheme(assessmentScheme1).attendeeNumber(20)
                .syllabusLevel(modelMapper.map(syllabusLevelRepository.findById(syllabusLevelId1).get(), SyllabusLevelDTO.class)).build();

        // test
        try {
            SyllabusDTO response = syllabusService.createSyllabus(requestSyllabus);
            assertEquals(response, "Syllabus unit not found");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_createSyllabus_Expect_NotFoundAssessmentScheme1() {
        //input

        Material material = Material.builder().name("Material").materialStatus(MaterialStatus.ACTIVE).createdDate(new Date())
                .updatedDate(new Date()).createdBy(UUID.randomUUID()).updatedBy(UUID.randomUUID()).build();
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        SyllabusUnitChapter syllabusUnitChapter1 = SyllabusUnitChapter.builder().isOnline(true).materials(materialList)
                .outputStandard(outputStandardRepository.findById(outputStandardId1).get())
                .deliveryType(deliveryTypeRepository.findById(deliveryTypeId1).get()).build();
        List<SyllabusUnitChapter> syllabusUnitChapterList = new ArrayList<>();
        syllabusUnitChapterList.add(syllabusUnitChapter1);

        SyllabusUnit syllabusUnit1 = SyllabusUnit.builder().unitNo(1).duration(10).syllabusUnitChapters(syllabusUnitChapterList).build();
        List<SyllabusUnit> syllabusUnitList = new ArrayList<>();
        syllabusUnitList.add(syllabusUnit1);

        SyllabusDay syllabusDay1 = SyllabusDay.builder().dayNo(1).status(SyllabusDayStatus.AVAILABLE).syllabusUnits(null).build();
        List<SyllabusDayDTOWithoutId> syllabusDayList = new ArrayList<>();
        syllabusDayList.add(modelMapper.map(syllabusDay1, SyllabusDayDTOWithoutId.class));
//
//        AssessmentScheme assessmentScheme1 = AssessmentScheme.builder().assignment(15.0).quiz(15.0).gpa(70.0)
//                .finalPoint(70.0).finalTheory(40.0).finalPractice(60.0).build();

        FormSyllabusDTOWithoutId requestSyllabus = FormSyllabusDTOWithoutId.builder().name(".NET Programming Language").code("NPL")
                .syllabusDays(syllabusDayList).days(2).hours(10).version("1.0").assessmentScheme(null).attendeeNumber(20)
                .syllabusLevel(modelMapper.map(syllabusLevelRepository.findById(syllabusLevelId1).get(), SyllabusLevelDTO.class)).build();

        // test
        try {
            SyllabusDTO response = syllabusService.createSyllabus(requestSyllabus);
            assertEquals(response, "assessmentScheme not found");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_createSyllabus_Expect_NotFoundSyllabusDay() {
        //input

//        Material material = Material.builder().name("Material").materialStatus(MaterialStatus.ACTIVE).createdDate(new Date())
//                .updatedDate(new Date()).createdBy(UUID.randomUUID()).updatedBy(UUID.randomUUID()).build();
//        List<Material> materialList = new ArrayList<>();
//        materialList.add(material);
//
//        SyllabusUnitChapter syllabusUnitChapter1 = SyllabusUnitChapter.builder().isOnline(true).materials(materialList)
//                .outputStandard(outputStandardRepository.findById(outputStandardId1).get())
//                .deliveryType(deliveryTypeRepository.findById(deliveryTypeId1).get()).build();
//        List<SyllabusUnitChapter> syllabusUnitChapterList = new ArrayList<>();
//        syllabusUnitChapterList.add(syllabusUnitChapter1);
//
//        SyllabusUnit syllabusUnit1 = SyllabusUnit.builder().unitNo(1).duration(10).syllabusUnitChapters(syllabusUnitChapterList).build();
//        List<SyllabusUnit> syllabusUnitList = new ArrayList<>();
//        syllabusUnitList.add(syllabusUnit1);
//
//        SyllabusDay syllabusDay1 = SyllabusDay.builder().dayNo(1).status(SyllabusDayStatus.AVAILABLE).syllabusUnits(null).build();
//        List<SyllabusDay> syllabusDayList = new ArrayList<>();
//        syllabusDayList.add(syllabusDay1);

        AssessmentScheme assessmentScheme1 = AssessmentScheme.builder().assignment(15.0).quiz(15.0).gpa(70.0)
                .finalPoint(70.0).finalTheory(40.0).finalPractice(60.0).build();

        FormSyllabusDTOWithoutId requestSyllabus = FormSyllabusDTOWithoutId.builder().name(".NET Programming Language").code("NPL")
                .syllabusDays(null).days(2).hours(10).version("1.0").assessmentScheme(null).attendeeNumber(20)
                .syllabusLevel(modelMapper.map(syllabusLevelRepository.findById(syllabusLevelId1).get(), SyllabusLevelDTO.class)).build();

        // test
        try {
            SyllabusDTO response = syllabusService.createSyllabus(requestSyllabus);
            assertEquals(response, "syllabusDay not found");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    private List<Syllabus> syllabuses0 = new ArrayList<>(List.of(
            Syllabus.builder().id(syllabus1).name(".Net Basic Program").code("NBP").createdBy(userB).days(2)
                    .createdDate(new Date(2020, 11, 11)).hours(10).status(SyllabusStatus.ACTIVE).updatedBy(userB).version("1.0").build(),
            Syllabus.builder().id(syllabus2).name("Azure DevOps").code("ADO").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2020, 12, 20)).status(SyllabusStatus.ACTIVE).updatedBy(userB).version("1.0").build(),
            Syllabus.builder().id(syllabus3).name("JUnit Testing").code("FSJ").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2019, 10, 10)).status(SyllabusStatus.ACTIVE).updatedBy(userB).version("1.0").build(),
            Syllabus.builder().id(syllabus4).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2011, 20, 20)).status(SyllabusStatus.DEACTIVE).updatedBy(userB).version("1.1").build(),
            Syllabus.builder().id(syllabus5).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2017, 9, 8)).status(SyllabusStatus.ACTIVE).updatedBy(userB).version("3.0").build()

    ));

    private List<Syllabus> syllabuses1 = new ArrayList<>(List.of(

            Syllabus.builder().id(syllabusDeleted).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2017, 9, 8)).status(SyllabusStatus.DELETED).updatedBy(userB).version("3.0").build(),
            Syllabus.builder().id(syllabusDraft).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2017, 9, 8)).status(SyllabusStatus.DRAFT).updatedBy(userB).version("3.0").build(),
            Syllabus.builder().id(syllabusDraft2).name("JUnit Testing").code("JUT").createdBy(userB).days(2).hours(10)
                    .createdDate(new Date(2017, 9, 8)).status(SyllabusStatus.DRAFT).updatedBy(userB).version("3.0").build()
    ));


    @Test
    @DisplayName("Get All Syllabuses Without Sort Options")
    public void testGetAllSyllabusesWithoutSortOptionsExpectFiveSyllabusesInList() {
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(new String[0], "", "",
                1, 10, null, null);

        Page<Syllabus> results = new PageImpl<>(syllabuses);
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Mockito.when(syllabusRepository.findAllSyllabuses(any(Pageable.class))).thenReturn(results);

        ResponseEntity<ResponseObject> response = syllabusService.getAllSyllabuses(request);
        List<ResponseForListOfSyllabus> actualList = (List<ResponseForListOfSyllabus>) response.getBody().getData();
        Assertions.assertThat(actualList.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("Get All Syllabuses With Ascending Order By Name")
    public void testGetAllSyllabusesWithAscendingOrderByNameExpectReturnSameObject() {
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(new String[0], "", "",
                1, 10, "NAME", "ASC");

        String sql = syllabusServiceUtil.getSQLForSortingAllSyllabuses(request.getPage(), request.getSize(),
                request.getSortBy(), request.getSortType());
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(syllabuses);

        List<SyllabusDTO> dtoList = syllabuses.stream().map(syllabus -> modelMapper.map(syllabus, SyllabusDTO.class))
                .collect(Collectors.toList());
        for (SyllabusDTO s : dtoList) {
            s.setCreatedByUser(syllabusService.getUserById(s.getCreatedBy()));
        }

        List<ResponseForListOfSyllabus> expectedList = dtoList.stream().map(syllabus -> modelMapper.map(syllabus, ResponseForListOfSyllabus.class))
                .collect(Collectors.toList());
        ResponseEntity<ResponseObject> response = syllabusService.getAllSyllabuses(request);
        List<ResponseForListOfSyllabus> actualList = (List<ResponseForListOfSyllabus>) response.getBody().getData();
        Assertions.assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    @DisplayName("Get All Syllabuses With Descending Order By Name")
    public void testGetAllSyllabusesWithDescendingOrderByNameExpectRightName() {
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(new String[0], "", "",
                1, 10, "NAME", "DESC");

        String sql = syllabusServiceUtil.getSQLForSortingAllSyllabuses(request.getPage(), request.getSize(),
                request.getSortBy(), request.getSortType());
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(syllabuses);
        String expectedName = syllabuses.get(4).getName();
        ResponseEntity<ResponseObject> response = syllabusService.getAllSyllabuses(request);
        List<ResponseForListOfSyllabus> actualList = (List<ResponseForListOfSyllabus>) response.getBody().getData();
        Collections.reverse(actualList);
        String actualName = actualList.get(0).getName();
        Assertions.assertThat(actualName).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Get All Syllabuses With Search By Keywords")
    public void testGetAllSyllabusesWithSearchByKeywordsExpectRightSize() {
        String[] tags = {"a"};
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(tags, "", "",
                1, 10, null, null);

        String sql = syllabusServiceUtil.getSQLForSearchingByKeywords(request.getPage(),
                request.getSize(), tags[0]);
        List<Syllabus> expectedList = new ArrayList<>();
        expectedList.add(syllabuses.get(0));
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(expectedList);

        int expectedSize = expectedList.size();
        ResponseEntity<ResponseObject> response = syllabusService.getAllSyllabuses(request);
        List<ResponseForListOfSyllabus> actualList = (List<ResponseForListOfSyllabus>) response.getBody().getData();
        int actualSize = actualList.size();
        Assertions.assertThat(actualSize).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("Get All Syllabuses With Search By Keywords And Tags")
    public void testGetAllSyllabusesWithSearchByKeywordsAndTagsExpectRightSize() {
        String[] tags = {"a"};
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(tags,
                "", "", 1, 10, null, null);

        String sql = syllabusServiceUtil.getSQLForSearchingByKeywords(request.getPage(),
                request.getSize(), "a");
        List<Syllabus> expectedList = new ArrayList<>();
        expectedList.add(syllabuses.get(0));
        expectedList.add(syllabuses.get(1));
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(expectedList);

        int expectedSize = expectedList.size();
        ResponseEntity<ResponseObject> response = syllabusService.getAllSyllabuses(request);
        List<ResponseForListOfSyllabus> actualList = (List<ResponseForListOfSyllabus>) response.getBody().getData();
        int actualSize = actualList.size();
        Assertions.assertThat(actualSize).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("Get All Syllabuses With Search By Keywords And Tags And Sort By Name In Ascending Order")
    public void testGetAllSyllabusesWithSearchByKeywordsAndTagsAndSortByNameInAscendingOrderExpectRightOrder() {
        String[] tags = {"a"};
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(tags, "", "",
                1, 10, "NAME", "ASC");

        String sql = syllabusServiceUtil.getSQLForSearchingByKeywords(request.getPage(),
                request.getSize(), "a");
        List<Syllabus> expectedList = new ArrayList<>();
        expectedList.add(syllabuses.get(0));
        expectedList.add(syllabuses.get(1));
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(expectedList);

        String expectedName = expectedList.get(0).getName();
        ResponseEntity<ResponseObject> response = syllabusService.getAllSyllabuses(request);
        List<ResponseForListOfSyllabus> actualList = (List<ResponseForListOfSyllabus>) response.getBody().getData();
        String actualName = actualList.get(0).getName();
        Assertions.assertThat(actualName).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Get All Syllabuses With Search By Keywords And Tags And Sort By Name In Descending Order")
    public void testGetAllSyllabusesWithSearchByKeywordsAndTagsAndSortByNameInDescendingOrderExpectRightOrder() {
        String[] tags = {"a"};
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(tags, "", "",
                1, 10, null, null);
        String sql = syllabusServiceUtil.getSQLForSearchingByKeywords(request.getPage(), request.getSize(), "a");
        List<Syllabus> expectedList = new ArrayList<>();
        expectedList.add(syllabuses.get(0));
        expectedList.add(syllabuses.get(1));
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(expectedList);

        String expectedName = expectedList.get(0).getName();
        ResponseEntity<ResponseObject> response = syllabusService.getAllSyllabuses(request);
        List<ResponseForListOfSyllabus> actualList = (List<ResponseForListOfSyllabus>) response.getBody().getData();
        String actualName = actualList.get(0).getName();
        Assertions.assertThat(actualName).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Get All Syllabuses With Search By Keywords And Tags")
    public void testGetAllSyllabusesWithSearchByKeywordsAndTagsExpectEmptyList() {
        String[] tags = {"w"};
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(tags, "", "",
                1, 10, null, null);

        String sql = syllabusServiceUtil.getSQLForSearchingByKeywords(request.getPage(),
                request.getSize(), tags[0]);
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(null);
        Mockito.when(syllabusJdbc.getTotalRows(syllabusServiceUtil.getTotalRowsForSearchingByCreatedDate(
                request.getStartDate(), request.getEndDate()))).thenReturn(0);
        for (String s : tags) {
            Mockito.when(syllabusJdbc.getSyllabuses(syllabusServiceUtil
                    .getTotalRowsForSearchingByKeywords(s))).thenReturn(null);
        }
        String expectedMessage = "Empty list";
        ResponseEntity<ResponseObject> response = syllabusService.getAllSyllabuses(request);
        String actualMessage = response.getBody().getMessage();
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Get All Syllabuses With Search By Created Date")
    public void testGetAllSyllabusesWithSearchByCreatedDateExpectRightSize() {
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(new String[0],
                "20/12/2019", "30/12/2020", 1, 10, null, null);

        String sql = syllabusServiceUtil.getSQLForSearchingByCreatedDate(request.getStartDate(),
                request.getEndDate(), request.getPage(), request.getSize());
        List<Syllabus> expectedList = new ArrayList<>();
        expectedList.add(syllabuses.get(0));
        expectedList.add(syllabuses.get(1));
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(expectedList);

        int expectedSize = expectedList.size();
        ResponseEntity<ResponseObject> response = syllabusService.getAllSyllabuses(request);
        List<ResponseForListOfSyllabus> actualList = (List<ResponseForListOfSyllabus>) response.getBody().getData();
        int actualSize = actualList.size();
        Assertions.assertThat(actualSize).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("Get All Syllabuses With Search By Created Date")
    public void testGetAllSyllabusesWithSearchByCreatedDateExpectEmptyList() {
        RequestForListOfSyllabus request = new RequestForListOfSyllabus(new String[0],
                "10/10/2000", "11/04/2001", 1, 10, null, null);

        String sql = syllabusServiceUtil.getSQLForSearchingByCreatedDate(request.getStartDate(),
                request.getEndDate(), request.getPage(), request.getSize());
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(null);

        String expectedMessage = "Empty list";
        ResponseEntity<ResponseObject> response = syllabusService.getAllSyllabuses(request);
        String actualMessage = response.getBody().getMessage();
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Get suggetions with keyword")
    public void testGetAllSuggestionsByKeyword() {
        String sql = syllabusServiceUtil
                .getSQLForSearchingByKeywords(0, 10, "a");
        List<Syllabus> syllabusNameList = new ArrayList<>();
        syllabusNameList.add(syllabuses.get(0));
        syllabusNameList.add(syllabuses.get(1));
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(syllabusNameList);
        ResponseEntity<ResponseObject> response = syllabusService.getSuggestions("a");
        String actualMessage = response.getBody().getMessage();
        System.out.println(response.getBody().getData());
        Assertions.assertThat(actualMessage).isEqualTo("Total 4 element(s)");
    }

    @Test
    @DisplayName("Get suggetions with keyword")
    public void testGetAllSuggestionsByKeywordEmpty() {
        String sql = syllabusServiceUtil
                .getSQLForSearchingByKeywords(0, 10, "a");
        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(null);
        ResponseEntity<ResponseObject> response = syllabusService.getSuggestions("a");
        String actualMessage = response.getBody().getMessage();
        System.out.println(response.getBody().getData());
        Assertions.assertThat(actualMessage).isEqualTo("Empty list");
    }

//    @Test
//    @DisplayName("Get All Draft")
//    public void testGetAllDraft() {
//        List<Syllabus> results = new ArrayList<>();
//        results.add(syllabuses1.get(1));
//        results.add(syllabuses1.get(2));
//        String sql = syllabusServiceUtil.getSQLForSortingAllDraft(userB, 1, 10, "ASC");
//        Mockito.when(syllabusJdbc.getSyllabuses(sql)).thenReturn(results);
//        System.out.println(results.get(0).getId());
//        System.out.println(results.get(1).getId());
//        Mockito.when(syllabusRepository.findSyllabusById(results.get(0).getId())).thenReturn(syllabuses1.get(1));
//        Mockito.when(syllabusRepository.findSyllabusById(results.get(1).getId())).thenReturn(syllabuses1.get(2));
//        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        UUID userId = userRepository.findById(userPrinciple.getId()).get().getId();
//        Mockito.when(userId).thenReturn(syllabuses1.get(2).getCreatedBy());
//        List<SyllabusDTO> expectedListDTO = results.stream().map(syllabus -> modelMapper.map(syllabus, SyllabusDTO.class))
//                .collect(Collectors.toList());;
//        List<ResponseForListOfSyllabus> expectedList = expectedListDTO.stream().map(syllabus -> modelMapper.map(syllabus, ResponseForListOfSyllabus.class))
//                .collect(Collectors.toList());
//        for (ResponseForListOfSyllabus s : expectedList) {
//            s.setCreatedByUser(syllabusService.getUserById(s.getCreatedBy()));
//        }
//        ResponseEntity<ResponseObject> response = syllabusService.getAllDraftByUserId("ASC", 1, 10);
//        List<ResponseForListOfSyllabus> actualList = (List<ResponseForListOfSyllabus>) response.getBody().getData();
////        List<ResponseForListOfSyllabus> actualList = actualListDTO.stream().map(syllabus -> modelMapper.map(syllabus, ResponseForListOfSyllabus.class))
////                .collect(Collectors.toList());
//        Assertions.assertThat(actualList).isEqualTo(expectedList);
//    }
}
