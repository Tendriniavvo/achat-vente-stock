# Todo List - Projet Data Engineering

Objectif : transformer le projet `achat-vente-stock` en plateforme Data Engineering complète avec PostgreSQL, Apache Airflow, ETL/ELT, dbt, Power BI et envoi automatique d'emails.

## 1. Cadrage du projet

- [ ] Définir les objectifs data du projet.
- [ ] Identifier les utilisateurs finaux :
  - Direction
  - Responsable achat
  - Responsable stock
  - Finance
  - Commercial
  - Administrateur
- [ ] Définir les KPI prioritaires :
  - Valeur totale du stock
  - Articles sous seuil minimum
  - Montant total des achats
  - Montant total des ventes
  - Marge brute
  - Budget consommé
  - Budget disponible
  - Fournisseurs les plus importants
  - Articles les plus vendus
  - Factures impayées
- [ ] Définir la fréquence d'actualisation :
  - Quotidienne
  - Horaire
  - Hebdomadaire
  - Mensuelle
- [ ] Définir les alertes automatiques :
  - Stock sous seuil minimum
  - Budget consommé supérieur à 90%
  - Factures clients impayées
  - Factures fournisseurs en retard
  - Anomalies de marge
  - Rapport journalier

## 2. Audit du projet existant

- [ ] Lire la structure actuelle du projet :
  - `backend-spring`
  - `frontend-vue`
  - `processus`
  - fichiers de documentation
  - scripts SQL
- [ ] Identifier toutes les tables PostgreSQL existantes.
- [ ] Vérifier les tables principales :
  - `articles`
  - `stocks`
  - `mouvements_stock`
  - `demandes_achat`
  - `lignes_demandes_achat`
  - `bons_commande_fournisseur`
  - `receptions`
  - `factures_fournisseur`
  - `commandes_clients`
  - `factures_clients`
  - `budgets`
  - `departements`
  - `fournisseurs`
  - `clients`
  - `depots`
- [ ] Vérifier les colonnes nécessaires aux KPI :
  - dates
  - montants
  - statuts
  - quantités
  - prix d'achat
  - prix de vente
  - clés étrangères
- [ ] Identifier les données manquantes.
- [ ] Identifier les champs à ajouter si nécessaire :
  - date de validation
  - date de paiement
  - statut de facture
  - seuil de stock
  - quantité réservée
  - quantité disponible
- [ ] Vérifier les scripts SQL existants :
  - `base.sql`
  - `dept-data.sql`
  - `data.sql`
  - `kpi.sql`
  - `stock-test-data.sql`

## 3. Préparation PostgreSQL

- [ ] Garder la base opérationnelle de l'application Spring Boot.
- [ ] Choisir l'approche analytique :
  - une deuxième base `gestion_stock_analytics`
  - ou plusieurs schemas dans la même base
- [x] Créer les schemas analytiques :
  - `raw`
  - `staging`
  - `marts`
  - `audit`
- [ ] Créer un utilisateur PostgreSQL pour l'application.
- [ ] Créer un utilisateur PostgreSQL pour Airflow.
- [ ] Créer un utilisateur PostgreSQL pour dbt.
- [ ] Créer un utilisateur PostgreSQL en lecture seule pour Power BI.
- [x] Créer une table `audit.pipeline_runs`.
- [x] Créer une table `audit.data_quality_results`.
- [x] Créer une table `audit.email_logs`.
- [x] Créer une table `audit.alerts`.
- [x] Vérifier les droits d'accès sur chaque schema.

## 4. Structure du repo

- [x] Ajouter un dossier `data-engineering/`.
- [x] Créer la structure suivante :

```text
data-engineering/
  airflow/
    dags/
    logs/
    plugins/
  dbt/
    models/
    seeds/
    snapshots/
    macros/
    tests/
  sql/
  scripts/
  docker/
  docs/
```

- [x] Ajouter un `docker-compose.yml`.
- [x] Ajouter un fichier `.env.example`.
- [x] Ajouter une documentation d'installation.
- [ ] Ajouter une documentation d'architecture.
- [ ] Ajouter une documentation des KPI.
- [ ] Ajouter une documentation Power BI mise à jour.

## 5. Docker et environnement local

- [x] Configurer Docker Compose avec PostgreSQL.
- [ ] Configurer Docker Compose avec Airflow webserver.
- [ ] Configurer Docker Compose avec Airflow scheduler.
- [ ] Configurer la base metadata d'Airflow.
- [ ] Configurer un service dbt.
- [ ] Ajouter éventuellement pgAdmin.
- [ ] Configurer les volumes :
  - DAGs Airflow
  - logs Airflow
  - projet dbt
  - scripts SQL
