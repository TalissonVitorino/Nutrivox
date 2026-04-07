CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    sex VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL,
    primary_goal VARCHAR(255),
    dietary_restrictions TEXT,
    clinical_notes TEXT,
    ai_consent BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE nutritionist_patient_links (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nutritionist_id UUID NOT NULL REFERENCES users(id),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(nutritionist_id, patient_id)
);

CREATE TABLE patient_invites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nutritionist_id UUID NOT NULL REFERENCES users(id),
    invite_code VARCHAR(100) NOT NULL UNIQUE,
    patient_name VARCHAR(255) NOT NULL,
    patient_email VARCHAR(255),
    patient_phone VARCHAR(50),
    patient_sex VARCHAR(20) NOT NULL,
    patient_date_of_birth DATE NOT NULL,
    patient_goal VARCHAR(255),
    patient_restrictions TEXT,
    patient_notes TEXT,
    is_used BOOLEAN NOT NULL DEFAULT false,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_patients_user ON patients(user_id);
CREATE INDEX idx_np_links_nutritionist ON nutritionist_patient_links(nutritionist_id);
CREATE INDEX idx_np_links_patient ON nutritionist_patient_links(patient_id);
CREATE INDEX idx_invites_code ON patient_invites(invite_code);
