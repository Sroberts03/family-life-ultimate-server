-- July 10, 2026
create table if not exists activities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

create table if not exists pers_activities (
    id SERIAL PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES auth.users(id),
    activity_id INTEGER NOT NULL REFERENCES activities(id),
    family_id uuid NOT NULL REFERENCES families(family_id) on delete cascade,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

alter table activities enable row level security;
alter table pers_activities enable row level security;

insert into activities (name, description) values 
('family_owner', 
    'User is the owner of the family'
);

insert into activities (name, description) values 
('auth_family_user', 
    'User is authorized to accept or deny join requests for a family as well as view and edit all family information'
);

insert into activities (name, description) values
('view_budget','can view the family budget');

insert into activities (name, description) values
('edit_budget','can edit the family budget');

insert into activities (name, description) values
('edit_meals','can edit the family meal plan');

insert into activities (name, description) values
('edit_chores','can edit the family chores');

ALTER TABLE pers_activities ADD CONSTRAINT pers_activities_unique UNIQUE (user_id, family_id, activity_id);

