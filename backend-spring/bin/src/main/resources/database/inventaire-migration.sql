-- ==========================================================
-- Migration Inventaire (Option A: emplacement obligatoire)
-- ==========================================================

-- Ajout emplacement sur lignes d'inventaire
ALTER TABLE lignes_inventaires
    ADD COLUMN IF NOT EXISTS emplacement_id INTEGER REFERENCES emplacements(id);

-- Ajout emplacement sur ajustements (pour appliquer l'ajustement au bon emplacement)
ALTER TABLE ajustements_stock
    ADD COLUMN IF NOT EXISTS emplacement_id INTEGER REFERENCES emplacements(id);

-- Ajout lot sur ajustements (obligatoire si article traçable)
ALTER TABLE ajustements_stock
    ADD COLUMN IF NOT EXISTS lot_id INTEGER REFERENCES lots(id);
