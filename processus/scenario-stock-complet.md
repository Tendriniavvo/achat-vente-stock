# Scénario complet de gestion de Stock (Front + règles + API)

Ce document décrit un **scénario E2E** (end-to-end) couvrant :

- Mouvement Stock
- Inventaire
- Ajustements
- Valorisation Stock

Il est écrit pour être **testable depuis le front** (Vue) et vérifiable via :

- l’écran **Stocks** (quantité + valeur)
- l’écran **Lots** (quantité par lot)
- l’écran **Audits** (traçabilité)

---

## 0) Pré-requis (avant de tester)

### 0.1 Services

- Front : `http://localhost:5173`
- Back : `http://localhost:8080`

### 0.2 Données minimales

Tu dois avoir au minimum :

- 1 dépôt (ex: `Depot A`)
- 1 emplacement dans ce dépôt (ex: `A-01`)
- 1 utilisateur (ex: `Magasinier`) et idéalement 1 superviseur (ex: `Chef magasin`)
- 2 articles :
  - **Article non traçable lot** (ex: `ART-NT`) : `traceableLot=false`
  - **Article traçable lot** (ex: `ART-T`) : `traceableLot=true`

### 0.3 Règles fonctionnelles importantes (issues de `processus/stock.md`)

- **Traçabilité totale** : chaque mouvement doit garder `référence`, `date/heure`, `utilisateur`, `motif`, `dépôt`, `emplacement`, `lot` (si traçable).
- **Lots** : blocage automatique si lot expiré / non conforme.
- **Valorisation** : `CUMP` (aujourd’hui implémenté) ; `FIFO` prévu en étape 2/3.
- **Inventaire** : la même personne ne peut pas lancer l’inventaire et valider les ajustements.

---

## 1) Scénario A — Entrée de stock (ENTREE) + valorisation CUMP

### Objectif

- Augmenter `stocks.quantite`
- Augmenter `stocks.valeur`
- Tracer dans le journal d’audit le `deltaQuantite` et `deltaValeur`

### Étapes Front

1. Ouvrir : **Stock → Mouvements de stock** (`/stock/mouvements`)
2. Cliquer : **Nouveau Mouvement** (`/stock/mouvements/create`)
3. Créer une **ENTREE** avec :
   - dépôt
   - emplacement
   - article
   - quantité (ex: 10)
   - **coût unitaire (obligatoire)** (ex: 5)
4. Valider le mouvement (bouton **Valider**)

### Attendus

- Le mouvement passe au statut `VALIDE`
- L’écran **Stocks** montre :
  - quantité +10
  - valeur +50 (si 10 × 5)
- L’écran **Audits** contient une ligne `VALIDATION_MOUVEMENT` avec :
  - `deltaQuantite=10`
  - `deltaValeur=50`

### Règle bloquante

- Si `coutUnitaire` est vide sur une **ENTREE**, la validation doit échouer :
  - message attendu : `Coût unitaire obligatoire sur une entrée`

---

## 2) Scénario B — Sortie de stock (SORTIE) + valorisation CUMP

### Objectif

- Diminuer `stocks.quantite`
- Diminuer `stocks.valeur` selon le coût moyen (CUMP)

### Étapes Front

1. Créer un mouvement **SORTIE** (même dépôt / emplacement / article)
   - quantité (ex: 4)
   - coût unitaire laissé vide
2. Valider

### Attendus

- quantité -4
- valeur diminue selon le **coût moyen** (valeur/quantité avant sortie)
- Audit : `deltaQuantite=-4` et `deltaValeur` négatif

---

## 3) Scénario C — Gestion des lots (traçabilité) + FEFO/FIFO/LIFO (allocation)

### Objectif

- Vérifier qu’un article traçable exige un lot (ou auto-affectation)
- Vérifier que la sortie consomme des lots selon la stratégie (allocation)

### Rappels importants

- **FEFO/FIFO/LIFO** ici = **allocation de lots** (logistique), pas la valorisation comptable.
- Pour un article `traceableLot=true`, le lot peut être :
  - choisi manuellement, ou
  - auto-affecté (si tu laisses `lot=null` sur sortie/ajustement négatif, selon l’implémentation)

### Étapes Front

1. Faire 2 entrées sur l’article traçable (ART-T) avec 2 lots différents :
   - Lot L1 : date expiration proche, quantite 5
   - Lot L2 : date expiration plus lointaine, quantite 10
2. Faire une sortie sur ART-T quantité 6

### Attendus

- Si stratégie `FEFO` : consommation prioritaire du lot qui expire le plus tôt
- Si `FIFO` : consommation par `date_entree` la plus ancienne
- Si `LIFO` : consommation par `date_entree` la plus récente

À vérifier sur l’écran **Lots** (quantité restante) et sur le **détail mouvement** (lignes avec lots).

---

## 4) Scénario D — Inventaire (création → lignes → saisie → terminer)

### Objectif

- Générer des lignes d’inventaire
- Saisir le physique
- Calculer l’écart

### Étapes Front

1. Ouvrir : **Stock → Inventaires** (`/stock/inventaires`)
2. Créer un inventaire sur un dépôt
3. Ouvrir le détail inventaire
4. Cliquer : **Générer lignes**
5. Sur quelques lignes : modifier `Quantité physique` puis enregistrer
6. Cliquer : **Terminer**

### Attendus

- Les lignes existent et sont persistées
- Les écarts sont visibles (théorique vs physique)

---

## 5) Scénario E — Ajustements (proposition → validation → impact stock)

### Objectif

- Générer des ajustements à partir des écarts
- Valider un ajustement
- Générer un mouvement `AJUSTEMENT`
- Mettre à jour stock + audit

### Étapes Front

1. Dans le détail inventaire : cliquer **Générer ajustements**
2. Vérifier la liste des ajustements en bas de page
3. Pour un article traçable : choisir le lot (ou laisser auto si prévu)
4. Cliquer **Valider** sur un ajustement

### Attendus

- L’ajustement passe à `valide`
- Un mouvement `AJUSTEMENT` est validé automatiquement
- Stock/lot est mis à jour
- Audit contient les traces

---

## 6) Scénario F — Valorisation Stock (récap)

### Ce qui est en place (Étape 1)

- **CUMP** :
  - Entrée : valeur += coût unitaire × quantité
  - Sortie : valeur -= coût moyen × quantité (si coût non fourni)
- JournalAudit enrichi : `deltaQuantite` + `deltaValeur`

### Ce qui arrive ensuite (Étapes 2/3)

- FIFO comptable par lot (ajout coût sur lot)
- FIFO complet par couches (même sans lot)
- Clôture mensuelle (interdiction rétrodatage)

---

## 7) Checklist rapide (validation)

- [ ] ENTREE avec coût unitaire => stock quantité+valeur OK
- [ ] ENTREE sans coût unitaire => validation refusée
- [ ] SORTIE sans coût unitaire => valorisation par coût moyen (CUMP)
- [ ] Lots expirés/non conformes => blocage
- [ ] Inventaire : génération lignes + saisie physique
- [ ] Ajustements : génération + validation => mouvement AJUSTEMENT
- [ ] Audits : présence des `deltaValeur` / `deltaQuantite`