- [x] Tester le démarrage des services.
- [ ] Vérifier l'accès à Airflow UI.
- [x] Vérifier la connexion PostgreSQL.
- [ ] Vérifier que dbt peut accéder à PostgreSQL.

## 6. Apache Airflow

- [ ] Initialiser Airflow.
- [ ] Configurer les connexions Airflow :
  - PostgreSQL source
  - PostgreSQL analytics
  - SMTP email
- [ ] Créer le DAG `daily_stock_pipeline`.
- [ ] Créer le DAG `daily_budget_pipeline`.
- [ ] Créer le DAG `daily_sales_pipeline`.
- [ ] Créer le DAG `daily_purchase_pipeline`.
- [ ] Créer le DAG `daily_alert_email_pipeline`.
- [ ] Pour chaque DAG, prévoir les étapes :
  - extraction des données
  - chargement dans `raw`
  - exécution dbt
  - tests data quality
  - génération d'alertes
  - envoi email si nécessaire
- [ ] Ajouter des retries.
- [ ] Ajouter des logs détaillés.
- [ ] Ajouter une notification en cas d'échec.
- [ ] Tester chaque DAG manuellement.
- [ ] Planifier les DAGs automatiquement.

## 7. ETL / ELT

- [ ] Extraire les données sources depuis PostgreSQL opérationnel.
- [ ] Charger les données dans le schema `raw`.
- [ ] Prévoir une stratégie V1 en full refresh.
- [ ] Prévoir une stratégie V2 en chargement incrémental.
- [ ] Ajouter un timestamp d'ingestion.
- [ ] Ajouter un identifiant d'exécution pipeline.
- [ ] Contrôler le nombre de lignes extraites.
- [ ] Contrôler les montants négatifs.
- [ ] Contrôler les dates nulles.
- [ ] Contrôler les statuts inconnus.
- [ ] Contrôler les clés étrangères manquantes.
- [ ] Journaliser chaque extraction dans `audit.pipeline_runs`.

## 8. dbt

- [ ] Créer un projet dbt.
- [ ] Configurer `profiles.yml`.
- [ ] Déclarer les sources dbt.
- [ ] Créer les modèles staging :
  - `stg_articles.sql`
  - `stg_stocks.sql`
  - `stg_mouvements_stock.sql`
  - `stg_demandes_achat.sql`
  - `stg_bons_commande_fournisseur.sql`
  - `stg_factures_fournisseur.sql`
  - `stg_commandes_clients.sql`
  - `stg_factures_clients.sql`
  - `stg_budgets.sql`
  - `stg_departements.sql`
  - `stg_fournisseurs.sql`
  - `stg_clients.sql`
  - `stg_depots.sql`
- [ ] Créer les dimensions :
  - `dim_date.sql`
  - `dim_article.sql`
  - `dim_client.sql`
  - `dim_fournisseur.sql`
  - `dim_depot.sql`
  - `dim_departement.sql`
  - `dim_utilisateur.sql`
- [ ] Créer les tables de faits :
  - `fact_achats.sql`
  - `fact_ventes.sql`
  - `fact_stock.sql`
  - `fact_mouvements_stock.sql`
  - `fact_budget.sql`
  - `fact_factures_clients.sql`
  - `fact_factures_fournisseur.sql`
- [ ] Créer les marts :
  - `mart_dashboard_global.sql`
  - `mart_dashboard_achats.sql`
  - `mart_dashboard_stock.sql`
  - `mart_dashboard_budget.sql`
  - `mart_dashboard_ventes.sql`
  - `mart_alertes.sql`
- [ ] Ajouter les tests dbt :
  - `unique`
  - `not_null`
  - `relationships`
  - `accepted_values`
- [ ] Générer la documentation dbt.
- [ ] Ajouter `dbt run` dans Airflow.
- [ ] Ajouter `dbt test` dans Airflow.

## 9. Modélisation analytique

- [ ] Définir le modèle en étoile.
- [ ] Créer les dimensions :
  - `dim_date`
  - `dim_article`
  - `dim_client`
  - `dim_fournisseur`
  - `dim_depot`
  - `dim_departement`
  - `dim_utilisateur`
- [ ] Créer les faits :
  - `fact_achats`
  - `fact_ventes`
  - `fact_stock_mouvements`
  - `fact_stock_snapshot`
  - `fact_budget`
  - `fact_factures_clients`
  - `fact_factures_fournisseurs`
