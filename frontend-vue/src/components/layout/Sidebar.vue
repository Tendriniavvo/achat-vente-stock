<template>
  <aside class="left-sidebar">
    <div>
      <div class="brand-logo d-flex align-items-center justify-content-between">
        <router-link to="/dashboard" class="text-nowrap logo-img">
          <img src="@/assets/images/logos/dark-logo.svg" width="180" alt="" />
        </router-link>
        <div class="close-btn d-xl-none d-block sidebartoggler cursor-pointer" id="sidebarCollapse">
          <i class="ti ti-x fs-8"></i>
        </div>
      </div>
      
      <!-- Sidebar navigation-->
      <nav class="sidebar-nav scroll-sidebar" data-simplebar="">
        <ul id="sidebarnav">
          <li class="nav-small-cap">
            <i class="ti ti-dots nav-small-cap-icon fs-4"></i>
            <span class="hide-menu">Accueil</span>
          </li>
          <li v-if="hasPermission('/dashboard')" class="sidebar-item">
            <router-link class="sidebar-link" to="/dashboard" aria-expanded="false">
              <span><i class="ti ti-layout-dashboard"></i></span>
              <span class="hide-menu">Dashboard</span>
            </router-link>
          </li>
          
          <li class="nav-small-cap">
            <i class="ti ti-dots nav-small-cap-icon fs-4"></i>
            <span class="hide-menu">Gestion</span>
          </li>
          
          <!-- Achats avec dropdown -->
          <li v-if="hasPermission('/achats')" class="sidebar-item">
            <a class="sidebar-link has-arrow" :class="{ active: isAchatsRoute() }" href="javascript:void(0)" :aria-expanded="achatsMenuOpen" @click="toggleAchatsMenu">
              <span><i class="ti ti-shopping-cart"></i></span>
              <span class="hide-menu">Achats</span>
            </a>
            <ul class="collapse first-level" :class="{ show: achatsMenuOpen }">
              <li class="sidebar-item">
                <router-link to="/achats" class="sidebar-link">
                  <div class="round-16 d-flex align-items-center justify-content-center">
                    <i class="ti ti-circle"></i>
                  </div>
                  <span class="hide-menu">Demandes d'Achat</span>
                </router-link>
              </li>
              <li class="sidebar-item">
                <router-link to="/achats/create" class="sidebar-link">
                  <div class="round-16 d-flex align-items-center justify-content-center">
                    <i class="ti ti-circle"></i>
                  </div>
                  <span class="hide-menu">Nouvelle Demande</span>
                </router-link>
              </li>
              <li class="sidebar-item">
                <router-link to="/commandes-achat" class="sidebar-link">
                  <div class="round-16 d-flex align-items-center justify-content-center">
                    <i class="ti ti-circle"></i>
                  </div>
                  <span class="hide-menu">Commandes d'Achat</span>
                </router-link>
              </li>
            </ul>
          </li>
          
          <li v-if="hasPermission('/ventes')" class="sidebar-item">
            <router-link class="sidebar-link" to="/ventes" aria-expanded="false">
              <span><i class="ti ti-coin"></i></span>
              <span class="hide-menu">Ventes</span>
            </router-link>
          </li>

          <li v-if="hasPermission('/budgets')" class="sidebar-item">
            <router-link class="sidebar-link" to="/budgets" aria-expanded="false">
              <span><i class="ti ti-wallet"></i></span>
              <span class="hide-menu">Budgets</span>
            </router-link>
          </li>
          
          <!-- Stock avec dropdown -->
          <li v-if="hasPermission('/stock')" class="sidebar-item">
            <a class="sidebar-link has-arrow" :class="{ active: isStockRoute() }" href="javascript:void(0)" :aria-expanded="stockMenuOpen" @click="toggleStockMenu">
              <span><i class="ti ti-package"></i></span>
              <span class="hide-menu">Stock</span>
            </a>
            <ul class="collapse first-level" :class="{ show: stockMenuOpen }">
              <li class="sidebar-item">
                <router-link to="/stock" class="sidebar-link">
                  <div class="round-16 d-flex align-items-center justify-content-center">
                    <i class="ti ti-circle"></i>
                  </div>
                  <span class="hide-menu">Vue d'ensemble</span>
                </router-link>
              </li>
              <li v-if="hasPermission('/depots')" class="sidebar-item">
                <router-link to="/depots" class="sidebar-link">
                  <div class="round-16 d-flex align-items-center justify-content-center">
                    <i class="ti ti-circle"></i>
                  </div>
                  <span class="hide-menu">Dépôts</span>
                </router-link>
              </li>
              <li v-if="hasPermission('/emplacements')" class="sidebar-item">
                <router-link to="/emplacements" class="sidebar-link">
                  <div class="round-16 d-flex align-items-center justify-content-center">
                    <i class="ti ti-circle"></i>
                  </div>
                  <span class="hide-menu">Emplacements</span>
                </router-link>
              </li>
            </ul>
          </li>
          
          <li v-if="hasPermission('/inventaire')" class="sidebar-item">
            <router-link class="sidebar-link" to="/inventaire" aria-expanded="false">
              <span><i class="ti ti-clipboard-list"></i></span>
              <span class="hide-menu">Inventaire</span>
            </router-link>
          </li>
          
          <li class="nav-small-cap">
            <i class="ti ti-dots nav-small-cap-icon fs-4"></i>
            <span class="hide-menu">Données</span>
          </li>
          <li v-if="hasPermission('/clients')" class="sidebar-item">
            <router-link class="sidebar-link" to="/clients" aria-expanded="false">
              <span><i class="ti ti-user-check"></i></span>
              <span class="hide-menu">Clients</span>
            </router-link>
          </li>
          <li v-if="hasPermission('/fournisseurs')" class="sidebar-item">
            <router-link class="sidebar-link" to="/fournisseurs" aria-expanded="false">
              <span><i class="ti ti-users"></i></span>
              <span class="hide-menu">Fournisseurs</span>
            </router-link>
          </li>
          <li v-if="hasPermission('/articles')" class="sidebar-item">
            <router-link class="sidebar-link" to="/articles" aria-expanded="false">
              <span><i class="ti ti-article"></i></span>
              <span class="hide-menu">Articles</span>
            </router-link>
          </li>
          
          <li v-if="hasPermission('/utilisateurs') || isAdmin()" class="nav-small-cap">
            <i class="ti ti-dots nav-small-cap-icon fs-4"></i>
            <span class="hide-menu">Administration</span>
          </li>
          <li v-if="hasPermission('/utilisateurs') || isAdmin()" class="sidebar-item">
            <a class="sidebar-link has-arrow" :class="{ active: isUserRoute() }" href="javascript:void(0)" :aria-expanded="userMenuOpen" @click="toggleUserMenu">
              <span><i class="ti ti-user"></i></span>
              <span class="hide-menu">Utilisateurs</span>
            </a>
            <ul class="collapse first-level" :class="{ show: userMenuOpen }">
              <li class="sidebar-item">
                <router-link to="/utilisateurs" class="sidebar-link">
                  <div class="round-16 d-flex align-items-center justify-content-center">
                    <i class="ti ti-circle"></i>
                  </div>
                  <span class="hide-menu">Gestion des Utilisateurs</span>
                </router-link>
              </li>
              <li class="sidebar-item">
                <router-link to="/utilisateurs/create" class="sidebar-link">
                  <div class="round-16 d-flex align-items-center justify-content-center">
                    <i class="ti ti-circle"></i>
                  </div>
                  <span class="hide-menu">Créer un utilisateur</span>
                </router-link>
              </li>
            </ul>
          </li>

          <!-- Rôles & Habilitations -->
          <li v-if="hasPermission('/roles') || hasPermission('/habilitations') || isAdmin()" class="sidebar-item">
            <a class="sidebar-link has-arrow" :class="{ active: isRolesRoute() }" href="javascript:void(0)" :aria-expanded="rolesMenuOpen" @click="toggleRolesMenu">
              <span><i class="ti ti-shield-lock"></i></span>
              <span class="hide-menu">Rôles & Habilitations</span>
            </a>
            <ul class="collapse first-level" :class="{ show: rolesMenuOpen }">
              <li v-if="hasPermission('/roles')" class="sidebar-item">
                <router-link to="/roles" class="sidebar-link">
                  <div class="round-16 d-flex align-items-center justify-content-center">
                    <i class="ti ti-circle"></i>
                  </div>
                  <span class="hide-menu">Gestion des Rôles</span>
                </router-link>
              </li>
              <li v-if="hasPermission('/habilitations')" class="sidebar-item">
                <router-link to="/habilitations" class="sidebar-link">
                  <div class="round-16 d-flex align-items-center justify-content-center">
                    <i class="ti ti-circle"></i>
                  </div>
                  <span class="hide-menu">Habilitations</span>
                </router-link>
              </li>
            </ul>
          </li>
        </ul>
      </nav>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps({
  currentUser: {
    type: Object,
    default: null
  }
});

