# Planio

Planio is a full-stack task and project management application built with Spring Boot and Angular.

The application allows users to organize work using boards and Kanban task management, collaborate with other users, assign tasks, track deadlines, manage priorities and labels, leave comments, receive notifications, and review task activity history.

---

## Features

### Authentication

- User registration
- Secure login
- JWT-based authentication
- Protected frontend routes
- Automatic redirect to the login page for unauthenticated users
- Password change
- Logout
- Current user profile

### Boards

- Create boards
- Update board name and description
- Delete boards
- Board ownership
- Custom board keys
- Invite participants by email
- Remove participants
- Access control for board members
- Dedicated Board Settings page
- Owner-only board management actions

### Kanban Task Management

Each board contains a Kanban board with four task statuses:

- To Do
- In Progress
- Done
- Cancelled

Tasks can be moved between columns using drag and drop.

### Tasks

- Create tasks
- View task details
- Update tasks
- Delete tasks
- Edit task title
- Edit task description
- Edit due date
- Change task status
- Change task priority
- Assign and unassign tasks
- Assign tasks only to board participants
- Task keys generated per board, for example:
  - `PLAN-1`
  - `PLAN-2`
- Independent task numbering for every board

### Task Priorities

Supported priorities:

- Low
- Medium
- High
- Urgent

Priority can be changed directly from Task Details.

### Labels

- Default labels created for each board
- Create custom board labels
- Delete custom labels
- Assign labels to tasks
- Remove labels from tasks
- Board-specific labels
- Label management restricted to the board owner

### Advanced Task Filtering

Tasks can be filtered by:

- Title
- Status
- Priority
- Assigned user
- Label
- Due date range

The backend also supports pagination and sorting.

### Comments

- Add comments to tasks
- View task comments
- Edit comments
- Delete comments
- Display comment author username
- Comment changes are recorded in task activity history

### Task Activity History

Planio stores the history of important task changes.

Tracked events include:

- Task creation
- Title changes
- Description changes
- Status changes
- Priority changes
- Due date changes
- Assignee changes
- Label additions
- Label removals
- Comment creation
- Comment updates
- Comment deletion

Each activity entry contains:

- User who performed the action
- Type of change
- Previous and new values when applicable
- Date and time of the action

### Notifications

- In-app notifications
- Notification history
- Mark notification as read
- Mark all notifications as read
- Open related task directly from a notification
- Deadline notifications

### Email Reminders

Planio automatically checks upcoming task deadlines.

Assigned users receive email reminders:

- 3 days before the deadline
- 1 day before the deadline

Reminder state is stored in the database to prevent duplicate notifications.

### Dashboard

The Dashboard provides an overview of the user's work.

It includes:

- My Boards
- My Tasks
- Task statistics
- To Do task count
- In Progress task count
- Completed task count
- Cancelled task count
- Upcoming deadlines
- Overdue tasks
- Direct navigation to boards and tasks

### User Profile

Users can:

- View their profile
- Update username
- Update email
- Change password

### Navigation

The frontend includes:

- Header
- Sidebar
- Dashboard navigation
- Boards navigation
- Notifications navigation
- Profile navigation
- Board Settings navigation
- Breadcrumb navigation between Dashboard, Boards, Settings, and Tasks

### Access Control

Planio includes board-level access control.

Board owners can:

- Manage board settings
- Manage participants
- Manage labels
- Delete the board
- Manage tasks

Board participants can:

- Access boards they participate in
- View tasks
- Create and update tasks
- Move tasks on the Kanban board
- Assign tasks
- Use existing labels
- Add comments

Users who are not members of a board cannot access its data.

---

## Administration

Planio supports role-based authorization.

### Roles

- `USER`
- `ADMIN`

### Administrators can:

- View all users
- Update users
- Change user roles
- Delete users
- Access protected administration endpoints

Regular users cannot access administrator endpoints.

---

## Security

Planio uses JWT-based authentication and role-based authorization.

Security features include:

- Spring Security
- JWT authentication
- BCrypt password hashing
- Stateless authentication
- Protected backend endpoints
- Protected Angular routes
- Board-level authorization
- Role-based administration endpoints

---

## Technology Stack

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Spring Email
- PostgreSQL
- JWT
- Lombok
- Maven

### Frontend

- Angular
- TypeScript
- HTML5
- CSS3
- Angular Router
- Angular HttpClient
- Angular CDK Drag & Drop
- RxJS

### API Documentation

- Swagger
- OpenAPI

---

## Main Application Structure

Planio consists of two main applications:

```text
Planio/
├── backend/
└── frontend/
```

### Backend

Responsible for:

- REST API
- Authentication
- Authorization
- Business logic
- Database access
- Notifications
- Email reminders
- Task activity tracking

### Frontend

Responsible for:

- User interface
- Authentication flow
- Dashboard
- Kanban board
- Task management
- Board management
- User profile
- Notifications
- Activity history

---

## Main Entities

The main domain entities are:

- User
- Board
- Task
- Label
- Comment
- Notification
- TaskActivity

Main relationships include:

- A User can own multiple Boards
- A Board can have multiple participants
- A Board contains multiple Tasks
- A Task belongs to one Board
- A Task can be assigned to a User
- A Task can have multiple Labels
- A Task can have multiple Comments
- A Task can have multiple Activity entries

---

## API Documentation

When the backend application is running, Swagger/OpenAPI documentation can be used to inspect and test available REST endpoints.

The API includes endpoints for:

- Authentication
- Users
- Administration
- Boards
- Tasks
- Labels
- Comments
- Notifications
- Task Activity

---

## Project Status

Core backend and frontend functionality is implemented.

Completed functionality includes:

- Authentication
- Board management
- Kanban task management
- Collaboration
- Task priorities
- Labels
- Advanced filters
- Comments
- Notifications
- Email deadline reminders
- Activity history
- Dashboard
- User profile
- Administration
- Access control

Final visual UI/CSS polish and deployment may be handled separately from the core functional implementation.

---

## Author

**Artiom Prilepschi**
