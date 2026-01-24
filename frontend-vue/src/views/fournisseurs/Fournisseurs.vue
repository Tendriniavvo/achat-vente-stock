<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Gestion des Fournisseurs</h5>
            <router-link to="/fournisseurs/create" class="btn btn-primary">
              <i class="ti ti-plus"></i> Nouveau Fournisseur
            </router-link>
          </div>

          <!-- Filtres et Recherche -->
          <div class="row mb-4 g-3">
            <div class="col-md-6">
              <div class="input-group">
                <span class="input-group-text bg-transparent border-end-0">
                  <i class="ti ti-search text-muted"></i>
                </span>
                <input 
                  type="text" 
                  class="form-control border-start-0" 
                  placeholder="Rechercher par nom, email ou téléphone..." 
                  v-model="searchQuery"
                >
              </div>
            </div>
            <div class="col-md-3">
              <select class="form-select" v-model="filterStatut">
                <option value="tous">Tous les statuts</option>
                <option value="actif">Actifs uniquement</option>
                <option value="inactif">Inactifs uniquement</option>
              </select>
            </div>
          </div>

          <div v-if="errorMessage" class="alert alert-danger" role="alert">
            {{ errorMessage }}
          </div>

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else-if="filteredFournisseurs.length === 0" class="text-center py-5">
            <i class="ti ti-users-off fs-1 text-muted"></i>
            <p class="mt-3 text-muted">Aucun fournisseur trouvé</p>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Nom du Partenaire</th>
                  <th>Contact</th>
                  <th>Adresse</th>
                  <th class="text-center">Statut</th>
                  <th class="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="f in filteredFournisseurs" :key="f.id">
                  <td>
                    <div class="d-flex flex-column">
                      <span class="fw-bold text-primary">{{ f.nom }}</span>
                      <small class="text-muted">Inscrit le {{ formatDate(f.dateCreation) }}</small>
                    </div>
                  </td>
                  <td>
                    <div class="d-flex flex-column">
                      <span><i class="ti ti-mail me-1 small"></i>{{ f.email || 'N/A' }}</span>
                      <span><i class="ti ti-phone me-1 small"></i>{{ f.telephone || 'N/A' }}</span>
                    </div>
                  </td>
                  <td>
                    <span class="text-truncate d-inline-block" style="max-width: 200px;" :title="f.adresse">
                      {{ f.adresse || 'N/A' }}
                    </span>
                  </td>
                  <td class="text-center">
                    <span :class="f.actif ? 'badge bg-success' : 'badge bg-danger'">
                      {{ f.actif ? 'Actif' : 'Inactif' }}
                    </span>
                  </td>
                  <td class="text-end">
                    <div class="btn-group">
                      <router-link 
                        :to="`/fournisseurs/${f.id}`" 
                        class="btn btn-sm btn-outline-info"
                        title="Voir détails"
                      >
                        <i class="ti ti-eye"></i>
                      </router-link>
                      <router-link 
                        :to="`/fournisseurs/${f.id}/edit`" 
                        class="btn btn-sm btn-outline-warning"
                        title="Modifier"
                      >
                        <i class="ti ti-edit"></i>
                      </router-link>
                      <button 
                        class="btn btn-sm" 
                        :class="f.actif ? 'btn-outline-danger' : 'btn-outline-success'"
                        :title="f.actif ? 'Désactiver' : 'Activer'"
                        @click="toggleStatus(f)"
                      >
                        <i :class="f.actif ? 'ti ti-player-pause' : 'ti ti-player-play'"></i>
                      </button>
                    </div>
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
import MainLayout from '../../layouts/MainLayout.vue';

export default {
  name: 'Fournisseurs',
  components: {
    MainLayout
  },
  data() {
    return {
      fournisseurs: [],
      searchQuery: '',
      filterStatut: 'tous',
      isLoading: false,
      errorMessage: '',
      currentUser: null
    };
  },
  computed: {
    filteredFournisseurs() {
      return this.fournisseurs.filter(f => {
        const matchesSearch = 
          f.nom.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          (f.email && f.email.toLowerCase().includes(this.searchQuery.toLowerCase())) ||
          (f.telephone && f.telephone.includes(this.searchQuery));
        
        const matchesStatut = 
          this.filterStatut === 'tous' || 
          (this.filterStatut === 'actif' && f.actif) || 
          (this.filterStatut === 'inactif' && !f.actif);
        
        return matchesSearch && matchesStatut;
      });
    }
  },
  mounted() {
    this.loadFournisseurs();
    const authData = JSON.parse(localStorage.getItem('user'));
    if (authData) {
      this.currentUser = authData.user || authData;
    }
  },
  methods: {
    async loadFournisseurs() {
      this.isLoading = true;
      try {
        const response = await fetch('/api/fournisseurs');
        if (response.ok) {
          this.fournisseurs = await response.json();
        } else {
          this.errorMessage = 'Erreur lors du chargement des fournisseurs';
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur';
      } finally {
        this.isLoading = false;
      }
    },
    async toggleStatus(fournisseur) {
      if (!this.currentUser) return;
      
      const action = fournisseur.actif ? 'désactiver' : 'activer';
      if (!confirm(`Voulez-vous vraiment ${action} ce fournisseur ?`)) return;

      try {
        const response = await fetch(`/api/fournisseurs/${fournisseur.id}/toggle-status?utilisateurId=${this.currentUser.id}`, {
          method: 'PATCH'
        });
        
        if (response.ok) {
          fournisseur.actif = !fournisseur.actif;
        } else {
          alert('Erreur lors de la modification du statut');
        }
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion');
      }
    },
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      return new Intl.DateTimeFormat('fr-FR').format(new Date(dateString));
    }
  }
};
</script>

<style scoped>
.text-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
