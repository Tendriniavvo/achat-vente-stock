# Environnement Data - Étape 2

Statut : structure créée.

## Objectif

Préparer l'environnement technique nécessaire à la couche Data Engineering.

## Livrables créés

- `data-engineering/docker-compose.yml`
- `data-engineering/.env.example`
- `data-engineering/sql/init/01_create_analytics_schemas.sql`
- `data-engineering/dbt/dbt_project.yml`
- `data-engineering/dbt/profiles/profiles.yml`
- structure Airflow
- structure dbt
- structure docs, scripts et SQL

## Services Docker prévus

```text
postgres-analytics
airflow-postgres
airflow-init
airflow-webserver
airflow-scheduler
dbt
```

## Schemas PostgreSQL analytiques

```text
raw
staging
marts
audit
```

## Tables d'audit créées

```text
audit.pipeline_runs
audit.data_quality_results
audit.email_logs
audit.alerts
```

## Points à configurer avant lancement

- Copier `.env.example` vers `.env`.
- Modifier les mots de passe.
- Modifier les paramètres SMTP.
- Vérifier que PostgreSQL opérationnel est accessible.
- Vérifier que Docker Desktop est lancé.

## Prochaine étape

Étape 3 :

- démarrer l'environnement ;
- vérifier les connexions ;
- créer les premières tables `raw` ;
- préparer les droits analytiques si besoin ;
- tester Airflow et dbt.
