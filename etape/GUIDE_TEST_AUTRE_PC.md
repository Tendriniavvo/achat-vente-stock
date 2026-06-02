# Guide de test sur un autre PC

Ce guide explique comment lancer et tester la couche Data Engineering du projet sur un autre ordinateur.

Objectif du test :

```text
PostgreSQL source
  -> Airflow
  -> PostgreSQL analytique
  -> raw
  -> dbt staging
  -> dbt marts
  -> Power BI
```

## 1. Prérequis à installer sur l'autre PC

Installer :

- Git ;
- Docker Desktop ;
- Power BI Desktop ;
- PostgreSQL local ou une base PostgreSQL source accessible ;
- Java / Node uniquement si on veut aussi lancer le backend ou le frontend.

Pour tester uniquement la partie Data Engineering, les éléments vraiment importants sont :

```text
Docker Desktop
PostgreSQL source avec la base gestion_stock
Power BI Desktop
```

## 2. Récupérer le projet

Dans PowerShell :

```powershell
git clone <url-du-repo>
cd achat-vente-stock
```

Si le projet est déjà copié manuellement, ouvrir simplement le dossier :

```powershell
cd C:\chemin\vers\achat-vente-stock
```

## 3. Préparer la base source locale

La base source attendue est :

```text
Host: localhost
Port: 5432
Database: gestion_stock
User: postgres
Password: postgres
```

Elle correspond à la base opérationnelle de l'application Spring Boot.

## 4. Créer la base source `gestion_stock`

Si la base n'existe pas, la créer dans PostgreSQL local :

```sql
CREATE DATABASE gestion_stock;
```

Ensuite exécuter les scripts SQL du backend dans cet ordre recommandé :

```text
backend-spring/src/main/resources/database/base.sql
backend-spring/src/main/resources/database/dept-data.sql
backend-spring/src/main/resources/database/data/data.sql
backend-spring/src/main/resources/database/data-article.sql
backend-spring/src/main/resources/database/depots-data.sql
backend-spring/src/main/resources/database/fournisseurs.sql
backend-spring/src/main/resources/database/stock-test-data.sql
backend-spring/src/main/resources/database/kpi.sql
```

L'ordre exact peut dépendre des données déjà présentes. Le script indispensable est :

```text
base.sql
```

Puis ajouter les scripts de données disponibles.

## 5. Vérifier la base source

Avec pgAdmin, DBeaver ou `psql`, vérifier :

```sql
SELECT COUNT(*) FROM articles;
SELECT COUNT(*) FROM stocks;
SELECT COUNT(*) FROM demandes_achat;
SELECT COUNT(*) FROM budgets;
```

Si certaines tables retournent `0`, le pipeline fonctionnera quand même, mais Power BI aura moins de données.

## 6. Aller dans le dossier Data Engineering

Dans PowerShell :

```powershell
cd C:\chemin\vers\achat-vente-stock\data-engineering
```

Exemple :

```powershell
cd C:\Users\Mikajy\Documents\GitHub\achat-vente-stock\data-engineering
```

## 7. Créer le fichier `.env`

Copier l'exemple :

```powershell
Copy-Item .env.example .env
```

Vérifier ces valeurs dans `.env` :

```text
SOURCE_POSTGRES_HOST=host.docker.internal
SOURCE_POSTGRES_PORT=5432
SOURCE_POSTGRES_DB=gestion_stock
SOURCE_POSTGRES_USER=postgres
SOURCE_POSTGRES_PASSWORD=postgres
```

Pourquoi `host.docker.internal` ?

Parce que Airflow tourne dans Docker et doit accéder au PostgreSQL local du PC.

## 8. Démarrer Docker Desktop

Ouvrir Docker Desktop et attendre qu'il soit bien lancé.

Vérifier dans PowerShell :

```powershell
docker --version
docker compose version
```

## 9. Démarrer PostgreSQL analytique

Depuis le dossier `data-engineering` :

```powershell
docker compose up -d postgres-analytics
```

Vérifier :

```powershell
docker compose ps
```

Le service doit être `healthy` :

```text
achat_stock_postgres_analytics   Up   healthy
```

## 10. Vérifier PostgreSQL analytique

Commande :

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT schema_name FROM information_schema.schemata WHERE schema_name IN ('raw','staging','marts','audit') ORDER BY schema_name;"
```

Résultat attendu :

```text
audit
marts
raw
staging
```

Vérifier les tables raw :

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'raw' ORDER BY table_name;"
```

## 11. Démarrer Airflow

Depuis `data-engineering` :

```powershell
docker compose up airflow-init
docker compose up -d airflow-webserver airflow-scheduler
```

Vérifier :

```powershell
docker compose ps
```

Services attendus :

```text
airflow-postgres
airflow-webserver
airflow-scheduler
postgres-analytics
```

## 12. Ouvrir Airflow

Dans le navigateur :

```text
http://localhost:8085
```

Identifiants :

```text
admin / admin
```

Tester aussi :

```text
http://localhost:8085/health
```

Si la page ne répond pas, attendre 1 à 2 minutes puis rafraîchir.

## 13. Créer les connexions Airflow

Dans Airflow :

```text
Admin -> Connections
```

