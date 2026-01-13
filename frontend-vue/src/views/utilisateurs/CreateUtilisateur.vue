<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Nouvel Utilisateur</h5>
            <router-link to="/utilisateurs" class="btn btn-secondary">
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
            <!-- Informations personnelles -->
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="nom" class="form-label">Nom <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="nom" 
                  v-model="form.nom" 
                  required
                >
              </div>
              <div class="col-md-6 mb-3">
                <label for="prenom" class="form-label">Prénom <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="prenom" 
                  v-model="form.prenom" 
                  required
                >
              </div>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                <input 
                  type="email" 
                  class="form-control" 
                  id="email" 
                  v-model="form.email" 
                  required
                >
              </div>
              <div class="col-md-6 mb-3">
                <label for="departement" class="form-label">Département</label>
                <select 
                  class="form-select" 
                  id="departement" 
                  v-model="form.departementId"
                >
                  <option value="">Aucun département</option>
                  <option v-for="dept in departements" :key="dept.id" :value="dept.id">
                    {{ dept.nom }} ({{ dept.code }})
                  </option>
                </select>
              </div>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="motDePasse" class="form-label">Mot de passe <span class="text-danger">*</span></label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="motDePasse" 
                  v-model="form.motDePasse" 
                  required
                  minlength="6"
                >
                <small class="form-text text-muted">Minimum 6 caractères</small>
              </div>
              <div class="col-md-6 mb-3">
                <label for="role" class="form-label">Rôle <span class="text-danger">*</span></label>
                <select 
                  class="form-select" 
                  id="role" 
                  v-model="form.roleId" 
                  required
                >
                  <option value="">Sélectionnez un rôle</option>
                  <option v-for="role in roles" :key="role.id" :value="role.id">
                    {{ role.nom }}
                  </option>
                </select>
              </div>
            </div>

            <!-- Boutons d'action -->
            <div class="d-flex justify-content-end gap-2 mt-4">
              <router-link to="/utilisateurs" class="btn btn-secondary">
                Annuler
              </router-link>
              <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
                <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                <i v-else class="ti ti-device-floppy me-1"></i>
                {{ isSubmitting ? 'Enregistrement...' : 'Créer l\'utilisateur' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../layouts/MainLayout.vue';
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
        motDePasse: '',
        departementId: '',
        roleId: ''
      },
      departements: [],
      roles: [],
      isSubmitting: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  mounted() {
    this.loadDepartements();
    this.loadRoles();
  },
  methods: {
    async loadDepartements() {
      try {
        const response = await axios.get('http://localhost:8080/api/departements?actif=true');
        this.departements = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement des départements:', error);
      }
    },
    async loadRoles() {
      try {
        const response = await axios.get('http://localhost:8080/api/roles');
        this.roles = response.data;
      } catch (error) {
        console.error('Erreur lors du chargement des rôles:', error);
      }
    },
    async handleSubmit() {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.successMessage = '';

      try {
        const payload = {
          nom: this.form.nom,
          prenom: this.form.prenom,
          email: this.form.email,
          motDePasse: this.form.motDePasse,
          roleIds: [this.form.roleId],
          departementId: this.form.departementId || null
        };

        const response = await axios.post('http://localhost:8080/api/auth/register', payload);
        
        if (response.data.success) {
          this.successMessage = 'Utilisateur créé avec succès !';
          
          // Rediriger après 1.5 secondes
          setTimeout(() => {
            this.$router.push('/utilisateurs');
          }, 1500);
        } else {
          this.errorMessage = response.data.message || 'Erreur lors de la création';
        }
      } catch (error) {
        console.error('Erreur lors de la création de l\'utilisateur:', error);
        if (error.response && error.response.data) {
          this.errorMessage = error.response.data.message || 'Erreur lors de la création de l\'utilisateur';
        } else {
          this.errorMessage = 'Erreur lors de la création de l\'utilisateur. Veuillez réessayer.';
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
