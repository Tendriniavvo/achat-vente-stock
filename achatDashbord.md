# Power BI — Achats Dashboard (`/achats/dashboard`)

Ce guide explique comment recréer dans **Power BI Desktop** le tableau de bord Achats équivalent à :

- Page web : `http://localhost:5173/achats/dashboard`
- Front : `frontend-vue/src/views/achats/AchatDashboard.vue`

Le dashboard Achats contient :

- Une rangée de **4 KPI cards**
  - Demandes d'Achat (et “en attente d'approbation”)
  - Budget Disponible
  - Efficacité Approbation (délai moyen)
  - Alertes Stock
- 3 blocs d’analyse
  - Donut : Statut des Demandes
  - Bar horizontal : Top Fournisseurs (volume d’achat)
  - Liste + barres : Utilisation Budget par département
- 2 blocs en bas
  - Area : Tendance des Achats (série achats uniquement)
  - Table : Besoins de Réapprovisionnement (alertes stock détaillées)

---

## 0) Pré-requis

- Power BI Desktop installé.
- Base PostgreSQL accessible.
- Scripts SQL exécutés au minimum :
  - `backend-spring/src/main/resources/database/base.sql`
  - `backend-spring/src/main/resources/database/dept-data.sql`
  - `backend-spring/src/main/resources/database/data/data.sql`
  - (optionnel) `backend-spring/src/main/resources/database/kpi.sql` (démo achats)

---

## 1) Connexion à PostgreSQL

1. Ouvrir Power BI Desktop.
2. `Accueil` -> `Obtenir des données` -> `PostgreSQL database`.
3. Remplir :
   - Server : `localhost` (ou l’hôte)
   - Database : nom de la base
4. Choisir :
   - `Import` (recommandé)
   - ou `DirectQuery`
5. Se connecter.

---

## 2) Tables à charger (Achats)

Charger au minimum :

- `demandes_achat`
- `lignes_demandes_achat`
- `utilisateurs`
- `budgets`
- `departements`
- `bons_commande_fournisseur`
- `fournisseurs`
- `stocks`
- `articles`
- `depots`

Ensuite cliquer `Charger`.

---

## 3) Modèle (relations)

Dans la vue `Modèle`, créer/valider :

- `demandes_achat[id]` (1) -> `lignes_demandes_achat[demande_achat_id]` (*)
- `utilisateurs[id]` (1) -> `demandes_achat[demandeur_id]` (*)
- `departements[id]` (1) -> `budgets[departement_id]` (*)
- `fournisseurs[id]` (1) -> `bons_commande_fournisseur[fournisseur_id]` (*)
- `articles[id]` (1) -> `stocks[article_id]` (*)
- `depots[id]` (1) -> `stocks[depot_id]` (*)

---

## 4) Table calendrier (pour la tendance Achats)

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

Ensuite :

- Si `bons_commande_fournisseur[date_commande]` est un timestamp, créer une colonne date (sans heure) et relier à `DimDate[Date]`.

### 4.1) Détail (important) : corriger l’erreur de relation `DimDate[Date]` -> `bons_commande_fournisseur[date_commande]`

Si tu ajoutes directement `DimDate[Date]` en relation avec `bons_commande_fournisseur[date_commande]`, Power BI peut afficher une erreur, typiquement parce que :

- `DimDate[Date]` est de type **Date**
- `bons_commande_fournisseur[date_commande]` est de type **Date/Heure** (timestamp)

La solution recommandée est de créer une colonne **Date (sans heure)** dans `bons_commande_fournisseur`, puis de faire la relation sur cette nouvelle colonne.

#### Étape A — Vérifier le type des colonnes

1. Ouvrir `Transformer les données` (Power Query).
2. Cliquer la table `bons_commande_fournisseur`.
3. Cliquer la colonne `date_commande`.
4. Vérifier le type (icône à gauche du nom de colonne) :
   - si c’est **Date/Heure** -> OK, on va la convertir
   - si c’est **Texte** -> il faut d’abord convertir en Date/Heure.

#### Étape B.0 — Création d’une colonne (si nécessaire)

À faire quand :

- tu as un **timestamp** (Date/Heure) mais tu dois relier à une colonne **Date**
- Power BI refuse la relation (types incompatibles)

Objectif : créer une nouvelle colonne **Date sans heure** nommée `date_commande_date`.

##### Option 1 (simple) — “Date uniquement”

1. Dans Power Query, sélectionner la colonne `date_commande`.
2. Onglet `Ajouter une colonne`.
3. `Colonne de date` -> `Date uniquement`.
4. Renommer la colonne créée en `date_commande_date`.
5. Vérifier que le type de `date_commande_date` est **Date**.

