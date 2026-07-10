--July 9, 2026
create type subscription_level as enum ('basic', 'premium');
create type family_role as enum ('parent', 'child', 'other');

create table if not exists families (
    family_id uuid primary key default gen_random_uuid(),
    owner_id uuid references auth.users(id) on delete cascade not null,
    subscription_level subscription_level not null default 'basic',
    created_at timestamp with time zone default timezone('utc'::text, now()) not null,
    updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

create table authorized_edit_family_users (
    user_id uuid references auth.users(id) on delete cascade not null,
    family_id uuid references families(family_id) on delete cascade not null,
    primary key (user_id, family_id) 
);

create table if not exists user_families (
    user_id uuid references auth.users(id) on delete cascade not null,
    family_id uuid references families(family_id) on delete cascade not null,
    family_role family_role not null,
    primary key (user_id, family_id)
);

create table if not exists join_family_requests (
    request_id SERIAL PRIMARY KEY,
    user_id uuid references auth.users(id) on delete cascade not null,
    family_id uuid references families(family_id) on delete cascade not null,
    family_role family_role not null,
    accepted boolean default null,
    created_at timestamp with time zone default timezone('utc'::text, now()) not null,
    updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

alter table user_families enable row level security;
alter table families enable row level security;
alter table authorized_edit_family_users enable row level security;
alter table join_family_requests enable row level security;