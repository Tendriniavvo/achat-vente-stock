import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Dashboard.vue')
    },
    {
      path: '/achats',
      name: 'achats',
      component: () => import('../views/demandes-achat/DemandesAchat.vue')
    },
    {
      path: '/achats/create',
      name: 'achats-create',
      component: () => import('../views/demandes-achat/CreateDemandeAchat.vue')
    },
    {
      path: '/achats/:id',
      name: 'achats-details',
      component: () => import('../views/demandes-achat/DetailsDemandeAchat.vue')
    },
    {
      path: '/achats/:id/edit',
      name: 'achats-edit',
      component: () => import('../views/demandes-achat/EditDemandeAchat.vue')
    },
    {
      path: '/utilisateurs',
      name: 'utilisateurs',
      component: () => import('../views/utilisateurs/Utilisateurs.vue')
    },
    {
      path: '/utilisateurs/create',
      name: 'utilisateurs-create',
      component: () => import('../views/utilisateurs/CreateUtilisateur.vue')
    },
    {
      path: '/utilisateurs/:id',
      name: 'utilisateurs-details',
      component: () => import('../views/utilisateurs/DetailsUtilisateur.vue')
    },
    {
      path: '/utilisateurs/:id/edit',
      name: 'utilisateurs-edit',
      component: () => import('../views/utilisateurs/EditUtilisateur.vue')
    },
    {
      path: '/fournisseurs',
      name: 'fournisseurs',
      component: () => import('../views/Dashboard.vue') // Temporaire
    },
    {
      path: '/commandes-achat',
      name: 'commandes-achat',
      component: () => import('../views/Dashboard.vue') // Temporaire
    },
    {
      path: '/ventes',
      name: 'ventes',
      component: () => import('../views/Dashboard.vue') // Temporaire
    },
    {
      path: '/stock',
      name: 'stock',
      component: () => import('../views/Dashboard.vue') // Temporaire
    },
    {
      path: '/depots',
      name: 'depots',
      component: () => import('../views/depots/Depots.vue')
    },
    {
      path: '/depots/create',
      name: 'depots-create',
      component: () => import('../views/depots/CreateDepot.vue')
    },
    {
      path: '/depots/:id',
      name: 'depots-details',
      component: () => import('../views/depots/DetailsDepot.vue')
    },
    {
      path: '/depots/:id/edit',
      name: 'depots-edit',
      component: () => import('../views/depots/EditDepot.vue')
    },
    {
      path: '/emplacements',
      name: 'emplacements',
      component: () => import('../views/emplacements/Emplacements.vue')
    },
    {
      path: '/emplacements/create',
      name: 'emplacements-create',
      component: () => import('../views/emplacements/CreateEmplacement.vue')
    },
    {
      path: '/emplacements/:id',
      name: 'emplacements-details',
      component: () => import('../views/emplacements/DetailsEmplacement.vue')
    },
    {
      path: '/emplacements/:id/edit',
      name: 'emplacements-edit',
      component: () => import('../views/emplacements/EditEmplacement.vue')
    },
    {
      path: '/inventaire',
      name: 'inventaire',
      component: () => import('../views/Dashboard.vue') // Temporaire
    },
    {
      path: '/partenaires',
      name: 'partenaires',
      component: () => import('../views/Dashboard.vue') // Temporaire
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('../views/Dashboard.vue') // Temporaire
    }
  ]
})

export default router
