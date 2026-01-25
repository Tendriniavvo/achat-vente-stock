<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title fw-semibold mb-4">Niveaux de Stock</h5>
          
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
                    <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Article</h6></th>
                    <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Dépôt</h6></th>
                    <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Emplacement</h6></th>
                    <th class="border-bottom-0 text-center"><h6 class="fw-semibold mb-0">Quantité</h6></th>
                    <th class="border-bottom-0 text-center"><h6 class="fw-semibold mb-0">Statut</h6></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="s in stocks" :key="s.id">
                    <td class="border-bottom-0">
                      <h6 class="fw-semibold mb-1">{{ s.article?.nom }}</h6>
                      <span class="fw-normal">{{ s.article?.code }}</span>
                    </td>
                    <td class="border-bottom-0">
                      <p class="mb-0 fw-normal">{{ s.depot?.nom }}</p>
                    </td>
                    <td class="border-bottom-0">
                      <p class="mb-0 fw-normal">{{ s.emplacement?.code || 'N/A' }}</p>
                    </td>
                    <td class="border-bottom-0 text-center">
                      <h6 class="fw-semibold mb-0">{{ s.quantite }}</h6>
                    </td>
                    <td class="border-bottom-0 text-center">
                      <span :class="getStatusClass(s)" class="badge rounded-3 fw-semibold">
                        {{ getStatusText(s) }}
                      </span>
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

const stocks = ref([]);
const loading = ref(true);

const fetchStocks = async () => {
  try {
    const response = await axios.get('/api/stocks');
    stocks.value = response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des stocks:', error);
  } finally {
    loading.value = false;
  }
};

const getStatusClass = (stock) => {
  const stockMin = stock.article?.stockMin || 10;
  if (stock.quantite <= 0) return 'bg-light-danger text-danger';
  if (stock.quantite < stockMin) return 'bg-light-warning text-warning';
  return 'bg-light-success text-success';
};

const getStatusText = (stock) => {
  const stockMin = stock.article?.stockMin || 10;
  if (stock.quantite <= 0) return 'Rupture';
  if (stock.quantite < stockMin) return 'Bas';
  return 'Ok';
};

onMounted(fetchStocks);
</script>
