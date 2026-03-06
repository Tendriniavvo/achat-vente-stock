
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

### 4.1) Détail (important) : corriger l’erreur de relation `DimDate[Date]` -> `bons_commande_fournisseur[date_commande]`

Si tu crées directement une relation entre :

- `DimDate[Date]` (type **Date**)
- `bons_commande_fournisseur[date_commande]` (souvent type **Date/Heure**)

Power BI peut refuser la relation ou donner des erreurs.

La solution recommandée est de créer une colonne **date sans heure** côté `bons_commande_fournisseur`, puis de relier `DimDate[Date]` à cette nouvelle colonne.

#### Étape A — Vérifier le type de `date_commande`

1. `Accueil` -> `Transformer les données` (Power Query).
2. Table : `bons_commande_fournisseur`.
3. Colonne : `date_commande`.
4. Vérifier le type :
   - Si **Date/Heure** : parfait.
   - Si **Texte** : changer le type en **Date/Heure** (sinon la conversion date-only sera instable).

#### Étape B (recommandée) — Créer `date_commande_date` via Power Query

1. Dans Power Query, sélectionner la colonne `date_commande`.
2. `Ajouter une colonne` -> `Colonne de date` -> `Date uniquement`.
3. Renommer la nouvelle colonne en `date_commande_date`.
4. Vérifier que `date_commande_date` est bien de type **Date**.
5. `Fermer et appliquer`.

#### Étape C (alternative) — Créer `date_commande_date` en DAX

`Modélisation` -> `Nouvelle colonne` (dans `bons_commande_fournisseur`) :

```DAX
date_commande_date =
DATE(
    YEAR(bons_commande_fournisseur[date_commande]),
    MONTH(bons_commande_fournisseur[date_commande]),
    DAY(bons_commande_fournisseur[date_commande])
)
```

Ensuite vérifier que le type de cette colonne est **Date**.

#### Étape D — Créer la relation

Dans la vue `Modèle` :

- `DimDate[Date]` (1) -> `bons_commande_fournisseur[date_commande_date]` (*)

Paramètres recommandés :

- Cardinalité : Many-to-one
- Direction : Single

#### Étape E — Erreur “caractères spéciaux” dans les noms de colonnes

Si Power BI affiche un message du type :

> Pour utiliser des caractères spéciaux dans un nom de colonne, mettez le nom entier entre crochets ( [] )...

Rappel :

- En DAX on écrit `Table[Nom Colonne]`.
- Si le nom contient un `]`, il faut l’échapper en doublant `]`.

Recommandation : renommer les colonnes dans Power Query en `snake_case` (ex: `date_commande_date`) pour éviter ce genre d’erreurs.

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

## 10) Design : changer les couleurs / ajouter des Cards KPI (comme le front)

Cette section t’aide à obtenir un rendu proche de `Dashboard.vue`.

### A. Changer les couleurs globales (Thème)

1. Dans Power BI Desktop : onglet `Affichage`.
2. Cliquer `Thèmes`.
3. Options :
   - Choisir un thème existant (rapide)
   - Ou `Personnaliser le thème actuel`.
4. Dans `Couleurs des données`, définir une palette proche de ton front (exemples) :
   - Primaire : `#5D87FF`
   - Success : `#13DEB9`
   - Warning : `#FFAE1F`
   - Danger : `#FA896B`
   - Info : `#49BEFF`
5. Valider `Appliquer`.

Astuce : si tu veux un thème réutilisable sur d’autres rapports : `Affichage` -> `Thèmes` -> `Enregistrer le thème actuel`.

### B. Changer la couleur d’un visuel (au cas par cas)

1. Sélectionner le visuel.
2. Panneau `Visualisations` -> onglet `Format` (icône rouleau de peinture).
3. Selon le visuel :
   - `Couleurs des données` : choisir la couleur des séries
   - `Titre` : couleur/typo
   - `Arrière-plan` : couleur + transparence
   - `Effets` / `Ombre` / `Bordure` : look “carte”.

### C. Créer une Card KPI “propre” (fond + bordure + titre)

Power BI propose plusieurs types de cartes. Pour imiter ton front, utilise :
- **Carte** (simple) ou **Carte (nouvelle)** si disponible.

