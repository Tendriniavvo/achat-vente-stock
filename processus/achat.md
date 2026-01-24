1. **Création de la demande d'achat (DA)**
   * **Rôle** : Demandeur (DA).
   * **Actions** : Créer la DA, la suivre (pas de validation ni de commande par le demandeur).
   * **Contrôles** : Statut initial "brouillon" ; motif de rejet possible ultérieurement.
   * **Objectif** : Identifier le besoin (articles, quantités, prix estimé).
2. **Approbation de la DA**
   * **Rôles** : Approbateurs N1/N2/N3 (selon seuils : montant, catégorie, fournisseur).
   * **Actions** : Valider la DA en fonction des attributs (ex. : restrictions ABAC par département, site).
   * **Contrôles** : Séparation des tâches (le demandeur ne peut pas approuver sa propre DA) ; principe du moindre privilège.
3. **Confirmation de la disponibilité des fonds**
   * **Rôle** : Finance.
   * **Actions** : Vérifier les fonds budgétaires avant transformation.
   * **Contrôles** : Blocage si fonds insuffisants ; intégration possible avec RH/API pour budgets.
4. **Transformation en bon de commande (BC) et négociation**
   * **Rôle** : Acheteur.
   * **Actions** : Transformer la DA en BC (pro-forma), négocier avec fournisseurs, gérer les conditions.
   * **Contrôles** : L'acheteur ne peut pas approuver s'il est le créateur ; numérotation automatique et non réutilisable.
5. **Validation du BC (si au-delà des seuils)**
   * **Rôles** : Responsable achats (pour seuils élevés, déblocage litiges) ; DG ou DAF (signataires légaux pour approbation finale).
   * **Actions** : Valider le BC ; approbation exceptionnelle pour montants importants.
   * **Contrôles** : Double validation sur opérations sensibles ; journalisation complète (historique non modifiable).
6. **Envoi de la commande au fournisseur**
   * **Rôle** : Acheteur ou système automatique.
   * **Actions** : Envoyer le BC ; suivre la date de livraison prévue.
   * **Contrôles** : Traçabilité (référence document, date/heure, utilisateur).
7. **Réception des articles**
   * **Rôle** : Magasinier réception.
   * **Actions** : Enregistrer la réception, contrôler quantités/qualité, générer bon de réception (contrôle vs BC et BL) ; réception partielle autorisée.
   * **Contrôles** : Mouvement d'entrée en stock (tracée : dépôt, emplacement, lot/série, coût) ; blocage si lot expiré/non conforme (DLUO/DLC).
8. **Réception et rapprochement de la facture fournisseur**
   * **Rôle** : Finance.
   * **Actions** : Rapprocher la facture vs réception vs BC (3-way match) ; vérifier TVA, montants.
   * **Contrôles** : Séparation des tâches (le réceptionneur ne peut pas valider la facture) ; blocage si écarts (mismatch) ; ajustements contrôlés si nécessaire.
9. **Paiement au fournisseur**
   * **Rôle** : Finance/DAF.
   * **Actions** : Valider et effectuer le paiement une fois le 3-way match confirmé.
   * **Contrôles** : Validation exceptionnelle pour seuils ; traçabilité complète pour audits ; réduction des risques de fraude.

## Remarques supplémentaires

* **Traçabilité et sécurité** : Chaque étape est tracée (utilisateur, date, motif) via journalisation ; respect de la séparation des tâches (ex. : même personne ne peut créer + approuver DA/BC, ni réceptionner + valider facture).
* **Intégration stock** : À la réception, mise à jour automatique des stocks (entrée, valorisation FIFO/CUMP) ; réservation possible si configurable.
* **KPI liés** : Pour le département Achats, des indicateurs comme le cycle time DA→BC, respect délais fournisseurs (OTD), taux de réception conforme, et taux litiges facture sont pilotés.
* Si des écarts surviennent (ex. : litiges), le responsable achats peut débloquer ; pour les ajustements stock liés, validation double requise.