const route = useRoute();
const achatsMenuOpen = ref(false);
const stockMenuOpen = ref(false);
const userMenuOpen = ref(false);
const rolesMenuOpen = ref(false);

// Vérifier si l'utilisateur a un rôle spécifique
const hasRole = (roleNom) => {
  if (!props.currentUser || !props.currentUser.roles) return false;
  return props.currentUser.roles.some(r => r.nom.toUpperCase() === roleNom.toUpperCase());
};

// Vérifier si l'utilisateur est administrateur par ID (1)
const isAdmin = () => {
  if (!props.currentUser || !props.currentUser.roles) return false;
  return props.currentUser.roles.some(r => r.id === 1);
};

// Vérifier les permissions granulaires par chemin
const hasPermission = (path) => {
  if (isAdmin()) return true;
  const permissions = JSON.parse(localStorage.getItem('permissions') || '[]');
  return permissions.some(p => p.path === path);
};

// Vérifier si on est sur une page d'achats
const isAchatsRoute = () => {
  return route.path.startsWith('/achats') || 
         route.path.startsWith('/commandes-achat');
};

// Vérifier si on est sur une page de stock
const isStockRoute = () => {
  return route.path.startsWith('/stock') || 
         route.path.startsWith('/depots') ||
         route.path.startsWith('/emplacements');
};

