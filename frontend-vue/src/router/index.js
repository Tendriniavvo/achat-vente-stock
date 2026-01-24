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
      path: '/emplacements/:id',
      name: 'emplacements-details',
      component: () => import('../views/emplacements/DetailsEmplacement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/emplacements/:id/edit',
      name: 'emplacements-edit',
      component: () => import('../views/emplacements/EditEmplacement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/inventaire',
      name: 'inventaire',
      component: () => import('../views/Dashboard.vue'), // Temporaire
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
        // Par exemple, si '/achats' est autorisé, alors '/achats/create' ou '/achats/1' le sont aussi
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
        
        // Si on n'a pas accès au dashboard, on ne redirige pas vers lui pour éviter une boucle
        const hasDashboardAccess = userPermissions.some(p => p.path === '/dashboard');
        if (to.path !== '/dashboard' && (isAdmin || hasDashboardAccess)) {
          next({ name: 'dashboard' });
        } else if (userPermissions.length > 0) {
          // Rediriger vers la première page autorisée
          next(userPermissions[0].path);
        } else {
          // Aucun accès du tout, déconnexion forcée ou page neutre
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
