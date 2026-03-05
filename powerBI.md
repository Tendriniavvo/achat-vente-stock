
# Power BI — Étapes pour recréer le Dashboard (équivalent à `frontend-vue/src/views/Dashboard.vue`)

Ce document décrit, **pas à pas**, comment reconstruire dans **Power BI Desktop** le dashboard “Achats / Stock / Budget / Marges” que tu as dans `Dashboard.vue`.

## 0) Pré-requis

- **Power BI Desktop** installé.
- Une base **PostgreSQL** accessible depuis ton PC.
- Les scripts SQL exécutés (au minimum) :
  - `backend-spring/src/main/resources/database/base.sql`
  - `backend-spring/src/main/resources/database/dept-data.sql`
  - `backend-spring/src/main/resources/database/data/data.sql`
  - (optionnel) `backend-spring/src/main/resources/database/kpi.sql` pour des données d’exemple “Achats”.

Remarque : si certaines métriques du dashboard Vue sont calculées côté backend (ex: délai d’approbation, rotation stock), Power BI ne pourra pas les reproduire *à l’identique* tant que les dates/événements nécessaires ne sont pas stockés en base.

## 1) Connexion Power BI à PostgreSQL

1. Ouvrir **Power BI Desktop**.
2. `Accueil` -> `Obtenir des données` -> **PostgreSQL database**.
3. Renseigner :
   - **Server** : `localhost` (ou ton hôte)
   - **Database** : nom de ta base
4. Mode :
   - **Import** (recommandé)
   - ou **DirectQuery** (live)
5. Authentification -> `Se connecter`.

## 2) Charger les tables nécessaires

Dans le navigateur, cocher et charger au minimum :

### A. Achats

- `demandes_achat`
- `lignes_demandes_achat`
- `bons_commande_fournisseur`
- `fournisseurs`
- `receptions`
- `factures_fournisseur`

### B. Stock

- `stocks`
- `articles`
- `depots`
- `emplacements` (optionnel)
- `lots` (optionnel)

### C. Budget / Organisation

- `budgets`
- `departements`

### D. Utilisateurs

- `utilisateurs`

### E. Ventes (si tu veux la courbe “Ventes”)

- `commandes_clients`, `livraisons`, `factures_clients` (selon ce qui est réellement alimenté)

Puis `Charger`.

## 3) Modèle de données (Relations)

Aller dans la vue `Modèle` et créer/valider les relations suivantes :

### A. Demandes d’achat

- `demandes_achat[id]` (1) -> `lignes_demandes_achat[demande_achat_id]` (*)
- `utilisateurs[id]` (1) -> `demandes_achat[demandeur_id]` (*)

### B. Commandes fournisseurs

- `fournisseurs[id]` (1) -> `bons_commande_fournisseur[fournisseur_id]` (*)
- (optionnel si tu relies le flux complet)
  - `demandes_achat[id]` (1) -> `bons_commande_fournisseur[demande_achat_id]` (*)
  - `bons_commande_fournisseur[id]` (1) -> `receptions[bon_commande_id]` (*)
  - `bons_commande_fournisseur[id]` (1) -> `factures_fournisseur[bon_commande_id]` (*)

### C. Stock

- `articles[id]` (1) -> `stocks[article_id]` (*)
- `depots[id]` (1) -> `stocks[depot_id]` (*)

### D. Budget

- `departements[id]` (1) -> `budgets[departement_id]` (*)

## 4) Créer une table calendrier (recommandé)

`Modélisation` -> `Nouvelle table` :

```DAX
DimDate =
ADDCOLUMNS(
    CALENDAR(DATE(2024,1,1), DATE(2028,12,31)),
    "Year", YEAR([Date]),
    "MonthNum", MONTH([Date]),
    "Month", FORMAT([Date], "YYYY-MM")
)
```

Ensuite, si tes colonnes sont des timestamps, crée une colonne “date” (sans l’heure) via Power Query ou DAX, puis relie à `DimDate[Date]`.

## 5) Mesures DAX à créer (KPI + totaux)

`Modélisation` -> `Nouvelle mesure`.

### A. KPI cards (haut de page)

```DAX
KPI - Total Demandes Achat =
COUNTROWS(demandes_achat)
```

```DAX
KPI - Budget Disponible =
SUM(budgets[montant_disponible])
```

```DAX
KPI - Valeur Stock =
SUM(stocks[valeur])
```

> Hypothèse : `stocks[quantite]` et `articles[stock_min]` existent.

