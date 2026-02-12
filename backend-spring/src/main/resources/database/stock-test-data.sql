-- ==========================================================
-- Données de test Stock + Mouvements (Option A: emplacement obligatoire)
--
-- Pré-requis :
-- 1) Exécuter base.sql
-- 2) Exécuter dept-data.sql
--
-- Ensuite exécuter ce script.
-- ==========================================================

-- ----------------------------------------------------------
-- 1) Ajustement schéma pour le nouveau MouvementStock (header + lignes)
-- ----------------------------------------------------------

ALTER TABLE mouvements_stock
    ADD COLUMN IF NOT EXISTS reference VARCHAR(50);

ALTER TABLE mouvements_stock
    ADD COLUMN IF NOT EXISTS statut VARCHAR(30) DEFAULT 'BROUILLON';

ALTER TABLE mouvements_stock
    ADD COLUMN IF NOT EXISTS depot_destination_id INTEGER REFERENCES depots(id);

ALTER TABLE mouvements_stock
    ADD COLUMN IF NOT EXISTS emplacement_destination_id INTEGER REFERENCES emplacements(id);

CREATE TABLE IF NOT EXISTS lignes_mouvements_stock (
    id SERIAL PRIMARY KEY,
    mouvement_id INTEGER NOT NULL REFERENCES mouvements_stock(id) ON DELETE CASCADE,
    article_id INTEGER REFERENCES articles(id),
    quantite INTEGER NOT NULL,
    cout_unitaire DECIMAL(10,2),
    lot_id INTEGER REFERENCES lots(id),
    emplacement_id INTEGER REFERENCES emplacements(id)
);

-- ----------------------------------------------------------
-- 2) Données de référentiel minimales (unités, taxe, catégorie)
-- ----------------------------------------------------------

INSERT INTO unites (nom, symbole) VALUES
('Piece', 'pc'),
('Kilogramme', 'kg')
ON CONFLICT (nom) DO NOTHING;

INSERT INTO taxes (nom, taux) VALUES
('TVA 20%', 20.00)
ON CONFLICT DO NOTHING;

INSERT INTO categories_articles (nom, description) VALUES
('Standard', 'Articles non traçables par lot'),
('Perissable', 'Articles traçables par lot / date')
ON CONFLICT (nom) DO NOTHING;

-- ----------------------------------------------------------
-- 3) Utilisateur de test (login)
--   Mot de passe: password
--   Hash bcrypt connu correspondant à "password"
-- ----------------------------------------------------------

INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, departement_id, actif)
SELECT 'Admin', 'System', 'admin@local.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5l5x7aYp3dWvYqKqRR3YKHyCuXapS', d.id, TRUE
FROM departements d
WHERE d.code = 'DEP-LOG'
ON CONFLICT (email) DO NOTHING;

-- Attribuer le rôle Magasinier à l'admin
INSERT INTO utilisateurs_roles (utilisateur_id, role_id)
SELECT u.id, r.id
FROM utilisateurs u, roles r
WHERE u.email='admin@local.test' AND r.nom='Magasinier'
ON CONFLICT DO NOTHING;

-- ----------------------------------------------------------
-- 4) Dépôts & emplacements (Option A: indispensables)
-- ----------------------------------------------------------

INSERT INTO depots (nom, code, adresse, capacite, actif) VALUES
('Depot Central', 'DEP-CENTRAL', 'Antananarivo', 100000, TRUE),
('Depot Secondaire', 'DEP-SECONDAIRE', 'Toamasina', 50000, TRUE)
ON CONFLICT DO NOTHING;

-- Emplacements pour Depot Central
INSERT INTO emplacements (depot_id, code, description, capacite)
SELECT d.id, 'A-01', 'Allée A - Niveau 1', 10000 FROM depots d WHERE d.nom='Depot Central'
ON CONFLICT DO NOTHING;

INSERT INTO emplacements (depot_id, code, description, capacite)
SELECT d.id, 'A-02', 'Allée A - Niveau 2', 10000 FROM depots d WHERE d.nom='Depot Central'
ON CONFLICT DO NOTHING;

