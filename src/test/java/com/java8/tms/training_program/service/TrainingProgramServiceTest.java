package com.java8.tms.training_program.service;

import com.java8.tms.common.dto.ErrorResponse;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.dto.TrainingProgramDTO;
import com.java8.tms.common.entity.TrainingClass;
import com.java8.tms.common.entity.TrainingProgram;
import com.java8.tms.common.exception.CustomExceptionHandler;
import com.java8.tms.common.exception.model.ResourceNotFoundException;
import com.java8.tms.common.meta.TrainingProgramStatus;
import com.java8.tms.common.payload.request.UpdateTrainingProgramForm;
import com.java8.tms.common.repository.TrainingProgramRepository;
import com.java8.tms.training_program.dto.RequestForFilterTrainingProgram;
import com.java8.tms.training_program.dto.ResponseForFilterTrainingProgram;
import com.java8.tms.training_program.dto.TrainingProgramForFilter;
import com.java8.tms.training_program.exception.InvalidRequestForFilterTrainingProgramException;
import com.java8.tms.training_program.jdbc.TrainingProgramJdbc;
import com.java8.tms.training_program.meta.AvailableTrainingProgramStatusForFilter;
import com.java8.tms.training_program.service.impl.TrainingProgramServiceImpl;
import com.java8.tms.training_program.service.impl.TrainingProgramServiceUtil;
import com.java8.tms.training_program.validation.ValidationOfRequestForFilterTrainingProgram;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.sql.Date;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
class TrainingProgramServiceTest {
    private static final List<TrainingProgramForFilter> trainingProgramsForFilterTest = new ArrayList<>(List.of(
            TrainingProgramForFilter.builder().name("JAVA").createdBy("Yasuo").status("Active")
                    .createdDate("2021-10-02").duration(30).build(),
            TrainingProgramForFilter.builder().name("JAVA2").createdBy("Yasuo").status("Active")
                    .createdDate("2021-09-10").duration(30).build(),
            TrainingProgramForFilter.builder().name("C#").createdBy("Warrior Tran").status("Deactive")
                    .createdDate("2021-10-03").duration(60).build(),
            TrainingProgramForFilter.builder().name("C#2").createdBy("Warrior Tran").status("Deactive")
                    .createdDate("2021-08-31").duration(45).build(),
            TrainingProgramForFilter.builder().name("PYTHON").createdBy("William").status("Deleted")
                    .createdDate("2020-11-20").duration(27).build(),
            TrainingProgramForFilter.builder().name("PYTHON2").createdBy("William").status("Deleted")
                    .createdDate("2021-12-01").duration(30).build(),
            TrainingProgramForFilter.builder().name("REACT").createdBy("Test").status("Draft").createdDate("2021-04-30")
                    .duration(30).build(),
            TrainingProgramForFilter.builder().name("REACT2").createdBy("Test").status("Draft")
                    .createdDate("2021-09-02").duration(27).build()));
    private static final List<String> keywordsForSuggestion = List.of("C Foundation_2_1.0", "C Foundation_1.0",
            "Fullstack Java Web Developer Foundation_2_1.0", "  Khóa React basic Foundation_2_1.0",
            "Java Advance_2_1.0", ".NET basic foundation_1.0", "React Advance_2_1.0");
    private final UUID idCase1 = UUID.randomUUID();
    private final UUID idCase2 = UUID.randomUUID();
    private final UUID idCase3 = UUID.randomUUID();
    @Autowired
    TrainingProgramServiceImpl trainingClassService;
    @Autowired
    private TrainingProgramService trainingProgramService;
    @MockBean
    private TrainingProgramRepository trainingProgramRepository;
    @Autowired
    private TrainingProgramServiceUtil trainingProgramServiceUtil;
    @MockBean
    private TrainingProgramJdbc trainingProgramJdbc;

    @BeforeEach
    void setupMockForTest() {
        System.out.println(idCase2);
        List<TrainingClass> trainingClasses = List.of(
                TrainingClass.builder().id(UUID.randomUUID()).name("Java-01").courseCode("JAVA01").build(),
                TrainingClass.builder().id(UUID.randomUUID()).name("Java-02").courseCode("JAVA02").build());

        List<TrainingProgram> trainingPrograms = new ArrayList<>(List.of(
                TrainingProgram.builder().id(idCase1).name("TP 1").status(TrainingProgramStatus.ACTIVE).version("1.0")
                        .build(),
                TrainingProgram.builder().id(idCase2).name("TP 2").status(TrainingProgramStatus.ACTIVE).version("1.1")
                        .trainingClass(trainingClasses.get(0)).build(),
                TrainingProgram.builder().id(idCase3).name("TP 3").status(TrainingProgramStatus.ACTIVE)
                        .version("1.2").build(),
                TrainingProgram.builder().id(UUID.randomUUID()).name("TP 4").status(TrainingProgramStatus.INACTIVE)
                        .version("1.3").build(),
                TrainingProgram.builder().id(UUID.randomUUID()).name("TP 5").status(TrainingProgramStatus.INACTIVE)
                        .version("1.4").build()));

        Mockito.when(trainingProgramRepository.findAll()).thenReturn(trainingPrograms);

        Optional<TrainingProgram> trainOptional = Optional.ofNullable(trainingPrograms.get(0));
        Mockito.when(trainingProgramRepository.findById(idCase1)).thenReturn((trainOptional));

        Optional<TrainingProgram> trainOptionalCase2 = Optional.ofNullable(trainingPrograms.get(1));
        Mockito.when(trainingProgramRepository.findById(idCase2)).thenReturn((trainOptionalCase2));

    }

