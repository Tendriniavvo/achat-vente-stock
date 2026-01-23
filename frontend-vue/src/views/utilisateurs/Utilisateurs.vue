<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Utilisateurs</h5>
            <router-link to="/utilisateurs/create" class="btn btn-primary">
              <i class="ti ti-plus"></i> Nouvel Utilisateur
            </router-link>
          </div>

          <!-- Filtres -->
          <div class="row mb-3">
            <div class="col-md-3">
              <label for="filtreDepartement" class="form-label">Filtrer par département :</label>
              <select 
                id="filtreDepartement" 
                class="form-select" 
                v-model="filtreDepartementId"
              >
                <option value="tous">Tous les départements</option>
                <option value="aucun">Sans département</option>
                <option v-for="dept in departements" :key="dept.id" :value="dept.id">
                  {{ dept.nom }}
                </option>
              </select>
            </div>
            <div class="col-md-3">
              <label for="filtreActif" class="form-label">Filtrer par statut :</label>
              <select 
                id="filtreActif" 
                class="form-select" 
                v-model="filtreActif"
              >
                <option value="tous">Tous</option>
                <option value="actif">Actifs</option>
                <option value="inactif">Inactifs</option>
              </select>
            </div>
          </div>

          <div v-if="errorMessage" class="alert alert-danger" role="alert">
            {{ errorMessage }}
          </div>

          <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
            {{ successMessage }}
            <button type="button" class="btn-close" @click="successMessage = ''"></button>
          </div>

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else-if="utilisateursFiltres.length === 0" class="text-center py-5">
            <i class="ti ti-users" style="font-size: 3rem; color: #ccc;"></i>
            <p class="text-muted mt-3">Aucun utilisateur trouvé</p>
            <router-link to="/utilisateurs/create" class="btn btn-primary btn-sm">
              <i class="ti ti-plus"></i> Créer le premier utilisateur
            </router-link>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Nom complet</th>
                  <th>Email</th>
                  <th>Département</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="utilisateur in utilisateursFiltres" :key="utilisateur.id">
                  <td>
                    <strong>{{ utilisateur.prenom }} {{ utilisateur.nom }}</strong>
                  </td>
                  <td>{{ utilisateur.email }}</td>
                  <td>
                    <span v-if="utilisateur.departementNom" class="badge bg-info">
                      {{ utilisateur.departementNom }}
                    </span>
                    <span v-else class="text-muted">Non assigné</span>
                  </td>
                  <td>
                    <span :class="utilisateur.actif ? 'badge bg-success' : 'badge bg-secondary'">
                      {{ utilisateur.actif ? 'Actif' : 'Inactif' }}
                    </span>
                  </td>
                  <td>
                    <router-link 
                      :to="`/utilisateurs/${utilisateur.id}`"
                      class="btn btn-sm btn-outline-primary me-1" 
                      title="Voir détails"
                    >
                      <i class="ti ti-eye"></i>
                    </router-link>
                    <router-link 
                      :to="`/utilisateurs/${utilisateur.id}/edit`"
                      class="btn btn-sm btn-outline-warning me-1" 
                      title="Modifier"
                    >
                      <i class="ti ti-edit"></i>
                    </router-link>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'Utilisateurs',
  components: {
    MainLayout
  },
  data() {
    return {
      utilisateurs: [],
      departements: [],
      filtreDepartementId: 'tous',
      filtreActif: 'tous',
      isLoading: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  computed: {
    utilisateursFiltres() {
      let filtered = this.utilisateurs;
      
      // Filtre par département
      if (this.filtreDepartementId === 'aucun') {
        filtered = filtered.filter(u => !u.departementId);
      } else if (this.filtreDepartementId !== 'tous') {
        filtered = filtered.filter(u => u.departementId === this.filtreDepartementId);
      }
      
      // Filtre par statut
      if (this.filtreActif === 'actif') {
        filtered = filtered.filter(u => u.actif);
      } else if (this.filtreActif === 'inactif') {
        filtered = filtered.filter(u => !u.actif);
      }
      
      return filtered;
    }
  },
  mounted() {
    this.loadDepartements();
    this.loadUtilisateurs();
  },
  methods: {
    async loadDepartements() {
      try {
        const response = await axios.get('/api/departements');
        this.departements = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement des départements:', error);
      }
    },
    async loadUtilisateurs() {
      this.isLoading = true;
      this.errorMessage = '';
      try {
        const response = await axios.get('/api/utilisateurs');
        this.utilisateurs = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement des utilisateurs:', error);
        this.errorMessage = 'Erreur lors du chargement des utilisateurs.';
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.table-responsive {
  margin-top: 1rem;
}
</style>
