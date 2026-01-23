INSERT INTO departements (code, nom, description, actif) VALUES
('DEP-ADM', 'Administration', 'Gestion administrative et organisationnelle', TRUE),
('DEP-FIN', 'Finance', 'Gestion financière, comptabilité et budget', TRUE),
('DEP-IT', 'Informatique', 'Développement, maintenance et systèmes informatiques', TRUE),
('DEP-RH', 'Ressources Humaines', 'Gestion du personnel et recrutement', TRUE),
('DEP-MKT', 'Marketing', 'Communication, publicité et études de marché', TRUE),
('DEP-LOG', 'Logistique', 'Transport, stockage et distribution', TRUE),
('DEP-PRD', 'Production', 'Gestion des opérations et de la production', TRUE),
('DEP-QA', 'Qualité', 'Contrôle qualité et amélioration continue', FALSE);




INSERT INTO roles (nom, description) VALUES
('Administrateur', 'Accès complet au système et gestion des utilisateurs'),
('Acheteur', 'Responsable des achats et des fournisseurs'),
('Magasinier', 'Gestion du stock et des entrées/sorties'),
('Commercial', 'Gestion des ventes et relation clients'),
('Comptable', 'Gestion comptable et financière'),
('Responsable Logistique', 'Supervision du transport et de la distribution'),
('Responsable RH', 'Gestion du personnel et des ressources humaines'),
('Utilisateur', 'Accès standard aux fonctionnalités du système');



INSERT INTO permissions (role_id, module, action, perimetre) VALUES

-- 🔑 ADMINISTRATEUR : tout
((SELECT id FROM roles WHERE nom = 'Administrateur'), 'Utilisateurs', 'creer', NULL),
((SELECT id FROM roles WHERE nom = 'Administrateur'), 'Utilisateurs', 'modifier', NULL),
((SELECT id FROM roles WHERE nom = 'Administrateur'), 'Utilisateurs', 'supprimer', NULL),
((SELECT id FROM roles WHERE nom = 'Administrateur'), 'Systeme', 'configurer', NULL),

-- 🛒 ACHETEUR
((SELECT id FROM roles WHERE nom = 'Acheteur'), 'Achats', 'creer', NULL),
((SELECT id FROM roles WHERE nom = 'Acheteur'), 'Achats', 'modifier', NULL),
((SELECT id FROM roles WHERE nom = 'Acheteur'), 'Achats', 'consulter', NULL),

-- 📦 MAGASINIER
((SELECT id FROM roles WHERE nom = 'Magasinier'), 'Stocks', 'consulter', NULL),
((SELECT id FROM roles WHERE nom = 'Magasinier'), 'Stocks', 'modifier', 'quantite>=0'),
((SELECT id FROM roles WHERE nom = 'Magasinier'), 'Stocks', 'valider', NULL),

-- 💼 COMMERCIAL
((SELECT id FROM roles WHERE nom = 'Commercial'), 'Ventes', 'creer', NULL),
((SELECT id FROM roles WHERE nom = 'Commercial'), 'Ventes', 'modifier', NULL),
((SELECT id FROM roles WHERE nom = 'Commercial'), 'Clients', 'consulter', NULL),

-- 💰 COMPTABLE
((SELECT id FROM roles WHERE nom = 'Comptable'), 'Finances', 'consulter', NULL),
((SELECT id FROM roles WHERE nom = 'Comptable'), 'Finances', 'valider', 'montant < 10000');