    /**
     * <p>
     * Test update status of training program successfully
     * </p>
     *
     * @throws ResourceNotFoundException if the training program could not be found
     * @author Vien Binh
     */
    @Test
    @DisplayName("TestUpdateStatusSuccessfully")
    void TestUpdateStatusSuccessfully() throws ResourceNotFoundException {
        TrainingProgram trainingProgram = trainingProgramService.findById(idCase1).get();
        TrainingProgramStatus expectedStatus = trainingProgram.getStatus().equals(TrainingProgramStatus.ACTIVE)
                ? TrainingProgramStatus.INACTIVE
                : TrainingProgramStatus.ACTIVE;
        UpdateTrainingProgramForm updateTrainingProgramForm = UpdateTrainingProgramForm.builder()
                .trainingProgramId(idCase1)
                .status(TrainingProgramStatus.INACTIVE)
                .build();
        ResponseEntity<ResponseObject> programResponse = trainingProgramService.updateStatus(updateTrainingProgramForm);
        TrainingProgramDTO programResponseEntity = (TrainingProgramDTO) Objects.requireNonNull(programResponse.getBody()).getData();
        String message = programResponse.getBody().getMessage();

        assertThat(programResponseEntity.getStatus()).isEqualTo(expectedStatus);
        assertThat(trainingProgram.getStatus()).isEqualTo(expectedStatus);
        assertEquals(200, programResponse.getStatusCodeValue());
        assertEquals("Update status successfully", message);
    }

    /**
     * <p>
     * Test update status of training program fail because some classes are using
     * </p>
     *
     * @throws ResourceNotFoundException if the training program could not be found
     * @author Vien Binh
     */
    @Test
    @DisplayName("TestUpdateTrainingProgramIsUsedBySomeClasses")
    void TestUpdateTrainingProgramIsUsedBySomeClasses() throws ResourceNotFoundException {
        UpdateTrainingProgramForm updateTrainingProgramForm = UpdateTrainingProgramForm.builder()
                .trainingProgramId(idCase2)
                .status(TrainingProgramStatus.INACTIVE)
                .build();
        ResponseEntity<ResponseObject> programResponse = trainingProgramService.updateStatus(updateTrainingProgramForm);
        TrainingProgramDTO programResponseEntity = (TrainingProgramDTO) Objects.requireNonNull(programResponse.getBody()).getData();
        String message = programResponse.getBody().getMessage();
        String expectedMessage = "Some classes are using this training program";
        TrainingProgram trainingProgram = trainingProgramService.findById(idCase2).get();

        assertEquals(programResponseEntity.getStatus(), trainingProgram.getStatus());
        assertEquals(500, programResponse.getStatusCodeValue());
        assertEquals(expectedMessage, message);
    }

    /**
     * <p>
     * Test update status of training program with wrong id
     * </p>
     *
     * @author Vien Binh
     */
    @Test
    @DisplayName("TestUpdateStatusTrainingProgramWithWrongId")
    void TestUpdateStatusTrainingProgramWithWrongId() {
        // Update training program status with random id which is not had in DB
        UUID randomId = UUID.randomUUID();
        UpdateTrainingProgramForm updateTrainingProgramForm = UpdateTrainingProgramForm.builder()
                .trainingProgramId(randomId)
                .status(TrainingProgramStatus.INACTIVE)
                .build();
        assertThrows(ResourceNotFoundException.class, () -> {
            trainingProgramService.updateStatus(updateTrainingProgramForm);
        }, "Training Program not found for this ID: " + randomId);

        try {
            trainingProgramService.updateStatus(updateTrainingProgramForm);
        } catch (ResourceNotFoundException e) {
            WebRequest request = null;
            CustomExceptionHandler exceptionHandler = new CustomExceptionHandler();
            ErrorResponse errorResponse = exceptionHandler.resourceNotFoundException(e, request);
            assertEquals(HttpStatus.NO_CONTENT.toString(), errorResponse.getError());
        }
    }

    /**
     * <p>
     * Delete training program
     * </p>
     *
     * @throws ResourceNotFoundException if training program not found
     * @author Vien Binh
     */
    @Test
    void DeleteTrainingProgramSuccess() throws ResourceNotFoundException {
        UpdateTrainingProgramForm updateTrainingProgramForm = UpdateTrainingProgramForm.builder()
                .trainingProgramId(idCase1)
                .build();
        ResponseEntity<ResponseObject> trainingProgramResponse = trainingProgramService.deleteById(idCase1);
        TrainingProgramDTO trainingProgramResponseDTO = (TrainingProgramDTO) Objects.requireNonNull(trainingProgramResponse.getBody()).getData();
        String message = trainingProgramResponse.getBody().getMessage();
        String expectedMessage = "Deleted successfully";

        assertEquals(trainingProgramResponseDTO.getStatus(), TrainingProgramStatus.DELETED);
        assertEquals(expectedMessage, message);
        assertEquals(200, trainingProgramResponse.getStatusCodeValue());
    }

