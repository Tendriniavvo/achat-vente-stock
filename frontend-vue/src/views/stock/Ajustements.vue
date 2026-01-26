<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Ajustements de Stock</h5>
            <button class="btn btn-outline-primary" @click="load" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Rafraîchir
            </button>
          </div>

          <div class="row g-2 mb-3">
            <div class="col-md-3">
              <input class="form-control" v-model="filters.reference" placeholder="Filtrer Référence" />
            </div>
            <div class="col-md-3">
              <input class="form-control" v-model="filters.article" placeholder="Filtrer Article (code)" />
            </div>
            <div class="col-md-3">
              <input class="form-control" v-model="filters.emplacement" placeholder="Filtrer Emplacement" />
            </div>
            <div class="col-md-3">
              <select class="form-select" v-model="filters.statut">
                <option value="">Tous statuts</option>
                <option value="propose">PROPOSE</option>
                <option value="valide">VALIDE</option>
                <option value="rejete">REJETE</option>
              </select>
            </div>
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
                  <th>Inventaire</th>
                  <th>Article</th>
                  <th>Emplacement</th>
                  <th>Lot</th>
                  <th>Type</th>
                  <th class="text-end">Quantité</th>
                  <th>Statut</th>
                  <th>Date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="a in filtered" :key="a.id">
                  <td><strong>{{ a.reference || ('#' + a.id) }}</strong></td>
                  <td>{{ a.inventaire?.reference || '—' }}</td>
                  <td>{{ a.article?.code || '—' }}</td>
                  <td>{{ a.emplacement?.code || '—' }}</td>
                  <td style="min-width: 220px">
                    <div v-if="a.article?.traceableLot">
                      <select class="form-select form-select-sm" v-model.number="selectedLot[a.id]">
                        <option :value="null">-- Choisir lot --</option>
                        <option v-for="l in lotsForArticle(a.article?.id)" :key="l.id" :value="l.id">
                          {{ l.numeroLot || ('Lot #' + l.id) }}
                        </option>
                      </select>
                    </div>
                    <div v-else class="text-muted">—</div>
                  </td>
                  <td>{{ (a.type || '').toUpperCase() }}</td>
                  <td class="text-end">{{ a.quantiteAjustee ?? '—' }}</td>
                  <td><span :class="badgeClass(a.statut)">{{ a.statut || 'propose' }}</span></td>
                  <td>{{ formatDate(a.dateAjustement) }}</td>
                  <td>
                    <button
                      class="btn btn-sm btn-outline-success"
                      :disabled="(a.statut || '').toLowerCase() === 'valide'"
                      @click="valider(a)"
                      title="Valider"
                    >
                      <i class="ti ti-check"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>

            <div v-if="filtered.length === 0" class="text-center text-muted py-4">
              Aucun ajustement trouvé.
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
  name: 'Ajustements',
  components: { MainLayout },
  data() {
    return {
      rows: [],
      lots: [],
      selectedLot: {},
      isLoading: false,
      errorMessage: '',
      successMessage: '',
      filters: {
        reference: '',
        article: '',
        emplacement: '',
        statut: ''
      }
    };
  },
  computed: {
    filtered() {
      const r = (this.filters.reference || '').toLowerCase().trim();
      const a = (this.filters.article || '').toLowerCase().trim();
      const e = (this.filters.emplacement || '').toLowerCase().trim();
      const s = (this.filters.statut || '').toLowerCase().trim();

      return (this.rows || []).filter(x => {
        const ref = `${x.reference || ''}`.toLowerCase();
        const art = `${x.article?.code || ''}`.toLowerCase();
        const empl = `${x.emplacement?.code || ''}`.toLowerCase();
        const st = `${x.statut || ''}`.toLowerCase();

        if (r && !ref.includes(r)) return false;
        if (a && !art.includes(a)) return false;
        if (e && !empl.includes(e)) return false;
        if (s && st !== s) return false;
        return true;
      });
    }
  },
  mounted() {
    this.load();
    this.loadLots();
  },
  methods: {
    currentUserId() {
      try {
        const auth = JSON.parse(localStorage.getItem('user') || 'null');
        return auth?.user?.id || null;
      } catch {
        return null;
      }
    },
    badgeClass(statut) {
      const s = (statut || 'propose').toLowerCase();
      if (s === 'valide') return 'badge bg-success';
      if (s === 'rejete') return 'badge bg-secondary';
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
    lotsForArticle(articleId) {
      if (!articleId) return [];
      return (this.lots || []).filter(l => l.article?.id === articleId);
    },
    async loadLots() {
      try {
        const res = await axios.get('/api/lots');
        this.lots = res.data || [];
      } catch (e) {
        console.error('Erreur chargement lots:', e);
      }
    },
    async load() {
      this.isLoading = true;
      this.errorMessage = '';
      try {
        const res = await axios.get('/api/ajustements-stock');
        this.rows = res.data || [];

        const sel = { ...(this.selectedLot || {}) };
        for (const a of this.rows) {
          if (a?.id && a?.lot?.id && sel[a.id] == null) {
            sel[a.id] = a.lot.id;
          }
        }
        this.selectedLot = sel;
      } catch (e) {
        console.error('Erreur chargement ajustements:', e);
        this.errorMessage = 'Erreur lors du chargement des ajustements.';
      } finally {
        this.isLoading = false;
      }
    },
    async valider(a) {
      if (!a?.id) return;
      this.errorMessage = '';
      try {
        if (a.article?.traceableLot && !this.selectedLot[a.id] && (a.lot?.id == null)) {
          this.errorMessage = `Lot obligatoire pour l'article traçable: ${a.article?.code || ''}`;
          return;
        }
        const userId = this.currentUserId();
        await axios.post(`/api/ajustements-stock/${a.id}/valider`, {
          utilisateurValidationId: userId,
          lotId: this.selectedLot[a.id] || null
        });
        this.successMessage = `Ajustement ${a.reference || ('#' + a.id)} validé.`;
        await this.load();
        setTimeout(() => (this.successMessage = ''), 2500);
      } catch (e) {
        console.error('Erreur validation ajustement:', e);
        const msg = e?.response?.headers?.['x-error-message'];
        this.errorMessage = msg ? `Impossible: ${msg}` : 'Impossible de valider l\'ajustement.';
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
