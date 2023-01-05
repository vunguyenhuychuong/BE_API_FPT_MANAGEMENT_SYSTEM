package com.java8.tms.training_program.service.impl;

import com.java8.tms.common.dto.Pagination;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.dto.TrainingProgramDTO;
import com.java8.tms.common.entity.ProgramSyllabus;
import com.java8.tms.common.entity.TrainingClass;
import com.java8.tms.common.entity.TrainingProgram;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.exception.model.ResourceNotFoundException;
import com.java8.tms.common.meta.ResponseStatus;
import com.java8.tms.common.meta.TrainingProgramStatus;
import com.java8.tms.common.payload.request.UpdateTrainingProgramForm;
import com.java8.tms.common.repository.TrainingProgramRepository;
import com.java8.tms.syllabus.dto.SyllabusDTO;
import com.java8.tms.syllabus.dto.UserDTO;
import com.java8.tms.syllabus.service.impl.SyllabusServiceImpl;
import com.java8.tms.training_program.dto.*;
import com.java8.tms.training_program.exception.InvalidRequestForFilterTrainingProgramException;
import com.java8.tms.training_program.exception.InvalidRequestForGetTrainingProgramException;
import com.java8.tms.training_program.jdbc.TrainingProgramJdbc;
import com.java8.tms.training_program.service.TrainingProgramService;
import com.java8.tms.training_program.validation.ValidationOfRequestForFilterTrainingProgram;
import com.java8.tms.training_program.validation.ValidationOfRequestForGetTrainingProgram;
import com.java8.tms.user.service.impl.UserServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class TrainingProgramServiceImpl implements TrainingProgramService {

    private final TrainingProgramRepository trainingProgramRepository;
    private final ModelMapper modelMapper;
    private final UserServiceImpl userService;
    private final JdbcTemplate jdbcTemplate;
    private final TrainingProgramJdbc trainingProgramJdbc;
    private final ValidationOfRequestForGetTrainingProgram validationOfRequestForGetTrainingProgram;
    private final ValidationOfRequestForFilterTrainingProgram validationOfRequestForFilterTrainingProgram;
    private final TrainingProgramServiceUtil trainingProgramServiceUtil;
    private final SyllabusServiceImpl syllabusService;

    @Autowired
    public TrainingProgramServiceImpl(TrainingProgramRepository trainingProgramRepository, ModelMapper modelMapper, UserServiceImpl userService, JdbcTemplate jdbcTemplate, TrainingProgramJdbc trainingProgramJdbc, ValidationOfRequestForGetTrainingProgram validationOfRequestForGetTrainingProgram, ValidationOfRequestForFilterTrainingProgram validationOfRequestForFilterTrainingProgram, TrainingProgramServiceUtil trainingProgramServiceUtil, SyllabusServiceImpl syllabusService) {
        this.trainingProgramRepository = trainingProgramRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
        this.trainingProgramJdbc = trainingProgramJdbc;
        this.validationOfRequestForGetTrainingProgram = validationOfRequestForGetTrainingProgram;
        this.validationOfRequestForFilterTrainingProgram = validationOfRequestForFilterTrainingProgram;
        this.trainingProgramServiceUtil = trainingProgramServiceUtil;
        this.syllabusService = syllabusService;
    }


    /**
     * {@inheritDoc}
     */

    @Override
    public ResponseEntity<ResponseObject> getAllTrainingProgram(int page, int size)
            throws InvalidRequestForGetTrainingProgramException {
        log.info("Start method get all training program in TrainingProgramService");
        log.debug("Request params: {}, {}", page, size);
        validationOfRequestForGetTrainingProgram.validateRequestForGetTrainingProgram(page, size);
        String sqlForResultInAPage = trainingProgramServiceUtil.constructSQLForResultInAPage(page - 1, size);
        String sqlForTotalRows = trainingProgramServiceUtil.constructSQLForTotalRows();
        Long totalRows = jdbcTemplate.queryForObject(sqlForTotalRows, Long.class);
        int totalPage = (int) (totalRows % size == 0 ? (totalRows / size) : (totalRows / size) + 1);
        Pagination pagination = new Pagination(page, size, totalPage);
        ResponseEntity<ResponseObject> responseObject = ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "Successfully!", pagination,
                        trainingProgramServiceUtil.getTrainingProgramByQuery(sqlForResultInAPage)));
        log.debug("Response data: {}", responseObject);
        log.info("End get all training program at TrainingProgramService");
        return responseObject;
    }

    /**
     * <p>
     * Update status if program is not assigned by any classes
     * </p>
     *
     * @return Collection<TrainingProgram>
     * @author Hieu
     */
    @Override
    public Collection<TrainingProgram> findAll() {
        return trainingProgramRepository.findAll();
    }

    public TrainingProgramDetailDTO mapToDto(TrainingProgram entity) {
        if (entity == null) {
            return null;
        }
        TrainingProgramDetailDTO trainingProgramDetailDTO = modelMapper.map(entity, TrainingProgramDetailDTO.class);

        int duration = 0;
        int hours = 0;
        List<SyllabusOfProgramDTO> syllabusOfProgramDTOList = new ArrayList<>();
        List<ProgramSyllabus> programSyllabusList = entity.getProgramSyllabusAssociation();
        if (programSyllabusList != null)
            for (ProgramSyllabus programSyllabus : programSyllabusList) {
                SyllabusDTO key = syllabusService.getSyllabusById(programSyllabus.getSyllabus().getId());
                SyllabusOfProgramDTO detail = modelMapper.map(key, SyllabusOfProgramDTO.class);

                detail.setCreatedByUser(key.getCreatedByUser().getFullname());
                if (key.getUpdatedByUser() != null)
                    detail.setUpdatedByUser(key.getUpdatedByUser().getFullname());
                int NOsession = 0;
                if (key.getSyllabusDays() != null) {
                    for (int i = 0; i < key.getSyllabusDays().size(); i++) {
                        NOsession++;
                        if (key.getSyllabusDays().get(i).getSyllabusUnits() != null) {
                            List<UnitDTO> unitDTOList = detail.getSyllabusDays().get(i).getSyllabusUnits();
                            for (int j = 0; j < unitDTOList.size(); j++) {
                                List<UnitChapterDTO> syllabusUnitChapters = unitDTOList.get(j).getSyllabusUnitChapters();
                                if (syllabusUnitChapters != null) {
                                    for (int k = 0; k < syllabusUnitChapters.size(); k++) {
                                        List<MaterialsDTO> materialsDTOList = syllabusUnitChapters.get(k).getMaterials();
                                        if (materialsDTOList != null) {
                                            for (int l = 0; l < materialsDTOList.size(); l++) {
                                                UserDTO create = key.getSyllabusDays().get(i).getSyllabusUnits().get(j).getSyllabusUnitChapters().get(k).getMaterials().get(l).getCreatedByUser();
                                                materialsDTOList.get(l).setCreatedBy(create == null ? null : create.getFullname());
                                                UserDTO updater = key.getSyllabusDays().get(i).getSyllabusUnits().get(j).getSyllabusUnitChapters().get(k).getMaterials().get(l).getUpdatedByUser();
                                                materialsDTOList.get(l).setUpdatedBy(updater == null ? null : updater.getFullname());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                detail.setSession(NOsession);
                syllabusOfProgramDTOList.add(detail);
                duration += key.getDays();
                hours += key.getHours();
            }
        if (entity.getCreatedBy() != null) {
            Optional<User> user = userService.findUserByID(entity.getCreatedBy());
            if (user.isPresent()) {
                trainingProgramDetailDTO.setCreatedBy(user.get().getFullname());
            }
        }
        if (entity.getUpdatedBy() != null) {
            Optional<User> user = userService.findUserByID(entity.getUpdatedBy());
            if (user.isPresent()) {
                trainingProgramDetailDTO.setUpdatedBy(user.get().getFullname());
            }
        }
        trainingProgramDetailDTO.setDuration(duration);
        trainingProgramDetailDTO.setHours(hours);
        trainingProgramDetailDTO.setSyllabusOfProgramDTOList(syllabusOfProgramDTOList);
        return trainingProgramDetailDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseForFilterTrainingProgram filterTrainingProgram(RequestForFilterTrainingProgram requestData)
            throws InvalidRequestForFilterTrainingProgramException {

        log.info("Start method filterTrainingProgram in TrainingProgramService");
        log.debug("Keywords: {}",
                requestData.getSearchValue() != null ? Arrays.asList(requestData.getSearchValue()) : null);

        // Validate input data from user
        validationOfRequestForFilterTrainingProgram.validateRequestForFilterTrainingProgram(requestData);

        int page = requestData.getPage() - 1;
        int size = requestData.getSize();

        String sqlForResultInAPage = trainingProgramServiceUtil.constructSQLForResultInAPage(requestData);
        String sqlForTotalRows = trainingProgramServiceUtil.constructSQLForTotalRows(requestData);

        // get total found result, return max matching page number with paging data as
        // pagination
        Long totalRows = trainingProgramJdbc.getTotalFoundResult(sqlForTotalRows);
        int totalPage = (int) (totalRows % size == 0 ? (totalRows / size) : (totalRows / size) + 1);
        Pagination pagination = new Pagination(page + 1, size, totalPage);

        List<TrainingProgramForFilter> trainingPrograms = trainingProgramJdbc
                .getTrainingProgramForFiltersByQuery(sqlForResultInAPage);

        ResponseForFilterTrainingProgram responseData = new ResponseForFilterTrainingProgram(trainingPrograms,
                pagination);

        log.debug("Response data: {}", responseData);
        log.info("End method filterTrainingProgram in TrainingProgramService");

        return responseData;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ResponseObject> getKeywordList(String requestData) {

        String keyword;

        // validate keyword entered by user
        if (requestData != null) {
            keyword = requestData.replaceAll("\\s+", " ").stripLeading();
            if (keyword.length() == 0)
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(),
                                "Fail to get keyword list, search value must not empty", null, null));
            if (keyword.length() > ValidationOfRequestForFilterTrainingProgram.MAX_LENGTH_OF_SEARCHVALUE)
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(),
                                "Maximum length of a search value cannot exceed "
                                        + ValidationOfRequestForFilterTrainingProgram.MAX_LENGTH_OF_SEARCHVALUE,
                                null, null));

        } else
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseObject(
                    HttpStatus.NOT_ACCEPTABLE.toString(), "Fail to get search value list, search value is null", null,
                    null));

        String query = trainingProgramServiceUtil.constructSQLForSuggestedKeywords(requestData);
        List<String> keywordList = trainingProgramJdbc.getKeywordList(query, requestData);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(), "Get search value list successfully", null,
                        keywordList));
    }

    @Override
    public Optional<TrainingProgram> findById(UUID id) {
        return trainingProgramRepository.findById(id);
    }

    @Override
    public TrainingProgramDetailDTO getTrainingProgramById(UUID id) {
        Optional<TrainingProgram> trainingProgramOptional = trainingProgramRepository.findById(id);
        if (trainingProgramOptional.isEmpty()) return null;
        TrainingProgram trainingProgram = trainingProgramOptional.get();
        return mapToDto(trainingProgram);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ResponseObject> updateStatus(UpdateTrainingProgramForm trainingProgramForm)
            throws ResourceNotFoundException {
        UUID trainingProgramId = trainingProgramForm.getTrainingProgramId();
        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProgramId)
                .orElseThrow(() -> new ResourceNotFoundException("Training program not found for this ID: " + trainingProgramId));
        String message;
        String status = String.valueOf(ResponseStatus.FAIL);
        ResponseObject responseObject;

        log.info("Start method updateStatus");
        log.debug("Training program id: {}", trainingProgramId.toString());
        TrainingClass trainingClasses = trainingProgram.getTrainingClass();
        // If training program is not assigned to any training classes then update the status
        if (trainingClasses == null) {
            trainingProgram.setStatus(trainingProgramForm.getStatus());
            trainingProgramRepository.save(trainingProgram);
            message = "Update status successfully";
            status = String.valueOf(ResponseStatus.OK);
        } else {
            message = "Some classes are using this training program";
        }
        TrainingProgramDTO dto = modelMapper.map(trainingProgram, TrainingProgramDTO.class);
        responseObject = new ResponseObject(status, message, null, dto);
        log.info("End method updateStatus");
        return ResponseEntity.status(status.equals(ResponseStatus.OK.toString()) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ResponseObject> deleteById(UUID id) throws ResourceNotFoundException {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training program not found for this ID: " + id));
        String message;
        String status = String.valueOf(ResponseStatus.FAIL);
        ResponseObject responseObject;

        log.info("Start method deleteById");
        log.debug("Training program id: {}", id.toString());
        TrainingClass trainingClasses = trainingProgram.getTrainingClass();
        // If training program is not assigned to any training classes then update the status
        if (trainingClasses == null) {
            trainingProgram.setStatus(TrainingProgramStatus.DELETED);
            trainingProgramRepository.save(trainingProgram);
            message = "Deleted successfully";
            status = String.valueOf(ResponseStatus.OK);
        } else {
            message = "Some classes are using this training program";
        }

        TrainingProgramDTO dto = modelMapper.map(trainingProgram, TrainingProgramDTO.class);
        responseObject = new ResponseObject(status, message, null, dto);
        log.info("End method deleteById");
        return ResponseEntity.status(status.equals(ResponseStatus.OK.toString()) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseObject);
    }

}
