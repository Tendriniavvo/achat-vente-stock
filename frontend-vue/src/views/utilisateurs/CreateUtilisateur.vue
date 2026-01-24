<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Créer un nouvel utilisateur</h5>
            <router-link to="/utilisateurs" class="btn btn-secondary">
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
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="nom" class="form-label">Nom <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="nom" v-model="form.nom" required placeholder="Ex: Rakoto">
              </div>
              <div class="col-md-6 mb-3">
                <label for="prenom" class="form-label">Prénom <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="prenom" v-model="form.prenom" required placeholder="Ex: Jean">
              </div>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                <input type="email" class="form-control" id="email" v-model="form.email" required placeholder="exemple@gmail.com">
              </div>
              <div class="col-md-6 mb-3">
                <label for="password" class="form-label">Mot de passe <span class="text-danger">*</span></label>
                <input type="password" class="form-control" id="password" v-model="form.motDePasse" required placeholder="Mot de passe par défaut">
              </div>
            </div>
            
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="role" class="form-label">Rôle <span class="text-danger">*</span></label>
                <select class="form-select" id="role" v-model="selectedRoleId" required>
                  <option value="" disabled>Sélectionner un rôle</option>
                  <option v-for="role in roles" :key="role.id" :value="role.id">{{ role.nom }}</option>
                </select>
              </div>
              <div class="col-md-6 mb-3">
                <label for="departement" class="form-label">Département <span class="text-danger">*</span></label>
                <select class="form-select" id="departement" v-model="form.departementId" required>
                  <option value="" disabled>Sélectionner un département</option>
                  <option v-for="dept in departements" :key="dept.id" :value="dept.id">{{ dept.nom }}</option>
                </select>
              </div>
            </div>

            <div class="d-flex justify-content-end mt-4">
              <button type="button" class="btn btn-light me-2" @click="$router.push('/utilisateurs')">Annuler</button>
              <button type="submit" class="btn btn-primary" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="ti ti-user-plus me-1"></i>
                Créer l'utilisateur
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
  name: 'CreateUtilisateur',
  components: {
    MainLayout
  },
  data() {
    return {
      form: {
        nom: '',
        prenom: '',
        email: '',
        motDePasse: 'test', // Valeur par défaut
        roleIds: [],
        departementId: ''
      },
      selectedRoleId: '',
      roles: [],
      departements: [],
      error: '',
      success: '',
      isLoading: false
    }
  },
  mounted() {
    this.loadDepartements();
    this.loadRoles();
  },
  methods: {
    async loadDepartements() {
      try {
        const response = await axios.get('/api/departements');
        this.departements = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement des départements:', error);
      }
    },
    async loadRoles() {
      try {
        const response = await axios.get('/api/roles');
        this.roles = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement des rôles:', error);
      }
    },
    async handleCreate() {
      this.isLoading = true;
      this.error = '';
      this.success = '';
      
      if (this.selectedRoleId) {
        this.form.roleIds = [parseInt(this.selectedRoleId)];
      }

      try {
        const response = await axios.post('/api/auth/register', this.form);

        if (response.status === 200 || response.status === 201) {
          this.success = 'Utilisateur créé avec succès !';
          setTimeout(() => {
            this.$router.push('/utilisateurs');
          }, 1500);
        }
      } catch (err) {
        console.error('Create user error:', err);
        this.error = err.response?.data?.message || 'Erreur lors de la création de l\'utilisateur';
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
