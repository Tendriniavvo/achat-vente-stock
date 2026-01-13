<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Détails de l'Utilisateur</h5>
            <router-link to="/utilisateurs" class="btn btn-secondary">
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

          <div v-else-if="utilisateur">
            <!-- Informations principales -->
            <div class="row mb-4">
              <div class="col-md-6">
                <div class="card bg-light">
                  <div class="card-body">
                    <h6 class="card-subtitle mb-3 text-muted">Informations personnelles</h6>
                    <table class="table table-borderless mb-0">
                      <tbody>
                        <tr>
                          <td class="fw-semibold" style="width: 40%;">Nom complet :</td>
                          <td>{{ utilisateur.prenom }} {{ utilisateur.nom }}</td>
                        </tr>
                        <tr>
                          <td class="fw-semibold">Email :</td>
                          <td>{{ utilisateur.email }}</td>
                        </tr>
                        <tr>
                          <td class="fw-semibold">Statut :</td>
                          <td>
                            <span :class="utilisateur.actif ? 'badge bg-success' : 'badge bg-secondary'">
                              {{ utilisateur.actif ? 'Actif' : 'Inactif' }}
                            </span>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="card bg-light">
                  <div class="card-body">
                    <h6 class="card-subtitle mb-3 text-muted">Département</h6>
                    <table class="table table-borderless mb-0">
                      <tbody>
                        <tr>
                          <td class="fw-semibold" style="width: 40%;">Département :</td>
                          <td>
                            <span v-if="utilisateur.departementNom" class="badge bg-info">
                              {{ utilisateur.departementNom }}
                            </span>
                            <span v-else class="text-muted">Non assigné</span>
                          </td>
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
                :to="`/utilisateurs/${utilisateur.id}/edit`" 
                class="btn btn-warning"
              >
                <i class="ti ti-edit"></i> Modifier
              </router-link>
            </div>
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
  name: 'DetailsUtilisateur',
  components: {
    MainLayout
  },
  data() {
    return {
      utilisateur: null,
      isLoading: false,
      errorMessage: ''
    };
  },
  mounted() {
    this.loadUtilisateur();
  },
  methods: {
    async loadUtilisateur() {
      this.isLoading = true;
      this.errorMessage = '';
      
      try {
        const response = await axios.get(`http://localhost:8080/api/utilisateurs`);
        const utilisateurs = response.data;
        this.utilisateur = utilisateurs.find(u => u.id === parseInt(this.$route.params.id));
        
        if (!this.utilisateur) {
          this.errorMessage = 'Utilisateur introuvable.';
        }
      } catch (error) {
        console.error('Erreur lors du chargement de l\'utilisateur:', error);
        this.errorMessage = 'Erreur lors du chargement de l\'utilisateur.';
      } finally {
        this.isLoading = false;
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
