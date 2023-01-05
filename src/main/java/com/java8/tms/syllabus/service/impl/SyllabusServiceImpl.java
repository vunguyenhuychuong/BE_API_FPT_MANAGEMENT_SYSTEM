package com.java8.tms.syllabus.service.impl;

import com.java8.tms.common.dto.Pagination;
import com.java8.tms.common.dto.ResponseObject;
import com.java8.tms.common.entity.*;
import com.java8.tms.common.meta.MaterialStatus;
import com.java8.tms.common.meta.SyllabusDayStatus;
import com.java8.tms.common.meta.SyllabusStatus;
import com.java8.tms.common.repository.*;
import com.java8.tms.common.security.userprincipal.UserPrinciple;
import com.java8.tms.syllabus.controller.SyllabusController;
import com.java8.tms.syllabus.dto.*;
import com.java8.tms.syllabus.jdbc.SyllabusJdbc;
import com.java8.tms.syllabus.service.SyllabusService;
import com.java8.tms.user.custom_exception.UserNotFoundException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * get Syllabus service
 * </p>
 *
 * @author kiet phan
 */
//@Component
@Service
public class SyllabusServiceImpl implements SyllabusService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyllabusController.class);
    @Autowired
    private SyllabusRepository syllabusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SyllabusJdbc syllabusJdbc;
    @Autowired
    private SyllabusServiceUtil syllabusServiceUtil;
    @Autowired
    private SyllabusLevelRepository syllabusLevelRepository;
    @Autowired
    private SyllabusDayRepository syllabusDayRepository;
    @Autowired
    private DeliveryPrincipleRepository deliveryPrincipleRepository;
    @Autowired
    private SyllabusUnitRepository syllabusUnitRepository;
    @Autowired
    private SyllabusUnitChapterRepository syllabusUnitChapterRepository;
    @Autowired
    private AssessmentSchemeRepository assessmentSchemeRepository;
    @Autowired
    private DeliveryTypeRepository deliveryTypeRepository;
    @Autowired
    private OutputStandardRepository outputStandardRepository;
    @Autowired
    private MaterialRepository materialRepository;

    /**
     * <p>
     * view Syllabus details service called by Controller
     * </p>
     *
     * @param id
     * @return responseEntity
     * @author KietPTT
     */
    @Override
    public ResponseEntity<ResponseObject> viewSyllabusDetails(UUID id) {
        LOGGER.info("Start method getSyllabusDetails in SyllabusController");
        SyllabusDTO result = getSyllabusById(id);
        if (result == null) {
            LOGGER.info("Cannot get Syllabus with id: " + id);
        }
        ResponseObject response = new ResponseObject("OK", "Get Syllabus successfully: " + id, null, result);

        LOGGER.info("End method getSyllabusDetails in SyllabusController");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * <p>
     * duplicate Syllabus service called by Controller
     * </p>
     *
     * @param id
     * @return responseEntity
     * @author KietPTT
     */
    @Override
    public ResponseEntity<ResponseObject> duplicateSyllabus(UUID id) {
        LOGGER.info("Start of method duplicateSyllabusById in SyllabusController");
        SyllabusDTO result = duplicateSyllabusById(id);
        if (result == null) {
            LOGGER.info("Can't duplicate syllabus");
        }
        ResponseObject response = new ResponseObject("OK", "Get Syllabus successfully: " + id, null, result);
        LOGGER.info("End of method duplicateSyllabusById in SyllabusController");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * <p>
     * get full Syllabus details by id
     * </p>
     *
     * @param id
     * @return syllabus
     * @author KietPTT
     */
    @Override
    public SyllabusDTO getSyllabus(UUID id) {
        SyllabusDTO syllabusDTO = null;
        if (id != null) {
            boolean check = syllabusRepository.findById(id).isPresent();
            if (check) {
                Syllabus result = syllabusRepository.findById(id).get();
                syllabusDTO = modelMapper.map(result, SyllabusDTO.class);

            }
        }
        return syllabusDTO;
    }
    
    /**
	 * <p>
	 * get full Syllabus details by id
	 * </p>
	 *
	 * @param id
	 * @return syllabus
	 * @author KietPTT
	 */
	@Override
	public SyllabusDTO getSyllabus_Details(UUID id) {
		SyllabusDTO syllabusDTO = null;
		if (id != null) {
			boolean check = syllabusRepository.findById(id).isPresent();
			if (check) {
				Syllabus result = syllabusRepository.findById(id).get();
				//lower data size
				for (SyllabusDay day : result.getSyllabusDays()) {
					for (SyllabusUnit unit : day.getSyllabusUnits()) {
						for (SyllabusUnitChapter unitChapter : unit.getSyllabusUnitChapters()) {
							for (Material material : unitChapter.getMaterials()) {
								if (material != null) {
									if (material.getData() != null) {
										byte[] a = "0".getBytes();
										material.setData(a);
									}
								}
							}
						}
					}
				}
				syllabusDTO = modelMapper.map(result, SyllabusDTO.class);
			}
		}
		return syllabusDTO;
	}

    /**
     * <p>
     * get Syllabus with imported and treated details
     * </p>
     *
     * @param id
     * @return syllabusDTO
     * @author KietPTT
     */
    public SyllabusDTO getSyllabusById(UUID id) {
        SyllabusDTO syllabusDTO = null;
        syllabusDTO = getSyllabus_Details(id);
        if (syllabusDTO != null) {

            // remove deleted status Day & Material
            syllabusDTO = getActiveSyllabus(syllabusDTO);

            // sorted syllabus
            syllabusDTO = getSortedSyllabus(syllabusDTO);

            // import userDTO into syllabusDTO
            syllabusDTO = importUserData(syllabusDTO);

            // import outputStandard list into syllabusDTO
            syllabusDTO = getOutputStandard(syllabusDTO);
        }
        return syllabusDTO;
    }

    public UserDTO getUserById(UUID id) {
        UserDTO userDTO = null;
        if (id != null) {
            boolean check = userRepository.findById(id).isPresent();
            if (check) {
                User user = userRepository.findById(id).get();
                userDTO = modelMapper.map(user, UserDTO.class);
            }
        }
        return userDTO;
    }

    public ResponseEntity<ResponseObject> getAllSyllabuses(RequestForListOfSyllabus request) {
        LOGGER.info("Start method getAllSyllabuses in SyllabusServiceImpl");
        List<Syllabus> syllabusList = new ArrayList<>();
        List<Syllabus> temp = new ArrayList<>();
        List<SyllabusDTO> results = new ArrayList<>();
        List<ResponseForListOfSyllabus> responseData = new ArrayList<>();
        int totalPage = 0;
        int totalRows = 0;
        String message = "";
        if (("".equals(request.getStartDate()) && "".equals(request.getEndDate())) && request.getTags().length == 0) {
            if (request.getSortBy() == null && request.getSortType() == null) {
                LOGGER.info("Start View All Syllabuses");
                Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
                Page<Syllabus> syllabusPage = syllabusRepository.findAllSyllabuses(pageable);
                totalPage = syllabusPage.getTotalPages();
                results = syllabusPage.getContent().stream().map(item -> modelMapper
                        .map(item, SyllabusDTO.class)).collect(Collectors.toList());
            } else {
                LOGGER.info("View All Syllabuses With Sort Options");
                if("CREATEDBYUSER".equalsIgnoreCase(request.getSortBy())){
                    temp = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                            .getSQLForSortingAllSyllabusesByCreatedBy(request.getPage() - 1,
                                    request.getSize(), request.getSortType()));
                }else if("CREATEDDATE".equalsIgnoreCase(request.getSortBy())) {
                    temp = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                            .getSQLForSortingAllSyllabuses(request.getPage() - 1, request.getSize(),
                                    "CREATED_DATE", request.getSortType()));
                }else {
                temp = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                        .getSQLForSortingAllSyllabuses(request.getPage() - 1, request.getSize(),
                                request.getSortBy(), request.getSortType()));
            }
                totalRows = syllabusRepository.getTotalRows();
            }
        } else {
            if (request.getTags().length == 0) {
                temp = searchByCreatedDate(request);
                totalRows = syllabusJdbc.getTotalRows(syllabusServiceUtil.getTotalRowsForSearchingByCreatedDate(
                        request.getStartDate(), request.getEndDate()));
            } else if ("".equals(request.getStartDate()) && "". equals(request.getEndDate())) {
                temp = searchByKeyWords(request, null);
                List<Syllabus> tempList = new ArrayList<>();
                for (String kw : request.getTags()) {
                    List<Syllabus> list = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                            .getTotalRowsForSearchingByKeywords(kw));
                    if (list != null) {
                        tempList.addAll(list);
                    }
                }
                totalRows = addToSyllabusList(tempList).size();
            } else {
                List<Syllabus> foundList = searchByCreatedDate(request);
                List<Syllabus> allCreatedDateList = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                        .getSQLForSearchingByAllCreatedDate(request.getStartDate(), request.getEndDate()));
                temp = searchByKeyWords(request, foundList);
                totalRows = searchByKeywordsAndCreatedDate(request, allCreatedDateList).size();
            }
        }
        if (temp != null) {
            if (temp.size() > 0) {
                for (Syllabus s : temp) {
                    syllabusList.add(syllabusRepository.findSyllabusById(s.getId()));
                }
                results = syllabusList.stream().map(syllabus -> modelMapper.map(syllabus, SyllabusDTO.class))
                        .collect(Collectors.toList());
            }
        }
        if (results.size() > 0) {
            if(totalPage == 0) {
                totalPage = (int)(totalRows % request.getSize() == 0 ? (totalRows / request.getSize()) : (totalRows / request.getSize()) + 1);
            }
            message += "Total " + results.size() + " element(s) in page " + request.getPage();
            for (SyllabusDTO sd : results) {
                sd.setCreatedByUser(getUserById(sd.getCreatedBy()));
                if (sd.getSyllabusDays() != null) {
                    sd = getOutputStandard(sd);
                }
            }
            responseData = results.stream().map(syllabus -> modelMapper.map(syllabus, ResponseForListOfSyllabus.class))
                    .collect(Collectors.toList());
        } else {
            LOGGER.info("Null result");
            message += "Empty list";
        }
        LOGGER.info("End method getAllSyllabuses in SyllabusServiceImpl");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(HttpStatus.OK.toString(), message,
                new Pagination(request.getPage(), request.getSize(), totalPage), responseData));
    }

    private List<Syllabus> searchByCreatedDate(RequestForListOfSyllabus request) {
        List<Syllabus> syllabusList = new ArrayList<>();
        if (request.getSortBy() == null && request.getSortType() == null) {
            LOGGER.info("Search Syllabuses By Created Date");
            syllabusList = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                    .getSQLForSearchingByCreatedDate(request.getStartDate(), request.getEndDate(),
                            request.getPage() - 1, request.getSize()));
        } else {
            LOGGER.info("Search Syllabuses By Created Date With Sort Options");
            if ("CREATEDBYUSER".equalsIgnoreCase(request.getSortBy())) {
                syllabusList = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                        .getSQLForSearchingByCreatedDateAndSortByCreatedBy(request.getStartDate(),
                                request.getEndDate(), request.getPage() -1, request.getSize(),
                                request.getSortType()));
            } else if("CREATEDDATE".equalsIgnoreCase(request.getSortBy())) {
                syllabusList = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                        .getSQLForSearchingByCreatedDateAndSort(request.getStartDate(), request.getEndDate(),
                                request.getPage() - 1, request.getSize(), "CREATED_DATE", request.getSortType()));
            } else {
                syllabusList = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                        .getSQLForSearchingByCreatedDateAndSort(request.getStartDate(), request.getEndDate(),
                                request.getPage() - 1, request.getSize(), request.getSortBy(), request.getSortType()));
            }
        }
        return syllabusList;
    }

    private List<Syllabus> searchByKeyWords(RequestForListOfSyllabus request, List<Syllabus> foundList) {
        LOGGER.info("Search Syllabuses By Keywords");
        List<Syllabus> syllabusList = new ArrayList<>();
        List<Syllabus> temp = new ArrayList<>();
        if (foundList == null) {
            for (String kw : request.getTags()) {
                List<Syllabus> tempList = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                        .getSQLForSearchingByKeywords(request.getPage() - 1, request.getSize(), kw));
                if (tempList != null) {
                    temp.addAll(tempList);
                }
            }
            syllabusList = addToSyllabusList(temp);
        } else {
            if (foundList.size() > 0) {
                syllabusList = searchByKeywordsAndCreatedDate(request, foundList);
            }
        }
        if (request.getSortBy() != null && request.getSortType() != null) {
            LOGGER.info("Search Syllabuses By Keywords With Sort Options");
            if ("NAME".equalsIgnoreCase(request.getSortBy())) {
                Collections.sort(syllabusList, new Comparator<Syllabus>() {
                    @Override
                    public int compare(Syllabus syllabus1, Syllabus syllabus2) {
                        return syllabus1.getName().compareTo(syllabus2.getName());
                    }
                });
            } else if ("DURATIONS".equalsIgnoreCase(request.getSortBy())) {
                Collections.sort(syllabusList, new Comparator<Syllabus>() {
                    @Override
                    public int compare(Syllabus syllabus1, Syllabus syllabus2) {
                        return syllabus1.getDays() - syllabus2.getDays();
                    }
                });
            } else if ("CODE".equalsIgnoreCase(request.getSortBy())) {
                Collections.sort(syllabusList, new Comparator<Syllabus>() {
                    @Override
                    public int compare(Syllabus syllabus1, Syllabus syllabus2) {
                        return syllabus1.getCode().compareTo(syllabus2.getCode());
                    }
                });

            } else if ("CREATEDDATE".equalsIgnoreCase(request.getSortBy())) {
                Collections.sort(syllabusList, new Comparator<Syllabus>() {
                    @Override
                    public int compare(Syllabus syllabus1, Syllabus syllabus2) {
                        return syllabus1.getCreatedDate().compareTo(syllabus2.getCreatedDate());
                    }
                });

            }
            else if("CREATEDBYUSER".equalsIgnoreCase(request.getSortBy())){
                Collections.sort(syllabusList, new Comparator<Syllabus>() {
                    @Override
                    public int compare(Syllabus syllabus1, Syllabus syllabus2) {
                        Syllabus foundSyllabus1 = syllabusRepository.findSyllabusById(syllabus1.getId());
                        Syllabus foundSyllabus2 = syllabusRepository.findSyllabusById(syllabus2.getId());
                        Optional<User> optionalUser1= userRepository.findById(foundSyllabus1.getCreatedBy());
                        Optional<User> optionalUser2= userRepository.findById(foundSyllabus2.getCreatedBy());
                        return optionalUser1.get().getFullname().compareTo(optionalUser2.get().getFullname());
                    }
                });
            }
            if ("DESC".equalsIgnoreCase(request.getSortType())) {
                Collections.reverse(syllabusList);
            }
        }
        return syllabusList;
    }

    private boolean checkDuplicateId(String id, List<Syllabus> syllabuses) {
        boolean check = false;
        for (Syllabus s : syllabuses) {
            if (s.getId().toString().equals(id)) {
                check = true;
                System.out.println(s.getId());
            }
        }
        return check;
    }

    private List<Syllabus> addToSyllabusList(List<Syllabus> temp){
        List<Syllabus> syllabusList = new ArrayList<>();
        for (Syllabus s : temp) {
            if (syllabusList.size() == 0) {
                syllabusList.add(s);
            } else {
                System.out.println(s.getId());
                boolean check = false;
                check = checkDuplicateId(s.getId().toString(), syllabusList);
                System.out.println(check);
                if (!check) {
                    syllabusList.add(s);
                }
            }
        }
        return syllabusList;
    }

    private List<Syllabus> searchByKeywordsAndCreatedDate(RequestForListOfSyllabus request, List<Syllabus> foundList){
        List<Syllabus> syllabusList = new ArrayList<>();
        for (String kw : request.getTags()) {
            kw = kw.toLowerCase();
            for (Syllabus s : foundList) {
                Syllabus syllabus = syllabusRepository.findSyllabusById(s.getId());
                if (s.getName().toLowerCase().contains(kw) || s.getCode().toLowerCase().contains(kw)
                        || getUserById(syllabus.getCreatedBy()).getFullname().toLowerCase().contains(kw)) {
                    if (syllabusList.size() == 0) {
                        syllabusList.add(s);
                    } else {
                        boolean check = false;
                        check = checkDuplicateId(s.getId().toString(), syllabusList);
                        if (!check) {
                            syllabusList.add(s);
                        }
                    }
                }
            }
        }
        return  syllabusList;
    }

    @Override
    public ResponseEntity<ResponseObject> getSuggestions(String searchKeyword) {
        LOGGER.info("Start method getSuggestions in SyllabusServiceImpl");
        List<String> suggestions = new ArrayList<>();
        String message = "";
        List<Syllabus> foundList = syllabusJdbc.getSyllabuses(syllabusServiceUtil
                .getSQLForSearchingByKeywordsForSuggestions(0,10,searchKeyword));
        if (foundList != null && foundList.size() > 0) {
            List<Syllabus> temp = new ArrayList<>();
            for (Syllabus s : foundList) {
                temp.add(syllabusRepository.findSyllabusById(s.getId()));
            }
            if (temp.size() > 0) {
                for (Syllabus s : temp) {
                    if (s.getName().toLowerCase().contains(searchKeyword)) {
                        if (!checkDuplicateSuggestion(suggestions, s.getName())) suggestions.add(s.getName());
                    }
                    if (s.getCode().toLowerCase().contains(searchKeyword)) {
                        if (!checkDuplicateSuggestion(suggestions, s.getCode())) suggestions.add(s.getCode());
                    }
                    if (getUserById(s.getCreatedBy()).getFullname().toLowerCase().contains(searchKeyword)) {
                        if (!checkDuplicateSuggestion(suggestions, getUserById(s.getCreatedBy()).getFullname()))
                            suggestions.add(getUserById(s.getCreatedBy()).getFullname());
                    }
                    if (suggestions.size() == 10) {
                        break;
                    }
                }
                message += "Total " + suggestions.size() + " element(s)";
            }
        } else {
            LOGGER.info("Null results");
            message += "Empty list";
        }
        LOGGER.info("Start method getSuggestions in SyllabusServiceImpl");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(HttpStatus.OK.toString(), message,
                null, suggestions));
    }

    private boolean checkDuplicateSuggestion(List<String> suggestions, String name) {
        boolean check = false;
        for (String string : suggestions) {
            if (string.equalsIgnoreCase(name)) {
                check = true;
            }
        }
        return check;
    }

    @Override
    public ResponseEntity<ResponseObject> getAllDraftByUserId(String sortType, int page, int size) {
        LOGGER.info("Start method getAllDraft in SyllabusServiceImpl");
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User findUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));
        List<Syllabus> syllabusList = new ArrayList<>();
        List<Syllabus> temp = new ArrayList<>();
        List<SyllabusDTO> results = new ArrayList<>();
        List<ResponseForListOfSyllabus> responseData = new ArrayList<>();
        int totalPage = 0;
        int totalRows = 0;
        String message = "";
        temp = syllabusJdbc.getDrafts(syllabusServiceUtil
                .getSQLForSortingAllDraft(findUser.getId(), page - 1, size,
                        sortType));
        if (temp != null)
            if (temp.size() > 0) {
                totalRows = temp.size();
                for (Syllabus s : temp) {
                    syllabusList.add(syllabusRepository.findSyllabusById(s.getId()));
                }
                totalPage = totalRows % size == 0 ? (totalRows / size) : (totalRows / size) + 1;
                results = syllabusList.stream().map(syllabus -> modelMapper.map(syllabus, SyllabusDTO.class))
                        .collect(Collectors.toList());
            }
        if (results.size() > 0) {
            message += "Total " + results.size() + " element(s) in page " + page;
            for (SyllabusDTO sd : results) {
                sd.setCreatedByUser(getUserById(sd.getCreatedBy()));
            }
            for (SyllabusDTO sd : results) {
                sd.setCreatedByUser(getUserById(sd.getCreatedBy()));
                if (sd.getSyllabusDays() != null) {
                    sd = getOutputStandard(sd);
                }
            }
            responseData = results.stream().map(syllabus -> modelMapper.map(syllabus, ResponseForListOfSyllabus.class))
                    .collect(Collectors.toList());
        } else {
            LOGGER.info("Null result");
            message += "Empty list";
        }
        LOGGER.info("End method getAllDraft in SyllabusServiceImpl");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(HttpStatus.OK.toString(), message,
                new Pagination(page, size, totalPage), responseData));
    }

    public Optional<Syllabus> findById(UUID id) {
        return syllabusRepository.findById(id);
    }

    @Override
    public ResponseObject deactiveSyllabus(UUID id) {
        LOGGER.info("Start method De-active Syllabus");
        LOGGER.info("Syllabus id: {}", id);
        Syllabus syllabus = syllabusRepository.findSyllabusById(id);
        ResponseObject responseObject;
        SyllabusDTO dto;
        boolean check;
        if (syllabus != null) {

            check = syllabus.getStatus() == SyllabusStatus.DEACTIVE || syllabus.getStatus() == SyllabusStatus.ACTIVE;
            check = syllabus.getStatus() == SyllabusStatus.DEACTIVE || syllabus.getStatus() == SyllabusStatus.ACTIVE;
        } else {
            check = false;
        }
        if (check) {
            if (syllabus.getStatus() == SyllabusStatus.ACTIVE) {
                syllabus.setStatus(SyllabusStatus.DEACTIVE);
            } else {
                syllabus.setStatus(SyllabusStatus.ACTIVE);
            }
            syllabus = syllabusRepository.save(syllabus);

            dto = modelMapper.map(syllabus, SyllabusDTO.class);
            responseObject = new ResponseObject(HttpStatus.OK.name(), "Update status successfully", null, dto);
        } else {
            responseObject = new ResponseObject("Not found", "Syllabus " + id + " not found", null, null);
        }
        LOGGER.info("End method de-active Syllabus");
        return responseObject;
    }

    public ResponseObject deleteSyllabus(UUID id) {
        ResponseObject responseObject;
        Syllabus syllabus = syllabusRepository.findSyllabusById(id);
        if (syllabus == null) {
            responseObject = new ResponseObject("Not found", "Syllabus " + id + " not found", null, null);
        } else {
            if (syllabus.getStatus() == SyllabusStatus.DELETED) {
                responseObject = new ResponseObject("Delete fail", "Syllabus was deleted: " + id, null, null);
            } else {
                LOGGER.info("Start method delete Syllabus");
                LOGGER.debug("Syllabus id: {}", id);
                syllabus.setStatus(SyllabusStatus.DELETED);
                syllabusRepository.save(syllabus);
                responseObject = new ResponseObject("OK", "Syllabus " + id + " delete successfully", null, null);

                LOGGER.info("End method delete Syllabus");
            }
        }
        return responseObject;
    }

    /**
     * <p>
     * Duplicate syllabus
     * </p>
     *
     * @param id
     * @return syllabusDTO
     * @author KietPTT
     */
    @Override
    public SyllabusDTO duplicateSyllabusById(UUID id) {
        SyllabusDTO syllabusDTO = null;
        syllabusDTO = getSyllabus(id);
        if (syllabusDTO != null) {

            // remove deleted status Day & Material
            syllabusDTO = getActiveSyllabus(syllabusDTO);

            // sorted syllabus
            syllabusDTO = getSortedSyllabus(syllabusDTO);

            // import userDTO into syllabusDTO
            syllabusDTO = importUserData(syllabusDTO);

            // import outputStandard list into syllabusDTO
            syllabusDTO = getOutputStandard(syllabusDTO);

            // set name + id of copy
            syllabusDTO.setName("Copy Of " + syllabusDTO.getName());
            syllabusDTO.setId(null);
        }
        return syllabusDTO;
    }

    /**
     * <p>
     * import user data into syllabus
     * </p>
     *
     * @param syllabusDTO {@code syllabusDTO}
     * @return syllabusDTO
     * @author KietPTT
     */
    @Override
    public SyllabusDTO importUserData(SyllabusDTO syllabusDTO) {
        if (syllabusDTO != null) {
            syllabusDTO = getSyllabusUserData(syllabusDTO);
            syllabusDTO = getMaterialUserData(syllabusDTO);
        }
        return syllabusDTO;
    }

    /**
     * <p>
     * import user data to createdby & updatedby in Syllabus object
     * </p>
     *
     * @param syllabusDTO
     * @return syllabusDTO
     * @author KietPTT
     */
    @Override
    public SyllabusDTO getSyllabusUserData(SyllabusDTO syllabusDTO) {
        if (syllabusDTO != null) {
            syllabusDTO.setCreatedByUser(getUserById(syllabusDTO.getCreatedBy()));
            syllabusDTO.setUpdatedByUser(getUserById(syllabusDTO.getUpdatedBy()));
        }
        return syllabusDTO;
    }

    /**
     * <p>
     * import user data to createdby & updatedby in Material object
     * </p>
     *
     * @param syllabusDTO
     * @return syllabusDTO
     * @author KietPTT
     */
    @Override
    public SyllabusDTO getMaterialUserData(SyllabusDTO syllabusDTO) {
        if (syllabusDTO != null)
            for (SyllabusDayDTO day : syllabusDTO.getSyllabusDays()) {
                for (SyllabusUnitDTO unit : day.getSyllabusUnits()) {
                    for (SyllabusUnitChapterDTO unitChapter : unit.getSyllabusUnitChapters()) {
                        for (MaterialDTO material : unitChapter.getMaterials()) {
                            if (material != null) {
                                material.setCreatedByUser(getUserById(material.getCreatedBy()));
                                material.setUpdatedByUser(getUserById(material.getUpdatedBy()));
                            }
                        }
                    }
                }
            }
        return syllabusDTO;
    }

    /**
     * <p>
     * sort syllabus
     * </p>
     *
     * @param syllabusDTO
     * @return syllabusDTO
     * @author KietPTT
     */
    @Override
    public SyllabusDTO getSortedSyllabus(SyllabusDTO syllabusDTO) {
        if (syllabusDTO != null) {
            // set sorted day list by DayNo lowest to highest
            syllabusDTO.setSyllabusDays(getSortedDay(syllabusDTO.getSyllabusDays()));
            for (SyllabusDayDTO day : syllabusDTO.getSyllabusDays()) {
                // set sorted unit list by UnitNo lowest to highest
                day.setSyllabusUnits(getSortedUnit(day.getSyllabusUnits()));
                for (SyllabusUnitDTO unit : day.getSyllabusUnits()) {
                    // set sorted unit chapter list by Duration lowest to highest
                    unit.setSyllabusUnitChapters(getSortedUnitChapter(unit.getSyllabusUnitChapters()));
                    for (SyllabusUnitChapterDTO unitChapter : unit.getSyllabusUnitChapters()) {
                        // set sorted material list by UpdatedDate oldest to newest
                        unitChapter.setMaterials(getSortedMaterial(unitChapter.getMaterials()));
                    }
                }
            }
        }
        return syllabusDTO;
    }

    /**
     * <p>
     * sort syllabus day
     * </p>
     *
     * @param list dayDTO
     * @return list dayDTO
     * @author KietPTT
     */
    @Override
    public List<SyllabusDayDTO> getSortedDay(List<SyllabusDayDTO> list) {
        if (list != null)
            if (list.size() > 1)
                // by DayNo lowest to highest
                Collections.sort(list, new Comparator<SyllabusDayDTO>() {
                    @Override
                    public int compare(SyllabusDayDTO o1, SyllabusDayDTO o2) {
                        return o2.getDayNo() > (o1.getDayNo()) ? -1 : 1;
                    }
                });
        return list;
    }

    /**
     * <p>
     * sort syllabus unit
     * </p>
     *
     * @param list unitDTO
     * @return list unitDTO
     * @author KietPTT
     */
    @Override
    public List<SyllabusUnitDTO> getSortedUnit(List<SyllabusUnitDTO> list) {
        if (list != null)
            if (list.size() > 1)
                // by UnitNo lowest to highest
                Collections.sort(list, new Comparator<SyllabusUnitDTO>() {
                    @Override
                    public int compare(SyllabusUnitDTO o1, SyllabusUnitDTO o2) {
                        return o2.getUnitNo() > (o1.getUnitNo()) ? -1 : 1;
                    }
                });
        return list;
    }

    /**
     * <p>
     * sort syllabus unit chapter
     * </p>
     *
     * @param list unitchapterDTO
     * @return list unitchapterDTO
     * @author KietPTT
     */
    @Override
    public List<SyllabusUnitChapterDTO> getSortedUnitChapter(List<SyllabusUnitChapterDTO> list) {
        if (list != null)
            if (list.size() > 1)
                // by Duration lowest to highest
                Collections.sort(list, new Comparator<SyllabusUnitChapterDTO>() {
                    @Override
                    public int compare(SyllabusUnitChapterDTO o1, SyllabusUnitChapterDTO o2) {
                        return o2.getDuration() > (o1.getDuration()) ? -1 : 1;
                    }
                });
        return list;
    }

    /**
     * <p>
     * sort material
     * </p>
     *
     * @param list materialDTO
     * @return list materialDTO
     * @author KietPTT
     */
    @Override
    public List<MaterialDTO> getSortedMaterial(List<MaterialDTO> list) {
        if (list != null)
            if (list.size() > 1)
                // by UpdatedDate oldest to newest
                Collections.sort(list, new Comparator<MaterialDTO>() {
                    @Override
                    public int compare(MaterialDTO o1, MaterialDTO o2) {
                        return o2.getUpdatedDate().after(o1.getUpdatedDate()) ? -1 : 1;
                    }
                });
        return list;
    }

    /**
     * <p>
     * remove deleted status object in Syllabus
     * </p>
     *
     * @param syllabusDTO
     * @return syllabusDTO
     * @author KietPTT
     */
    @Override
    public SyllabusDTO getActiveSyllabus(SyllabusDTO syllabusDTO) {
        if (syllabusDTO != null) {
            // remove deleted day
            syllabusDTO.setSyllabusDays(removeDeletedDay(syllabusDTO.getSyllabusDays()));
            for (SyllabusDayDTO day : syllabusDTO.getSyllabusDays()) {
                for (SyllabusUnitDTO unit : day.getSyllabusUnits()) {
                    for (SyllabusUnitChapterDTO unitChapter : unit.getSyllabusUnitChapters()) {
                        // remove deleted material
                        unitChapter.setMaterials(removeDeletedMaterial(unitChapter.getMaterials()));
                    }
                }
            }
        }
        return syllabusDTO;
    }

    /**
     * <p>
     * remove deleted status object in Syllabus material
     * </p>
     *
     * @param list {@code List<MaterialDTO>}
     * @return List
     * @author KietPTT
     */
    @Override
    public List<MaterialDTO> removeDeletedMaterial(List<MaterialDTO> list) {
        if (list != null)
            for (int i = list.size() - 1; i >= 0; i--) {
                // remove deleted status
                if (list.get(i).getMaterialStatus().equals(MaterialStatus.DELETED)) {
                    list.remove(list.get(i));
                }
            }
        return list;
    }

    /**
     * <p>
     * remove deleted status object in Syllabus day
     * </p>
     *
     * @param list {@code List<SyllabusDayDTO>}
     * @return List
     * @author KietPTT
     */
    @Override
    public List<SyllabusDayDTO> removeDeletedDay(List<SyllabusDayDTO> list) {
        if (list != null)
            for (int i = list.size() - 1; i >= 0; i--) {
                // remove deleted status
                if (list.get(i).getStatus().equals(SyllabusDayStatus.DELETED)) {
                    list.remove(list.get(i));
                }
            }
        return list;
    }

    /**
     * <p>
     * import outputstandard list into syllabus
     * </p>
     *
     * @param syllabusDTO
     * @return syllabusDTO
     * @author TrungNT
     */
    @Override
    public SyllabusDTO getOutputStandard(SyllabusDTO syllabusDTO) {
        Set<OutputStandardDTO> list = new HashSet();
        if (syllabusDTO != null) {
            for (SyllabusDayDTO day : syllabusDTO.getSyllabusDays()) {
                for (SyllabusUnitDTO unit : day.getSyllabusUnits()) {
                    for (SyllabusUnitChapterDTO unitChapter : unit.getSyllabusUnitChapters()) {
                        // add output standard of unit chapter into list
                        if(unitChapter.getOutputStandard() != null){
                            list.add(unitChapter.getOutputStandard());
                        }
                    }
                }
            }

            // change type to arraylist
            List<OutputStandardDTO> result = new ArrayList<>();
            for (OutputStandardDTO os : list) {
                result.add(os);
            }
            syllabusDTO.setOutputStandardCovered(result);
        }
        return syllabusDTO;
    }

    public SyllabusDTO saveDraftSyllabus(FormSyllabusDTOWithoutId syllabusDTO) {
        Syllabus syllabus, syllabusWithId;
        SyllabusLevel syllabusLevel;
        List<SyllabusDayDTOWithoutId> syllabusDayList;
        List<SyllabusUnit> syllabusUnitList;
        if (syllabusDTO.getSyllabusLevel().getId() != null) {
            syllabusLevel = syllabusLevelRepository.findById(syllabusDTO.getSyllabusLevel().getId()).get();
            syllabusDTO.setSyllabusLevel(modelMapper.map(syllabusLevel, SyllabusLevelDTO.class));
        } else {
            syllabusDTO.setSyllabusLevel(null);
        }
//		syllabusLevel = syllabusLevelRepository.findById(syllabusDTO.getSyllabusLevel().getId()).get();
//		syllabusDTO.setSyllabusLevel(syllabusLevel);
        syllabus = modelMapper.map(syllabusDTO, Syllabus.class);
        syllabus.setStatus(SyllabusStatus.DRAFT);
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User createdByUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));
        User updatedByUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));
        syllabus.setVersion("1.0");
        syllabus.setCreatedDate(new Date());
        syllabus.setUpdatedDate(new Date());
        syllabus.setCreatedBy(createdByUser.getId());
        syllabus.setUpdatedBy(updatedByUser.getId());
        syllabusDayList = syllabusDTO.getSyllabusDays();
        if (syllabusDayList == null) {
            syllabus.setSyllabusDays(null);
            syllabusWithId = syllabusRepository.save(syllabus);
        } else {
            syllabusWithId = syllabusRepository.save(syllabus);

//            || SET SYLLABUS ID TO SYLLABUS DAY ||
            for (int i = 0; i <= syllabusWithId.getSyllabusDays().size() - 1; i++) {
                syllabusWithId.getSyllabusDays().get(i).setStatus(SyllabusDayStatus.AVAILABLE);
                syllabusUnitList = syllabusWithId.getSyllabusDays().get(i).getSyllabusUnits();
                if (syllabusUnitList == null) {
                    syllabusWithId.getSyllabusDays().get(i).setSyllabusUnits(null);
                    syllabusWithId.getSyllabusDays().get(i).setSyllabus(syllabusWithId);
                    syllabusDayRepository.save(syllabusWithId.getSyllabusDays().get(i));
                } else {
                    draftSyllabusDay(syllabusWithId, syllabusWithId.getSyllabusDays().get(i));
                }
            }
        }

