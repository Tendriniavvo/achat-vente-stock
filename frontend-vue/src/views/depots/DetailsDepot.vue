<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Détails du Dépôt</h5>
            <router-link to="/depots" class="btn btn-secondary">
              <i class="ti ti-arrow-left"></i> Retour
            </router-link>
          </div>

          <div v-if="errorMessage" class="alert alert-danger" role="alert">
            {{ errorMessage }}
          </div>

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else-if="depot">
            <!-- Informations principales -->
            <div class="row mb-4">
              <div class="col-md-6">
                <div class="card bg-light">
                  <div class="card-body">
                    <h6 class="card-subtitle mb-3 text-muted">Informations générales</h6>
                    <table class="table table-borderless mb-0">
                      <tbody>
                        <tr>
                          <td class="fw-semibold" style="width: 40%;">Nom :</td>
                          <td>{{ depot.nom }}</td>
                        </tr>
                        <tr>
                          <td class="fw-semibold">Statut :</td>
                          <td>
                            <span :class="depot.actif ? 'badge bg-success' : 'badge bg-secondary'">
                              {{ depot.actif ? 'Actif' : 'Inactif' }}
                            </span>
                          </td>
                        </tr>
                        <tr v-if="depot.capacite">
                          <td class="fw-semibold">Capacité :</td>
                          <td>{{ depot.capacite }} unités</td>
                        </tr>
                        <tr v-if="depot.dateCreation">
                          <td class="fw-semibold">Date de création :</td>
                          <td>{{ formatDate(depot.dateCreation) }}</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="card bg-light">
                  <div class="card-body">
                    <h6 class="card-subtitle mb-3 text-muted">Localisation</h6>
                    <table class="table table-borderless mb-0">
                      <tbody>
                        <tr>
                          <td class="fw-semibold" style="width: 40%;">Adresse :</td>
                          <td>{{ depot.adresse || 'Non renseignée' }}</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>

            <!-- Actions -->
            <div class="d-flex justify-content-end gap-2 mt-4">
              <router-link 
                :to="`/depots/${depot.id}/edit`" 
                class="btn btn-warning"
              >
                <i class="ti ti-edit"></i> Modifier
              </router-link>
              <button 
                class="btn btn-danger" 
                @click="confirmDelete"
              >
                <i class="ti ti-trash"></i> Supprimer
              </button>
            </div>
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
            <p>Êtes-vous sûr de vouloir supprimer le dépôt <strong>{{ depot?.nom }}</strong> ?</p>
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
  name: 'DetailsDepot',
  components: {
    MainLayout
  },
  data() {
    return {
      depot: null,
      isLoading: false,
      errorMessage: ''
    };
  },
  mounted() {
    this.loadDepot();
  },
  methods: {
    async loadDepot() {
      this.isLoading = true;
      this.errorMessage = '';
      
      try {
        const response = await axios.get(`http://localhost:8080/api/depots/${this.$route.params.id}`);
        this.depot = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement du dépôt:', error);
        this.errorMessage = 'Erreur lors du chargement du dépôt.';
      } finally {
        this.isLoading = false;
      }
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date);
    },
    confirmDelete() {
      const modal = new window.bootstrap.Modal(document.getElementById('deleteModal'));
      modal.show();
    },
    async deleteDepot() {
      try {
        await axios.delete(`http://localhost:8080/api/depots/${this.depot.id}`);
        
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
        
        // Rediriger vers la liste
        this.$router.push('/depots');
      } catch (error) {
        console.error('Erreur lors de la suppression:', error);
        this.errorMessage = 'Erreur lors de la suppression du dépôt.';
      }
    }
  }
};
</script>

<style scoped>
.gap-2 {
  gap: 0.5rem;
}

.card.bg-light {
  background-color: #f8f9fa !important;
}
</style>
