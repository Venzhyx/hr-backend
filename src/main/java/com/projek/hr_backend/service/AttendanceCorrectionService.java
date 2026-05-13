package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.AttendanceCorrectionRequest;
import com.projek.hr_backend.dto.AttendanceCorrectionResponse;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.*;
import com.projek.hr_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceCorrectionService {

    private final AttendanceCorrectionRepository correctionRepository;
    private final AttendanceCorrectionApprovalRepository approvalRepository;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final ApprovalApproverRepository approverRepository;

    @Transactional
    public AttendanceCorrectionResponse createCorrection(AttendanceCorrectionRequest request) {
        validateRequest(request);

        if (request.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        attendanceRepository.findByEmployeeIdAndDate(request.getEmployeeId(), request.getDate())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance not found for date: " + request.getDate()));

        AttendanceCorrection correction = new AttendanceCorrection();
        correction.setEmployee(employee);
        correction.setDate(request.getDate());
        correction.setNewCheckIn(request.getNewCheckIn());
        correction.setNewCheckOut(request.getNewCheckOut());
        correction.setType(request.getType().toUpperCase());
        correction.setDescription(request.getDescription());
        correction.setStatus("PENDING");

        AttendanceCorrection saved = correctionRepository.save(correction);

        createApprovalRecords(saved);

        System.out.println("[CORRECTION CREATED] Employee: " + request.getEmployeeId() + " Date: " + request.getDate());

        return mapToResponse(saved);
    }

    private void createApprovalRecords(AttendanceCorrection correction) {
        List<ApprovalApprover> approvers = approverRepository.findAllByOrderByApprovalOrderAsc();

        for (int i = 0; i < approvers.size(); i++) {
            AttendanceCorrectionApproval approval = new AttendanceCorrectionApproval();
            approval.setCorrection(correction);
            approval.setApproverId(approvers.get(i).getEmployee().getId());
            approval.setSequence(i + 1);
            approval.setStatus(ApprovalStatus.PENDING);
            approvalRepository.save(approval);
        }
    }

    public List<AttendanceCorrectionResponse> getAllCorrections() {
        return correctionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AttendanceCorrectionResponse> getCorrectionsByEmployee(Long employeeId) {
        return correctionRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceCorrectionResponse approveCorrection(Long correctionId, Long approverId, String notes) {
        AttendanceCorrection correction = correctionRepository.findById(correctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Correction not found"));

        if (!correction.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Correction sudah diproses");
        }

        List<AttendanceCorrectionApproval> approvals =
                approvalRepository.findByCorrectionIdOrderBySequenceAsc(correctionId);

        // Take first PENDING approval (DEMO MODE)
        AttendanceCorrectionApproval myApproval = approvals.stream()
                .filter(a -> a.getStatus().equals(ApprovalStatus.PENDING))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Semua approval sudah selesai"));

        // Save notes
        if (notes != null && !notes.isEmpty()) {
            myApproval.setNotes(notes);
        }
        
        myApproval.setStatus(ApprovalStatus.APPROVED);
        myApproval.setApprovedAt(LocalDateTime.now());
        approvalRepository.save(myApproval);

        System.out.println("[APPROVED] Correction: " + correctionId + " Sequence: " + myApproval.getSequence());

        // Check if ALL approvals are now APPROVED
        List<AttendanceCorrectionApproval> updatedApprovals = 
                approvalRepository.findByCorrectionIdOrderBySequenceAsc(correctionId);
                
        boolean allApproved = updatedApprovals.stream()
                .allMatch(a -> a.getStatus().equals(ApprovalStatus.APPROVED));

        if (allApproved) {
            // Update attendance
            Attendance attendance = attendanceRepository
                    .findByEmployeeIdAndDate(correction.getEmployee().getId(), correction.getDate())
                    .orElseThrow(() -> new ResourceNotFoundException("Attendance tidak ditemukan"));

            String type = correction.getType();
            if (type.equals("CHECKIN") || type.equals("BOTH")) {
                attendance.setCheckIn(correction.getNewCheckIn());
            }
            if (type.equals("CHECKOUT") || type.equals("BOTH")) {
                attendance.setCheckOut(correction.getNewCheckOut());
            }

            attendance.setStatus(determineStatus(attendance));
            attendanceRepository.save(attendance);

            correction.setStatus("APPROVED");
            correctionRepository.save(correction);

            System.out.println("[FINAL APPROVED] Correction: " + correctionId);
        }

        return mapToResponse(correction);
    }

    @Transactional
    public AttendanceCorrectionResponse rejectCorrection(Long correctionId, Long approverId, String notes) {
        AttendanceCorrection correction = correctionRepository.findById(correctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Correction not found"));

        if (!correction.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Correction sudah diproses");
        }

        List<AttendanceCorrectionApproval> approvals =
                approvalRepository.findByCorrectionIdOrderBySequenceAsc(correctionId);

        // Take first PENDING approval (DEMO MODE)
        AttendanceCorrectionApproval myApproval = approvals.stream()
                .filter(a -> a.getStatus().equals(ApprovalStatus.PENDING))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Semua approval sudah selesai"));

        // Save notes (required)
        if (notes == null || notes.isEmpty()) {
            throw new IllegalArgumentException("Alasan penolakan wajib diisi");
        }
        
        myApproval.setNotes(notes);
        myApproval.setStatus(ApprovalStatus.REJECTED);
        myApproval.setApprovedAt(LocalDateTime.now());
        approvalRepository.save(myApproval);

        correction.setStatus("REJECTED");
        correctionRepository.save(correction);

        System.out.println("[REJECTED] Correction: " + correctionId);

        return mapToResponse(correction);
    }

    // ✅ TAMBAH: Update Correction (hanya untuk status yang belum diproses)
    @Transactional
    public AttendanceCorrectionResponse updateCorrection(Long id, AttendanceCorrectionRequest request) {
        AttendanceCorrection correction = correctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Correction not found"));

        // Validasi: hanya bisa update jika status masih PENDING (belum ada approval)
        // Atau bisa juga hanya untuk status yang belum diapprove sama sekali
        if (!correction.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Cannot update correction that is already " + correction.getStatus());
        }

        // Cek apakah sudah ada approval yang APPROVED
        List<AttendanceCorrectionApproval> approvals = 
                approvalRepository.findByCorrectionIdOrderBySequenceAsc(id);
        boolean hasApproved = approvals.stream()
                .anyMatch(a -> a.getStatus().equals(ApprovalStatus.APPROVED));
        
        if (hasApproved) {
            throw new IllegalStateException("Cannot update correction that already has an approved approval");
        }

        // Update fields
        validateRequest(request);
        
        if (request.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }

        correction.setDate(request.getDate());
        correction.setNewCheckIn(request.getNewCheckIn());
        correction.setNewCheckOut(request.getNewCheckOut());
        correction.setType(request.getType().toUpperCase());
        correction.setDescription(request.getDescription());
        
        AttendanceCorrection updated = correctionRepository.save(correction);
        
        System.out.println("[CORRECTION UPDATED] ID: " + id);
        
        return mapToResponse(updated);
    }

    // ✅ TAMBAH: Delete Correction (hanya untuk status yang belum diproses)
    @Transactional
    public void deleteCorrection(Long id) {
        AttendanceCorrection correction = correctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Correction not found"));

        // Validasi: hanya bisa delete jika status masih PENDING
        if (!correction.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Cannot delete correction that is already " + correction.getStatus());
        }

        // Cek apakah sudah ada approval yang APPROVED
        List<AttendanceCorrectionApproval> approvals = 
                approvalRepository.findByCorrectionIdOrderBySequenceAsc(id);
        boolean hasApproved = approvals.stream()
                .anyMatch(a -> a.getStatus().equals(ApprovalStatus.APPROVED));
        
        if (hasApproved) {
            throw new IllegalStateException("Cannot delete correction that already has an approved approval");
        }

        // Delete approvals first (due to foreign key constraint)
        approvalRepository.deleteAll(approvals);
        
        // Delete correction
        correctionRepository.delete(correction);
        
        System.out.println("[CORRECTION DELETED] ID: " + id);
    }

    private String determineStatus(Attendance attendance) {
        DayOfWeek day = attendance.getDate().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) return "OFF";
        if (attendance.getCheckIn() == null) return "ABSENT";
        if (attendance.getCheckIn().toLocalTime().isAfter(LocalTime.of(8, 0))) return "LATE";
        return "PRESENT";
    }

    private void validateRequest(AttendanceCorrectionRequest request) {
        String type = request.getType().toUpperCase();
        if (type.equals("CHECKIN") && request.getNewCheckIn() == null) {
            throw new IllegalArgumentException("newCheckIn is required for type CHECKIN");
        }
        if (type.equals("CHECKOUT") && request.getNewCheckOut() == null) {
            throw new IllegalArgumentException("newCheckOut is required for type CHECKOUT");
        }
        if (type.equals("BOTH") && (request.getNewCheckIn() == null || request.getNewCheckOut() == null)) {
            throw new IllegalArgumentException("newCheckIn and newCheckOut are required for type BOTH");
        }
    }

    private AttendanceCorrectionResponse mapToResponse(AttendanceCorrection correction) {
        AttendanceCorrectionResponse response = new AttendanceCorrectionResponse();
        response.setId(correction.getId());
        response.setEmployeeId(correction.getEmployee().getId());
        response.setEmployeeName(correction.getEmployee().getName());
        response.setDate(correction.getDate());
        response.setNewCheckIn(correction.getNewCheckIn());
        response.setNewCheckOut(correction.getNewCheckOut());
        response.setType(correction.getType());
        response.setDescription(correction.getDescription());
        response.setStatus(correction.getStatus());
        response.setCreatedAt(correction.getCreatedAt());
        response.setUpdatedAt(correction.getUpdatedAt());

        List<AttendanceCorrectionApproval> approvals =
                approvalRepository.findByCorrectionIdOrderBySequenceAsc(correction.getId());

        response.setApprovals(approvals.stream()
                .map(a -> {
                    String approverName = employeeRepository.findById(a.getApproverId())
                            .map(Employee::getName)
                            .orElse("Unknown Approver");
                    
                    return new AttendanceCorrectionResponse.ApprovalDetail(
                            a.getId(),
                            a.getApproverId(),
                            approverName,
                            a.getSequence(),
                            a.getStatus(),
                            a.getNotes(),
                            a.getApprovedAt()
                    );
                })
                .collect(Collectors.toList()));

        return response;
    }
}