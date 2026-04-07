CREATE TYPE plan_status AS ENUM ('draft', 'active', 'inactive', 'archived', 'replaced');

CREATE TABLE meal_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    nutritionist_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    objective TEXT,
    status plan_status NOT NULL DEFAULT 'draft',
    start_date DATE,
    end_date DATE,
    general_notes TEXT,
    goal_calories DECIMAL(8,2),
    goal_protein_g DECIMAL(8,2),
    goal_carbs_g DECIMAL(8,2),
    goal_fat_g DECIMAL(8,2),
    goal_fiber_g DECIMAL(8,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE diet_variations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_id UUID NOT NULL REFERENCES meal_plans(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT false,
    is_patient_accessible BOOLEAN NOT NULL DEFAULT true,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE meals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    variation_id UUID NOT NULL REFERENCES diet_variations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    suggested_time TIME,
    sort_order INT NOT NULL DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE meal_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meal_id UUID NOT NULL REFERENCES meals(id) ON DELETE CASCADE,
    food_id UUID REFERENCES foods(id),
    food_name VARCHAR(500) NOT NULL,
    quantity_grams DECIMAL(8,2),
    household_measure VARCHAR(100),
    is_ad_libitum BOOLEAN NOT NULL DEFAULT false,
    notes TEXT,
    calories DECIMAL(8,2),
    protein_g DECIMAL(8,2),
    carbs_g DECIMAL(8,2),
    fat_g DECIMAL(8,2),
    fiber_g DECIMAL(8,2),
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE authorized_substitutions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meal_item_id UUID NOT NULL REFERENCES meal_items(id) ON DELETE CASCADE,
    food_id UUID REFERENCES foods(id),
    food_name VARCHAR(500) NOT NULL,
    quantity_grams DECIMAL(8,2),
    household_measure VARCHAR(100),
    calories DECIMAL(8,2),
    protein_g DECIMAL(8,2),
    carbs_g DECIMAL(8,2),
    fat_g DECIMAL(8,2),
    notes TEXT
);

CREATE INDEX idx_plans_patient ON meal_plans(patient_id);
CREATE INDEX idx_plans_status ON meal_plans(patient_id, status);
CREATE INDEX idx_variations_plan ON diet_variations(plan_id);
CREATE INDEX idx_meals_variation ON meals(variation_id);
CREATE INDEX idx_items_meal ON meal_items(meal_id);
CREATE INDEX idx_subs_item ON authorized_substitutions(meal_item_id);
