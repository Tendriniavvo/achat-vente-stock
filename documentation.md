# Documentation — Système de Gestion Achat / Vente / Stock

## 1) Présentation du projet

Ce projet est une application de gestion intégrée destinée à couvrir le cycle **Achats → Réception → Facturation fournisseur**, le cycle **Ventes → Livraison → Facturation client**, ainsi que la **gestion de stock**, l’**inventaire** et le **reporting**.

L’objectif est de fournir :

- Une application web structurée par modules/pages et sécurisée par rôles.
- Un suivi opérationnel (achats, ventes, stock) et financier (budgets, valorisation, marges).
- Des tableaux de bord (web + Power BI) et une assistance à l’analyse via **IA**.

## 2) Objectifs

### 2.1 Objectifs fonctionnels

- Centraliser la gestion des **articles**, **fournisseurs**, **clients**, **dépôts** et **emplacements**.
- Digitaliser les workflows :
  - demandes d’achat (DA) et validation,
  - bons de commande fournisseur,
  - réception fournisseur,
  - facture fournisseur (rapprochement),
  - devis client → commande client,
  - livraison / sortie stock,
  - facturation client.
- Garantir la traçabilité : mouvements de stock, historique et audit.
- Supporter les opérations d’**inventaire** et la validation des ajustements.
- Fournir des **KPI** et du **reporting** par rôle.

### 2.2 Objectifs de pilotage / décisionnel

- Mesurer et suivre :
  - volume et statut des demandes d’achat,
  - alertes de stock et besoins de réapprovisionnement,
  - valeur totale du stock,
  - consommation budgétaire par département,
  - tendances (achats / ventes si alimentées).
- Faciliter l’analyse stratégique via un module **IA** (questions d’interprétation budgétaire, recommandations, risques, etc.).

## 3) Périmètre — pages et modules

Le projet couvre les pages suivantes (voir `pages.md` et `listes_taches_par_pages.md`) :

### 3.1 Accès & sécurité

- **Connexion / Accueil**
  - Authentification
  - Redirection selon rôle
  - Alertes importantes
  - Déconnexion

### 3.2 Tableaux de bord / reporting

- **Dashboard (Tableau de bord)**
  - KPI personnalisés selon rôle
  - Vue globale achats / ventes / stock
  - Détection d’anomalies
  - Accès rapide aux actions

- **Reporting & KPI**
  - Indicateurs par rôle
  - Comparaisons par période / site / dépôt

### 3.3 Référentiels

- **Gestion des Articles**
- **Gestion des Fournisseurs**
- **Gestion des Clients**
- **Dépôts & Emplacements**

### 3.4 Processus Achats

- **Demande d’Achat (DA)**
- **Validation des Demandes d’Achat**
- **Bon de Commande Fournisseur**
- **Réception Fournisseur**
- **Facture Fournisseur** (rapprochement commande/réception/facture)

### 3.5 Processus Ventes

- **Devis Client**
- **Commande Client** (réservation de stock)
- **Livraison / Sortie de stock**
- **Facturation Client** (facture / avoir / encaissement)

### 3.6 Stock & inventaire

- **Mouvements de stock** (historique, filtres, export)
- **Inventaire**
- **Validation des ajustements de stock**
- **Valorisation du stock**

### 3.7 Administration & conformité

- **Gestion des utilisateurs**
- **Rôles & habilitations**
- **Journal & audit**

## 4) Technologies utilisées

> Certains éléments (ports, commandes exactes, structure complète) dépendent du projet tel qu’il est configuré dans ton environnement. Les points ci-dessous reflètent les fichiers de documentation présents dans le dépôt.

### 4.1 Frontend

- **Vue.js**
- Vues identifiées dans la documentation :
  - `frontend-vue/src/views/Dashboard.vue`
  - `frontend-vue/src/views/achats/AchatDashboard.vue`
- Dépendances mentionnées :
  - `marked` (rendu Markdown → HTML pour l’affichage de réponses IA)

### 4.2 Backend

- **Spring Boot** (backend Java)
- Endpoints cités dans la documentation IA :
  - `POST /api/budget/analyze-chart`
- Configuration attendue (IA Mistral) :
  - `mistral.api.key` (via variable d’environnement `MISTRAL_API_KEY` ou propriété)
  - `mistral.api.url` (ex: `https://api.mistral.ai/v1/chat/completions`)

