# Module Demandes d'Achat

Ce dossier contient toutes les vues liées à la gestion des demandes d'achat.

## Vues disponibles

### DemandesAchat.vue
- **Route**: `/achats`
- **Description**: Liste de toutes les demandes d'achat
- **Fonctionnalités**: 
  - Affichage de la liste des demandes
  - Filtrage par statut
  - Bouton pour créer une nouvelle demande

### CreateDemandeAchat.vue
- **Route**: `/achats/create`
- **Description**: Formulaire de création d'une nouvelle demande d'achat
- **Fonctionnalités**:
  - Saisie de la référence (auto-générée)
  - Ajout de plusieurs lignes d'articles
  - Sélection d'articles depuis la liste
  - Calcul automatique du total estimé
  - Prix estimé pré-rempli avec le prix d'achat de l'article

## Backend Endpoints utilisés

- `GET /api/articles` - Récupération de la liste des articles actifs
- `POST /api/demandes-achat` - Création d'une nouvelle demande d'achat
- `GET /api/demandes-achat` - Liste de toutes les demandes d'achat

## Modèle de données

### DemandeAchatRequest
```javascript
{
  reference: String,        // Ex: "DA-20260111-123456"
  demandeurId: Integer,     // ID de l'utilisateur connecté
  lignes: [
    {
      articleId: Integer,   // ID de l'article
      quantite: Integer,    // Quantité demandée
      prixEstime: Number    // Prix estimé (optionnel)
    }
  ]
}
```

## Navigation

Le lien "Achats" dans le sidebar principal mène vers `/achats` (liste des demandes).
