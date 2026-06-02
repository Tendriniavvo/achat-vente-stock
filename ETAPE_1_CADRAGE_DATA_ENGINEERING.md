# Étape 1 - Cadrage et analyse de l'existant

Statut : terminé pour une V1 de cadrage.

Ce document correspond à l'étape 1 de la roadmap Data Engineering. Il synthétise les données existantes, les modules métier, les KPI prioritaires, les dashboards attendus, les alertes automatiques et les points à compléter avant l'implémentation technique.

## 1. Objectif de l'étape

L'objectif de cette étape est de comprendre le projet existant `achat-vente-stock` et de définir une base claire pour construire la couche Data Engineering.

Le projet actuel contient déjà :

- une application backend Spring Boot ;
- une application frontend Vue.js ;
- une base PostgreSQL opérationnelle ;
- des scripts SQL de création et d'alimentation ;
- une documentation Power BI ;
- des modules métier achats, ventes, stock, budget, inventaire et audit.

## 2. Architecture actuelle observée

```text
frontend-vue
  -> interface utilisateur

backend-spring
  -> API métier
  -> logique applicative
  -> connexion PostgreSQL

PostgreSQL
  -> données opérationnelles achat / vente / stock / budget

Documentation existante
  -> powerBI.md
  -> achatDashbord.md
  -> documentation.md
  -> pages.md
```

## 3. Architecture Data Engineering cible

```text
Application Spring Boot / Vue.js
  -> PostgreSQL opérationnel
  -> Apache Airflow
  -> schema raw
  -> schema staging
  -> schema marts
  -> dbt
  -> Power BI
  -> emails automatiques
```

Décision recommandée pour la V1 :

- garder la base PostgreSQL actuelle comme source opérationnelle ;
- ajouter des schemas analytiques dans PostgreSQL :
  - `raw`
  - `staging`
  - `marts`
  - `audit`

Cette approche est plus simple pour démarrer qu'une deuxième base séparée.

## 4. Modules métier identifiés

### Référentiels

Tables principales :

- `articles`
- `categories_articles`
- `unites`
- `taxes`
- `fournisseurs`
- `clients`
- `departements`
- `utilisateurs`
- `roles`
- `permissions`

Usage data :

- dimensions analytiques ;
- filtres Power BI ;
- rattachement des transactions aux articles, clients, fournisseurs et départements.

### Achats

Tables principales :

- `demandes_achat`
- `lignes_demandes_achat`
- `bons_commande_fournisseur`
- `lignes_bons_commande`
- `receptions`
- `lignes_receptions`
- `factures_fournisseur`
- `lignes_factures_fournisseur`

Usage data :

- analyse des demandes d'achat ;
- suivi des validations ;
- montant des commandes fournisseurs ;
- top fournisseurs ;
- rapprochement commande / réception / facture ;
- suivi des factures fournisseurs.

### Ventes

Tables principales :

- `devis_clients`
- `lignes_devis`
- `commandes_clients`
- `lignes_commandes_clients`
- `livraisons`
- `lignes_livraisons`
- `factures_clients`
- `lignes_factures_clients`
- `encaissements`

Usage data :

- chiffre d'affaires ;
- commandes clients ;
- livraisons ;
- factures clients ;
- encaissements ;
- marge par article et par client.

### Stock et inventaire

Tables principales :

- `stocks`
- `lots`
- `mouvements_stock`
- `inventaires`
- `lignes_inventaires`
- `ajustements_stock`
- `valorisations_stock`
- `depots`
- `emplacements`
- `types_emplacement`

Usage data :

- valeur totale du stock ;
- stock par dépôt ;
- stock par article ;
- stock sous seuil ;
- mouvements entrants et sortants ;
- écarts d'inventaire ;
- valorisation périodique.

### Budget

Tables principales :

- `budgets`
- `departements`

Usage data :

- budget initial ;
- budget consommé ;
- budget disponible ;
- pourcentage d'utilisation ;
- départements en alerte.

### Audit et sécurité

Tables principales :

- `journal_audit`
- `utilisateurs`
- `roles`
- `utilisateurs_roles`
- `permissions`

Usage data :

