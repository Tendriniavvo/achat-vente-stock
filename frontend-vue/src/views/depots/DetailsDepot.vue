<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex align-items-center mb-4">
            <button @click="$router.back()" class="btn btn-outline-secondary me-3">
              <i class="ti ti-arrow-left"></i>
            </button>
            <h5 class="card-title fw-semibold mb-0">Détails du Dépôt : {{ depot?.nom }}</h5>
            <div class="ms-auto">
              <router-link :to="`/depots/${depot?.id}/edit`" class="btn btn-warning me-2">
                <i class="ti ti-edit"></i> Modifier
              </router-link>
            </div>
          </div>

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else-if="depot" class="row">
            <!-- Colonne de gauche : Informations principales -->
            <div class="col-md-8">
              <div class="card bg-light-info shadow-none border-0 mb-4">
                <div class="card-body">
                  <div class="row">
                    <div class="col-md-6 mb-3">
                      <label class="text-muted small text-uppercase fw-bold">Code Identification</label>
                      <p class="fs-4 fw-semibold mb-0 text-primary">{{ depot.code }}</p>
                    </div>
                    <div class="col-md-6 mb-3">
                      <label class="text-muted small text-uppercase fw-bold">Statut</label>
                      <div>
                        <span :class="depot.actif ? 'badge bg-success' : 'badge bg-danger'">
                          {{ depot.actif ? 'Actif' : 'Inactif' }}
                        </span>
                      </div>
                    </div>
                    <div class="col-md-6 mb-3">
                      <label class="text-muted small text-uppercase fw-bold">Responsable</label>
                      <p class="fs-4 fw-semibold mb-0">{{ depot.responsable || 'Non assigné' }}</p>
                    </div>
                    <div class="col-md-6 mb-3">
                      <label class="text-muted small text-uppercase fw-bold">Capacité</label>
                      <p class="fs-4 fw-semibold mb-0">{{ depot.capacite ? depot.capacite + ' m³' : 'N/A' }}</p>
                    </div>
                  </div>
                </div>
              </div>

              <div class="mb-4">
                <h6 class="fw-semibold mb-3">Adresse Physique</h6>
                <div class="p-3 bg-white border rounded">
                  <i class="ti ti-map-pin me-2 text-primary"></i>
                  {{ depot.adresse || 'Aucune adresse renseignée' }}
                </div>
              </div>

              <div class="row">
                <div class="col-md-6 mb-4">
                  <h6 class="fw-semibold mb-3">Type d'entreposage</h6>
                  <div class="p-3 bg-white border rounded">
                    <i class="ti ti-box me-2 text-primary"></i>
                    {{ depot.typeEntreposage || 'Non spécifié' }}
                  </div>
                </div>
                <div class="col-md-6 mb-4">
                  <h6 class="fw-semibold mb-3">Horaires d'ouverture</h6>
                  <div class="p-3 bg-white border rounded">
                    <i class="ti ti-clock me-2 text-primary"></i>
                    {{ depot.horairesOuverture || 'Non spécifiés' }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Colonne de droite : Emplacements ou statistiques rapides -->
            <div class="col-md-4">
              <div class="card border mb-4">
                <div class="card-header bg-transparent border-bottom">
                  <h6 class="card-title fw-semibold mb-0">Emplacements</h6>
                </div>
                <div class="card-body">
                  <p class="text-muted small">Ce dépôt contient plusieurs emplacements de stockage pour organiser les articles.</p>
                  <router-link :to="`/emplacements?depotId=${depot.id}`" class="btn btn-outline-primary w-100">
                    Gérer les emplacements
                  </router-link>
                </div>
              </div>

              <div class="card border">
                <div class="card-header bg-transparent border-bottom">
                  <h6 class="card-title fw-semibold mb-0">Historique rapide</h6>
                </div>
                <div class="card-body p-0">
                  <div class="list-group list-group-flush">
                    <div class="list-group-item px-3 py-2 small">
                      <span class="text-muted">Créé le :</span> {{ formatDate(depot.dateCreation) || 'N/A' }}
                    </div>
                    <div class="list-group-item px-3 py-2 small">
                      <span class="text-muted">Dernière modif :</span> {{ formatDate(depot.dateModification) || 'N/A' }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import MainLayout from '../../layouts/MainLayout.vue';
import axios from 'axios';

const route = useRoute();
const depot = ref(null);
const isLoading = ref(true);

const fetchDepot = async () => {
  try {
    const response = await axios.get(`/api/depots/${route.params.id}`);
    depot.value = response.data;
  } catch (err) {
    console.error("Erreur lors de la récupération du dépôt:", err);
  } finally {
    isLoading.value = false;
  }
};

const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
};

onMounted(fetchDepot);
</script>

<style scoped>
.bg-light-info {
  background-color: #e8f7ff !important;
}
</style>