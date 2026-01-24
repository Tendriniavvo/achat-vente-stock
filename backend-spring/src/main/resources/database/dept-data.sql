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
('Demandeur', 'Création et suivi des demandes d''achat (DA)'),
('Approbateur', 'Approbation unique des demandes d''achat'),
('Acheteur', 'Transformation des DA en BC, négociation et gestion des fournisseurs'),
('Responsable Achats', 'Validation des BC, déblocage des litiges et supervision des achats'),
('Finance', 'Vérification budgétaire, rapprochement facture (3-way match) et paiements'),
('DAF', 'Approbation finale des BC et validation des paiements'),
('DG', 'Approbation exceptionnelle pour montants importants'),
('Magasinier', 'Réception des articles et gestion du stock');



INSERT INTO permissions (role_id, module, action, path, perimetre) VALUES

-- 🔑 ADMINISTRATEUR : Tout
((SELECT id FROM roles WHERE nom = 'Administrateur'), 'DASHBOARD', 'ACCESS', '/dashboard', NULL),
((SELECT id FROM roles WHERE nom = 'Administrateur'), 'SYSTEME', 'ACCESS', '/utilisateurs', NULL),
((SELECT id FROM roles WHERE nom = 'Administrateur'), 'SYSTEME', 'ACCESS', '/roles', NULL),
((SELECT id FROM roles WHERE nom = 'Administrateur'), 'SYSTEME', 'ACCESS', '/habilitations', NULL),

-- 📝 DEMANDEUR (Étape 1)
((SELECT id FROM roles WHERE nom = 'Demandeur'), 'DASHBOARD', 'ACCESS', '/dashboard', NULL),
((SELECT id FROM roles WHERE nom = 'Demandeur'), 'ACHATS', 'ACCESS', '/achats', NULL),
((SELECT id FROM roles WHERE nom = 'Demandeur'), 'ACHATS', 'CREATE', '/achats/create', NULL),

-- ✅ APPROBATEUR (Étape 2)
((SELECT id FROM roles WHERE nom = 'Approbateur'), 'DASHBOARD', 'ACCESS', '/dashboard', NULL),
((SELECT id FROM roles WHERE nom = 'Approbateur'), 'ACHATS', 'APPROVE', '/achats', NULL),
((SELECT id FROM roles WHERE nom = 'Approbateur'), 'ACHATS', 'ACCESS', '/achats', NULL),

-- 💰 FINANCE (Étapes 3, 8, 9)
((SELECT id FROM roles WHERE nom = 'Finance'), 'DASHBOARD', 'ACCESS', '/dashboard', NULL),
((SELECT id FROM roles WHERE nom = 'Finance'), 'FINANCES', 'BUDGET_CHECK', '/budgets', NULL),
((SELECT id FROM roles WHERE nom = 'Finance'), 'FINANCES', 'INVOICE_MATCH', '/factures', NULL),
((SELECT id FROM roles WHERE nom = 'Finance'), 'FINANCES', 'PAYMENT', '/paiements', NULL),
((SELECT id FROM roles WHERE nom = 'Finance'), 'ACHATS', 'ACCESS', '/achats', NULL),

-- 🛒 ACHETEUR (Étapes 4, 6)
((SELECT id FROM roles WHERE nom = 'Acheteur'), 'DASHBOARD', 'ACCESS', '/dashboard', NULL),
((SELECT id FROM roles WHERE nom = 'Acheteur'), 'ACHATS', 'TRANSFORM', '/achats', NULL),
((SELECT id FROM roles WHERE nom = 'Acheteur'), 'ACHATS', 'ACCESS', '/commandes-achat', NULL),
((SELECT id FROM roles WHERE nom = 'Acheteur'), 'PARTENAIRES', 'ACCESS', '/fournisseurs', NULL),

-- 👔 RESPONSABLE ACHATS (Étape 5)
((SELECT id FROM roles WHERE nom = 'Responsable Achats'), 'DASHBOARD', 'ACCESS', '/dashboard', NULL),
((SELECT id FROM roles WHERE nom = 'Responsable Achats'), 'ACHATS', 'VALIDATE_BC', '/commandes-achat', NULL),
((SELECT id FROM roles WHERE nom = 'Responsable Achats'), 'ACHATS', 'LITIGE_RESOLVE', '/commandes-achat', NULL),

-- 🏦 DAF / DG (Étape 5, 9)
((SELECT id FROM roles WHERE nom = 'DAF'), 'DASHBOARD', 'ACCESS', '/dashboard', NULL),
((SELECT id FROM roles WHERE nom = 'DAF'), 'ACHATS', 'FINAL_APPROVE', '/commandes-achat', NULL),
((SELECT id FROM roles WHERE nom = 'DAF'), 'FINANCES', 'VALIDATE_PAYMENT', '/paiements', NULL),

((SELECT id FROM roles WHERE nom = 'DG'), 'DASHBOARD', 'ACCESS', '/dashboard', NULL),
((SELECT id FROM roles WHERE nom = 'DG'), 'ACHATS', 'EXCEPTIONAL_APPROVE', '/commandes-achat', NULL),

-- 📦 MAGASINIER (Étape 7)
((SELECT id FROM roles WHERE nom = 'Magasinier'), 'DASHBOARD', 'ACCESS', '/dashboard', NULL),
((SELECT id FROM roles WHERE nom = 'Magasinier'), 'STOCK', 'RECEPTION', '/receptions', NULL),
((SELECT id FROM roles WHERE nom = 'Magasinier'), 'STOCK', 'ACCESS', '/stock', NULL),
((SELECT id FROM roles WHERE nom = 'Magasinier'), 'STOCK', 'ACCESS', '/depots', NULL),
((SELECT id FROM roles WHERE nom = 'Magasinier'), 'STOCK', 'ACCESS', '/emplacements', NULL);


