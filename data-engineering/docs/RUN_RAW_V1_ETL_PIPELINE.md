# Exécuter le DAG raw V1 ETL

Ce guide explique comment lancer le premier pipeline ETL :

```text
raw_v1_etl_pipeline
```

## 1. Où est le code ?

Le fichier est local :

```text
data-engineering/airflow/dags/raw_v1_etl_pipeline.py
```

Il est exécuté par Airflow dans Docker.

## 2. Connexions Airflow nécessaires

Dans Airflow :

```text
Admin -> Connections
```

Créer ou vérifier :

### Source opérationnelle locale

```text
Connection Id: postgres_source
Connection Type: Postgres
Host: host.docker.internal
Port: 5432
Schema: gestion_stock
Login: postgres
Password: postgres
```

### Base analytique Docker

```text
Connection Id: postgres_analytics
Connection Type: Postgres
Host: postgres-analytics
Port: 5432
Schema: gestion_stock_analytics
Login: analytics_user
Password: analytics_password
```

## 3. Tables chargées

Le DAG charge :

```text
departements -> raw.raw_departements
articles -> raw.raw_articles
depots -> raw.raw_depots
fournisseurs -> raw.raw_fournisseurs
budgets -> raw.raw_budgets
demandes_achat -> raw.raw_demandes_achat
lignes_demandes_achat -> raw.raw_lignes_demandes_achat
bons_commande_fournisseur -> raw.raw_bons_commande_fournisseur
stocks -> raw.raw_stocks
mouvements_stock -> raw.raw_mouvements_stock
```

## 4. Type de chargement

La V1 utilise un full refresh :

```text
TRUNCATE table raw
INSERT données source
```

Chaque ligne reçoit :

```text
ingestion_run_id
ingested_at
```

Chaque tâche écrit aussi un log dans :

```text
audit.pipeline_runs
```

## 5. Lancer le DAG

Ouvrir Airflow :

```text
http://localhost:8085
```

Puis :

```text
DAGs -> raw_v1_etl_pipeline -> Trigger DAG
```

## 6. Vérifier les données chargées

Depuis PowerShell, dans `data-engineering/` :

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT COUNT(*) FROM raw.raw_articles;"
```

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT COUNT(*) FROM raw.raw_stocks;"
```

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT COUNT(*) FROM raw.raw_budgets;"
```

## 7. Vérifier les logs ETL

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT dag_id, task_id, status, rows_processed, created_at FROM audit.pipeline_runs ORDER BY created_at DESC LIMIT 20;"
```

## 8. Erreurs fréquentes

### `relation "articles" does not exist`

La table n'existe pas dans la base source `gestion_stock`.

### `connection refused`

Airflow Docker n'arrive pas à joindre PostgreSQL local.

Vérifier :

```text
Host: host.docker.internal
Port: 5432
```

### `password authentication failed`

Le login ou mot de passe de la connexion Airflow est incorrect.

### `column does not exist`

La structure de la table source ne correspond pas au schéma attendu.

