<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Détails de l'Emplacement</h5>
            <router-link to="/emplacements" class="btn btn-secondary">
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

          <div v-else-if="emplacement">
            <!-- Informations principales -->
            <div class="row mb-4">
              <div class="col-md-6">
                <div class="card bg-light">
                  <div class="card-body">
                    <h6 class="card-subtitle mb-3 text-muted">Informations générales</h6>
                    <table class="table table-borderless mb-0">
                      <tbody>
                        <tr>
                          <td class="fw-semibold" style="width: 40%;">Code :</td>
                          <td>{{ emplacement.code }}</td>
                        </tr>
                        <tr>
                          <td class="fw-semibold">Dépôt :</td>
                          <td>
                            <router-link :to="`/depots/${emplacement.depotId}`" class="badge bg-info text-decoration-none">
                              {{ emplacement.depotNom }}
                            </router-link>
                          </td>
                        </tr>
                        <tr v-if="emplacement.capacite">
                          <td class="fw-semibold">Capacité :</td>
                          <td>{{ emplacement.capacite }} unités</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="card bg-light">
                  <div class="card-body">
                    <h6 class="card-subtitle mb-3 text-muted">Description</h6>
                    <p class="mb-0">{{ emplacement.description || 'Aucune description' }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Actions -->
            <div class="d-flex justify-content-end gap-2 mt-4">
              <router-link 
                :to="`/emplacements/${emplacement.id}/edit`" 
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
            <p>Êtes-vous sûr de vouloir supprimer l'emplacement <strong>{{ emplacement?.code }}</strong> ?</p>
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
  name: 'DetailsEmplacement',
  components: {
    MainLayout
  },
  data() {
    return {
      emplacement: null,
      isLoading: false,
      errorMessage: ''
    };
  },
  mounted() {
    this.loadEmplacement();
  },
  methods: {
    async loadEmplacement() {
      this.isLoading = true;
      this.errorMessage = '';
      
      try {
        const response = await axios.get(`http://localhost:8080/api/emplacements/${this.$route.params.id}`);
        this.emplacement = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement de l\'emplacement:', error);
        this.errorMessage = 'Erreur lors du chargement de l\'emplacement.';
      } finally {
        this.isLoading = false;
      }
    },
    confirmDelete() {
      const modal = new window.bootstrap.Modal(document.getElementById('deleteModal'));
      modal.show();
    },
    async deleteEmplacement() {
      try {
        await axios.delete(`http://localhost:8080/api/emplacements/${this.emplacement.id}`);
        
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
        this.$router.push('/emplacements');
      } catch (error) {
        console.error('Erreur lors de la suppression:', error);
        this.errorMessage = 'Erreur lors de la suppression de l\'emplacement.';
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
