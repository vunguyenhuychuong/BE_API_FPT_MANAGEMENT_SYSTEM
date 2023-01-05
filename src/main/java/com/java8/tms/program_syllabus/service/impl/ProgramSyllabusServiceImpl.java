package com.java8.tms.program_syllabus.service.impl;

import com.java8.tms.common.entity.*;
import com.java8.tms.common.meta.SyllabusStatus;
import com.java8.tms.common.meta.TrainingProgramStatus;
import com.java8.tms.common.repository.ProgramSyllabusRepository;
import com.java8.tms.common.repository.SyllabusRepository;
import com.java8.tms.common.repository.TrainingProgramRepository;
import com.java8.tms.common.repository.UserRepository;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.program_syllabus.dto.*;
import com.java8.tms.program_syllabus.exception.InvalidCreateProgramException;
import com.java8.tms.program_syllabus.exception.InvalidRequestForSaveProgramException;
import com.java8.tms.program_syllabus.exception.UserNotFoundException;
import com.java8.tms.program_syllabus.jdbc.ProgramSyllabusJDBC;
import com.java8.tms.program_syllabus.service.ProgramSyllabusService;
import com.java8.tms.program_syllabus.utils.TrainingProgramExcelConstant;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Log4j2
public class ProgramSyllabusServiceImpl implements ProgramSyllabusService {

    // The number of Draft Program
    public static final int NUMBER_DRAFT_PROGRAM = 10;
    private static final String MESSAGE_NOT_FOUND_USER_CREATE_SYLLABUS = "Can not find user created syllabus, syllabus ID: ";
    private static final String MESSAGE_NOT_FOUND_USER_UPDATE_SYLLABUS = "Can not find user updated syllabus, syllabus ID: ";
    private static final String MESSAGE_NOT_FOUND_USER_CREATE_PROGRAM = "Can not find user created program, syllabus ID: ";

    @Autowired
    ProgramSyllabusJDBC programSyllabusJdbc;
    // use for check duplicated syllabus
    Set<String> syllabussNameSet = null;
    @Autowired
    private ProgramSyllabusRepository programSyllabusRepository;
    @Autowired
    private SyllabusRepository syllabusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainingProgramRepository trainingProgramRepository;
    @Value("classpath:/template/TrainingProgramTemplate.xlsx")
    private Resource templateFileClasspathResource;

    /**
     * {@inheritDoc}
     */
    @Override
    public SaveProgramResponse saveDraftProgram(ProgramRequest requestProgram, UUID userID) {

        log.info("Start method saveDraftProgram - ProgramSyllabusService");
        // create program
        TrainingProgram program;

//          Check ID From requestProgram null -> user want to create new draft exist ->
//          id present -> user want to modify

        if (!Objects.equals(requestProgram.getId(), null)) {
            // User want to modify
            // call method Check And Get Draft by program ID
            program = checkAndGetDraftByProgramID(requestProgram.getId(), userID);

            // set created date for program
            program.setCreatedDate(new Date());
            program.setUpdatedBy(userID);
            program.setUpdatedDate(new Date());
            program.setName(requestProgram.getName());
        } else {
            // User want to create new program
            // call method and get Number of Draft
            int numDraft = trainingProgramRepository
                    .findAllByCreatedByAndStatusOrderByCreatedDateDesc(userID, TrainingProgramStatus.DRAFT)
                    .size();

            // check number draft
            if (numDraft >= NUMBER_DRAFT_PROGRAM) {
                log.error(" Admin has more than " + NUMBER_DRAFT_PROGRAM + " drafts, you can not create more.");
                throw new InvalidCreateProgramException(
                        " Admin has more than " + NUMBER_DRAFT_PROGRAM + " drafts, you can not create more.");
            }

            // set or create new program
            program = TrainingProgram.builder()
                    .name(requestProgram.getName())
                    .createdBy(userID)
                    .createdDate(new Date())
                    .updatedBy(userID)
                    .updatedDate(new Date())
                    .status(TrainingProgramStatus.DRAFT)
                    .build();
        }

        SyllabusAndError syllabusOKAndError;

        // check list syllabus from requestProgram
        if (requestProgram.getSyllabuses().isEmpty()) {
            // user want to save with program has no syllabus
            // list empty, save program
            trainingProgramRepository.save(program);
            if (!Objects.equals(requestProgram.getId(), null)) {
                // delete data program_syllabus
                programSyllabusRepository.deleteALLByProgramID(requestProgram.getId().toString());
            }
        } else {
            // check validate of list syllabus, call method
            syllabusOKAndError = checkListSyllabus(requestProgram.getSyllabuses());

            if (syllabusOKAndError.getSyllabusOk().size() == requestProgram.getSyllabuses().size()) {
                // list syllabus data OK, save
                trainingProgramRepository.save(program);
                if (!Objects.equals(requestProgram.getId(), null)) {
                    // delete data program_syllabus before saving again.
                    programSyllabusRepository.deleteALLByProgramID(requestProgram.getId().toString());
                }
                saveProgramSyllabus(program.getId(), requestProgram.getSyllabuses());

            } else {
                log.info("End method saveDraftProgram - ProgramSyllabusService - Failed");
                // list syllabus data error, return object error
                return SaveProgramResponse.builder()
                        .id(requestProgram.getId())
                        .version("")
                        .status("")
                        .name(requestProgram.getName())
                        .createdOn(new Date())
                        .nameCreatedBy(userRepository.findById(userID)
                                .orElseThrow(() -> new UserNotFoundException(
                                        "Can not find user created program, programID: " + requestProgram.getId()))
                                .getFullname())
                        .syllabusData(syllabusOKAndError)
                        .build();
            }
        }
        log.info("End method saveDraftProgram - ProgramSyllabusService - Successful");
        // save successful
        return null;
    }