//            || SET SYLLABUS ID TO ASSESSMENT SCHEME ||
        createAssessmentScheme(syllabusWithId, syllabusWithId.getAssessmentScheme());

//            || SET SYLLABUS ID TO DELIVERY PRINCIPLE ||
        createDeliveryPrinciple(syllabusWithId, syllabusWithId.getDeliveryPrinciple());
//        return modelMapper.map(syllabusWithId, SyllabusDTO.class);
//        UserDTO createdByUser, updatedByUser;
//        createdByUser = getUserById(syllabusWithId.getCreatedBy());
//        updatedByUser = getUserById(syllabusWithId.getUpdatedBy());

        SyllabusDTO result;
        result = modelMapper.map(syllabusWithId, SyllabusDTO.class);
        result.setCreatedDate(new Date());
        result.setUpdatedDate(new Date());
        result.setCreatedBy(createdByUser.getId());
        result.setUpdatedBy(updatedByUser.getId());
        result.setCreatedByUser(modelMapper.map(createdByUser, UserDTO.class));
        result.setUpdatedByUser(modelMapper.map(updatedByUser, UserDTO.class));
//
//        for (SyllabusDayDTOWithoutId SyllabusDayDTOWithoutId : result.getSyllabusDays()) {
//            for (SyllabusUnitDTO syllabusUnitDTO : SyllabusDayDTOWithoutId.getSyllabusUnits()) {
//                for (SyllabusUnitChapterDTO syllabusUnitChapterDTO : syllabusUnitDTO.getSyllabusUnitChapters()) {
//                    for (MaterialDTO materialDTO : syllabusUnitChapterDTO.getMaterials()) {
//                        materialDTO.setCreatedByUser(getUserById(materialDTO.getCreatedBy()));
//                        materialDTO.setUpdatedByUser(getUserById(materialDTO.getUpdatedBy()));
//                    }
//                }
//            }
//        }
//
        return result;
    }

    public SyllabusDayDTOWithoutId draftSyllabusDay(Syllabus syllabus, SyllabusDay syllabusDay) {
        SyllabusDay syllabusDayWithId;
        SyllabusDayDTOWithoutId result;
        syllabusDay.setSyllabus(syllabus);
        List<SyllabusUnitChapter> syllabusUnitChapterList;
        List<Material> materialList;

//            || SET SYLLABUS UNIT DATA ||
        for (int i = 0; i <= syllabusDay.getSyllabusUnits().size() - 1; i++) {
            SyllabusUnit syllabusUnit = syllabusDay.getSyllabusUnits().get(i);
            syllabusUnit.setSyllabusDay(syllabusDay);
            syllabusUnit.setUnitNo(syllabusDay.getSyllabusUnits().get(i).getUnitNo());
            syllabusUnit.setName(syllabusDay.getSyllabusUnits().get(i).getName());
            syllabusUnit.setDuration(syllabusDay.getSyllabusUnits().get(i).getDuration());
            syllabusUnitChapterList = syllabusDay.getSyllabusUnits().get(i).getSyllabusUnitChapters();

            if (syllabusUnitChapterList == null) {
                syllabusUnit.setSyllabusUnitChapters(null);
                syllabusUnitRepository.save(syllabusUnit);
            } else {
                // if not null, set unit chapters to unit and set other data to unit chapter
                syllabusUnit.setSyllabusUnitChapters(syllabusDay.getSyllabusUnits().get(i).getSyllabusUnitChapters());
                syllabusUnitRepository.save(syllabusUnit);

                // || SET SYLLABUS UNIT CHAPTER DATA ||
                for (int j = 0; j <= syllabusUnit.getSyllabusUnitChapters().size() - 1; j++) {
                    SyllabusUnitChapter syllabusUnitChapter = syllabusUnit.getSyllabusUnitChapters().get(j);
                    syllabusUnitChapter.setSyllabusUnit(syllabusUnit);

                    // || SET DELIVERY TYPE & OUTPUT STANDARD ||
                    if (syllabusUnitChapter.getDeliveryType() != null) {
                        DeliveryType deliveryType = deliveryTypeRepository
                                .findById(syllabusUnitChapter.getDeliveryType().getId()).get();
                        syllabusUnitChapter.setDeliveryType(deliveryType);
                    } else {
                        syllabusUnitChapter.setDeliveryType(null);
                    }

                    if (syllabusUnitChapter.getOutputStandard() != null) {
                        OutputStandard outputStandard = outputStandardRepository
                                .findById(syllabusUnitChapter.getOutputStandard().getId()).get();
                        syllabusUnitChapter.setOutputStandard(outputStandard);
                    } else {
                        syllabusUnitChapter.setOutputStandard(null);
                    }

                    syllabusUnitChapter.setName(syllabusUnit.getSyllabusUnitChapters().get(j).getName());
                    syllabusUnitChapter.setDuration(syllabusUnit.getSyllabusUnitChapters().get(j).getDuration());

                    // || SET MATERIALS ||
                    materialList = syllabusUnitChapter.getMaterials();
                    if (materialList == null) {
                        syllabusUnitChapter.setMaterials(null);
                    } else {
                        for (int k = 0; k <= syllabusUnitChapter.getMaterials().size() - 1; k++) {
                            Material material = syllabusUnitChapter.getMaterials().get(k);
                            material.setUnitChapter(syllabusUnitChapter);
                            material.setName(syllabusUnitChapter.getMaterials().get(k).getName());
                            material.setUrl(syllabusUnitChapter.getMaterials().get(k).getUrl());
                            UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                            User createdByUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                                    () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));
                            User updatedByUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                                    () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));
                            material.setCreatedDate(new Date());
                            material.setUpdatedDate(new Date());
                            material.setCreatedBy(createdByUser.getId());
                            material.setUpdatedBy(updatedByUser.getId());
//                            material.setCreatedBy(syllabusUnitChapter.getMaterials().get(k).getCreatedBy());
//                            material.setCreatedDate(syllabusUnitChapter.getMaterials().get(k).getCreatedDate());
//                            material.setUpdatedBy(syllabusUnitChapter.getMaterials().get(k).getUpdatedBy());
//                            material.setUpdatedDate(syllabusUnitChapter.getMaterials().get(k).getUpdatedDate());
                            material.setMaterialStatus(syllabusUnitChapter.getMaterials().get(k).getMaterialStatus());
                            materialRepository.save(material);
                            syllabusUnitChapter.setMaterials(syllabusUnit.getSyllabusUnitChapters().get(j).getMaterials());
                        }
                    }

                    syllabusUnitChapter.setOnline(syllabusUnit.getSyllabusUnitChapters().get(j).isOnline());
                    syllabusUnitChapterRepository.save(syllabusUnitChapter);
                }
            }
        }

        syllabusDayWithId = syllabusDayRepository.save(syllabusDay);

        result = modelMapper.map(syllabusDayWithId, SyllabusDayDTOWithoutId.class);
        return result;
    }

    @Scheduled(fixedRate = 1209600033)
    public void autoDeleteDraft() {
        syllabusRepository.setForeignKey0();
        syllabusRepository.deleteDraft();
        syllabusRepository.setForeignKey1();
    }

    public SyllabusDTO createSyllabus(FormSyllabusDTOWithoutId syllabusDTO) {
        Syllabus syllabus, syllabusWithId;
        SyllabusLevel syllabusLevel;
        syllabusLevel = syllabusLevelRepository.findById(syllabusDTO.getSyllabusLevel().getId()).get();
        syllabusDTO.setSyllabusLevel(modelMapper.map(syllabusLevel, SyllabusLevelDTO.class));
        syllabus = modelMapper.map(syllabusDTO, Syllabus.class);

        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User createdByUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));
        User updatedByUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));

        /*
        List<Syllabus> sameNameSyllabuses = syllabusRepository.searchSyllabusByKeyword(syllabusDTO.getName(), SyllabusStatus.ACTIVE);
        String sNewVersion = syllabusRepository.findVersionByName(syllabusDTO.getName());
        Double newVersion;
        if (sNewVersion == null) {
            newVersion = 1.0;
        } else {
            newVersion = Double.parseDouble(sNewVersion);
            for (Syllabus sameNameSyllabus :  sameNameSyllabuses) {
                newVersion = newVersion + 1.0;
            }
        }
        NumberFormat numf = NumberFormat.getNumberInstance();
        numf.setMaximumFractionDigits(1);
        syllabus.setVersion(numf.format(newVersion));
         */

        syllabus.setVersion("1.0");
        syllabus.setStatus(SyllabusStatus.ACTIVE);
        syllabus.setCreatedDate(new Date());
        syllabus.setUpdatedDate(new Date());
        syllabus.setCreatedBy(createdByUser.getId());
        syllabus.setUpdatedBy(updatedByUser.getId());
        syllabusWithId = syllabusRepository.save(syllabus);

