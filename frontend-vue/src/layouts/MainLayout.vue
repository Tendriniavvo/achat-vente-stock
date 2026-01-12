<template>
  <div class="page-wrapper" id="main-wrapper" data-layout="vertical" data-navbarbg="skin6" data-sidebartype="full"
    data-sidebar-position="fixed" data-header-position="fixed">
    
    <!-- Sidebar Start -->
    <aside class="left-sidebar">
      <div>
        <div class="brand-logo d-flex align-items-center justify-content-between">
          <router-link to="/" class="text-nowrap logo-img">
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
            <li class="sidebar-item">
              <router-link class="sidebar-link" to="/" aria-expanded="false">
                <span><i class="ti ti-layout-dashboard"></i></span>
                <span class="hide-menu">Dashboard</span>
              </router-link>
            </li>
            
            <li class="nav-small-cap">
              <i class="ti ti-dots nav-small-cap-icon fs-4"></i>
              <span class="hide-menu">Gestion</span>
            </li>
            
            <!-- Achats avec dropdown -->
            <li class="sidebar-item">
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
                  <router-link to="/fournisseurs" class="sidebar-link">
                    <div class="round-16 d-flex align-items-center justify-content-center">
                      <i class="ti ti-circle"></i>
                    </div>
                    <span class="hide-menu">Fournisseurs</span>
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
            
            <li class="sidebar-item">
              <router-link class="sidebar-link" to="/ventes" aria-expanded="false">
                <span><i class="ti ti-coin"></i></span>
                <span class="hide-menu">Ventes</span>
              </router-link>
            </li>
            
            <!-- Stock avec dropdown -->
            <li class="sidebar-item">
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
                <li class="sidebar-item">
                  <router-link to="/depots" class="sidebar-link">
                    <div class="round-16 d-flex align-items-center justify-content-center">
                      <i class="ti ti-circle"></i>
                    </div>
                    <span class="hide-menu">Dépôts</span>
                  </router-link>
                </li>
                <li class="sidebar-item">
                  <router-link to="/emplacements" class="sidebar-link">
                    <div class="round-16 d-flex align-items-center justify-content-center">
                      <i class="ti ti-circle"></i>
                    </div>
                    <span class="hide-menu">Emplacements</span>
                  </router-link>
                </li>
              </ul>
            </li>
            
            <li class="sidebar-item">
              <router-link class="sidebar-link" to="/inventaire" aria-expanded="false">
                <span><i class="ti ti-clipboard-list"></i></span>
                <span class="hide-menu">Inventaire</span>
              </router-link>
            </li>
            
            <li class="nav-small-cap">
              <i class="ti ti-dots nav-small-cap-icon fs-4"></i>
              <span class="hide-menu">Données</span>
            </li>
            <li class="sidebar-item">
              <router-link class="sidebar-link" to="/partenaires" aria-expanded="false">
                <span><i class="ti ti-users"></i></span>
                <span class="hide-menu">Partenaires</span>
              </router-link>
            </li>
            <li class="sidebar-item">
              <router-link class="sidebar-link" to="/articles" aria-expanded="false">
                <span><i class="ti ti-article"></i></span>
                <span class="hide-menu">Articles</span>
              </router-link>
            </li>
            
            <li class="nav-small-cap">
              <i class="ti ti-dots nav-small-cap-icon fs-4"></i>
              <span class="hide-menu">Administration</span>
            </li>
            <li class="sidebar-item">
              <router-link class="sidebar-link" to="/utilisateurs" aria-expanded="false">
                <span><i class="ti ti-user"></i></span>
                <span class="hide-menu">Utilisateurs</span>
              </router-link>
            </li>
          </ul>
        </nav>
      </div>
    </aside>
    <!--  Sidebar End -->
    
    <!--  Main wrapper -->
    <div class="body-wrapper">
      <!--  Header Start -->
      <header class="app-header">
        <nav class="navbar navbar-expand-lg navbar-light">
          <ul class="navbar-nav">
            <li class="nav-item d-block d-xl-none">
              <a class="nav-link sidebartoggler nav-icon-hover" id="headerCollapse" href="javascript:void(0)">
                <i class="ti ti-menu-2"></i>
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link nav-icon-hover" href="javascript:void(0)">
                <i class="ti ti-bell-ringing"></i>
                <div class="notification bg-primary rounded-circle"></div>
              </a>
            </li>
          </ul>
          <div class="navbar-collapse justify-content-end px-0" id="navbarNav">
            <ul class="navbar-nav flex-row ms-auto align-items-center justify-content-end">
              <li class="nav-item dropdown">
                <a class="nav-link nav-icon-hover" href="javascript:void(0)" id="drop2" data-bs-toggle="dropdown"
                  aria-expanded="false">
                  <img src="@/assets/images/profile/user-1.jpg" alt="" width="35" height="35" class="rounded-circle">
                </a>
                <div class="dropdown-menu dropdown-menu-end dropdown-menu-animate-up" aria-labelledby="drop2">
                  <div class="message-body">
                    <a href="javascript:void(0)" class="d-flex align-items-center gap-2 dropdown-item">
                      <i class="ti ti-user fs-6"></i>
                      <p class="mb-0 fs-3">{{ user?.nom }} {{ user?.prenom }}</p>
                    </a>
                    <a href="javascript:void(0)" class="d-flex align-items-center gap-2 dropdown-item">
                      <i class="ti ti-mail fs-6"></i>
                      <p class="mb-0 fs-3">{{ user?.email }}</p>
                    </a>
                    <a href="#" @click.prevent="logout" class="btn btn-outline-primary mx-3 mt-2 d-block">Déconnexion</a>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </nav>
      </header>
      <!--  Header End -->
      
      <div style="padding-left: 15px !important; padding-right: 15px !important; padding-top: 80px !important;">
        <!-- Slot pour le contenu de la page -->
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const user = ref(null);
const achatsMenuOpen = ref(false);
const stockMenuOpen = ref(false);

// Vérifier si on est sur une page d'achats
const isAchatsRoute = () => {
  return route.path.startsWith('/achats') || 
         route.path.startsWith('/fournisseurs') || 
         route.path.startsWith('/commandes-achat');
};

// Vérifier si on est sur une page de stock
const isStockRoute = () => {
  return route.path.startsWith('/stock') || 
         route.path.startsWith('/depots') ||
         route.path.startsWith('/emplacements');
};

onMounted(() => {
  // Récupérer les informations de l'utilisateur depuis localStorage
  const userData = localStorage.getItem('user');
  if (userData) {
    user.value = JSON.parse(userData);
  } else {
    // Si pas d'utilisateur connecté, rediriger vers login
    router.push('/login');
  }
  
  // Ouvrir le menu Achats si on est sur une page d'achats
  achatsMenuOpen.value = isAchatsRoute();
  // Ouvrir le menu Stock si on est sur une page de stock
  stockMenuOpen.value = isStockRoute();
});

// Observer les changements de route
watch(() => route.path, () => {
  achatsMenuOpen.value = isAchatsRoute();
  stockMenuOpen.value = isStockRoute();
});

const logout = () => {
  localStorage.removeItem('user');
  router.push('/login');
};

const toggleAchatsMenu = () => {
  achatsMenuOpen.value = !achatsMenuOpen.value;
};

const toggleStockMenu = () => {
  stockMenuOpen.value = !stockMenuOpen.value;
};
</script>

<style>
.container-fluid.content-wrapper {
  padding-left: 15px !important;
  padding-right: 15px !important;
}

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
