CREATE TABLE assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    nutritionist_id UUID NOT NULL REFERENCES users(id),
    date DATE NOT NULL,
    assessment_type VARCHAR(50) NOT NULL DEFAULT 'in-person',
    weight_kg DECIMAL(6,2),
    height_cm DECIMAL(6,2),
    bmi DECIMAL(5,2),
    waist_cm DECIMAL(6,2),
    hip_cm DECIMAL(6,2),
    abdomen_cm DECIMAL(6,2),
    body_fat_pct DECIMAL(5,2),
    muscle_mass_kg DECIMAL(6,2),
    body_water_pct DECIMAL(5,2),
    clinical_notes TEXT,
    is_draft BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_assessments_patient ON assessments(patient_id);
CREATE INDEX idx_assessments_date ON assessments(patient_id, date);
