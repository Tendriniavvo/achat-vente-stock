import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/Dashboard.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
      meta: { guest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: Register,
      meta: { guest: true }
    },
    {
      path: '/achats',
      name: 'achats',
      component: () => import('../views/demandes-achat/DemandesAchat.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/achats/create',
      name: 'achats-create',
      component: () => import('../views/demandes-achat/CreateDemandeAchat.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/achats/:id',
      name: 'achats-details',
      component: () => import('../views/demandes-achat/DetailsDemandeAchat.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/achats/:id/edit',
      name: 'achats-edit',
      component: () => import('../views/demandes-achat/EditDemandeAchat.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/utilisateurs',
      name: 'utilisateurs',
      component: () => import('../views/utilisateurs/Utilisateurs.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/utilisateurs/create',
      name: 'utilisateurs-create',
      component: () => import('../views/utilisateurs/CreateUtilisateur.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/utilisateurs/:id',
      name: 'utilisateurs-details',
      component: () => import('../views/utilisateurs/DetailsUtilisateur.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/utilisateurs/:id/edit',
      name: 'utilisateurs-edit',
      component: () => import('../views/utilisateurs/EditUtilisateur.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/fournisseurs',
      name: 'fournisseurs',
      component: () => import('../views/fournisseurs/Fournisseurs.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/fournisseurs/create',
      name: 'fournisseurs-create',
      component: () => import('../views/fournisseurs/CreateFournisseur.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/fournisseurs/:id',
      name: 'fournisseurs-details',
      component: () => import('../views/fournisseurs/DetailsFournisseur.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/fournisseurs/:id/edit',
      name: 'fournisseurs-edit',
      component: () => import('../views/fournisseurs/EditFournisseur.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/commandes-achat',
      name: 'commandes-achat',
      component: () => import('../views/bons-commande/BonsCommandeFournisseur.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/commandes-achat/:id',
      name: 'bc-details',
      component: () => import('../views/bons-commande/DetailsBonCommandeFournisseur.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/commandes-achat/:id/edit',
      name: 'bc-edit',
      component: () => import('../views/bons-commande/EditBonCommandeFournisseur.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/ventes',
      name: 'ventes',
      component: () => import('../views/Dashboard.vue'), // Temporaire
      meta: { requiresAuth: true }
    },
    {
      path: '/stock',
      name: 'stock',
      component: () => import('../views/Dashboard.vue'), // Temporaire
      meta: { requiresAuth: true }
    },
    {
      path: '/stock/mouvements',
      name: 'stock-mouvements',
      component: () => import('../views/stock/MouvementsStock.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stock/mouvements/create',
      name: 'stock-mouvements-create',
      component: () => import('../views/stock/CreateMouvementStock.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stock/mouvements/:id',
      name: 'stock-mouvements-details',
      component: () => import('../views/stock/DetailsMouvementStock.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stock/stocks',
      name: 'stock-stocks',
      component: () => import('../views/stock/Stocks.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stock/lots',
      name: 'stock-lots',
      component: () => import('../views/stock/Lots.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stock/audits',
      name: 'stock-audits',
      component: () => import('../views/stock/Audits.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stock/ajustements',
      name: 'stock-ajustements',
      component: () => import('../views/stock/Ajustements.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stock/inventaires',
      name: 'stock-inventaires',
      component: () => import('../views/stock/Inventaires.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stock/inventaires/create',
      name: 'stock-inventaires-create',
      component: () => import('../views/stock/CreateInventaire.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stock/inventaires/:id',
      name: 'stock-inventaires-details',
      component: () => import('../views/stock/DetailsInventaire.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/depots',
      name: 'depots',
      component: () => import('../views/depots/Depots.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/depots/create',
      name: 'depots-create',
      component: () => import('../views/depots/CreateDepot.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/depots/:id',
      name: 'depots-details',
      component: () => import('../views/depots/DetailsDepot.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/depots/:id/edit',
      name: 'depots-edit',
      component: () => import('../views/depots/EditDepot.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/emplacements',
      name: 'emplacements',
      component: () => import('../views/emplacements/Emplacements.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/emplacements/create',
      name: 'emplacements-create',
      component: () => import('../views/emplacements/CreateEmplacement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/emplacements/:id/edit',
      name: 'edit-emplacement',
      component: () => import('../views/emplacements/EditEmplacement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/partenaires',
      name: 'partenaires',
      redirect: '/fournisseurs',
      meta: { requiresAuth: true }
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('../views/articles/Articles.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/articles/create',
      name: 'articles-create',
      component: () => import('../views/articles/CreateArticle.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/articles/:id',
      name: 'articles-details',
      component: () => import('../views/articles/DetailsArticle.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/articles/:id/edit',
      name: 'articles-edit',
      component: () => import('../views/articles/EditArticle.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/budgets',
      name: 'budgets',
      component: () => import('../views/budgets/Budgets.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/factures',
      name: 'factures',
      component: () => import('../views/budgets/Factures.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/factures/:id',
      name: 'facture-details',
      component: () => import('../views/budgets/DetailsFacture.vue'),
      meta: { requiresAuth: true }
    },
    // Ventes / Devis
    {
      path: '/devis',
      name: 'devis',
      component: () => import('../views/ventes/Devis.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/devis/create',
      name: 'devis-create',
      component: () => import('../views/ventes/CreateDevis.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/devis/:id',
      name: 'devis-details',
      component: () => import('../views/ventes/DetailsDevis.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/commandes-client',
      name: 'commandes-client',
      component: () => import('../views/ventes/CommandesClient.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/commandes-client/:id',
      name: 'commande-client-details',
      component: () => import('../views/ventes/DetailsCommandeClient.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/livraisons',
      name: 'livraisons',
      component: () => import('../views/ventes/Livraisons.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/livraisons/:id',
      name: 'livraison-details',
      component: () => import('../views/ventes/DetailsLivraison.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/paiements',
      name: 'paiements',
      component: () => import('../views/budgets/Paiements.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/receptions',
      name: 'receptions',
      component: () => import('../views/stock/Receptions.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/receptions/enregistrer/:bcId',
      name: 'receptions-enregistrer',
      component: () => import('../views/stock/EnregistrerReception.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/receptions/:id',
      name: 'receptions-details',
      component: () => import('../views/stock/DetailsReception.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/budgets/create',
      name: 'budgets-create',
      component: () => import('../views/budgets/CreateBudget.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/clients',
      name: 'clients',
      component: () => import('../views/clients/Clients.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/clients/create',
      name: 'clients-create',
      component: () => import('../views/clients/CreateClient.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/clients/:id',
      name: 'clients-details',
      component: () => import('../views/clients/DetailsClient.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/clients/:id/edit',
      name: 'clients-edit',
      component: () => import('../views/clients/CreateClient.vue'),
      props: { isEdit: true },
      meta: { requiresAuth: true }
    },
    {
      path: '/roles',
      name: 'roles',
      component: () => import('../views/roles/Roles.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/roles/create',
      name: 'roles-create',
      component: () => import('../views/roles/CreateRole.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/roles/:id/edit',
      name: 'roles-edit',
      component: () => import('../views/roles/EditRole.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/habilitations',
      name: 'habilitations',
      component: () => import('../views/utilisateurs/Habilitations.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('user');
  const userPermissions = JSON.parse(localStorage.getItem('permissions') || '[]');

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      next({ name: 'login' });
    } else {
      // Vérifier les permissions pour les routes protégées
      if (to.path === '/login' || to.path === '/register') {
        return next();
      }

      // Pour les autres routes, vérifier si l'un des chemins autorisés correspond
      const isAuthorized = userPermissions.some(p => {
        // On vérifie si le chemin de la route commence par le chemin autorisé
        return to.path === p.path || to.path.startsWith(p.path + '/');
      });

      // Cas particulier : l'admin a toujours accès à tout
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      const isAdmin = userData.user?.roles?.some(r => r.id === 1);

      if (isAdmin || isAuthorized) {
        next();
      } else {
        // Alerte et redirection sécurisée
        alert("Vous n'avez pas l'autorisation d'accéder à cette section.");
        
        const hasDashboardAccess = userPermissions.some(p => p.path === '/dashboard');
        if (to.path !== '/dashboard' && (isAdmin || hasDashboardAccess)) {
          next({ name: 'dashboard' });
        } else if (userPermissions.length > 0) {
          next(userPermissions[0].path);
        } else {
          localStorage.removeItem('user');
          localStorage.removeItem('permissions');
          next({ name: 'login' });
        }
      }
    }
  } else if (to.matched.some(record => record.meta.guest)) {
    if (isAuthenticated) {
      next({ name: 'dashboard' });
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router