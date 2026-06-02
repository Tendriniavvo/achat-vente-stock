# Principes et fonctionnement de ce qui existe déjà

Ce document explique le principe de l'architecture Data Engineering déjà mise en place, ce qu'elle permet de faire, comment elle fonctionne et où se trouvent les fichiers importants.

## 1. Principe général

L'application existante reste l'application opérationnelle :

```text
backend-spring + frontend-vue + PostgreSQL source
```

La nouvelle couche Data Engineering sert à automatiser le traitement analytique :

```text
Airflow + PostgreSQL analytique + dbt + Power BI
```

Le principe est simple :

```text
1. L'application produit des données métier.
2. Airflow copie ces données vers la zone raw.
3. dbt nettoie et transforme les données.
4. Les marts sont créés pour Power BI.
5. Les alertes et emails peuvent être générés depuis les marts.
```

## 2. Architecture actuelle

```text
PostgreSQL source local : gestion_stock
  -> Airflow Docker
  -> PostgreSQL analytique Docker : gestion_stock_analytics
  -> raw
  -> staging
  -> marts
  -> Power BI local
```

## 3. Docker ou local ?

## Local

Éléments locaux :

```text
backend-spring
frontend-vue
PostgreSQL source : localhost:5432 / gestion_stock
Power BI Desktop
fichiers du projet
```

## Docker

Éléments Docker :

```text
Airflow webserver
Airflow scheduler
Airflow metadata database
PostgreSQL analytique
dbt
```

Ports :

```text
Airflow : http://localhost:8085
PostgreSQL analytique : localhost:5433
```

## 4. Fonctionnement des schemas PostgreSQL analytiques

## raw

Chemin logique :

```text
gestion_stock source -> raw
```

Utilité :

- stocker les données brutes copiées depuis la base opérationnelle ;
- garder une trace d'ingestion ;
- servir de source à dbt.

Tables :

```text
raw.raw_articles
raw.raw_stocks
raw.raw_depots
raw.raw_departements
raw.raw_budgets
raw.raw_demandes_achat
raw.raw_lignes_demandes_achat
raw.raw_bons_commande_fournisseur
raw.raw_fournisseurs
raw.raw_mouvements_stock
```

## staging

Chemin logique :

```text
raw -> dbt -> staging
```

Utilité :

- nettoyer les noms de colonnes ;
- préparer les données ;
- rendre les données plus lisibles ;
- éviter d'utiliser directement les tables brutes.

Vues :

```text
staging.stg_articles
staging.stg_stocks
staging.stg_depots
staging.stg_departements
staging.stg_budgets
staging.stg_demandes_achat
staging.stg_lignes_demandes_achat
staging.stg_bons_commande_fournisseur
staging.stg_fournisseurs
staging.stg_mouvements_stock
```

## marts

Chemin logique :

```text
staging -> dbt -> marts
```

Utilité :

- fournir les tables finales pour Power BI ;
- centraliser les KPI ;
- produire les alertes métier ;
- réduire la logique complexe dans Power BI.

Tables :

```text
marts.mart_kpi_global
marts.mart_dashboard_stock
marts.mart_dashboard_budget
marts.mart_dashboard_achats
marts.mart_alertes
```

## audit

Utilité :

- suivre les exécutions ;
- suivre les erreurs ;
- suivre les emails ;
- stocker les alertes générées.

Tables :

```text
audit.pipeline_runs
audit.data_quality_results
audit.email_logs
audit.alerts
```

## 5. Fonctionnement Airflow

Airflow orchestre les traitements.

URL :

```text
http://localhost:8085
```

Identifiants :

```text
admin / admin
```

Chemin des DAGs :

```text
data-engineering/airflow/dags/
```

DAG existant :

```text
data-engineering/airflow/dags/raw_v1_etl_pipeline.py
```

Ce DAG :

- lit les tables dans PostgreSQL source ;
- vide les tables `raw` correspondantes ;
- insère les données ;
- écrit les logs dans `audit.pipeline_runs`.

Connexions Airflow nécessaires :

```text
postgres_source
postgres_analytics
```

Connexion source depuis Airflow Docker :

```text
Host: host.docker.internal
Port: 5432
Database: gestion_stock
```

Connexion analytique depuis Airflow Docker :

```text
Host: postgres-analytics
Port: 5432
Database: gestion_stock_analytics
```

## 6. Fonctionnement dbt

dbt transforme les données.

Chemin du projet dbt :

```text
data-engineering/dbt/
```

Configuration :

```text
data-engineering/dbt/dbt_project.yml
data-engineering/dbt/profiles/profiles.yml
```

Sources :

```text
data-engineering/dbt/models/sources.yml
```

Modèles staging :

```text
data-engineering/dbt/models/staging/
```

Modèles marts :

```text
data-engineering/dbt/models/marts/
```

Macro schema :

```text
data-engineering/dbt/macros/generate_schema_name.sql
```

Commandes :

```powershell
cd C:\Users\Mikajy\Documents\GitHub\achat-vente-stock\data-engineering
docker compose run --rm dbt debug --profiles-dir /usr/app/profiles
docker compose run --rm dbt run --profiles-dir /usr/app/profiles
docker compose run --rm dbt test --profiles-dir /usr/app/profiles
```

