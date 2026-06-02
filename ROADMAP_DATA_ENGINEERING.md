# Roadmap - Implementation Data Engineering

Ce fichier divise le projet Data Engineering en grandes étapes pour faciliter l'organisation, l'implémentation et la répartition des tâches.

Le fichier `TODO_DATA_ENGINEERING.md` contient la checklist complète détaillée. Ce fichier sert plutôt de feuille de route globale.

## Vue globale

Architecture cible :

```text
Application Spring Boot / Vue.js
  -> PostgreSQL opérationnel
  -> Apache Airflow
  -> raw / staging / marts
  -> dbt
  -> Power BI
  -> emails automatiques
```

## Étape 1 - Cadrage et analyse de l'existant

Statut : terminé.

Livrable associé : `ETAPE_1_CADRAGE_DATA_ENGINEERING.md`.

### Objectif

Comprendre les données déjà présentes dans le projet et définir les besoins décisionnels.

### Tâches principales

- Identifier les tables existantes dans PostgreSQL.
- Identifier les modules métier :
  - Achats
  - Ventes
  - Stock
  - Budget
  - Finance
  - Utilisateurs
- Définir les KPI prioritaires.
- Définir les dashboards Power BI attendus.
- Définir les alertes email attendues.
- Identifier les données manquantes.

### Livrables

- Liste des tables sources.
- Liste des KPI.
- Liste des alertes.
- Schéma métier global.
- Décision sur la stratégie analytique :
  - schemas dans la même base
  - ou base analytique séparée

### Priorité

Très haute.

### Dépendances

Aucune.

## Étape 2 - Préparation de l'environnement Data

Statut : terminé.

Livrable associé : `ETAPE_2_PREPARATION_ENVIRONNEMENT_DATA.md`.

### Objectif

Créer la base technique qui permettra d'exécuter les pipelines data.

### Tâches principales

- Créer le dossier `data-engineering/`.
- Préparer l'organisation des sous-dossiers :
  - Airflow
  - dbt
  - SQL
  - scripts
  - documentation
- Créer les schemas PostgreSQL :
  - `raw`
  - `staging`
  - `marts`
  - `audit`
- Créer les utilisateurs techniques PostgreSQL.
- Préparer le fichier `.env.example`.
- Préparer Docker Compose.

### Livrables

- Structure de dossiers prête.
- Schemas analytiques créés.
- Environnement Docker préparé.
- Variables d'environnement documentées.

### Priorité

Très haute.

### Dépendances

Étape 1.

## Étape 3 - Mise en place PostgreSQL analytique

Statut : terminé pour PostgreSQL analytique V1. `dbt debug` reste à relancer quand l'image Docker dbt sera disponible.

Livrable associé : `ETAPE_3_POSTGRESQL_ANALYTIQUE.md`.

### Objectif

Séparer proprement les données opérationnelles et les données analytiques.

### Tâches principales

- Créer les tables techniques d'audit :
  - `audit.pipeline_runs`
  - `audit.data_quality_results`
  - `audit.email_logs`
  - `audit.alerts`
- Créer les premières tables `raw`.
- Préparer les droits d'accès.
- Vérifier que Power BI pourra accéder uniquement aux tables finales.

### Livrables

- Schemas PostgreSQL opérationnels.
- Tables d'audit créées.
- Droits utilisateurs configurés.

### Priorité

Haute.

### Dépendances

Étape 2.

## Étape 4 - Mise en place Apache Airflow

### Objectif

Automatiser et orchestrer les traitements data.

### Tâches principales

- Installer et lancer Airflow.
- Configurer Airflow Webserver.
- Configurer Airflow Scheduler.
- Configurer les connexions :
  - PostgreSQL source
  - PostgreSQL analytics
  - SMTP
- Créer un premier DAG de test.
- Créer les DAGs principaux :
  - `daily_stock_pipeline`
  - `daily_budget_pipeline`
  - `daily_purchase_pipeline`
  - `daily_sales_pipeline`
  - `daily_alert_email_pipeline`

### Livrables

- Airflow UI accessible.
- Connexions Airflow configurées.
- Premier DAG fonctionnel.
- Pipelines planifiés.

### Priorité

Haute.

### Dépendances

Étape 3.

## Étape 5 - Extraction et chargement des données

### Objectif

Extraire les données opérationnelles et les charger dans la zone `raw`.

### Tâches principales

- Extraire les données depuis les tables sources.
- Charger les données dans `raw`.
- Ajouter un timestamp d'ingestion.
- Ajouter un identifiant d'exécution.
- Journaliser les exécutions dans `audit.pipeline_runs`.
- Mettre en place un chargement full refresh pour la V1.
- Prévoir un chargement incrémental pour la V2.

