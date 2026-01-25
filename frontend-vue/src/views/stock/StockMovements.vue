<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title fw-semibold mb-4">Mouvements de Stock</h5>
          
          <div v-if="loading" class="text-center my-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else>
            <div class="table-responsive">
              <table class="table table-hover text-nowrap mb-0 align-middle">
                <thead class="text-dark fs-4">
                  <tr>
                    <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Date</h6></th>
                    <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Article</h6></th>
                    <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Type</h6></th>
                    <th class="border-bottom-0 text-center"><h6 class="fw-semibold mb-0">Quantité</h6></th>
                    <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Dépôt / Empl.</h6></th>
                    <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Référence</h6></th>
                    <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Motif</h6></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="m in mouvements" :key="m.id">
                    <td class="border-bottom-0">
                      <p class="mb-0 fw-normal">{{ formatDate(m.dateMouvement) }}</p>
                    </td>
                    <td class="border-bottom-0">
                      <h6 class="fw-semibold mb-1">{{ m.article?.nom }}</h6>
                      <span class="fw-normal">{{ m.article?.code }}</span>
                    </td>
                    <td class="border-bottom-0">
                      <span :class="getTypeClass(m.type)" class="badge rounded-3 fw-semibold text-uppercase">
                        {{ m.type }}
                      </span>
                    </td>
                    <td class="border-bottom-0 text-center">
                      <h6 class="fw-semibold mb-0" :class="m.type === 'entree' ? 'text-success' : 'text-danger'">
                        {{ m.type === 'entree' ? '+' : '-' }}{{ m.quantite }}
                      </h6>
                    </td>
                    <td class="border-bottom-0">
                      <p class="mb-0 fw-normal">{{ m.depot?.nom }}</p>
                      <small class="text-muted">{{ m.emplacement?.code || 'N/A' }}</small>
                    </td>
                    <td class="border-bottom-0">
                      <p class="mb-0 fw-normal">{{ m.referenceDocument }}</p>
                    </td>
                    <td class="border-bottom-0">
                      <p class="mb-0 fw-normal text-wrap" style="max-width: 200px;">{{ m.motif }}</p>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import MainLayout from '@/layouts/MainLayout.vue';

const mouvements = ref([]);
const loading = ref(true);

const fetchMouvements = async () => {
  try {
    const response = await axios.get('/api/mouvements-stock');
    // Trier par date décroissante
    mouvements.value = response.data.sort((a, b) => new Date(b.dateMouvement) - new Date(a.dateMouvement));
  } catch (error) {
    console.error('Erreur lors de la récupération des mouvements:', error);
  } finally {
    loading.value = false;
  }
};

const getTypeClass = (type) => {
  switch (type.toLowerCase()) {
    case 'entree': return 'bg-light-success text-success';
    case 'sortie': return 'bg-light-danger text-danger';
    case 'reservation': return 'bg-light-warning text-warning';
    case 'ajustement': return 'bg-light-primary text-primary';
    default: return 'bg-light-secondary text-secondary';
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

onMounted(fetchMouvements);
</script>