Étapes :

1. Ajouter un visuel `Carte`.
2. Glisser la mesure KPI (ex: `KPI - Total Demandes Achat`) dans le champ du visuel.
3. Avec la carte sélectionnée -> `Format` :
   - `Titre` -> `Activé` -> mettre un titre (ex: `Demandes d'Achat`)
   - `Étiquette de données` / `Valeur` :
     - augmenter la taille (ex: 20-32)
     - mettre en gras
   - `Arrière-plan` -> `Activé` :
     - couleur : blanc (`#FFFFFF`)
     - transparence : 0%
   - `Bordure` (ou `Effets` -> `Bordure`) -> `Activé` :
     - couleur proche de la KPI (ex: primaire `#5D87FF`)
     - épaisseur (1-3)
   - `Ombre` -> `Activé` : léger (comme `shadow-sm`).

### D. Ajouter une “icône” sur une card (équivalent des icônes ti)

Les cartes natives n’ont pas toujours une icône à droite comme dans ton Vue.
Deux méthodes simples :

#### Méthode 1 (simple) : insérer une Image

1. `Insertion` -> `Image`.
2. Choisir une icône (png/svg converti en png).
3. Placer l’image à droite de la carte.
4. Sélectionner la carte + l’image -> `Format` -> `Grouper` (si dispo) ou aligner ensemble.

#### Méthode 2 (pro) : utiliser un visuel “KPI” custom

1. `Insertion` -> `Obtenir plus de visuels`.
2. Chercher un visuel de type `Card with States` / `Cards`.
3. Choisir un visuel qui supporte `Icon` + `Category` + `Value`.

### E. Ajouter une nouvelle Card KPI (étape par étape)

Exemple : tu veux une carte `BC du mois`.

1. Créer la mesure :

```DAX
KPI - BC du mois =
CALCULATE(
    COUNTROWS(bons_commande_fournisseur),
    FILTER(
        ALL(DimDate),
        DimDate[Year] = YEAR(TODAY())
            && DimDate[MonthNum] = MONTH(TODAY())
    )
)
```

Notes :
- Cette mesure suppose que `bons_commande_fournisseur` est reliée à `DimDate` via une date (ex: `date_commande`).
- Si ce n’est pas relié, il faut d’abord créer une colonne date (sans heure) et faire la relation.

2. Ajouter un visuel `Carte`.
3. Mettre `KPI - BC du mois` dans la carte.
4. Appliquer le même style que les autres (Titre, bordure, ombre, format nombre).
5. Aligner :
   - Sélectionner toutes les cartes -> `Format` -> `Aligner` -> `Aligner en haut`
   - Puis `Distribuer horizontalement`.

### F. Alignement / grille (pour un rendu “front”)

1. `Affichage` -> activer `Lignes de grille` + `Aligner sur la grille`.
2. Utiliser :
   - `Format` -> `Aligner`
   - `Format` -> `Distribuer`.
3. Conserver la même hauteur/largeur pour toutes les cards KPI.

### G. Couleurs conditionnelles (ex: Alertes Stock en orange)

  Pour une carte “Alertes Stock” :

1. Sélectionner la carte.
2. `Format` -> `Étiquette de données` (ou `Valeur`).
3. Chercher `Couleur` -> cliquer `fx`.
4. Règles exemple :
   - si `KPI - Alertes Stock` >= 1 -> orange `#FFAE1F`
   - sinon -> vert `#13DEB9`.

### H. Tutoriel complet : Card KPI “Demandes d’Achat” en violet (et garder l’emplacement)

Objectif : créer exactement une card KPI comme dans ton front :

- **Titre** : `Demandes d'Achat`
- **Valeur** : nombre total de demandes
- **Couleur principale** : violet (ex: `#7C3AED` ou `#b085ff`)
- **Placement** : rester au même endroit (pas bouger quand tu ajustes d’autres visuels)

#### Étape 1 — Créer la mesure DAX

1. Dans Power BI : onglet `Modélisation`.
2. Cliquer `Nouvelle mesure`.
3. Coller la mesure suivante :

