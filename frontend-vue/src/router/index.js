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
      component: () => import('../views/Dashboard.vue') // Temporaire, à remplacer plus tard
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
