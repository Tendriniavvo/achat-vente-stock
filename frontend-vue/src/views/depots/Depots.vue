<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Dépôts de Stockage</h5>
            <router-link to="/depots/create" class="btn btn-primary">
              <i class="ti ti-plus"></i> Nouveau Dépôt
            </router-link>
          </div>

          <!-- Filtre par statut -->
          <div class="row mb-3">
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

          <div v-else-if="depotsFiltres.length === 0" class="text-center py-5">
            <i class="ti ti-building-warehouse" style="font-size: 3rem; color: #ccc;"></i>
            <p class="text-muted mt-3">
              {{ filtreActif === 'tous' ? 'Aucun dépôt' : `Aucun dépôt ${filtreActif}` }}
            </p>
            <router-link to="/depots/create" class="btn btn-primary btn-sm">
              <i class="ti ti-plus"></i> Créer le premier dépôt
            </router-link>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Nom</th>
                  <th>Adresse</th>
                  <th>Capacité</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="depot in depotsFiltres" :key="depot.id">
                  <td>
                    <strong>{{ depot.nom }}</strong>
                  </td>
                  <td>{{ depot.adresse || 'Non renseignée' }}</td>
                  <td>
                    <span v-if="depot.capacite">{{ depot.capacite }} unités</span>
                    <span v-else class="text-muted">Non définie</span>
                  </td>
                  <td>
                    <span :class="depot.actif ? 'badge bg-success' : 'badge bg-secondary'">
                      {{ depot.actif ? 'Actif' : 'Inactif' }}
                    </span>
                  </td>
                  <td>
                    <router-link 
                      :to="`/depots/${depot.id}`"
                      class="btn btn-sm btn-outline-primary me-1" 
                      title="Voir détails"
                    >
                      <i class="ti ti-eye"></i>
                    </router-link>
                    <router-link 
                      :to="`/depots/${depot.id}/edit`"
                      class="btn btn-sm btn-outline-warning me-1" 
                      title="Modifier"
                    >
                      <i class="ti ti-edit"></i>
                    </router-link>
                    <button 
                      class="btn btn-sm btn-outline-info me-1" 
                      @click="toggleActif(depot.id)"
                      :title="depot.actif ? 'Désactiver' : 'Activer'"
                    >
                      <i :class="depot.actif ? 'ti ti-toggle-right' : 'ti ti-toggle-left'"></i>
                    </button>
                    <button 
                      class="btn btn-sm btn-outline-danger" 
                      @click="confirmDelete(depot)"
                      title="Supprimer"
                    >
                      <i class="ti ti-trash"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de confirmation de suppression -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="deleteModalLabel">Confirmer la suppression</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>Êtes-vous sûr de vouloir supprimer le dépôt <strong>{{ depotASupprimer?.nom }}</strong> ?</p>
            <p class="text-danger">Cette action est irréversible.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
            <button type="button" class="btn btn-danger" @click="deleteDepot">Supprimer</button>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../../layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'Depots',
  components: {
    MainLayout
  },
  data() {
    return {
      depots: [],
      filtreActif: 'tous',
      isLoading: false,
      errorMessage: '',
      successMessage: '',
      depotASupprimer: null
    };
  },
  computed: {
    depotsFiltres() {
      if (this.filtreActif === 'tous') {
        return this.depots;
      } else if (this.filtreActif === 'actif') {
        return this.depots.filter(depot => depot.actif);
      } else {
        return this.depots.filter(depot => !depot.actif);
      }
    }
  },
  mounted() {
    this.loadDepots();
  },
  methods: {
    async loadDepots() {
      this.isLoading = true;
      this.errorMessage = '';
      try {
        const response = await axios.get('http://localhost:8080/api/depots');
        this.depots = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement des dépôts:', error);
        this.errorMessage = 'Erreur lors du chargement des dépôts.';
      } finally {
        this.isLoading = false;
      }
    },
    async toggleActif(id) {
      try {
        await axios.post(`http://localhost:8080/api/depots/${id}/toggle-actif`);
        this.successMessage = 'Statut du dépôt modifié avec succès.';
        await this.loadDepots();
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      } catch (error) {
        console.error('Erreur lors du changement de statut:', error);
        this.errorMessage = 'Erreur lors du changement de statut du dépôt.';
      }
    },
    confirmDelete(depot) {
      this.depotASupprimer = depot;
      const modal = new window.bootstrap.Modal(document.getElementById('deleteModal'));
      modal.show();
    },
    async deleteDepot() {
      if (!this.depotASupprimer) return;
      
      try {
        await axios.delete(`http://localhost:8080/api/depots/${this.depotASupprimer.id}`);
        this.successMessage = `Dépôt "${this.depotASupprimer.nom}" supprimé avec succès.`;
        
        // Fermer le modal
        const modalElement = document.getElementById('deleteModal');
        const modal = window.bootstrap?.Modal?.getInstance(modalElement);
        if (modal) {
          modal.hide();
        } else {
          modalElement.classList.remove('show');
          modalElement.style.display = 'none';
          document.querySelector('.modal-backdrop')?.remove();
        }
        
        this.depotASupprimer = null;
        await this.loadDepots();
        
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      } catch (error) {
        console.error('Erreur lors de la suppression:', error);
        this.errorMessage = 'Erreur lors de la suppression du dépôt.';
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
