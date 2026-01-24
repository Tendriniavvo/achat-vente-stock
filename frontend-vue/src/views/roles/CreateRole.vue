<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Créer un nouveau rôle</h5>
            <router-link to="/roles" class="btn btn-secondary">
              <i class="ti ti-arrow-left"></i> Retour à la liste
            </router-link>
          </div>

          <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ error }}
            <button type="button" class="btn-close" @click="error = ''"></button>
          </div>
          <div v-if="success" class="alert alert-success alert-dismissible fade show" role="alert">
            {{ success }}
            <button type="button" class="btn-close" @click="success = ''"></button>
          </div>

          <form @submit.prevent="handleCreate">
            <div class="mb-3">
              <label for="nom" class="form-label">Nom du Rôle <span class="text-danger">*</span></label>
              <input 
                type="text" 
                class="form-control" 
                id="nom" 
                v-model="form.nom" 
                required 
                placeholder="Ex: COMPTABLE, VENDEUR, etc."
                @input="form.nom = form.nom.toUpperCase()"
              >
              <div class="form-text">Le nom sera converti en majuscules.</div>
            </div>

            <div class="alert alert-info" role="alert">
              <i class="ti ti-info-circle me-2"></i>
              Par défaut, les nouveaux rôles n'ont aucune habilitation spécifique. 
              Les droits par module pourront être configurés dans une étape ultérieure.
            </div>

            <div class="d-flex justify-content-end mt-4">
              <button type="button" class="btn btn-light me-2" @click="$router.push('/roles')">Annuler</button>
              <button type="submit" class="btn btn-primary" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="ti ti-shield-check me-1"></i>
                Créer le rôle
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
  name: 'CreateRole',
  components: {
    MainLayout
  },
  data() {
    return {
      form: {
        nom: ''
      },
      error: '',
      success: '',
      isLoading: false
    }
  },
  methods: {
    async handleCreate() {
      this.isLoading = true;
      this.error = '';
      this.success = '';
      
      try {
        const response = await axios.post('/api/roles', this.form);

        if (response.status === 200 || response.status === 201) {
          this.success = 'Rôle créé avec succès !';
          setTimeout(() => {
            this.$router.push('/roles');
          }, 1500);
        }
      } catch (err) {
        console.error('Create role error:', err);
        this.error = err.response?.data?.message || 'Erreur lors de la création du rôle';
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.text-danger {
  color: #fa896b !important;
}
</style>
