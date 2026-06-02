# Étape 2 - Préparation de l'environnement Data

Statut : terminé pour la structure initiale.

Ce document résume l'implémentation de l'étape 2 de la roadmap Data Engineering.

## 1. Objectif

Préparer l'environnement technique qui servira de base aux pipelines Data Engineering :

- structure de dossiers ;
- configuration Docker Compose ;
- PostgreSQL analytique ;
- Airflow ;
- dbt ;
- scripts SQL d'initialisation ;
- variables d'environnement ;
- documentation de démarrage.

## 2. Livrables créés

```text
data-engineering/
  airflow/
    dags/
    logs/
    plugins/
  dbt/
    analyses/
    macros/
    models/
      staging/
      marts/
    profiles/
    seeds/
    snapshots/
    tests/
  docs/
  scripts/
  sql/
    init/
  docker-compose.yml
  .env.example
  README.md
```

## 3. Fichiers principaux

### Configuration Docker

Fichier :

```text
data-engineering/docker-compose.yml
```

Services configurés :

```text
postgres-analytics
airflow-postgres
airflow-init
airflow-webserver
airflow-scheduler
dbt
```

### Variables d'environnement

Fichier :

```text
data-engineering/.env.example
```

Variables préparées :

- PostgreSQL analytique ;
- PostgreSQL opérationnel existant ;
- Airflow ;
- SMTP ;
- destinataires emails.

### Initialisation PostgreSQL analytique

Fichier :

```text
data-engineering/sql/init/01_create_analytics_schemas.sql
```

Schemas créés :

```text
raw
staging
marts
audit
```

Tables d'audit créées :

```text
audit.pipeline_runs
audit.data_quality_results
audit.email_logs
audit.alerts
```

### Projet dbt

Fichiers :

```text
data-engineering/dbt/dbt_project.yml
data-engineering/dbt/profiles/profiles.yml
```

Schemas dbt prévus :

- modèles `staging` matérialisés en vues ;
- modèles `marts` matérialisés en tables.

## 4. Modification Git

Le fichier `.gitignore` a été mis à jour pour permettre de versionner les exemples d'environnement :

```text
!.env.example
!data-engineering/.env.example
```

Le vrai fichier `.env` reste ignoré.

## 5. Commandes de démarrage prévues

Depuis le dossier `data-engineering/` :

```powershell
Copy-Item .env.example .env
docker compose up airflow-init
docker compose up -d
```

Airflow :

```text
http://localhost:8085
```

Identifiants par défaut :

```text
admin / admin
```

PostgreSQL analytique local :

```text
localhost:5433
```

## 6. Commandes dbt prévues

Tester la connexion :

```powershell
docker compose run --rm dbt debug --profiles-dir /usr/app/profiles
```

Lancer les modèles :

```powershell
docker compose run --rm dbt run --profiles-dir /usr/app/profiles
```

Lancer les tests :

```powershell
docker compose run --rm dbt test --profiles-dir /usr/app/profiles
```

## 7. Décisions techniques

### PostgreSQL analytique séparé dans Docker

La V1 utilise un service Docker `postgres-analytics` exposé sur le port local `5433`.

Avantages :

- séparation claire entre application opérationnelle et analytique ;
- moins de risque de modifier la base Spring Boot ;
- environnement plus simple à démontrer.

### Source opérationnelle

Le PostgreSQL opérationnel existant reste la source.

Depuis Docker, la connexion source est préparée avec :

```text
SOURCE_POSTGRES_HOST=host.docker.internal
SOURCE_POSTGRES_PORT=5432
SOURCE_POSTGRES_DB=gestion_stock
```

### Airflow

Airflow utilise une base metadata séparée `airflow-postgres`.

### dbt

dbt se connecte à `postgres-analytics` et écrira dans les schemas analytiques.

## 8. Critères de réussite de l'étape 2

- [x] Dossier `data-engineering/` créé.
- [x] Structure Airflow créée.
- [x] Structure dbt créée.
- [x] Structure SQL créée.
- [x] Structure documentation créée.
- [x] `.env.example` créé.
- [x] `docker-compose.yml` créé.
- [x] Script SQL de schemas analytiques créé.
- [x] Tables d'audit initiales créées dans le script.
- [x] Projet dbt initial créé.
- [x] Documentation de démarrage créée.

## 9. Prochaine étape

Prochaine étape recommandée :

```text
Étape 3 - Mise en place PostgreSQL analytique
```

Travaux à réaliser :

- démarrer les services Docker ;
- vérifier la création des schemas ;
- tester la connexion à PostgreSQL analytique ;
- tester `dbt debug` ;
- préparer les premières tables `raw` ;
- préparer les droits analytiques si nécessaire.
