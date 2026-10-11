CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(254) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE projects (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(2000),
  owner_id BIGINT NOT NULL REFERENCES users(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE project_members (
  project_id BIGINT NOT NULL REFERENCES projects(id),
  user_id BIGINT NOT NULL REFERENCES users(id),
  role VARCHAR(20) NOT NULL CHECK (role IN ('OWNER','ADMIN','MEMBER')),
  joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY(project_id, user_id)
);
CREATE INDEX ix_project_members_user ON project_members(user_id, project_id);
CREATE TABLE tasks (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT NOT NULL REFERENCES projects(id),
  title VARCHAR(180) NOT NULL,
  description VARCHAR(5000),
  status VARCHAR(20) NOT NULL CHECK (status IN ('TODO','IN_PROGRESS','IN_REVIEW','DONE')),
  priority VARCHAR(20) NOT NULL CHECK (priority IN ('LOW','MEDIUM','HIGH','URGENT')),
  assignee_id BIGINT REFERENCES users(id),
  created_by BIGINT NOT NULL REFERENCES users(id),
  due_date DATE,
  version BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX ix_tasks_project_status ON tasks(project_id,status,updated_at DESC);
CREATE INDEX ix_tasks_project_priority ON tasks(project_id,priority);
CREATE INDEX ix_tasks_assignee ON tasks(project_id,assignee_id);
CREATE TABLE audit_logs (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT NOT NULL REFERENCES projects(id),
  task_id BIGINT NOT NULL REFERENCES tasks(id),
  actor_id BIGINT NOT NULL REFERENCES users(id),
  action VARCHAR(40) NOT NULL,
  old_value TEXT,
  new_value TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX ix_audit_task_created ON audit_logs(task_id,created_at DESC);
