<template>
  <MainLayout>
    <div class="container-fluid print-container">
      <!-- Actions (Hidden during print) -->
      <div class="d-flex justify-content-between align-items-center mb-4 d-print-none">
        <div>
          <button class="btn btn-outline-secondary me-2" @click="$router.back()">
            <i class="ti ti-arrow-left"></i> Retour
          </button>
          <h5 class="card-title fw-semibold d-inline-block mb-0">Détails du Bon de Réception</h5>
        </div>
        <div>
          <button class="btn btn-info me-2" @click="creerFacture" :disabled="isGenerating">
            <span v-if="isGenerating" class="spinner-border spinner-border-sm me-1"></span>
            <i v-else class="ti ti-file-invoice me-1"></i> Générer Facture
          </button>
          <button class="btn btn-primary" @click="imprimerBR">
            <i class="ti ti-printer me-1"></i> Imprimer / PDF
          </button>
        </div>
      </div>

      <div v-if="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="mt-2">Chargement des détails...</p>
      </div>

      <div v-else-if="br" class="card shadow-sm">
        <div class="card-body">
          <!-- Header for Print -->
          <div class="d-none d-print-block mb-4 text-center">
            <h2 class="fw-bold">BON DE RÉCEPTION</h2>
            <h4 class="text-primary">{{ br.reference }}</h4>
            <hr>
          </div>

          <div class="row mb-4">
            <div class="col-md-6 col-6">
              <div class="mb-3">
                <label class="text-muted small text-uppercase fw-bold">Référence BR</label>
                <div class="fs-5 fw-bold text-primary">{{ br.reference }}</div>
              </div>
              <div class="mb-3">
                <label class="text-muted small text-uppercase fw-bold">BC Source</label>
                <div class="fs-6">{{ br.bonCommande?.reference || '-' }}</div>
              </div>
              <div class="mb-3">
                <label class="text-muted small text-uppercase fw-bold">Fournisseur</label>
                <div class="fs-6">{{ br.bonCommande?.fournisseur?.nom || '-' }}</div>
              </div>
            </div>
            <div class="col-md-6 col-6 text-md-end">
              <div class="mb-3">
                <label class="text-muted small text-uppercase fw-bold">Date Réception</label>
                <div class="fs-6">{{ formatDate(br.dateReception) }}</div>
              </div>
              <div class="mb-3">
                <label class="text-muted small text-uppercase fw-bold">Dépôt</label>
                <div class="fs-6">{{ br.depot?.nom || '-' }}</div>
              </div>
              <div class="mb-3">
                <label class="text-muted small text-uppercase fw-bold">Magasinier</label>
                <div class="fs-6">{{ br.utilisateur?.nom || '-' }}</div>
              </div>
              <div class="mb-3">
                <label class="text-muted small text-uppercase fw-bold">Statut</label>
                <div>
                  <span :class="getStatutClass(br.statut)">{{ formatStatut(br.statut) }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="table-responsive">
            <table class="table table-bordered align-middle">
              <thead class="table-light">
                <tr>
                  <th style="width: 40%">Article</th>
                  <th class="text-center">Qté Reçue</th>
                  <th>Lot / Série</th>
                  <th>Expiration</th>
                  <th>Observations</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="ligne in lignes" :key="ligne.id">
                  <td>
                    <div class="fw-bold">{{ ligne.article?.nom }}</div>
                    <small class="text-muted">{{ ligne.article?.code }}</small>
                  </td>
                  <td class="text-center">
                    <span class="fs-5 fw-bold">{{ ligne.quantiteRecue }}</span>
                  </td>
                  <td>{{ ligne.lot?.numeroLot || '-' }}</td>
                  <td>{{ ligne.lot?.dateExpiration ? formatDateSimple(ligne.lot.dateExpiration) : '-' }}</td>
                  <td>{{ ligne.ecart || '-' }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Traçabilité (Audit Log) -->
          <div v-if="audits.length > 0" class="mt-4 d-print-none">
            <h6 class="fw-bold mb-3">Traçabilité & Suivi</h6>
            <div class="table-responsive">
              <table class="table table-sm table-hover border">
                <thead class="table-light">
                  <tr>
                    <th>Date</th>
                    <th>Utilisateur</th>
                    <th>Action</th>
                    <th>Détails</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="audit in audits" :key="audit.id">
                    <td>{{ formatDate(audit.dateAction) }}</td>
                    <td>{{ audit.utilisateur?.prenom }} {{ audit.utilisateur?.nom }}</td>
                    <td><span class="badge bg-light text-dark">{{ audit.action }}</span></td>
                    <td>{{ audit.details }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div class="mt-5 d-none d-print-block">
            <div class="row">
              <div class="col-6 text-center">
                <p class="fw-bold">Signature Magasinier</p>
                <div style="height: 80px;"></div>
                <p>{{ br.utilisateur?.nom }}</p>
              </div>
              <div class="col-6 text-center">
                <p class="fw-bold">Cachet Dépôt</p>
                <div style="height: 80px;"></div>
                <p>{{ br.depot?.nom }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="alert alert-warning">
        <i class="ti ti-alert-triangle me-2"></i> Impossible de charger les détails de cette réception.
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '@/layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'DetailsReception',
  components: {
    MainLayout
  },
  data() {
    return {
      br: null,
      lignes: [],
      audits: [],
      isLoading: true,
      isGenerating: false
    };
  },
  computed: {
    brId() {
      return this.$route.params.id;
    }
  },
  async mounted() {
    await this.loadDetails();
  },
  methods: {
    async loadDetails() {
      this.isLoading = true;
      try {
        const [brRes, linesRes] = await Promise.all([
          axios.get(`/api/receptions/${this.brId}`),
          axios.get(`/api/receptions/${this.brId}/lignes`)
        ]);
        
        this.br = brRes.data;
        this.lignes = linesRes.data;
        
        if (this.br?.reference) {
          this.loadAudits(this.br.reference);
        }
      } catch (error) {
        console.error('Erreur chargement détails réception:', error);
      } finally {
        this.isLoading = false;
      }
    },
    async loadAudits(reference) {
      try {
        const response = await axios.get(`/api/audits/module/STOCK/ref/${reference}`);
        if (response.status === 200) {
          this.audits = response.data;
        }
      } catch (error) {
        console.warn('Erreur chargement audits:', error);
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '-';
      return new Date(dateStr).toLocaleString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    formatDateSimple(dateStr) {
      if (!dateStr) return '-';
      return new Date(dateStr).toLocaleDateString('fr-FR');
    },
    formatStatut(statut) {
      if (!statut) return '-';
      const map = {
        'complete': 'Complet',
        'partielle': 'Partiel',
        'valide': 'Validé'
      };
      return map[statut.toLowerCase()] || statut;
    },
    getStatutClass(statut) {
      const s = statut?.toLowerCase();
      if (s === 'complete' || s === 'valide') return 'badge bg-success';
      if (s === 'partielle') return 'badge bg-warning';
      return 'badge bg-secondary';
    },
    async creerFacture() {
      if (!confirm('Voulez-vous générer une facture à partir de cette réception ?')) return;
      
      this.isGenerating = true;
      try {
        const response = await axios.post(`/api/factures-fournisseur/generer/${this.brId}`);
        if (response.status === 200) {
          alert('Facture générée avec succès !');
          this.$router.push('/factures');
        }
      } catch (error) {
        console.error('Erreur génération facture:', error);
        alert('Erreur lors de la génération de la facture. Elle existe peut-être déjà.');
      } finally {
        this.isGenerating = false;
      }
    },
    imprimerBR() {
      window.print();
    }
  }
}
</script>

<style scoped>
@media print {
  .d-print-none {
    display: none !important;
  }
  .d-print-block {
    display: block !important;
  }
  .card {
    border: none !important;
    box-shadow: none !important;
  }
  .container-fluid {
    padding: 0 !important;
  }
  body {
    background-color: white !important;
  }
}
</style>
