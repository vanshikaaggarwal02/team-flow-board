# TeamFlow — Product overview

## At a glance

TeamFlow gives a small team one shared place to plan projects and see work move from idea to completion. Members can organize tasks on a board, coordinate ownership, and review progress without needing a separate spreadsheet for status tracking.

**Audience:** small product, design, and engineering teams that need lightweight project coordination.

**Format:** browser-based project management app, available as a responsive single-page application.

## The problem it addresses

When work is tracked across conversations, personal task lists, and spreadsheets, it is hard to answer basic questions: What is in progress? Who owns the next step? What is blocked in review? Is the project moving forward? TeamFlow brings those answers into each project workspace.

## Main features

### Projects and teammates

- Create projects with a name and description.
- Add existing TeamFlow users to a project and assign member or admin roles.
- The person who creates the project becomes its owner.
- Owners and admins can manage membership. Ownership transfer and project deletion are not part of this MVP.

### Tasks and board

- Create tasks with a title, description, priority, assignee, and optional due date.
- Assign a task only to a member of that project.
- Move work between **To do**, **In progress**, **In review**, and **Done** using the board or status selector.
- Search, filter, sort, and browse tasks in a list view.
- Open a task to see its details and a readable activity history.

### Project overview and analytics

- See current task totals by status, completion, upcoming due dates, overdue work, and recently updated tasks.
- Review task creation and completion trends over a chosen date range.
- Break work down by status, priority, and top assignees.
- The completion trend counts a task once in the selected range, even if it is moved out of Done and back into Done during that range.

### Account and experience

- Register, sign in, and sign out.
- Update your name, email, and password in profile settings.
- Switch between light and dark themes.
- Use the app on desktop or a mobile-sized screen.

## Typical workflow

1. A teammate registers and creates a project.
2. The project owner adds the other people who will work on it.
3. The team creates tasks, sets priority and due dates, and assigns each task to a project member.
4. Members move tasks across the board as work progresses.
5. The team uses the overview and analytics views to review current state and recent delivery.

## Roles in plain language

| Role | What it can do |
|---|---|
| Owner | Project creator; can edit project details and manage members and roles. |
| Admin | Can manage members and roles. |
| Member | Can view project work and create or update tasks. |

All project and task data is scoped to project membership. A logged-in user who is not a member cannot read that project through the API.

## Demo and current boundaries

The portfolio deployment is intended to use sample data only. The local demo dataset has 100 reusable demo accounts, five sample projects owned by `disha1@gmail.com`, five people per project, and three tasks per person per project. Some tasks are To do, In progress, In review, and Done.

The bell icon currently shows a simple “caught up” message. There are no real-time notifications, notification preferences, or email delivery. The MVP also has no comments, file attachments, project deletion, ownership transfer, or read-only guest role.

## Portfolio summary

> Built TeamFlow, a full-stack project and task management app with a responsive React interface, a Spring Boot REST API, and PostgreSQL persistence. Implemented project-scoped access control, Kanban task workflows, task audit history, member management, profile settings, and project analytics. Packaged the backend with Docker and prepared a Render + Neon deployment path.

Adjust the wording to match what you personally implemented, and add the live demo URL after deployment is complete.
