INSERT INTO fournisseurs (
    nom,
    adresse,
    email,
    telephone,
    conditions,
    actif,
    historique
) VALUES
(
    'Fournisseur Alpha',
    'Lot II A 45, Antananarivo, Madagascar',
    'contact@alpha-mg.com',
    '+261 34 12 345 67',
    'Paiement à 30 jours, livraison sous 7 jours',
    TRUE,
    'Commandes régulières depuis 2022'
),
(
    'Beta Distribution',
    'Zone Industrielle Forello, Antsirabe',
    'sales@beta-distribution.mg',
    '+261 32 45 678 90',
    'Paiement comptant, remise 5% sur gros volumes',
    TRUE,
    'Fournisseur principal pour produits alimentaires'
),
(
    'Gamma Import Export',
    'Port de Toamasina',
    'info@gamma-import.com',
    '+261 33 98 765 43',
    'Paiement à 60 jours, livraison maritime',
    TRUE,
    'Importations depuis l’Asie et l’Europe'
),
(
    'Delta Services',
    'Rue de la Paix, Mahajanga',
    'contact@delta-services.mg',
    '+261 38 11 223 34',
    'Paiement à 15 jours',
    FALSE,
    'Fournisseur suspendu depuis 2024'
);
