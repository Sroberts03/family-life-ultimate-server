create table if not exists chore_templates (
    id SERIAL PRIMARY KEY,
    family_id uuid references families(family_id) on delete cascade not null,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    recurring varchar(20) not null,
    start_date Date not null,
    end_date Date,
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

-- Function to automatically generate chores based on the recurrence string
CREATE OR REPLACE FUNCTION create_chores_from_template()
RETURNS TRIGGER AS $$
DECLARE
    v_end_date DATE;
    v_parts text[];
    v_days text[];
    v_day_of_month int;
    v_week_num int;
    v_day_of_week text;
BEGIN
    -- Default to 3 months from start_date if end_date is null
    v_end_date := COALESCE(NEW.end_date, NEW.start_date + interval '3 months');

    -- If the chore is a one-off ('O' or 'none')
    IF NEW.recurring = 'O' OR lower(NEW.recurring) = 'none' THEN
        INSERT INTO chores (chore_id, due_date) VALUES (NEW.id, NEW.start_date);
        RETURN NEW;
    END IF;

    -- Split the recurring string by colon
    v_parts := string_to_array(NEW.recurring, ':');

    IF v_parts[1] = 'D' OR lower(v_parts[1]) = 'daily' THEN
        INSERT INTO chores (chore_id, due_date)
        SELECT NEW.id, d::date
        FROM generate_series(NEW.start_date::timestamp, v_end_date::timestamp, '1 day'::interval) d;

    ELSIF v_parts[1] = 'Y' OR lower(v_parts[1]) = 'yearly' THEN
        INSERT INTO chores (chore_id, due_date)
        SELECT NEW.id, d::date
        FROM generate_series(NEW.start_date::timestamp, v_end_date::timestamp, '1 year'::interval) d;

    ELSIF v_parts[1] = 'W' THEN
        -- Format W:Mon,Wed,Fri
        v_days := string_to_array(v_parts[2], ',');
        INSERT INTO chores (chore_id, due_date)
        SELECT NEW.id, d::date
        FROM generate_series(NEW.start_date::timestamp, v_end_date::timestamp, '1 day'::interval) d
        WHERE lower(trim(to_char(d, 'Dy'))) IN (SELECT lower(trim(x)) FROM unnest(v_days) x);

    ELSIF lower(v_parts[1]) = 'weekly' THEN
        -- Fallback for simple 'weekly'
        INSERT INTO chores (chore_id, due_date)
        SELECT NEW.id, d::date
        FROM generate_series(NEW.start_date::timestamp, v_end_date::timestamp, '1 week'::interval) d;

    ELSIF v_parts[1] = 'M' THEN
        IF array_length(v_parts, 1) = 2 THEN
            -- Format M:15
            v_day_of_month := v_parts[2]::int;
            INSERT INTO chores (chore_id, due_date)
            SELECT NEW.id, d::date
            FROM generate_series(NEW.start_date::timestamp, v_end_date::timestamp, '1 day'::interval) d
            WHERE extract(day from d) = v_day_of_month;
        ELSIF array_length(v_parts, 1) = 3 THEN
            -- Format M:1:Mon
            v_week_num := v_parts[2]::int;
            v_day_of_week := v_parts[3];
            INSERT INTO chores (chore_id, due_date)
            SELECT NEW.id, d::date
            FROM generate_series(NEW.start_date::timestamp, v_end_date::timestamp, '1 day'::interval) d
            WHERE lower(trim(to_char(d, 'Dy'))) = lower(trim(v_day_of_week))
              AND floor((extract(day from d) - 1) / 7) + 1 = v_week_num;
        END IF;

    ELSIF lower(v_parts[1]) = 'monthly' THEN
        -- Fallback for simple 'monthly'
        INSERT INTO chores (chore_id, due_date)
        SELECT NEW.id, d::date
        FROM generate_series(NEW.start_date::timestamp, v_end_date::timestamp, '1 month'::interval) d;

    ELSE
        -- Fallback if pattern is unknown: just create the first chore
        INSERT INTO chores (chore_id, due_date) VALUES (NEW.id, NEW.start_date);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to execute the function after a new template is inserted
DROP TRIGGER IF EXISTS trg_chore_template_insert ON chore_templates;
CREATE TRIGGER trg_chore_template_insert
AFTER INSERT ON chore_templates
FOR EACH ROW
EXECUTE FUNCTION create_chores_from_template();