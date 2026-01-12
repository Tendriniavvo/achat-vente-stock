<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Nouvel Emplacement</h5>
            <router-link to="/emplacements" class="btn btn-secondary">
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
                <label for="depotId" class="form-label">Dépôt <span class="text-danger">*</span></label>
                <select 
                  class="form-select" 
                  id="depotId" 
                  v-model="form.depotId" 
                  required
                >
                  <option value="">Sélectionnez un dépôt</option>
                  <option v-for="depot in depots" :key="depot.id" :value="depot.id">
                    {{ depot.nom }}
                  </option>
                </select>
              </div>
              <div class="col-md-6 mb-3">
                <label for="code" class="form-label">Code <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="code" 
                  v-model="form.code" 
                  placeholder="Ex: A-01, B-12, Zone-C"
                  required
                >
                <small class="form-text text-muted">Code unique pour identifier l'emplacement</small>
              </div>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="capacite" class="form-label">Capacité (unités)</label>
                <input 
                  type="number" 
                  class="form-control" 
                  id="capacite" 
                  v-model.number="form.capacite" 
                  placeholder="Ex: 100"
                  min="0"
                >
                <small class="form-text text-muted">Capacité de stockage en nombre d'unités</small>
              </div>
            </div>

            <div class="row">
              <div class="col-md-12 mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea 
                  class="form-control" 
                  id="description" 
                  v-model="form.description" 
                  rows="3"
                  placeholder="Description de l'emplacement (ex: allée, niveau, caractéristiques...)"
                ></textarea>
              </div>
            </div>

            <!-- Boutons d'action -->
            <div class="d-flex justify-content-end gap-2 mt-4">
              <router-link to="/emplacements" class="btn btn-secondary">
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
  name: 'CreateEmplacement',
  components: {
    MainLayout
  },
  data() {
    return {
      form: {
        depotId: '',
        code: '',
        description: '',
        capacite: null
      },
      depots: [],
      isSubmitting: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  mounted() {
    this.loadDepots();
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
        this.errorMessage = 'Erreur lors du chargement des dépôts.';
      }
    },
    async handleSubmit() {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.successMessage = '';

      try {
        const response = await axios.post('http://localhost:8080/api/emplacements', this.form);
        this.successMessage = 'Emplacement créé avec succès !';
        
        // Réinitialiser le formulaire
        this.form = {
          depotId: '',
          code: '',
          description: '',
          capacite: null
        };

        // Rediriger après 1.5 secondes
        setTimeout(() => {
          this.$router.push('/emplacements');
        }, 1500);
      } catch (error) {
        console.error('Erreur lors de la création de l\'emplacement:', error);
        if (error.response) {
          this.errorMessage = `Erreur: ${error.response.data.message || 'Impossible de créer l\'emplacement'}`;
        } else {
          this.errorMessage = 'Erreur lors de la création de l\'emplacement. Veuillez réessayer.';
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
