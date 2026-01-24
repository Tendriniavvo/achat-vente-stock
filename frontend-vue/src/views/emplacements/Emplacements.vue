<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
              <h5 class="card-title fw-semibold mb-1">Organisation des Emplacements</h5>
              <p class="text-muted mb-0" v-if="depot">Dépôt : {{ depot.nom }} ({{ depot.code }})</p>
            </div>
            <div class="d-flex gap-2">
              <button @click="$router.back()" class="btn btn-outline-secondary">
                <i class="ti ti-arrow-left"></i> Retour
              </button>
              <router-link :to="`/emplacements/create?depotId=${depotId}`" class="btn btn-primary">
                <i class="ti ti-plus"></i> Nouvel Emplacement
              </router-link>
            </div>
          </div>

          <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else-if="emplacements.length === 0" class="text-center py-5">
            <i class="ti ti-box-off fs-9 text-muted mb-3 d-block"></i>
            <p class="text-muted">Aucun emplacement configuré pour ce dépôt.</p>
            <router-link :to="`/emplacements/create?depotId=${depotId}`" class="btn btn-primary btn-sm">
              Créer le premier emplacement
            </router-link>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Nomenclature / Code</th>
                  <th>Type</th>
                  <th>Description</th>
                  <th>Capacité</th>
                  <th>Caractéristiques</th>
                  <th class="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                <template v-for="emp in flattenedEmplacements" :key="emp.id">
                  <tr :class="{ 'bg-light-primary': emp.typeEmplacement?.libelle === 'ZONE' }">
                    <td>
                      <div :style="{ paddingLeft: (emp.level * 20) + 'px' }" class="d-flex align-items-center">
                        <i v-if="emp.level > 0" class="ti ti-corner-down-right me-2 text-muted"></i>
                        <span class="fw-bold text-primary">{{ emp.code }}</span>
                      </div>
                    </td>
                    <td>
                      <span :class="getTypeBadgeClass(emp.typeEmplacement?.libelle)">
                        {{ emp.typeEmplacement?.libelle }}
                      </span>
                    </td>
                    <td class="small">{{ emp.description }}</td>
                    <td>{{ emp.capacite ? emp.capacite + ' m³' : 'N/A' }}</td>
                    <td class="small">
                      <div v-if="emp.caracteristiques" class="text-truncate" style="max-width: 200px;" :title="emp.caracteristiques">
                        {{ emp.caracteristiques }}
                      </div>
                      <span v-else class="text-muted">-</span>
                    </td>
                    <td class="text-end">
                      <div class="btn-group btn-group-sm">
                        <router-link :to="`/emplacements/${emp.id}`" class="btn btn-light" title="Détails">
                          <i class="ti ti-eye"></i>
                        </router-link>
                        <router-link :to="`/emplacements/${emp.id}/edit`" class="btn btn-light" title="Modifier">
                          <i class="ti ti-edit"></i>
                        </router-link>
                        <button @click="confirmDelete(emp)" class="btn btn-light text-danger" title="Supprimer">
                          <i class="ti ti-trash"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                </template>
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
            Êtes-vous sûr de vouloir supprimer l'emplacement <strong>{{ emplacementToDelete?.code }}</strong> ?
            <p class="text-danger small mt-2 mb-0">
              <i class="ti ti-alert-triangle"></i> Cette action supprimera également tous les sous-emplacements associés.
            </p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-light" data-bs-dismiss="modal">Annuler</button>
            <button type="button" @click="deleteEmplacement" class="btn btn-danger">Supprimer</button>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import MainLayout from '@/layouts/MainLayout.vue';
import { Modal } from 'bootstrap';

const route = useRoute();
const router = useRouter();
const depotId = route.query.depotId;

const loading = ref(true);
const depot = ref(null);
const emplacements = ref([]);
const emplacementToDelete = ref(null);
let deleteModal = null;

const fetchDepot = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/depots/${depotId}`);
    depot.value = response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération du dépôt:', error);
  }
};

const fetchEmplacements = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/emplacements/depot/${depotId}`);
    emplacements.value = response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des emplacements:', error);
  } finally {
    loading.value = false;
  }
};

const flattenedEmplacements = computed(() => {
  const result = [];
  const roots = emplacements.value.filter(e => !e.parent);
  
  const traverse = (nodes, level = 0) => {
    // Trier les nœuds par code ou type si nécessaire
    nodes.forEach(node => {
      result.push({ ...node, level });
      const children = emplacements.value.filter(e => e.parent && e.parent.id === node.id);
      if (children.length > 0) {
        traverse(children, level + 1);
      }
    });
  };
  
  traverse(roots);
  return result;
});

const getTypeBadgeClass = (type) => {
  const classes = {
    'ZONE': 'badge bg-primary-subtle text-primary',
    'ALLEE': 'badge bg-secondary-subtle text-secondary',
    'RAYONNAGE': 'badge bg-success-subtle text-success',
    'NIVEAU': 'badge bg-info-subtle text-info',
    'CASIER': 'badge bg-warning-subtle text-warning'
  };
  return classes[type] || 'badge bg-light text-dark';
};

const confirmDelete = (emp) => {
  emplacementToDelete.value = emp;
  deleteModal.show();
};

const deleteEmplacement = async () => {
  if (!emplacementToDelete.value) return;
  
  try {
    await axios.delete(`http://localhost:8080/api/emplacements/${emplacementToDelete.value.id}`);
    deleteModal.hide();
    fetchEmplacements();
  } catch (error) {
    console.error('Erreur lors de la suppression:', error);
    alert('Erreur lors de la suppression de l\'emplacement.');
  }
};

onMounted(() => {
  if (depotId && depotId !== 'undefined') {
    fetchDepot();
    fetchEmplacements();
    deleteModal = new Modal(document.getElementById('deleteModal'));
  } else {
    router.push('/depots');
  }
});
</script>