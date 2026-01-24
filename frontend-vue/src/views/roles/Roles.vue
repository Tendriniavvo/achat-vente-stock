<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Gestion des Rôles & Habilitations</h5>
            <div class="d-flex gap-2">
              <button 
                v-if="!editMode" 
                @click="enableEditMode" 
                class="btn btn-outline-warning"
              >
                <i class="ti ti-edit"></i> Édition multiple
              </button>
              <button 
                v-else 
                @click="saveAllChanges" 
                class="btn btn-success"
                :disabled="isSaving"
              >
                <span v-if="isSaving" class="spinner-border spinner-border-sm me-1"></span>
                <i v-else class="ti ti-device-floppy"></i> Enregistrer tout
              </button>
              <button 
                v-if="editMode" 
                @click="cancelEditMode" 
                class="btn btn-outline-danger"
              >
                Annuler
              </button>
              <router-link to="/roles/create" class="btn btn-primary">
                <i class="ti ti-plus"></i> Nouveau Rôle
              </router-link>
            </div>
          </div>

          <!-- Filtres -->
          <div class="row mb-3" v-if="!editMode">
            <div class="col-md-4">
              <label class="form-label">Recherche</label>
              <input type="text" class="form-control" v-model="searchQuery" placeholder="Nom du rôle...">
            </div>
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
            <div class="spinner-border text-primary" role="status"></div>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th style="width: 10%">ID</th>
                  <th style="width: 40%">Nom du Rôle</th>
                  <th style="width: 35%">Description / Code</th>
                  <th style="width: 15%">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="role in filteredRoles" :key="role.id">
                  <td>{{ role.id }}</td>
                  
                  <!-- Nom -->
                  <td>
                    <input v-if="editMode" type="text" class="form-control form-control-sm" v-model="role.tempNom">
                    <span v-else class="fw-bold">{{ role.nom }}</span>
                  </td>
                  
                  <!-- Code/Description (on utilise le nom technique souvent) -->
                  <td>
                    <span class="badge bg-light-secondary text-secondary">ROLE_{{ role.nom.toUpperCase() }}</span>
                  </td>

                  <!-- Actions -->
                  <td>
                    <div class="d-flex gap-1">
                      <button v-if="!editMode" @click="deleteRole(role.id)" class="btn btn-sm btn-light-danger text-danger">
                        <i class="ti ti-trash"></i>
                      </button>
                      <span v-if="editMode" class="text-muted small">Mode édition</span>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
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
  name: 'Roles',
  components: {
    MainLayout
  },
  data() {
    return {
      roles: [],
      isLoading: false,
      isSaving: false,
      editMode: false,
      searchQuery: '',
      errorMessage: '',
      successMessage: ''
    }
  },
  computed: {
    filteredRoles() {
      return this.roles.filter(r => {
        const search = this.searchQuery.toLowerCase();
        return r.nom.toLowerCase().includes(search);
      });
    }
  },
  mounted() {
    this.loadRoles();
    if (this.$route.query.mode === 'edit') {
      this.editMode = true;
    }
  },
  watch: {
    '$route.query.mode'(newMode) {
      if (newMode === 'edit') {
        this.editMode = true;
      } else {
        this.editMode = false;
      }
    }
  },
  methods: {
    async loadRoles() {
      this.isLoading = true;
      try {
        const response = await axios.get('/api/roles');
        this.roles = response.data.map(r => ({
          ...r,
          tempNom: r.nom
        }));
      } catch (error) {
        this.errorMessage = "Erreur lors du chargement des rôles.";
        console.error(error);
      } finally {
        this.isLoading = false;
      }
    },
    enableEditMode() {
      this.editMode = true;
    },
    cancelEditMode() {
      this.editMode = false;
      this.roles.forEach(r => {
        r.tempNom = r.nom;
      });
    },
    async saveAllChanges() {
      this.isSaving = true;
      this.errorMessage = '';
      
      const changes = this.roles.filter(r => r.tempNom !== r.nom);

      if (changes.length === 0) {
        this.editMode = false;
        this.isSaving = false;
        return;
      }

      try {
        for (const role of changes) {
          // Note: On suppose qu'il y a un endpoint PUT /api/roles/{id}
          // Si non, il faudra peut-être l'ajouter au backend
          await axios.post('/api/roles', {
            id: role.id,
            nom: role.tempNom
          });
        }
        
        this.successMessage = `${changes.length} rôle(s) mis à jour.`;
        await this.loadRoles();
        this.editMode = false;
      } catch (error) {
        this.errorMessage = "Erreur lors de l'enregistrement des modifications.";
        console.error(error);
      } finally {
        this.isSaving = false;
      }
    },
    async deleteRole(id) {
      if (confirm('Êtes-vous sûr de vouloir supprimer ce rôle ?')) {
        try {
          await axios.delete(`/api/roles/${id}`);
          this.successMessage = "Rôle supprimé.";
          this.loadRoles();
        } catch (error) {
          this.errorMessage = "Erreur lors de la suppression. Le rôle est probablement utilisé.";
        }
      }
    }
  }
}
</script>

<style scoped>
.badge.bg-light-secondary {
  background-color: #f1f3f5;
  color: #495057;
}
.btn-light-danger {
  background-color: #fef2f2;
  border-color: transparent;
}
</style>
