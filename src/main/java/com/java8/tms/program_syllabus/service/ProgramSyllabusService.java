package com.java8.tms.program_syllabus.service;

import com.java8.tms.common.entity.User;
import com.java8.tms.program_syllabus.dto.*;
import com.java8.tms.program_syllabus.exception.InvalidCreateProgramException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ProgramSyllabusService {

    /*
     *
     * <p>
     * Save Program with Status: DRAFT
     * This method check RequestProgram, number of draft in DB and finally save
     * program.
     * </p>
     *
     * @param requestProgram
     * 
     * @param userID
     * 
     * @return null - save Successful
     * 
     * @return ResponseForSaveProgram - save Failed - Syllabus Data Error
     * 
     * @throws InvalidInputDraftProgramException - save Failed - Program Data Error
     *
     * @author Luu Thanh Huy
     */
    SaveProgramResponse saveDraftProgram(ProgramRequest requestProgram, UUID userID);

    /*
     *
     * <p>
     * Delete Draft Program, delete all information from Database
     * </p>
     *
     * @param programID
     * 
     * @param userID
     * 
     * @return message (Successful || Failed)
     *
     * @author Nguyen Minh Tam
     */
    boolean deleteDraftProgram(UUID userID, UUID programID);

    /*
     *
     * <p>
     * Save Program with Status: INACTIVE
     *
     * </p>
     *
     * @param requestProgram
     * 
     * @return null - save Successful
     * 
     * @return ResponseForSaveProgram - save Failed - Syllabus Data Error
     * 
     * @throws InvalidInputDraftProgramException - save Failed - Program Data Error
     *
     * @author Trinh Phan Duc Cuong
     */
    SaveProgramResponse saveCompleteProgram(ProgramRequest requestProgram, UUID userID);

    /*
     *
     * <p>
     * Find Syllabus with keyword ( syllabus name + syllabus version)
     * </p>
     *
     * @param keyword
     * 
     * @return ResponseForGetListSyllabus || null
     *
     * @author Nguyen Gia Bao
     */
    List<SyllabusResponse> searchSyllabusByKeyword(String keyword);

    /*
     *
     * <p>
     * Get detailed information of Program with Status: Draft
     * </p>
     *
     * @param programID
     * 
     * @param userID
     * 
     * @return ResponseForGetDraft
     *
     * @author Le Tran Quang Linh
     */
    DetailDraftProgramResponse getDraftProgram(UUID programID, UUID userID);

    /*
     *
     * <p>
     * Get List Program with Status: Draft of a user
     * </p>
     *
     * @param userID
     * 
     * @return ResponseForGetListDraftProgram
     *
     * @author Nguyen Quang Nhat
     */
    List<DraftProgramResponse> getListDraftProgramByUserID(UUID userID);

    /*
     *
     * <p>
     * Locate template file from the pre-defined path
     * </p>
     *
     * @param
     * 
     * @return Byte array of file
     *
     * @author Chu Quang Du
     */
    ByteArrayResource getTemplateResource();

    /*
     *
     * <p>
     * Return response object for save training program include syllabus that find
     * by id, name, version,
     * if system can not find available syllabus, it can be add to syllabus error
     * list, else if system can find that syllabus at the database it will be add
     * into dataOk list and ready for save into table program_syllabus
     * </p>
     *
     * @param file
     * 
     * @return
     * 
     * @throws IOException
     * 
     * @throws InvalidCreateProgramException
     *
     * @author Nguyen Quoc Bao
     *
     */
    SaveProgramResponse readTrainingProgram(MultipartFile file) throws IOException, InvalidCreateProgramException;

    SaveProgramResponse saveTrainingProgram(SaveProgramResponse res) throws InvalidCreateProgramException;

    /*
     *
     * <p>
     * Get user from Security Context
     * </p>
     *
     * @param
     * 
     * @return User
     *
     * @author Luu Thanh Huy
     *
     */
    User getUserFromContext();
    /*
    *
    * <p>
    * Get Program
    * </p>
    *
    * @param  programID
    * 
    * @return DetailDraftProgramResponse
    *
    * @author Luu Thanh Huy
    *
    */
	DetailDraftProgramResponse getProgram(UUID programID);

	/*
    *
    * <p>
    * Edit Program with Status: INACTIVE, ACTIVE
    * </p>
    *
    * @param requestProgram
    * 
    * @param userID
    * 
    * @return null - edit Successful
    * 
    * @return ResponseForSaveProgram - edit Failed - Syllabus Data Error
    * 
    * @throws InvalidInputDraftProgramException - edit Failed - Program Data Error
    *
    * @author Luu Thanh Huy
    */
	SaveProgramResponse editProgram(EditProgramRequest requestProgram, UUID userID);
}
