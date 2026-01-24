### Processus de Vente – Étapes détaillées

1. **Création du devis client (pro-forma)**
   * **Rôle principal** : Commercial
   * **Actions** : Créer le devis, proposer les articles, quantités, prix unitaires, remises (dans la limite du plafond autorisé par rôle), calcul du montant total
   * **Contrôles** :
     * Remises plafonnées par défaut
     * Pas de livraison possible sans stock réservé (vérification en aval)
     * Statut initial : brouillon
   * **Objectif** : Présenter une offre formelle au client
2. **Validation du devis (si remise exceptionnelle ou conditions particulières)**
   * **Rôle** : Responsable ventes
   * **Actions** : Valider les remises élevées ou conditions hors standard
   * **Contrôles** : Double validation sur opérations sensibles (ex. : remise > plafond) ; journalisation complète
3. **Transformation en commande client**
   * **Rôle principal** : Commercial
   * **Actions** : Transformer le devis accepté en commande client ; confirmer la date de livraison prévue
   * **Contrôles** :
     * Vérification de la disponibilité stock → **réservation stock** obligatoire à ce stade (configurable dans le système)
     * Allocation FIFO / FEFO selon nature des produits (périssables → FEFO prioritaire)
     * Blocage si stock insuffisant ou lots non conformes/expirés
   * **Objectif** : Engager fermement la vente et réserver le stock pour éviter les sur-ventes
4. **Préparation et picking (préparation de la livraison)**
   * **Rôle principal** : Magasinier sortie
   * **Actions** :
     * Préparer la sortie / livraison
     * Scanner les articles, confirmer picking (article, quantité, emplacement, lot/série, zone)
     * Gérer les priorités, lignes/heure
   * **Contrôles** : Traçabilité complète du mouvement de sortie ; productivité mesurée (lignes/heure, erreurs picking)
5. **Livraison / Sortie effective du stock**
   * **Rôle** : Magasinier / Commercial (confirmation finale)
   * **Actions** : Générer le bon de livraison ; expédier au client
   * **Contrôles** :
     * Mouvement de sortie tracé (référence document, date/heure, utilisateur, dépôt, emplacement, lot, coût)
     * Mise à jour automatique du stock (diminution quantité + valorisation)
     * Pas de livraison sans réservation préalable effective
6. **Émission de la facture client**
   * **Rôle principal** : Comptable client / Finance
   * **Actions** :
     * Créer la facture à partir de la commande client + livraison
     * Appliquer taxes (TVA), remises validées, calcul montant total
     * Gérer les avoirs éventuels (retour, erreur prix, casse)
   * **Contrôles** :
     * Séparation des tâches : même personne ne doit pas créer client + valider avoir + encaisser
     * Contrôle TVA et cohérence prix/quantités vs commande/livraison
7. **Encaissement (paiement client)**
   * **Rôle** : Finance / Comptable client
   * **Actions** : Enregistrer le paiement (virement, carte, etc.), rapprocher avec la facture
   * **Contrôles** :
     * Traçabilité complète (montant, date, mode paiement, utilisateur)
     * Mise à jour statut facture → payée
     * Suivi des retards / impayés (via KPI backlog)

### Remarques supplémentaires (issues du cahier des charges)

* **Traçabilité et sécurité** : Chaque étape génère un mouvement de stock tracé + journal d’audit (utilisateur, date, motif, référence document). Respect strict de la séparation des tâches.
* **Gestion du stock** : Réservation à la commande client (obligatoire pour éviter ruptures apparentes), allocation FIFO/FEFO, blocage lots expirés/non conformes.
* **Contrôle interne** :
  * Pas de livraison sans stock réservé
  * Double validation sur remises exceptionnelles / annulations de commande
  * Accès temporaire avec justification et expiration possible
* **KPI liés au processus de vente** (pilotage par rôle) :
  * Commandes en cours, livrées, en retard
  * Taux d’annulation commandes + motifs
  * Remises accordées vs plafond / exceptions
  * Avoirs : volume, valeur, causes (retour, erreur prix, casse)
  * Backlog non servi (stock insuffisant)
