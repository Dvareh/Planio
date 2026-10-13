# Planio

Planio is a full-stack task and project management application built with Spring Boot and Angular.

It helps users organize work on shared boards, manage tasks, track deadlines, and collaborate through comments and task activity history.

**[Open live application](https://alekphoto.com/)**

## Versions

- **`main`** — the functional application with a basic interface.
- **`server-deploy`** — the deployed version with the styled interface.

## Features

### Boards and collaboration

- Create, edit, and delete boards.
- Use board keys and numbered task identifiers.
- Add existing users to boards by email.
- Remove participants or leave a board.
- Restrict board settings to the owner.
- Automatically unassign tasks when their assignee leaves or is removed.

### Task management

- Organize tasks on a Kanban board.
- Set task status, priority, assignee, and deadline.
- Create board labels and attach them to tasks.
- Add comments and edit or delete your own comments.
- Review task activity history.
- View assigned tasks and deadlines on the dashboard.

### Accounts and access

- Register and log in using lowercase email addresses.
- Authenticate API requests with JWT.
- Update your profile and change your password.
- Access boards and tasks as an owner or participant.
- List users and change their roles through the administration API.

### Deadline reminders

The application checks deadlines daily at **09:00 Europe/Warsaw**.

Assigned tasks can generate email and in-app reminders **3 days and 1 day before their deadline**. Completed and cancelled tasks are excluded.

The backend must be running at the scheduled time. Missed runs are not automatically replayed after a restart.

## Tech stack

| Area | Technologies |
| --- | --- |
| Frontend | Angular 22, TypeScript, Angular CDK, RxJS |
| Backend | Java 21, Spring Boot 4, Spring Security, Spring Data JPA |
| Database | PostgreSQL |
| Authentication | JWT, BCrypt |
| Email | Spring Mail, SMTP |
| API documentation | Springdoc OpenAPI, Swagger UI |
| Deployment | Ubuntu, nginx, systemd, HTTPS |

## Getting started

### Requirements

- Java 21
- PostgreSQL
- Node.js compatible with Angular 22
- npm
- Gmail credentials with an App Password for email reminders

The Maven Wrapper is included.

### 1. Clone the repository

```powershell
git clone https://github.com/Dvareh/Planio.git
cd Planio
git switch main
```

### 2. Create a PostgreSQL database

Connect to PostgreSQL as an administrator and run:

```sql
CREATE USER planio WITH PASSWORD 'replace-with-your-local-password';
CREATE DATABASE planio OWNER planio;
```

### 3. Configure the backend

Copy the configuration template:

```powershell
Copy-Item backend/src/main/resources/application.properties.template backend/src/main/resources/application.properties
```

Replace the contents of the application.properties.template with your data and rename it to application.properties or copy to application.properties prepared template

For Gmail, `spring.mail.password` must contain an App Password.

`app.mail.from` specifies the sender address. In this configuration, it uses the Gmail address from `spring.mail.username`.


`jwt.expiration` is measured in milliseconds. The example uses a token lifetime of 24 hours.

For this local setup, Hibernate creates and updates tables through `ddl-auto=update`. You do not need to execute a separate table creation script.

### 4. Start the backend

From the repository root:

```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.jvmArguments=-Duser.timezone=Europe/Warsaw"
```

Don't forget to change timezone if needed

The backend runs at **http://localhost:8080**.

### 5. Start the frontend

Open a second terminal at the repository root:

```powershell
cd frontend
npm ci
npm start
```

Open **http://localhost:4200**.

The local API URL is configured in:

`frontend/src/app/environments/environment.ts`

```typescript
export const environment = {
  apiUrl: 'http://localhost:8080'
};
```

Use `localhost:4200` for local development, as this origin is allowed by the backend's CORS configuration.

### 6. Try the application

1. Register an account using a lowercase email address.
2. Log in and create a board.
3. Create tasks and set their priorities, assignees, and deadlines.
4. Add another registered user to collaborate.

## API documentation

With the backend running locally:

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI specification:** http://localhost:8080/v3/api-docs

Protected endpoints require a JWT obtained through login.

## Project structure

```text
backend/
  src/main/java/          Backend application
  src/main/resources/     Configuration template and resources
  src/test/               Backend tests

frontend/
  src/app/                Pages, components, services, and routing
  public/                 Static assets
```

## Author

Artiom Prilepschi

## License

This project is licensed under the [MIT License](LICENSE).