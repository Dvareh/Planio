# Planio
Planio is a full-stack task management application designed to help users organize and manage their tasks.
The system allows users to create tasks, set deadlines, add comments, and receive notifications about upcoming deadlines.

# Features:
  ## Authentication:
      User registration
      JWT authentication
      Secure login
      Password change
  ## Boards:
      Create, update and delete boards
      Board ownership
      Invite participants by email
      Access control for board members
  ## Tasks:
      Create, update and delete tasks
      Assign tasks to board participants
      Task status management
      Due dates
      Search by title
      Filtering by status
      Pagination
      Sorting
  ## Comments:
      Add comments to tasks
      View task comments
      Delete comments
      Notifications
      Automatic email reminders
      Notifications sent:
      3 days before deadline
      1 day before deadline
      Notification history stored in the database
  ## User Management:
      View current profile
      Update own profile
      Change password
      Administration
      ADMIN role
      View all users
      Change user roles
      Delete users
      Protected admin endpoints
# Technology Stack:
  ## Backend:
    Java 21
    Spring Boot
    Spring Security
    Spring Data JPA
    Spring Email
    Hibernate
    PostgreSQL
    JWT
    Lombok
    Maven
    
  ## Documentation:
    Swagger / OpenAPI
    
  ## Frontend:

    Angular
    TypeScript
    HTML5
    CSS3
    Angular Router
    Angular HttpClient
    RxJS
# Security:

  Planio implements JWT-based authentication and role-based authorization.

  ## Roles:
    USER
    ADMIN
    Authorization

  ## Regular users can:

    Manage their own profile
    Manage their own boards
    Access boards they participate in

  ## Administrators can additionally:
    
    View all users
    Delete users
    Change user roles


# Email Notifications:

  The application automatically checks upcoming task deadlines every day.

## Users receive reminder emails:

    3 days before deadline
    1 day before deadline

  Sent notifications are saved in the database to prevent duplicates.

# Author:
  Artiom Prilepschi
