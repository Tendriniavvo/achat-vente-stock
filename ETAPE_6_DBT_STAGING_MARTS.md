# Étape 6 - dbt Staging et Marts

Statut : terminé pour la V1.

Ce document résume la mise en place de dbt pour transformer les données `raw` en vues `staging` et tables `marts`.

## 1. Objectif

Transformer les données brutes chargées par Airflow dans le schema `raw` en modèles analytiques propres pour Power BI et les alertes.

Flux :

```text
raw
  -> dbt staging
  -> dbt marts
  -> Power BI / alertes
```

## 2. Modèles staging créés

Vues créées dans le schema `staging` :

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

## 3. Marts créés

Tables créées dans le schema `marts` :

```text
marts.mart_dashboard_stock
marts.mart_dashboard_budget
marts.mart_dashboard_achats
marts.mart_alertes
marts.mart_kpi_global
```

## 4. Tests dbt

Tests ajoutés :

- `unique`
- `not_null`
- `accepted_values`

Résultat final :

```text
PASS=34
WARN=0
ERROR=0
SKIP=0
TOTAL=34
```

## 5. Commandes exécutées

```powershell
docker compose run --rm dbt debug --profiles-dir /usr/app/profiles
docker compose run --rm dbt run --profiles-dir /usr/app/profiles
docker compose run --rm dbt test --profiles-dir /usr/app/profiles
```

## 6. Prochaine étape recommandée

Prochaine étape :

```text
Étape 7 - Power BI
```

Connexion recommandée :

```text
Host: localhost
Port: 5433
Database: gestion_stock_analytics
User: analytics_user
Password: analytics_password
Schema: marts
```

Tables à charger dans Power BI :

```text
mart_kpi_global
mart_dashboard_stock
mart_dashboard_budget
mart_dashboard_achats
mart_alertes
```

