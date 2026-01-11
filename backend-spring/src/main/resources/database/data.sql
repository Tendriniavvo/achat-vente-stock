-- Insertion des rôles par défaut
INSERT INTO roles (nom, description, date_creation) VALUES 
('Administrateur', 'Accès complet au système', CURRENT_TIMESTAMP),
('Acheteur', 'Gestion des achats et commandes fournisseurs', CURRENT_TIMESTAMP),
('Magasinier', 'Gestion des stocks et inventaires', CURRENT_TIMESTAMP),
('Commercial', 'Gestion des ventes et clients', CURRENT_TIMESTAMP),
('Comptable', 'Gestion financière et facturation', CURRENT_TIMESTAMP)
ON CONFLICT (nom) DO NOTHING;

-- Insertion de quelques articles de test
INSERT INTO articles (code, nom, description, prix_achat, prix_vente, actif, date_creation) VALUES 
('ART-001', 'Ordinateur Portable HP', 'HP ProBook 450 G8, Intel Core i5, 8GB RAM, 256GB SSD', 2500000.00, 3200000.00, true, CURRENT_TIMESTAMP),
('ART-002', 'Souris Sans Fil Logitech', 'Logitech M185, sans fil 2.4GHz, batterie longue durée', 25000.00, 35000.00, true, CURRENT_TIMESTAMP),
('ART-003', 'Clavier USB Standard', 'Clavier filaire USB, AZERTY français', 15000.00, 22000.00, true, CURRENT_TIMESTAMP),
('ART-004', 'Écran LED 24"', 'Moniteur LED 24 pouces Full HD 1920x1080', 450000.00, 620000.00, true, CURRENT_TIMESTAMP),
('ART-005', 'Imprimante Laser Brother', 'Brother HL-L2350DW, impression recto-verso automatique', 780000.00, 950000.00, true, CURRENT_TIMESTAMP),
('ART-006', 'Ramette Papier A4', 'Papier blanc A4 80g/m², 500 feuilles', 12000.00, 18000.00, true, CURRENT_TIMESTAMP),
('ART-007', 'Stylo à Bille Bleu', 'Lot de 10 stylos à bille, encre bleue', 3000.00, 5000.00, true, CURRENT_TIMESTAMP),
('ART-008', 'Agrafeuse Métallique', 'Agrafeuse de bureau, capacité 25 feuilles', 8000.00, 12000.00, true, CURRENT_TIMESTAMP),
('ART-009', 'Classeur à Levier', 'Classeur à levier format A4, dos 7cm', 4500.00, 7000.00, true, CURRENT_TIMESTAMP),
('ART-010', 'Câble HDMI 2m', 'Câble HDMI haute vitesse 2.0, 2 mètres', 15000.00, 22000.00, true, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

