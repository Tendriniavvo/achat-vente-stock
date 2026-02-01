BEGIN;

-- =========================
-- 1. Catégories
-- =========================
INSERT INTO categories_articles (nom, description) VALUES
('Informatique', 'Matériel informatique'),
('Bureautique', 'Fournitures de bureau'),
('Électronique', 'Équipements électroniques'),
('Alimentaire', 'Produits alimentaires')
ON CONFLICT DO NOTHING;

-- =========================
-- 2. Unités
-- =========================
INSERT INTO unites (nom, symbole) VALUES
('Pièce', 'pc'),
('Kilogramme', 'kg'),
('Litre', 'L')
ON CONFLICT DO NOTHING;

-- =========================
-- 3. Taxes
-- =========================
INSERT INTO taxes (nom, taux, date_debut) VALUES
('TVA 20%', 20.00, CURRENT_TIMESTAMP),
('TVA 0%', 0.00, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- =========================
-- 4. Articles
-- =========================
INSERT INTO articles (
    code, nom, description,
    categorie_id, unite_id, taxe_id,
    prix_achat, prix_vente,
    methode_valorisation,
    stock_min, stock_max,
    traceable_lot, actif
)
VALUES
-- Informatique
('ART-100', 'Laptop Dell Latitude',
 'Dell i5 / 16Go / SSD 512Go',
 (SELECT id FROM categories_articles WHERE nom='Informatique'),
 (SELECT id FROM unites WHERE nom='Pièce'),
 (SELECT id FROM taxes WHERE nom='TVA 20%'),
 900000, 1150000, 'FIFO', 3, 30, FALSE, TRUE),

('ART-101', 'Imprimante HP LaserJet',
 'Imprimante laser réseau',
 (SELECT id FROM categories_articles WHERE nom='Informatique'),
 (SELECT id FROM unites WHERE nom='Pièce'),
 (SELECT id FROM taxes WHERE nom='TVA 20%'),
 800000, 980000, 'CUMP', 2, 15, FALSE, TRUE),

-- Bureautique
('ART-105', 'Ramette papier A4',
 '500 feuilles 80g',
 (SELECT id FROM categories_articles WHERE nom='Bureautique'),
 (SELECT id FROM unites WHERE nom='Pièce'),
 (SELECT id FROM taxes WHERE nom='TVA 20%'),
 18000, 25000, 'CUMP', 30, 600, FALSE, TRUE),

-- Électronique
('ART-108', 'TV LED 43 pouces',
 'Smart TV Full HD',
 (SELECT id FROM categories_articles WHERE nom='Électronique'),
 (SELECT id FROM unites WHERE nom='Pièce'),
 (SELECT id FROM taxes WHERE nom='TVA 20%'),
 750000, 950000, 'FIFO', 2, 25, FALSE, TRUE),

-- Alimentaire (traçable)
('ART-111', 'Riz parfumé 5kg',
 'Sac riz importé 5kg',
 (SELECT id FROM categories_articles WHERE nom='Alimentaire'),
 (SELECT id FROM unites WHERE nom='Kilogramme'),
 (SELECT id FROM taxes WHERE nom='TVA 0%'),
 32000, 42000, 'CUMP', 20, 200, TRUE, TRUE),

('ART-112', 'Huile végétale 1L',
 'Huile alimentaire',
 (SELECT id FROM categories_articles WHERE nom='Alimentaire'),
 (SELECT id FROM unites WHERE nom='Litre'),
 (SELECT id FROM taxes WHERE nom='TVA 0%'),
 6500, 9500, 'CUMP', 50, 400, TRUE, TRUE);

-- =========================
-- 5. Dépôt
-- =========================
INSERT INTO depots (
    nom, code, adresse,
    responsable, capacite,
    type_entreposage, actif
)
VALUES
('Dépôt Central Antananarivo', 'DEP-TNR',
 'Zone industrielle Forello',
 'Rakoto Jean', 50000,
 'Mixte', TRUE);

-- =========================
-- 6. Emplacements (hiérarchie)
-- =========================

-- ZONE
INSERT INTO emplacements (depot_id, type_id, code, description)
SELECT d.id, t.id, 'ZONE-C', 'Zone principale'
FROM depots d, types_emplacement t
WHERE d.code='DEP-TNR' AND t.libelle='ZONE';

-- ALLÉE
INSERT INTO emplacements (depot_id, parent_id, type_id, code)
SELECT d.id, z.id, t.id, 'A-01'
FROM depots d
JOIN emplacements z ON z.code='ZONE-C'
JOIN types_emplacement t ON t.libelle='ALLEE'
WHERE d.code='DEP-TNR';

-- RAYONNAGE
INSERT INTO emplacements (depot_id, parent_id, type_id, code)
SELECT d.id, a.id, t.id, 'R-01'
FROM depots d
JOIN emplacements a ON a.code='A-01'
JOIN types_emplacement t ON t.libelle='RAYONNAGE'
WHERE d.code='DEP-TNR';

-- NIVEAU
INSERT INTO emplacements (depot_id, parent_id, type_id, code)
SELECT d.id, r.id, t.id, 'N-01'
FROM depots d
JOIN emplacements r ON r.code='R-01'
JOIN types_emplacement t ON t.libelle='NIVEAU'
WHERE d.code='DEP-TNR';

-- CASIER
INSERT INTO emplacements (depot_id, parent_id, type_id, code, capacite)
SELECT d.id, n.id, t.id, 'C-01', 1000
FROM depots d
JOIN emplacements n ON n.code='N-01'
JOIN types_emplacement t ON t.libelle='CASIER'
WHERE d.code='DEP-TNR';

-- =========================
-- 7. Lots (articles traçables)
-- =========================
INSERT INTO lots (
    article_id, numero_lot,
    date_entree, date_expiration,
    quantite, conforme
)
VALUES
(
 (SELECT id FROM articles WHERE code='ART-111'),
 'LOT-RIZ-2026-01',
 CURRENT_DATE,
 CURRENT_DATE + INTERVAL '12 months',
 150, TRUE
),

(
 (SELECT id FROM articles WHERE code='ART-112'),
 'LOT-HUILE-2026-01',
 CURRENT_DATE,
 CURRENT_DATE + INTERVAL '18 months',
 300, TRUE
),
(
 (SELECT id FROM articles WHERE code='ART-100'),
 'LOT-ORDI-2026-01',
 CURRENT_DATE,
 CURRENT_DATE + INTERVAL '12 months',
 150, TRUE
),
(
 (SELECT id FROM articles WHERE code='ART-101'),
 'LOT-IMPRIMANTE-2026-01',
 CURRENT_DATE,
 CURRENT_DATE + INTERVAL '12 months',
 150, TRUE
);

COMMIT;