- suivi des actions sensibles ;
- analyse des activités utilisateurs ;
- traçabilité ;
- monitoring de conformité.

## 5. Tables sources prioritaires pour la V1

Pour une première version démontrable, il faut prioriser les tables suivantes :

```text
articles
stocks
depots
budgets
departements
demandes_achat
lignes_demandes_achat
bons_commande_fournisseur
fournisseurs
mouvements_stock
```

Ces tables permettent de construire rapidement :

- un dashboard stock ;
- un dashboard budget ;
- un dashboard achats simple ;
- des alertes stock ;
- des alertes budget ;
- un premier email automatique.

## 6. Tables sources pour la V2

Pour étendre le projet après la V1 :

```text
commandes_clients
lignes_commandes_clients
factures_clients
lignes_factures_clients
encaissements
factures_fournisseur
lignes_factures_fournisseur
receptions
lignes_receptions
inventaires
lignes_inventaires
ajustements_stock
valorisations_stock
journal_audit
```

Ces tables permettront d'ajouter :

- dashboard ventes ;
- dashboard finance ;
- suivi des paiements ;
- analyse des factures ;
- analyse d'inventaire ;
- monitoring audit.

## 7. KPI prioritaires

### KPI Stock

- Valeur totale du stock.
- Nombre d'articles sous seuil minimum.
- Quantité disponible par article.
- Quantité par dépôt.
- Valeur stock par dépôt.
- Entrées de stock.
- Sorties de stock.
- Rotation du stock.
- Écarts d'inventaire.

### KPI Budget

- Budget initial.
- Budget consommé.
- Budget disponible.
- Pourcentage d'utilisation du budget.
- Départements avec budget consommé supérieur à 90%.
- Départements en dépassement.

### KPI Achats

- Nombre total de demandes d'achat.
- Demandes en attente.
- Demandes validées.
- Demandes rejetées.
- Montant total des commandes fournisseurs.
- Top fournisseurs par montant.
- Nombre de commandes fournisseurs.
- Réceptions partielles ou en litige.

### KPI Ventes

- Chiffre d'affaires.
- Nombre de commandes clients.
- Nombre de factures clients.
- Top clients.
- Top articles vendus.
- Marge brute.
- Factures clients impayées.

### KPI Finance

- Factures fournisseurs en attente.
- Factures fournisseurs bloquées.
- Factures fournisseurs payées.
- Factures clients en attente.
- Factures clients payées.
- Encaissements par période.

## 8. Dashboards Power BI attendus

### Dashboard global

Objectif : donner une vue synthétique de l'activité.

Contenu :

- KPI stock ;
- KPI achats ;
- KPI ventes ;
- KPI budget ;
- alertes principales ;
- tendances achats / ventes ;
- top fournisseurs ;
- top articles.

### Dashboard achats

Objectif : suivre le cycle d'approvisionnement.

Contenu :

- demandes d'achat ;
- demandes en attente ;
- statut des demandes ;
- top fournisseurs ;
- montant des commandes ;
- tendance des achats ;
- besoins de réapprovisionnement.

### Dashboard stock

Objectif : suivre les niveaux de stock et les risques de rupture.

Contenu :

- valeur totale du stock ;
- articles sous seuil ;
- stock par dépôt ;
- stock par article ;
- mouvements entrants / sortants ;
- écarts d'inventaire.

### Dashboard budget

Objectif : suivre la consommation budgétaire.

Contenu :

- budget initial ;
- budget consommé ;
- budget disponible ;
- pourcentage d'utilisation ;
- départements en alerte ;
- comparaison budget / achats.

### Dashboard ventes

Objectif : suivre l'activité commerciale.

Contenu :

- chiffre d'affaires ;
- commandes clients ;
- factures clients ;
- top clients ;
- top articles vendus ;
- marge brute.

### Dashboard alertes

Objectif : centraliser les anomalies et priorités.

Contenu :

- stock critique ;
- budget à risque ;
- factures impayées ;
- factures en retard ;
- commandes ou réceptions en litige.

## 9. Alertes email attendues

### Alerte stock critique

Déclenchement :

```text
stocks.quantite < articles.stock_min
```

Destinataires recommandés :

- responsable stock ;
- magasinier ;
- responsable achat.