Créer la connexion source :

```text
Connection Id: postgres_source
Connection Type: Postgres
Host: host.docker.internal
Port: 5432
Schema: gestion_stock
Login: postgres
Password: postgres
```

Créer la connexion analytique :

```text
Connection Id: postgres_analytics
Connection Type: Postgres
Host: postgres-analytics
Port: 5432
Schema: gestion_stock_analytics
Login: analytics_user
Password: analytics_password
```

## 14. Lancer le DAG ETL

Dans Airflow, chercher :

```text
raw_v1_etl_pipeline
```

Puis :

```text
Trigger DAG
```

Attendre que toutes les tâches deviennent vertes.

## 15. Vérifier le chargement raw

Depuis PowerShell dans `data-engineering` :

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT COUNT(*) FROM raw.raw_articles;"
```

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT COUNT(*) FROM raw.raw_stocks;"
```

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT COUNT(*) FROM raw.raw_demandes_achat;"
```

Vérifier les logs ETL :

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT dag_id, task_id, status, rows_processed, created_at FROM audit.pipeline_runs ORDER BY created_at DESC LIMIT 20;"
```

## 16. Lancer dbt

Depuis `data-engineering` :

```powershell
docker compose run --rm dbt debug --profiles-dir /usr/app/profiles
```

Si tout est OK :

```powershell
docker compose run --rm dbt run --profiles-dir /usr/app/profiles
```

Puis :

```powershell
docker compose run --rm dbt test --profiles-dir /usr/app/profiles
```

Résultat attendu :

```text
Completed successfully
```

Pour les tests :

```text
PASS=34
ERROR=0
```

## 17. Vérifier les marts

Commande :

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT table_schema, table_name FROM information_schema.tables WHERE table_schema = 'marts' ORDER BY table_name;"
```

Résultat attendu :

```text
mart_alertes
mart_dashboard_achats
mart_dashboard_budget
mart_dashboard_stock
mart_kpi_global
```

Vérifier le KPI global :

```powershell
docker compose exec -T postgres-analytics psql -U analytics_user -d gestion_stock_analytics -c "SELECT * FROM marts.mart_kpi_global;"
```

## 18. Connecter Power BI

Ouvrir Power BI Desktop.

Aller dans :

```text
Accueil -> Obtenir les données -> PostgreSQL database
```

Renseigner :

```text
Server: localhost:5433
Database: gestion_stock_analytics
```

Mode :

```text
Import
```

Identifiants :

```text
User: analytics_user
Password: analytics_password
```

Charger uniquement les tables du schema `marts` :

```text
mart_kpi_global
mart_dashboard_stock
mart_dashboard_budget
mart_dashboard_achats
mart_alertes
```

## 19. Créer un premier test Power BI simple

Créer une page :

```text
Dashboard Global
```

Ajouter des cartes depuis `mart_kpi_global` :

```text
total_articles
valeur_totale_stock
total_alertes_stock
total_alertes_budget
total_demandes_achat
total_demandes_achat_en_attente
```

Créer une page :

```text
Stock
```

Avec `mart_dashboard_stock`, ajouter une table :

```text
article_nom
depot_nom
quantite
stock_min
valeur
is_stock_alerte
```

## 20. Cycle complet de test

À chaque nouveau test :

```text
1. Modifier ou ajouter des données dans gestion_stock.
2. Lancer raw_v1_etl_pipeline dans Airflow.
3. Lancer dbt run.
4. Lancer dbt test.
5. Actualiser Power BI.
```

Commandes dbt :

```powershell
docker compose run --rm dbt run --profiles-dir /usr/app/profiles
docker compose run --rm dbt test --profiles-dir /usr/app/profiles
```

## 21. Arrêter l'environnement

Depuis `data-engineering` :

```powershell
docker compose stop
```

Pour arrêter et supprimer les conteneurs sans supprimer les volumes :

```powershell
docker compose down
```

Attention : ne pas utiliser `docker compose down -v` sauf si on veut supprimer les données PostgreSQL Docker.

## 22. Erreurs fréquentes

## Airflow ne répond pas sur 8085

Vérifier :

```powershell
docker compose ps
docker compose logs --tail 80 airflow-webserver
```

Tester :

```text
http://localhost:8085/health
```

## PostgreSQL source inaccessible depuis Airflow

Vérifier dans la connexion Airflow :

```text
Host: host.docker.internal
Port: 5432
```

Vérifier que PostgreSQL local accepte les connexions.

## dbt ne se lance pas

Relancer :

```powershell
docker compose pull dbt
docker compose run --rm dbt debug --profiles-dir /usr/app/profiles
```

## Power BI ne se connecte pas

Vérifier :

```text
Server: localhost:5433
Database: gestion_stock_analytics
User: analytics_user
Password: analytics_password
```

Vérifier que Docker expose bien le port :

```powershell
docker compose ps
```

## 23. Résultat attendu final

Si tout fonctionne, on obtient :

```text
Airflow accessible sur localhost:8085
PostgreSQL analytique accessible sur localhost:5433
DAG raw_v1_etl_pipeline réussi
Tables raw alimentées
Vues staging créées
Tables marts créées
Tests dbt réussis
Power BI connecté aux marts
```

