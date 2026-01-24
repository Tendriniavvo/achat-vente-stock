<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Gestion des Utilisateurs</h5>
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
              <router-link to="/utilisateurs/create" class="btn btn-primary">
                <i class="ti ti-plus"></i> Nouveau
              </router-link>
            </div>
          </div>

          <!-- Filtres -->
          <div class="row mb-3" v-if="!editMode">
            <div class="col-md-3">
              <label class="form-label">Département</label>
              <select class="form-select" v-model="filtreDepartementId">
                <option value="tous">Tous les départements</option>
                <option v-for="dept in departements" :key="dept.id" :value="dept.id">{{ dept.nom }}</option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">Recherche</label>
              <input type="text" class="form-control" v-model="searchQuery" placeholder="Nom ou email...">
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
                  <th style="width: 20%">Prénom</th>
                  <th style="width: 20%">Nom</th>
                  <th style="width: 25%">Email</th>
                  <th style="width: 20%">Département</th>
                  <th style="width: 15%">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in filteredUsers" :key="user.id">
                  <!-- Prénom -->
                  <td>
                    <input v-if="editMode" type="text" class="form-control form-control-sm" v-model="user.tempPrenom">
                    <span v-else>{{ user.prenom }}</span>
                  </td>
                  
                  <!-- Nom -->
                  <td>
                    <input v-if="editMode" type="text" class="form-control form-control-sm" v-model="user.tempNom">
                    <span v-else>{{ user.nom }}</span>
                  </td>

                  <!-- Email -->
                  <td>
                    <input v-if="editMode" type="email" class="form-control form-control-sm" v-model="user.tempEmail">
                    <span v-else>{{ user.email }}</span>
                  </td>

                  <!-- Département -->
                  <td>
                    <select v-if="editMode" class="form-select form-select-sm" v-model="user.tempDepartementId">
                      <option v-for="dept in departements" :key="dept.id" :value="dept.id">{{ dept.nom }}</option>
                    </select>
                    <span v-else class="badge bg-light-primary text-primary">{{ user.departement?.nom || 'N/A' }}</span>
                  </td>

                  <!-- Actions -->
                  <td>
                    <div class="d-flex gap-1">
                      <router-link v-if="!editMode" :to="`/utilisateurs/${user.id}/edit`" class="btn btn-sm btn-light-warning text-warning">
                        <i class="ti ti-edit"></i>
                      </router-link>
                      <button v-if="!editMode" @click="deleteUser(user.id)" class="btn btn-sm btn-light-danger text-danger">
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
  name: 'Utilisateurs',
  components: {
    MainLayout
  },
  data() {
    return {
      utilisateurs: [],
      departements: [],
      isLoading: false,
      isSaving: false,
      editMode: false,
      filtreDepartementId: 'tous',
      searchQuery: '',
      errorMessage: '',
      successMessage: ''
    }
  },
  computed: {
    filteredUsers() {
      return this.utilisateurs.filter(u => {
        const matchesDept = this.filtreDepartementId === 'tous' || u.departement?.id === parseInt(this.filtreDepartementId);
        const search = this.searchQuery.toLowerCase();
        const matchesSearch = u.nom.toLowerCase().includes(search) || 
                             u.prenom.toLowerCase().includes(search) || 
                             u.email.toLowerCase().includes(search);
        return matchesDept && matchesSearch;
      });
    }
  },
  mounted() {
    this.loadData();
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
    async loadData() {
      this.isLoading = true;
      try {
        const [usersRes, deptsRes] = await Promise.all([
          axios.get('/api/utilisateurs'),
          axios.get('/api/departements')
        ]);
        this.utilisateurs = usersRes.data.map(u => ({
          ...u,
          tempNom: u.nom,
          tempPrenom: u.prenom,
          tempEmail: u.email,
          tempDepartementId: u.departement?.id
        }));
        this.departements = deptsRes.data;
      } catch (error) {
        this.errorMessage = "Erreur lors du chargement des données.";
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
      // Reset temp values
      this.utilisateurs.forEach(u => {
        u.tempNom = u.nom;
        u.tempPrenom = u.prenom;
        u.tempEmail = u.email;
        u.tempDepartementId = u.departement?.id;
      });
    },
    async saveAllChanges() {
      this.isSaving = true;
      this.errorMessage = '';
      
      // Filter users that actually changed
      const changes = this.utilisateurs.filter(u => 
        u.tempNom !== u.nom || 
        u.tempPrenom !== u.prenom || 
        u.tempEmail !== u.email || 
        u.tempDepartementId !== u.departement?.id
      );

      if (changes.length === 0) {
        this.editMode = false;
        this.isSaving = false;
        return;
      }

      try {
        // Envoi des modifications une par une (ou en batch si l'API le permet)
        // Ici on simule ou on fait un par un pour la démo
        for (const user of changes) {
          await axios.put(`/api/utilisateurs/${user.id}`, {
            nom: user.tempNom,
            prenom: user.tempPrenom,
            email: user.tempEmail,
            departementId: user.tempDepartementId
          });
        }
        
        this.successMessage = `${changes.length} utilisateur(s) mis à jour avec succès.`;
        await this.loadData();
        this.editMode = false;
      } catch (error) {
        this.errorMessage = "Erreur lors de l'enregistrement des modifications.";
        console.error(error);
      } finally {
        this.isSaving = false;
      }
    },
    async deleteUser(id) {
      if (confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) {
        try {
          await axios.delete(`/api/utilisateurs/${id}`);
          this.successMessage = "Utilisateur supprimé.";
          this.loadData();
        } catch (error) {
          this.errorMessage = "Erreur lors de la suppression.";
        }
      }
    }
  }
}
</script>

<style scoped>
.badge.bg-light-primary {
  background-color: #ecf2ff;
  color: #5d87ff;
}
.btn-light-warning {
  background-color: #fef5e5;
  border-color: transparent;
}
.btn-light-danger {
  background-color: #fef2f2;
  border-color: transparent;
}
</style>