//            || SET SYLLABUS ID TO SYLLABUS DAY ||
        for (int i = 0; i <= syllabusWithId.getSyllabusDays().size() - 1; i++) {
            createSyllabusDay(syllabusWithId, syllabusWithId.getSyllabusDays().get(i));
        }

//            || SET SYLLABUS ID TO ASSESSMENT SCHEME ||
        createAssessmentScheme(syllabusWithId, syllabusWithId.getAssessmentScheme());

//            || SET SYLLABUS ID TO DELIVERY PRINCIPLE ||
        createDeliveryPrinciple(syllabusWithId, syllabusWithId.getDeliveryPrinciple());
//		return modelMapper.map(syllabusWithId, SyllabusDTO.class);

//        createdByUser = getUserById(syllabusWithId.getCreatedBy());
//        updatedByUser = getUserById(syllabusWithId.getUpdatedBy());
        SyllabusDTO result;
        result = modelMapper.map(syllabusWithId, SyllabusDTO.class);
        result.setCreatedDate(new Date());
        result.setUpdatedDate(new Date());
        result.setCreatedBy(createdByUser.getId());
        result.setUpdatedBy(updatedByUser.getId());
        result.setCreatedByUser(modelMapper.map(createdByUser, UserDTO.class));
        result.setUpdatedByUser(modelMapper.map(updatedByUser, UserDTO.class));

        for (SyllabusDayDTO syllabusDayDTO : result.getSyllabusDays()) {
            for (SyllabusUnitDTO syllabusUnitDTO : syllabusDayDTO.getSyllabusUnits()) {
                for (SyllabusUnitChapterDTO syllabusUnitChapterDTO : syllabusUnitDTO.getSyllabusUnitChapters()) {
                    for (MaterialDTO materialDTO : syllabusUnitChapterDTO.getMaterials()) {
                        materialDTO.setCreatedDate(new Date());
                        materialDTO.setUpdatedDate(new Date());
                        materialDTO.setCreatedBy(createdByUser.getId());
                        materialDTO.setUpdatedBy(updatedByUser.getId());
                        materialDTO.setCreatedByUser(modelMapper.map(createdByUser, UserDTO.class));
                        materialDTO.setUpdatedByUser(modelMapper.map(updatedByUser, UserDTO.class));
                    }
                }
            }
        }

        return result;
    }

    public SyllabusDayDTOWithoutId createSyllabusDay(Syllabus syllabus, SyllabusDay syllabusDay) {
        SyllabusDay syllabusDayWithId;
        SyllabusDayDTOWithoutId result;
        syllabusDay.setSyllabus(syllabus);
//		User createdByUser;
//		User updatedByUser;
//		MaterialDTO materialDTO;

//            || SET SYLLABUS UNIT DATA ||
        for (int i = 0; i <= syllabusDay.getSyllabusUnits().size() - 1; i++) {
            SyllabusUnit syllabusUnit = syllabusDay.getSyllabusUnits().get(i);
            syllabusUnit.setSyllabusDay(syllabusDay);
            syllabusUnit.setUnitNo(syllabusDay.getSyllabusUnits().get(i).getUnitNo());
            syllabusUnit.setName(syllabusDay.getSyllabusUnits().get(i).getName());
            syllabusUnit.setDuration(syllabusDay.getSyllabusUnits().get(i).getDuration());
            syllabusUnit.setSyllabusUnitChapters(syllabusDay.getSyllabusUnits().get(i).getSyllabusUnitChapters());
            syllabusUnitRepository.save(syllabusUnit);

//            || SET SYLLABUS UNIT CHAPTER DATA ||
            for (int j = 0; j <= syllabusUnit.getSyllabusUnitChapters().size() - 1; j++) {
                SyllabusUnitChapter syllabusUnitChapter = syllabusUnit.getSyllabusUnitChapters().get(j);
                syllabusUnitChapter.setSyllabusUnit(syllabusUnit);

//            || SET DELIVERY TYPE & OUTPUT STANDARD ||
                DeliveryType deliveryType = deliveryTypeRepository
                        .findById(syllabusUnitChapter.getDeliveryType().getId()).get();
                syllabusUnitChapter.setDeliveryType(deliveryType);
//
                OutputStandard outputStandard = outputStandardRepository
                        .findById(syllabusUnitChapter.getOutputStandard().getId()).get();
                syllabusUnitChapter.setOutputStandard(outputStandard);

                syllabusUnitChapter.setName(syllabusUnit.getSyllabusUnitChapters().get(j).getName());
                syllabusUnitChapter.setDuration(syllabusUnit.getSyllabusUnitChapters().get(j).getDuration());

//            || SET MATERIALS ||
                for (int k = 0; k <= syllabusUnitChapter.getMaterials().size() - 1; k++) {
                    Material material = syllabusUnitChapter.getMaterials().get(k);
                    material.setUnitChapter(syllabusUnitChapter);
                    material.setName(syllabusUnitChapter.getMaterials().get(k).getName());
                    material.setUrl(syllabusUnitChapter.getMaterials().get(k).getUrl());
                    material.setCreatedBy(syllabusUnitChapter.getMaterials().get(k).getCreatedBy());
//					createdByUser = userRepository.findById(material.getCreatedBy()).get();
                    material.setCreatedDate(syllabusUnitChapter.getMaterials().get(k).getCreatedDate());
                    material.setUpdatedBy(syllabusUnitChapter.getMaterials().get(k).getUpdatedBy());
                    material.setUpdatedDate(syllabusUnitChapter.getMaterials().get(k).getUpdatedDate());
                    material.setMaterialStatus(syllabusUnitChapter.getMaterials().get(k).getMaterialStatus());
                    materialRepository.save(material);
//					materialDTO = modelMapper.map(material, MaterialDTO.class);
//					materialDTO.setCreatedByUser(modelMapper.map(createdByUser, UserDTO.class));
                }

                syllabusUnitChapter.setMaterials(syllabusUnit.getSyllabusUnitChapters().get(j).getMaterials());
                syllabusUnitChapter.setOnline(syllabusUnit.getSyllabusUnitChapters().get(j).isOnline());
                syllabusUnitChapterRepository.save(syllabusUnitChapter);
            }
        }

        syllabusDay.setStatus(SyllabusDayStatus.AVAILABLE);
        syllabusDayWithId = syllabusDayRepository.save(syllabusDay);

        result = modelMapper.map(syllabusDayWithId, SyllabusDayDTOWithoutId.class);
        return result;
    }

    public AssessmentSchemeDTO createAssessmentScheme(Syllabus syllabus, AssessmentScheme assessmentScheme) {
        AssessmentScheme assessmentSchemeWithId;
        AssessmentSchemeDTO result;
        assessmentScheme.setSyllabus(syllabus);
        assessmentSchemeWithId = assessmentSchemeRepository.save(assessmentScheme);
        result = modelMapper.map(assessmentSchemeWithId, AssessmentSchemeDTO.class);
        return result;
    }

    public DeliveryPrincipleDTO createDeliveryPrinciple(Syllabus syllabus, DeliveryPrinciple deliveryPrinciple) {
        DeliveryPrinciple deliveryPrincipleWithId;
        DeliveryPrincipleDTO result;
        deliveryPrinciple.setSyllabus(syllabus);
        deliveryPrincipleWithId = deliveryPrincipleRepository.save(deliveryPrinciple);
        result = modelMapper.map(deliveryPrincipleWithId, DeliveryPrincipleDTO.class);
        return result;
    }

    public void mapReapExcelDatatoDB(MultipartFile reapExcelDataFile, List<String> checkbox, String radio) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
        XSSFSheet worksheet1 = workbook.getSheetAt(0);
        XSSFSheet worksheet2 = workbook.getSheetAt(1);

        Syllabus syllabus = new Syllabus();
        DeliveryPrinciple deliveryprinciple = new DeliveryPrinciple();
        AssessmentScheme assessmentScheme = new AssessmentScheme();
        SyllabusDay syllabusDay = new SyllabusDay();
        syllabusDay.setStatus(SyllabusDayStatus.AVAILABLE);

        syllabus.setDays(Integer.parseInt(String.valueOf(worksheet1.getRow(13).getCell(6).getStringCellValue().charAt(0))));
        syllabus.setName(worksheet1.getRow(2).getCell(3).getStringCellValue());
        syllabus.setCode(worksheet1.getRow(3).getCell(3).getStringCellValue());
        syllabus.setVersion(worksheet1.getRow(4).getCell(3).getStringCellValue());
        syllabus.setCourseObjective(worksheet1.getRow(6).getCell(3).getStringCellValue() +
                "\n" + worksheet1.getRow(11).getCell(3).getStringCellValue());
        syllabus.setTechnicalRequirement(worksheet1.getRow(22).getCell(4).getStringCellValue());
        syllabus.setHours((int) worksheet2.getRow(37).getCell(5).getNumericCellValue());
        syllabus.setStatus(SyllabusStatus.DRAFT);
        syllabus.setAssessmentScheme(assessmentScheme);
        syllabus.setDeliveryPrinciple(deliveryprinciple);
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User createdByUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));
        User updatedByUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));
        syllabus.setCreatedBy(createdByUser.getId());
        syllabus.setUpdatedBy(updatedByUser.getId());

        boolean dupe = true;
        if (checkbox.size() == 1) {
            if (checkbox.get(0).equals("code")) {
                //name
                if (syllabusRepository.findAllByCode(syllabus.getCode()).isEmpty()) {
                    Syllabus savedSyllabus = syllabusRepository.save(syllabus);
                    assessmentScheme.setSyllabus(savedSyllabus);
                    deliveryprinciple.setSyllabus(savedSyllabus);
                    syllabusDay.setSyllabus(savedSyllabus);
                    dupe = false;
                }
            } else {
                if (syllabusRepository.findAllByName(syllabus.getName()).isEmpty()) {
                    Syllabus savedSyllabus = syllabusRepository.save(syllabus);
                    assessmentScheme.setSyllabus(savedSyllabus);
                    deliveryprinciple.setSyllabus(savedSyllabus);
                    syllabusDay.setSyllabus(savedSyllabus);
                    dupe = false;
                }
            }
        } else {
            //ca 2
            if (syllabusRepository.findAllByName(syllabus.getName()).isEmpty()) {
                if (syllabusRepository.findAllByCode(syllabus.getCode()).isEmpty()) {
                    Syllabus savedSyllabus = syllabusRepository.save(syllabus);
                    assessmentScheme.setSyllabus(savedSyllabus);
                    deliveryprinciple.setSyllabus(savedSyllabus);
                    syllabusDay.setSyllabus(savedSyllabus);
                    dupe = false;
                }
            }
        }
        if (dupe) {
            if (radio.equals("allow")) {
                Syllabus savedSyllabus = syllabusRepository.save(syllabus);
                assessmentScheme.setSyllabus(savedSyllabus);
                deliveryprinciple.setSyllabus(savedSyllabus);
                syllabusDay.setSyllabus(savedSyllabus);
            } else if (radio.equals("replace")) {
                if (checkbox.size() == 1) {
                    if (checkbox.get(0).equals("code")) {

                        for (Syllabus asylabus : syllabusRepository.findAllByCode(syllabus.getCode())) {
                            syllabusRepository.setForeignKey0();
                            syllabusRepository.deleteById1(asylabus.getId().toString());
                            syllabusRepository.setForeignKey1();
                        }
                        Syllabus savedSyllabus = syllabusRepository.save(syllabus);
                        assessmentScheme.setSyllabus(savedSyllabus);
                        deliveryprinciple.setSyllabus(savedSyllabus);
                        syllabusDay.setSyllabus(savedSyllabus);
                    } else {
                        for (Syllabus asylabus : syllabusRepository.findAllByName(syllabus.getName())) {
                            syllabusRepository.setForeignKey0();
                            syllabusRepository.deleteById1(asylabus.getId().toString());
                            syllabusRepository.setForeignKey1();
                        }
                        Syllabus savedSyllabus = syllabusRepository.save(syllabus);
                        assessmentScheme.setSyllabus(savedSyllabus);
                        deliveryprinciple.setSyllabus(savedSyllabus);
                        syllabusDay.setSyllabus(savedSyllabus);
                    }
                } else {
                    //ca 2
                    for (Syllabus asylabus : syllabusRepository.findAllByCode(syllabus.getCode())) {
                        syllabusRepository.setForeignKey0();
                        syllabusRepository.deleteById1(asylabus.getId().toString());
                        syllabusRepository.setForeignKey1();
                    }
                    for (Syllabus asylabus : syllabusRepository.findAllByName(syllabus.getName())) {
                        syllabusRepository.setForeignKey0();
                        syllabusRepository.deleteById1(asylabus.getId().toString());
                        syllabusRepository.setForeignKey1();
                    }
                    Syllabus savedSyllabus = syllabusRepository.save(syllabus);
                    assessmentScheme.setSyllabus(savedSyllabus);
                    deliveryprinciple.setSyllabus(savedSyllabus);
                    syllabusDay.setSyllabus(savedSyllabus);
                }
            }
        }

