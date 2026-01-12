<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Nouveau Dépôt</h5>
            <router-link to="/depots" class="btn btn-secondary">
              <i class="ti ti-arrow-left"></i> Retour
            </router-link>
          </div>

          <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ errorMessage }}
            <button type="button" class="btn-close" @click="errorMessage = ''"></button>
          </div>

          <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
            {{ successMessage }}
            <button type="button" class="btn-close" @click="successMessage = ''"></button>
          </div>

          <form @submit.prevent="handleSubmit">
            <!-- Informations générales -->
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="nom" class="form-label">Nom du dépôt <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="nom" 
                  v-model="form.nom" 
                  placeholder="Ex: Entrepôt Central"
                  required
                >
              </div>
              <div class="col-md-6 mb-3">
                <label for="capacite" class="form-label">Capacité (unités)</label>
                <input 
                  type="number" 
                  class="form-control" 
                  id="capacite" 
                  v-model.number="form.capacite" 
                  placeholder="Ex: 10000"
                  min="0"
                >
                <small class="form-text text-muted">Capacité de stockage en nombre d'unités</small>
              </div>
            </div>

            <div class="row">
              <div class="col-md-12 mb-3">
                <label for="adresse" class="form-label">Adresse</label>
                <textarea 
                  class="form-control" 
                  id="adresse" 
                  v-model="form.adresse" 
                  rows="3"
                  placeholder="Adresse complète du dépôt"
                ></textarea>
              </div>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <div class="form-check">
                  <input 
                    class="form-check-input" 
                    type="checkbox" 
                    id="actif" 
                    v-model="form.actif"
                  >
                  <label class="form-check-label" for="actif">
                    Dépôt actif
                  </label>
                  <small class="form-text text-muted d-block">
                    Un dépôt actif peut recevoir et expédier des marchandises
                  </small>
                </div>
              </div>
            </div>

            <!-- Boutons d'action -->
            <div class="d-flex justify-content-end gap-2 mt-4">
              <router-link to="/depots" class="btn btn-secondary">
                Annuler
              </router-link>
              <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
                <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                <i v-else class="ti ti-device-floppy me-1"></i>
                {{ isSubmitting ? 'Enregistrement...' : 'Enregistrer' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../../layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'CreateDepot',
  components: {
    MainLayout
  },
  data() {
    return {
      form: {
        nom: '',
        adresse: '',
        capacite: null,
        actif: true
      },
      isSubmitting: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  methods: {
    async handleSubmit() {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.successMessage = '';

      try {
        const response = await axios.post('http://localhost:8080/api/depots', this.form);
        this.successMessage = 'Dépôt créé avec succès !';
        
        // Réinitialiser le formulaire
        this.form = {
          nom: '',
          adresse: '',
          capacite: null,
          actif: true
        };

        // Rediriger après 1.5 secondes
        setTimeout(() => {
          this.$router.push('/depots');
        }, 1500);
      } catch (error) {
        console.error('Erreur lors de la création du dépôt:', error);
        if (error.response) {
          this.errorMessage = `Erreur: ${error.response.data.message || 'Impossible de créer le dépôt'}`;
        } else {
          this.errorMessage = 'Erreur lors de la création du dépôt. Veuillez réessayer.';
        }
      } finally {
        this.isSubmitting = false;
      }
    }
  }
};
</script>

<style scoped>
.form-label {
  font-weight: 500;
}

.text-danger {
  color: #dc3545;
}

.gap-2 {
  gap: 0.5rem;
}
</style>
