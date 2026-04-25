-- Seed default users after Hibernate creates the schema
INSERT INTO users (username, password, email, full_name, role, active, created_at)
VALUES ('24RP02342', '24RP03897', 'user@airtel.com', 'Test User', 'USER', TRUE, NOW())
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    email = VALUES(email),
    full_name = VALUES(full_name),
    role = VALUES(role),
    active = VALUES(active);

INSERT INTO users (username, password, email, full_name, role, active, created_at)
VALUES ('admin', 'admin123', 'admin@airtel.com', 'Administrator', 'ADMIN', TRUE, NOW())
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    email = VALUES(email),
    full_name = VALUES(full_name),
    role = VALUES(role),
    active = VALUES(active);

-- Seed demo records so Assignments History has visible data
INSERT INTO departments (name, description)
SELECT 'IT', 'Information Technology'
WHERE NOT EXISTS (
    SELECT 1 FROM departments WHERE name = 'IT'
);

INSERT INTO employees (first_name, last_name, employee_id, email, phone, job_title, department_id, active)
SELECT 'Demo', 'Employee', 'EMP-001', 'demo.employee@airtel.com', '0700000000', 'Network Engineer',
       (SELECT id FROM departments WHERE name = 'IT' LIMIT 1), TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM employees WHERE employee_id = 'EMP-001'
);

INSERT INTO devices (asset_tag, device_type, brand, model, serial_number, processor, ram, storage,
                                         operating_system, device_status, device_condition, purchase_date, warranty_expiry, notes)
VALUES ('AIR-LT-001', 'Laptop', 'Dell', 'Latitude 5520', 'SN-5520-001', 'Intel Core i5', '8GB', '256GB SSD',
                'Windows 11', 'AVAILABLE', 'GOOD', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 365 DAY), 'Demo seeded device')
ON DUPLICATE KEY UPDATE
        device_status = 'AVAILABLE',
        device_condition = 'GOOD';

-- Keep history visible but leave the device AVAILABLE for new assignments
INSERT INTO assignments (device_id, employee_id, issue_date, return_date, issued_by, returned_to, purpose, notes, assignment_status)
SELECT d.id, e.id, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY),
             'System Seed', 'System Seed', 'Demo Assignment', 'Seeded for history visibility', 'RETURNED'
FROM devices d
JOIN employees e ON e.employee_id = 'EMP-001'
WHERE d.asset_tag = 'AIR-LT-001'
    AND NOT EXISTS (
            SELECT 1 FROM assignments a WHERE a.device_id = d.id
    );