    /**
     * <p>
     * Delete training program which is used by some classes
     * </p>
     *
     * @throws ResourceNotFoundException if training program not found
     * @author Vien Binh
     */
    @Test
    void DeleteTrainingProgramWithClassInUsed() throws ResourceNotFoundException {
        ResponseEntity<ResponseObject> trainingProgramResponse = trainingProgramService.deleteById(idCase2);
        TrainingProgramDTO trainingProgramResponseDTO = (TrainingProgramDTO) Objects.requireNonNull(trainingProgramResponse.getBody()).getData();
        TrainingProgram trainingProgram = trainingProgramService.findById(idCase2).get();
        String message = trainingProgramResponse.getBody().getMessage();
        String expectedMessage = "Some classes are using this training program";

        assertEquals(trainingProgramResponseDTO.getStatus(), trainingProgram.getStatus());
        assertEquals(expectedMessage, message);
    }

    /**
     * <p>
     * Delete training program with wrong id
     * </p>
     *
     * @throws ResourceNotFoundException if training program is not found
     * @author Vien Binh
     */
    @Test
    void DeleteTrainingProgramWrongId() throws ResourceNotFoundException {
        // Delete training program status with random id which is not had in DB
        UUID randomId = UUID.randomUUID();
        assertThrows(ResourceNotFoundException.class, () -> {
            trainingProgramService.deleteById(randomId);
        }, "Training Program not found for this ID: " + randomId);
        try {
            trainingProgramService.deleteById(randomId);
        } catch (ResourceNotFoundException e) {
            WebRequest request = null;
            CustomExceptionHandler exceptionHandler = new CustomExceptionHandler();
            ErrorResponse errorResponse = exceptionHandler.resourceNotFoundException(e, request);
            assertEquals(HttpStatus.NO_CONTENT.toString(), errorResponse.getError());
        }
    }

    /**
     * <p>
     * Test found result when request filter by status active
     * </p>
     *
     * @throws InvalidRequestForFilterTrainingProgramException
     * @author Pham Xuan Kien
     */
    @Test
    void When_ValidTrainingProgramActiveStatus_Expect_ActiveTrainingProgramShouldFound()
            throws InvalidRequestForFilterTrainingProgramException {

        List<TrainingProgramForFilter> actualTrainingPrograms = getTrainingProgramsByStatus("active");
        Long actualTotalRows = (long) getTrainingProgramsByStatus("active").size();

        RequestForFilterTrainingProgram requestDataWithAciveStatus = RequestForFilterTrainingProgram.builder()
                .status("active").page(1).size(10).build();

        String sqlForResultInAPage = trainingProgramServiceUtil
                .constructSQLForResultInAPage(requestDataWithAciveStatus);
        String sqlForTotalRows = trainingProgramServiceUtil.constructSQLForTotalRows(requestDataWithAciveStatus);

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithAciveStatus);

        boolean isAllActive = true;
        for (TrainingProgramForFilter trainingProgram : responseResult.getTrainingPrograms()) {
            if (!trainingProgram.getStatus().equalsIgnoreCase("active")) {
                isAllActive = false;
                break;
            }
        }

