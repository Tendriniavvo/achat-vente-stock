# Data Engineering - Achat Vente Stock

Ce dossier contient la couche Data Engineering du projet :

- Apache Airflow pour orchestrer les pipelines ;
- PostgreSQL analytique pour les schemas `raw`, `staging`, `marts` et `audit` ;
- dbt pour transformer et tester les données ;
- scripts SQL pour initialiser l'environnement ;
- base de configuration pour les emails automatiques.

## Structure

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
```

## Démarrage local

Depuis ce dossier :

```powershell
Copy-Item .env.example .env
docker compose up airflow-init
docker compose up -d
```

Airflow sera disponible sur :

```text
http://localhost:8085
```

Identifiants par défaut :

```text
admin / admin
```

PostgreSQL analytique sera exposé en local sur :

```text
localhost:5433
```

## Connexion à la base opérationnelle

Le backend existant utilise par défaut :

```text
localhost:5432/gestion_stock
```

Depuis les conteneurs Docker, la variable suivante pointe vers le PostgreSQL local Windows :

```text
SOURCE_POSTGRES_HOST=host.docker.internal
```

## Schemas analytiques

Le service `postgres-analytics` initialise automatiquement :

- `raw` : données brutes extraites depuis la base opérationnelle ;
- `staging` : données nettoyées et normalisées ;
- `marts` : tables finales pour Power BI ;
- `audit` : logs pipelines, tests qualité, emails et alertes.

## Commandes utiles

Tester dbt :

```powershell
docker compose run --rm dbt debug --profiles-dir /usr/app/profiles
```

Lancer dbt :

```powershell
docker compose run --rm dbt run --profiles-dir /usr/app/profiles
```

Lancer les tests dbt :

```powershell
docker compose run --rm dbt test --profiles-dir /usr/app/profiles
```

## Prochaine étape

Après cette préparation, l'étape suivante consiste à créer les premiers pipelines :

- extraction des tables `articles`, `stocks`, `budgets`, `departements` ;
- chargement dans `raw` ;
- création des premiers modèles dbt `staging` et `marts` ;
- premier DAG Airflow de test.
