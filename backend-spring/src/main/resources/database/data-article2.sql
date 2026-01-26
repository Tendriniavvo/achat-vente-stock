BEGIN;

-- (On suppose que les catégories, unités et taxes existent déjà grâce à tes INSERT précédents)

-- ===========
-- 4. Articles (version enrichie avec prix plus réalistes 2025-2026 à Madagascar)
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
    -- Informatique (prix actualisés + ajouts)
    (
        'ART-100',
        'Ordinateur portable Dell Latitude',
        'Dell Latitude i5 11e/12e gen – 8-16 Go RAM – SSD 512 Go – Windows 11',
        (SELECT id FROM categories_articles WHERE nom = 'Informatique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        900000,    -- ≈ prix achat grossiste/import
        950000,    -- prix vente magasin ≈ 1.9–2.2 MAr
        'FIFO',
        3,
        30,
        FALSE,
        TRUE
    ),
    (
        'ART-101',
        'Imprimante HP LaserJet Pro',
        'HP LaserJet Pro mono ou multifonction – 40 ppm – WiFi',
        (SELECT id FROM categories_articles WHERE nom = 'Informatique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        800000,
        950000,
        'CUMP',
        2,
        15,
        FALSE,
        TRUE
    ),
    (
        'ART-102',
        'Souris sans fil Logitech',
        'Souris Logitech M185 ou équivalent – sans fil 2.4 GHz',
        (SELECT id FROM categories_articles WHERE nom = 'Informatique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        12000,
        25000,
        'CUMP',
        20,
        200,
        FALSE,
        TRUE
    ),
    (
        'ART-103',
        'Clavier USB standard',
        'Clavier filaire AZERTY – entrée de gamme',
        (SELECT id FROM categories_articles WHERE nom = 'Informatique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        18000,
        35000,
        'CUMP',
        10,
        100,
        FALSE,
        TRUE
    ),
    (
        'ART-104',
        'Disque dur externe 1 To',
        'HDD/SSD externe 1 To USB 3.0 – marque Western Digital / Seagate',
        (SELECT id FROM categories_articles WHERE nom = 'Informatique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        95000,
        145000,
        'FIFO',
        5,
        50,
        FALSE,
        TRUE
    ),

    -- Bureautique
    (
        'ART-105',
        'Ramette papier A4 80g',
        'Ramette 500 feuilles A4 80g – marques Double A, PaperOne, Laureat…',
        (SELECT id FROM categories_articles WHERE nom = 'Bureautique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        18000,   -- prix actuel ≈ 18 000 – 23 000 Ar
        25000,
        'CUMP',
        30,
        600,
        FALSE,
        TRUE
    ),
    (
        'ART-106',
        'Stylo bille Bic Cristal',
        'Paquet de 50 stylos bille Bic Cristal bleu/noir',
        (SELECT id FROM categories_articles WHERE nom = 'Bureautique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        28000,
        42000,
        'CUMP',
        10,
        150,
        FALSE,
        TRUE
    ),
    (
        'ART-107',
        'Reliure spirale plastique 12 mm',
        'Boîte de 100 reliures spirales 12 mm – noir ou transparent',
        (SELECT id FROM categories_articles WHERE nom = 'Bureautique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        45000,
        68000,
        'CUMP',
        5,
        80,
        FALSE,
        TRUE
    ),

    -- Électronique
    (
        'ART-108',
        'Téléviseur LED 43 pouces Smart',
        'Smart TV LED 43" Full HD ou 4K – marques Trust, Jeyoo, TCL…',
        (SELECT id FROM categories_articles WHERE nom = 'Électronique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        750000,
        900000,   -- fourchette courante 950k–1.6M selon marque
        'FIFO',
        2,
        25,
        FALSE,
        TRUE
    ),
    (
        'ART-109',
        'Chargeur secteur universel 65W',
        'Chargeur laptop 65W – connecteurs multiples',
        (SELECT id FROM categories_articles WHERE nom = 'Électronique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        35000,
        65000,
        'CUMP',
        10,
        80,
        FALSE,
        TRUE
    ),
    (
        'ART-110',
        'Écouteurs Bluetooth entrée de gamme',
        'Écouteurs sans fil TWS – marque générique ou Realme/Awei',
        (SELECT id FROM categories_articles WHERE nom = 'Électronique'),
        (SELECT id FROM unites WHERE nom = 'Pièce'),
        (SELECT id FROM taxes WHERE nom = 'TVA 20%'),
        28000,
        55000,
        'CUMP',
        15,
        120,
        FALSE,
        TRUE
    ),

    -- Alimentaire (exemples réalistes)
    (
        'ART-111',
        'Riz parfumé 5 kg',
        'Sac de riz parfumé Thaïlande ou Vietnam 5 kg',
        (SELECT id FROM categories_articles WHERE nom = 'Alimentaire'),
        (SELECT id FROM unites WHERE nom = 'Kilogramme'),
        (SELECT id FROM taxes WHERE nom = 'TVA 0%'),
        32000,   -- prix achat sac 5 kg
        42000,   -- prix vente au détail
        'CUMP',
        20,
        200,
        TRUE,    -- traceable par lot pour alimentaire
        TRUE
    ),
    (
        'ART-112',
        'Huile de table 1 L',
        'Bouteille huile végétale 1 L – marque locale ou import',
        (SELECT id FROM categories_articles WHERE nom = 'Alimentaire'),
        (SELECT id FROM unites WHERE nom = 'Litre'),
        (SELECT id FROM taxes WHERE nom = 'TVA 0%'),
        6500,
        9500,
        'CUMP',
        50,
        400,
        TRUE,
        TRUE
    ),
    (
        'ART-113',
        'Sucre en poudre 1 kg',
        'Paquet sucre cristallisé 1 kg',
        (SELECT id FROM categories_articles WHERE nom = 'Alimentaire'),
        (SELECT id FROM unites WHERE nom = 'Kilogramme'),
        (SELECT id FROM taxes WHERE nom = 'TVA 0%'),
        4800,
        6800,
        'CUMP',
        40,
        300,
        TRUE,
        TRUE
    )

) AS a(
    code, nom, description, categorie_id, unite_id, taxe_id,
    prix_achat, prix_vente, methode_valorisation,
    stock_min, stock_max, traceable_lot, actif
);

COMMIT;