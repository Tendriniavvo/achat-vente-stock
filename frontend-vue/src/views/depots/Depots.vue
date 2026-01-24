<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Gestion des Dépôts</h5>
            <router-link to="/depots/create" class="btn btn-primary">
              <i class="ti ti-plus"></i> Nouveau Dépôt
            </router-link>
          </div>

          <!-- Filtres et Recherche -->
          <div class="row mb-4 g-3">
            <div class="col-md-6">
              <div class="input-group">
                <span class="input-group-text bg-transparent border-end-0">
                  <i class="ti ti-search text-muted"></i>
                </span>
                <input 
                  type="text" 
                  class="form-control border-start-0" 
                  placeholder="Rechercher par nom, code ou responsable..." 
                  v-model="searchQuery"
                >
              </div>
            </div>
            <div class="col-md-3">
              <select class="form-select" v-model="filterStatut">
                <option value="tous">Tous les statuts</option>
                <option value="actif">Actifs uniquement</option>
                <option value="inactif">Inactifs uniquement</option>
              </select>
            </div>
          </div>

          <div v-if="errorMessage" class="alert alert-danger" role="alert">
            {{ errorMessage }}
          </div>

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else-if="filteredDepots.length === 0" class="text-center py-5">
            <i class="ti ti-building-warehouse fs-1 text-muted"></i>
            <p class="mt-3 text-muted">Aucun dépôt trouvé</p>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Code</th>
                  <th>Nom du Dépôt</th>
                  <th>Responsable</th>
                  <th>Capacité</th>
                  <th class="text-center">Statut</th>
                  <th class="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="d in filteredDepots" :key="d.id">
                  <td><span class="fw-bold">{{ d.code }}</span></td>
                  <td>
                    <div class="d-flex flex-column">
                      <span class="text-primary">{{ d.nom }}</span>
                      <small class="text-muted">{{ d.adresse || 'Sans adresse' }}</small>
                    </div>
                  </td>
                  <td>{{ d.responsable || 'N/A' }}</td>
                  <td>{{ d.capacite ? d.capacite + ' m³' : 'N/A' }}</td>
                  <td class="text-center">
                    <span :class="d.actif ? 'badge bg-success' : 'badge bg-danger'" 
                          @click="toggleStatus(d)" 
                          style="cursor: pointer"
                          :title="d.actif ? 'Désactiver' : 'Activer'">
                      {{ d.actif ? 'Actif' : 'Inactif' }}
                    </span>
                  </td>
                  <td class="text-end">
                    <div class="btn-group">
                      <router-link 
                        :to="`/emplacements?depotId=${d.id}`" 
                        class="btn btn-sm btn-outline-primary"
                        title="Gérer les emplacements"
                      >
                        <i class="ti ti-box"></i>
                      </router-link>
                      <router-link 
                        :to="`/depots/${d.id}`" 
                        class="btn btn-sm btn-outline-info"
                        title="Voir détails"
                      >
                        <i class="ti ti-eye"></i>
                      </router-link>
                      <router-link 
                        :to="`/depots/${d.id}/edit`" 
                        class="btn btn-sm btn-outline-warning"
                        title="Modifier"
                      >
                        <i class="ti ti-edit"></i>
                      </router-link>
                      <button 
                        @click="confirmDelete(d)" 
                        class="btn btn-sm btn-outline-danger"
                        title="Supprimer"
                      >
                        <i class="ti ti-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de confirmation de suppression -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Confirmer la suppression</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            Êtes-vous sûr de vouloir supprimer le dépôt <strong>{{ depotToDelete?.nom }}</strong> ?
            Cette action est irréversible.
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
            <button type="button" class="btn btn-danger" @click="deleteDepot" :disabled="isDeleting">
              <span v-if="isDeleting" class="spinner-border spinner-border-sm me-1"></span>
              Supprimer
            </button>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import MainLayout from '../../layouts/MainLayout.vue';
import axios from 'axios';
import { Modal } from 'bootstrap';

const depots = ref([]);
const isLoading = ref(true);
const isDeleting = ref(false);
const errorMessage = ref('');
const searchQuery = ref('');
const filterStatut = ref('tous');
const depotToDelete = ref(null);
let deleteModal = null;

const fetchDepots = async () => {
  isLoading.ref = true;
  try {
    const response = await axios.get('/api/depots');
    depots.value = response.data;
  } catch (err) {
    errorMessage.value = "Erreur lors du chargement des dépôts.";
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

const filteredDepots = computed(() => {
  return depots.value.filter(d => {
    const matchesSearch = 
      d.nom?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      d.code?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      d.responsable?.toLowerCase().includes(searchQuery.value.toLowerCase());
    
    const matchesStatut = 
      filterStatut.value === 'tous' || 
      (filterStatut.value === 'actif' && d.actif) || 
      (filterStatut.value === 'inactif' && !d.actif);
      
    return matchesSearch && matchesStatut;
  });
});

const toggleStatus = async (depot) => {
  try {
    await axios.patch(`/api/depots/${depot.id}/toggle-status`);
    depot.actif = !depot.actif;
  } catch (err) {
    alert("Erreur lors de la modification du statut.");
  }
};

const confirmDelete = (depot) => {
  depotToDelete.value = depot;
  deleteModal.show();
};

const deleteDepot = async () => {
  if (!depotToDelete.value) return;
  
  isDeleting.value = true;
  try {
    await axios.delete(`/api/depots/${depotToDelete.value.id}`);
    depots.value = depots.value.filter(d => d.id !== depotToDelete.value.id);
    deleteModal.hide();
  } catch (err) {
    alert("Erreur lors de la suppression du dépôt.");
  } finally {
    isDeleting.value = false;
  }
};

onMounted(() => {
  fetchDepots();
  deleteModal = new Modal(document.getElementById('deleteModal'));
});
</script>

<style scoped>
.badge {
  font-weight: 500;
  padding: 0.5em 0.75em;
}
.table-responsive {
  min-height: 200px;
}
</style>