create type meal_type as enum ('breakfast', 'lunch', 'dinner', 'snack', 'other');

create table if not exists recipe_books (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table if not exists family_recipe_book (
    family_id UUID REFERENCES families(family_id),
    recipe_book_id INT REFERENCES recipe_books(id),
    PRIMARY KEY (family_id, recipe_book_id)
);

create table if not exists recipes (
    id SERIAL PRIMARY KEY,
    recipe_book_id INT REFERENCES recipe_books(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    prep_time INT,
    cook_time INT,
    servings INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table if not exists recipe_ingredients (
    id SERIAL PRIMARY KEY,
    recipe_id INT REFERENCES recipes(id),
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table if not exists recipe_steps (
    id SERIAL PRIMARY KEY,
    recipe_id INT REFERENCES recipes(id),
    step_order INT NOT NULL,
    step_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table if not exists shopping_list_items (
    id SERIAL PRIMARY KEY,
    family_id UUID REFERENCES families(family_id),
    quantity int NOT NULL,
    unit VARCHAR(255) NOT NULL,
    item VARCHAR(255) NOT NULL,
    purchased boolean DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table if not exists meal_plan_item (
    id SERIAL PRIMARY KEY,
    family_id UUID REFERENCES families(family_id),
    recipe_id int REFERENCES recipes(id),
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    meal_type meal_type NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX shopping_list_items_family_id_idx ON shopping_list_items(family_id);
CREATE INDEX meal_plan_item_family_id_idx ON meal_plan_item(family_id);
CREATE INDEX meal_plan_item_date_and_family_id ON meal_plan_item(date, family_id);
CREATE INDEX meal_plan_item_date_and_meal_type ON meal_plan_item(date, meal_type);
CREATE INDEX recipe_steps_recipe_id_idx ON recipe_steps(recipe_id);
CREATE INDEX recipe_ingredients_recipe_id_idx ON recipe_ingredients(recipe_id);
