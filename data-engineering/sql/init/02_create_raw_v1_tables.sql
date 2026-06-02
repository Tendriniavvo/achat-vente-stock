CREATE TABLE IF NOT EXISTS raw.raw_articles (
    source_id INTEGER PRIMARY KEY,
    code VARCHAR(50),
    nom VARCHAR(255),
    description TEXT,
    categorie_id INTEGER,
    unite_id INTEGER,
    taxe_id INTEGER,
    prix_achat DECIMAL(10, 2),
    prix_vente DECIMAL(10, 2),
    methode_valorisation VARCHAR(10),
    stock_min INTEGER,
    stock_max INTEGER,
    traceable_lot BOOLEAN,
    stock_strategy VARCHAR(10),
    actif BOOLEAN,
    historique TEXT,
    date_creation TIMESTAMP,
    ingestion_run_id VARCHAR(255),
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS raw.raw_stocks (
    source_id INTEGER PRIMARY KEY,
    article_id INTEGER,
    depot_id INTEGER,
    emplacement_id INTEGER,
    quantite INTEGER,
    cout_unitaire DECIMAL(10, 2),
    valeur DECIMAL(10, 2),
    date_maj TIMESTAMP,
    ingestion_run_id VARCHAR(255),
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS raw.raw_depots (
    source_id INTEGER PRIMARY KEY,
    nom VARCHAR(100),
    code VARCHAR(20),
    adresse TEXT,
    responsable VARCHAR(100),
    capacite INTEGER,
    type_entreposage VARCHAR(50),
    horaires_ouverture VARCHAR(100),
    actif BOOLEAN,
    ingestion_run_id VARCHAR(255),
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS raw.raw_departements (
    source_id INTEGER PRIMARY KEY,
    code VARCHAR(50),
    nom VARCHAR(100),
    description TEXT,
    actif BOOLEAN,
    ingestion_run_id VARCHAR(255),
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS raw.raw_budgets (
    source_id INTEGER PRIMARY KEY,
    departement_id INTEGER,
    annee INTEGER,
    montant_initial DECIMAL(19, 2),
    montant_consomme DECIMAL(19, 2),
    montant_disponible DECIMAL(19, 2),
    date_creation TIMESTAMP,
    ingestion_run_id VARCHAR(255),
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS raw.raw_demandes_achat (
    source_id INTEGER PRIMARY KEY,
    reference VARCHAR(50),
    demandeur_id INTEGER,
    date_creation TIMESTAMP,
    statut VARCHAR(50),
    motif_rejet TEXT,
    historique_validations TEXT,
    ingestion_run_id VARCHAR(255),
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS raw.raw_lignes_demandes_achat (
    source_id INTEGER PRIMARY KEY,
    demande_achat_id INTEGER,
    article_id INTEGER,
    quantite INTEGER,
    prix_estime DECIMAL(10, 2),
    ingestion_run_id VARCHAR(255),
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS raw.raw_bons_commande_fournisseur (
    source_id INTEGER PRIMARY KEY,
    reference VARCHAR(50),
    demande_achat_id INTEGER,
    fournisseur_id INTEGER,
    date_commande TIMESTAMP,
    date_livraison_prevue TIMESTAMP,
    statut VARCHAR(50),
    montant_total DECIMAL(10, 2),
    utilisateur_id INTEGER,
    ingestion_run_id VARCHAR(255),
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS raw.raw_fournisseurs (
    source_id INTEGER PRIMARY KEY,
    nom VARCHAR(255),
    adresse TEXT,
    email VARCHAR(255),
    telephone VARCHAR(50),
    conditions TEXT,
    actif BOOLEAN,
    historique TEXT,
    date_creation TIMESTAMP,
    ingestion_run_id VARCHAR(255),
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS raw.raw_mouvements_stock (
    source_id INTEGER PRIMARY KEY,
    type VARCHAR(50),
    article_id INTEGER,
    quantite INTEGER,
    cout DECIMAL(10, 2),
    date_mouvement TIMESTAMP,
    depot_id INTEGER,
    emplacement_id INTEGER,
    lot_id INTEGER,
    reference_document VARCHAR(50),
    utilisateur_id INTEGER,
    motif TEXT,
    ingestion_run_id VARCHAR(255),
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

