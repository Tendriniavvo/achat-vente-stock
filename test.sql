INSERT INTO lignes_commandes (commande_id, produit_id, quantite) 
SELECT 
    FLOOR(RANDOM() * 500000 + 1)::INT, 
    FLOOR(RANDOM() * 50000 + 1)::INT, 
    FLOOR(RANDOM() * 5 + 1)::INT
FROM generate_series(1, 5000000); 