package com.java8.tms.material.service.impl;

import com.java8.tms.common.dto.ErrorResponse;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.Material;
import com.java8.tms.common.entity.SyllabusUnitChapter;
import com.java8.tms.common.meta.MaterialStatus;
import com.java8.tms.common.repository.MaterialRepository;
import com.java8.tms.common.repository.SyllabusUnitChapterRepository;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.material.dto.ResponseCustom;
import com.java8.tms.material.dto.SendParamMaterial;
import com.java8.tms.material.exception.NotFoundException;
import com.java8.tms.material.service.MaterialService;
import com.java8.tms.syllabus.dto.MaterialDTO;
import com.java8.tms.syllabus.dto.SyllabusDTO;
import com.java8.tms.syllabus.service.SyllabusService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

@Component
public class MaterialServiceImpl implements MaterialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialServiceImpl.class);
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private SyllabusUnitChapterRepository syllabusUnitChapterRepository;
    @Autowired
    private SyllabusService syllabusService;
    @Autowired
    private ModelMapper modelMapper;

    private UUID getUserId() {
    	UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return userPrinciple.getId();
    }
    
    /**
     * <p>
     * Download file material
     * </p>
     *
     * @param materialId
     * 
     * @return file
     * @author TrungNT
     */
    @Override
    public ResponseEntity<byte[]> downloadFile(UUID materialId) {
        Material material = getFileById(materialId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + material.getName() + "");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(material.getData());
    }
    
    
    /**
     * <p>
     * Delete training material
     * </p>
     *
     * @param materialId
     * 
     * @return 
     * @author TrungNT
     */
    @Override
    public ResponseEntity<?> deleteTrainingMaterial(UUID materialId) {
        ResponseObject responseObj;
        
        boolean result = deleteFile(materialId, getUserId());
        if (result) {
            responseObj = new ResponseObject("OK", "Delete material success", null, null);
            return ResponseEntity.status(HttpStatus.OK).body(responseObj);
        } else {
            responseObj = new ResponseObject("Delete failed", "Delete material failed", null, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObj);
        }
    }
    
    /**
     * <p>
     * Update training material
     * </p>
     *
     * @param param
     * @param file
     * 
     * @author TrungNT
     */
    @Override
    public ResponseEntity<?> update(SendParamMaterial param, MultipartFile file) {
    	String message = "";
		ResponseObject response = null;
		boolean checkUpdate = false;
		
		UUID materialId = param.getId();
		UUID syllabusId = param.getSyllabusId();
		String url = param.getUrl();
		String name = param.getName();
		UUID userId = getUserId();
		
		if (file==null && param.getUrl().isEmpty() && param.getUrl().isBlank()) {
            throw new NotFoundException("File and url is empty");
        }else if(file==null) {
        	LOGGER.info("File is null -> update url");
        	checkUpdate = updateUrl(materialId, url, name, userId);
        }else {
        	LOGGER.info("File not null -> Update file");
        	checkUpdate = updateFile(materialId, url, name, userId, file);
        }
		
		if(checkUpdate) {
			SyllabusDTO syllabusDTO = syllabusService.getSyllabusById(syllabusId);
			UUID syllabusUnitChapterId = getFileById(materialId).getUnitChapter().getId();
			ResponseCustom responseCustom = new ResponseCustom(syllabusDTO, syllabusUnitChapterId);
			response = new ResponseObject("OK", "Update success", null, responseCustom);
			return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            message = "Could not update material";
            ErrorResponse error = new ErrorResponse(new Date(), "ERROR.", message);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(error);
        }
    }
    
    /**
     * <p>
     * Upload training material
     * </p>
     *
     * @param param
     * @param file
     * 
     * @author TrungNT
     */
    @Override
    public ResponseEntity<?> upload(SendParamMaterial param, MultipartFile file) {
    	String message = "";
		ResponseObject response = null;
		boolean checkSave = false;
		
		UUID unitChapterId = param.getId();
		UUID syllabusId = param.getSyllabusId();
		String url = param.getUrl();
		String name = param.getName();
		UUID userId = getUserId();
				
		if (file==null && param.getUrl().isEmpty() && param.getUrl().isBlank()) {
            throw new NotFoundException("File and url is empty.");
        }else if(file==null) {
    		
    		LOGGER.info("File is null -> save url");
    		checkSave = saveUrl(unitChapterId, url, name, userId);
    		
    		
    	}else {
    		LOGGER.info("File not null -> Save file");
    		checkSave = saveFile(file, unitChapterId, url, name, userId);
    	}
		
		if(checkSave) {
			SyllabusDTO syllabusDTO = syllabusService.getSyllabusById(syllabusId);
            ResponseCustom responseCustom = new ResponseCustom(syllabusDTO, param.getId());
            response = new ResponseObject("OK", "Save success", null, responseCustom);
            return ResponseEntity.status(HttpStatus.OK).body(response);
		}else {
			message = "Could not upload new material link url";
            ErrorResponse error = new ErrorResponse(new Date(), ".ERROR", message);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(error);
		}
    }
    
    /**
     * <p>
     * Save material
     * </p>
     *
     * @param file
     * @param param
     * 
     * @return boolean
     * @author TrungNT
     */
    public Boolean saveFile(MultipartFile file, UUID unitChapterId, String url, String name, UUID userId) {
    	Boolean result 	= false;
    	Date date = new Date();
    	
    	SyllabusUnitChapter syllabusUnitChapter = getUnitChapterById(unitChapterId);
    	
    	String fileName = file.getOriginalFilename();
    	String fileType = "." + getFileType(fileName);
    	String materialName = name + fileType;
    	try {
    		Material material = Material.builder()
        			.name(materialName)							
        			.url(url)						
        			.createdBy(userId)
        			.createdDate(date)
        			.updatedBy(userId)
        			.updatedDate(date)
        			.data(file.getBytes())
        			.materialStatus(MaterialStatus.ACTIVE)
        			.unitChapter(syllabusUnitChapter)
        			.build();
    		
    		materialRepository.save(material);
    		result = true;
    		LOGGER.info("Save material file success");
    	}catch (Exception e) {
    		LOGGER.error("Save material file failed.");
		}
    	return result;
    }
    
    public Boolean saveUrl(UUID unitChapterId, String url, String name, UUID userId) {
    	Boolean result = false;
    	Date date = new Date();
    	SyllabusUnitChapter syllabusUnitChapter = getUnitChapterById(unitChapterId);
    	Material material = Material.builder()
    			.name(name)
    			.url(url)
    			.createdBy(userId)
    			.createdDate(date)
    			.updatedBy(userId)
    			.updatedDate(date)
    			.materialStatus(MaterialStatus.ACTIVE)
    			.unitChapter(syllabusUnitChapter)
    			.build();
    	materialRepository.save(material);
    	result= true;
    	LOGGER.info("Save material link url success.");
    	return result;
    }
    
    


    /**
     * <p>
     * Get syllabus unit chapter by id
     * </p>
     *
     * @param syllabusUnitChapterId
     * @author TrungNT
     */
    public SyllabusUnitChapter getUnitChapterById(UUID syllabusUnitChapterId) {
        LOGGER.info("Start method getUnitChapterById in MaterialServiceImpl");
        try {
            return syllabusUnitChapterRepository.findById(syllabusUnitChapterId).get();
        } catch (Exception e) {
            throw new NotFoundException("Not found Unit chapter.");
        }
    }

    /**
     * <p>
     * Get file type 
     * EX: fileName = avatar.png -> fileType is png
     * </p>
     *
     * @param filename
     * 
     * @return fileType
     * @author TrungNT
     */
    public String getFileType(String fileName) {
    	String fileType = "";
    	int i = fileName.lastIndexOf(".");
    	if(i>0) {
    		fileType = fileName.substring(i+1);
    	}
    	return fileType;
    }

    /**
     * <p>
     * Update material can empty link or file or link and file Update
     * </p>
     *
     * @param materialId
     * @param file
     * @param userId
     * @param url
     * @param name
     * @author TrungNT
     */
    public Boolean updateUrl(UUID materialId, String url, String name, UUID userId) {
    	Boolean result = false;
    	LOGGER.info("Entering method update link material in MaterialServiceImpl");
    	Date date = new Date();
    	Material material = materialRepository.findById(materialId).get();
    	
    	if(material != null) {
    		material.setName(name);
    		material.setUrl(url);
    		material.setUpdatedBy(userId);
    		material.setUpdatedDate(date);
    		
    		materialRepository.save(material);
        	LOGGER.info("Update link url material success.");
        	result = true;
    	}
    	return result;
    }
    
    public Boolean updateFile(UUID materialId, String url, String name, UUID userId, MultipartFile file) {
    	Boolean result = false;
    	LOGGER.info("Entering method update material in MaterialServiceImpl");
    	Date date = new Date();
    	Material material = materialRepository.findById(materialId).get();
    	if (material != null) {
    		String fileName = file.getOriginalFilename();
    		String fileType = "." + getFileType(fileName);
    		String materialName = name + fileType;
			try {
				material.setName(materialName);
				material.setData(file.getBytes());
				material.setUrl(url);
				material.setUpdatedBy(userId);
				material.setUpdatedDate(date);
			} catch (Exception e) {
				LOGGER.error("Update file failed.");
                e.printStackTrace();
			}
			materialRepository.save(material);
	    	result = true;
	    	LOGGER.info("Update material success.");
		}
    	return result;
    }
    
    

    /**
     * <p>
     * Delete material by id
     * </p>
     *
     * @param id
     * @param userId
     * @return true or false
     * @author TrungNT
     */
    public Boolean deleteFile(UUID id, UUID userId) {
        LOGGER.trace("Entering method delete material file by id in MaterialServiceImpl");
        boolean result = false;
        boolean check = materialRepository.findById(id).isPresent();
        if (check) {
            Material material = materialRepository.findById(id).get();
            if (material.getMaterialStatus() == MaterialStatus.ACTIVE) {
                material.setMaterialStatus(MaterialStatus.DELETED); // change status active -> deleted
                material.setUpdatedBy(userId); // user delete
                material.setUpdatedDate(new java.util.Date()); // date delete
                materialRepository.save(material); // save
                LOGGER.info("Delete material successfull.");

                // result
                result = true;
            } else {
                LOGGER.error("Training material đã ở trạng thái delete");
            }
        } else {

            LOGGER.error("Not found file ");
            throw new NotFoundException("Not found file training material");
        }
        return result;
    }

    /**
     * <p>
     * Get materialDTO by Id
     * </p>
     *
     * @param id
     * @return materialDTO
     * @author TrungNT
     */
    public MaterialDTO getById(UUID id) {
        LOGGER.trace("Entering method get MaterialDTO by id.");
        LOGGER.debug("ID: ");
        MaterialDTO materialDTO = null;

        // check material by id present or not
        boolean check = materialRepository.findById(id).isPresent();
        if (check) {
            Material material = materialRepository.findById(id).get();
            materialDTO = modelMapper.map(material, MaterialDTO.class);
            LOGGER.info("Get material by id successfull.");
        } else {
            LOGGER.error("Get material by id failed.");
            throw new NotFoundException("Not found training material");
        }
        return materialDTO;
    }

    /**
     * <p>
     * Get Material by Id
     * </p>
     *
     * @param id {@code UUID}
     * @return material
     * @author TrungNT
     */
    public Material getFileById(UUID id) {
        try {
            return materialRepository.findById(id).get();
        } catch (Exception e) {
            throw new NotFoundException("Not found file to download");
        }
    }

}
