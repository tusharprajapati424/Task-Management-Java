--INSERT INTO users (username, email, password, role)
--VALUES ('john_doe', 'john@example.com', '123', 'ADMIN');

--INSERT INTO users (username, email, password, role) VALUES
--( 'Amit Sharma', 'amit.sharma@example.com', '$2a$10$.S6FMcPo4A6ZmLitMCDCJOtbImRmXOn1AtyHHNt2cAfjfOzWHYXHS','ADMIN'),
--('Priya Verma', 'priya.verma@example.com', '$2a$10$.S6FMcPo4A6ZmLitMCDCJOtbImRmXOn1AtyHHNt2cAfjfOzWHYXHS', 'EMPLOYEE'),
--('Rahul Kumar', 'rahul.kumar@example.com', '$2a$10$.S6FMcPo4A6ZmLitMCDCJOtbImRmXOn1AtyHHNt2cAfjfOzWHYXHS', 'EMPLOYEE'),
--( 'Neha Singh', 'neha.singh@example.com', '$2a$10$.S6FMcPo4A6ZmLitMCDCJOtbImRmXOn1AtyHHNt2cAfjfOzWHYXHS', 'EMPLOYEE'),
--('Vikram Joshi', 'vikram.joshi@example.com', '$2a$10$.S6FMcPo4A6ZmLitMCDCJOtbImRmXOn1AtyHHNt2cAfjfOzWHYXHS', 'EMPLOYEE');

--INSERT INTO users (username, email, password, role) VALUES
--( 'Amit Sharma', 'amit.sharma@example.com', '123','ADMIN'),
--('Priya Verma', 'priya.verma@example.com', '123', 'EMPLOYEE'),
--('Rahul Kumar', 'rahul.kumar@example.com', '123', 'EMPLOYEE'),
--( 'Neha Singh', 'neha.singh@example.com', '123', 'EMPLOYEE'),
--('Vikram Joshi', 'vikram.joshi@example.com', '123', 'EMPLOYEE');

--INSERT INTO task (id, title, description, status, user_id) VALUES
--(1, 'Complete Spring Security Module', 'Implement role-based authentication.', 'IN_PROGRESS', 2),
--(2, 'Design ', 'Create ER diagram and define relationships.', 'PENDING', 3),
--(3, 'REST API', 'Build CRUD operations using Spring Boot.', 'IN_PROGRESS', 4),
--(4, 'Unit Tests', 'Cover API endpoints with JUnit & Mockito.', 'PENDING', 5),
--(5, 'CI/CD Pipeline', 'Configure Jenkins for automated deployment.', 'COMPLETED', 2);

INSERT INTO users (username, email, password, role) VALUES
( 'Tushar P', 'tushar@gmail.com', '$2a$10$.S6FMcPo4A6ZmLitMCDCJOtbImRmXOn1AtyHHNt2cAfjfOzWHYXHS','ADMIN');