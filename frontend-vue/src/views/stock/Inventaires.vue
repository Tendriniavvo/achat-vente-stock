<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Inventaires</h5>
            <router-link to="/stock/inventaires/create" class="btn btn-primary">
              <i class="ti ti-plus"></i> Nouvel Inventaire
            </router-link>
          </div>

          <div v-if="errorMessage" class="alert alert-danger" role="alert">
            {{ errorMessage }}
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

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Référence</th>
                  <th>Type</th>
                  <th>Statut</th>
                  <th>Dépôt</th>
                  <th>Date début</th>
                  <th>Date fin</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="inv in inventaires" :key="inv.id">
                  <td><strong>{{ inv.reference || ('#' + inv.id) }}</strong></td>
                  <td>{{ inv.type || '—' }}</td>
                  <td><span :class="badgeClass(inv.statut)">{{ inv.statut || 'en_cours' }}</span></td>
                  <td>{{ inv.depot?.nom || '—' }}</td>
                  <td>{{ formatDate(inv.dateDebut) }}</td>
                  <td>{{ formatDate(inv.dateFin) }}</td>
                  <td>
                    <router-link :to="`/stock/inventaires/${inv.id}`" class="btn btn-sm btn-outline-primary" title="Voir">
                      <i class="ti ti-eye"></i>
                    </router-link>
                  </td>
                </tr>
              </tbody>
            </table>

            <div v-if="inventaires.length === 0" class="text-center text-muted py-4">
              Aucun inventaire.
            </div>
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
  name: 'Inventaires',
  components: { MainLayout },
  data() {
    return {
      inventaires: [],
      isLoading: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  mounted() {
    this.load();
  },
  methods: {
    badgeClass(statut) {
      const s = (statut || 'en_cours').toLowerCase();
      if (s === 'valide') return 'badge bg-success';
      if (s === 'termine') return 'badge bg-secondary';
      return 'badge bg-warning';
    },
    formatDate(value) {
      if (!value) return '—';
      try {
        return new Date(value).toLocaleString();
      } catch {
        return value;
      }
    },
    async load() {
      this.isLoading = true;
      this.errorMessage = '';
      try {
        const res = await axios.get('/api/inventaires');
        this.inventaires = res.data || [];
      } catch (e) {
        console.error('Erreur chargement inventaires:', e);
        this.errorMessage = 'Erreur lors du chargement des inventaires.';
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.table-responsive {
  margin-top: 1rem;
}
</style>
