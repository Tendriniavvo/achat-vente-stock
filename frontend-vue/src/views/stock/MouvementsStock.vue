<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Mouvements de Stock</h5>
            <router-link to="/stock/mouvements/create" class="btn btn-primary">
              <i class="ti ti-plus"></i> Nouveau Mouvement
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

          <div v-else-if="mouvements.length === 0" class="text-center py-5">
            <i class="ti ti-arrows-exchange" style="font-size: 3rem; color: #ccc;"></i>
            <p class="text-muted mt-3">Aucun mouvement de stock</p>
            <router-link to="/stock/mouvements/create" class="btn btn-primary btn-sm">
              <i class="ti ti-plus"></i> Créer le premier mouvement
            </router-link>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Référence</th>
                  <th>Type</th>
                  <th>Statut</th>
                  <th>Date</th>
                  <th>Dépôt</th>
                  <th>Emplacement</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="m in mouvements" :key="m.id">
                  <td><strong>{{ m.reference || ('#' + m.id) }}</strong></td>
                  <td>{{ (m.type || '').toUpperCase() }}</td>
                  <td>
                    <span :class="badgeClass(m.statut)">{{ m.statut || 'BROUILLON' }}</span>
                  </td>
                  <td>{{ formatDate(m.dateMouvement) }}</td>
                  <td>{{ m.depot?.nom || '—' }}</td>
                  <td>{{ m.emplacement?.code || '—' }}</td>
                  <td>
                    <router-link :to="`/stock/mouvements/${m.id}`" class="btn btn-sm btn-outline-primary me-1" title="Voir">
                      <i class="ti ti-eye"></i>
                    </router-link>
                    <button
                      class="btn btn-sm btn-outline-success"
                      :disabled="(m.statut || '').toUpperCase() === 'VALIDE'"
                      @click="valider(m)"
                      title="Valider"
                    >
                      <i class="ti ti-check"></i>
                    </button>
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
  name: 'MouvementsStock',
  components: { MainLayout },
  data() {
    return {
      mouvements: [],
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
      const s = (statut || 'BROUILLON').toUpperCase();
      if (s === 'VALIDE') return 'badge bg-success';
      if (s === 'ANNULE') return 'badge bg-secondary';
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
        const res = await axios.get('http://localhost:8080/api/mouvements-stock');
        this.mouvements = res.data;
      } catch (e) {
        console.error('Erreur chargement mouvements:', e);
        this.errorMessage = 'Erreur lors du chargement des mouvements.';
      } finally {
        this.isLoading = false;
      }
    },
    async valider(m) {
      if (!m?.id) return;
      try {
        await axios.post(`http://localhost:8080/api/mouvements-stock/${m.id}/valider`);
        this.successMessage = `Mouvement ${m.reference || ('#' + m.id)} validé.`;
        await this.load();
        setTimeout(() => (this.successMessage = ''), 3000);
      } catch (e) {
        console.error('Erreur validation mouvement:', e);
        this.errorMessage = 'Impossible de valider le mouvement (vérifie emplacement, lots, stock disponible).';
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
