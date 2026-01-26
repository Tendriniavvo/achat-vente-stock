<template>
  <header class="app-header">
    <nav class="navbar navbar-expand-lg navbar-light">
      <ul class="navbar-nav">
        <li class="nav-item d-block d-xl-none">
          <a class="nav-link sidebartoggler nav-icon-hover" id="headerCollapse" href="javascript:void(0)">
            <i class="ti ti-menu-2"></i>
          </a>
        </li>
        <li class="nav-item">
          <div class="dropdown">
            <a class="nav-link nav-icon-hover" href="javascript:void(0)" id="dropAlerts" data-bs-toggle="dropdown" aria-expanded="false">
              <i class="ti ti-bell-ringing"></i>
              <div v-if="alerts.length > 0" class="notification bg-primary rounded-circle"></div>
            </a>
            <div class="dropdown-menu dropdown-menu-animate-up dropdown-menu-end p-0" aria-labelledby="dropAlerts" style="min-width: 300px; max-height: 400px; overflow-y: auto;">
              <div class="p-3 border-bottom d-flex align-items-center justify-content-between">
                <h6 class="mb-0 fw-semibold">Alertes de Stock</h6>
                <span v-if="alerts.length > 0" class="badge bg-primary rounded-pill">{{ alerts.length }}</span>
              </div>
              <div class="message-body">
                <div v-if="loadingAlerts" class="p-3 text-center">
                  <div class="spinner-border spinner-border-sm text-primary" role="status"></div>
                </div>
                <div v-else-if="alerts.length === 0" class="p-3 text-center text-muted">
                  Aucune alerte de stock
                </div>
                <template v-else>
                  <a v-for="alert in alerts" :key="alert.id" href="javascript:void(0)" class="py-3 px-4 d-flex align-items-center dropdown-item border-bottom">
                    <div class="d-inline-flex align-items-center justify-content-center bg-light-warning text-warning rounded-circle p-2 me-3">
                      <i class="ti ti-alert-triangle fs-6"></i>
                    </div>
                    <div class="w-75 d-inline-block v-middle">
                      <h6 class="mb-1 fw-semibold">{{ alert.article.nom }}</h6>
                      <span class="d-block text-muted">Qté: {{ alert.quantite }} / Min: {{ alert.article.stockMin }}</span>
                      <small class="text-muted">{{ alert.depot.nom }}</small>
                    </div>
                  </a>
                </template>
              </div>
              <div class="p-3 text-center">
                <router-link to="/stock/niveaux" class="btn btn-outline-primary btn-sm w-100">Voir tout le stock</router-link>
              </div>
            </div>
          </div>
        </li>
      </ul>
      <div class="navbar-collapse justify-content-end px-0" id="navbarNav">
        <ul class="navbar-nav flex-row ms-auto align-items-center justify-content-end">
          <!-- Affichage du rôle et bouton déconnexion à l'extérieur -->
          <li class="nav-item d-none d-md-flex align-items-center me-3">
            <span class="badge bg-light-primary text-primary rounded-pill px-3 py-2 me-3">
              <i class="ti ti-shield me-1"></i> {{ userRoles }}
            </span>
            <button @click="$emit('logout')" class="btn btn-outline-danger btn-sm d-flex align-items-center gap-2">
              <i class="ti ti-logout fs-4"></i>
              <span>Déconnexion</span>
            </button>
          </li>

          <li class="nav-item dropdown">
            <a class="nav-link nav-icon-hover" href="javascript:void(0)" id="drop2" data-bs-toggle="dropdown"
              aria-expanded="false">
              <img src="@/assets/images/profile/user-1.jpg" alt="" width="35" height="35" class="rounded-circle">
            </a>
            <div class="dropdown-menu dropdown-menu-end dropdown-menu-animate-up" aria-labelledby="drop2">
              <div class="message-body">
                <a href="javascript:void(0)" class="d-flex align-items-center gap-2 dropdown-item">
                  <i class="ti ti-user fs-6"></i>
                  <p class="mb-0 fs-3">{{ currentUser?.nom }} {{ currentUser?.prenom }}</p>
                </a>
                <a href="javascript:void(0)" class="d-flex align-items-center gap-2 dropdown-item">
                  <i class="ti ti-mail fs-6"></i>
                  <p class="mb-0 fs-3">{{ currentUser?.email }}</p>
                </a>
                <div class="dropdown-divider"></div>
                <div class="px-3 py-2">
                  <div class="d-flex align-items-center gap-2">
                    <i class="ti ti-building fs-6"></i>
                    <p class="mb-0 fs-3"><strong>Dépt:</strong> {{ userDepartement }}</p>
                  </div>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue';
import axios from 'axios';

const props = defineProps({
  currentUser: {
    type: Object,
    default: null
  }
});

defineEmits(['logout']);

const alerts = ref([]);
const loadingAlerts = ref(false);

const fetchAlerts = async () => {
  loadingAlerts.value = true;
  try {
    const response = await axios.get('/api/stocks/alerts');
    alerts.value = response.data;
  } catch (error) {
    console.error('Erreur lors du chargement des alertes:', error);
  } finally {
    loadingAlerts.value = false;
  }
};

onMounted(() => {
  fetchAlerts();
  // Rafraîchir toutes les 5 minutes
  setInterval(fetchAlerts, 5 * 60 * 1000);
});

// Formater les rôles pour l'affichage
const userRoles = computed(() => {
  const roles = props.currentUser?.roles;
  if (!roles || roles.length === 0) return 'Aucun rôle';
  return roles.map(r => r.nom).join(', ');
});

// Récupérer le nom du département
const userDepartement = computed(() => {
  return props.currentUser?.departement?.nom || 'N/A';
});
</script>

<style scoped>
.app-header {
  z-index: 1000 !important;
}

.dropdown-menu {
  z-index: 1050 !important;
}
</style>
