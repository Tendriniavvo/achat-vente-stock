-- Script de vérification des données pour debug

-- Vérifier les utilisateurs
SELECT id, nom, prenom, email FROM utilisateurs;

-- Vérifier les articles
SELECT id, code, nom, prix_achat FROM articles LIMIT 10;

-- Vérifier les demandes d'achat
SELECT id, reference, demandeur_id, statut, date_creation FROM demandes_achat;

-- Vérifier les lignes de demandes d'achat
SELECT id, demande_achat_id, article_id, quantite, prix_estime FROM lignes_demandes_achat;

-- Détails d'une demande spécifique (ID 2)
SELECT 
    da.id,
    da.reference,
    da.statut,
    u.nom as demandeur_nom,
    u.prenom as demandeur_prenom,
    COUNT(lda.id) as nb_lignes
FROM demandes_achat da
LEFT JOIN utilisateurs u ON da.demandeur_id = u.id
LEFT JOIN lignes_demandes_achat lda ON lda.demande_achat_id = da.id
WHERE da.id = 2
GROUP BY da.id, da.reference, da.statut, u.nom, u.prenom;
