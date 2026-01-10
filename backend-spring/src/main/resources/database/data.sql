-- Insertion des rôles par défaut
INSERT INTO roles (nom, description, date_creation) VALUES 
('Administrateur', 'Accès complet au système', CURRENT_TIMESTAMP),
('Acheteur', 'Gestion des achats et commandes fournisseurs', CURRENT_TIMESTAMP),
('Magasinier', 'Gestion des stocks et inventaires', CURRENT_TIMESTAMP),
('Commercial', 'Gestion des ventes et clients', CURRENT_TIMESTAMP),
('Comptable', 'Gestion financière et facturation', CURRENT_TIMESTAMP)
ON CONFLICT (nom) DO NOTHING;
