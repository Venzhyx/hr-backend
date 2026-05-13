# HR Management System (HRIS) Backend - Complete Version

Backend system lengkap untuk HR Management System menggunakan Spring Boot, PostgreSQL, dan REST API dengan fitur employee management yang komprehensif.

## Tech Stack
- Spring Boot 4.0.3
- Maven
- PostgreSQL
- Spring Data JPA (Hibernate)
- Lombok
- Jakarta Validation

## Database Configuration

Buat database PostgreSQL dengan nama `hris_db`:
```sql
CREATE DATABASE hris_db;
```

Konfigurasi database ada di `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hris_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
```

## Cara Menjalankan

1. Pastikan PostgreSQL sudah running
2. Buat database `hris_db`
3. Update password di `application.properties`
4. Jalankan aplikasi:
```bash
mvnw.cmd spring-boot:run
```

5. Akses UI di browser:
```
http://localhost:8080
```

## Fitur Lengkap

### 1. Employee Management (Full CRUD)

#### Employee Basic Info
- Employee Code (unique)
- Name
- Job Title
- Job Position
- Work Email, Phone, Mobile
- Employee Type (FULL_TIME, PART_TIME, CONTRACT)
- Status (ACTIVE, RESIGNED, TERMINATED)
- Join Date
- Photo URL
- Department, Manager, Coach

#### Private Contact
- Private Address
- Private Email
- Private Phone
- Bank Account (Bank Name, Account Number)
- Home to Work Distance (km)
- BPJS ID

#### Citizenship
- Nationality
- Identification Number (KTP)
- Passport Number
- Gender (MALE, FEMALE)
- Date of Birth
- Place of Birth
- Country of Birth

#### Emergency Contact
- Contact Name
- Contact Phone

#### Education
- Certificate Level (HIGH_SCHOOL, DIPLOMA, BACHELOR, MASTER, DOCTORATE)
- Field of Study
- School

#### Family Status
- Marital Status (SINGLE, MARRIED, DIVORCED, WIDOWED)
- Number of Dependent Children

### 2. Department Management
- Department Name
- Manager
- Employee Count per Department

### 3. Attendance Management
- Check In / Check Out
- Attendance History
- Status Tracking

### 4. Dashboard
- Total Employees Statistics
- Employee Type Breakdown
- Monthly Join Statistics

## API Endpoints

### Employee Complete API

#### Create Complete Employee
```
POST /api/employees-complete
Content-Type: application/json

{
  "employeeCode": "EMP001",
  "name": "John Doe",
  "jobTitle": "Software Engineer",
  "jobPosition": "Senior Developer",
  "workEmail": "john.doe@company.com",
  "workPhone": "021-1234567",
  "workMobile": "081234567890",
  "employeeType": "FULL_TIME",
  "status": "ACTIVE",
  "joinDate": "2024-01-15",
  "photo": "https://example.com/photo.jpg",
  "departmentId": 1,
  "managerId": 2,
  "coachId": 3,
  
  "privateAddress": "Jl. Sudirman No. 123, Jakarta",
  "privateEmail": "john.personal@gmail.com",
  "privatePhone": "081234567890",
  "bankName": "Bank Mandiri",
  "accountNumber": "1234567890",
  "homeToWorkDistance": 15.5,
  "bpjsId": "BPJS123456",
  
  "nationality": "Indonesia",
  "identificationNumber": "3201234567890123",
  "passportNumber": "A1234567",
  "gender": "MALE",
  "dateOfBirth": "1990-01-15",
  "placeOfBirth": "Jakarta",
  "countryOfBirth": "Indonesia",
  
  "emergencyContactName": "Jane Doe",
  "emergencyContactPhone": "081987654321",
  
  "certificateLevel": "BACHELOR",
  "fieldOfStudy": "Computer Science",
  "school": "University of Indonesia",
  
  "maritalStatus": "MARRIED",
  "numberOfDependentChildren": 2
}
```

#### Get All Complete Employees
```
GET /api/employees-complete
```

#### Get Complete Employee by ID
```
GET /api/employees-complete/{id}
```

#### Update Complete Employee
```
PUT /api/employees-complete/{id}
```

#### Delete Complete Employee
```
DELETE /api/employees-complete/{id}
```