- [ ] Créer les marts métier :
  - `mart_dashboard_global`
  - `mart_dashboard_achats`
  - `mart_dashboard_stock`
  - `mart_dashboard_budget`
  - `mart_alertes`
- [ ] Vérifier que Power BI peut lire les marts sans logique SQL complexe.

## 10. KPI Stock

- [ ] Calculer la valeur totale du stock.
- [ ] Calculer le stock disponible.
- [ ] Calculer le stock réservé.
- [ ] Identifier les articles sous seuil.
- [ ] Calculer les entrées de stock.
- [ ] Calculer les sorties de stock.
- [ ] Calculer la rotation du stock.
- [ ] Calculer le stock par dépôt.
- [ ] Calculer le stock par article.
- [ ] Calculer les écarts d'inventaire.

## 11. KPI Achats

- [ ] Calculer le nombre total de demandes d'achat.
- [ ] Calculer les demandes validées.
- [ ] Calculer les demandes refusées.
- [ ] Calculer les demandes en attente.
- [ ] Calculer le montant total des achats.
- [ ] Calculer le montant par fournisseur.
- [ ] Calculer le top fournisseurs.
- [ ] Calculer le délai moyen d'approbation.
- [ ] Calculer les commandes fournisseurs ouvertes.
- [ ] Calculer les réceptions non facturées.

## 12. KPI Ventes

- [ ] Calculer le chiffre d'affaires.
- [ ] Calculer le nombre de commandes clients.
- [ ] Calculer le nombre de factures clients.
- [ ] Calculer le top clients.
- [ ] Calculer le top articles vendus.
- [ ] Calculer la marge brute.
- [ ] Calculer la marge par article.
- [ ] Calculer la marge par client.
- [ ] Calculer les livraisons en attente.
- [ ] Calculer les factures clients impayées.

## 13. KPI Budget

- [ ] Calculer le budget initial.
- [ ] Calculer le budget consommé.
- [ ] Calculer le budget disponible.
- [ ] Calculer le pourcentage d'utilisation.
- [ ] Identifier les départements avec budget consommé supérieur à 90%.
- [ ] Identifier les départements en dépassement.
- [ ] Comparer budget et achats réalisés.
- [ ] Créer une table `mart_budget_alerts`.

## 14. KPI Finance

- [ ] Calculer les factures clients impayées.
- [ ] Calculer les factures clients en retard.
- [ ] Calculer les encaissements.
- [ ] Calculer les factures fournisseurs non réglées.
- [ ] Calculer les dettes fournisseurs.
- [ ] Calculer le solde client.
- [ ] Calculer le solde fournisseur.

## 15. Emails automatiques

- [ ] Configurer SMTP.
- [ ] Créer un template email HTML.
- [ ] Créer un email quotidien de résumé.
- [ ] Créer un email d'alerte stock critique.
- [ ] Créer un email d'alerte budget.
- [ ] Créer un email d'alerte facture en retard.
- [ ] Définir les destinataires par rôle.
- [ ] Tester l'envoi email.
- [ ] Ajouter l'envoi email dans Airflow.
- [ ] Ajouter les logs d'emails dans `audit.email_logs`.
- [ ] Gérer les erreurs d'envoi.

## 16. Power BI

- [ ] Connecter Power BI à PostgreSQL.
- [ ] Charger uniquement les tables du schema `marts`.
- [ ] Créer le modèle relationnel Power BI.
- [ ] Créer ou charger une table calendrier.
- [ ] Créer les relations entre dimensions et faits.
- [ ] Créer les mesures DAX principales.
- [ ] Créer la page Dashboard Global.
- [ ] Créer la page Achats.
- [ ] Créer la page Ventes.
- [ ] Créer la page Stock.
- [ ] Créer la page Budget.
- [ ] Créer la page Alertes.
- [ ] Ajouter les filtres :
  - date
  - département
  - fournisseur
  - client
  - dépôt
  - article
- [ ] Publier sur Power BI Service si disponible.
- [ ] Configurer le refresh Power BI.
- [ ] Configurer gateway si PostgreSQL est local.

## 17. Qualité des données

- [ ] Vérifier les doublons.
- [ ] Vérifier les valeurs nulles.
- [ ] Vérifier les montants incohérents.
- [ ] Vérifier les quantités négatives.
- [ ] Vérifier les dates invalides.
- [ ] Vérifier les relations cassées.
- [ ] Vérifier les statuts inconnus.
- [ ] Ajouter des tests dbt.
- [ ] Ajouter des alertes data quality dans Airflow.
- [ ] Bloquer le pipeline si erreur critique.
- [ ] Journaliser les résultats dans `audit.data_quality_results`.

