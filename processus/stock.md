### Processus de Gestion du Stock – Étapes et règles principales

1. **Entrée de stock (approvisionnement)**
   * **Origines principales** :
     * Réception fournisseur (après bon de commande)
     * Retour client
     * Ajustement positif (validé)
     * Transfert entrant (d’un autre dépôt)
   * **Rôle principal** : Magasinier réception
   * **Actions** :
     * Enregistrer la réception (quantités, lots/séries, emplacements)
     * Contrôler vs bon de commande et bon de livraison (réception partielle autorisée)
     * Générer mouvement d’entrée
   * **Règles imposées par le système** :
     * Traçabilité obligatoire : référence document, date/heure, utilisateur, dépôt, emplacement, quantités, coût unitaire
     * Numérotation automatique et non réutilisable des mouvements
     * Gestion des lots/séries/DLUO/DLC pour familles concernées
     * Blocage automatique si lot expiré ou non conforme
     * Mise à jour immédiate du stock théorique + valorisation (FIFO ou CUMP selon paramètre article)
2. **Réservation de stock**
   * **Déclencheur** : Commande client confirmée
   * **Règle** : Réservation obligatoire à la commande client (configurable : oui/non)
   * **Objectif** : Éviter les sur-ventes et ruptures apparentes
   * **Allocation** : FIFO par défaut, FEFO prioritaire pour produits périssables
   * **Contrôles** : Blocage de la livraison si stock réservé insuffisant
3. **Mouvements internes (hors achat/vente)**
   * **Types** :
     * Transfert inter-dépôts / inter-emplacements
     * Consommation interne
     * Rebut / destruction
   * **Rôle principal** : Magasinier + Chef magasin (validation transferts)
   * **Actions** : Scanner, confirmer picking, valider le transfert
   * **Contrôles** : Traçabilité complète, double validation pour transferts importants
4. **Sortie de stock (livraison ou consommation)**
   * **Origines principales** :
     * Livraison client
     * Consommation interne
     * Rebut
     * Ajustement négatif (validé)
     * Transfert sortant
   * **Rôle principal** : Magasinier sortie
   * **Actions** :
     * Préparer la sortie, scanner articles/lots/emplacements
     * Confirmer picking (quantité, article, zone, priorité, lignes/heure)
     * Générer mouvement de sortie
   * **Règles imposées** :
     * Allocation FIFO/FEFO respectée
     * Traçabilité complète (référence livraison/commande, utilisateur, etc.)
     * Mise à jour stock théorique + valorisation (sortie au coût moyen ou FIFO)
5. **Inventaire physique**
   * **Types supportés** :
     * Inventaire tournant (partiel, fréquent)
     * Inventaire annuel (global)
   * **Rôle principal** : Chef magasin (lancement + autorisation)
   * **Actions** :
     * Lancer l’inventaire (référence, date début/fin, dépôt concerné)
     * Saisir quantités physiques par article
     * Calculer écarts (théorique vs physique)
   * **Contrôles** :
     * Même personne ne peut pas effectuer l’inventaire + valider les ajustements
     * Ajustements proposés → validation par superviseur / chef magasin
     * Journalisation complète des écarts et motifs
6. **Ajustements de stock**
   * **Types** : Positif (écart +) ou négatif (écart –)
   * **Rôle** : Chef magasin / superviseur (proposition) + valideur (niveau supérieur)
   * **Actions** : Proposer ajustement avec motif obligatoire → double validation
   * **Contrôles** :
     * Double validation obligatoire pour tout ajustement (surtout négatif ou important)
     * Traçabilité + historique non modifiable
     * Impact sur valorisation tracé
7. **Valorisation et clôture périodique**
   * **Méthodes supportées** : FIFO ou CUMP (coût moyen pondéré unitaire)
   * **Règles** :
     * Clôture mensuelle : gel des coûts → interdiction mouvements rétrodatés sans autorisation
     * Gestion des écarts de valorisation (inventaire vs valorisation comptable)
     * Rapports : variation de coût, écarts de marge, valeur stock vs comptable
   * **Objectif** : Fiabiliser la valorisation pour la comptabilité

### Remarques supplémentaires (issues du cahier des charges)

* **Traçabilité totale** : Chaque mouvement doit être tracé (référence document, date/heure, utilisateur, motif, coût, lot, emplacement) → journal d’audit non modifiable.
* **Sécurité et contrôle interne** :
  * Principe du moindre privilège (ex. : magasinier ne peut pas modifier coûts ni valider gros ajustements)
  * Double validation sur opérations sensibles (ajustements, transferts majeurs)
  * Blocage automatique lots expirés / non conformes
* **KPI Magasin / Responsable Stock** :
  * Taux de précision stock (théorique vs physique)
  * Obsolescence / péremption (valeur, lots à risque)
  * Productivité préparation (lignes/heure, erreurs picking)
  * Temps de traitement réception (dock-to-stock)
* **Direction Générale** : Valeur stock totale + évolution, rotation stock (turnover), top 5 surstocks/obsolescence, taux écarts inventaire
