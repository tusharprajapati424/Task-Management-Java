# Task-Management-Java
Task Management Java


  📌 Features Implemented
✅ Spring Boot for REST API development
 ✅ Spring Security with Admin & Employee roles
 ✅ CRUD operations for Tasks and Users
 ✅ JPA/Hibernate for One-to-Many relationship (A user can have multiple tasks)
 ✅ Gradle for dependency management

--------------------------------------------------------------


🛠 Entities & Relationships
1️⃣ User (ID, Name, Email, Role [ADMIN, EMPLOYEE])
One user can have multiple tasks
 2️⃣ Task (ID, Title, Description, Status, Assigned User)

Each task is assigned to one user


--------------------------------------------------------------



🛠 Endpoints
🔐 Authentication
POST /auth/login → Get JWT Token


POST /auth/register → Register (Only Admin can register new users)



--------------------------------------------------------------


👤 User Management
GET /users → Admin can view all users


GET /users/{id} → Get a user by ID


DELETE /users/{id} → Admin can delete a user



--------------------------------------------------------------


✅ Task Management
POST /tasks → Create a new task (Admin only)


GET /tasks → View all tasks (Admin & Employee)


GET /tasks/{id} → Get task details


PUT /tasks/{id} → Update task (Assigned Employee can update their tasks)


DELETE /tasks/{id} → Admin can delete any task
