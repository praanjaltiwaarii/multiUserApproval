CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    creator_id INTEGER REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id SERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    author_id INTEGER REFERENCES users(id),
    task_id INTEGER REFERENCES tasks(id)
);

CREATE TABLE IF NOT EXISTS task_approvers (
    task_id INTEGER REFERENCES tasks(id),
    user_id INTEGER REFERENCES users(id),
    PRIMARY KEY (task_id, user_id)
);

CREATE TABLE IF NOT EXISTS approvals (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    approver_id BIGINT NOT NULL,
    approved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (approver_id) REFERENCES users(id)
);

CREATE INDEX idx_task_approver ON approvals(task_id, approver_id);