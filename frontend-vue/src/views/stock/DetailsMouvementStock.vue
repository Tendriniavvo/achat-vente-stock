<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Détails Mouvement</h5>
            <router-link to="/stock/mouvements" class="btn btn-secondary">
              <i class="ti ti-arrow-left"></i> Retour
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

          <div v-else-if="!mouvement" class="text-muted">Mouvement introuvable.</div>

          <div v-else>
            <div class="row mb-3">
              <div class="col-md-4"><strong>Référence:</strong> {{ mouvement.reference || ('#' + mouvement.id) }}</div>
              <div class="col-md-4"><strong>Type:</strong> {{ (mouvement.type || '').toUpperCase() }}</div>
              <div class="col-md-4">
                <strong>Statut:</strong>
                <span :class="badgeClass(mouvement.statut)">{{ mouvement.statut || 'BROUILLON' }}</span>
              </div>
            </div>

            <div class="row mb-3">
              <div class="col-md-6"><strong>Dépôt:</strong> {{ mouvement.depot?.nom || '—' }}</div>
              <div class="col-md-6"><strong>Emplacement:</strong> {{ mouvement.emplacement?.code || '—' }}</div>
            </div>

            <div v-if="(mouvement.type || '').toUpperCase() === 'TRANSFERT'" class="row mb-3">
              <div class="col-md-6"><strong>Dépôt destination:</strong> {{ mouvement.depotDestination?.nom || '—' }}</div>
              <div class="col-md-6"><strong>Emplacement destination:</strong> {{ mouvement.emplacementDestination?.code || '—' }}</div>
            </div>

            <div class="row mb-3">
              <div class="col-md-6"><strong>Date:</strong> {{ formatDate(mouvement.dateMouvement) }}</div>
              <div class="col-md-6"><strong>Réf doc:</strong> {{ mouvement.referenceDocument || '—' }}</div>
            </div>

            <div class="row mb-4">
              <div class="col-12"><strong>Motif:</strong> {{ mouvement.motif || '—' }}</div>
            </div>

            <div class="d-flex justify-content-end mb-3">
              <button
                class="btn btn-success"
                :disabled="(mouvement.statut || '').toUpperCase() === 'VALIDE'"
                @click="valider"
              >
                <i class="ti ti-check me-1"></i> Valider
              </button>
            </div>

            <h6>Lignes</h6>
            <div class="table-responsive">
              <table class="table table-bordered align-middle">
                <thead class="table-light">
                  <tr>
                    <th>Article</th>
                    <th>Qté</th>
                    <th>Coût unitaire</th>
                    <th>Lot</th>
                    <th>Emplacement</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="l in resolvedLines" :key="l.key">
                    <td>{{ l.articleLabel }}</td>
                    <td>{{ l.quantite }}</td>
                    <td>{{ l.coutUnitaire ?? '—' }}</td>
                    <td>{{ l.lotLabel }}</td>
                    <td>{{ l.emplacementLabel }}</td>
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

<script>
import MainLayout from '../../layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'DetailsMouvementStock',
  components: { MainLayout },
  data() {
    return {
      mouvement: null,
      isLoading: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  computed: {
    resolvedLines() {
      if (!this.mouvement) return [];
      const lines = this.mouvement.lignes && this.mouvement.lignes.length
        ? this.mouvement.lignes
        : [{
            article: this.mouvement.article,
            quantite: this.mouvement.quantite,
            coutUnitaire: this.mouvement.cout,
            lot: this.mouvement.lot,
            emplacement: this.mouvement.emplacement
          }];

      return lines.map((l, idx) => ({
        key: `${idx}-${l?.id || 'x'}`,
        articleLabel: l.article ? `${l.article.code || ''} ${l.article.nom ? '- ' + l.article.nom : ''}`.trim() : '—',
        quantite: l.quantite,
        coutUnitaire: l.coutUnitaire,
        lotLabel: l.lot?.numeroLot || '—',
        emplacementLabel: l.emplacement?.code || this.mouvement.emplacement?.code || '—'
      }));
    }
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
        const id = this.$route.params.id;
        const res = await axios.get(`/api/mouvements-stock/${id}`);
        this.mouvement = res.data;
      } catch (e) {
        console.error('Erreur chargement mouvement:', e);
        this.errorMessage = 'Erreur lors du chargement du mouvement.';
      } finally {
        this.isLoading = false;
      }
    },
    async valider() {
      if (!this.mouvement?.id) return;
      try {
        await axios.post(`/api/mouvements-stock/${this.mouvement.id}/valider`);
        this.successMessage = 'Mouvement validé.';
        await this.load();
        setTimeout(() => (this.successMessage = ''), 3000);
      } catch (e) {
        console.error('Erreur validation:', e);
        const msg = e?.response?.headers?.['x-error-message'];
        this.errorMessage = msg
          ? `Impossible de valider: ${msg}`
          : 'Impossible de valider le mouvement (vérifie emplacement, lots, stock disponible).';
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