### 4.3 Base de données

- **PostgreSQL**
- Scripts SQL utilisés dans la documentation Power BI :
  - `backend-spring/src/main/resources/database/base.sql`
  - `backend-spring/src/main/resources/database/dept-data.sql`
  - `backend-spring/src/main/resources/database/data/data.sql`
  - (optionnel) `backend-spring/src/main/resources/database/kpi.sql`

### 4.4 BI / DataViz

- **Power BI Desktop**
- Connexion à PostgreSQL en mode `Import` (recommandé) ou `DirectQuery`.

### 4.5 Intégration IA

Deux familles d’intégration sont mentionnées dans le dépôt :

- **Mistral AI** (chat completions) :
  - utilisée pour analyser un graphique (cas budget) et fournir une interprétation + recommandations.
  - nécessite une clé API valide, la gestion CORS, et un service HTTP backend (RestTemplate/WebClient).

- **Google Generative AI / GenAI** (dépendances Node) :
  - paquets mentionnés : `@google/generative-ai` et `@google/genai`.

## 5) Dashboards — Web et Power BI

### 5.1 Dashboard Power BI (global)

Le fichier `powerBI.md` décrit comment recréer dans Power BI le dashboard équivalent à :

- `frontend-vue/src/views/Dashboard.vue`

Incluant notamment :

- KPI (demandes d’achat, budget disponible, valeur stock, alertes stock)
- Courbes (Achats vs Ventes si disponibles)
- Donut (demandes par statut)
- Top fournisseurs
- Budget (répartition + matrice avec % utilisé)
- Marges par article (si `prix_achat` et `prix_vente` existent)

### 5.2 Dashboard Power BI Achats

Le fichier `achatDashbord.md` décrit comment recréer dans Power BI un dashboard Achats équivalent à :

- Route web : `/achats/dashboard`
- Vue : `frontend-vue/src/views/achats/AchatDashboard.vue`

Contenu :

- 4 KPI cards (demandes, budget disponible, délai moyen d’approbation si possible, alertes stock)
- Statut des demandes (donut)
- Top fournisseurs (bar)
- Utilisation budget par département
- Tendance des achats (area)
- Besoins de réapprovisionnement (table)

## 6) Module IA — objectif, usage et points de configuration

### 6.1 Objectif

Le module IA sert à **interpréter** les données (ex: budget, répartition, risques) et fournir :

- une synthèse,
- des hypothèses,
- des recommandations,
- des alertes / points d’attention.

Exemples de questions (voir `README.md`) :

- Quels départements concentrent la majorité du budget et pourquoi ?
- La répartition budgétaire est-elle équilibrée ?
- Quels départements semblent sous-financés ?
- Quel impact aurait une réallocation partielle du budget ?

### 6.2 Endpoint d’analyse

- Backend : `POST /api/budget/analyze-chart`
- Le frontend déclenche l’analyse via un bouton du type « Analyser avec IA ».
- La réponse IA peut être rendue en Markdown côté UI (via `marked`).

### 6.3 Points de fiabilisation (résumé)

Les points suivants sont à vérifier en cas d’erreur « Impossible de contacter le service d’analyse IA… » (voir `correction.md`) :

- Backend Spring Boot lancé et endpoint testable (Postman/curl).
- CORS correctement configuré selon l’URL du frontend.
- Clé API IA (Mistral) correctement injectée.
- Gestion d’erreurs HTTP (401/429/500) et logs utiles.

## 7) Données & KPI attendus (exemples)

Les KPI décrits dans `README.md` incluent :

- **Gestion du stock**
  - valeur totale du stock
  - répartition par dépôt
  - rotation des stocks (selon disponibilité des mouvements)

- **Gestion budgétaire**
  - consommation budgétaire par département
  - alertes de dépassement budgétaire

## 8) Références internes (fichiers du dépôt)

- `pages.md` : liste détaillée des pages et fonctionnalités.
- `listes_taches_par_pages.md` : cahier des charges (tâches par page).
- `powerBI.md` : guide Power BI pour dashboard global.
- `achatDashbord.md` : guide Power BI pour dashboard Achats.
- `correction.md` : pistes de correction pour l’intégration IA (backend + frontend).
- `README.md` : notes IA / questions d’analyse et dépendances mentionnées.
