-- Script de réinitialisation de la base de données
-- Supprime toutes les tables existantes dans l'ordre inverse des dépendances ou avec CASCADE

DROP TABLE IF EXISTS journal_audit CASCADE;
DROP TABLE IF EXISTS valorisations_stock CASCADE;
DROP TABLE IF EXISTS ajustements_stock CASCADE;
DROP TABLE IF EXISTS lignes_inventaires CASCADE;
DROP TABLE IF EXISTS inventaires CASCADE;
DROP TABLE IF EXISTS mouvements_stock CASCADE;
DROP TABLE IF EXISTS encaissements CASCADE;
DROP TABLE IF EXISTS lignes_factures_clients CASCADE;
DROP TABLE IF EXISTS factures_clients CASCADE;
DROP TABLE IF EXISTS lignes_livraisons CASCADE;
DROP TABLE IF EXISTS livraisons CASCADE;
DROP TABLE IF EXISTS lignes_commandes_clients CASCADE;
DROP TABLE IF EXISTS commandes_clients CASCADE;
DROP TABLE IF EXISTS lignes_devis CASCADE;
DROP TABLE IF EXISTS devis_clients CASCADE;
DROP TABLE IF EXISTS lignes_factures_fournisseur CASCADE;
DROP TABLE IF EXISTS factures_fournisseur CASCADE;
DROP TABLE IF EXISTS lignes_receptions CASCADE;
DROP TABLE IF EXISTS receptions CASCADE;
DROP TABLE IF EXISTS lignes_bons_commande CASCADE;
DROP TABLE IF EXISTS bons_commande_fournisseur CASCADE;
DROP TABLE IF EXISTS lignes_demandes_achat CASCADE;
DROP TABLE IF EXISTS demandes_achat CASCADE;
DROP TABLE IF EXISTS lots CASCADE;
DROP TABLE IF EXISTS stocks CASCADE;
DROP TABLE IF EXISTS emplacements CASCADE;
DROP TABLE IF EXISTS depots CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS fournisseurs CASCADE;
DROP TABLE IF EXISTS articles CASCADE;
DROP TABLE IF EXISTS taxes CASCADE;
DROP TABLE IF EXISTS unites CASCADE;
DROP TABLE IF EXISTS categories_articles CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS utilisateurs_roles CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS utilisateurs CASCADE;
DROP TABLE IF EXISTS budgets CASCADE;
DROP TABLE IF EXISTS departements CASCADE;
DROP TABLE IF EXISTS types_emplacement CASCADE;

-- Fin du script de nettoyage
