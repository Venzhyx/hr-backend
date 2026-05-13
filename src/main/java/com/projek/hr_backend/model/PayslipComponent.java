package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
    name = "payslip_components",
    indexes = {
        @Index(name = "idx_payslip_component_payslip_id", columnList = "payslip_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayslipComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payslip_id", nullable = false)
    private Payslip payslip;

    @Column(name = "component_name", nullable = false)
    private String componentName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayslipComponentType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
}
