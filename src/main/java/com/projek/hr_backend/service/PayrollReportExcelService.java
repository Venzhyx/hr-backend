package com.projek.hr_backend.service;

import com.projek.hr_backend.exception.BadRequestException;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.Payslip;
import com.projek.hr_backend.model.PayrollPeriod;
import com.projek.hr_backend.repository.PayrollPeriodRepository;
import com.projek.hr_backend.repository.PayslipRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PayrollReportExcelService {

    private final PayrollPeriodRepository payrollPeriodRepository;
    private final PayslipRepository       payslipRepository;

    // ─── Public API ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] generatePayrollExcel(int month, int year) {
        PayrollPeriod period = payrollPeriodRepository.findByMonthAndYear(month, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Payroll period not found for " + buildPeriodLabel(month, year)));

        List<Payslip> payslips = payslipRepository.findByPayrollPeriodIdForExport(period.getId());

        if (payslips.isEmpty()) {
            throw new BadRequestException(
                "No payslip data found for period " + buildPeriodLabel(month, year));
        }

        return buildExcel(period, payslips);
    }

    // ─── Excel Builder ────────────────────────────────────────────────────────

    private byte[] buildExcel(PayrollPeriod period, List<Payslip> payslips) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Payroll Report");

            // Styles
            CellStyle titleStyle      = createTitleStyle(workbook);
            CellStyle subTitleStyle   = createSubTitleStyle(workbook);
            CellStyle headerStyle     = createHeaderStyle(workbook);
            CellStyle dataStyle       = createDataStyle(workbook);
            CellStyle currencyStyle   = createCurrencyStyle(workbook);
            CellStyle totalLabelStyle = createTotalLabelStyle(workbook);
            CellStyle totalCurrStyle  = createTotalCurrencyStyle(workbook);
            CellStyle mutedStyle      = createMutedStyle(workbook);

            int rowNum = 0;

            // ── Title ────────────────────────────────────────────────────────
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.setHeight((short) 600);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("PAYROLL REPORT");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            // ── Sub-title: period ─────────────────────────────────────────────
            Row subRow = sheet.createRow(rowNum++);
            subRow.setHeight((short) 400);
            Cell subCell = subRow.createCell(0);
            subCell.setCellValue("Period: " + buildPeriodLabel(period.getMonth(), period.getYear())
                + "   |   Status: " + period.getStatus().name()
                + "   |   Total Employees: " + payslips.size());
            subCell.setCellStyle(subTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

            rowNum++; // blank row

            // ── Column Headers ────────────────────────────────────────────────
            String[] headers = {
                "No", "Employee ID", "Employee Name", "Department",
                "Basic Salary", "Overtime Pay", "Total Earnings",
                "Total Deductions", "Net Salary"
            };

            Row headerRow = sheet.createRow(rowNum++);
            headerRow.setHeight((short) 450);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ── Data Rows ─────────────────────────────────────────────────────
            BigDecimal sumBasic      = BigDecimal.ZERO;
            BigDecimal sumOvertime   = BigDecimal.ZERO;
            BigDecimal sumEarning    = BigDecimal.ZERO;
            BigDecimal sumDeduction  = BigDecimal.ZERO;
            BigDecimal sumNet        = BigDecimal.ZERO;

            int seq = 1;
            for (Payslip p : payslips) {
                Row row = sheet.createRow(rowNum++);
                row.setHeight((short) 380);

                String deptName = p.getEmployee().getDepartment() != null
                    ? p.getEmployee().getDepartment().getDepartmentName() : "-";

                setCell(row, 0, seq++,                                    dataStyle);
                setCell(row, 1, p.getEmployee().getId(),                  dataStyle);
                setCell(row, 2, p.getEmployee().getName(),                dataStyle);
                setCell(row, 3, deptName,                                 dataStyle);
                setCurrencyCell(row, 4, p.getBasicSalary(),               currencyStyle);
                setCurrencyCell(row, 5, p.getOvertimePay(),               currencyStyle);
                setCurrencyCell(row, 6, p.getTotalEarning(),              currencyStyle);
                setCurrencyCell(row, 7, p.getTotalDeduction(),            currencyStyle);
                setCurrencyCell(row, 8, p.getNetSalary(),                 currencyStyle);

                sumBasic     = sumBasic.add(p.getBasicSalary());
                sumOvertime  = sumOvertime.add(p.getOvertimePay());
                sumEarning   = sumEarning.add(p.getTotalEarning());
                sumDeduction = sumDeduction.add(p.getTotalDeduction());
                sumNet       = sumNet.add(p.getNetSalary());
            }

            // ── Total Row ─────────────────────────────────────────────────────
            rowNum++; // blank row sebelum total
            Row totalRow = sheet.createRow(rowNum++);
            totalRow.setHeight((short) 450);

            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TOTAL");
            totalLabelCell.setCellStyle(totalLabelStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 3));

            // Kolom 1-3 merge — isi dengan style kosong
            for (int i = 1; i <= 3; i++) {
                totalRow.createCell(i).setCellStyle(totalLabelStyle);
            }

            setCurrencyCell(totalRow, 4, sumBasic,     totalCurrStyle);
            setCurrencyCell(totalRow, 5, sumOvertime,  totalCurrStyle);
            setCurrencyCell(totalRow, 6, sumEarning,   totalCurrStyle);
            setCurrencyCell(totalRow, 7, sumDeduction, totalCurrStyle);
            setCurrencyCell(totalRow, 8, sumNet,       totalCurrStyle);

            // ── Summary Box ───────────────────────────────────────────────────
            rowNum += 2;
            addSummaryBox(sheet, rowNum, payslips.size(), sumNet, sumOvertime, sumDeduction,
                subTitleStyle, mutedStyle, totalCurrStyle);

            // ── Column Widths ─────────────────────────────────────────────────
            int[] colWidths = {8, 14, 28, 22, 18, 16, 18, 18, 18};
            for (int i = 0; i < colWidths.length; i++) {
                sheet.setColumnWidth(i, colWidths[i] * 256);
            }

            // ── Freeze header rows ────────────────────────────────────────────
            sheet.createFreezePane(0, 4);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (ResourceNotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate Excel report: " + ex.getMessage(), ex);
        }
    }

    // ─── Summary Box ─────────────────────────────────────────────────────────

    private void addSummaryBox(XSSFSheet sheet, int startRow, int totalEmp,
                               BigDecimal sumNet, BigDecimal sumOvertime, BigDecimal sumDeduction,
                               CellStyle labelStyle, CellStyle muted, CellStyle currStyle) {
        String[][] summaryData = {
            {"Total Employees Paid", String.valueOf(totalEmp)},
            {"Total Net Salary",     formatRp(sumNet)},
            {"Total Overtime Pay",   formatRp(sumOvertime)},
            {"Total Deductions",     formatRp(sumDeduction)},
        };

        for (String[] entry : summaryData) {
            Row row = sheet.createRow(startRow++);
            row.setHeight((short) 380);

            Cell lc = row.createCell(0);
            lc.setCellValue(entry[0]);
            lc.setCellStyle(muted);
            sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 2));

            Cell vc = row.createCell(3);
            vc.setCellValue(entry[1]);
            vc.setCellStyle(labelStyle);
            sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 3, 5));
        }
    }

    // ─── Cell Helpers ─────────────────────────────────────────────────────────

    private void setCell(Row row, int col, Object value, CellStyle style) {
        Cell cell = row.createCell(col);
        if (value instanceof String)  cell.setCellValue((String) value);
        else if (value instanceof Long)    cell.setCellValue((Long) value);
        else if (value instanceof Integer) cell.setCellValue((Integer) value);
        cell.setCellStyle(style);
    }

    private void setCurrencyCell(Row row, int col, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value.doubleValue() : 0.0);
        cell.setCellStyle(style);
    }

    // ─── Style Factories ─────────────────────────────────────────────────────

    private CellStyle createTitleStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)37, (byte)99, (byte)235}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
        style.setFont(font);
        return style;
    }

    private CellStyle createSubTitleStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)239, (byte)246, (byte)255}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(new XSSFColor(new byte[]{(byte)37, (byte)99, (byte)235}, null));
        style.setFont(font);
        return style;
    }

    private CellStyle createHeaderStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30, (byte)64, (byte)175}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
        style.setFont(font);
        return style;
    }

    private CellStyle createDataStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }

    private CellStyle createCurrencyStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        // Format angka dengan pemisah ribuan
        DataFormat fmt = wb.createDataFormat();
        style.setDataFormat(fmt.getFormat("#,##0"));
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }

    private CellStyle createTotalLabelStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)37, (byte)99, (byte)235}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
        style.setFont(font);
        return style;
    }

    private CellStyle createTotalCurrencyStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)37, (byte)99, (byte)235}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        DataFormat fmt = wb.createDataFormat();
        style.setDataFormat(fmt.getFormat("#,##0"));
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
        style.setFont(font);
        return style;
    }

    private CellStyle createMutedStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)248, (byte)250, (byte)252}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(new XSSFColor(new byte[]{(byte)100, (byte)116, (byte)139}, null));
        style.setFont(font);
        return style;
    }

    // ─── Util ─────────────────────────────────────────────────────────────────

    private String formatRp(BigDecimal amount) {
        if (amount == null) return "Rp 0";
        return "Rp " + String.format("%,.0f", amount);
    }

    private String buildPeriodLabel(int month, int year) {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;
    }
}