-- Emplacements pour Depot Secondaire
INSERT INTO emplacements (depot_id, code, description, capacite)
SELECT d.id, 'B-01', 'Zone B - Niveau 1', 8000 FROM depots d WHERE d.nom='Depot Secondaire'
ON CONFLICT DO NOTHING;

-- ----------------------------------------------------------
-- 5) Articles
-- ----------------------------------------------------------

INSERT INTO articles (code, nom, description, categorie_id, unite_id, taxe_id, prix_achat, prix_vente, methode_valorisation, stock_min, stock_max, traceable_lot, actif)
SELECT 'ART-STD-001', 'Clavier USB', 'Article standard non traçable',
       c.id, u.id, t.id, 20000.00, 35000.00, 'CUMP', 5, 500, FALSE, TRUE
FROM categories_articles c, unites u, taxes t
WHERE c.nom='Standard' AND u.nom='Piece' AND t.nom='TVA 20%'
ON CONFLICT (code) DO NOTHING;

INSERT INTO articles (code, nom, description, categorie_id, unite_id, taxe_id, prix_achat, prix_vente, methode_valorisation, stock_min, stock_max, traceable_lot, actif)
SELECT 'ART-LOT-001', 'Yaourt 125g', 'Périssable, traçable par lot',
       c.id, u.id, t.id, 500.00, 900.00, 'CUMP', 20, 10000, TRUE, TRUE
FROM categories_articles c, unites u, taxes t
WHERE c.nom='Perissable' AND u.nom='Piece' AND t.nom='TVA 20%'
ON CONFLICT (code) DO NOTHING;

-- ----------------------------------------------------------
-- 6) Lots (OK, expiré, non conforme)
-- ----------------------------------------------------------

-- Lot OK (expiration future)
INSERT INTO lots (article_id, numero_lot, date_entree, date_expiration, quantite, conforme)
SELECT a.id, 'LOT-YAO-OK', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '30 days', 300, TRUE
FROM articles a
WHERE a.code='ART-LOT-001'
ON CONFLICT (numero_lot) DO NOTHING;

-- Lot expiré
INSERT INTO lots (article_id, numero_lot, date_entree, date_expiration, quantite, conforme)
SELECT a.id, 'LOT-YAO-EXP', CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP - INTERVAL '1 days', 100, TRUE
FROM articles a
WHERE a.code='ART-LOT-001'
ON CONFLICT (numero_lot) DO NOTHING;

-- Lot non conforme
INSERT INTO lots (article_id, numero_lot, date_entree, date_expiration, quantite, conforme)
SELECT a.id, 'LOT-YAO-NC', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '10 days', 50, FALSE
FROM articles a
WHERE a.code='ART-LOT-001'
ON CONFLICT (numero_lot) DO NOTHING;

-- ----------------------------------------------------------
-- 7) Stock initial (par dépôt + emplacement)
-- ----------------------------------------------------------

-- Stock Clavier USB : Depot Central / A-01
INSERT INTO stocks (article_id, depot_id, emplacement_id, quantite, valeur)
SELECT a.id, d.id, e.id, 40, 800000.00
FROM articles a, depots d, emplacements e
WHERE a.code='ART-STD-001' AND d.nom='Depot Central' AND e.code='A-01'
ON CONFLICT DO NOTHING;

-- Stock Yaourt : Depot Central / A-02
INSERT INTO stocks (article_id, depot_id, emplacement_id, quantite, valeur)
SELECT a.id, d.id, e.id, 200, 100000.00
FROM articles a, depots d, emplacements e
WHERE a.code='ART-LOT-001' AND d.nom='Depot Central' AND e.code='A-02'
ON CONFLICT DO NOTHING;

-- ----------------------------------------------------------
-- 8) Mouvements brouillon prêts à valider (UI)
-- ----------------------------------------------------------

-- ENTREE brouillon : +10 Clavier USB vers Depot Central / A-01
INSERT INTO mouvements_stock (reference, type, quantite, statut, date_mouvement, depot_id, emplacement_id, reference_document, utilisateur_id, motif)
SELECT 'MS-DEMO-ENT-001', 'ENTREE', 0, 'BROUILLON', CURRENT_TIMESTAMP, d.id, e.id, 'DOC-ENT-001', u.id, 'Entrée démo'
FROM depots d, emplacements e, utilisateurs u
WHERE d.nom='Depot Central' AND e.code='A-01' AND u.email='admin@local.test'
ON CONFLICT DO NOTHING;