//  9.Assessment Scheme
        assessmentScheme.setQuiz(worksheet1.getRow(23).getCell(4).getNumericCellValue());
        assessmentScheme.setAssignment(worksheet1.getRow(24).getCell(4).getNumericCellValue());
        assessmentScheme.setFinalTheory(worksheet1.getRow(25).getCell(4).getNumericCellValue());
        assessmentScheme.setFinalPractice(worksheet1.getRow(26).getCell(4).getNumericCellValue());
        assessmentScheme.setGpa(worksheet1.getRow(27).getCell(4).getNumericCellValue());

//  10.Training Delivery Principles
        deliveryprinciple.setTrainees(worksheet1.getRow(28).getCell(4).getStringCellValue());
        deliveryprinciple.setTrainer(worksheet1.getRow(29).getCell(4).getStringCellValue());
        deliveryprinciple.setTraining(worksheet1.getRow(30).getCell(4).getStringCellValue());
        deliveryprinciple.setRe_test(worksheet1.getRow(31).getCell(4).getStringCellValue());
        deliveryprinciple.setMarking(worksheet1.getRow(32).getCell(4).getStringCellValue());
        deliveryprinciple.setWaiverCriteria(worksheet1.getRow(33).getCell(4).getStringCellValue());
        deliveryprinciple.setOthers(worksheet1.getRow(34).getCell(4).getStringCellValue());

        SyllabusDay savedSyllabusDay = syllabusDayRepository.save(syllabusDay);

