const API = 'http://localhost:8080/api';

// Tab Management
function showTab(tabName) {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
    document.getElementById(tabName).classList.add('active');
    
    const clickedTab = document.querySelector(`[data-tab="${tabName}"]`);
    if (clickedTab) clickedTab.classList.add('active');
    
    if (tabName === 'dashboard') loadDashboard();
    if (tabName === 'employees') loadEmployees();
    if (tabName === 'departments') loadDepartments();
    if (tabName === 'attendance') {
        loadAttendances();
        loadEmployeesForSelect();
    }
}

// Notification
function showNotification(message, isError = false) {
    const notif = document.getElementById('notification');
    notif.textContent = message;
    notif.className = 'notification show' + (isError ? ' error' : '');
    setTimeout(() => notif.classList.remove('show'), 3000);
}

// Modal
function showModal(id) {
    document.getElementById(id).classList.add('show');
}

function closeModal(id) {
    document.getElementById(id).classList.remove('show');
}

// Dashboard
async function loadDashboard() {
    try {
        const res = await fetch(`${API}/dashboard/summary`);
        const data = await res.json();
        if (data.success) {
            const stats = document.getElementById('dashboard-stats');
            stats.innerHTML = `
                <div class="stat-card"><h3>${data.data.totalEmployee}</h3><p>Total Employees</p></div>
                <div class="stat-card"><h3>${data.data.totalFullTime}</h3><p>Full Time</p></div>
                <div class="stat-card"><h3>${data.data.totalPartTime}</h3><p>Part Time</p></div>
                <div class="stat-card"><h3>${data.data.totalContract}</h3><p>Contract</p></div>
            `;
        }
    } catch (error) {
        showNotification('Error loading dashboard', true);
    }
}

// Employees
async function loadEmployees() {
    const list = document.getElementById('employee-list');
    if (!list) return; // Skip if element not found
    
    try {
        const res = await fetch(`${API}/employees-complete`);
        const data = await res.json();
        
        if (data.success && data.data.length > 0) {
            list.innerHTML = data.data.map(emp => `
                <div class="data-item">
                    <div class="data-item-info">
                        <strong>${emp.name}</strong> (${emp.employeeCode})<br>
                        <small>${emp.jobTitle || '-'} | ${emp.departmentName || '-'} | 
                        <span class="badge badge-${emp.status === 'ACTIVE' ? 'success' : 'warning'}">${emp.status}</span></small>
                    </div>
                    <div class="data-item-actions">
                        <button class="btn btn-primary btn-sm" onclick="viewEmployee(${emp.id})">View</button>
                        <button class="btn btn-warning btn-sm" onclick="editEmployee(${emp.id})">Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteEmployee(${emp.id})">Delete</button>
                    </div>
                </div>
            `).join('');
        } else {
            list.innerHTML = '<p>No employees found</p>';
        }
    } catch (error) {
        showNotification('Error loading employees', true);
    }
}

function showEmployeeForm() {
    document.getElementById('employee-form').reset();
    document.getElementById('emp-id').value = '';
    document.getElementById('employee-form-title').textContent = 'Add Employee';
    loadDepartmentsForSelect();
    loadEmployeesForManagerCoach();
    loadEmployeesForRelatedUser();
    showModal('employee-modal');
}

