import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
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
      component: () => import('../views/Dashboard.vue'), // Temporaire
      meta: { requiresAuth: true }
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('../views/Dashboard.vue'), // Temporaire
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
    }
  ]
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('user');

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      next({ name: 'login' });
    } else {
      next();
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