//----------------------------------------------------------------------------------------------------------------------
//Sheet 2 :Schedule
        int numMerged = worksheet2.getNumMergedRegions();
        for (int n = 0; n < numMerged; n++) {

            SyllabusUnit syllabusUnit = new SyllabusUnit();

            int firstRow = worksheet2.getMergedRegion(n).getFirstRow();
            int lastRow = worksheet2.getMergedRegion(n).getLastRow();
            syllabusUnit.setName(worksheet2.getRow(firstRow).getCell(1).getStringCellValue());
            syllabusUnit.setUnitNo((int) worksheet2.getRow(firstRow).getCell(2).getNumericCellValue());
            syllabusUnit.setSyllabusDay(savedSyllabusDay);

            double sum = 0;
            for (int c = firstRow; c <= lastRow; c++) {
                sum = sum + worksheet2.getRow(c).getCell(5).getNumericCellValue();

                SyllabusUnitChapter syllabusUnitChapter = new SyllabusUnitChapter();
//				DeliveryType deliveryType = new DeliveryType();
                Material material = new Material();

                syllabusUnitChapter.setName(worksheet2.getRow(c).getCell(3).getStringCellValue());
                String name = worksheet2.getRow(c).getCell(4).getStringCellValue();
                List<DeliveryType> deliveryType = new ArrayList<>();
                deliveryType = deliveryTypeRepository.findByName(name);
//				deliveryType.setName(worksheet2.getRow(c).getCell(4).getStringCellValue());
                syllabusUnitChapter.setDuration(worksheet2.getRow(c).getCell(5).getNumericCellValue());
//				deliveryType.setDescription(worksheet2.getRow(c).getCell(6).getStringCellValue());

                syllabusUnitChapter.setDeliveryType(deliveryType.get(0));
                syllabusUnitChapter.setSyllabusUnit(syllabusUnit);
//				syllabusUnitChapter.setMaterial(material);
                syllabusUnitChapterRepository.save(syllabusUnitChapter);
            }
            syllabusUnit.setDuration((int) sum);
            syllabusUnitRepository.save(syllabusUnit);
        }
    }

    @Override
    public SyllabusDTO updateSyllabus(FormSyllabusDTO syllabusDTO) {
        Syllabus syllabus, syllabusWithId;
        SyllabusLevel syllabusLevel;
        SyllabusStatus status = syllabusDTO.getStatus();
        syllabusLevel = syllabusLevelRepository.findById(syllabusDTO.getSyllabusLevel().getId()).get();
        syllabusDTO.setSyllabusLevel(modelMapper.map(syllabusLevel, SyllabusLevelDTO.class));
        syllabus = modelMapper.map(syllabusDTO, Syllabus.class);
        syllabus.setStatus(status);
        // Update version + 0.1
        String name = syllabus.getName();
        String sNewVersion = syllabusRepository.findVersionByName(name);
        Double newVersion;
        if (sNewVersion == null) {
            newVersion = 1.0;
        } else {
            newVersion = Double.parseDouble(sNewVersion);
            newVersion = newVersion + 0.1;
        }

        NumberFormat numf = NumberFormat.getNumberInstance();
        numf.setMaximumFractionDigits(1);
        syllabus.setVersion(numf.format(newVersion));

        syllabusWithId = syllabusRepository.save(syllabus);

        for (int i = 0; i <= syllabusWithId.getSyllabusDays().size() - 1; i++) {
            createSyllabusDay(syllabusWithId, syllabusWithId.getSyllabusDays().get(i));
        }
        createAssessmentScheme(syllabusWithId, syllabusWithId.getAssessmentScheme());
        createDeliveryPrinciple(syllabusWithId, syllabusWithId.getDeliveryPrinciple());
      //  return modelMapper.map(syllabusWithId, SyllabusDTO.class);
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User createdByUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));
        User updatedByUser = userRepository.findById(userPrinciple.getId()).orElseThrow(
                () -> new UserNotFoundException(userPrinciple.getId().toString(), "user login profile not found"));