INSERT INTO lignes_mouvements_stock (mouvement_id, article_id, quantite, cout_unitaire, lot_id, emplacement_id)
SELECT m.id, a.id, 10, 20000.00, NULL, e.id
FROM mouvements_stock m, articles a, emplacements e
WHERE m.reference='MS-DEMO-ENT-001' AND a.code='ART-STD-001' AND e.code='A-01'
ON CONFLICT DO NOTHING;

-- SORTIE brouillon : -5 Yaourt (lot OK) depuis Depot Central / A-02
INSERT INTO mouvements_stock (reference, type, quantite, statut, date_mouvement, depot_id, emplacement_id, reference_document, utilisateur_id, motif)
SELECT 'MS-DEMO-SOR-001', 'SORTIE', 0, 'BROUILLON', CURRENT_TIMESTAMP, d.id, e.id, 'DOC-SOR-001', u.id, 'Sortie démo'
FROM depots d, emplacements e, utilisateurs u
WHERE d.nom='Depot Central' AND e.code='A-02' AND u.email='admin@local.test'
ON CONFLICT DO NOTHING;

INSERT INTO lignes_mouvements_stock (mouvement_id, article_id, quantite, cout_unitaire, lot_id, emplacement_id)
SELECT m.id, a.id, 5, NULL, l.id, e.id
FROM mouvements_stock m, articles a, lots l, emplacements e
WHERE m.reference='MS-DEMO-SOR-001' AND a.code='ART-LOT-001' AND l.numero_lot='LOT-YAO-OK' AND e.code='A-02'
ON CONFLICT DO NOTHING;

-- TRANSFERT brouillon : déplacer 3 Clavier USB de Depot Central/A-01 vers Depot Secondaire/B-01
INSERT INTO mouvements_stock (reference, type, quantite, statut, date_mouvement, depot_id, emplacement_id, depot_destination_id, emplacement_destination_id, reference_document, utilisateur_id, motif)
SELECT 'MS-DEMO-TRF-001', 'TRANSFERT', 0, 'BROUILLON', CURRENT_TIMESTAMP,
       ds.id, es.id, dd.id, ed.id, 'DOC-TRF-001', u.id, 'Transfert démo'
FROM depots ds, depots dd, emplacements es, emplacements ed, utilisateurs u
WHERE ds.nom='Depot Central' AND es.code='A-01'
  AND dd.nom='Depot Secondaire' AND ed.code='B-01'
  AND u.email='admin@local.test'
ON CONFLICT DO NOTHING;

INSERT INTO lignes_mouvements_stock (mouvement_id, article_id, quantite, cout_unitaire, lot_id, emplacement_id)
SELECT m.id, a.id, 3, NULL, NULL, es.id
FROM mouvements_stock m, articles a, emplacements es
WHERE m.reference='MS-DEMO-TRF-001' AND a.code='ART-STD-001' AND es.code='A-01'
ON CONFLICT DO NOTHING;

-- Mouvement qui doit échouer à la validation (lot expiré)
INSERT INTO mouvements_stock (reference, type, quantite, statut, date_mouvement, depot_id, emplacement_id, reference_document, utilisateur_id, motif)
SELECT 'MS-DEMO-FAIL-EXP', 'SORTIE', 0, 'BROUILLON', CURRENT_TIMESTAMP, d.id, e.id, 'DOC-FAIL-001', u.id, 'Doit échouer (lot expiré)'
FROM depots d, emplacements e, utilisateurs u
WHERE d.nom='Depot Central' AND e.code='A-02' AND u.email='admin@local.test'
ON CONFLICT DO NOTHING;

INSERT INTO lignes_mouvements_stock (mouvement_id, article_id, quantite, cout_unitaire, lot_id, emplacement_id)
SELECT m.id, a.id, 1, NULL, l.id, e.id
FROM mouvements_stock m, articles a, lots l, emplacements e
WHERE m.reference='MS-DEMO-FAIL-EXP' AND a.code='ART-LOT-001' AND l.numero_lot='LOT-YAO-EXP' AND e.code='A-02'
ON CONFLICT DO NOTHING;
