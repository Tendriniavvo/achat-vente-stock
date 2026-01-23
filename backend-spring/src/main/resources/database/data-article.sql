BEGIN;

-- =========================
-- 1. Catégories d’articles
-- =========================
INSERT INTO categories_articles (nom, description) VALUES
('Informatique', 'Matériel informatique'),
('Bureautique', 'Fournitures de bureau'),
('Alimentaire', 'Produits alimentaires'),
('Électronique', 'Équipements électroniques');

-- ==========
-- 2. Unités
-- ==========
INSERT INTO unites (nom, symbole) VALUES
('Pièce', 'pc'),
('Kilogramme', 'kg'),
('Litre', 'L');

-- ========
-- 3. Taxes
-- ========
INSERT INTO taxes (nom, taux, date_debut) VALUES
('TVA 20%', 20.00, CURRENT_TIMESTAMP),
('TVA 10%', 10.00, CURRENT_TIMESTAMP),
('TVA 0%', 0.00, CURRENT_TIMESTAMP);

-- ===========
-- 4. Articles
-- ===========
INSERT INTO articles (
    code,
    nom,
    description,
    categorie_id,
    unite_id,
    taxe_id,
    prix_achat,
    prix_vente,
    methode_valorisation,
    stock_min,
    stock_max,
    traceable_lot,
    actif
)
SELECT * FROM (
    VALUES
    (
        'ART-001',
        'Ordinateur portable Dell',
        'Laptop Dell i5, 8Go RAM, SSD 512Go',
        (SELECT id FROM categories_articles WHERE nom = 'Informatique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        800.00,
        1050.00,
        'FIFO',
        5,
        50,
        FALSE,
        TRUE
    ),
    (
        'ART-002',
        'Imprimante HP Laser',
        'Imprimante laser noir et blanc',
        (SELECT id FROM categories_articles WHERE nom = 'Informatique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        150.00,
        230.00,
        'CUMP',
        3,
        20,
        FALSE,
        TRUE
    ),
    (
        'ART-003',
        'Ramette papier A4',
        'Ramette papier A4 – 500 feuilles',
        (SELECT id FROM categories_articles WHERE nom = 'Bureautique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        4.50,
        7.00,
        'CUMP',
        20,
        500,
        FALSE,
        TRUE
    ),
    (
        'ART-004',
        'Riz blanc local',
        'Riz blanc qualité supérieure',
        (SELECT id FROM categories_articles WHERE nom = 'Alimentaire'),
        (SELECT id FROM unites WHERE nom = 'Kilogramme'),
        (SELECT id FROM taxes WHERE nom = 'TVA 0%'),
        0.80,
        1.20,
        'CUMP',
        100,
        2000,
        TRUE,
        TRUE
    ),
    (
        'ART-005',
        'Télévision LED 43 pouces',
        'TV LED Full HD 43 pouces',
        (SELECT id FROM categories_articles WHERE nom = 'Électronique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        220.00,
        320.00,
        'FIFO',
        2,
        30,
        FALSE,
        TRUE
    )
) AS a(
    code,
    nom,
    description,
    categorie_id,
    unite_id,
    taxe_id,
    prix_achat,
    prix_vente,
    methode_valorisation,
    stock_min,
    stock_max,
    traceable_lot,
    actif
);

COMMIT;
