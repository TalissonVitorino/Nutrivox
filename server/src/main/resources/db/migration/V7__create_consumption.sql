CREATE TYPE consumption_mode AS ENUM ('full', 'partial', 'with_substitution', 'off_plan');
CREATE TYPE substitution_origin AS ENUM ('authorized', 'ai_suggestion');

CREATE TABLE consumption_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    meal_id UUID REFERENCES meals(id),
    variation_id UUID REFERENCES diet_variations(id),
    date DATE NOT NULL,
    time TIME,
    mode consumption_mode NOT NULL,
    notes TEXT,
    photo_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE consumption_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    record_id UUID NOT NULL REFERENCES consumption_records(id) ON DELETE CASCADE,
    meal_item_id UUID REFERENCES meal_items(id),
    food_id UUID REFERENCES foods(id),
    food_name VARCHAR(500) NOT NULL,
    quantity_grams DECIMAL(8,2),
    household_measure VARCHAR(100),
    was_consumed BOOLEAN NOT NULL DEFAULT true,
    is_substitution BOOLEAN NOT NULL DEFAULT false,
    substitution_origin substitution_origin,
    original_food_name VARCHAR(500),
    is_off_plan BOOLEAN NOT NULL DEFAULT false,
    calories DECIMAL(8,2),
    protein_g DECIMAL(8,2),
    carbs_g DECIMAL(8,2),
    fat_g DECIMAL(8,2),
    fiber_g DECIMAL(8,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_consumption_patient_date ON consumption_records(patient_id, date);
CREATE INDEX idx_consumption_items_record ON consumption_items(record_id);