//        createdByUser = getUserById(syllabusWithId.getCreatedBy());
//        updatedByUser = getUserById(syllabusWithId.getUpdatedBy());
        SyllabusDTO result;
        result = modelMapper.map(syllabusWithId, SyllabusDTO.class);
        result.setCreatedDate(syllabusDTO.getCreatedDate());
        result.setUpdatedDate(new Date());
        result.setCreatedBy(syllabusDTO.getCreatedBy());
        result.setUpdatedBy(updatedByUser.getId());
        result.setCreatedByUser(modelMapper.map(createdByUser, UserDTO.class));
        result.setUpdatedByUser(modelMapper.map(updatedByUser, UserDTO.class));

        for (SyllabusDayDTO syllabusDayDTO : result.getSyllabusDays()) {
            for (SyllabusUnitDTO syllabusUnitDTO : syllabusDayDTO.getSyllabusUnits()) {
                for (SyllabusUnitChapterDTO syllabusUnitChapterDTO : syllabusUnitDTO.getSyllabusUnitChapters()) {
                    for (MaterialDTO materialDTO : syllabusUnitChapterDTO.getMaterials()) {
                        materialDTO.setCreatedDate(materialDTO.getCreatedDate());
                        materialDTO.setUpdatedDate(new Date());
                        materialDTO.setCreatedBy(materialDTO.getCreatedBy());
                        materialDTO.setUpdatedBy(updatedByUser.getId());
                        materialDTO.setCreatedByUser(modelMapper.map(createdByUser, UserDTO.class));
                        materialDTO.setUpdatedByUser(modelMapper.map(updatedByUser, UserDTO.class));
                    }
                }
            }
        }

        return result;
    }
}
