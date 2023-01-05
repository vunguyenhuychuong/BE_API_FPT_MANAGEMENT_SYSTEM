package com.java8.tms.syllabus.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.exception.model.ResourceNotFoundException;
import com.java8.tms.syllabus.dto.RequestForListOfSyllabus;
import org.springframework.http.ResponseEntity;

import com.java8.tms.syllabus.dto.SyllabusDTO;
import com.java8.tms.syllabus.dto.UserDTO;
import com.java8.tms.syllabus.dto.*;

import org.springframework.web.multipart.MultipartFile;


/**
 * <p>
 * Interface
 * </p>
 *
 * @author kiet phan
 */

//@Service
public interface SyllabusService {
    ResponseEntity<ResponseObject> getSuggestions(String searchKeyword);
    ResponseEntity<ResponseObject> getAllSyllabuses(RequestForListOfSyllabus request);
    ResponseEntity<ResponseObject> getAllDraftByUserId( String sortType, int page, int size);
    ResponseObject deactiveSyllabus(UUID id) throws ResourceNotFoundException;

    ResponseObject deleteSyllabus(UUID id) throws ResourceNotFoundException;

    SyllabusDTO getSyllabusById(UUID id);
    
    public SyllabusDTO getSyllabus_Details(UUID id);

    SyllabusDTO getSyllabus(UUID id);

    UserDTO getUserById(UUID id);

    public ResponseEntity<ResponseObject> viewSyllabusDetails(UUID id);

    public ResponseEntity<ResponseObject> duplicateSyllabus(UUID id);

    public SyllabusDTO duplicateSyllabusById(UUID id);

    public SyllabusDTO importUserData(SyllabusDTO syllabusDTO);

    public SyllabusDTO getSyllabusUserData(SyllabusDTO syllabusDTO);

    public SyllabusDTO getMaterialUserData(SyllabusDTO syllabusDTO);

    public SyllabusDTO getOutputStandard(SyllabusDTO syllabusDTO);

    public SyllabusDTO getActiveSyllabus(SyllabusDTO syllabusDTO);

    public List<MaterialDTO> removeDeletedMaterial(List<MaterialDTO> list);

    public List<SyllabusDayDTO> removeDeletedDay(List<SyllabusDayDTO> list);

    public SyllabusDTO getSortedSyllabus(SyllabusDTO syllabusDTO);

    public List<SyllabusDayDTO> getSortedDay(List<SyllabusDayDTO> list);

    public List<SyllabusUnitDTO> getSortedUnit(List<SyllabusUnitDTO> list);

    public List<SyllabusUnitChapterDTO> getSortedUnitChapter(List<SyllabusUnitChapterDTO> list);

    public List<MaterialDTO> getSortedMaterial(List<MaterialDTO> list);

    public SyllabusDTO createSyllabus(FormSyllabusDTOWithoutId syllabusDTO);

    public SyllabusDTO saveDraftSyllabus(FormSyllabusDTOWithoutId syllabusDTO);

    void mapReapExcelDatatoDB(MultipartFile reapExcelDataFile, List<String> checkbox, String radio) throws IOException;
    public SyllabusDTO updateSyllabus(FormSyllabusDTO syllabusDTO);
}