##### Option 2 (si le menu n’existe pas) — Colonne personnalisée (M)

1. Dans Power Query, onglet `Ajouter une colonne` -> `Colonne personnalisée`.
2. Nom de colonne : `date_commande_date`.
3. Formule (M) :

```powerquery
= Date.From([date_commande])
```

4. Valider.
5. Mettre le type de `date_commande_date` sur **Date** si Power Query ne l’a pas fait automatiquement.

##### Option 3 (alternative) — Nouvelle colonne en DAX (dans le modèle)

`Modélisation` -> `Nouvelle colonne` (table `bons_commande_fournisseur`) :

```DAX
date_commande_date =
DATE(
    YEAR(bons_commande_fournisseur[date_commande]),
    MONTH(bons_commande_fournisseur[date_commande]),
    DAY(bons_commande_fournisseur[date_commande])
)
```

Puis, dans la vue `Données`, vérifier que `date_commande_date` est bien de type **Date**.

#### Étape B (recommandée) — Créer une colonne “date” via Power Query

1. Dans Power Query (toujours table `bons_commande_fournisseur`).
2. Sélectionner la colonne `date_commande`.
3. `Ajouter une colonne` -> `Colonne de date` -> `Date uniquement`.
4. Power Query crée une colonne (souvent nommée `Date` ou similaire). Renomme-la en :
   - `date_commande_date`
5. Vérifier que le type de `date_commande_date` est bien **Date**.
6. Cliquer `Fermer et appliquer`.

> Avantage : c’est la méthode la plus stable, car la conversion est faite avant le modèle.

#### Étape C (alternative) — Créer une colonne “date” en DAX (si tu ne veux pas Power Query)

Dans Power BI : `Modélisation` -> `Nouvelle colonne` (dans `bons_commande_fournisseur`) :

```DAX
date_commande_date =
DATE(YEAR(bons_commande_fournisseur[date_commande]), MONTH(bons_commande_fournisseur[date_commande]), DAY(bons_commande_fournisseur[date_commande]))
```

Puis vérifier dans `Données` que `date_commande_date` est bien de type **Date**.

#### Étape D — Créer la relation dans le modèle

1. Aller dans la vue `Modèle`.
2. Créer la relation :
   - `DimDate[Date]` (1) -> `bons_commande_fournisseur[date_commande_date]` (*)
3. Type de relation : **Many-to-one**.
4. Direction de filtrage : **Single** (recommandé).

#### Étape E — Cas particulier : noms de colonnes avec caractères spéciaux

Le message que tu as :

> "Pour utiliser des caractères spéciaux dans un nom de colonne, mettez le nom entier entre crochets ( [] ) et ajoutez un ] à tout crochet fermant dans le nom."

arrive quand ton nom contient des espaces, des accents, ou des caractères spéciaux.

Bonnes pratiques :

- Dans DAX, référence une colonne comme : `Table[Nom Colonne]`.
- Si le nom de colonne contient un `]`, il faut l’échapper en doublant `]`.

Exemples :

- Colonne normale : `bons_commande_fournisseur[date_commande]`
- Colonne avec espace : `bons_commande_fournisseur[Date commande]`

Recommandation : renomme les colonnes en `snake_case` dans Power Query (ex: `date_commande_date`) pour éviter ces erreurs.

> Si tu n’as pas de date fiable côté Achats, la courbe “Tendance” ne pourra pas être correcte.

---

## 5) Mesures DAX (équivalentes aux KPI du front)

### A) KPI — Demandes d’Achat

```DAX
KPI - Demandes d'Achat =
COUNTROWS(demandes_achat)
```

### B) KPI — Demandes en attente

Le front affiche `statistiques.demandesEnAttente`.
Sans code backend, on prend une règle simple : statut “soumise” ou “en attente”.

```DAX
KPI - Demandes en attente =
CALCULATE(
    COUNTROWS(demandes_achat),
    demandes_achat[statut] IN { "soumise", "en attente" }
)
```

> Si ton backend utilise des statuts différents (`attente_finance`, `attente_admin`, etc.), adapte la liste.

### C) KPI — Budget disponible

```DAX
KPI - Budget Disponible =
SUM(budgets[montant_disponible])
```

### D) KPI — Alertes stock (count)

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

### E) KPI — Efficacité approbation (délai moyen)

Le front affiche `statistiques.delaiMoyenApprobation`.
Dans le schéma SQL fourni, il n’y a pas de champ “date validation finale” explicite.

