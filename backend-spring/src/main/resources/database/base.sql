
CREATE TABLE departements (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    actif BOOLEAN DEFAULT TRUE
);

CREATE TABLE utilisateurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL, -- Hashé
    departement_id INTEGER REFERENCES departements(id),
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_derniere_connexion TIMESTAMP
);

-- Table des rôles (RBAC)
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) UNIQUE NOT NULL, -- Ex: Acheteur, Magasinier, Commercial, etc.
    description TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table de liaison utilisateurs-roles
CREATE TABLE utilisateurs_roles (
    utilisateur_id INTEGER REFERENCES utilisateurs(id) ON DELETE CASCADE,
    role_id INTEGER REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (utilisateur_id, role_id),
    date_attribution TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_expiration TIMESTAMP -- Pour accès temporaires
);

-- Table des permissions (pour ABAC, restrictions par attributs)
CREATE TABLE permissions (
    id SERIAL PRIMARY KEY,
    role_id INTEGER REFERENCES roles(id) ON DELETE CASCADE,
    module VARCHAR(100) NOT NULL, -- Ex: Achats, Ventes, Stocks
    action VARCHAR(50) NOT NULL, -- Ex: creer, modifier, valider, consulter
    perimetre VARCHAR(255), -- Ex: site=Paris, montant<10000, etc.
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des catégories d'articles
CREATE TABLE categories_articles (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    parent_id INTEGER REFERENCES categories_articles(id) -- Hiérarchie
);

-- Table des unités (ex: kg, pièce, litre)
CREATE TABLE unites (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) UNIQUE NOT NULL,
    symbole VARCHAR(10)
);

-- Table des taxes (ex: TVA)
CREATE TABLE taxes (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    taux DECIMAL(5,2) NOT NULL, -- Ex: 20.00
    date_debut TIMESTAMP,
    date_fin TIMESTAMP
);

