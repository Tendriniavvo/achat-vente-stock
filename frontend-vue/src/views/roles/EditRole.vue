<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Modifier le rôle</h5>
            <router-link to="/roles" class="btn btn-secondary">
              <i class="ti ti-arrow-left"></i> Retour à la liste
            </router-link>
          </div>

          <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else>
            <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
              {{ error }}
              <button type="button" class="btn-close" @click="error = ''"></button>
            </div>
            <div v-if="success" class="alert alert-success alert-dismissible fade show" role="alert">
              {{ success }}
              <button type="button" class="btn-close" @click="success = ''"></button>
            </div>

            <form @submit.prevent="handleUpdate">
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
                La modification du nom d'un rôle impacte immédiatement les habilitations associées.
              </div>

              <div class="d-flex justify-content-end mt-4">
                <button type="button" class="btn btn-light me-2" @click="$router.push('/roles')">Annuler</button>
                <button type="submit" class="btn btn-primary" :disabled="isSaving">
                  <span v-if="isSaving" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="ti ti-device-floppy me-1"></i>
                  Enregistrer les modifications
                </button>
              </div>
            </form>
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
  name: 'EditRole',
  components: {
    MainLayout
  },
  data() {
    return {
      form: {
        id: null,
        nom: ''
      },
      error: '',
      success: '',
      loading: true,
      isSaving: false
    }
  },
  async mounted() {
    await this.fetchRole();
  },
  methods: {
    async fetchRole() {
      this.loading = true;
      try {
        const id = this.$route.params.id;
        const response = await axios.get(`/api/roles/${id}`);
        this.form = response.data;
      } catch (err) {
        console.error('Fetch role error:', err);
        this.error = 'Erreur lors de la récupération des données du rôle';
      } finally {
        this.loading = false;
      }
    },
    async handleUpdate() {
      this.isSaving = true;
      this.error = '';
      this.success = '';
      
      try {
        const response = await axios.post('/api/roles', this.form);

        if (response.status === 200 || response.status === 201) {
          this.success = 'Rôle mis à jour avec succès !';
          setTimeout(() => {
            this.$router.push('/roles');
          }, 1500);
        }
      } catch (err) {
        console.error('Update role error:', err);
        this.error = err.response?.data?.message || 'Erreur lors de la mise à jour du rôle';
      } finally {
        this.isSaving = false;
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
