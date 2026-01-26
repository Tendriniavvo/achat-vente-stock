<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Lots</h5>
            <button class="btn btn-outline-primary" @click="load" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Rafraîchir
            </button>
          </div>

          <div class="row g-2 mb-3">
            <div class="col-md-4">
              <input class="form-control" v-model="filters.numeroLot" placeholder="Filtrer Numéro lot" />
            </div>
            <div class="col-md-4">
              <input class="form-control" v-model="filters.article" placeholder="Filtrer Article (code/nom)" />
            </div>
            <div class="col-md-4">
              <select class="form-select" v-model="filters.etat">
                <option value="">Tous</option>
                <option value="OK">OK</option>
                <option value="EXPIRE">Expiré</option>
                <option value="NON_CONFORME">Non conforme</option>
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

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Numéro lot</th>
                  <th>Article</th>
                  <th>Date entrée</th>
                  <th>Date expiration</th>
                  <th class="text-end">Quantité</th>
                  <th>Conforme</th>
                  <th>État</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="l in filtered" :key="l.id">
                  <td><strong>{{ l.numeroLot }}</strong></td>
                  <td>
                    <strong>{{ l.article?.code || '—' }}</strong>
                    <div class="text-muted small">{{ l.article?.nom || '' }}</div>
                  </td>
                  <td>{{ formatDate(l.dateEntree) }}</td>
                  <td>{{ formatDate(l.dateExpiration) }}</td>
                  <td class="text-end">{{ l.quantite ?? '—' }}</td>
                  <td>
                    <span :class="l.conforme ? 'badge bg-success' : 'badge bg-danger'">
                      {{ l.conforme ? 'Oui' : 'Non' }}
                    </span>
                  </td>
                  <td>
                    <span :class="etatBadgeClass(l)">{{ etatLabel(l) }}</span>
                  </td>
                </tr>
              </tbody>
            </table>

            <div v-if="filtered.length === 0" class="text-center text-muted py-4">
              Aucun lot trouvé.
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
  name: 'Lots',
  components: { MainLayout },
  data() {
    return {
      rows: [],
      isLoading: false,
      errorMessage: '',
      filters: {
        numeroLot: '',
        article: '',
        etat: ''
      }
    };
  },
  computed: {
    filtered() {
      const n = (this.filters.numeroLot || '').toLowerCase().trim();
      const a = (this.filters.article || '').toLowerCase().trim();
      const etat = (this.filters.etat || '').toUpperCase().trim();

      return (this.rows || []).filter(l => {
        const numero = (l.numeroLot || '').toLowerCase();
        const articleStr = `${l.article?.code || ''} ${l.article?.nom || ''}`.toLowerCase();
        if (n && !numero.includes(n)) return false;
        if (a && !articleStr.includes(a)) return false;

        if (etat) {
          const now = new Date();
          const exp = l.dateExpiration ? new Date(l.dateExpiration) : null;
          const expired = !!exp && exp.getTime() < now.getTime();
          const nonConforme = l.conforme === false;

          if (etat === 'EXPIRE' && !expired) return false;
          if (etat === 'NON_CONFORME' && !nonConforme) return false;
          if (etat === 'OK' && (expired || nonConforme)) return false;
        }

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
    etatLabel(l) {
      const now = new Date();
      const exp = l.dateExpiration ? new Date(l.dateExpiration) : null;
      const expired = !!exp && exp.getTime() < now.getTime();
      if (l.conforme === false) return 'NON CONFORME';
      if (expired) return 'EXPIRÉ';
      return 'OK';
    },
    etatBadgeClass(l) {
      const label = this.etatLabel(l);
      if (label === 'OK') return 'badge bg-success';
      if (label === 'EXPIRÉ') return 'badge bg-warning';
      return 'badge bg-danger';
    },
    async load() {
      this.isLoading = true;
      this.errorMessage = '';
      try {
        const res = await axios.get('/api/lots');
        this.rows = res.data;
      } catch (e) {
        console.error('Erreur chargement lots:', e);
        this.errorMessage = 'Erreur lors du chargement des lots.';
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
