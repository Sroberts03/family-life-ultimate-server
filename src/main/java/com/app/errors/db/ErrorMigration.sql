create table if not exists errors (
    id bigserial primary key,
    error_class text not null,
    error_message text not null,
    stack_trace text not null,    
    effected_user uuid references auth.users(id) on delete set null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

alter table errors enable row level security;