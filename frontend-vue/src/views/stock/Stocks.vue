<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Stocks</h5>
            <button class="btn btn-outline-primary" @click="load" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Rafraîchir
            </button>
          </div>

          <div class="row g-2 mb-3">
            <div class="col-md-3">
              <input class="form-control" v-model="filters.article" placeholder="Filtrer Article (code/nom)" />
            </div>
            <div class="col-md-3">
              <input class="form-control" v-model="filters.depot" placeholder="Filtrer Dépôt" />
            </div>
            <div class="col-md-3">
              <input class="form-control" v-model="filters.emplacement" placeholder="Filtrer Emplacement" />
            </div>
            <div class="col-md-3">
              <input class="form-control" v-model="filters.quantiteMin" type="number" placeholder="Qté min" />
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
                  <th>Article</th>
                  <th>Dépôt</th>
                  <th>Emplacement</th>
                  <th class="text-end">Quantité</th>
                  <th class="text-end">Valeur</th>
                  <th>Dernière MAJ</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="s in filtered" :key="s.id">
                  <td>
                    <strong>{{ s.article?.code || '—' }}</strong>
                    <div class="text-muted small">{{ s.article?.nom || '' }}</div>
                  </td>
                  <td>{{ s.depot?.nom || '—' }}</td>
                  <td>{{ s.emplacement?.code || '—' }}</td>
                  <td class="text-end">{{ s.quantite ?? '—' }}</td>
                  <td class="text-end">{{ s.valeur ?? '—' }}</td>
                  <td>{{ formatDate(s.dateMaj) }}</td>
                </tr>
              </tbody>
            </table>

            <div v-if="filtered.length === 0" class="text-center text-muted py-4">
              Aucun stock trouvé.
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
  name: 'Stocks',
  components: { MainLayout },
  data() {
    return {
      rows: [],
      isLoading: false,
      errorMessage: '',
      filters: {
        article: '',
        depot: '',
        emplacement: '',
        quantiteMin: ''
      }
    };
  },
  computed: {
    filtered() {
      const a = (this.filters.article || '').toLowerCase().trim();
      const d = (this.filters.depot || '').toLowerCase().trim();
      const e = (this.filters.emplacement || '').toLowerCase().trim();
      const qMin = this.filters.quantiteMin === '' || this.filters.quantiteMin === null
        ? null
        : Number(this.filters.quantiteMin);

      return (this.rows || []).filter(s => {
        const articleStr = `${s.article?.code || ''} ${s.article?.nom || ''}`.toLowerCase();
        const depotStr = `${s.depot?.nom || ''}`.toLowerCase();
        const emplStr = `${s.emplacement?.code || ''}`.toLowerCase();

        if (a && !articleStr.includes(a)) return false;
        if (d && !depotStr.includes(d)) return false;
        if (e && !emplStr.includes(e)) return false;
        if (qMin !== null && Number(s.quantite || 0) < qMin) return false;
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
        const res = await axios.get('/api/stocks');
        this.rows = res.data;
      } catch (e) {
        console.error('Erreur chargement stocks:', e);
        this.errorMessage = 'Erreur lors du chargement des stocks.';
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