    /**
     * <p>
     * Check list syllabus ID, error: duplicate syllabus, syllabus not active, Not
     * Found
     * </p>
     *
     * @param syllabusesID
     * @return SyllabusAndError
     * @author Lưu Thanh Huy
     */
    private SyllabusAndError checkListSyllabus(List<UUID> syllabusesID) {
        // create list containing syllabus data OK
        List<SyllabusResponse> syllabusOK = new ArrayList<>();
        // create list containing syllabus data error
        List<SyllabusErrorResponse> syllabusError = new ArrayList<>();
        log.info("Start method checkListSyllabus - ProgramSyllabusService");

        // start check syllabus, loop
        for (UUID syllabusID : syllabusesID) {
            try {
                // get syllabus by ID
                Syllabus tmpSyllabus = syllabusRepository.findById(syllabusID).get();

                // check status of syllabus, not ACTIVE put on list error
                if (tmpSyllabus.getStatus() != SyllabusStatus.ACTIVE) {
                    syllabusError.add(
                            SyllabusErrorResponse.builder()
                                    .id(tmpSyllabus.getId())
                                    .name(tmpSyllabus.getName())
                                    .status(tmpSyllabus.getStatus().toString())
                                    .version(tmpSyllabus.getVersion())
                                    .hours(tmpSyllabus.getHours())
                                    .days(tmpSyllabus.getDays())

                                    .createdOn(tmpSyllabus.getCreatedDate())
                                    .nameCreatedBy(userRepository.findById(tmpSyllabus.getCreatedBy())
                                            .orElseThrow(() -> new UserNotFoundException(
                                                    MESSAGE_NOT_FOUND_USER_CREATE_SYLLABUS + tmpSyllabus.getId()))
                                            .getFullname())

                                    .updatedOn(tmpSyllabus.getUpdatedDate())
//                                    .nameUpdatedBy(userRepository.findById(tmpSyllabus.getUpdatedBy())
//                                            .orElseThrow(() -> new UserNotFoundException(
//                                                    MESSAGE_NOT_FOUND_USER_UPDATE_SYLLABUS + tmpSyllabus.getId()))
//                                            .getFullname())
                                    .nameUpdatedBy(findNameUserUpdatedBy(tmpSyllabus.getUpdatedBy(), tmpSyllabus.getId()))
                                    .code(tmpSyllabus.getCode())
                                    .messageError("Syllabus is not Active")
                                    .build());
                } else {
                    // status syllabus ACTIVE
                    boolean isDataOK = true;
                    if (!Objects.equals(syllabusOK, null)) {
                        // loop for list OK
                        for (SyllabusResponse tmpOK : syllabusOK) {
                            // check duplicate syllabus by name, duplicate put on list error
                            if (Objects.equals(tmpOK.getName(), tmpSyllabus.getName())) {

                                syllabusError.add(
                                        SyllabusErrorResponse.builder()
                                                .id(tmpSyllabus.getId())
                                                .name(tmpSyllabus.getName())
                                                .status(tmpSyllabus.getStatus().toString())
                                                .version(tmpSyllabus.getVersion())
                                                .hours(tmpSyllabus.getHours())
                                                .days(tmpSyllabus.getDays())

                                                .createdOn(tmpSyllabus.getCreatedDate())
                                                .nameCreatedBy(userRepository.findById(tmpSyllabus.getCreatedBy())
                                                        .orElseThrow(() -> new UserNotFoundException(
                                                                MESSAGE_NOT_FOUND_USER_CREATE_SYLLABUS
                                                                        + tmpSyllabus.getId()))
                                                        .getFullname())

                                                .updatedOn(tmpSyllabus.getUpdatedDate())
//                                                .nameUpdatedBy(userRepository.findById(tmpSyllabus.getUpdatedBy())
//                                                        .orElseThrow(() -> new UserNotFoundException(
//                                                                MESSAGE_NOT_FOUND_USER_UPDATE_SYLLABUS
//                                                                        + tmpSyllabus.getId()))
//                                                        .getFullname())
                                                .nameUpdatedBy(findNameUserUpdatedBy(tmpSyllabus.getUpdatedBy(), tmpSyllabus.getId()))
                                                .code(tmpSyllabus.getCode())
                                                .messageError("Duplicate syllabus " + tmpOK.getName() + " version "
                                                        + tmpOK.getVersion())
                                                .build());
                                isDataOK = false;
                                break;
                            }
                        }
                    }
                    // after checking finished, add list OK
                    if (isDataOK) {
                        syllabusOK.add(
                                SyllabusResponse.builder()
                                        .id(tmpSyllabus.getId())
                                        .name(tmpSyllabus.getName())
                                        .status(tmpSyllabus.getStatus().toString())
                                        .version(tmpSyllabus.getVersion())
                                        .hours(tmpSyllabus.getHours())
                                        .days(tmpSyllabus.getDays())

                                        .createdOn(tmpSyllabus.getCreatedDate())
                                        .nameCreatedBy(userRepository.findById(tmpSyllabus.getCreatedBy())
                                                .orElseThrow(() -> new UserNotFoundException(
                                                        MESSAGE_NOT_FOUND_USER_CREATE_SYLLABUS
                                                                + tmpSyllabus.getId()))
                                                .getFullname())

                                        .updatedOn(tmpSyllabus.getCreatedDate())
//                                        .nameUpdatedBy(userRepository.findById(tmpSyllabus.getUpdatedBy())
//                                                .orElseThrow(() -> new UserNotFoundException(
//                                                        MESSAGE_NOT_FOUND_USER_UPDATE_SYLLABUS
//                                                                + tmpSyllabus.getId()))
//                                                .getFullname())
                                        .nameUpdatedBy(findNameUserUpdatedBy(tmpSyllabus.getUpdatedBy(), tmpSyllabus.getId()))
                                        .code(tmpSyllabus.getCode())
                                        .build());
                    }
                }
            } catch (NoSuchElementException e) {
                log.error("Syllabus Error: Not Found");
                // syllabus not found, add list error
                syllabusError.add(
                        SyllabusErrorResponse.builder()
                                .id(syllabusID)
                                .name("")
                                .status("")
                                .version("")
                                .hours(0)
                                .days(0)
                                .createdOn(null)
                                .nameCreatedBy("")
                                .updatedOn(null)
                                .nameUpdatedBy("")
                                .code("")
                                .messageError("Syllabus not found with ID: " + syllabusID)
                                .build());
            }
        }
        log.info("End method checkListSyllabus - ProgramSyllabusService");
        // after check all syllabus, return object containing list error and OK
        return SyllabusAndError.builder()
                .syllabusOk(syllabusOK)
                .syllabusError(syllabusError)
                .build();
    }

