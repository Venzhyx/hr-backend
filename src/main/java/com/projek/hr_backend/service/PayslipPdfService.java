package com.projek.hr_backend.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.projek.hr_backend.exception.BadRequestException;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.*;
import com.projek.hr_backend.repository.PayslipComponentRepository;
import com.projek.hr_backend.repository.PayslipRepository;
import com.projek.hr_backend.repository.PayrollPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Generates a formal, government-document-style payslip PDF.
 *
 * Layout reference: PPh 21 form (PERHITUNGAN PPH 21)
 *  - Centered italic+bold title in a bordered box
 *  - Identity block (NIK / Nama / Jabatan / Departemen)
 *  - Single full-width table:
 *      • column header row       → dark-green bg, white bold
 *      • section rows (A/B/C)    → dark-green bg, white bold, 2-col span
 *      • data rows               → alternating white / light-gray
 *      • subtotal rows           → medium-green bg, white bold
 *      • net-salary row          → dark-green bg, white bold, larger font
 *  - Signature area + printed note
 */
@Service
@RequiredArgsConstructor
public class PayslipPdfService {

    private final PayslipRepository          payslipRepository;
    private final PayslipComponentRepository payslipComponentRepository;
    private final PayrollPeriodRepository    payrollPeriodRepository;

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color C_DARK_GREEN  = new Color(56,  87,  35);
    private static final Color C_MED_GREEN   = new Color(84, 130,  53);
    private static final Color C_TITLE_GREEN = new Color(0,   97,   0);
    private static final Color C_WHITE       = Color.WHITE;
    private static final Color C_LIGHT_GRAY  = new Color(242, 242, 242);
    private static final Color C_BORDER      = new Color(180, 180, 180);
    private static final Color C_BLACK       = Color.BLACK;
    private static final Color C_MUTED       = new Color(130, 130, 130);

    private static final NumberFormat IDR =
            NumberFormat.getNumberInstance(new Locale("id", "ID"));

    // ── Font helpers ──────────────────────────────────────────────────────────
    private Font fBold (int sz, Color c) { return new Font(Font.HELVETICA, sz, Font.BOLD,        c); }
    private Font fNorm (int sz, Color c) { return new Font(Font.HELVETICA, sz, Font.NORMAL,      c); }
    private Font fBoldI(int sz, Color c) { return new Font(Font.HELVETICA, sz, Font.BOLDITALIC,  c); }

    // ── Public API ────────────────────────────────────────────────────────────

    /** PDF untuk satu payslip. */
    @Transactional(readOnly = true)
    public byte[] generatePayslipPdf(Long payslipId) {
        Payslip payslip = payslipRepository.findByIdForExport(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payslip not found with id: " + payslipId));

        List<PayslipComponent> components =
                payslipComponentRepository.findByPayslipId(payslipId);

        return buildPdf(payslip, components);
    }

    /**
     * PDF semua payslip dalam satu periode — satu file multi-halaman.
     * Setiap karyawan mendapat 1 halaman penuh.
     */
    @Transactional(readOnly = true)
    public byte[] generateAllPayslipsPdf(int month, int year) {
        PayrollPeriod period = payrollPeriodRepository.findByMonthAndYear(month, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payroll period not found for " + buildPeriodLabel(month, year)));

        List<Payslip> payslips = payslipRepository.findByPayrollPeriodIdForExport(period.getId());

        if (payslips.isEmpty()) {
            throw new BadRequestException(
                    "No payslip data found for period " + buildPeriodLabel(month, year));
        }

        return buildAllPayslipsPdf(payslips);
    }

    // ── PDF Builder — all payslips in one period ──────────────────────────────

    private byte[] buildAllPayslipsPdf(List<Payslip> payslips) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 40, 40);
        PdfWriter.getInstance(doc, out);
        doc.open();

        for (int i = 0; i < payslips.size(); i++) {
            Payslip payslip = payslips.get(i);
            List<PayslipComponent> components =
                    payslipComponentRepository.findByPayslipId(payslip.getId());

            Employee      emp    = payslip.getEmployee();
            Department    dept   = emp.getDepartment();
            Company       co     = emp.getCompany();
            PayrollPeriod period = payslip.getPayrollPeriod();

            String coName      = co != null ? co.getCompanyName().toUpperCase() : "HR MANAGEMENT SYSTEM";
            String periodLabel = buildPeriodLabel(period.getMonth(), period.getYear()).toUpperCase();

            // Halaman baru untuk setiap karyawan (kecuali yang pertama)
            if (i > 0) {
                doc.newPage();
            }

            addTitleBlock(doc, coName, periodLabel);
            addSpacer(doc, 6);
            addIdentityBlock(doc, emp, dept);
            addSpacer(doc, 8);
            addMainTable(doc, payslip, components);
            addSpacer(doc, 12);
            addFooter(doc, payslip, period);
        }

        doc.close();
        return out.toByteArray();
    }