```DAX
KPI - Demandes d'Achat =
COUNTROWS(demandes_achat)
```

4. Appuyer sur `Entrée`.

#### Étape 2 — Ajouter le visuel Card

1. Aller sur la page `Dashboard`.
2. Dans `Visualisations`, cliquer sur le visuel **Carte**.
3. Une carte vide apparaît sur la page.
4. Dans le panneau `Données`, glisser la mesure `KPI - Demandes d'Achat` dans le champ `Valeur` de la carte.

#### Étape 3 — Mettre le titre “Demandes d'Achat”

1. Sélectionner la carte.
2. Aller dans `Format` (rouleau de peinture).
3. Ouvrir `Titre`.
4. Activer `Titre`.
5. Dans `Texte du titre`, saisir : `Demandes d'Achat`.
6. Choisir :
   - Taille (ex: 12-14)
   - Couleur : gris foncé ou violet selon ton style.

#### Étape 4 — Choisir le violet (2 façons)

##### Option A (recommandée) : violet sur la valeur + bordure

1. `Format` -> chercher `Étiquette de données` / `Valeur`.
2. Mettre :
   - Taille (ex: 24-32)
   - Gras (si option dispo)
3. Sur `Couleur`, choisir un violet, par exemple :
   - Violet net : `#7C3AED`
   - Violet soft (proche de ton front) : `#b085ff`

Puis ajouter une bordure violette :

1. `Format` -> `Effets` (ou `Général` selon version).
2. Activer `Bordure`.
3. Couleur : même violet (`#7C3AED`).
4. Épaisseur : 2 (ou 3 si tu veux un style “border-start”).

##### Option B : fond violet clair + valeur violette

1. `Format` -> `Arrière-plan`.
2. Activer `Arrière-plan`.
3. Couleur : un violet très clair (ex: `#F3E8FF`).
4. Transparence : 0% à 20%.
5. Garder la valeur en violet foncé (ex: `#7C3AED`).

#### Étape 5 — Ajouter une ombre (effet “card”)

1. `Format` -> `Effets`.
2. Activer `Ombre`.
3. Choisir une ombre légère (petite distance + faible flou) pour imiter Bootstrap `shadow-sm`.

#### Étape 6 — Positionner la card (garder le même emplacement)

1. Activer la grille : `Affichage` -> activer `Lignes de grille`.
2. Activer : `Aligner sur la grille`.
3. Déplacer la carte à l’endroit voulu (ligne KPI en haut).

Pour garder le même emplacement et éviter de la bouger par erreur :

1. Une fois placé, cliquer sur la carte.
2. `Format` -> `Général` -> `Propriétés` (ou `Position`).
3. Noter (ou fixer) les valeurs :
   - `X`
   - `Y`
   - `Largeur`
   - `Hauteur`

Si tu as plusieurs cards alignées :

1. Sélectionner toutes les cards (CTRL + clic sur chaque).
2. Menu `Format` ->
   - `Aligner` -> `Aligner en haut`
   - `Distribuer` -> `Distribuer horizontalement`

#### Étape 7 — Verrouiller la card (pour ne plus la déplacer)

Selon la version Power BI :

1. Activer le verrouillage : `Affichage` -> `Verrouiller les objets`.
2. Une fois actif, tes visuels ne bougent plus tant que tu ne désactives pas.

Alternative : ouvrir le `Volet de sélection` (`Affichage` -> `Volet de sélection`) et verrouiller l’objet si l’option existe.

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

## 10) Design : changer les couleurs / ajouter des Cards KPI (comme le front)

Cette section t’aide à obtenir un rendu proche de `Dashboard.vue`.

### A. Changer les couleurs globales (Thème)

1. Dans Power BI Desktop : onglet `Affichage`.
2. Cliquer `Thèmes`.
3. Options :
   - Choisir un thème existant (rapide)
   - Ou `Personnaliser le thème actuel`.
4. Dans `Couleurs des données`, définir une palette proche de ton front (exemples) :
   - Primaire : `#5D87FF`
   - Success : `#13DEB9`
   - Warning : `#FFAE1F`
   - Danger : `#FA896B`
   - Info : `#49BEFF`
5. Valider `Appliquer`.

