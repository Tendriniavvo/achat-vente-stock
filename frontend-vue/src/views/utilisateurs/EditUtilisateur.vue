<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Modifier l'Utilisateur</h5>
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

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <form v-else @submit.prevent="handleSubmit">
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
                <label for="role" class="form-label">Rôle <span class="text-danger">*</span></label>
                <select class="form-select" id="role" v-model="selectedRoleId" required>
                  <option value="" disabled>Sélectionner un rôle</option>
                  <option v-for="role in roles" :key="role.id" :value="role.id">{{ role.nom }}</option>
                </select>
              </div>
              <div class="col-md-6 mb-3">
                <div class="form-check mt-4">
                  <input 
                    class="form-check-input" 
                    type="checkbox" 
                    id="actif" 
                    v-model="form.actif"
                  >
                  <label class="form-check-label" for="actif">
                    Utilisateur actif
                  </label>
                </div>
              </div>
            </div>

            <!-- Note sur le mot de passe -->
            <div class="alert alert-info" role="alert">
              <i class="ti ti-info-circle me-2"></i>
              Pour modifier le mot de passe, veuillez contacter un administrateur.
            </div>

            <!-- Boutons d'action -->
            <div class="d-flex justify-content-end gap-2 mt-4">
              <router-link to="/utilisateurs" class="btn btn-secondary">
                Annuler
              </router-link>
              <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
                <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                <i v-else class="ti ti-device-floppy me-1"></i>
                {{ isSubmitting ? 'Enregistrement...' : 'Enregistrer les modifications' }}
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
  name: 'EditUtilisateur',
  components: {
    MainLayout
  },
  data() {
    return {
      form: {
        nom: '',
        prenom: '',
        email: '',
        departementId: '',
        actif: true,
        roleIds: []
      },
      selectedRoleId: '',
      roles: [],
      departements: [],
      isLoading: false,
      isSubmitting: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  mounted() {
    this.loadDepartements();
    this.loadRoles();
    this.loadUtilisateur();
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
    async loadUtilisateur() {
      this.isLoading = true;
      this.errorMessage = '';
      
      try {
        const response = await axios.get(`/api/utilisateurs/${this.$route.params.id}`);
        const utilisateur = response.data;
        
        if (utilisateur) {
          this.form = {
            nom: utilisateur.nom,
            prenom: utilisateur.prenom,
            email: utilisateur.email,
            departementId: utilisateur.departement ? utilisateur.departement.id : '',
            actif: utilisateur.actif !== undefined ? utilisateur.actif : true,
            roleIds: utilisateur.roles ? utilisateur.roles.map(r => r.id) : []
          };
          if (this.form.roleIds.length > 0) {
            this.selectedRoleId = this.form.roleIds[0];
          }
        } else {
          this.errorMessage = 'Utilisateur introuvable.';
        }
      } catch (error) {
        console.error('Erreur lors du chargement de l\'utilisateur:', error);
        this.errorMessage = 'Erreur lors du chargement de l\'utilisateur.';
      } finally {
        this.isLoading = false;
      }
    },
    async handleSubmit() {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.successMessage = '';

      if (this.selectedRoleId) {
        this.form.roleIds = [parseInt(this.selectedRoleId)];
      }

      try {
        await axios.put(`/api/utilisateurs/${this.$route.params.id}`, this.form);
        this.successMessage = 'Utilisateur mis à jour avec succès !';
        
        setTimeout(() => {
          this.$router.push('/utilisateurs');
        }, 2000);
      } catch (error) {
        console.error('Erreur lors de la modification de l\'utilisateur:', error);
        this.errorMessage = error.response?.data?.message || 'Erreur lors de la modification de l\'utilisateur.';
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
