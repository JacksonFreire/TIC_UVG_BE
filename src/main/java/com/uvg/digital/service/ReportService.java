package com.uvg.digital.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.uvg.digital.entity.Enrollment;
import com.uvg.digital.entity.User;
import com.uvg.digital.repository.EnrollmentRepository;

@Service
public class ReportService {

    private final EnrollmentRepository enrollmentRepository;

    public ReportService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public ResponseEntity<byte[]> generateParticipantReport(Long courseId) throws IOException {
        // Obtener todas las inscripciones del curso, incluyendo todos los estados
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        // Verificar que existan inscripciones y que el curso esté definido
        if (enrollments.isEmpty() || enrollments.get(0).getCourse() == null) {
            throw new IllegalArgumentException("No se encontraron inscripciones o el curso no existe.");
        }
        
        String courseName = enrollments.get(0).getCourse().getName();

        // Crear el libro de Excel y una hoja para los participantes
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Participantes");

        // Encabezado con el nombre de la empresa
        String titleText = "UNIVERITAS GROUP - Reporte de Participantes";
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(titleText);

        // Estilo para el título
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);

        // Fusionar celdas para el título
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        // Encabezado del curso
        Row courseRow = sheet.createRow(1);
        Cell courseCell = courseRow.createCell(0);
        courseCell.setCellValue("Curso: " + courseName);

        CellStyle courseStyle = workbook.createCellStyle();
        Font courseFont = workbook.createFont();
        courseFont.setFontHeightInPoints((short) 12);
        courseFont.setColor(IndexedColors.DARK_BLUE.getIndex());
        courseStyle.setFont(courseFont);
        courseCell.setCellStyle(courseStyle);

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

        // Espacio antes del encabezado de columnas
        sheet.createRow(2);

        // Crear encabezado de columnas
        Row headerRow = sheet.createRow(3);
        String[] headers = {"Nombres", "Username", "Teléfono", "Email", "Estado de Inscripción"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos de los participantes
        int rowIdx = 4;
        for (Enrollment enrollment : enrollments) {
            Row row = sheet.createRow(rowIdx++);
            User user = enrollment.getUser();

            row.createCell(0).setCellValue(user.getFirstName() + " " + user.getLastName());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getPhoneNumber());
            row.createCell(3).setCellValue(user.getEmail());
            row.createCell(4).setCellValue(enrollment.getStatus());
        }

        // Ajustar tamaño de las columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir el libro a un array de bytes
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // Configurar la respuesta HTTP
        HttpHeaders headersResp = new HttpHeaders();
        headersResp.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=participantes_curso_" + courseId + ".xlsx");
        return ResponseEntity.ok()
                .headers(headersResp)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(out.toByteArray());
    }
}