Astuce : si tu veux un thème réutilisable sur d’autres rapports : `Affichage` -> `Thèmes` -> `Enregistrer le thème actuel`.

### B. Changer la couleur d’un visuel (au cas par cas)

1. Sélectionner le visuel.
2. Panneau `Visualisations` -> onglet `Format` (icône rouleau de peinture).
3. Selon le visuel :
   - `Couleurs des données` : choisir la couleur des séries
   - `Titre` : couleur/typo
   - `Arrière-plan` : couleur + transparence
   - `Effets` / `Ombre` / `Bordure` : look “carte”.

### C. Créer une Card KPI “propre” (fond + bordure + titre)

Power BI propose plusieurs types de cartes. Pour imiter ton front, utilise :
- **Carte** (simple) ou **Carte (nouvelle)** si disponible.

Étapes :
1. Ajouter un visuel `Carte`.
2. Glisser la mesure KPI (ex: `KPI - Total Demandes Achat`) dans le champ du visuel.
3. Avec la carte sélectionnée -> `Format` :
   - `Titre` -> `Activé` -> mettre un titre (ex: `Demandes d'Achat`)
   - `Étiquette de données` / `Valeur` :
     - augmenter la taille (ex: 20-32)
     - mettre en gras
   - `Arrière-plan` -> `Activé` :
     - couleur : blanc (`#FFFFFF`)
     - transparence : 0%
   - `Bordure` (ou `Effets` -> `Bordure`) -> `Activé` :
     - couleur proche de la KPI (ex: primaire `#5D87FF`)
     - épaisseur (1-3)
   - `Ombre` -> `Activé` : léger (comme `shadow-sm`).

### D. Ajouter une “icône” sur une card (équivalent des icônes ti)

Les cartes natives n’ont pas toujours une icône à droite comme dans ton Vue.
Deux méthodes simples :
#### Méthode 1 (simple) : insérer une Image

1. `Insertion` -> `Image`.
2. Choisir une icône (png/svg converti en png).
3. Placer l’image à droite de la carte.
4. Sélectionner la carte + l’image -> `Format` -> `Grouper` (si dispo) ou aligner ensemble.

#### Méthode 2 (pro) : utiliser un visuel “KPI” custom

1. `Insertion` -> `Obtenir plus de visuels`.
2. Chercher un visuel de type `Card with States` / `Cards`.
3. Choisir un visuel qui supporte `Icon` + `Category` + `Value`.

### E. Ajouter une nouvelle Card KPI (étape par étape)

Exemple : tu veux une carte `BC du mois`.

1. Créer la mesure :
```DAX
KPI - BC du mois =
CALCULATE(
    COUNTROWS(bons_commande_fournisseur),
    FILTER(
        ALL(DimDate),
        DimDate[Year] = YEAR(TODAY())
            && DimDate[MonthNum] = MONTH(TODAY())
    )
)
```
Notes :
- Cette mesure suppose que `bons_commande_fournisseur` est reliée à `DimDate` via une date (ex: `date_commande`).
- Si ce n’est pas relié, il faut d’abord créer une colonne date (sans heure) et faire la relation.

2. Ajouter un visuel `Carte`.
3. Mettre `KPI - BC du mois` dans la carte.
4. Appliquer le même style que les autres (Titre, bordure, ombre, format nombre).
5. Aligner :
   - Sélectionner toutes les cartes -> `Format` -> `Aligner` -> `Aligner en haut`
   - Puis `Distribuer horizontalement`.

### F. Alignement / grille (pour un rendu “front”)

1. `Affichage` -> activer `Lignes de grille` + `Aligner sur la grille`.
2. Utiliser :
   - `Format` -> `Aligner`
   - `Format` -> `Distribuer`.
3. Conserver la même hauteur/largeur pour toutes les cards KPI.

### G. Couleurs conditionnelles (ex: Alertes Stock en orange)

Pour une carte “Alertes Stock” :
1. Sélectionner la carte.
2. `Format` -> `Étiquette de données` (ou `Valeur`).
3. Chercher `Couleur` -> cliquer `fx`.
4. Règles exemple :
   - si `KPI - Alertes Stock` >= 1 -> orange `#FFAE1F`
   - sinon -> vert `#13DEB9`.

