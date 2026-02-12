-- Insertion des dépôts (Sites de stockage)
INSERT INTO depots (nom, code, adresse, responsable, capacite, type_entreposage, horaires_ouverture, actif) VALUES
('Entrepôt Central - Antananarivo', 'DEP-TNR-01', 'Ankorondrano, Antananarivo', 'Rivo Rakoto', 5000, 'Général', '08:00 - 17:00', TRUE),
('Dépôt Régional - Toamasina', 'DEP-TAM-02', 'Port de Toamasina', 'Faly Tam', 3000, 'Vrac et Conteneurs', '07:30 - 18:00', TRUE),
('Point de Vente - Majunga', 'DEP-MJN-03', 'Bord de mer, Majunga', 'Lalao Soa', 500, 'Produits finis', '09:00 - 19:00', TRUE);

-- Insertion des emplacements pour l'Entrepôt Central (Hiérarchie)
-- Note: On utilise des sous-requêtes pour récupérer les IDs des types d'emplacement définis dans base.sql

-- 1. Les Zones (Niveau 1)
INSERT INTO emplacements (depot_id, type_id, code, description, capacite) VALUES
((SELECT id FROM depots WHERE code = 'DEP-TNR-01'), (SELECT id FROM types_emplacement WHERE libelle = 'ZONE'), 'ZONE-SEC', 'Zone de stockage produits secs', 2000),
((SELECT id FROM depots WHERE code = 'DEP-TNR-01'), (SELECT id FROM types_emplacement WHERE libelle = 'ZONE'), 'ZONE-FRIGO', 'Zone de stockage réfrigérée', 500);

-- 2. Les Allées dans la Zone Sec (Niveau 2)
INSERT INTO emplacements (depot_id, parent_id, type_id, code, description) VALUES
((SELECT id FROM depots WHERE code = 'DEP-TNR-01'), (SELECT id FROM emplacements WHERE code = 'ZONE-SEC'), (SELECT id FROM types_emplacement WHERE libelle = 'ALLEE'), 'ALLEE-A', 'Allée principale A'),
((SELECT id FROM depots WHERE code = 'DEP-TNR-01'), (SELECT id FROM emplacements WHERE code = 'ZONE-SEC'), (SELECT id FROM types_emplacement WHERE libelle = 'ALLEE'), 'ALLEE-B', 'Allée secondaire B');

-- 3. Les Rayonnages dans l'Allée A (Niveau 3)
INSERT INTO emplacements (depot_id, parent_id, type_id, code, description) VALUES
((SELECT id FROM depots WHERE code = 'DEP-TNR-01'), (SELECT id FROM emplacements WHERE code = 'ALLEE-A'), (SELECT id FROM types_emplacement WHERE libelle = 'RAYONNAGE'), 'RAY-A1', 'Rayonnage A1'),
((SELECT id FROM depots WHERE code = 'DEP-TNR-01'), (SELECT id FROM emplacements WHERE code = 'ALLEE-A'), (SELECT id FROM types_emplacement WHERE libelle = 'RAYONNAGE'), 'RAY-A2', 'Rayonnage A2');

-- 4. Les Niveaux dans le Rayonnage A1 (Niveau 4)
INSERT INTO emplacements (depot_id, parent_id, type_id, code, description) VALUES
((SELECT id FROM depots WHERE code = 'DEP-TNR-01'), (SELECT id FROM emplacements WHERE code = 'RAY-A1'), (SELECT id FROM types_emplacement WHERE libelle = 'NIVEAU'), 'NIV-01', 'Niveau sol'),
((SELECT id FROM depots WHERE code = 'DEP-TNR-01'), (SELECT id FROM emplacements WHERE code = 'RAY-A1'), (SELECT id FROM types_emplacement WHERE libelle = 'NIVEAU'), 'NIV-02', 'Premier étage');

-- 5. Les Casiers finaux dans le Niveau 01 (Niveau 5)
INSERT INTO emplacements (depot_id, parent_id, type_id, code, description, capacite) VALUES
((SELECT id FROM depots WHERE code = 'DEP-TNR-01'), (SELECT id FROM emplacements WHERE code = 'NIV-01' AND parent_id = (SELECT id FROM emplacements WHERE code = 'RAY-A1')), (SELECT id FROM types_emplacement WHERE libelle = 'CASIER'), 'CAS-A1-01-01', 'Bac de rangement 01', 50),
((SELECT id FROM depots WHERE code = 'DEP-TNR-01'), (SELECT id FROM emplacements WHERE code = 'NIV-01' AND parent_id = (SELECT id FROM emplacements WHERE code = 'RAY-A1')), (SELECT id FROM types_emplacement WHERE libelle = 'CASIER'), 'CAS-A1-01-02', 'Bac de rangement 02', 50);