        assertTrue(isAllActive);
    }

    /**
     * <p>
     * Test found result when request filter by status inactive
     * </p>
     *
     * @throws InvalidRequestForFilterTrainingProgramException
     * @author Pham Xuan Kien
     */
    @Test
    void When_ValidTrainingProgramInactiveStatus_Expect_InactiveTrainingProgramShouldFound()
            throws InvalidRequestForFilterTrainingProgramException {
        List<TrainingProgramForFilter> actualTrainingPrograms = getTrainingProgramsByStatus("inactive");
        Long actualTotalRows = (long) getTrainingProgramsByStatus("inactive").size();

        RequestForFilterTrainingProgram requestDataWithAciveStatus = RequestForFilterTrainingProgram.builder()
                .status("inactive").page(1).size(10).build();

        String sqlForResultInAPage = trainingProgramServiceUtil
                .constructSQLForResultInAPage(requestDataWithAciveStatus);
        String sqlForTotalRows = trainingProgramServiceUtil.constructSQLForTotalRows(requestDataWithAciveStatus);

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithAciveStatus);

        boolean isAllInactive = true;
        for (TrainingProgramForFilter trainingProgram : responseResult.getTrainingPrograms()) {
            if (!trainingProgram.getStatus().equalsIgnoreCase("inactive")) {
                isAllInactive = false;
                break;
            }
        }

        assertTrue(isAllInactive);
    }

    /**
     * <p>
     * Test found result when request filter by all status
     * </p>
     *
     * @throws InvalidRequestForFilterTrainingProgramException
     * @author Pham Xuan Kien
     */
    @Test
    void When_TrainingProgramStatusIsNull_Expect_TrainingProgramFromAllStatusShouldFound()
            throws InvalidRequestForFilterTrainingProgramException {
        List<TrainingProgramForFilter> actualTrainingPrograms = trainingProgramsForFilterTest;
        Long actualTotalRows = (long) trainingProgramsForFilterTest.size();

        RequestForFilterTrainingProgram requestDataWithNullStatus = RequestForFilterTrainingProgram.builder()
                .status(null).page(1).size(10).build();

        String sqlForResultInAPage = trainingProgramServiceUtil.constructSQLForResultInAPage(requestDataWithNullStatus);
        String sqlForTotalRows = trainingProgramServiceUtil.constructSQLForTotalRows(requestDataWithNullStatus);

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithNullStatus);

        assertEquals(trainingProgramsForFilterTest.size(), responseResult.getTrainingPrograms().size());
    }

    /**
     * <p>
     * Test with expect InvalidRequestForFilterTrainingProgramException when input
     * invalid status
     * </p>
     *
     * @author Pham Xuan Kien
     */
    @Test
    void When_InvalidTrainingProgramStatus_Expect_ReturnInvalidRequestForFilterTrainingProgramException() {

        RequestForFilterTrainingProgram requestDataWithInvalidStatus = RequestForFilterTrainingProgram.builder()
                .status("in-active").page(1).size(10).build();

        InvalidRequestForFilterTrainingProgramException actualException = assertThrows(
                InvalidRequestForFilterTrainingProgramException.class, () -> {
                    trainingProgramService.filterTrainingProgram(requestDataWithInvalidStatus);
                });

        String expectedMessage = "Status to filter training programs must be "
                + ValidationOfRequestForFilterTrainingProgram
                .getEnumString(AvailableTrainingProgramStatusForFilter.values())
                + " or leave null if you want to get all training programs with all status";
        String actualMessage = actualException.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * <p>
     * Test with expect InvalidRequestForFilterTrainingProgramException when input
     * invalid page number
     * </p>
     *
     * @author Pham Xuan Kien
     */
    @Test
    void When_InvalidPageNumber_Expect_ReturnInvalidRequestForFilterTrainingProgramException() {

        RequestForFilterTrainingProgram requestDataWithInvalidPageNumber = RequestForFilterTrainingProgram.builder()
                .page(-1).size(10).build();

        InvalidRequestForFilterTrainingProgramException actualException = assertThrows(
                InvalidRequestForFilterTrainingProgramException.class, () -> {
                    trainingProgramService.filterTrainingProgram(requestDataWithInvalidPageNumber);
                });

        String expectedMessage = "Page number must be an integer > 0";
        String actualMessage = actualException.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * <p>
     * Test with expect InvalidRequestForFilterTrainingProgramException when input
     * invalid page size
     * </p>
     *
     * @author Pham Xuan Kien
     */
    @Test
    void When_InvalidPageSize_Expect_ReturnInvalidRequestForFilterTrainingProgramException() {

        RequestForFilterTrainingProgram requestDataWithInvalidPageSize = RequestForFilterTrainingProgram.builder()
                .page(1).size(-10).build();

        InvalidRequestForFilterTrainingProgramException actualException = assertThrows(
                InvalidRequestForFilterTrainingProgramException.class, () -> {
                    trainingProgramService.filterTrainingProgram(requestDataWithInvalidPageSize);
                });

        String expectedMessage = "Size of page must be an integer > 0";
        String actualMessage = actualException.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * <p>
     * Test suggest keywords
     * </p>
     *
     * @author Pham Xuan Kien
     */
    @Test
    void When_ValidKeyword_Expect_ReturnKeywordListForSuggestion() {
        String requestDataWithValidKeyword = "c";
        String sqlForSuggestedKeywords = trainingProgramServiceUtil
                .constructSQLForSuggestedKeywords(requestDataWithValidKeyword);

        Mockito.when(trainingProgramJdbc.getKeywordList(sqlForSuggestedKeywords, requestDataWithValidKeyword))
                .thenReturn(keywordsForSuggestion);

        ResponseEntity<ResponseObject> expectResponse = ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get search value list successfully", null, keywordsForSuggestion));

        assertEquals(expectResponse, trainingProgramService.getKeywordList(requestDataWithValidKeyword));
    }

    @Test
    @DisplayName("Test Not Found Keywords")
    void When_KeywordsNotFound_Expect_TrainingProgramReturnNull()
            throws InvalidRequestForFilterTrainingProgramException {

        String[] keywords = {"zo", "huhu"};

        List<TrainingProgramForFilter> actualTrainingPrograms = searchTrainingProgramByKeywords(keywords);
        Long actualTotalRows = (long) searchTrainingProgramByKeywords(keywords).size();

        RequestForFilterTrainingProgram requestData = RequestForFilterTrainingProgram.builder().page(1).size(10)
                .build();

        String sqlForResultInAPage = trainingProgramServiceUtil.constructSQLForResultInAPage(requestData);
        String sqlForTotalRows = trainingProgramServiceUtil.constructSQLForTotalRows(requestData);

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService.filterTrainingProgram(requestData);

        List<TrainingProgramForFilter> returnList = responseResult.getTrainingPrograms();

        assertEquals(0, returnList.size());
    }

    @Test
    @DisplayName("Test Null Keywords")
    void When_KeywordsAreNull_Expect_TrainingProgramReturnAll() throws InvalidRequestForFilterTrainingProgramException {
        String[] keywords = null;

        List<TrainingProgramForFilter> actualTrainingPrograms = searchTrainingProgramByKeywords(keywords);
        Long actualTotalRows = (long) searchTrainingProgramByKeywords(keywords).size();

        RequestForFilterTrainingProgram requestData = RequestForFilterTrainingProgram.builder().page(1).size(10)
                .build();

        String sqlForResultInAPage = trainingProgramServiceUtil.constructSQLForResultInAPage(requestData);
        String sqlForTotalRows = trainingProgramServiceUtil.constructSQLForTotalRows(requestData);

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService.filterTrainingProgram(requestData);

        List<TrainingProgramForFilter> returnList = responseResult.getTrainingPrograms();

        assertEquals(trainingProgramsForFilterTest.size(), returnList.size());
    }

    @Test
    @DisplayName("Test Found Keywords")
    void When_KeywordsAreNotNullAndFound_Expect_TrainingProgramReturnTrainingPrograms()
            throws InvalidRequestForFilterTrainingProgramException {
        String[] keywords = {"ja", "ya", "Warrior Tran"};

        List<TrainingProgramForFilter> actualTrainingPrograms = searchTrainingProgramByKeywords(keywords);
        Long actualTotalRows = (long) searchTrainingProgramByKeywords(keywords).size();

        RequestForFilterTrainingProgram requestData = RequestForFilterTrainingProgram.builder().page(1).size(10)
                .build();

        String sqlForResultInAPage = trainingProgramServiceUtil.constructSQLForResultInAPage(requestData);
        String sqlForTotalRows = trainingProgramServiceUtil.constructSQLForTotalRows(requestData);

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService.filterTrainingProgram(requestData);

        List<TrainingProgramForFilter> returnList = responseResult.getTrainingPrograms();

        assertEquals(4, returnList.size());
    }

    /**
     * <p>
     * Return a list of TrainingProgramForFilter with expected keywords that pass in
     * this function
     * </p>
     *
     * @param keywords {@code String[]}
     * @return list of TrainingProgramForFilter with expected keywords
     * @author Le Tri Quyen
     */
    private List<TrainingProgramForFilter> searchTrainingProgramByKeywords(String[] keywords) {
        List<TrainingProgramForFilter> searchList = new ArrayList<>();
        List<TrainingProgramForFilter> finalList = new ArrayList<>();
        if (keywords != null && keywords.length != 0) {
            for (String k : keywords)
                for (TrainingProgramForFilter tp : trainingProgramsForFilterTest)
                    if (tp.getCreatedBy().toLowerCase().contains(k.toLowerCase())
                            || tp.getName().toLowerCase().contains(k.toLowerCase()))
                        searchList.add(tp);
            for (TrainingProgramForFilter tp : searchList)
                if (!finalList.contains(tp))
                    finalList.add(tp);
        } else
            return trainingProgramsForFilterTest;
        return finalList;
    }

    @Test
    void When_ExceedMaxNumbersOfKeyWords_Expect_ReturnInvalidRequestForFilterTrainingProgramException() {

        String[] keywords = {"abc", "def", "ghi", "jkl", "mno", "pqr"};
        RequestForFilterTrainingProgram requestData = RequestForFilterTrainingProgram.builder().searchValue(keywords)
                .page(1).size(10).build();
        InvalidRequestForFilterTrainingProgramException actualException = assertThrows(
                InvalidRequestForFilterTrainingProgramException.class, () -> {
                    trainingProgramService.filterTrainingProgram(requestData);
                });

        String expectedMessage = "The number of search values cannot exceed "
                + ValidationOfRequestForFilterTrainingProgram.MAX_NUMBER_OF_SEARCHVALUES;
        String actualMessage = actualException.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void When_ExceedMaxLengthOfKeyWords_Expect_ReturnInvalidRequestForFilterTrainingProgramException() {

        String[] keywords = {"deadislikethewind,alwaysbymyside.Honorisinthehear,notthename."};
        RequestForFilterTrainingProgram requestData = RequestForFilterTrainingProgram.builder().searchValue(keywords)
                .page(1).size(10).build();
        InvalidRequestForFilterTrainingProgramException actualException = assertThrows(
                InvalidRequestForFilterTrainingProgramException.class, () -> {
                    trainingProgramService.filterTrainingProgram(requestData);
                });

        String expectedMessage = "Maximum length of a search value cannot exceed "
                + ValidationOfRequestForFilterTrainingProgram.MAX_LENGTH_OF_SEARCHVALUE;
        String actualMessage = actualException.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * <p>
     * Return a list of TrainingProgramForFilter with corresponding sort field and
     * sort direction that pass in this function
     * </p>
     *
     * @param sortField     {@code String} : name, createdDate, createdBy
     * @param sortDirection {@code String} : ascending, descending
     * @return list of TrainingProgramForFilter with created date ascending
     * @author Le Vu Lam Duy
     */
    private List<TrainingProgramForFilter> getTrainingProgramsBySortFieldAndSortDirection(String sortField,
                                                                                          String sortDirection) {
        List<TrainingProgramForFilter> result = trainingProgramsForFilterTest;

        if (sortField.equalsIgnoreCase("createdDate")) {
            Collections.sort(result, new Comparator<TrainingProgramForFilter>() {
                @Override
                public int compare(TrainingProgramForFilter t1, TrainingProgramForFilter t2) {
                    return Date.valueOf(t1.getCreatedDate()).compareTo(Date.valueOf(t2.getCreatedDate()));
                }
            });
        } else if (sortField.equalsIgnoreCase("createdBy")) {
            Collections.sort(result, new Comparator<TrainingProgramForFilter>() {
                @Override
                public int compare(TrainingProgramForFilter t1, TrainingProgramForFilter t2) {
                    return t1.getCreatedBy().compareTo(t2.getCreatedBy());
                }
            });
        } else if (sortField.equalsIgnoreCase("name")) {
            Collections.sort(result, new Comparator<TrainingProgramForFilter>() {
                @Override
                public int compare(TrainingProgramForFilter t1, TrainingProgramForFilter t2) {
                    return t1.getName().compareTo(t2.getName());
                }
            });
        } else if (sortField.equalsIgnoreCase("duration")) {
            result.sort(Comparator.comparingInt(trainingProgram -> trainingProgram.getDuration()));
        }

        if (sortDirection.equalsIgnoreCase("descending")) {
            Collections.reverse(result);
        }

        return result;
    }

    @Test
    void When_SortCreatedDateAscending_Expect_ReturnTrainingProgramListSortedByCreatedDateAscending()
            throws InvalidRequestForFilterTrainingProgramException {
        List<TrainingProgramForFilter> actualTrainingPrograms = getTrainingProgramsBySortFieldAndSortDirection(
                "createdDate", "asc");

        RequestForFilterTrainingProgram requestDataWithSortByCreatedDateAndSortInAscending = RequestForFilterTrainingProgram
                .builder().sortBy("createdDate").sortType("Asc").page(1).size(10).build();
        String sqlForResultInAPage = trainingProgramServiceUtil
                .constructSQLForResultInAPage(requestDataWithSortByCreatedDateAndSortInAscending);
        String sqlForTotalRows = trainingProgramServiceUtil
                .constructSQLForTotalRows(requestDataWithSortByCreatedDateAndSortInAscending);

        Long actualTotalRows = (long) trainingProgramsForFilterTest.size();

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithSortByCreatedDateAndSortInAscending);

        assertEquals(actualTrainingPrograms, responseResult.getTrainingPrograms());

    }

    @Test
    void When_SortCreatedDateDescending_Expect_ReturnTrainingProgramListSortedByCreatedDateDescending()
            throws InvalidRequestForFilterTrainingProgramException {

        List<TrainingProgramForFilter> actualTrainingPrograms = getTrainingProgramsBySortFieldAndSortDirection(
                "createdDate", "desc");

        RequestForFilterTrainingProgram requestDataWithSortByCreatedDateAndSortInDescending = RequestForFilterTrainingProgram
                .builder().sortBy("createdDate").sortType("Desc").page(1).size(10).build();

        String sqlForResultInAPage = trainingProgramServiceUtil
                .constructSQLForResultInAPage(requestDataWithSortByCreatedDateAndSortInDescending);
        String sqlForTotalRows = trainingProgramServiceUtil
                .constructSQLForTotalRows(requestDataWithSortByCreatedDateAndSortInDescending);

        Long actualTotalRows = (long) trainingProgramsForFilterTest.size();

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithSortByCreatedDateAndSortInDescending);

        assertEquals(actualTrainingPrograms, responseResult.getTrainingPrograms());

    }

    @Test
    void When_SortCreatedByAscending_Expect_ReturnTrainingProgramListSortedByCreatedByAscending()
            throws InvalidRequestForFilterTrainingProgramException {

        List<TrainingProgramForFilter> actualTrainingPrograms = getTrainingProgramsBySortFieldAndSortDirection(
                "createdBy", "asc");

        RequestForFilterTrainingProgram requestDataWithSortByCreatedByAndSortInAscending = RequestForFilterTrainingProgram
                .builder().sortBy("createdBy").sortType("Asc").page(1).size(10).build();

        String sqlForResultInAPage = trainingProgramServiceUtil
                .constructSQLForResultInAPage(requestDataWithSortByCreatedByAndSortInAscending);
        String sqlForTotalRows = trainingProgramServiceUtil
                .constructSQLForTotalRows(requestDataWithSortByCreatedByAndSortInAscending);

        Long actualTotalRows = (long) trainingProgramsForFilterTest.size();

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithSortByCreatedByAndSortInAscending);

        assertEquals(actualTrainingPrograms, responseResult.getTrainingPrograms());

    }

    @Test
    void When_SortCreatedByDescending_Expect_ReturnTrainingProgramListSortedByCreatedByDescending()
            throws InvalidRequestForFilterTrainingProgramException {

        List<TrainingProgramForFilter> actualTrainingPrograms = getTrainingProgramsBySortFieldAndSortDirection(
                "createdBy", "desc");

        RequestForFilterTrainingProgram requestDataWithSortByCreatedByAndSortInDescending = RequestForFilterTrainingProgram
                .builder().sortBy("createdBy").sortType("Desc").page(1).size(10).build();

        String sqlForResultInAPage = trainingProgramServiceUtil
                .constructSQLForResultInAPage(requestDataWithSortByCreatedByAndSortInDescending);
        String sqlForTotalRows = trainingProgramServiceUtil
                .constructSQLForTotalRows(requestDataWithSortByCreatedByAndSortInDescending);

        Long actualTotalRows = (long) trainingProgramsForFilterTest.size();

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithSortByCreatedByAndSortInDescending);

        assertEquals(actualTrainingPrograms, responseResult.getTrainingPrograms());

    }

    @Test
    void When_SortNameAscending_Expect_ReturnTrainingProgramListSortedByNameAscending()
            throws InvalidRequestForFilterTrainingProgramException {

        List<TrainingProgramForFilter> actualTrainingPrograms = getTrainingProgramsBySortFieldAndSortDirection("name",
                "asc");

        RequestForFilterTrainingProgram requestDataWithSortByNameAndSortInAscending = RequestForFilterTrainingProgram
                .builder().sortBy("name").sortType("Asc").page(1).size(10).build();

        String sqlForResultInAPage = trainingProgramServiceUtil
                .constructSQLForResultInAPage(requestDataWithSortByNameAndSortInAscending);
        String sqlForTotalRows = trainingProgramServiceUtil
                .constructSQLForTotalRows(requestDataWithSortByNameAndSortInAscending);

        Long actualTotalRows = (long) trainingProgramsForFilterTest.size();

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithSortByNameAndSortInAscending);

        assertEquals(actualTrainingPrograms, responseResult.getTrainingPrograms());

    }

    @Test
    void When_SortNameDescending_Expect_ReturnTrainingProgramListSortedByNameDescending()
            throws InvalidRequestForFilterTrainingProgramException {

        List<TrainingProgramForFilter> actualTrainingPrograms = getTrainingProgramsBySortFieldAndSortDirection("name",
                "desc");

        RequestForFilterTrainingProgram requestDataWithSortByNameAndSortInDescending = RequestForFilterTrainingProgram
                .builder().sortBy("name").sortType("Desc").page(1).size(10).build();

        String sqlForResultInAPage = trainingProgramServiceUtil
                .constructSQLForResultInAPage(requestDataWithSortByNameAndSortInDescending);
        String sqlForTotalRows = trainingProgramServiceUtil
                .constructSQLForTotalRows(requestDataWithSortByNameAndSortInDescending);

        Long actualTotalRows = (long) trainingProgramsForFilterTest.size();

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithSortByNameAndSortInDescending);

        assertEquals(actualTrainingPrograms, responseResult.getTrainingPrograms());

    }

    @Test
    void When_SortDurationAscending_Expect_ReturnTrainingProgramListSortedByDurationAscending()
            throws InvalidRequestForFilterTrainingProgramException {

        List<TrainingProgramForFilter> actualTrainingPrograms = getTrainingProgramsBySortFieldAndSortDirection(
                "duration", "asc");

        RequestForFilterTrainingProgram requestDataWithSortByDurationAndSortInAscending = RequestForFilterTrainingProgram
                .builder().sortBy("duration").sortType("Asc").page(1).size(10).build();

        String sqlForResultInAPage = trainingProgramServiceUtil
                .constructSQLForResultInAPage(requestDataWithSortByDurationAndSortInAscending);
        String sqlForTotalRows = trainingProgramServiceUtil
                .constructSQLForTotalRows(requestDataWithSortByDurationAndSortInAscending);

        Long actualTotalRows = (long) trainingProgramsForFilterTest.size();

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithSortByDurationAndSortInAscending);

        assertEquals(actualTrainingPrograms, responseResult.getTrainingPrograms());

    }

    @Test
    void When_SortDurationDescending_Expect_ReturnTrainingProgramListSortedByDurationDescending()
            throws InvalidRequestForFilterTrainingProgramException {

        List<TrainingProgramForFilter> actualTrainingPrograms = getTrainingProgramsBySortFieldAndSortDirection(
                "duration", "desc");

        RequestForFilterTrainingProgram requestDataWithSortByDurationAndSortInDescending = RequestForFilterTrainingProgram
                .builder().sortBy("duration").sortType("Desc").page(1).size(10).build();

        String sqlForResultInAPage = trainingProgramServiceUtil
                .constructSQLForResultInAPage(requestDataWithSortByDurationAndSortInDescending);
        String sqlForTotalRows = trainingProgramServiceUtil
                .constructSQLForTotalRows(requestDataWithSortByDurationAndSortInDescending);

        Long actualTotalRows = (long) trainingProgramsForFilterTest.size();

        Mockito.when(trainingProgramJdbc.getTrainingProgramForFiltersByQuery(sqlForResultInAPage))
                .thenReturn(actualTrainingPrograms);
        Mockito.when(trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows)).thenReturn(actualTotalRows);

        ResponseForFilterTrainingProgram responseResult = trainingProgramService
                .filterTrainingProgram(requestDataWithSortByDurationAndSortInDescending);

        assertEquals(actualTrainingPrograms, responseResult.getTrainingPrograms());

    }

    @Test
    void When_InvalidSortField_Expect_ThrowsInvalidRequestForFilterTrainingProgramException() {
        RequestForFilterTrainingProgram requestDataWithInvalidSortField = RequestForFilterTrainingProgram.builder()
                .sortBy("duratio").sortType("Desc").page(1).size(10).build();
        InvalidRequestForFilterTrainingProgramException actualException = assertThrows(
                InvalidRequestForFilterTrainingProgramException.class, () -> {
                    trainingProgramService.filterTrainingProgram(requestDataWithInvalidSortField);
                });

        String expectedMessage = "Column to sort training programs must be "
                + TrainingProgramForFilter.Fields.name.toUpperCase() + ", "
                + TrainingProgramForFilter.Fields.createdDate.toUpperCase() + ", "
                + TrainingProgramForFilter.Fields.createdBy.toUpperCase() + ", "
                + TrainingProgramForFilter.Fields.duration.toUpperCase() + " or leave null if you don't want to sort";

        assertEquals(expectedMessage, actualException.getMessage());
    }

    @Test
    void When_InvalidSortDirection_Expect_ThrowsInvalidRequestForFilterTrainingProgramException() {
        RequestForFilterTrainingProgram requestDataWithInvalidSortDirection = RequestForFilterTrainingProgram.builder()
                .sortBy("duration").sortType("esc").page(1).size(10).build();
        InvalidRequestForFilterTrainingProgramException actualException = assertThrows(
                InvalidRequestForFilterTrainingProgramException.class, () -> {
                    trainingProgramService.filterTrainingProgram(requestDataWithInvalidSortDirection);
                });

        String expectedMessage = "Order to sort must be ASC, DESC or leave null if you don't want to sort";

        assertEquals(expectedMessage, actualException.getMessage());
    }

    /**
     * <p>
     * Return a list of TrainingProgramForFilter with corresponding status that pass
     * in this function
     * </p>
     *
     * @param status {@code String}
     * @return list of TrainingProgramForFilter with corresponding status
     * @author Pham Xuan Kien
     */
    private List<TrainingProgramForFilter> getTrainingProgramsByStatus(String status) {
        List<TrainingProgramForFilter> result = new ArrayList<>();

        for (TrainingProgramForFilter trainingProgram : trainingProgramsForFilterTest) {
            if (trainingProgram.getStatus().equalsIgnoreCase(status))
                result.add(trainingProgram);
        }

        return result;
    }

    @Test
    void When_ExceedKeywordMaxLength_Then_ThrowsInvalidRequestForFilterTrainingProgramException() {
        String requestData = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        ResponseEntity<ResponseObject> expected = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(),
                        "Maximum length of a search value cannot exceed "
                                + ValidationOfRequestForFilterTrainingProgram.MAX_LENGTH_OF_SEARCHVALUE,
                        null, null));

        ResponseEntity<ResponseObject> actual = trainingProgramService.getKeywordList(requestData);
        assertEquals(expected, actual);
    }

    @Test
    void When_ValidKeyword_Then_ReturnEmptySuggestedKeywordList() {
        String requestData = "azzbyy";
        String query = trainingProgramServiceUtil.constructSQLForSuggestedKeywords(requestData);
        List<String> keywordList = List.of("azzbyy");
        Mockito.when(trainingProgramJdbc.getKeywordList(query, requestData)).thenReturn(keywordList);


        ResponseEntity<ResponseObject> expected =
                ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(
                                        HttpStatus.OK.toString(),
                                        "Get search value list successfully",
                                        null,
                                        keywordList
                                )
                        );

        ResponseEntity<ResponseObject> actual = trainingProgramService.getKeywordList(requestData);
        assertEquals(expected, actual);
    }

    @Test
    void When_InputEmptyKeyWord_Then_ThrowsInvalidRequestForFilterTrainingProgramException() {
        String requestData = "";
        ResponseEntity<ResponseObject> expected = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(),
                        "Fail to get keyword list, search value must not empty", null, null));

        ResponseEntity<ResponseObject> actual = trainingProgramService.getKeywordList(requestData);
        assertEquals(expected, actual);
    }

    @Test
    void When_ValidKeyword_Then_ReturnSuggestedKeywordList() {
        String requestData = "Java";
        String query = trainingProgramServiceUtil.constructSQLForSuggestedKeywords(requestData);
        List<String> keywordList = List.of("Java", "Java Foundation", "Basic Java", "Advanced Java", "Java In Practice");
        Mockito.when(trainingProgramJdbc.getKeywordList(query, requestData)).thenReturn(keywordList);


        ResponseEntity<ResponseObject> expected =
                ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(
                                        HttpStatus.OK.toString(),
                                        "Get search value list successfully",
                                        null,
                                        keywordList
                                )
                        );

        ResponseEntity<ResponseObject> actual = trainingProgramService.getKeywordList(requestData);
        assertEquals(expected, actual);
    }
}