// Vérifier si on est sur une page d'utilisateurs
const isUserRoute = () => {
  return route.path.startsWith('/utilisateurs');
};

// Vérifier si on est sur une page de rôles
const isRolesRoute = () => {
  return route.path.startsWith('/roles');
};

const updateMenuStates = () => {
  achatsMenuOpen.value = isAchatsRoute();
  stockMenuOpen.value = isStockRoute();
  userMenuOpen.value = isUserRoute();
  rolesMenuOpen.value = isRolesRoute();
};

onMounted(() => {
  updateMenuStates();
});

// Observer les changements de route
watch(() => route.path, () => {
  updateMenuStates();
});

const toggleAchatsMenu = () => {
  achatsMenuOpen.value = !achatsMenuOpen.value;
};

const toggleStockMenu = () => {
  stockMenuOpen.value = !stockMenuOpen.value;
};

const toggleUserMenu = () => {
  userMenuOpen.value = !userMenuOpen.value;
};

const toggleRolesMenu = () => {
  rolesMenuOpen.value = !rolesMenuOpen.value;
};
</script>

<style scoped>
.sidebar-item .has-arrow::after {
  content: '\f107';
  font-family: 'tabler-icons';
  position: absolute;
  right: 15px;
  transition: transform 0.3s;
}

.sidebar-item .has-arrow[aria-expanded="true"]::after {
  transform: rotate(180deg);
}

.sidebar-item .collapse {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease-out;
}

.sidebar-item .collapse.show {
  max-height: 500px;
  transition: max-height 0.3s ease-in;
}

.sidebar-item .first-level .sidebar-item {
  padding-left: 20px;
}

.sidebar-item .first-level .sidebar-link {
  padding: 8px 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.sidebar-item .first-level .sidebar-link .round-16 {
  min-width: 16px;
  width: 16px;
  height: 16px;
}

.sidebar-item .first-level .sidebar-link i {
  font-size: 8px;
}

.sidebar-link.active {
  background-color: #5d87ff;
  color: white !important;
}

.sidebar-link.active i {
  color: white !important;
}

.sidebar-link.router-link-active {
  background-color: #5d87ff;
  color: white !important;
}

.sidebar-link.router-link-active i {
  color: white !important;
}
</style>
