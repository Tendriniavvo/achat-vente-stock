<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Emplacements de Stockage</h5>
            <router-link to="/emplacements/create" class="btn btn-primary">
              <i class="ti ti-plus"></i> Nouvel Emplacement
            </router-link>
          </div>

          <!-- Filtre par dépôt -->
          <div class="row mb-3">
            <div class="col-md-4">
              <label for="filtreDepot" class="form-label">Filtrer par dépôt :</label>
              <select 
                id="filtreDepot" 
                class="form-select" 
                v-model="filtreDepotId"
                @change="loadEmplacements"
              >
                <option value="">Tous les dépôts</option>
                <option v-for="depot in depots" :key="depot.id" :value="depot.id">
                  {{ depot.nom }}
                </option>
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

          <div v-else-if="emplacements.length === 0" class="text-center py-5">
            <i class="ti ti-map-pin" style="font-size: 3rem; color: #ccc;"></i>
            <p class="text-muted mt-3">
              {{ filtreDepotId ? 'Aucun emplacement dans ce dépôt' : 'Aucun emplacement' }}
            </p>
            <router-link to="/emplacements/create" class="btn btn-primary btn-sm">
              <i class="ti ti-plus"></i> Créer le premier emplacement
            </router-link>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Code</th>
                  <th>Dépôt</th>
                  <th>Description</th>
                  <th>Capacité</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="emplacement in emplacements" :key="emplacement.id">
                  <td>
                    <strong>{{ emplacement.code }}</strong>
                  </td>
                  <td>
                    <span class="badge bg-info">{{ emplacement.depotNom }}</span>
                  </td>
                  <td>{{ emplacement.description || 'Non renseignée' }}</td>
                  <td>
                    <span v-if="emplacement.capacite">{{ emplacement.capacite }} unités</span>
                    <span v-else class="text-muted">Non définie</span>
                  </td>
                  <td>
                    <router-link 
                      :to="`/emplacements/${emplacement.id}`"
                      class="btn btn-sm btn-outline-primary me-1" 
                      title="Voir détails"
                    >
                      <i class="ti ti-eye"></i>
                    </router-link>
                    <router-link 
                      :to="`/emplacements/${emplacement.id}/edit`"
                      class="btn btn-sm btn-outline-warning me-1" 
                      title="Modifier"
                    >
                      <i class="ti ti-edit"></i>
                    </router-link>
                    <button 
                      class="btn btn-sm btn-outline-danger" 
                      @click="confirmDelete(emplacement)"
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
            <p>Êtes-vous sûr de vouloir supprimer l'emplacement <strong>{{ emplacementASupprimer?.code }}</strong> ?</p>
            <p class="text-danger">Cette action est irréversible.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
            <button type="button" class="btn btn-danger" @click="deleteEmplacement">Supprimer</button>
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
  name: 'Emplacements',
  components: {
    MainLayout
  },
  data() {
    return {
      emplacements: [],
      depots: [],
      filtreDepotId: '',
      isLoading: false,
      errorMessage: '',
      successMessage: '',
      emplacementASupprimer: null
    };
  },
  mounted() {
    this.loadDepots();
    this.loadEmplacements();
  },
  methods: {
    async loadDepots() {
      try {
        const response = await axios.get('http://localhost:8080/api/depots', {
          params: { actif: true }
        });
        this.depots = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement des dépôts:', error);
      }
    },
    async loadEmplacements() {
      this.isLoading = true;
      this.errorMessage = '';
      try {
        const params = this.filtreDepotId ? { depotId: this.filtreDepotId } : {};
        const response = await axios.get('http://localhost:8080/api/emplacements', { params });
        this.emplacements = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement des emplacements:', error);
        this.errorMessage = 'Erreur lors du chargement des emplacements.';
      } finally {
        this.isLoading = false;
      }
    },
    confirmDelete(emplacement) {
      this.emplacementASupprimer = emplacement;
      const modal = new window.bootstrap.Modal(document.getElementById('deleteModal'));
      modal.show();
    },
    async deleteEmplacement() {
      if (!this.emplacementASupprimer) return;
      
      try {
        await axios.delete(`http://localhost:8080/api/emplacements/${this.emplacementASupprimer.id}`);
        this.successMessage = `Emplacement "${this.emplacementASupprimer.code}" supprimé avec succès.`;
        
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
        
        this.emplacementASupprimer = null;
        await this.loadEmplacements();
        
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      } catch (error) {
        console.error('Erreur lors de la suppression:', error);
        this.errorMessage = 'Erreur lors de la suppression de l\'emplacement.';
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
