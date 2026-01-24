<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Journal d'audit</h5>
            <button class="btn btn-outline-primary" @click="load" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Rafraîchir
            </button>
          </div>

          <div class="row g-2 mb-3">
            <div class="col-md-3">
              <input class="form-control" v-model="filters.module" placeholder="Filtrer module (ex: STOCK)" />
            </div>
            <div class="col-md-3">
              <input class="form-control" v-model="filters.action" placeholder="Filtrer action" />
            </div>
            <div class="col-md-3">
              <input class="form-control" v-model="filters.reference" placeholder="Filtrer référence objet" />
            </div>
            <div class="col-md-3">
              <input class="form-control" v-model="filters.user" placeholder="Filtrer utilisateur (email/nom)" />
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

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Date</th>
                  <th>Module</th>
                  <th>Action</th>
                  <th>Référence objet</th>
                  <th>Utilisateur</th>
                  <th>Détails</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="a in filtered" :key="a.id">
                  <td>{{ formatDate(a.dateAction) }}</td>
                  <td>{{ a.module || '—' }}</td>
                  <td>{{ a.action || '—' }}</td>
                  <td><strong>{{ a.referenceObjet || '—' }}</strong></td>
                  <td>
                    <div>{{ a.utilisateur?.email || '—' }}</div>
                    <div class="text-muted small">{{ (a.utilisateur?.nom || '') + ' ' + (a.utilisateur?.prenom || '') }}</div>
                  </td>
                  <td style="max-width: 380px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                    {{ a.details || '—' }}
                  </td>
                </tr>
              </tbody>
            </table>

            <div v-if="filtered.length === 0" class="text-center text-muted py-4">
              Aucun audit trouvé.
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
  name: 'Audits',
  components: { MainLayout },
  data() {
    return {
      rows: [],
      isLoading: false,
      errorMessage: '',
      filters: {
        module: 'STOCK',
        action: '',
        reference: '',
        user: ''
      }
    };
  },
  computed: {
    filtered() {
      const m = (this.filters.module || '').toLowerCase().trim();
      const a = (this.filters.action || '').toLowerCase().trim();
      const r = (this.filters.reference || '').toLowerCase().trim();
      const u = (this.filters.user || '').toLowerCase().trim();

      return (this.rows || []).filter(x => {
        const moduleStr = (x.module || '').toLowerCase();
        const actionStr = (x.action || '').toLowerCase();
        const refStr = (x.referenceObjet || '').toLowerCase();
        const userStr = `${x.utilisateur?.email || ''} ${x.utilisateur?.nom || ''} ${x.utilisateur?.prenom || ''}`.toLowerCase();

        if (m && !moduleStr.includes(m)) return false;
        if (a && !actionStr.includes(a)) return false;
        if (r && !refStr.includes(r)) return false;
        if (u && !userStr.includes(u)) return false;
        return true;
      });
    }
  },
  mounted() {
    this.load();
  },
  methods: {
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
        const res = await axios.get('/api/audits');
        this.rows = res.data;
      } catch (e) {
        console.error('Erreur chargement audits:', e);
        this.errorMessage = 'Erreur lors du chargement des audits.';
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