-- Table des articles
CREATE TABLE articles (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    categorie_id INTEGER REFERENCES categories_articles(id),
    unite_id INTEGER REFERENCES unites(id),
    taxe_id INTEGER REFERENCES taxes(id),
    prix_achat DECIMAL(10,2),
    prix_vente DECIMAL(10,2),
    methode_valorisation VARCHAR(10) DEFAULT 'CUMP', -- FIFO ou CUMP
    stock_min INTEGER,
    stock_max INTEGER,
    traceable_lot BOOLEAN DEFAULT FALSE, -- Pour lots/séries
    stock_strategy VARCHAR(10) DEFAULT 'FEFO',
    actif BOOLEAN DEFAULT TRUE,
    historique TEXT, -- JSON ou texte pour historique
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des fournisseurs
CREATE TABLE fournisseurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    adresse TEXT,
    email VARCHAR(255),
    telephone VARCHAR(50),
    conditions TEXT, -- Conditions commerciales
    actif BOOLEAN DEFAULT TRUE,
    historique TEXT, -- Historique commandes
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des clients
CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    adresse TEXT,
    email VARCHAR(255),
    telephone VARCHAR(50),
    actif BOOLEAN DEFAULT TRUE,
    historique TEXT, -- Historique interactions
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des dépôts (sites de stockage)
CREATE TABLE depots (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse TEXT,
    capacite INTEGER,
    actif BOOLEAN DEFAULT TRUE
);

-- Table des emplacements (dans un dépôt)
CREATE TABLE emplacements (
    id SERIAL PRIMARY KEY,
    depot_id INTEGER REFERENCES depots(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    description TEXT,
    capacite INTEGER
);

-- Table des stocks (niveau actuel par article/dépôt/emplacement)
CREATE TABLE stocks (
    id SERIAL PRIMARY KEY,
    article_id INTEGER REFERENCES articles(id) ON DELETE CASCADE,
    depot_id INTEGER REFERENCES depots(id),
    emplacement_id INTEGER REFERENCES emplacements(id),
    quantite INTEGER NOT NULL DEFAULT 0,
    valeur DECIMAL(10,2) NOT NULL DEFAULT 0.00, -- Valorisation
    date_maj TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des lots (pour traçabilité)
CREATE TABLE lots (
    id SERIAL PRIMARY KEY,
    article_id INTEGER REFERENCES articles(id),
    numero_lot VARCHAR(50) UNIQUE NOT NULL,
    date_entree TIMESTAMP,
    date_expiration TIMESTAMP, -- DLUO/DLC
    quantite INTEGER,
    conforme BOOLEAN DEFAULT TRUE
);

-- Table des demandes d'achat (DA)
CREATE TABLE demandes_achat (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    demandeur_id INTEGER REFERENCES utilisateurs(id),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) DEFAULT 'brouillon', -- brouillon, soumise, validee, rejetee
    motif_rejet TEXT,
    historique_validations TEXT -- JSON pour workflow
);

-- Table des lignes de demande d'achat
CREATE TABLE lignes_demandes_achat (
    id SERIAL PRIMARY KEY,
    demande_achat_id INTEGER REFERENCES demandes_achat(id) ON DELETE CASCADE,
    article_id INTEGER REFERENCES articles(id),
    quantite INTEGER NOT NULL,
    prix_estime DECIMAL(10,2)
);

-- Table des bons de commande fournisseur (BC)
CREATE TABLE bons_commande_fournisseur (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    demande_achat_id INTEGER REFERENCES demandes_achat(id), -- Lien DA -> BC
    fournisseur_id INTEGER REFERENCES fournisseurs(id),
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_livraison_prevue TIMESTAMP,
    statut VARCHAR(50) DEFAULT 'brouillon', -- brouillon, valide, envoye, recu, annule
    montant_total DECIMAL(10,2),
    utilisateur_id INTEGER REFERENCES utilisateurs(id) -- Créateur
);

-- Table des lignes de bon de commande
CREATE TABLE lignes_bons_commande (
    id SERIAL PRIMARY KEY,
    bon_commande_id INTEGER REFERENCES bons_commande_fournisseur(id) ON DELETE CASCADE,
    article_id INTEGER REFERENCES articles(id),
    quantite INTEGER NOT NULL,
    prix_unitaire DECIMAL(10,2),
    remise DECIMAL(5,2) DEFAULT 0.00
);

-- Table des réceptions fournisseur
CREATE TABLE receptions (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    bon_commande_id INTEGER REFERENCES bons_commande_fournisseur(id),
    date_reception TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    depot_id INTEGER REFERENCES depots(id),
    statut VARCHAR(50) DEFAULT 'partielle', -- partielle, complete, litige
    utilisateur_id INTEGER REFERENCES utilisateurs(id) -- Magasinier
);

-- Table des lignes de réception
CREATE TABLE lignes_receptions (
    id SERIAL PRIMARY KEY,
    reception_id INTEGER REFERENCES receptions(id) ON DELETE CASCADE,
    article_id INTEGER REFERENCES articles(id),
    quantite_recue INTEGER NOT NULL,
    lot_id INTEGER REFERENCES lots(id), -- Pour traçabilité
    ecart TEXT -- Écarts quantitatifs/qualitatifs
);

-- Table des factures fournisseur
CREATE TABLE factures_fournisseur (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    bon_commande_id INTEGER REFERENCES bons_commande_fournisseur(id),
    reception_id INTEGER REFERENCES receptions(id),
    fournisseur_id INTEGER REFERENCES fournisseurs(id),
    date_facture TIMESTAMP,
    montant_total DECIMAL(10,2),
    statut VARCHAR(50) DEFAULT 'attente', -- attente, validee, bloquee, payee
    ecarts TEXT, -- Rapprochement 3-way match
    utilisateur_validation INTEGER REFERENCES utilisateurs(id) -- Finance
);

-- Table des lignes de facture fournisseur
CREATE TABLE lignes_factures_fournisseur (
    id SERIAL PRIMARY KEY,
    facture_id INTEGER REFERENCES factures_fournisseur(id) ON DELETE CASCADE,
    article_id INTEGER REFERENCES articles(id),
    quantite INTEGER,
    prix_unitaire DECIMAL(10,2),
    taxe_id INTEGER REFERENCES taxes(id)
);

-- Table des devis clients
CREATE TABLE devis_clients (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    client_id INTEGER REFERENCES clients(id),
    date_devis TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_validite TIMESTAMP,
    statut VARCHAR(50) DEFAULT 'brouillon', -- brouillon, envoye, accepte, rejete
    montant_total DECIMAL(10,2),
    remise_exceptionnelle BOOLEAN DEFAULT FALSE,
    utilisateur_id INTEGER REFERENCES utilisateurs(id) -- Commercial
);

-- Table des lignes de devis
CREATE TABLE lignes_devis (
    id SERIAL PRIMARY KEY,
    devis_id INTEGER REFERENCES devis_clients(id) ON DELETE CASCADE,
    article_id INTEGER REFERENCES articles(id),
    quantite INTEGER NOT NULL,
    prix_unitaire DECIMAL(10,2),
    remise DECIMAL(5,2) DEFAULT 0.00
);

-- Table des commandes clients
CREATE TABLE commandes_clients (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    devis_id INTEGER REFERENCES devis_clients(id), -- Lien devis -> commande
    client_id INTEGER REFERENCES clients(id),
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_livraison_prevue TIMESTAMP,
    statut VARCHAR(50) DEFAULT 'en_cours', -- en_cours, reservee, livree, annulee
    montant_total DECIMAL(10,2),
    utilisateur_id INTEGER REFERENCES utilisateurs(id)
);

-- Table des lignes de commande client
CREATE TABLE lignes_commandes_clients (
    id SERIAL PRIMARY KEY,
    commande_id INTEGER REFERENCES commandes_clients(id) ON DELETE CASCADE,
    article_id INTEGER REFERENCES articles(id),
    quantite INTEGER NOT NULL,
    prix_unitaire DECIMAL(10,2),
    remise DECIMAL(5,2) DEFAULT 0.00
);

-- Table des livraisons / sorties de stock
CREATE TABLE livraisons (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    commande_client_id INTEGER REFERENCES commandes_clients(id),
    date_livraison TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    depot_id INTEGER REFERENCES depots(id),
    statut VARCHAR(50) DEFAULT 'preparation', -- preparation, picking, expediee
    utilisateur_id INTEGER REFERENCES utilisateurs(id) -- Magasinier
);

-- Table des lignes de livraison
CREATE TABLE lignes_livraisons (
    id SERIAL PRIMARY KEY,
    livraison_id INTEGER REFERENCES livraisons(id) ON DELETE CASCADE,
    article_id INTEGER REFERENCES articles(id),
    quantite_livree INTEGER NOT NULL,
    emplacement_id INTEGER REFERENCES emplacements(id),
    lot_id INTEGER REFERENCES lots(id)
);

-- Table des factures clients
CREATE TABLE factures_clients (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    commande_client_id INTEGER REFERENCES commandes_clients(id),
    livraison_id INTEGER REFERENCES livraisons(id),
    client_id INTEGER REFERENCES clients(id),
    date_facture TIMESTAMP,
    montant_total DECIMAL(10,2),
    statut VARCHAR(50) DEFAULT 'attente', -- attente, validee, payee
    utilisateur_validation INTEGER REFERENCES utilisateurs(id)
);

-- Table des lignes de facture client
CREATE TABLE lignes_factures_clients (
    id SERIAL PRIMARY KEY,
    facture_id INTEGER REFERENCES factures_clients(id) ON DELETE CASCADE,
    article_id INTEGER REFERENCES articles(id),
    quantite INTEGER,
    prix_unitaire DECIMAL(10,2),
    taxe_id INTEGER REFERENCES taxes(id)
);

-- Table des encaissements (paiements clients)
CREATE TABLE encaissements (
    id SERIAL PRIMARY KEY,
    facture_client_id INTEGER REFERENCES factures_clients(id),
    montant DECIMAL(10,2) NOT NULL,
    date_encaissement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mode_paiement VARCHAR(50), -- Ex: virement, carte
    utilisateur_id INTEGER REFERENCES utilisateurs(id)
);

-- Table des mouvements de stock
CREATE TABLE mouvements_stock (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- entree, sortie, transfert, ajustement
    article_id INTEGER REFERENCES articles(id),
    quantite INTEGER NOT NULL,
    cout DECIMAL(10,2),
    date_mouvement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    depot_id INTEGER REFERENCES depots(id),
    emplacement_id INTEGER REFERENCES emplacements(id),
    lot_id INTEGER REFERENCES lots(id),
    reference_document VARCHAR(50), -- Lien vers BC, reception, etc.
    utilisateur_id INTEGER REFERENCES utilisateurs(id),
    motif TEXT
);

-- Table des inventaires
CREATE TABLE inventaires (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(50) DEFAULT 'annuel', -- annuel, tournant
    date_debut TIMESTAMP,
    date_fin TIMESTAMP,
    depot_id INTEGER REFERENCES depots(id),
    statut VARCHAR(50) DEFAULT 'en_cours', -- en_cours, termine, valide
    utilisateur_lancement INTEGER REFERENCES utilisateurs(id)
);

-- Table des lignes d'inventaire
CREATE TABLE lignes_inventaires (
    id SERIAL PRIMARY KEY,
    inventaire_id INTEGER REFERENCES inventaires(id) ON DELETE CASCADE,
    article_id INTEGER REFERENCES articles(id),
    quantite_theorique INTEGER,
    quantite_physique INTEGER,
    ecart INTEGER,
    motif_ecart TEXT
);

-- Table des ajustements de stock
CREATE TABLE ajustements_stock (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    inventaire_id INTEGER REFERENCES inventaires(id),
    article_id INTEGER REFERENCES articles(id),
    quantite_ajustee INTEGER NOT NULL,
    type VARCHAR(20) DEFAULT 'positif', -- positif, negatif
    motif TEXT NOT NULL,
    statut VARCHAR(50) DEFAULT 'propose', -- propose, valide, rejete
    utilisateur_proposition INTEGER REFERENCES utilisateurs(id),
    utilisateur_validation INTEGER REFERENCES utilisateurs(id),
    date_ajustement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des valorisations stock (périodiques)
CREATE TABLE valorisations_stock (
    id SERIAL PRIMARY KEY,
    date_valorisation TIMESTAMP NOT NULL,
    depot_id INTEGER REFERENCES depots(id),
    valeur_totale DECIMAL(10,2),
    ecarts DECIMAL(10,2),
    rapport TEXT -- JSON ou texte pour détails
);

-- Table du journal d'audit
CREATE TABLE journal_audit (
    id SERIAL PRIMARY KEY,
    utilisateur_id INTEGER REFERENCES utilisateurs(id),
    action VARCHAR(100) NOT NULL, -- Ex: creation, modification, validation
    module VARCHAR(100),
    reference_objet VARCHAR(50),
    details TEXT,
    date_action TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes pour performances (exemples)
CREATE INDEX idx_articles_code ON articles(code);
CREATE INDEX idx_mouvements_date ON mouvements_stock(date_mouvement);
CREATE INDEX idx_stocks_article ON stocks(article_id);

-- Fin du script