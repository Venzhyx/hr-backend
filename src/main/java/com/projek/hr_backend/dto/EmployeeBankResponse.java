package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBankResponse {
    private Long id;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
}
