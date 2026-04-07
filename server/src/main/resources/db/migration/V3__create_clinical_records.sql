CREATE TABLE clinical_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    chief_complaint TEXT,
    family_history TEXT,
    pathologies TEXT,
    intolerances TEXT,
    allergies TEXT,
    medications TEXT,
    supplementation TEXT,
    bowel_habits TEXT,
    sleep_pattern TEXT,
    physical_activity TEXT,
    water_intake TEXT,
    food_preferences TEXT,
    food_aversions TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE clinical_evolutions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    nutritionist_id UUID NOT NULL REFERENCES users(id),
    date DATE NOT NULL,
    general_notes TEXT,
    plan_adherence TEXT,
    complications TEXT,
    reported_symptoms TEXT,
    adjustments TEXT,
    recommendations TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_clinical_records_patient ON clinical_records(patient_id);
CREATE INDEX idx_clinical_evolutions_patient ON clinical_evolutions(patient_id);