    /**
     * <p>
     * Check programID is exist, program created by other user, program status is
     * draft
     * </p>
     *
     * @param programID
     * @param userID
     * @return TrainingProgram
     * @author Lưu Thanh Huy
     */
    private TrainingProgram checkAndGetDraftByProgramID(UUID programID, UUID userID) {
        log.info("Start method checkAndGetDraftByProgramID - ProgramSyllabusService");

        // get program by program ID
        TrainingProgram program = trainingProgramRepository.findById(programID)
                .orElseThrow(() -> new InvalidRequestForSaveProgramException("Program Not Found"));

        // check User from DB and Client are same
        if (!Objects.equals(program.getCreatedBy(), userID)) {
            throw new InvalidRequestForSaveProgramException("Program is created by other user");
        }

        // check program status is draft
        if (program.getStatus() != TrainingProgramStatus.DRAFT) {
            throw new InvalidRequestForSaveProgramException("Program Status is not Draft");
        }

        log.info("End method checkAndGetDraftByProgramID - ProgramSyllabusService");
        return program;
    }

    /**
     * <p>
     * Save data in Program_Syllabus (programID, syllabusID, position)
     * </p>
     *
     * @param programID
     * @param syllabusesID
     * @author Lưu Thanh Huy
     */
    private void saveProgramSyllabus(UUID programID, List<UUID> syllabusesID) {
        // set position for syllabus in program
        int position = 1;
        for (UUID syllabusID : syllabusesID) {
            ProgramSyllabus tmp = ProgramSyllabus.builder()
                    .id(ProgramSyllabusId.builder()
                            .trainingProgramId(programID).syllabusId(syllabusID).build())
                    .position(position++) // increase position
                    .build();
            programSyllabusRepository.save(tmp); // save program_syllabus table
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteDraftProgram(UUID userID, UUID programID) {
        log.info("Start method deleteDraftProgram - ProgramSyllabusService");
        // call method, get and check progarm
        TrainingProgram trainingProgram = checkAndGetDraftByProgramID(programID, userID);
        try {
            // delete program
            trainingProgramRepository.delete(trainingProgram);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            log.info("End method deleteDraftProgram - ProgramSyllabusService - Failed");
            return false;
        }
        log.info("End method deleteDraftProgram - ProgramSyllabusService - Successful");
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SaveProgramResponse saveCompleteProgram(ProgramRequest requestProgram, UUID userID) {
        log.info("Start method saveCompleteProgram - ProgramSyllabusService");
        // create program
        TrainingProgram program = new TrainingProgram();

//      Check ID From request null -> user want to create new program  
//      Check ID is present -> user want to create new program from draft

        if (!Objects.equals(requestProgram.getId(), null)) {

            // Check And Get Draft
            program = checkAndGetDraftByProgramID(requestProgram.getId(), userID);

        }
        // check list syllabus, call method
        SyllabusAndError syllabusOKAndError = checkListSyllabus(requestProgram.getSyllabuses());

        if (syllabusOKAndError.getSyllabusOk().size() == requestProgram.getSyllabuses().size()) {

            // get current version of program name
            String version = trainingProgramRepository.getCurrentVersionWithProgramName(requestProgram.getName());

            if (!Objects.equals(null, version)) {
                int iVersion = Integer.parseInt(version);
                iVersion++; // increase version
                // convert version to String
                version = String.valueOf(iVersion).concat(".0");
            } else {
                version = "1.0";
            }
            // set new value for program
            program.setName(requestProgram.getName());
            program.setCreatedBy(userID);
            program.setCreatedDate(new Date());
            program.setUpdatedBy(userID);
            program.setUpdatedDate(new Date());
            program.setVersion(version);
            program.setStatus(TrainingProgramStatus.INACTIVE);

            trainingProgramRepository.save(program);

            // check program is draft, delete all data program_syllabus
            if (!Objects.equals(requestProgram.getId(), null)) {
                programSyllabusRepository.deleteALLByProgramID(requestProgram.getId().toString());
            }

            saveProgramSyllabus(program.getId(), requestProgram.getSyllabuses());

        } else {

            return SaveProgramResponse.builder()
                    .id(requestProgram.getId())
                    .version("")
                    .createdOn(new Date())
                    .status("")
                    .name(requestProgram.getName())
                    .nameCreatedBy(userRepository.findById(userID)
                            .orElseThrow(() -> new UserNotFoundException(
                                    MESSAGE_NOT_FOUND_USER_CREATE_PROGRAM + requestProgram.getId()))
                            .getFullname())
                    .syllabusData(syllabusOKAndError)
                    .build();
        }
        log.info("End method saveCompleteProgram - ProgramSyllabusService");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SyllabusResponse> searchSyllabusByKeyword(String keyword) {
        log.info("Start method searchSyllabusByKeyword - ProgramSyllabusService");
        // call method searchSyllabusByKeyword
        List<Syllabus> syllabuses = syllabusRepository.searchSyllabusByKeyword(keyword, SyllabusStatus.ACTIVE);

        if (syllabuses.isEmpty()) {
            return Collections.emptyList();
        }

        // convert Syllabus to SyllabusResponse
        List<SyllabusResponse> syllReturns = new ArrayList<>();

        syllabuses.forEach(syllabus -> {
            SyllabusResponse tmpSylReturn = SyllabusResponse.builder()
                    .id(syllabus.getId())
                    .name(syllabus.getName())
                    .status(syllabus.getStatus().toString())
                    .version(syllabus.getVersion())

                    .hours(syllabus.getHours())
                    .days(syllabus.getDays())

                    .createdOn(syllabus.getCreatedDate())
                    .nameCreatedBy(userRepository.findById(syllabus.getCreatedBy())
                            .orElseThrow(() -> new UserNotFoundException(
                                    MESSAGE_NOT_FOUND_USER_CREATE_SYLLABUS + syllabus.getId()))
                            .getFullname())

                    .updatedOn(syllabus.getUpdatedDate())
//                    .nameUpdatedBy(userRepository.findById(syllabus.getUpdatedBy())
//                            .orElseThrow(() -> new UserNotFoundException(
//                                    MESSAGE_NOT_FOUND_USER_UPDATE_SYLLABUS + syllabus.getId()))
//                            .getFullname())
                    .nameUpdatedBy(findNameUserUpdatedBy(syllabus.getUpdatedBy(), syllabus.getId()))
                    .code(syllabus.getCode())
                    .build();
            syllReturns.add(tmpSylReturn);
        });
        log.info("End method searchSyllabusByKeyword - ProgramSyllabusService");
        return syllReturns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DetailDraftProgramResponse getDraftProgram(UUID programID, UUID userID) {
        log.info("Start method getDraftProgram - ProgramSyllabusService");
        // call method checkAndGetDraftByProgramID
        TrainingProgram program = checkAndGetDraftByProgramID(programID, userID);
        List<SyllabusResponse> syllabusReturn = new ArrayList<>();
        // call method findAllSyllabusByProgramID, get list syllabus of program
        List<Syllabus> syllabuses = syllabusRepository.findAllSyllabusByProgramID(programID.toString());

        int hour = 0;
        int day = 0;
        if (!syllabuses.isEmpty()) {
            // convert list Syllabus to Syllabus return for client
            for (Syllabus syllabus : syllabuses) {
                hour += syllabus.getHours(); // sum hours of each syllabus
                day += syllabus.getDays(); // sum days of each syllabus

                SyllabusResponse tmp = SyllabusResponse.builder()
                        .id(syllabus.getId())
                        .name(syllabus.getName())
                        .status(syllabus.getStatus().toString())
                        .version(syllabus.getVersion())

                        .hours(syllabus.getHours())
                        .days(syllabus.getDays())

                        .createdOn(syllabus.getCreatedDate())
                        .nameCreatedBy(userRepository.findById(syllabus.getCreatedBy())
                                .orElseThrow(() -> new UserNotFoundException(
                                        MESSAGE_NOT_FOUND_USER_CREATE_SYLLABUS + syllabus.getId()))
                                .getFullname())

                        .updatedOn(syllabus.getUpdatedDate())
//                        .nameUpdatedBy(userRepository.findById(syllabus.getUpdatedBy())
//                                .orElseThrow(() -> new UserNotFoundException(
//                                        MESSAGE_NOT_FOUND_USER_UPDATE_SYLLABUS + syllabus.getId()))
//                                .getFullname())
                        .nameUpdatedBy(findNameUserUpdatedBy(syllabus.getUpdatedBy(), syllabus.getId()))
                        .code(syllabus.getCode())
                        .build();

                syllabusReturn.add(tmp);
            }
        } else {
            syllabusReturn = List.of();
        }
        log.info("End method getDraftProgram - ProgramSyllabusService");
        // convert to programResponse, return
        return DetailDraftProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .status(program.getStatus().toString())

                .nameCreatedBy(userRepository.findById(program.getCreatedBy())
                        .orElseThrow(() -> new UserNotFoundException(
                                MESSAGE_NOT_FOUND_USER_CREATE_PROGRAM + program.getId()))
                        .getFullname())
                .createdOn(program.getCreatedDate())

                .days(day)
                .hours(hour)

                .syllabuses(syllabusReturn)
                .build();
    }

    /**
     * <p>
     * Sum days and hours of list syllabus of program
     * </p>
     *
     * @param programID
     * @return DataTotalFromSyllabus
     * @author Lưu Thanh Huy
     */
    private DataTotalFromSyllabus getTotalDayAndHoursByProgramID(UUID programID) {
        int day = 0;
        int hours = 0;

        // call method findAllSyllabusByProgramID
        List<Syllabus> syllabuses = syllabusRepository.findAllSyllabusByProgramID(programID.toString());

        // sum days and hours of each Syllabus
        if (!Objects.equals(syllabuses, null)) {
            for (Syllabus syllabus : syllabuses) {
                day += syllabus.getDays();
                hours += syllabus.getHours();
            }
        }

        return DataTotalFromSyllabus.builder().days(day).hours(hours).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DraftProgramResponse> getListDraftProgramByUserID(UUID userID) {
        log.info("Start method getListDraftProgramByUserID - ProgramSyllabusService");
        // call method, get list program with status draft of a user
        List<TrainingProgram> programs = trainingProgramRepository.findAllByCreatedByAndStatusOrderByCreatedDateDesc(
                userID,
                TrainingProgramStatus.DRAFT);

        if (Objects.equals(programs, null)) {
            log.info("End method getListDraftProgramByUserID - ProgramSyllabusService - null");
            return Collections.emptyList();
        }
        // convert program to draft program response
        List<DraftProgramResponse> drafts = new ArrayList<>();

        programs.forEach(program -> {

            DataTotalFromSyllabus total = getTotalDayAndHoursByProgramID(program.getId());

            DraftProgramResponse tmp =
                    DraftProgramResponse.builder()
                            .id(program.getId())
                            .name(program.getName())
                            .status(program.getStatus().toString())

                            .nameCreatedBy(userRepository.findById(program.getCreatedBy())
                                    .orElseThrow(() -> new UserNotFoundException(
                                            MESSAGE_NOT_FOUND_USER_CREATE_PROGRAM + program.getId()))
                                    .getFullname())
                            .createdOn(program.getCreatedDate())

                            .days(total.getDays()).hours(total.getHours()).build();

            drafts.add(tmp);
        });
        log.info("End method getListDraftProgramByUserID - ProgramSyllabusService");
        return drafts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArrayResource getTemplateResource() {
        try {
            // Getting template file
            log.info("Start method getTemplateFile - ProgramSyllabusService");
            return new ByteArrayResource(templateFileClasspathResource.getInputStream().readAllBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            log.info("Start method getTemplateFile - ProgramSyllabusService - null");
            return null;
        }

    }

    /**
     * <p>
     * Read import file for add training program
     * </p>
     *
     * @param sheet
     * @return
     * @throws InvalidCreateProgramException
     * @author Nguyen Quoc Bao
     */
    public SaveProgramResponse readProgramFromFile(XSSFSheet sheet) throws InvalidCreateProgramException {
        SaveProgramResponse trainingProgramRes = new SaveProgramResponse();
        trainingProgramRes.setNameCreatedBy(getUserFromContext().getFullname());
        trainingProgramRes.setVersion("");

        trainingProgramRes.setStatus("");

        XSSFCell programNameCell = sheet.getRow(TrainingProgramExcelConstant.ROW_INDEX_PROGRAM_NAME).getCell(0);
        String programName ;
        try {
          programName = programNameCell.getStringCellValue();
          programName = programName.trim();

        } catch (IllegalStateException e) {
            throw new InvalidCreateProgramException("Invalid cell type please set all cell is text format");
        }
        if (validateProgramNameLength(programName) ) {

            trainingProgramRes.setName(programName);

        } else {
            throw new InvalidCreateProgramException(
                    "Invalid name, name of training program must be from 5 to 100 character");
        }

        log.debug("Read syllabus list");
        trainingProgramRes.setSyllabusData(getSyllabusList(sheet));

        return trainingProgramRes;
    }

    /**
     * <p>
     * Get syllabus list from file
     * </p>
     *
     * @param sheet
     * @return SyllabusDataOkAndError
     * @throws InvalidCreateProgramException
     * @author Nguyen Quoc Bao
     */
    private SyllabusAndError getSyllabusList(XSSFSheet sheet) throws InvalidCreateProgramException {


        List<SyllabusResponse> dataOK = new ArrayList<>();
        List<SyllabusErrorResponse> dataError = new ArrayList<>();
        SyllabusAndError syllabusData = new SyllabusAndError();

        log.info("Start method getSyllabusList - ProgramSyllabusService");

        Iterator<?> iterator = sheet.iterator();
        while (iterator.hasNext()) {

            XSSFRow row = (XSSFRow) iterator.next();
            Iterator<?> cellIterator = row.cellIterator();

            Syllabus syllabus = readSyllabus(cellIterator);

            if ((syllabus.getName().trim().isEmpty()
                    || syllabus.getCode().trim().isEmpty())
                    || row.getRowNum() == 0
                    || row.getRowNum() == 1)
                continue;

            Optional<Syllabus> rs = programSyllabusJdbc.findAvailableSyllabus(syllabus);


            if (!rs.isPresent()) {

                SyllabusErrorResponse data =
                        SyllabusErrorResponse.builder()

                                .name(syllabus.getName())
                                .code(syllabus.getCode())
                                .version("")
                                .status("")
                                .createdOn(null)
                                .updatedOn(null)
                                .nameCreatedBy("")
                                .nameUpdatedBy("")
                                .messageError("Cannot find available syllabus")
                                .build();
                dataError.add(data);
            } else {
                syllabus = rs.get();

                List<String> msg = validateSyllabus(syllabus);
                if (!msg.isEmpty()) {
                    String statusName = syllabus.getStatus().name();
                    String version = syllabus.getVersion();
                    if(SyllabusStatus.DRAFT.toString().equals(syllabus.getStatus().toString())){
                        statusName = "";
                        version = "";

                    }
                    SyllabusErrorResponse data =
                            SyllabusErrorResponse.builder()
                                    .name(syllabus.getName())
                                    .code(syllabus.getCode())
                                    .nameCreatedBy(userRepository.findById(syllabus.getCreatedBy()).get().getFullname())
                                    .updatedOn(syllabus.getUpdatedDate())
                                    .nameUpdatedBy(userRepository.findById(syllabus.getCreatedBy()).get().getFullname())
                                    .version(syllabus.getVersion())
                                    .messageError(msg.toString())
                                    .status(statusName)
                                    .version(version)
                                    .build();

                    dataError.add(data);
                } else {
                    String statusName = syllabus.getStatus().name();

                    SyllabusResponse data =
                            SyllabusResponse.builder()
                                    .id(syllabus.getId())
                                    .name(syllabus.getName())
                                    .code(syllabus.getCode())
                                    .version(syllabus.getVersion())
                                    .status(statusName)
                                    .createdOn(syllabus.getCreatedDate())
                                    .nameCreatedBy(userRepository.findById(syllabus.getCreatedBy()).get().getFullname())
                                    .updatedOn(syllabus.getUpdatedDate())
                                    .nameUpdatedBy(userRepository.findById(syllabus.getCreatedBy()).get().getFullname())
                                    .days(syllabus.getDays())
                                    .hours(syllabus.getHours())

                                    .build();

                    dataOK.add(data);
                    // add data into set to check duplicated
                    syllabussNameSet.add(syllabus.getName());
                }
            }

        }

        syllabusData.setSyllabusOk(dataOK);
        syllabusData.setSyllabusError(dataError);
        log.info("End method getSyllabusList - ProgramSyllabusService");
        return syllabusData;
    }

    /**
     * <p>
     * read syllabus list from file
     * </p>
     *
     * @param cellIterator
     * @return
     * @throws InvalidCreateProgramException
     * @author Nguyen Quoc Bao
     */
    public Syllabus readSyllabus(Iterator<?> cellIterator) throws InvalidCreateProgramException {
        log.info("Start method readSyllabus - ProgramSyllabusService");
        Syllabus syllabus = Syllabus.builder().name("").code("").build();// build to prevent blank cell at excel sheet
        while (cellIterator.hasNext()) {


            try {
                XSSFCell cell = (XSSFCell) cellIterator.next();


                int columnIndex = cell.getColumnIndex();

                switch (columnIndex) {

                    case TrainingProgramExcelConstant.COLUMN_INDEX_SYLLABUS_NAME:
                        syllabus.setName(cell.getStringCellValue().trim());

                        break;
                    case TrainingProgramExcelConstant.COLUMN_INDEX_SYLLABUS_CODE:
                        syllabus.setCode(cell.getStringCellValue().trim());
                        break;
                    case TrainingProgramExcelConstant.COLUMN_INDEX_SYLLABUS_VERSION:

                        if (cell.getCellType() != CellType.BLANK) {

                            if (cell.getCellType() == CellType.NUMERIC) {
                                String data = String.valueOf(cell.getNumericCellValue());
                                syllabus.setVersion(data.trim());
                            } else {
                                syllabus.setVersion(cell.getStringCellValue().trim());
                            }
                        }
                        break;
                    default:
                        break;
                }

            } catch (IllegalStateException e) {
                throw new InvalidCreateProgramException("Invalid cell type please set all cell is text format");
            }
        }

        log.info("End method readSyllabus - ProgramSyllabusService");
        return syllabus;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SaveProgramResponse readTrainingProgram(MultipartFile file) throws InvalidCreateProgramException {
        SaveProgramResponse res = null;
        syllabussNameSet = new HashSet<>();

        log.info("Start method readTrainingProgram - ProgramSyllabusService");
        try {

            if (!validateProgramFileImport(file))
                throw new InvalidCreateProgramException(
                        "Invalid file input, file input must be under 1 MB and correct file excel format(xlsx)");
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());



            XSSFSheet sheet = workbook.getSheetAt(0);// get first sheet

            res = readProgramFromFile(sheet);
            log.info(res);

            if(res.getSyllabusData().getSyllabusOk().isEmpty()&&res.getSyllabusData().getSyllabusError().isEmpty()){
                throw new NullPointerException();
            }
        } catch (IOException e) {
            log.debug(e);
        } catch (NotOfficeXmlFileException e) {

            throw new InvalidCreateProgramException(
                    "Invalid file input, file input must be under 1 MB and correct file excel format(xlsx)");
        } catch (InvalidCreateProgramException e){
            throw new InvalidCreateProgramException(e.getMessage());
        }
        catch (NullPointerException e){
            log.info(e);
            throw new InvalidCreateProgramException(
                    "Invalid file input, file input must be correct file excel format(xlsx)");
        }

        log.info("End method readTrainingProgram - ProgramSyllabusService");
        return res;
    }

    /**
     * <p>
     * Save training program if it has some version, it increase to new version
     * </p>
     *
     * @param res
     * @throws InvalidCreateProgramException
     * @author Mguyen Quoc Bao
     */
    @Override
    public SaveProgramResponse saveTrainingProgram(SaveProgramResponse res) throws InvalidCreateProgramException {

        log.info("Start method saveTrainingProgram - ProgramSyllabusService");
        TrainingProgram program = new TrainingProgram();


        // validate syllabus list
        validateListSyllabus(res.getSyllabusData().getSyllabusOk());
        Date date = new Date();
        program.setCreatedDate(date);
        program.setStatus(TrainingProgramStatus.INACTIVE);
        program.setTemplate(false);
        program.setUpdatedDate(date);
        program.setName(res.getName());


        double version = 1.0;

        Optional<?> tmp = programSyllabusJdbc.findNewestVersionOfTrainingProgram(program.getName());
        if (tmp.isPresent()) {
            log.info("Training Program already has version, system will increase current version and save");
            // find newest version and if it already exist increase it
            TrainingProgram x = (TrainingProgram) tmp.get();

            version = Math.floor(Double.parseDouble(x.getVersion()) + 1);
        }

        User user = getUserFromContext();
        program.setCreatedBy(user.getId());
        program.setUpdatedBy(user.getId());

        res.setVersion(String.valueOf(version));
        program.setVersion(String.valueOf(version));

        TrainingProgram programData = trainingProgramRepository.save(program);
        res.setId(programData.getId());
        List<SyllabusResponse> programSyllabuses = res.getSyllabusData().getSyllabusOk();


        for (int i = 0; i < programSyllabuses.size(); i++) {

            ProgramSyllabusId key = new ProgramSyllabusId();
            key.setSyllabusId(programSyllabuses.get(i).getId());
            key.setTrainingProgramId(programData.getId());
            ProgramSyllabus p = new ProgramSyllabus();
            p.setId(key);
            p.setPosition(i);

            programSyllabusRepository.save(p);

        }



        log.info("End method saveTrainingProgram - ProgramSyllabusService");
        return res;
    }

    /**
     * <p>
     * Validate size of file import
     * </p>
     *
     * @param file
     * @return
     * @throws InvalidCreateProgramException
     * @author Nguyen Quoc Bao
     */
    private boolean validateProgramFileImport(MultipartFile file) {
        return TrainingProgramExcelConstant.TRANING_PROGRAM_FILE_TYPE.equals(file.getContentType())
                && file.getSize() < TrainingProgramExcelConstant.FILE_SIZE;

    }

    /**
     * <p>
     * Validate Syllabus
     * </p>
     *
     * @param syllabus
     * @return message
     * @author Nguyen Quoc Bao
     */
    public List<String> validateSyllabus(Syllabus syllabus) {
        List<String> errorMsgList = new ArrayList<>();

        if (syllabussNameSet.contains(syllabus.getName())) {

            errorMsgList.add(
                    "Duplicated syllabus " + "'" + syllabus.getName() + "'" + " at version: " + syllabus.getVersion());
        }
        if (SyllabusStatus.DEACTIVE.toString().equals(syllabus.getStatus().toString())) {

            errorMsgList.add("Syllabus status is " + SyllabusStatus.DEACTIVE.toString());
        }
        if (SyllabusStatus.DELETED.toString().equals(syllabus.getStatus().toString())) {

            errorMsgList.add("Syllabus status is " + SyllabusStatus.DELETED.toString());
        }
        if (SyllabusStatus.DRAFT.toString().equals(syllabus.getStatus().toString())) {

            errorMsgList.add("Can not find available syllabus");
        }
        if (SyllabusStatus.PENDING.toString().equals(syllabus.getStatus().toString())) {

            errorMsgList.add("Syllabus status is " + SyllabusStatus.PENDING.toString());
        }
        if (syllabus.getName().length() > 255) {
            errorMsgList.add("Syllabus's name must under 255 characters");
        }
        if (syllabus.getCode().length() > 255) {
            errorMsgList.add("Syllabus's code must under 255 characters");
        }
        if (syllabus.getVersion().length() > 10 && syllabus.getVersion() != null) {
            errorMsgList.add("Syllabus's version must under 10 characters");
        }
        return errorMsgList;
    }

    private boolean validateProgramNameLength(String programName) {


        return programName.length() >= 5 && programName.length() <= 100;
    }

    /**
     * <p>
     * Valiadate size of syllabus
     * </p>
     *
     * @param list
     * @author Nguyen Quoc Bao
     */
    private void validateListSyllabus(List<SyllabusResponse> list) {
        // check size of syllabus data
        if (list.size() > 10)
            throw new InvalidCreateProgramException("Training Program can not has more than 10 syllabus");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserFromContext() {
        // get user email from token
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        // get user by email
        return userRepository.findByEmail(userPrinciple.getEmail()).orElseThrow(
                () -> new UserNotFoundException("Can not find user with email: " + userPrinciple.getEmail()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DetailDraftProgramResponse getProgram(UUID programID) {
        log.info("Start method getProgram - ProgramSyllabusService");
        // get program by id
        TrainingProgram program = checkAndGetProgram(programID);

        List<SyllabusResponse> syllabusReturn = new ArrayList<>();
        // call method findAllSyllabusByProgramID, get list syllabus of program
        List<Syllabus> syllabuses = syllabusRepository.findAllSyllabusByProgramID(programID.toString());

        int hour = 0;
        int day = 0;
        if (!syllabuses.isEmpty()) {
            // convert list Syllabus to Syllabus return for client
            for (Syllabus syllabus : syllabuses) {
                hour += syllabus.getHours(); // sum hours of each syllabus
                day += syllabus.getDays(); // sum days of each syllabus

                SyllabusResponse tmp = SyllabusResponse.builder()
                        .id(syllabus.getId())
                        .name(syllabus.getName())
                        .status(syllabus.getStatus().toString())
                        .version(syllabus.getVersion())

                        .hours(syllabus.getHours())
                        .days(syllabus.getDays())

                        .createdOn(syllabus.getCreatedDate())
                        .nameCreatedBy(userRepository.findById(syllabus.getCreatedBy())
                                .orElseThrow(() -> new UserNotFoundException(
                                        MESSAGE_NOT_FOUND_USER_CREATE_SYLLABUS + syllabus.getId()))
                                .getFullname())

                        .updatedOn(syllabus.getUpdatedDate())
//                        .nameUpdatedBy(userRepository.findById(syllabus.getUpdatedBy())
//                                .orElseThrow(() -> new UserNotFoundException(
//                                        MESSAGE_NOT_FOUND_USER_UPDATE_SYLLABUS + syllabus.getId()))
//                                .getFullname())
                        .nameUpdatedBy(findNameUserUpdatedBy(syllabus.getUpdatedBy(), syllabus.getId()))
                        .code(syllabus.getCode())
                        .build();

                syllabusReturn.add(tmp);
            }
        } else {
            syllabusReturn = List.of();
        }
        log.info("End method getProgram - ProgramSyllabusService");
        // convert to programResponse, return
        return DetailDraftProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .status(program.getStatus().toString())

                .nameCreatedBy(userRepository.findById(program.getCreatedBy())
                        .orElseThrow(() -> new UserNotFoundException(
                                MESSAGE_NOT_FOUND_USER_CREATE_PROGRAM + program.getId()))
                        .getFullname())
                .createdOn(program.getCreatedDate())


                .days(day)
                .hours(hour)

                .syllabuses(syllabusReturn)
                .build();
    }

    @Override
    public SaveProgramResponse editProgram(EditProgramRequest requestProgram, UUID userID) {
        log.info("Start method editProgram - ProgramSyllabusService");

        // get program by id and check program status
        TrainingProgram program = checkAndGetProgram(requestProgram.getId());

        // check program is used
        if (program.getTrainingClass() != null) {
            throw new InvalidRequestForSaveProgramException("Program is being used by a training class and can not be edited");
        }

        List<Syllabus> syllabuses = syllabusRepository.findAllSyllabusByProgramID(requestProgram.getId().toString());
        List<UUID> oldSyllabuses = new ArrayList<>();
        syllabuses.forEach(syllabus -> oldSyllabuses.add(syllabus.getId()));

        // check list syllabus is different
        boolean check = checkNewAndOldListSyllabusIsDifferent(oldSyllabuses, requestProgram.getSyllabuses());


        if (!check) {
            log.info("End method editProgram - ProgramSyllabusService - Successful");
            // no change
            return null;
        }

        SyllabusAndError syllabusOKAndError = checkListSyllabus(requestProgram.getSyllabuses());

        if (syllabusOKAndError.getSyllabusOk().size() != requestProgram.getSyllabuses().size()) {

            log.info("End method editProgram - ProgramSyllabusService - Failed");
            return SaveProgramResponse.builder()
                    .id(requestProgram.getId())
                    .name(program.getName())
                    .nameCreatedBy(userRepository.findById(userID)
                            .orElseThrow(() -> new UserNotFoundException(
                                    MESSAGE_NOT_FOUND_USER_CREATE_PROGRAM + requestProgram.getId()))
                            .getFullname())
                    .createdOn(program.getCreatedDate())
                    .syllabusData(syllabusOKAndError)
                    .build();
        }

        String[] result = program.getVersion().split(Pattern.quote("."));
        int iVersion = Integer.parseInt(result[1]) + 1;
        String version = result[0] + "." + iVersion;

        TrainingProgram newProgram = TrainingProgram.builder()
                .name(program.getName())
                .status(program.getStatus())
                .createdBy(program.getCreatedBy())
                .createdDate(program.getCreatedDate())
                .updatedBy(userID)
                .updatedDate(new Date())
                .version(version)
                .build();

        program.setStatus(TrainingProgramStatus.DELETED);
        trainingProgramRepository.save(program);
        trainingProgramRepository.save(newProgram);
        saveProgramSyllabus(newProgram.getId(), requestProgram.getSyllabuses());

        log.info("End method editProgram - ProgramSyllabusService - Successful");
        // edit successful
        return null;
    }

    private TrainingProgram checkAndGetProgram(UUID programID) {
        log.info("Start method checkAndGetForEditProgram - ProgramSyllabusService");

        // get program by program ID
        TrainingProgram program = trainingProgramRepository.findById(programID)
                .orElseThrow(() -> new InvalidRequestForSaveProgramException("Program Not Found"));

        // check program status is inactive or active
        if (!(program.getStatus() == TrainingProgramStatus.ACTIVE
                || program.getStatus() == TrainingProgramStatus.INACTIVE)) {
            throw new InvalidRequestForSaveProgramException("Program Status is not ACTIVE or INACTIVE");
        }

        log.info("End method checkAndGetForEditProgram - ProgramSyllabusService");
        return program;
    }

    private boolean checkNewAndOldListSyllabusIsDifferent(List<UUID> oldSyllabus, List<UUID> newSyllabus) {
        if (oldSyllabus.size() != newSyllabus.size()) {
            return true;
        }

        for (int i = 0; i < oldSyllabus.size(); i++) {
            if (oldSyllabus.get(i).compareTo(newSyllabus.get(i)) != 0) {
                return true;
            }
        }
        return false;
    }

    private String findNameUserUpdatedBy(UUID userId, UUID syllabusID) {
        if (userId == null) {
            return "";
        }

        if (userRepository.findById(userId).isEmpty()) {
            return "";
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        MESSAGE_NOT_FOUND_USER_UPDATE_SYLLABUS + syllabusID))
                .getFullname();
    }

}
