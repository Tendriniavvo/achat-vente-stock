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
                  <th style="width: 40px"></th>
                  <th>Article</th>
                  <th>Dépôt</th>
                  <th>Emplacement</th>
                  <th class="text-end">Quantité Totale</th>
                  <th class="text-end">Valeur Totale</th>
                  <th>Dernière MAJ</th>
                </tr>
              </thead>
              <tbody>
                <template v-for="g in groupedRows" :key="g.id">
                  <!-- Ligne Principale (Groupée) -->
                  <tr @click="toggleRow(g.id)" class="cursor-pointer main-row">
                    <td>
                      <button class="btn btn-sm btn-icon transition-rotate" :class="{ 'rotated': isExpanded(g.id) }">
                        <i class="ti ti-plus" v-if="!isExpanded(g.id)"></i>
                        <i class="ti ti-minus" v-else></i>
                      </button>
                    </td>
                    <td>
                      <strong>{{ g.article?.code || '—' }}</strong>
                      <div class="text-muted small">{{ g.article?.nom || '' }}</div>
                    </td>
                    <td>{{ g.depot?.nom || '—' }}</td>
                    <td>{{ g.emplacement?.code || '—' }}</td>
                    <td class="text-end fw-bold">{{ g.totalQuantite }}</td>
                    <td class="text-end fw-bold">{{ formatCurrency(g.totalValeur) }}</td>
                    <td>{{ formatDate(g.lastMaj) }}</td>
                  </tr>

                  <!-- Lignes de Détails (Conditionnelles) -->
                  <tr v-if="isExpanded(g.id)" class="detail-row">
                    <td colspan="7" class="p-0 border-0">
                      <div class="expand-container p-3 bg-light-subtle">
                        <div class="ms-4 detail-table-wrapper">
                          <table class="table table-sm table-bordered mb-0 bg-white shadow-sm">
                            <thead class="table-light small">
                              <tr>
                                <th>Date Entrée / MAJ</th>
                                <th class="text-end">Prix d'Achat</th>
                                <th class="text-end">Quantité</th>
                                <th class="text-end">Valeur</th>
                              </tr>
                            </thead>
                            <tbody>
                              <tr v-for="d in g.details" :key="d.id">
                                <td class="small">{{ formatDate(d.dateMaj) }}</td>
                                <td class="text-end small">{{ formatCurrency(d.coutUnitaire) }}</td>
                                <td class="text-end small">{{ d.quantite }}</td>
                                <td class="text-end small">{{ formatCurrency(d.valeur) }}</td>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </td>
                  </tr>
                </template>
              </tbody>
            </table>

            <div v-if="groupedRows.length === 0" class="text-center text-muted py-4">
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
      expandedRows: [], // IDs des groupes étendus (format: articleId-depotId-emplacementId)
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
    groupedRows() {
      const a = (this.filters.article || '').toLowerCase().trim();
      const d = (this.filters.depot || '').toLowerCase().trim();
      const e = (this.filters.emplacement || '').toLowerCase().trim();
      const qMin = this.filters.quantiteMin === '' || this.filters.quantiteMin === null
        ? null
        : Number(this.filters.quantiteMin);

      // 1. Filtrer les lignes individuelles
      const filtered = (this.rows || []).filter(s => {
        const articleStr = `${s.article?.code || ''} ${s.article?.nom || ''}`.toLowerCase();
        const depotStr = `${s.depot?.nom || ''}`.toLowerCase();
        const emplStr = `${s.emplacement?.code || ''}`.toLowerCase();

        if (a && !articleStr.includes(a)) return false;
        if (d && !depotStr.includes(d)) return false;
        if (e && !emplStr.includes(e)) return false;
        return true;
      });

      // 2. Grouper par Article + Dépôt + Emplacement
      const groups = {};
      filtered.forEach(s => {
        const key = `${s.article?.id || 0}-${s.depot?.id || 0}-${s.emplacement?.id || 0}`;
        if (!groups[key]) {
          groups[key] = {
            id: key,
            article: s.article,
            depot: s.depot,
            emplacement: s.emplacement,
            totalQuantite: 0,
            totalValeur: 0,
            lastMaj: s.dateMaj,
            details: []
          };
        }
        groups[key].totalQuantite += (s.quantite || 0);
        groups[key].totalValeur += (s.valeur || 0);
        groups[key].details.push(s);
        
        // Garder la date de mise à jour la plus récente
        if (new Date(s.dateMaj) > new Date(groups[key].lastMaj)) {
          groups[key].lastMaj = s.dateMaj;
        }
      });

      // 3. Convertir en tableau et appliquer le filtre de quantité min sur le total
      return Object.values(groups).filter(g => {
        if (qMin !== null && g.totalQuantite < qMin) return false;
        return true;
      }).sort((x, y) => x.article?.code?.localeCompare(y.article?.code));
    }
  },
  mounted() {
    this.load();
  },
  methods: {
    toggleRow(rowId) {
      const index = this.expandedRows.indexOf(rowId);
      if (index > -1) {
        this.expandedRows.splice(index, 1);
      } else {
        this.expandedRows.push(rowId);
      }
    },
    isExpanded(rowId) {
      return this.expandedRows.includes(rowId);
    },
    formatCurrency(value) {
      if (value === null || value === undefined) return '—';
      return new Intl.NumberFormat('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value) + ' Ar';
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

.cursor-pointer {
  cursor: pointer;
}

.main-row:hover {
  background-color: rgba(93, 135, 255, 0.05);
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  padding: 0;
  border-radius: 4px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  color: #5d87ff;
}

.transition-rotate {
  transition: transform 0.3s ease;
}

.rotated {
  transform: rotate(180deg);
  background: #5d87ff;
  color: white;
  border-color: #5d87ff;
}

.expand-container {
  animation: slideDown 0.3s ease-out;
  border-left: 4px solid #5d87ff;
  margin-left: 20px;
}

.detail-table-wrapper {
  overflow: hidden;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.bg-light-subtle {
  background-color: #fcfdfe !important;
}

.table-sm th {
  font-weight: 600;
  color: #5a6a85;
}
</style>