### H. Tutoriel complet : Card KPI “Demandes d’Achat” en violet (et garder l’emplacement)

Objectif : créer exactement une card KPI comme dans ton front :
- **Titre** : `Demandes d'Achat`
- **Valeur** : nombre total de demandes
- **Couleur principale** : violet (ex: `#7C3AED` ou `#b085ff`)
- **Placement** : rester au même endroit (pas bouger quand tu ajustes d’autres visuels)

#### Étape 1 — Créer la mesure DAX

1. Dans Power BI : onglet `Modélisation`.
2. Cliquer `Nouvelle mesure`.
3. Coller la mesure suivante :
```DAX
KPI - Demandes d'Achat =
COUNTROWS(demandes_achat)
```

4. Appuyer sur `Entrée`.

#### Étape 2 — Ajouter le visuel Card

1. Aller sur la page `Dashboard`.
2. Dans `Visualisations`, cliquer sur le visuel **Carte**.
3. Une carte vide apparaît sur la page.
4. Dans le panneau `Données`, glisser la mesure `KPI - Demandes d'Achat` dans le champ `Valeur` de la carte.

#### Étape 3 — Mettre le titre “Demandes d'Achat”

1. Sélectionner la carte.
2. Aller dans `Format` (rouleau de peinture).
3. Ouvrir `Titre`.
4. Activer `Titre`.
5. Dans `Texte du titre`, saisir : `Demandes d'Achat`.
6. Choisir :
   - Taille (ex: 12-14)
   - Couleur : gris foncé ou violet selon ton style.

#### Étape 4 — Choisir le violet (2 façons)

##### Option A (recommandée) : violet sur la valeur + bordure

1. `Format` -> chercher `Étiquette de données` / `Valeur`.
2. Mettre :
   - Taille (ex: 24-32)
   - Gras (si option dispo)
3. Sur `Couleur`, choisir un violet, par exemple :
   - Violet net : `#7C3AED`
   - Violet soft (proche de ton front) : `#b085ff`

Puis ajouter une bordure violette :
1. `Format` -> `Effets` (ou `Général` selon version).
2. Activer `Bordure`.
3. Couleur : même violet (`#7C3AED`).
4. Épaisseur : 2 (ou 3 si tu veux un style “border-start”).

##### Option B : fond violet clair + valeur violette

1. `Format` -> `Arrière-plan`.
2. Activer `Arrière-plan`.
3. Couleur : un violet très clair (ex: `#F3E8FF`).
4. Transparence : 0% à 20%.
5. Garder la valeur en violet foncé (ex: `#7C3AED`).

#### Étape 5 — Ajouter une ombre (effet “card”)

1. `Format` -> `Effets`.
2. Activer `Ombre`.
3. Choisir une ombre légère (petite distance + faible flou) pour imiter Bootstrap `shadow-sm`.

#### Étape 6 — Positionner la card (garder le même emplacement)

1. Activer la grille : `Affichage` -> activer `Lignes de grille`.
2. Activer : `Aligner sur la grille`.
3. Déplacer la carte à l’endroit voulu (ligne KPI en haut).

Pour garder le même emplacement et éviter de la bouger par erreur :
1. Une fois placé, cliquer sur la carte.
2. `Format` -> `Général` -> `Propriétés` (ou `Position`).
3. Noter (ou fixer) les valeurs :
   - `X`
   - `Y`
   - `Largeur`
   - `Hauteur`

Si tu as plusieurs cards alignées :
1. Sélectionner toutes les cards (CTRL + clic sur chaque).
2. Menu `Format` ->
   - `Aligner` -> `Aligner en haut`
   - `Distribuer` -> `Distribuer horizontalement`

#### Étape 7 — Verrouiller la card (pour ne plus la déplacer)

Selon la version Power BI :
1. Activer le verrouillage : `Affichage` -> `Verrouiller les objets`.
2. Une fois actif, tes visuels ne bougent plus tant que tu ne désactives pas.

Alternative : ouvrir le `Volet de sélection` (`Affichage` -> `Volet de sélection`) et verrouiller l’objet si l’option existe.
