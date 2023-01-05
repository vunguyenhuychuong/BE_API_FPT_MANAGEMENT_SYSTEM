package com.java8.tms.common.excel;

import com.java8.tms.training_class.dto.TrainingClassDTO;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImportFile {
//    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//    //static String[] HEADERs = { "#","Site","No Class","Course code","Status","Attendee Type","Format Type", "FSU", "University Code", "Technical Group", "Training Program", "Program Content ID", "RECer", "Trainee No", "Plan start date", "Plan end date"};
//    static String SHEET = "HCM";
//
//    public static boolean hasExcelFormat(MultipartFile file) {
//
//        if (!TYPE.equals(file.getContentType())) {
//            return false;
//        }
//
//        return true;
//    }
//
//    public static List<TrainingClassDTO> excelToTrainingClassDTO(InputStream is) {
//        try {
//            Workbook workbook = WorkbookFactory.create(is);
//            Sheet sheet = workbook.getSheet(SHEET);
//            Iterator<Row> rows = sheet.iterator();
//            List<TrainingClassDTO> trainingClassDTOS = new ArrayList<TrainingClassDTO>();
//
//            int rowNumber = 0;
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//
//                // skip header
//                if (rowNumber == 0) {
//                    rowNumber++;
//                    continue;
//                }
//
//                Iterator<Cell> cellsInRow = currentRow.iterator();
//
//                TrainingClassDTO trainingClassDTO = new TrainingClassDTO();
//                while (cellsInRow.hasNext()) {
//                    Cell currentCell = cellsInRow.next();
//                    int columnIndex = currentCell.getColumnIndex();
//                    switch (columnIndex) {
//                        case 3:
//                            trainingClassDTO.setCode(currentCell.getStringCellValue());
//                            break;
//                        case 4:
//                            trainingClassDTO.setClassStatusName(currentCell.getStringCellValue());
//                            break;
//                        case 5:
//                            trainingClassDTO.setAttendeeLevelName(currentCell.getStringCellValue());
//                            break;
//                        case 6:
//                            trainingClassDTO.setFormatTypeName(currentCell.getStringCellValue());
//                            break;
//                        case 7:
//                            trainingClassDTO.setFsuName(currentCell.getStringCellValue());
//                            break;
//                        case 8:
//                            trainingClassDTO.setUniversityCode(currentCell.getStringCellValue());
//                            break;
//                        case 9:
//                            trainingClassDTO.setTechnicalGroupName(currentCell.getStringCellValue());
//                            break;
//                        case 10:
//                            trainingClassDTO.setProgramName(currentCell.getStringCellValue());
//                            break;
//                        case 11:
//                            trainingClassDTO.setCreatedByName(currentCell.getStringCellValue());
//                            break;
//                        case 12:
//                            trainingClassDTO.setTraineeNo((int) currentCell.getNumericCellValue());
//                            break;
//                        case 15:
//                            trainingClassDTO.setDuration((int) currentCell.getNumericCellValue());
//                            break;
//                        case 16:
//                            trainingClassDTO.setTrainerName(currentCell.getStringCellValue());
//                            break;
//                        case 18:
//                            trainingClassDTO.setClassAdminName(currentCell.getStringCellValue());
//                            break;
//                        case 19:
//                            trainingClassDTO.setClassLocationName(currentCell.getStringCellValue());
//                            break;
//
//                        default:
//                            break;
//                    }
//                }
//                trainingClassDTOS.add(trainingClassDTO);
//            }
//            workbook.close();
//            return trainingClassDTOS;
//        } catch ( IOException e) {
//            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
//        }
//    }
}