- Si tu as une colonne date de validation : on pourra calculer.
- Sinon : laisse cette card vide (ou mets une valeur manuelle) jusqu’à ajouter la donnée.

---

## 6) Visuels Power BI (mise en page identique)

Crée une page nommée : `Achats Dashboard`.

### A) Rangée KPI (4 cards)

1. Ajouter 4 visuels `Carte`.
2. Assigner :
   - Card 1 : `KPI - Demandes d'Achat`
   - Card 2 : `KPI - Budget Disponible`
   - Card 3 : (si dispo) délai moyen approbation
   - Card 4 : `KPI - Alertes Stock`

Pour imiter le front (bordure gauche couleur + ombre) :

- `Format` -> `Arrière-plan` : blanc
- `Format` -> `Effets` -> `Ombre` : ON (léger)
- `Format` -> `Effets` -> `Bordure` : ON

> Power BI ne gère pas toujours une “border-start” épaisse comme Bootstrap. Alternative :

- `Insertion` -> `Formes` -> `Rectangle`
- Mettre une bande verticale fine (ex: largeur 6-8px) à gauche de la card
- Couleur = couleur KPI
- Envoyer derrière la card

### B) Donut — Statut des demandes

Visuel : `Donut`.

- Légende : `demandes_achat[statut]`
- Valeur : `COUNTROWS(demandes_achat)`

### C) Bar — Top fournisseurs

Visuel : `Bar chart`.

- Axe : `fournisseurs[nom]`
- Valeur : `SUM(bons_commande_fournisseur[montant_total])`
- Filtre : `Top N = 5` par la somme du montant

### D) Liste — Utilisation Budget (par département)

Le front affiche des barres de progression par département.
Dans Power BI, le plus simple est : `Matrice` + mise en forme conditionnelle.

Mesures :

```DAX
Budget - Initial = SUM(budgets[montant_initial])
Budget - Consomme = SUM(budgets[montant_consomme])
Budget - % Utilise = DIVIDE([Budget - Consomme], [Budget - Initial])
```

Matrice :

- Lignes : `departements[nom]`
- Valeurs : `Budget - % Utilise`, `Budget - Consomme`, `Budget - Initial`

Appliquer :

- Mise en forme conditionnelle sur `%` (vert/orange/rouge).

### E) Area — Tendance des achats

Visuel : `Graphique en aires`.

- Axe X : `DimDate[Month]`
- Valeur : `SUM(bons_commande_fournisseur[montant_total])` (ou une mesure dédiée)

### F) Table — Besoins de réapprovisionnement

Visuel : `Table`.

Champs :

- `articles[nom]`
- `depots[nom]`
- `stocks[quantite]`
- `articles[stock_min]`

Filtre visuel : `stocks[quantite] < articles[stock_min]`.

---

## 7) Couleurs (comme l’interface) — étapes rapides

Dans `AchatDashboard.vue`, les couleurs dominantes sont :

- Primary : `#5D87FF`
- Info : `#49BEFF`
- Success : `#13DEB9`
- Warning : `#FFAE1F`
- Danger : `#FA896B`

### A) Mettre un thème global

1. `Affichage` -> `Thèmes`.
2. `Personnaliser le thème actuel`.
3. Dans `Couleurs des données` : renseigner la palette.
4. `Appliquer`.

### B) Mettre une card en couleur (ex: violet)

1. Sélectionner la card.
2. `Format` -> `Étiquette de données` -> `Couleur` -> choisir le violet (ex: `#7C3AED`).
3. `Format` -> `Effets` -> `Bordure` -> `Couleur` -> même violet.

---

## 8) Gestion interface : alignement, emplacement, verrouillage

### A) Grille + alignement

1. `Affichage` -> activer `Lignes de grille`.
2. `Affichage` -> activer `Aligner sur la grille`.
3. Sélectionner plusieurs visuels -> `Format` -> `Aligner` -> `Aligner en haut`.
4. `Format` -> `Distribuer horizontalement`.

### B) Garder l’emplacement (ne pas bouger)

1. Quand tout est bien placé : `Affichage` -> `Verrouiller les objets`.
2. Utiliser `Volet de sélection` pour masquer/afficher et verrouiller selon disponibilité.

---

## 9) Améliorations recommandées (optionnelles)

- Ajouter une page `Drill-through` “Détail fournisseur” (clic sur un fournisseur -> liste BC).
- Ajouter des slicers : période, fournisseur, département.
- Créer des vues SQL `kpi_achats_*` si tu veux un modèle plus simple (moins de DAX).