    // ── PDF Builder — single payslip ─────────────────────────────────────────

    private byte[] buildPdf(Payslip payslip, List<PayslipComponent> components) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 40, 40);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Employee      emp    = payslip.getEmployee();
        Department    dept   = emp.getDepartment();
        Company       co     = emp.getCompany();
        PayrollPeriod period = payslip.getPayrollPeriod();

        String coName      = co != null ? co.getCompanyName().toUpperCase() : "HR MANAGEMENT SYSTEM";
        String periodLabel = buildPeriodLabel(period.getMonth(), period.getYear()).toUpperCase();

        // ① Title
        addTitleBlock(doc, coName, periodLabel);
        addSpacer(doc, 6);

        // ② Identity
        addIdentityBlock(doc, emp, dept);
        addSpacer(doc, 8);

        // ③ Main table
        addMainTable(doc, payslip, components);
        addSpacer(doc, 12);

        // ④ Signature + footer
        addFooter(doc, payslip, period);

        doc.close();
        return out.toByteArray();
    }

    // ── ① Title Block ─────────────────────────────────────────────────────────

    private void addTitleBlock(Document doc, String coName, String periodLabel) {
        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(C_BORDER);
        cell.setBorderWidth(1.2f);
        cell.setPaddingTop(10);
        cell.setPaddingBottom(10);

        Paragraph line1 = new Paragraph("PERHITUNGAN PAYSLIP " + periodLabel, fBoldI(13, C_TITLE_GREEN));
        line1.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(line1);

        Paragraph line2 = new Paragraph(coName, fBold(10, C_BLACK));
        line2.setAlignment(Element.ALIGN_CENTER);
        line2.setSpacingBefore(3);
        cell.addElement(line2);

        box.addCell(cell);
        doc.add(box);
    }

    // ── ② Identity Block ──────────────────────────────────────────────────────

    private void addIdentityBlock(Document doc, Employee emp, Department dept) {
        PdfPTable t = new PdfPTable(new float[]{2.2f, 0.3f, 3.5f});
        t.setWidthPercentage(58);
        t.setHorizontalAlignment(Element.ALIGN_LEFT);

        addIdRow(t, "NIK",            emp.getId() != null ? String.valueOf(emp.getId()) : "-");
        addIdRow(t, "Nama Karyawan",  emp.getName());
        addIdRow(t, "Jabatan",        emp.getJobTitle());
        addIdRow(t, "Departemen",     dept != null ? dept.getDepartmentName() : "-");
        addIdRow(t, "Email Kerja",    emp.getWorkEmail());
        addIdRow(t, "No. Telepon",    emp.getWorkPhone());

        doc.add(t);
    }

    private void addIdRow(PdfPTable t, String label, String value) {
        t.addCell(noBorder(new Phrase(label, fNorm(9, C_BLACK))));
        t.addCell(noBorder(new Phrase(":", fNorm(9, C_BLACK))));
        t.addCell(noBorder(new Phrase(value != null ? value : "-", fNorm(9, C_BLACK))));
    }

    // ── ③ Main Table ──────────────────────────────────────────────────────────

    private void addMainTable(Document doc, Payslip payslip,
                               List<PayslipComponent> components) {
        List<PayslipComponent> earnings =
                components.stream()
                        .filter(c -> c.getType() == PayslipComponentType.EARNING)
                        .collect(Collectors.toList());
        List<PayslipComponent> deductions =
                components.stream()
                        .filter(c -> c.getType() == PayslipComponentType.DEDUCTION)
                        .collect(Collectors.toList());

        PdfPTable t = new PdfPTable(new float[]{4.5f, 2f});
        t.setWidthPercentage(100);

        // Column headers
        t.addCell(colHeader("Keterangan", Element.ALIGN_LEFT));
        t.addCell(colHeader("Penghasilan / Potongan", Element.ALIGN_CENTER));

        // ─── A. PENGHASILAN ───────────────────────────────────────────────
        t.addCell(sectionCell("A.  PENGHASILAN"));
        t.addCell(sectionCell(""));

        int r = 0;
        t.addCell(dataL("     Gaji / Upah Pokok", r));
        t.addCell(dataR(rp(payslip.getBasicSalary()), r++));

        t.addCell(dataL("     Lembur / Overtime", r));
        t.addCell(dataR(rp(payslip.getOvertimePay()), r++));

        for (PayslipComponent c : earnings) {
            t.addCell(dataL("     " + c.getComponentName(), r));
            t.addCell(dataR(rp(c.getAmount()), r++));
        }

        t.addCell(subtotalL("     Jumlah Penghasilan Bruto"));
        t.addCell(subtotalR(rp(payslip.getTotalEarning())));

        // ─── B. POTONGAN ──────────────────────────────────────────────────
        t.addCell(sectionCell("B.  POTONGAN"));
        t.addCell(sectionCell(""));

        r = 0;
        if (deductions.isEmpty()) {
            t.addCell(dataL("     -", r));
            t.addCell(dataR("-", r++));
        } else {
            for (PayslipComponent c : deductions) {
                t.addCell(dataL("     " + c.getComponentName(), r));
                t.addCell(dataR(rp(c.getAmount()), r++));
            }
        }

        t.addCell(subtotalL("     Jumlah Potongan"));
        t.addCell(subtotalR(rp(payslip.getTotalDeduction())));

        // ─── C. KEHADIRAN ─────────────────────────────────────────────────
        t.addCell(sectionCell("C.  KEHADIRAN"));
        t.addCell(sectionCell(""));

        t.addCell(dataL("     Hari Tidak Masuk (Absent)", 0));
        t.addCell(dataR(payslip.getTotalAbsent() + " hari", 0));

        t.addCell(dataL("     Keterlambatan (Late)", 1));
        t.addCell(dataR(payslip.getTotalLate() + " kali", 1));

        t.addCell(dataL("     Jam Lembur (Overtime)", 0));
        t.addCell(dataR(String.format("%.1f jam", payslip.getTotalOvertimeHours()), 0));

        // ─── GAJI BERSIH ──────────────────────────────────────────────────
        t.addCell(netCell("GAJI BERSIH  (TAKE HOME PAY)", Element.ALIGN_LEFT));
        t.addCell(netCell(rp(payslip.getNetSalary()), Element.ALIGN_RIGHT));

        doc.add(t);
    }

    // ── Cell Factories ────────────────────────────────────────────────────────

    private PdfPCell colHeader(String text, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, fBold(9, C_WHITE)));
        c.setBackgroundColor(C_DARK_GREEN);
        c.setPadding(6); c.setPaddingLeft(7);
        c.setBorderColor(C_BORDER);
        c.setHorizontalAlignment(align);
        return c;
    }

    private PdfPCell sectionCell(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, fBold(9, C_WHITE)));
        c.setBackgroundColor(C_DARK_GREEN);
        c.setPadding(5); c.setPaddingLeft(7);
        c.setBorderColor(C_BORDER);
        return c;
    }

    private PdfPCell dataL(String text, int row) {
        PdfPCell c = new PdfPCell(new Phrase(text, fNorm(9, C_BLACK)));
        c.setBackgroundColor(row % 2 == 0 ? C_WHITE : C_LIGHT_GRAY);
        c.setPadding(4); c.setPaddingLeft(7);
        c.setBorderColor(C_BORDER);
        return c;
    }

    private PdfPCell dataR(String text, int row) {
        PdfPCell c = new PdfPCell(new Phrase(text, fNorm(9, C_BLACK)));
        c.setBackgroundColor(row % 2 == 0 ? C_WHITE : C_LIGHT_GRAY);
        c.setPadding(4);
        c.setBorderColor(C_BORDER);
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return c;
    }

    private PdfPCell subtotalL(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, fBold(9, C_WHITE)));
        c.setBackgroundColor(C_MED_GREEN);
        c.setPadding(5); c.setPaddingLeft(7);
        c.setBorderColor(C_BORDER);
        return c;
    }

    private PdfPCell subtotalR(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, fBold(9, C_WHITE)));
        c.setBackgroundColor(C_MED_GREEN);
        c.setPadding(5);
        c.setBorderColor(C_BORDER);
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return c;
    }

    private PdfPCell netCell(String text, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, fBold(10, C_WHITE)));
        c.setBackgroundColor(C_DARK_GREEN);
        c.setPadding(8); c.setPaddingLeft(7);
        c.setBorderColor(C_BORDER);
        c.setHorizontalAlignment(align);
        return c;
    }

    private PdfPCell noBorder(Phrase phrase) {
        PdfPCell c = new PdfPCell(phrase);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPaddingTop(2); c.setPaddingBottom(2);
        return c;
    }

    // ── ④ Footer / Signature ──────────────────────────────────────────────────

    private void addFooter(Document doc, Payslip payslip, PayrollPeriod period) {
        String generatedAt = payslip.getGeneratedAt() != null
                ? payslip.getGeneratedAt()
                        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm",
                                new Locale("id", "ID")))
                : "-";

        // Signature row
        PdfPTable sig = new PdfPTable(new float[]{1f, 1f, 1f});
        sig.setWidthPercentage(100);

        sig.addCell(sigCell("Diterima oleh,", "Karyawan", payslip.getEmployee().getName()));
        sig.addCell(sigCell("Mengetahui,", "Atasan Langsung", ""));
        sig.addCell(sigCell("Disetujui oleh,", "HRD / Payroll", ""));

        doc.add(sig);

        // Printed-on note
        Paragraph note = new Paragraph(
                "Dokumen ini dicetak secara otomatis oleh sistem pada " + generatedAt
                + "  |  Status Payroll: " + period.getStatus().name(),
                fNorm(7, C_MUTED));
        note.setAlignment(Element.ALIGN_CENTER);
        note.setSpacingBefore(8);
        doc.add(note);
    }

    private PdfPCell sigCell(String role, String unit, String name) {
        PdfPCell c = new PdfPCell();
        c.setBorderColor(C_BORDER);
        c.setPadding(8);
        c.setMinimumHeight(72);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph top = new Paragraph(role, fNorm(8, C_BLACK));
        top.setAlignment(Element.ALIGN_CENTER);
        c.addElement(top);

        Paragraph sub = new Paragraph(unit, fBold(8, C_BLACK));
        sub.setAlignment(Element.ALIGN_CENTER);
        c.addElement(sub);

        // Spacer for signature space
        c.addElement(new Paragraph("\n\n", fNorm(8, C_WHITE)));

        String label = name.isEmpty() ? "(__________________________)" : name;
        Paragraph nm = new Paragraph(label, name.isEmpty() ? fNorm(8, C_BLACK) : fBold(8, C_BLACK));
        nm.setAlignment(Element.ALIGN_CENTER);
        c.addElement(nm);

        return c;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void addSpacer(Document doc, float height) {
        Paragraph sp = new Paragraph(" ");
        sp.setSpacingAfter(height);
        doc.add(sp);
    }

    private String rp(java.math.BigDecimal v) {
        if (v == null) return "-";
        return IDR.format(v) + ",00";
    }

    private String buildPeriodLabel(int month, int year) {
        return java.time.Month.of(month)
                .getDisplayName(java.time.format.TextStyle.FULL, Locale.forLanguageTag("id"))
                + " " + year;
    }
}