async function saveEmployee(e) {
    e.preventDefault();
    
    try {
        const id = document.getElementById('emp-id').value;
        
        // Get existing data if editing
        let existingData = {};
        if (id) {
            const res = await fetch(`${API}/employees-complete/${id}`);
            const data = await res.json();
            if (data.success) existingData = data.data;
        }
        
        // Upload photo
        let photoUrl = existingData.photo || '';
        const photoFile = document.getElementById('emp-photo').files[0];
        if (photoFile) {
            if (existingData.photo) await deleteFile(existingData.photo);
            const formData = new FormData();
            formData.append('file', photoFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('Photo upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) photoUrl = uploadData.data;
        }
        
        // Upload contract
        let contractUrl = existingData.contractDocument || '';
        const contractFile = document.getElementById('emp-contract-file').files[0];
        if (contractFile) {
            if (existingData.contractDocument) await deleteFile(existingData.contractDocument);
            const formData = new FormData();
            formData.append('file', contractFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('Contract upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) contractUrl = uploadData.data;
        }
        
        // Upload bank book
        let bankBookUrl = existingData.bankBookDocument || '';
        const bankBookFile = document.getElementById('emp-bankbook-file').files[0];
        if (bankBookFile) {
            if (existingData.bankBookDocument) await deleteFile(existingData.bankBookDocument);
            const formData = new FormData();
            formData.append('file', bankBookFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('Bank book upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) bankBookUrl = uploadData.data;
        }
        
        // Upload BPJS card
        let bpjsCardUrl = existingData.bpjsCardDocument || '';
        const bpjsCardFile = document.getElementById('emp-bpjs-file').files[0];
        if (bpjsCardFile) {
            if (existingData.bpjsCardDocument) await deleteFile(existingData.bpjsCardDocument);
            const formData = new FormData();
            formData.append('file', bpjsCardFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('BPJS card upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) bpjsCardUrl = uploadData.data;
        }
        
        // Upload KTP
        let ktpUrl = existingData.ktpDocument || '';
        const ktpFile = document.getElementById('emp-ktp-file').files[0];
        if (ktpFile) {
            if (existingData.ktpDocument) await deleteFile(existingData.ktpDocument);
            const formData = new FormData();
            formData.append('file', ktpFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('KTP upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) ktpUrl = uploadData.data;
        }
        
        // Upload family card
        let familyCardUrl = existingData.familyCardDocument || '';
        const familyCardFile = document.getElementById('emp-familycard-file').files[0];
        if (familyCardFile) {
            if (existingData.familyCardDocument) await deleteFile(existingData.familyCardDocument);
            const formData = new FormData();
            formData.append('file', familyCardFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('Family card upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) familyCardUrl = uploadData.data;
        }
        
        // Upload Passport
        let passportUrl = existingData.passportDocument || '';
        const passportFile = document.getElementById('emp-passport-file').files[0];
        if (passportFile) {
            if (existingData.passportDocument) await deleteFile(existingData.passportDocument);
            const formData = new FormData();
            formData.append('file', passportFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('Passport upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) passportUrl = uploadData.data;
        }
        
        // Upload Certificate
        let certificateUrl = existingData.certificateDocument || '';
        const certificateFile = document.getElementById('emp-certificate-file').files[0];
        if (certificateFile) {
            if (existingData.certificateDocument) await deleteFile(existingData.certificateDocument);
            const formData = new FormData();
            formData.append('file', certificateFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('Certificate upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) certificateUrl = uploadData.data;
        }
        
        // Upload transcript
        let transcriptUrl = existingData.transcriptDocument || '';
        const transcriptFile = document.getElementById('emp-transcript-file').files[0];
        if (transcriptFile) {
            if (existingData.transcriptDocument) await deleteFile(existingData.transcriptDocument);
            const formData = new FormData();
            formData.append('file', transcriptFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('Transcript upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) transcriptUrl = uploadData.data;
        }
        
        // Upload marriage certificate
        let marriageUrl = existingData.marriageCertificateDocument || '';
        const marriageFile = document.getElementById('emp-marriage-file').files[0];
        if (marriageFile) {
            if (existingData.marriageCertificateDocument) await deleteFile(existingData.marriageCertificateDocument);
            const formData = new FormData();
            formData.append('file', marriageFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('Marriage certificate upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) marriageUrl = uploadData.data;
        }
        
        // Upload child certificate
        let childCertUrl = existingData.childCertificateDocument || '';
        const childCertFile = document.getElementById('emp-child-file').files[0];
        if (childCertFile) {
            if (existingData.childCertificateDocument) await deleteFile(existingData.childCertificateDocument);
            const formData = new FormData();
            formData.append('file', childCertFile);
            const uploadRes = await fetch(`${API}/files/upload`, { method: 'POST', body: formData });
            if (!uploadRes.ok) throw new Error('Child certificate upload failed');
            const uploadData = await uploadRes.json();
            if (uploadData.success) childCertUrl = uploadData.data;
        }
        const data = {
            employeeCode: document.getElementById('emp-code').value,
            name: document.getElementById('emp-name').value,
            jobTitle: document.getElementById('emp-job').value,
            jobPosition: document.getElementById('emp-position').value,
            workEmail: document.getElementById('emp-email').value,
            workPhone: document.getElementById('emp-phone').value,
            workMobile: document.getElementById('emp-mobile').value,
            employeeType: document.getElementById('emp-type').value,
            status: document.getElementById('emp-status').value,
            joinDate: document.getElementById('emp-join').value,
            monthlyCost: document.getElementById('emp-monthly-cost').value ? parseFloat(document.getElementById('emp-monthly-cost').value) : null,
            relatedUser: document.getElementById('emp-related-user').value || null,
            photo: photoUrl,
            contractDocument: contractUrl,
            departmentId: document.getElementById('emp-dept').value || null,
            managerId: document.getElementById('emp-manager').value || null,
            coachId: document.getElementById('emp-coach').value || null,
            
            privateAddress: document.getElementById('emp-private-address').value,
            privateEmail: document.getElementById('emp-private-email').value,
            privatePhone: document.getElementById('emp-private-phone').value,
            bankName: document.getElementById('emp-bank').value,
            accountNumber: document.getElementById('emp-account').value,
            homeToWorkDistance: document.getElementById('emp-distance').value ? parseFloat(document.getElementById('emp-distance').value) : null,
            bpjsId: document.getElementById('emp-bpjs').value,
            bankBookDocument: bankBookUrl,
            bpjsCardDocument: bpjsCardUrl,
            
            nationality: document.getElementById('emp-nationality').value,
            identificationNumber: document.getElementById('emp-id-number').value,
            ktpDocument: ktpUrl,
            familyCardDocument: familyCardUrl,
            passportNumber: document.getElementById('emp-passport').value,
            passportDocument: passportUrl,
            gender: document.getElementById('emp-gender').value || null,
            dateOfBirth: document.getElementById('emp-dob').value || null,
            placeOfBirth: document.getElementById('emp-pob').value,
            countryOfBirth: document.getElementById('emp-cob').value,
            
            emergencyContactName: document.getElementById('emp-emergency-name').value,
            emergencyContactPhone: document.getElementById('emp-emergency-phone').value,
            
            certificateLevel: document.getElementById('emp-cert-level').value || null,
            fieldOfStudy: document.getElementById('emp-field').value,
            school: document.getElementById('emp-school').value,
            certificateDocument: certificateUrl,
            transcriptDocument: transcriptUrl,
            
            maritalStatus: document.getElementById('emp-marital').value || null,
            numberOfDependentChildren: document.getElementById('emp-children').value ? parseInt(document.getElementById('emp-children').value) : null,
            marriageCertificateDocument: marriageUrl,
            childCertificateDocument: childCertUrl
        };
    
        const url = id ? `${API}/employees-complete/${id}` : `${API}/employees-complete`;
        const res = await fetch(url, {
            method: id ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        const result = await res.json();
        
        if (result.success) {
            showNotification(result.message);
            closeModal('employee-modal');
            loadEmployees();
            loadDashboard();
        } else {
            showNotification(result.message, true);
        }
    } catch (error) {
        showNotification(error.message || 'Error saving employee', true);
    }
}

async function editEmployee(id) {
    try {
        const res = await fetch(`${API}/employees-complete/${id}`);
        const data = await res.json();
        
        if (data.success) {
            const emp = data.data;
            document.getElementById('emp-id').value = emp.id;
            document.getElementById('emp-code').value = emp.employeeCode;
            document.getElementById('emp-name').value = emp.name;
            document.getElementById('emp-job').value = emp.jobTitle || '';
            document.getElementById('emp-position').value = emp.jobPosition || '';
            document.getElementById('emp-email').value = emp.workEmail || '';
            document.getElementById('emp-phone').value = emp.workPhone || '';
            document.getElementById('emp-mobile').value = emp.workMobile || '';
            document.getElementById('emp-type').value = emp.employeeType;
            document.getElementById('emp-status').value = emp.status;
            document.getElementById('emp-join').value = emp.joinDate;
            document.getElementById('emp-monthly-cost').value = emp.monthlyCost || '';
            document.getElementById('emp-related-user').value = emp.relatedUser || '';
            document.getElementById('emp-dept').value = emp.departmentId || '';
            document.getElementById('emp-manager').value = emp.managerId || '';
            document.getElementById('emp-coach').value = emp.coachId || '';
            
            document.getElementById('emp-private-address').value = emp.privateAddress || '';
            document.getElementById('emp-private-email').value = emp.privateEmail || '';
            document.getElementById('emp-private-phone').value = emp.privatePhone || '';
            document.getElementById('emp-bank').value = emp.bankName || '';
            document.getElementById('emp-account').value = emp.accountNumber || '';
            document.getElementById('emp-distance').value = emp.homeToWorkDistance || '';
            document.getElementById('emp-bpjs').value = emp.bpjsId || '';
            
            document.getElementById('emp-nationality').value = emp.nationality || '';
            document.getElementById('emp-id-number').value = emp.identificationNumber || '';
            document.getElementById('emp-passport').value = emp.passportNumber || '';
            document.getElementById('emp-gender').value = emp.gender || '';
            document.getElementById('emp-dob').value = emp.dateOfBirth || '';
            document.getElementById('emp-pob').value = emp.placeOfBirth || '';
            document.getElementById('emp-cob').value = emp.countryOfBirth || '';
            
            document.getElementById('emp-emergency-name').value = emp.emergencyContactName || '';
            document.getElementById('emp-emergency-phone').value = emp.emergencyContactPhone || '';
            
            document.getElementById('emp-cert-level').value = emp.certificateLevel || '';
            document.getElementById('emp-field').value = emp.fieldOfStudy || '';
            document.getElementById('emp-school').value = emp.school || '';
            
            document.getElementById('emp-marital').value = emp.maritalStatus || '';
            document.getElementById('emp-children').value = emp.numberOfDependentChildren || '';
            
            document.getElementById('employee-form-title').textContent = 'Edit Employee';
            await loadDepartmentsForSelect();
            await loadEmployeesForManagerCoach();
            await loadEmployeesForRelatedUser();
            document.getElementById('emp-dept').value = emp.departmentId || '';
            document.getElementById('emp-manager').value = emp.managerId || '';
            document.getElementById('emp-coach').value = emp.coachId || '';
            document.getElementById('emp-related-user').value = emp.relatedUser || '';
            showModal('employee-modal');
        }
    } catch (error) {
        showNotification('Error loading employee', true);
    }
}

async function viewEmployee(id) {
    try {
        const res = await fetch(`${API}/employees-complete/${id}`);
        const data = await res.json();
        
        if (data.success) {
            const emp = data.data;
            const content = `
                <div class="detail-section">
                    <h4>Basic Information</h4>
                    <p><strong>Employee Code:</strong> ${emp.employeeCode}</p>
                    <p><strong>Name:</strong> ${emp.name}</p>
                    <p><strong>Job Title:</strong> ${emp.jobTitle || '-'}</p>
                    <p><strong>Job Position:</strong> ${emp.jobPosition || '-'}</p>
                    <p><strong>Work Email:</strong> ${emp.workEmail || '-'}</p>
                    <p><strong>Work Phone:</strong> ${emp.workPhone || '-'}</p>
                    <p><strong>Work Mobile:</strong> ${emp.workMobile || '-'}</p>
                    <p><strong>Type:</strong> ${emp.employeeType}</p>
                    <p><strong>Status:</strong> <span class="badge badge-${emp.status === 'ACTIVE' ? 'success' : 'warning'}">${emp.status}</span></p>
                    <p><strong>Join Date:</strong> ${emp.joinDate}</p>
                    <p><strong>Monthly Cost:</strong> ${emp.monthlyCost ? 'Rp ' + emp.monthlyCost.toLocaleString() : '-'}</p>
                    <p><strong>Related User:</strong> ${emp.relatedUser || '-'}</p>
                    <p><strong>Department:</strong> ${emp.departmentName || '-'}</p>
                    <p><strong>Manager:</strong> ${emp.managerName || '-'}</p>
                    <p><strong>Coach:</strong> ${emp.coachName || '-'}</p>
                    ${emp.photo ? `<p><strong>Photo:</strong> <a href="${emp.photo}" target="_blank">View</a></p>` : ''}
                    ${emp.contractDocument ? `<p><strong>Contract:</strong> <a href="${emp.contractDocument}" target="_blank">View</a></p>` : ''}
                </div>
                
                <div class="detail-section">
                    <h4>Private Contact</h4>
                    <p><strong>Address:</strong> ${emp.privateAddress || '-'}</p>
                    <p><strong>Email:</strong> ${emp.privateEmail || '-'}</p>
                    <p><strong>Phone:</strong> ${emp.privatePhone || '-'}</p>
                    <p><strong>Bank:</strong> ${emp.bankName || '-'}</p>
                    <p><strong>Account Number:</strong> ${emp.accountNumber || '-'}</p>
                    <p><strong>Distance to Work:</strong> ${emp.homeToWorkDistance ? emp.homeToWorkDistance + ' km' : '-'}</p>
                    <p><strong>BPJS ID:</strong> ${emp.bpjsId || '-'}</p>
                    ${emp.bankBookDocument ? `<p><strong>Bank Book:</strong> <a href="${emp.bankBookDocument}" target="_blank">View</a></p>` : ''}
                    ${emp.bpjsCardDocument ? `<p><strong>BPJS Card:</strong> <a href="${emp.bpjsCardDocument}" target="_blank">View</a></p>` : ''}
                </div>
                
                <div class="detail-section">
                    <h4>Citizenship</h4>
                    <p><strong>Nationality:</strong> ${emp.nationality || '-'}</p>
                    <p><strong>ID Number:</strong> ${emp.identificationNumber || '-'}</p>
                    <p><strong>Passport:</strong> ${emp.passportNumber || '-'}</p>
                    <p><strong>Gender:</strong> ${emp.gender || '-'}</p>
                    <p><strong>Date of Birth:</strong> ${emp.dateOfBirth || '-'}</p>
                    <p><strong>Place of Birth:</strong> ${emp.placeOfBirth || '-'}</p>
                    <p><strong>Country of Birth:</strong> ${emp.countryOfBirth || '-'}</p>
                    ${emp.ktpDocument ? `<p><strong>KTP:</strong> <a href="${emp.ktpDocument}" target="_blank">View</a></p>` : ''}
                    ${emp.passportDocument ? `<p><strong>Passport Doc:</strong> <a href="${emp.passportDocument}" target="_blank">View</a></p>` : ''}
                    ${emp.familyCardDocument ? `<p><strong>Family Card:</strong> <a href="${emp.familyCardDocument}" target="_blank">View</a></p>` : ''}
                </div>
                
                <div class="detail-section">
                    <h4>Emergency Contact</h4>
                    <p><strong>Name:</strong> ${emp.emergencyContactName || '-'}</p>
                    <p><strong>Phone:</strong> ${emp.emergencyContactPhone || '-'}</p>
                </div>
                
                <div class="detail-section">
                    <h4>Education</h4>
                    <p><strong>Level:</strong> ${emp.certificateLevel || '-'}</p>
                    <p><strong>Field of Study:</strong> ${emp.fieldOfStudy || '-'}</p>
                    <p><strong>School:</strong> ${emp.school || '-'}</p>
                    ${emp.certificateDocument ? `<p><strong>Certificate:</strong> <a href="${emp.certificateDocument}" target="_blank">View</a></p>` : ''}
                    ${emp.transcriptDocument ? `<p><strong>Transcript:</strong> <a href="${emp.transcriptDocument}" target="_blank">View</a></p>` : ''}
                </div>
                
                <div class="detail-section">
                    <h4>Family Status</h4>
                    <p><strong>Marital Status:</strong> ${emp.maritalStatus || '-'}</p>
                    <p><strong>Children:</strong> ${emp.numberOfDependentChildren || '0'}</p>
                    ${emp.marriageCertificateDocument ? `<p><strong>Marriage Certificate:</strong> <a href="${emp.marriageCertificateDocument}" target="_blank">View</a></p>` : ''}
                    ${emp.childCertificateDocument ? `<p><strong>Child Certificate:</strong> <a href="${emp.childCertificateDocument}" target="_blank">View</a></p>` : ''}
                </div>
            `;
            
            document.getElementById('employee-detail-content').innerHTML = content;
            showModal('employee-detail-modal');
        }
    } catch (error) {
        showNotification('Error loading employee details', true);
    }
}

async function deleteEmployee(id) {
    if (!confirm('Delete this employee?')) return;
    
    try {
        // Get employee data to delete files
        const getRes = await fetch(`${API}/employees-complete/${id}`);
        const getData = await getRes.json();
        
        if (getData.success) {
            const emp = getData.data;
            // Delete all files
            if (emp.photo) await deleteFile(emp.photo);
            if (emp.contractDocument) await deleteFile(emp.contractDocument);
            if (emp.bankBookDocument) await deleteFile(emp.bankBookDocument);
            if (emp.bpjsCardDocument) await deleteFile(emp.bpjsCardDocument);
            if (emp.ktpDocument) await deleteFile(emp.ktpDocument);
            if (emp.familyCardDocument) await deleteFile(emp.familyCardDocument);
            if (emp.passportDocument) await deleteFile(emp.passportDocument);
            if (emp.certificateDocument) await deleteFile(emp.certificateDocument);
            if (emp.transcriptDocument) await deleteFile(emp.transcriptDocument);
            if (emp.marriageCertificateDocument) await deleteFile(emp.marriageCertificateDocument);
            if (emp.childCertificateDocument) await deleteFile(emp.childCertificateDocument);
        }
        
        const res = await fetch(`${API}/employees-complete/${id}`, { method: 'DELETE' });
        const data = await res.json();
        
        if (data.success) {
            showNotification(data.message);
            loadEmployees();
            loadDashboard();
        } else {
            showNotification(data.message, true);
        }
    } catch (error) {
        showNotification('Error deleting employee', true);
    }
}

// Departments
async function loadDepartments() {
    try {
        const res = await fetch(`${API}/departments`);
        const data = await res.json();
        const list = document.getElementById('department-list');
        
        if (data.success && data.data.length > 0) {
            list.innerHTML = data.data.map(dept => `
                <div class="data-item">
                    <div class="data-item-info">
                        <strong>${dept.departmentName}</strong><br>
                        <small>Manager: ${dept.managerName || '-'}</small>
                    </div>
                    <div class="data-item-actions">
                        <button class="btn btn-warning btn-sm" onclick="editDepartment(${dept.id})">Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteDepartment(${dept.id})">Delete</button>
                    </div>
                </div>
            `).join('');
        } else {
            list.innerHTML = '<p>No departments found</p>';
        }
    } catch (error) {
        showNotification('Error loading departments', true);
    }
}

function showDepartmentForm() {
    document.getElementById('department-form').reset();
    document.getElementById('dept-id').value = '';
    document.getElementById('department-form-title').textContent = 'Add Department';
    loadEmployeesForManager();
    showModal('department-modal');
}

async function saveDepartment(e) {
    e.preventDefault();
    
    const id = document.getElementById('dept-id').value;
    const data = {
        departmentName: document.getElementById('dept-name').value,
        managerId: document.getElementById('dept-manager').value || null
    };
    
    try {
        const url = id ? `${API}/departments/${id}` : `${API}/departments`;
        const res = await fetch(url, {
            method: id ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        const result = await res.json();
        
        if (result.success) {
            showNotification(result.message);
            closeModal('department-modal');
            loadDepartments();
        } else {
            showNotification(result.message, true);
        }
    } catch (error) {
        showNotification('Error saving department', true);
    }
}

async function editDepartment(id) {
    try {
        const res = await fetch(`${API}/departments/${id}`);
        const data = await res.json();
        
        if (data.success) {
            const dept = data.data;
            document.getElementById('dept-id').value = dept.id;
            document.getElementById('dept-name').value = dept.departmentName;
            
            document.getElementById('department-form-title').textContent = 'Edit Department';
            await loadEmployeesForManager();
            document.getElementById('dept-manager').value = dept.managerId || '';
            showModal('department-modal');
        }
    } catch (error) {
        showNotification('Error loading department', true);
    }
}

async function deleteDepartment(id) {
    if (!confirm('Delete this department?')) return;
    
    try {
        const res = await fetch(`${API}/departments/${id}`, { method: 'DELETE' });
        const data = await res.json();
        
        if (data.success) {
            showNotification(data.message);
            loadDepartments();
        } else {
            showNotification(data.message, true);
        }
    } catch (error) {
        showNotification('Error deleting department', true);
    }
}

// Attendance
async function loadAttendances(dateFrom = '', dateTo = '', employeeId = '') {
    try {
        let url = `${API}/attendances?`;
        if (dateFrom) url += `dateFrom=${dateFrom}&`;
        if (dateTo) url += `dateTo=${dateTo}&`;
        if (employeeId) url += `employeeId=${employeeId}`;
        
        const res = await fetch(url);
        const data = await res.json();
        const list = document.getElementById('attendance-list');
        
        if (data.success && data.data.length > 0) {
            list.innerHTML = data.data.map(att => `
                <div class="data-item">
                    <div class="data-item-info">
                        <strong>${att.employeeName}</strong><br>
                        <small>${att.date} | In: ${att.checkIn ? new Date(att.checkIn).toLocaleTimeString() : '-'} | 
                        Out: ${att.checkOut ? new Date(att.checkOut).toLocaleTimeString() : '-'} | 
                        <span class="badge badge-success">${att.status}</span></small>
                    </div>
                </div>
            `).join('');
        } else {
            list.innerHTML = '<p>No attendance records found</p>';
        }
    } catch (error) {
        showNotification('Error loading attendances', true);
    }
}

async function checkIn() {
    const employeeId = document.getElementById('att-employee').value;
    if (!employeeId) return showNotification('Select employee', true);
    
    try {
        const res = await fetch(`${API}/attendances/checkin`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ employeeId: parseInt(employeeId) })
        });
        const data = await res.json();
        
        if (data.success) {
            showNotification(data.message);
            loadAttendances();
        } else {
            showNotification(data.message, true);
        }
    } catch (error) {
        showNotification('Error checking in', true);
    }
}

async function checkOut() {
    const employeeId = document.getElementById('att-employee').value;
    if (!employeeId) return showNotification('Select employee', true);
    
    try {
        const res = await fetch(`${API}/attendances/checkout`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ employeeId: parseInt(employeeId) })
        });
        const data = await res.json();
        
        if (data.success) {
            showNotification(data.message);
            loadAttendances();
        } else {
            showNotification(data.message, true);
        }
    } catch (error) {
        showNotification('Error checking out', true);
    }
}

function filterAttendance() {
    const dateFrom = document.getElementById('filter-from').value;
    const dateTo = document.getElementById('filter-to').value;
    const employeeId = document.getElementById('filter-emp').value;
    loadAttendances(dateFrom, dateTo, employeeId);
}

function resetFilter() {
    document.getElementById('filter-from').value = '';
    document.getElementById('filter-to').value = '';
    document.getElementById('filter-emp').value = '';
    loadAttendances();
}

// Helper Functions
async function deleteFile(fileUrl) {
    if (!fileUrl) return;
    try {
        const filename = fileUrl.split('/').pop();
        await fetch(`${API}/files/${filename}`, { method: 'DELETE' });
    } catch (error) {
        console.error('Error deleting file:', error);
    }
}

async function loadDepartmentsForSelect() {
    try {
        const res = await fetch(`${API}/departments`);
        const data = await res.json();
        const select = document.getElementById('emp-dept');
        select.innerHTML = '<option value="">Select Department</option>';
        if (data.success) {
            data.data.forEach(dept => {
                select.innerHTML += `<option value="${dept.id}">${dept.departmentName}</option>`;
            });
        }
    } catch (error) {
        console.error('Error loading departments');
    }
}

async function loadEmployeesForManager() {
    try {
        const res = await fetch(`${API}/employees-complete`);
        const data = await res.json();
        const select = document.getElementById('dept-manager');
        select.innerHTML = '<option value="">Select Manager</option>';
        if (data.success) {
            data.data.forEach(emp => {
                select.innerHTML += `<option value="${emp.id}">${emp.name} (${emp.employeeCode})</option>`;
            });
        }
    } catch (error) {
        console.error('Error loading employees');
    }
}

async function loadEmployeesForManagerCoach() {
    try {
        const res = await fetch(`${API}/employees-complete`);
        const data = await res.json();
        const managerSelect = document.getElementById('emp-manager');
        const coachSelect = document.getElementById('emp-coach');
        
        managerSelect.innerHTML = '<option value="">Select Manager</option>';
        coachSelect.innerHTML = '<option value="">Select Coach</option>';
        
        if (data.success) {
            data.data.forEach(emp => {
                const option = `<option value="${emp.id}">${emp.name} (${emp.employeeCode})</option>`;
                managerSelect.innerHTML += option;
                coachSelect.innerHTML += option;
            });
        }
    } catch (error) {
        console.error('Error loading employees');
    }
}

async function loadEmployeesForRelatedUser() {
    try {
        const res = await fetch(`${API}/employees-complete`);
        const data = await res.json();
        const select = document.getElementById('emp-related-user');
        
        select.innerHTML = '<option value="">Select Related User</option>';
        
        if (data.success) {
            data.data.forEach(emp => {
                select.innerHTML += `<option value="${emp.id}">${emp.name} (${emp.employeeCode})</option>`;
            });
        }
    } catch (error) {
        console.error('Error loading employees');
    }
}

async function loadEmployeesForSelect() {
    try {
        const res = await fetch(`${API}/employees-complete`);
        const data = await res.json();
        const select1 = document.getElementById('att-employee');
        const select2 = document.getElementById('filter-emp');
        
        select1.innerHTML = '<option value="">Select Employee</option>';
        select2.innerHTML = '<option value="">All Employees</option>';
        
        if (data.success) {
            data.data.forEach(emp => {
                const option = `<option value="${emp.id}">${emp.name} (${emp.employeeCode})</option>`;
                select1.innerHTML += option;
                select2.innerHTML += option;
            });
        }
    } catch (error) {
        console.error('Error loading employees');
    }
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    // Setup tab click handlers
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', () => {
            const tabName = tab.getAttribute('data-tab');
            showTab(tabName);
        });
    });
    
    // Only load dashboard on init
    loadDashboard();
});