### Livrables

- Tables `raw` alimentées.
- Logs d'exécution disponibles.
- Pipeline ETL de base fonctionnel.

### Priorité

Haute.

### Dépendances

Étape 4.

## Étape 6 - Mise en place dbt

Statut : terminé pour la V1.

Livrable associé : `ETAPE_6_DBT_STAGING_MARTS.md`.

### Objectif

Transformer les données brutes en modèles propres, testés et exploitables.

### Tâches principales

- Créer le projet dbt.
- Configurer `profiles.yml`.
- Déclarer les sources.
- Créer les modèles `staging`.
- Créer les dimensions.
- Créer les tables de faits.
- Créer les marts.
- Ajouter les tests dbt.
- Générer la documentation dbt.
- Intégrer `dbt run` et `dbt test` dans Airflow.

### Livrables

- Projet dbt fonctionnel.
- Modèles staging créés.
- Dimensions créées.
- Faits créés.
- Marts créés.
- Tests dbt exécutables.

### Priorité

Très haute.

### Dépendances

Étape 5.

## Étape 7 - Construction des marts métier

### Objectif

Créer les tables finales utilisées par Power BI et les emails automatiques.

### Tâches principales

- Créer `mart_dashboard_global`.
- Créer `mart_dashboard_stock`.
- Créer `mart_dashboard_budget`.
- Créer `mart_dashboard_achats`.
- Créer `mart_dashboard_ventes`.
- Créer `mart_alertes`.
- Vérifier que chaque mart contient des données simples à exploiter.
- Réduire la logique complexe côté Power BI.

### Livrables

- Marts métier disponibles.
- Données prêtes pour Power BI.
- Données prêtes pour les alertes email.

### Priorité

Très haute.

### Dépendances

Étape 6.

## Étape 8 - Qualité des données

### Objectif

Garantir que les données produites sont fiables.

### Tâches principales

- Ajouter des tests `not_null`.
- Ajouter des tests `unique`.
- Ajouter des tests de relations.
- Ajouter des tests de valeurs acceptées.
- Vérifier les montants négatifs.
- Vérifier les dates invalides.
- Vérifier les statuts inconnus.
- Journaliser les résultats dans `audit.data_quality_results`.
- Ajouter une règle d'échec si une erreur critique est détectée.

### Livrables

- Tests dbt disponibles.
- Contrôles qualité Airflow.
- Journalisation des erreurs data.

### Priorité

Moyenne à haute.

### Dépendances

Étape 6 et Étape 7.

## Étape 9 - Emails automatiques

### Objectif

Envoyer automatiquement des rapports et alertes aux utilisateurs concernés.

### Tâches principales

- Configurer SMTP.
- Créer un template email HTML.
- Créer un email d'alerte stock.
- Créer un email d'alerte budget.
- Créer un email de rapport journalier.
- Définir les destinataires.
- Ajouter les tâches email dans Airflow.
- Enregistrer les envois dans `audit.email_logs`.

### Livrables

- Email stock critique.
- Email budget dépassé.
- Email résumé quotidien.
- Logs d'emails envoyés.

### Priorité

Haute.

### Dépendances

Étape 7.

## Étape 10 - Power BI

### Objectif

Créer les tableaux de bord décisionnels à partir des marts.

### Tâches principales

- Connecter Power BI à PostgreSQL.
- Charger les tables du schema `marts`.
- Créer le modèle relationnel.
- Créer les mesures DAX nécessaires.
- Créer les pages :
  - Dashboard global
  - Achats
  - Ventes
  - Stock
  - Budget
  - Alertes
- Ajouter les filtres.
- Publier le rapport si Power BI Service est disponible.
- Configurer le refresh.

### Livrables

- Rapport Power BI connecté aux marts.
- Dashboards métier.
- Refresh configuré.

### Priorité

Haute.

### Dépendances

Étape 7.

## Étape 11 - Sécurité et configuration

### Objectif

Sécuriser les accès, les mots de passe et les clés.

### Tâches principales

- Retirer les clés codées en dur.
- Utiliser `.env`.
- Créer `.env.example`.
- Vérifier `.gitignore`.
- Créer des comptes séparés :
  - application
  - Airflow
  - dbt
  - Power BI
- Limiter Power BI en lecture seule.
- Sécuriser SMTP.
- Prévoir une sauvegarde PostgreSQL.

### Livrables

- Secrets externalisés.
- Accès sécurisés.
- Configuration documentée.

### Priorité

Haute.

### Dépendances