### Department API
```
POST   /api/departments
GET    /api/departments
GET    /api/departments/{id}
PUT    /api/departments/{id}
DELETE /api/departments/{id}
GET    /api/departments/{id}/employee-count
```

### Attendance API
```
POST /api/attendances/checkin
POST /api/attendances/checkout
GET  /api/attendances/employee/{employeeId}
GET  /api/attendances
```

### Dashboard API
```
GET /api/dashboard/summary
```

## UI Features

### Employee List
- Tampilan foto employee
- Informasi lengkap (Name, ID, Role, Join Date, Department, Status)
- Actions: Edit & Delete

### Create/Edit Employee Form
Form dengan 6 tabs:
1. **Employee Info** - Data dasar employee
2. **Private Contact** - Kontak pribadi dan bank
3. **Citizenship** - Data kewarganegaraan
4. **Emergency** - Kontak darurat
5. **Education** - Riwayat pendidikan
6. **Family Status** - Status keluarga

### Dashboard
- Statistics cards
- Monthly join statistics
- Real-time data

### Departments
- List departments
- Employee count per department
- CRUD operations

### Attendance
- Check in/out interface
- Today's attendance list
- Employee selection dropdown

## Database Schema

### Tables
- `employees` - Employee basic info
- `departments` - Department info
- `employee_private_info` - Private contact info
- `employee_citizenship` - Citizenship data
- `employee_emergency` - Emergency contact
- `employee_education` - Education history
- `employee_family_status` - Family status
- `attendances` - Attendance records

### Relationships
- Employee → Department (ManyToOne)
- Employee → Manager (Self-reference ManyToOne)
- Employee → Coach (Self-reference ManyToOne)
- Employee → PrivateInfo (OneToOne)
- Employee → Citizenship (OneToOne)
- Employee → Emergency (OneToOne)
- Employee → Education (OneToOne)
- Employee → FamilyStatus (OneToOne)
- Employee → Attendance (OneToMany)

## Project Structure

```
src/main/java/com/projek/hr_backend/
├── controller/
│   ├── EmployeeCompleteController.java
│   ├── EmployeeController.java
│   ├── DepartmentController.java
│   ├── AttendanceController.java
│   └── DashboardController.java
├── service/
│   ├── EmployeeCompleteService.java
│   ├── EmployeeService.java
│   ├── DepartmentService.java
│   ├── AttendanceService.java
│   └── DashboardService.java
├── repository/
│   ├── EmployeeRepository.java
│   ├── DepartmentRepository.java
│   ├── EmployeePrivateInfoRepository.java
│   ├── EmployeeCitizenshipRepository.java
│   ├── EmployeeEmergencyRepository.java
│   ├── EmployeeEducationRepository.java
│   ├── EmployeeFamilyStatusRepository.java
│   └── AttendanceRepository.java
├── model/
│   ├── Employee.java
│   ├── Department.java
│   ├── EmployeePrivateInfo.java
│   ├── EmployeeCitizenship.java
│   ├── EmployeeEmergency.java
│   ├── EmployeeEducation.java
│   ├── EmployeeFamilyStatus.java
│   ├── Attendance.java
│   └── [Enums]
├── dto/
│   ├── EmployeeCompleteRequest.java
│   ├── EmployeeCompleteResponse.java
│   └── [Other DTOs]
└── exception/
    ├── GlobalExceptionHandler.java
    └── [Custom Exceptions]

src/main/resources/static/
├── index.html
├── css/
│   └── style.css
└── js/
    └── app.js
```

## Notes

- Semua endpoint terbuka (tidak ada authentication untuk development)
- Database schema akan dibuat otomatis oleh Hibernate (ddl-auto=update)
- UI menggunakan vanilla JavaScript (no framework)
- Form multi-tab untuk input employee yang lengkap
- Support foto employee dengan URL
- Dropdown untuk bank, nationality, dll
- Responsive design

## Testing

1. Buat Department terlebih dahulu
2. Tambah Employee dengan semua data lengkap
3. Test Check In/Out
4. Lihat Dashboard untuk statistik

## Future Enhancements

- File upload untuk foto employee
- Authentication & Authorization
- Role-based access control
- Export to Excel/PDF
- Advanced reporting
- Email notifications
- Leave management
- Payroll integration
