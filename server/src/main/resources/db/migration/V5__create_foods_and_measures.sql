CREATE TABLE foods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(500) NOT NULL,
    category VARCHAR(100),
    calories_per_100g DECIMAL(8,2),
    protein_per_100g DECIMAL(8,2),
    carbs_per_100g DECIMAL(8,2),
    fat_per_100g DECIMAL(8,2),
    fiber_per_100g DECIMAL(8,2),
    sodium_per_100g DECIMAL(8,2),
    calcium_mg DECIMAL(8,2),
    iron_mg DECIMAL(8,2),
    magnesium_mg DECIMAL(8,2),
    potassium_mg DECIMAL(8,2),
    zinc_mg DECIMAL(8,2),
    vitamin_a_mcg DECIMAL(8,2),
    vitamin_c_mg DECIMAL(8,2),
    vitamin_d_mcg DECIMAL(8,2),
    source VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE household_measures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    food_id UUID NOT NULL REFERENCES foods(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    grams DECIMAL(8,2) NOT NULL
);

CREATE INDEX idx_foods_name ON foods USING gin(to_tsvector('portuguese', name));
CREATE INDEX idx_foods_category ON foods(category);
CREATE INDEX idx_household_food ON household_measures(food_id);