```DAX
KPI - Alertes Stock =
VAR Alerts =
    FILTER(
        stocks,
        stocks[quantite] < RELATED(articles[stock_min])
    )
RETURN
COUNTROWS(Alerts)
```

### B. Montant total d’une demande (table “Aperçu des Demandes”)

```DAX
DA - Montant Total =
SUMX(
    RELATEDTABLE(lignes_demandes_achat),
    lignes_demandes_achat[quantite] * lignes_demandes_achat[prix_estime]
)
```

### C. Top fournisseurs (volume achat)

```DAX
Achats - Montant BC =
SUM(bons_commande_fournisseur[montant_total])
```

### D. Marges par article (si `prix_achat` et `prix_vente` sont présents)

```DAX
Article - Marge Brute % =
VAR PV = MAX(articles[prix_vente])
VAR PA = MAX(articles[prix_achat])
RETURN
IF(PV = 0, BLANK(), DIVIDE(PV - PA, PV))
```

## 6) Construction des visuels (page `Dashboard`)

Crée une page `Dashboard` et reproduis la structure de `Dashboard.vue`.

### A. Rangée KPI (4 cartes)

1. **Carte** : `KPI - Total Demandes Achat`
2. **Carte** : Ventes (si tu as la table, sinon supprimer)
3. **Carte** : `KPI - Alertes Stock`
4. **Carte** : `KPI - Budget Disponible`

### B. Performance globale (Achats vs Ventes)

Visuel : **Graphique en aires**.

- Axe X : `DimDate[Month]`
- Valeurs :
  - `Achats - Montant BC`
  - (si dispo) mesure ventes (ex: `SUM(factures_clients[montant_total])`)

### C. Alertes de stock (liste)

Visuel : **Table**.

- `articles[nom]`
- `depots[nom]`
- `stocks[quantite]`
- `articles[stock_min]`

Filtre visuel : `stocks[quantite] < articles[stock_min]`.
Option : `Top N = 5`.

### D. Donut : demandes par statut

Visuel : **Donut**.

- Légende : `demandes_achat[statut]`
- Valeur : `COUNTROWS(demandes_achat)`

### E. Bar : Top fournisseurs

Visuel : **Bar chart**.

- Axe : `fournisseurs[nom]`
- Valeur : `Achats - Montant BC`
- Filtre : `Top N = 5` par `Achats - Montant BC`

### F. Budget

#### 1) Pie (répartition)

Visuel : **Pie**.

- Légende : `departements[nom]`
- Valeur : `SUM(budgets[montant_initial])`

#### 2) Détail (table/matrice)

Mesures :

```DAX
Budget - Initial = SUM(budgets[montant_initial])
Budget - Consomme = SUM(budgets[montant_consomme])
Budget - % Utilise = DIVIDE([Budget - Consomme], [Budget - Initial])
```

Visuel : **Matrice**.

- Lignes : `departements[nom]`
- Valeurs : `Budget - Consomme`, `Budget - Initial`, `Budget - % Utilise`

Appliquer mise en forme conditionnelle sur `%`.

### G. Marges

Visuel : **Combo chart** (ou bar multi-séries).

- Axe : `articles[nom]`
- Valeur : `Article - Marge Brute %`
- Valeur : `MAX(articles[prix_vente])`

### H. Méthodes de valorisation

Visuel : **Pie**.

- Légende : `articles[methode_valorisation]`
- Valeur : `COUNTROWS(articles)`

### I. Table : Aperçu des demandes d’achat (Top 5)

Visuel : **Table**.

- `demandes_achat[reference]`
- `utilisateurs[prenom]`, `utilisateurs[nom]`
- `DA - Montant Total`
- `demandes_achat[statut]`
- `demandes_achat[date_creation]`

Tri : `date_creation` décroissant, filtre `Top N = 5`.

## 7) Formats (MGA, %)

- Mettre les mesures de montants en **Devise MGA**.
- Mettre `Budget - % Utilise` et `Article - Marge Brute %` au format **Pourcentage**.

## 8) Rafraîchissement

- Import : `Actualiser`.
- Publication (Power BI Service) : publier puis configurer le **gateway** si la DB est locale.

## 9) Points à valider (écarts possibles)

- **Délai moyen d’approbation** : nécessite des champs dates (soumission / validation). Si tu n’as que `date_creation`, tu ne peux pas calculer un “délai réel”.
- **Rotation stock** : nécessite une définition (sorties sur période / stock moyen) et des mouvements (ex: `mouvements_stock` + lignes).
- **Ventes** : si non alimenté, la série “Ventes” sera vide.

