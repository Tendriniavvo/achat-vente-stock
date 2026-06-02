# Étape 3 - Mise en place PostgreSQL analytique

Statut : terminé pour PostgreSQL analytique V1.

Ce document résume l'implémentation de l'étape 3 de la roadmap Data Engineering.

## 1. Objectif

Mettre en place la base analytique PostgreSQL qui servira aux pipelines Airflow, aux transformations dbt et aux dashboards Power BI.

## 2. Service démarré

Le service Docker suivant a été démarré :

```text
postgres-analytics
```

Conteneur :

```text
achat_stock_postgres_analytics
```

Port local :

```text
localhost:5433
```

Base :

```text
gestion_stock_analytics
```

Utilisateur :

```text
analytics_user
```

## 3. Schemas validés

Les schemas analytiques suivants existent :

```text
audit
marts
raw
staging
```

## 4. Tables audit validées

Tables créées :

```text
audit.alerts
audit.data_quality_results
audit.email_logs
audit.pipeline_runs
```

## 5. Tables raw V1 créées

Les premières tables `raw` ont été créées pour le périmètre V1 Stock / Budget / Achats :

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

Toutes les tables contiennent :

- les colonnes sources principales ;
- `ingestion_run_id` ;
- `ingested_at`.

## 6. Scripts créés

Scripts d'initialisation automatique :

```text
data-engineering/sql/init/01_create_analytics_schemas.sql
data-engineering/sql/init/02_create_raw_v1_tables.sql
data-engineering/sql/init/03_create_analytics_permissions.sql
```

Scripts de maintenance pour une base déjà initialisée :

```text
data-engineering/sql/maintenance/create_raw_v1_tables.sql
data-engineering/sql/maintenance/create_analytics_permissions.sql
```

## 7. Rôles analytiques créés

Rôles PostgreSQL créés :

```text
analytics_etl_role
analytics_dbt_role
analytics_bi_role
```

Usage recommandé :

- `analytics_etl_role` : chargement des données dans `raw` et logs `audit` ;
- `analytics_dbt_role` : lecture `raw`, écriture `staging` et `marts` ;
- `analytics_bi_role` : lecture seule sur `staging` et `marts`.

## 8. Vérifications réalisées

- Schemas analytiques : validés.
- Tables `audit` : 4 tables validées.
- Tables `raw` V1 : 10 tables validées.
- Rôles analytiques : 3 rôles validés.

## 9. Point bloquant dbt

La commande suivante a été tentée :

```powershell
docker compose run --rm dbt debug --profiles-dir /usr/app/profiles
```

Puis le téléchargement explicite :

```powershell
docker compose pull dbt
```

Résultat :

- PostgreSQL analytique fonctionne ;
- l'image dbt `ghcr.io/dbt-labs/dbt-postgres:1.8.2` n'a pas terminé son téléchargement après plusieurs minutes ;
- `dbt debug` reste donc à relancer quand le téléchargement de l'image sera disponible.

Ce blocage ne remet pas en cause PostgreSQL analytique. Il concerne uniquement la disponibilité locale de l'image Docker dbt.

## 10. Commandes utiles

Vérifier les services :

```powershell
docker compose ps
```

Vérifier les schemas :

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT schema_name FROM information_schema.schemata WHERE schema_name IN ('raw','staging','marts','audit') ORDER BY schema_name;"
```

Vérifier les tables :

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT table_schema, table_name FROM information_schema.tables WHERE table_schema IN ('raw','audit') ORDER BY table_schema, table_name;"
```

Relancer dbt debug :

```powershell
docker compose pull dbt
docker compose run --rm dbt debug --profiles-dir /usr/app/profiles
```

## 11. Critères de réussite de l'étape 3

- [x] PostgreSQL analytique démarré.
- [x] Schemas `raw`, `staging`, `marts`, `audit` créés.
- [x] Tables d'audit créées.
- [x] Tables `raw` V1 créées.
- [x] Rôles analytiques créés.
- [x] Vérification PostgreSQL effectuée.
- [ ] `dbt debug` validé.

## 12. Prochaine étape

Prochaine étape recommandée :

```text
Étape 4 - Mise en place Apache Airflow
```

Travaux à réaliser :

- démarrer Airflow metadata DB ;
- démarrer Airflow init ;
- démarrer Airflow webserver ;
- démarrer Airflow scheduler ;
- créer les connexions Airflow PostgreSQL source et analytics ;
- créer un premier DAG de test.