### Alerte budget

Déclenchement :

```text
budgets.montant_consomme / budgets.montant_initial >= 0.90
```

Destinataires recommandés :

- direction ;
- finance ;
- responsable du département concerné.

### Alerte facture client impayée

Déclenchement V2 :

```text
factures_clients.statut <> 'payee'
```

Destinataires recommandés :

- finance ;
- commercial.

### Alerte facture fournisseur bloquée

Déclenchement V2 :

```text
factures_fournisseur.statut = 'bloquee'
```

Destinataires recommandés :

- finance ;
- achat.

### Rapport quotidien

Contenu :

- nombre d'alertes stock ;
- nombre d'alertes budget ;
- montant achats du jour ;
- chiffre d'affaires du jour ;
- factures en attente.

Destinataires recommandés :

- direction ;
- responsables métier.

## 10. Modèle analytique recommandé

### Dimensions

```text
dim_date
dim_article
dim_client
dim_fournisseur
dim_depot
dim_departement
dim_utilisateur
```

### Tables de faits

```text
fact_achats
fact_ventes
fact_stock
fact_mouvements_stock
fact_budget
fact_factures_clients
fact_factures_fournisseur
```

### Marts

```text
mart_dashboard_global
mart_dashboard_stock
mart_dashboard_budget
mart_dashboard_achats
mart_dashboard_ventes
mart_alertes
```

## 11. Données manquantes ou à confirmer

### Achats

- Date exacte de validation d'une demande d'achat.
- Utilisateur validateur d'une demande d'achat.
- Historique structuré des validations.
- Date d'envoi d'un bon de commande fournisseur.

Impact :

- le délai moyen d'approbation ne sera pas fiable tant que ces champs ne sont pas clairement disponibles.

### Ventes

- Date de paiement réelle d'une facture client.
- Statuts détaillés de paiement.
- Gestion des avoirs.
- Retours clients.

Impact :

- les KPI d'impayés et de retard devront être simplifiés en V1.

### Stock

- Définition métier exacte de la rotation du stock.
- Gestion de la quantité réservée.
- Historisation quotidienne du stock.

Impact :

- la rotation stock et les snapshots stock nécessiteront une V2 plus avancée.

### Budget

- Règle de consommation budgétaire :
  - consommation basée sur les demandes ?
  - sur les bons de commande ?
  - sur les factures validées ?

Impact :

- le calcul de consommation réelle doit être validé avec le métier.

## 12. Décisions pour la V1

Pour livrer vite et proprement, la V1 doit se concentrer sur :

```text
Stock + Budget + Achats simples + Email automatique + Power BI
```

Périmètre V1 recommandé :

- création des schemas `raw`, `staging`, `marts`, `audit` ;
- extraction des tables stock, budget et achats ;
- création des premiers modèles dbt ;
- création de `mart_dashboard_stock` ;
- création de `mart_dashboard_budget` ;
- création de `mart_dashboard_achats` simple ;
- création de `mart_alertes` ;
- email automatique stock/budget ;
- Power BI connecté aux marts.

## 13. Décisions pour la V2

La V2 doit ajouter :

- ventes ;
- factures clients ;
- factures fournisseurs ;
- encaissements ;
- inventaires ;
- snapshots de stock ;
- chargement incrémental ;
- tests dbt avancés ;
- monitoring complet.

## 14. Critères de réussite de l'étape 1

- [x] Modules métier identifiés.
- [x] Tables sources principales identifiées.
- [x] Tables prioritaires V1 identifiées.
- [x] KPI prioritaires définis.
- [x] Dashboards Power BI attendus définis.
- [x] Alertes email attendues définies.
- [x] Données manquantes identifiées.
- [x] Architecture analytique cible confirmée.
- [x] Périmètre V1 proposé.

## 15. Prochaine étape

Prochaine étape recommandée :

```text
Étape 2 - Préparation de l'environnement Data
```

Travaux à réaliser :

- créer le dossier `data-engineering/` ;
- préparer les sous-dossiers Airflow, dbt, SQL, scripts et docs ;
- préparer `.env.example` ;
- préparer les scripts de création des schemas PostgreSQL ;
- préparer Docker Compose.