Peut être fait en parallèle avec les étapes 2 à 10.

## Étape 12 - Monitoring et observabilité

### Objectif

Suivre l'état des pipelines et détecter rapidement les problèmes.

### Tâches principales

- Suivre les runs Airflow.
- Ajouter des retries.
- Ajouter des alertes en cas d'échec.
- Ajouter des SLA.
- Remplir les tables d'audit.
- Créer un dashboard technique :
  - dernier refresh
  - durée pipeline
  - nombre de lignes traitées
  - erreurs
  - emails envoyés

### Livrables

- Monitoring Airflow.
- Tables d'audit alimentées.
- Dashboard technique.

### Priorité

Moyenne.

### Dépendances

Étapes 4 à 9.

## Étape 13 - Documentation

### Objectif

Rendre le projet compréhensible, maintenable et présentable.

### Tâches principales

- Documenter l'architecture.
- Documenter les DAGs.
- Documenter les modèles dbt.
- Documenter les KPI.
- Documenter les dashboards Power BI.
- Documenter les variables d'environnement.
- Documenter les commandes de lancement.
- Documenter les erreurs fréquentes.
- Préparer un guide de démonstration.

### Livrables

- Documentation technique.
- Documentation fonctionnelle.
- Guide de démo.
- Guide de dépannage.

### Priorité

Moyenne à haute.

### Dépendances

Peut être commencé dès l'Étape 1.

## Étape 14 - Démonstration finale

### Objectif

Présenter un flux complet, automatisé et fonctionnel.

### Tâches principales

- Lancer PostgreSQL.
- Lancer Airflow.
- Lancer le DAG principal.
- Vérifier les tables `raw`.
- Vérifier les tables `staging`.
- Vérifier les tables `marts`.
- Vérifier les tests dbt.
- Vérifier la réception des emails.
- Ouvrir Power BI.
- Présenter les dashboards.
- Expliquer le flux de bout en bout.

### Livrables

- Démo fonctionnelle.
- Pipeline complet exécuté.
- Email reçu.
- Dashboard Power BI alimenté.

### Priorité

Très haute pour la livraison finale.

### Dépendances

Toutes les étapes précédentes.

## Découpage conseillé en versions

### V1 - Version minimale démontrable

Objectif : livrer rapidement une première version complète.

- [x] PostgreSQL analytique prêt.
- [ ] Airflow lancé.
- [x] dbt configuré.
- [ ] Pipeline stock/budget fonctionnel.
- [x] `mart_dashboard_stock` créé.
- [x] `mart_dashboard_budget` créé.
- [ ] Email automatique stock/budget envoyé.
- [ ] Power BI connecté aux marts.

### V2 - Version métier complète

Objectif : couvrir tous les grands modules métier.

- [ ] Ajouter le pipeline achats.
- [ ] Ajouter le pipeline ventes.
- [ ] Ajouter le pipeline factures.
- [ ] Ajouter les marts achats.
- [ ] Ajouter les marts ventes.
- [ ] Ajouter les KPI finance.
- [ ] Ajouter les pages Power BI complètes.
- [ ] Ajouter les tests qualité principaux.

### V3 - Version industrialisée

Objectif : rendre le projet plus robuste et professionnel.

- [ ] Ajouter les chargements incrémentaux.
- [ ] Ajouter les snapshots dbt.
- [ ] Ajouter la documentation dbt générée.
- [ ] Ajouter les SLA Airflow.
- [ ] Ajouter le monitoring technique.
- [ ] Ajouter le refresh Power BI automatisé.
- [ ] Ajouter une stratégie de sauvegarde.
- [ ] Ajouter une gestion avancée des erreurs.

## Ordre d'implémentation recommandé

```text
1. Cadrage
2. Préparation environnement
3. PostgreSQL analytique
4. Airflow
5. Extraction raw
6. dbt staging
7. dbt marts
8. Emails
9. Power BI
10. Qualité
11. Sécurité
12. Monitoring
13. Documentation
14. Démonstration
```

## Répartition possible des responsabilités

### Data Engineer

- Airflow
- ETL/ELT
- PostgreSQL analytique
- dbt
- qualité des données
- monitoring

### Data Analyst / BI

- définition KPI
- modèle Power BI
- mesures DAX
- dashboards
- analyse métier

### Backend Developer

- stabilité des tables sources
- corrections modèle opérationnel
- APIs si nécessaire
- variables d'environnement

### DevOps

- Docker
- secrets
- sauvegardes
- déploiement
- monitoring technique

### Métier

- validation KPI
- validation dashboards
- règles d'alertes
- destinataires emails
