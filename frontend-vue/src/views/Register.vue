<template>
  <div class="page-wrapper" id="main-wrapper" data-layout="vertical" data-navbarbg="skin6" data-sidebartype="full"
    data-sidebar-position="fixed" data-header-position="fixed">
    <div
      class="position-relative overflow-hidden radial-gradient min-vh-100 d-flex align-items-center justify-content-center">
      <div class="d-flex align-items-center justify-content-center w-100">
        <div class="row justify-content-center w-100">
          <div class="col-md-8 col-lg-6 col-xxl-4">
            <div class="card mb-0">
              <div class="card-body">
                <router-link to="/" class="text-nowrap logo-img text-center d-block py-3 w-100">
                  <img src="@/assets/images/logos/dark-logo.svg" width="180" alt="" />
                </router-link>
                <p class="text-center">Votre plateforme de gestion d'achats</p>
                <div v-if="error" class="alert alert-danger" role="alert">
                  {{ error }}
                </div>
                <div v-if="success" class="alert alert-success" role="alert">
                  {{ success }}
                </div>
                <form @submit.prevent="handleRegister">
                  <div class="row">
                    <div class="col-md-6 mb-3">
                      <label for="nom" class="form-label">Nom</label>
                      <input type="text" class="form-control" id="nom" v-model="form.nom" required>
                    </div>
                    <div class="col-md-6 mb-3">
                      <label for="prenom" class="form-label">Prénom</label>
                      <input type="text" class="form-control" id="prenom" v-model="form.prenom" required>
                    </div>
                  </div>
                  <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" v-model="form.email" required>
                  </div>
                  <div class="mb-3">
                    <label for="password" class="form-label">Mot de passe</label>
                    <input type="password" class="form-control" id="password" v-model="form.motDePasse" required>
                  </div>
                  
                  <div class="row">
                    <div class="col-md-6 mb-3">
                      <label for="role" class="form-label">Rôle</label>
                      <select class="form-select" id="role" v-model="selectedRoleId" required>
                        <option value="" disabled selected>Sélectionner un rôle</option>
                        <option v-for="role in roles" :key="role.id" :value="role.id">{{ role.nom }}</option>
                      </select>
                    </div>
                    <div class="col-md-6 mb-3">
                      <label for="departement" class="form-label">Département</label>
                      <select class="form-select" id="departement" v-model="form.departementId" required>
                        <option value="" disabled selected>Sélectionner un département</option>
                        <option v-for="dept in departements" :key="dept.id" :value="dept.id">{{ dept.nom }}</option>
                      </select>
                    </div>
                  </div>

                  <button type="submit" class="btn btn-primary w-100 py-8 fs-4 mb-4 rounded-2" :disabled="isLoading">
                    <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                    S'inscrire
                  </button>
                  <div class="d-flex align-items-center justify-content-center">
                    <p class="fs-4 mb-0 fw-bold">Déjà un compte ?</p>
                    <router-link class="text-primary fw-bold ms-2" to="/login">Se connecter</router-link>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Register',
  data() {
    return {
      form: {
        nom: '',
        prenom: '',
        email: '',
        motDePasse: '',
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
    async handleRegister() {
      this.isLoading = true;
      this.error = '';
      this.success = '';
      
      // Mettre à jour roleIds avec le rôle sélectionné
      if (this.selectedRoleId) {
        this.form.roleIds = [parseInt(this.selectedRoleId)];
      }

      try {
        const response = await axios.post('/api/auth/register', this.form);

        if (response.status === 200 || response.status === 201) {
          this.success = 'Compte créé avec succès ! Redirection vers la page de connexion...';
          setTimeout(() => {
            this.$router.push('/login');
          }, 2000);
        }
      } catch (err) {
        console.error('Register error:', err);
        this.error = err.response?.data?.message || 'Erreur lors de l\'inscription';
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.radial-gradient {
  background: radial-gradient(#f5f7ff 0%, #eef3fe 100%);
}
</style>
