create table if not exists chore_templates (
    id SERIAL PRIMARY KEY,
    family_id uuid references families(family_id) on delete cascade not null,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    recurring varchar(20) not null,
    is_active BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP     
);

create table if not exists chores (
    id SERIAL PRIMARY KEY,
    chore_id integer references chore_templates(id) on delete cascade not null,
    due_date Date,
    date_completed Date,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table if not exists user_chore (
    user_id uuid references auth.users(id) on delete cascade not null,
    chore_id integer references chores(id) on delete cascade not null,
    primary key (user_id, chore_id)
);

create index if not exists idx_chores_due_date on chores(due_date);
create index if not exists idx_chores_date_completed on chores(date_completed);
create index if not exists idx_family_chores on chore_templates(family_id);

alter table chores enable row level security;
alter table chore_templates enable row level security;
alter table user_chore enable row level security;