## 18. Sécurité

- [ ] Retirer les clés API codées en dur.
- [ ] Utiliser des variables d'environnement.
- [x] Créer un fichier `.env.example`.
- [x] Vérifier que `.env` est ignoré par Git.
- [ ] Séparer les comptes techniques :
  - application
  - Airflow
  - dbt
  - Power BI
- [ ] Limiter Power BI à un accès lecture seule.
- [ ] Sécuriser les paramètres SMTP.
- [ ] Vérifier les mots de passe PostgreSQL.
- [ ] Prévoir une stratégie de sauvegarde PostgreSQL.

## 19. Monitoring

- [ ] Suivre les exécutions Airflow.
- [ ] Ajouter des logs détaillés par tâche.
- [ ] Ajouter des retries.
- [ ] Ajouter des alertes en cas d'échec.
- [ ] Ajouter un SLA pour les pipelines critiques.
- [ ] Remplir `audit.pipeline_runs`.
- [ ] Remplir `audit.data_quality_results`.
- [ ] Remplir `audit.email_logs`.
- [ ] Créer un dashboard technique :
  - dernier refresh
  - durée pipeline
  - nombre de lignes traitées
  - erreurs
  - emails envoyés

## 20. Documentation

- [ ] Documenter l'architecture globale.
- [ ] Documenter chaque DAG Airflow.
- [ ] Documenter chaque modèle dbt.
- [ ] Documenter chaque KPI.
- [ ] Documenter le modèle Power BI.
- [ ] Documenter les variables d'environnement.
- [ ] Documenter les commandes de lancement.
- [ ] Documenter les erreurs fréquentes.
- [ ] Ajouter un guide de démonstration.
- [ ] Ajouter un guide de dépannage.

## 21. Démonstration finale

- [ ] Lancer PostgreSQL.
- [ ] Lancer Airflow.
- [ ] Lancer un DAG complet.
- [ ] Vérifier les tables `raw`.
- [ ] Vérifier les tables `staging`.
- [ ] Vérifier les tables `marts`.
- [ ] Vérifier les tests dbt.
- [ ] Vérifier la réception de l'email automatique.
- [ ] Ouvrir Power BI.
- [ ] Montrer les dashboards.
- [ ] Expliquer le flux complet :

```text
Données opérationnelles
  -> Airflow
  -> raw
  -> dbt staging
  -> dbt marts
  -> Power BI
  -> email automatique
```

## 22. Priorité recommandée

- [x] Créer les schemas PostgreSQL analytiques.
- [x] Créer la structure `data-engineering/`.
- [x] Ajouter Docker Compose.
- [ ] Installer et lancer Airflow.
- [ ] Créer un premier projet dbt.
- [ ] Créer un premier pipeline stock/budget.
- [ ] Créer les premiers modèles dbt :
  - `stg_articles`
  - `stg_stocks`
  - `stg_budgets`
  - `mart_dashboard_stock`
  - `mart_dashboard_budget`
- [ ] Créer un email automatique stock/budget.
- [ ] Connecter Power BI aux marts.
- [ ] Ajouter achats et ventes.
- [ ] Ajouter les tests data quality.
- [ ] Ajouter monitoring et documentation.

## 23. V1 minimale à livrer

- [ ] Airflow lancé.
- [ ] PostgreSQL connecté.
- [ ] dbt lancé par Airflow.
- [ ] `mart_dashboard_stock` créé.
- [ ] `mart_dashboard_budget` créé.
- [ ] Email d'alerte stock/budget envoyé.
- [ ] Power BI connecté aux marts.
- [ ] Documentation d'installation disponible.
- [ ] Démonstration fonctionnelle prête.

## 24. V2 avancée

- [ ] Ajouter les chargements incrémentaux.
- [ ] Ajouter les snapshots dbt.
- [ ] Ajouter la documentation dbt générée.
- [ ] Ajouter un refresh Power BI automatisé.
- [ ] Ajouter l'historisation des stocks.
- [ ] Ajouter des alertes intelligentes.
- [ ] Ajouter un dashboard technique de monitoring.
- [ ] Ajouter des contrôles avancés de qualité.
- [ ] Ajouter une orchestration complète multi-DAG.
- [ ] Ajouter une stratégie de sauvegarde et restauration.
