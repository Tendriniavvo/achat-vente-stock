INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, actif) VALUES
('Admin', 'System', 'admin@local.test', 'hash', true)
ON CONFLICT (email) DO NOTHING;

INSERT INTO fournisseurs (nom, email, telephone, actif) VALUES
('Fournisseur A', 'fa@local.test', '0340000001', true),
('Fournisseur B', 'fb@local.test', '0340000002', true),
('Fournisseur C', 'fc@local.test', '0340000003', true)
ON CONFLICT DO NOTHING;

INSERT INTO demandes_achat (reference, demandeur_id, date_creation, statut)
SELECT 'DA-001', u.id, CURRENT_TIMESTAMP - INTERVAL '8 days', 'soumise' FROM utilisateurs u WHERE u.email='admin@local.test'
ON CONFLICT (reference) DO NOTHING;

INSERT INTO demandes_achat (reference, demandeur_id, date_creation, statut)
SELECT 'DA-002', u.id, CURRENT_TIMESTAMP - INTERVAL '2 days', 'validee' FROM utilisateurs u WHERE u.email='admin@local.test'
ON CONFLICT (reference) DO NOTHING;

INSERT INTO bons_commande_fournisseur (reference, demande_achat_id, fournisseur_id, date_commande, date_livraison_prevue, statut, montant_total, utilisateur_id)
SELECT 'BC-001', d.id, f.id, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 'envoye', 1200000.00, u.id
FROM demandes_achat d, fournisseurs f, utilisateurs u
WHERE d.reference='DA-001' AND f.nom='Fournisseur A' AND u.email='admin@local.test'
ON CONFLICT (reference) DO NOTHING;

INSERT INTO bons_commande_fournisseur (reference, demande_achat_id, fournisseur_id, date_commande, date_livraison_prevue, statut, montant_total, utilisateur_id)
SELECT 'BC-002', d.id, f.id, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP + INTERVAL '2 days', 'valide', 800000.00, u.id
FROM demandes_achat d, fournisseurs f, utilisateurs u
WHERE d.reference='DA-002' AND f.nom='Fournisseur B' AND u.email='admin@local.test'
ON CONFLICT (reference) DO NOTHING;

INSERT INTO receptions (reference, bon_commande_id, date_reception, statut, utilisateur_id)
SELECT 'REC-001', b.id, CURRENT_TIMESTAMP - INTERVAL '3 days', 'partielle', u.id
FROM bons_commande_fournisseur b, utilisateurs u
WHERE b.reference='BC-001' AND u.email='admin@local.test'
ON CONFLICT (reference) DO NOTHING;

INSERT INTO factures_fournisseur (reference, bon_commande_id, fournisseur_id, date_facture, montant_total, statut)
SELECT 'FAC-001', b.id, f.id, CURRENT_TIMESTAMP - INTERVAL '2 days', 1200000.00, 'attente'
FROM bons_commande_fournisseur b, fournisseurs f
WHERE b.reference='BC-001' AND f.nom='Fournisseur A'
ON CONFLICT (reference) DO NOTHING;

SELECT COUNT(*) AS da_soumises FROM demandes_achat WHERE statut='soumise';
SELECT COUNT(*) AS bc_mois FROM bons_commande_fournisseur WHERE date_commande >= date_trunc('month', CURRENT_TIMESTAMP);
SELECT COALESCE(SUM(montant_total),0) AS bc_montant_mois FROM bons_commande_fournisseur WHERE date_commande >= date_trunc('month', CURRENT_TIMESTAMP);
SELECT COUNT(*) AS receptions_partielle_litige_mois FROM receptions WHERE statut IN ('partielle','litige') AND date_reception >= date_trunc('month', CURRENT_TIMESTAMP);
SELECT COUNT(*) AS bc_retard FROM bons_commande_fournisseur WHERE date_livraison_prevue IS NOT NULL AND date_livraison_prevue < CURRENT_TIMESTAMP AND statut NOT IN ('recu','annule');
SELECT COUNT(*) AS factures_bloquees FROM factures_fournisseur WHERE statut='bloquee';