## 7. Fonctionnement du pipeline complet actuel

Le cycle actuel est :

```text
1. Données dans gestion_stock local.
2. Lancement du DAG raw_v1_etl_pipeline dans Airflow.
3. Données copiées dans raw.
4. Lancement de dbt run.
5. Création des vues staging.
6. Création des tables marts.
7. Lancement de dbt test.
8. Power BI peut lire les marts.
```

## 8. Ce qu'on peut déjà faire

Avec ce qui est déjà en place, on peut :

- automatiser le chargement des données achats, stock et budget vers `raw` ;
- créer des vues propres dans `staging` ;
- générer des tables finales dans `marts` ;
- tester la qualité de base des données avec dbt ;
- connecter Power BI aux tables `marts` ;
- construire un dashboard global ;
- construire un dashboard stock ;
- construire un dashboard budget ;
- construire un dashboard achats ;
- construire une page alertes.

## 9. Fonctionnalités existantes par chemin

## Docker Compose

Chemin :

```text
data-engineering/docker-compose.yml
```

Utilité :

- démarrer PostgreSQL analytique ;
- démarrer Airflow ;
- démarrer dbt ;
- gérer les volumes et ports.

## Variables d'environnement

Chemin :

```text
data-engineering/.env.example
```

Utilité :

- documenter les paramètres PostgreSQL ;
- documenter les paramètres Airflow ;
- documenter les paramètres SMTP ;
- éviter de versionner les secrets réels.

## Scripts SQL

Chemins :

```text
data-engineering/sql/init/
data-engineering/sql/maintenance/
```

Utilité :

- créer les schemas ;
- créer les tables raw ;
- créer les rôles analytiques ;
- appliquer manuellement des scripts sur une base déjà lancée.

## DAG Airflow ETL

Chemin :

```text
data-engineering/airflow/dags/raw_v1_etl_pipeline.py
```

Utilité :

- charger les données de la source vers `raw`.

## Documentation du DAG

Chemin :

```text
data-engineering/docs/RUN_RAW_V1_ETL_PIPELINE.md
```

Utilité :

- expliquer comment lancer le DAG ;
- expliquer comment vérifier les tables raw ;
- expliquer les erreurs fréquentes.

## Modèles dbt

Chemins :

```text
data-engineering/dbt/models/staging/
data-engineering/dbt/models/marts/
```

Utilité :

- nettoyer les données ;
- créer les KPI ;
- préparer les tables Power BI.

## Livrables d'étapes

Chemins :

```text
ETAPE_1_CADRAGE_DATA_ENGINEERING.md
ETAPE_2_PREPARATION_ENVIRONNEMENT_DATA.md
ETAPE_3_POSTGRESQL_ANALYTIQUE.md
ETAPE_6_DBT_STAGING_MARTS.md
```

Utilité :

- expliquer chaque étape déjà terminée ;
- garder une trace du travail ;
- faciliter la présentation du projet.

## 10. Fonctionnement des tables marts

## mart_kpi_global

Utilité :

- regrouper les KPI principaux dans une seule table.

Contient :

```text
total_articles
total_alertes_stock
valeur_totale_stock
total_alertes_budget
budget_initial_total
budget_consomme_total
budget_disponible_total
total_demandes_achat
total_demandes_achat_en_attente
calculated_at
```

Usage :

- cards KPI Power BI.

## mart_dashboard_stock

Utilité :

- analyser le stock par article et dépôt ;
- détecter les stocks faibles.

Usage :

- dashboard stock ;
- alertes stock.

## mart_dashboard_budget

Utilité :

- suivre la consommation budgétaire par département ;
- détecter les budgets à risque.

Usage :

- dashboard budget ;
- alertes budget.

## mart_dashboard_achats

Utilité :

- suivre les demandes d'achat ;
- suivre les montants estimés ;
- suivre les commandes fournisseurs liées.

Usage :

- dashboard achats.

## mart_alertes

Utilité :

- centraliser les alertes stock et budget.

Usage :

- page alertes Power BI ;
- futurs emails automatiques.

## 11. Ce qu'il reste à construire

## Power BI

À faire :

- connecter Power BI à `localhost:5433` ;
- charger les tables `marts` ;
- créer les pages métier.

## Intégration dbt dans Airflow

À faire :

- ajouter une tâche Airflow pour `dbt run` ;
- ajouter une tâche Airflow pour `dbt test` ;
- créer un pipeline complet automatisé.

## Emails automatiques

À faire :

- configurer SMTP ;
- lire `marts.mart_alertes` ;
- envoyer les alertes.

## Ventes et finance

À faire :

- charger les commandes clients ;
- charger les factures clients ;
- charger les encaissements ;
- créer les marts ventes et finance.

## Industrialisation

À faire :

- chargements incrémentaux ;
- snapshots dbt ;
- documentation dbt générée ;
- monitoring ;
- refresh Power BI ;
- sauvegarde PostgreSQL.

