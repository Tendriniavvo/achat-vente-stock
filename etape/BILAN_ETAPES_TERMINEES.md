# Bilan des étapes déjà terminées

Ce document résume les étapes Data Engineering déjà réalisées dans le projet `achat-vente-stock`, avec leur utilité, les livrables créés et ce qu'il reste à faire.

## Vue d'ensemble

Nous avons transformé progressivement le projet initial en base de projet Data Engineering.

Flux déjà mis en place :

```text
PostgreSQL opérationnel local
  -> Airflow Docker
  -> PostgreSQL analytique Docker
  -> raw
  -> dbt staging
  -> dbt marts
  -> Power BI à connecter
```

## Étape 1 - Cadrage et analyse de l'existant

Statut : terminé.

### Ce qui a été fait

- Analyse du projet existant.
- Identification des modules métier :
  - achats ;
  - ventes ;
  - stock ;
  - budget ;
  - inventaire ;
  - audit ;
  - référentiels.
- Identification des tables sources PostgreSQL.
- Définition des KPI prioritaires.
- Définition des dashboards attendus.
- Définition des alertes email attendues.
- Identification des données manquantes ou à confirmer.

### Utilité

Cette étape sert à éviter de construire une architecture Data Engineering générique. Elle fixe le périmètre métier réel du projet et permet de savoir quelles tables et quels KPI traiter en priorité.

### Livrable créé

```text
ETAPE_1_CADRAGE_DATA_ENGINEERING.md
```

### Résultat

Le périmètre V1 recommandé est :

```text
Stock + Budget + Achats simples + Email automatique + Power BI
```

## Étape 2 - Préparation de l'environnement Data

Statut : terminé.

### Ce qui a été fait

Création du dossier :

```text
data-engineering/
```

Création de la structure :

```text
data-engineering/
  airflow/
    dags/
    logs/
    plugins/
  dbt/
    models/
      staging/
      marts/
    profiles/
    seeds/
    snapshots/
    macros/
    tests/
  docs/
  scripts/
  sql/
    init/
  docker-compose.yml
  .env.example
  README.md
```

Création de la configuration Docker :

```text
data-engineering/docker-compose.yml
```

Services préparés :

- PostgreSQL analytique ;
- PostgreSQL metadata Airflow ;
- Airflow init ;
- Airflow webserver ;
- Airflow scheduler ;
- dbt.

### Utilité

Cette étape crée l'environnement technique qui permet d'exécuter les pipelines Data Engineering avec Docker, sans mélanger la logique data avec le backend Spring Boot ou le frontend Vue.

### Livrables créés

```text
ETAPE_2_PREPARATION_ENVIRONNEMENT_DATA.md
data-engineering/README.md
data-engineering/docs/ENVIRONMENT_SETUP.md
```

### Résultat

Airflow est accessible sur :

```text
http://localhost:8085
```

PostgreSQL analytique est accessible en local sur :

```text
localhost:5433
```

## Étape 3 - PostgreSQL analytique

Statut : terminé pour la V1.

### Ce qui a été fait

Création et validation des schemas analytiques :

```text
raw
staging
marts
audit
```

Création des tables d'audit :

```text
audit.pipeline_runs
audit.data_quality_results
audit.email_logs
audit.alerts
```

Création des tables `raw` V1 :

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

Création des rôles analytiques :

```text
analytics_etl_role
analytics_dbt_role
analytics_bi_role
```

### Utilité

Cette étape sépare les données opérationnelles des données analytiques.

Le schema `raw` reçoit les données brutes.  
Le schema `staging` reçoit les vues nettoyées par dbt.  
Le schema `marts` reçoit les tables finales pour Power BI.  
Le schema `audit` trace les exécutions, tests, emails et alertes.

### Livrable créé

```text
ETAPE_3_POSTGRESQL_ANALYTIQUE.md
```

### Résultat

PostgreSQL analytique Docker fonctionne et contient les structures nécessaires pour les pipelines.

## Étape 4 - Airflow opérationnel

Statut : fonctionnel pour la V1.

### Ce qui a été fait

- Airflow webserver lancé.
- Airflow scheduler lancé.
- Airflow accessible sur le port `8085`.
- Correction du port initial `8080` vers `8085`.
- Correction d'un blocage de démarrage causé par l'installation de `dbt-postgres` dans les conteneurs Airflow.
- Séparation correcte :
  - Airflow exécute les DAGs ;
  - dbt garde son propre conteneur.

### Utilité

Airflow sert à orchestrer les pipelines :

```text
extraction -> chargement raw -> dbt run -> dbt test -> alertes -> email
```

Dans la V1 actuelle, Airflow sert déjà à exécuter le pipeline ETL vers `raw`.

### Chemins utiles

```text
data-engineering/airflow/dags/
data-engineering/docker-compose.yml
```

### URL

```text
http://localhost:8085
```

Identifiants :

```text
admin / admin
```

## Étape 5 - Pipeline ETL raw V1

Statut : implémenté et lancé avec succès selon le retour utilisateur.

### Ce qui a été fait

Création du DAG :

```text
data-engineering/airflow/dags/raw_v1_etl_pipeline.py
```

Ce DAG charge les données depuis PostgreSQL source local vers PostgreSQL analytique Docker.

Flux :

```text
gestion_stock local
  -> Airflow Docker
  -> gestion_stock_analytics Docker
  -> schema raw
```

Tables chargées :

```text
departements
articles
depots
fournisseurs
budgets
demandes_achat
lignes_demandes_achat
bons_commande_fournisseur
stocks
mouvements_stock
```

### Utilité

Cette étape automatise la copie des données opérationnelles vers la zone analytique brute.

Chaque tâche :

- lit la table source ;
- vide la table `raw` cible ;
- insère les nouvelles données ;
- ajoute `ingestion_run_id` ;
- ajoute `ingested_at` ;
- écrit un log dans `audit.pipeline_runs`.

### Documentation créée

```text
data-engineering/docs/RUN_RAW_V1_ETL_PIPELINE.md
```

## Étape 6 - dbt staging et marts

Statut : terminé pour la V1.

### Ce qui a été fait

Création des sources dbt :

```text
data-engineering/dbt/models/sources.yml
```

Création des modèles `staging` :

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

Création des modèles `marts` :

```text
marts.mart_dashboard_stock
marts.mart_dashboard_budget
marts.mart_dashboard_achats
marts.mart_alertes
marts.mart_kpi_global
```

Création de la macro :

```text
data-engineering/dbt/macros/generate_schema_name.sql
```

### Utilité

dbt transforme les données brutes en données propres et exploitables.

Les vues `staging` normalisent les noms de colonnes et préparent les données.  
Les tables `marts` servent directement à Power BI et aux alertes.

### Validation

Commandes validées :

```powershell
docker compose run --rm dbt debug --profiles-dir /usr/app/profiles
docker compose run --rm dbt run --profiles-dir /usr/app/profiles
docker compose run --rm dbt test --profiles-dir /usr/app/profiles
```

Résultat des tests :

```text
34 tests passés
0 erreur
```

### Livrable créé

```text
ETAPE_6_DBT_STAGING_MARTS.md
```

## Ce qu'il reste à faire

## Prochaine priorité 1 - Power BI

À faire :

- connecter Power BI à PostgreSQL analytique ;
- charger uniquement les tables `marts` ;
- créer les pages :
  - Dashboard global ;
  - Stock ;
  - Budget ;
  - Achats ;
  - Alertes.

Connexion Power BI :

```text
Host: localhost
Port: 5433
Database: gestion_stock_analytics
User: analytics_user
Password: analytics_password
Schema: marts
```

## Prochaine priorité 2 - Intégrer dbt dans Airflow

À faire :

- ajouter une tâche Airflow `dbt run` ;
- ajouter une tâche Airflow `dbt test` ;
- faire enchaîner :

```text
raw_v1_etl_pipeline
  -> dbt run
  -> dbt test
```

## Prochaine priorité 3 - Emails automatiques

À faire :

- configurer SMTP ;
- lire `marts.mart_alertes` ;
- envoyer :
  - email stock critique ;
  - email budget alerte ;
  - rapport quotidien.

## Prochaine priorité 4 - Industrialisation

À faire :

- génération de documentation dbt ;
- monitoring Airflow ;
- chargement incrémental ;
- snapshots de stock ;
- refresh Power BI automatisé ;
- sauvegarde PostgreSQL ;
- sécurité des secrets.

