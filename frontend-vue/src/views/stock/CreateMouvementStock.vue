<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Nouveau Mouvement de Stock</h5>
            <router-link to="/stock/mouvements" class="btn btn-secondary">
              <i class="ti ti-arrow-left"></i> Retour
            </router-link>
          </div>

          <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ errorMessage }}
            <button type="button" class="btn-close" @click="errorMessage = ''"></button>
          </div>

          <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
            {{ successMessage }}
            <button type="button" class="btn-close" @click="successMessage = ''"></button>
          </div>

          <form @submit.prevent="handleSubmit">
            <div class="row">
              <div class="col-md-4 mb-3">
                <label class="form-label">Type <span class="text-danger">*</span></label>
                <select class="form-select" v-model="form.type" required>
                  <option value="">Sélectionner</option>
                  <option value="ENTREE">Entrée</option>
                  <option value="SORTIE">Sortie</option>
                  <option value="TRANSFERT">Transfert</option>
                  <option value="AJUSTEMENT">Ajustement</option>
                </select>
              </div>

              <div class="col-md-4 mb-3">
                <label class="form-label">Dépôt (source) <span class="text-danger">*</span></label>
                <select class="form-select" v-model.number="form.depotId" required>
                  <option value="">Sélectionner</option>
                  <option v-for="d in depots" :key="d.id" :value="d.id">{{ d.nom }}</option>
                </select>
              </div>

              <div class="col-md-4 mb-3">
                <label class="form-label">Emplacement (source) <span class="text-danger">*</span></label>
                <select class="form-select" v-model.number="form.emplacementId" required>
                  <option value="">Sélectionner</option>
                  <option v-for="e in emplacementsFiltres" :key="e.id" :value="e.id">{{ e.code }}</option>
                </select>
                <small class="form-text text-muted">Obligatoire (Option A)</small>
              </div>
            </div>

            <div v-if="form.type === 'TRANSFERT'" class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Dépôt destination <span class="text-danger">*</span></label>
                <select class="form-select" v-model.number="form.depotDestinationId" required>
                  <option value="">Sélectionner</option>
                  <option v-for="d in depots" :key="d.id" :value="d.id">{{ d.nom }}</option>
                </select>
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label">Emplacement destination <span class="text-danger">*</span></label>
                <select class="form-select" v-model.number="form.emplacementDestinationId" required>
                  <option value="">Sélectionner</option>
                  <option v-for="e in emplacementsDestinationFiltres" :key="e.id" :value="e.id">{{ e.code }}</option>
                </select>
              </div>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Référence document</label>
                <input class="form-control" v-model="form.referenceDocument" placeholder="Ex: BL-0001, FACT-2026-01" />
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Motif</label>
                <input class="form-control" v-model="form.motif" placeholder="Motif / commentaire" />
              </div>
            </div>

            <hr />

            <div class="d-flex justify-content-between align-items-center mb-2">
              <h6 class="mb-0">Lignes</h6>
              <button type="button" class="btn btn-sm btn-outline-primary" @click="addLine">
                <i class="ti ti-plus"></i> Ajouter
              </button>
            </div>

            <div class="table-responsive">
              <table class="table table-bordered align-middle">
                <thead class="table-light">
                  <tr>
                    <th style="width: 28%">Article <span class="text-danger">*</span></th>
                    <th style="width: 12%">Qté <span class="text-danger">*</span></th>
                    <th style="width: 18%">Coût unitaire</th>
                    <th style="width: 28%">Lot</th>
                    <th style="width: 14%">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(l, idx) in lignes" :key="idx" :class="{ 'table-info': activeLineIndex === idx && form.type === 'SORTIE' }" @click="activeLineIndex = idx">
                    <td>
                      <select class="form-select" v-model.number="l.articleId" required @change="handleArticleChange(l, idx)">
                        <option value="">Sélectionner</option>
                        <option v-for="a in articles" :key="a.id" :value="a.id">{{ a.code }} - {{ a.nom }}</option>
                      </select>
                      <small v-if="isTraceableLot(l.articleId)" class="text-muted d-block">Lot obligatoire pour cet article</small>
                    </td>
                    <td>
                      <input type="number" class="form-control" v-model.number="l.quantite" min="1" required />
                    </td>
                    <td>
                      <input type="number" class="form-control" v-model.number="l.coutUnitaire" min="0" step="0.01" />
                    </td>
                    <td>
                      <select class="form-select" v-model.number="l.lotId" :required="isTraceableLot(l.articleId)">
                        <option value="">—</option>
                        <option v-for="lot in lotsFiltres(l.articleId)" :key="lot.id" :value="lot.id">
                          {{ lot.numeroLot }}
                        </option>
                      </select>
                    </td>
                    <td>
                      <button type="button" class="btn btn-sm btn-outline-danger" @click="removeLine(idx)">
                        <i class="ti ti-trash"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Détails du Stock Disponible (pour Sortie) -->
            <div v-if="form.type === 'SORTIE' && stockDetails" class="mt-4 animate-slide-in">
              <div class="card bg-light-info border-info">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-center mb-3">
                    <h6 class="card-title text-info mb-0">
                      <i class="ti ti-info-circle me-1"></i>
                      État du stock : <strong>{{ stockDetails.article?.nom }}</strong> 
                      ({{ stockDetails.depot?.nom }} - {{ stockDetails.emplacement?.code }})
                    </h6>
                    <button type="button" class="btn-close" @click="stockDetails = null"></button>
                  </div>

                  <!-- Récapitulatif -->
                  <div class="row g-3 mb-4">
                    <div class="col-md-3">
                      <div class="p-3 bg-white rounded border">
                        <small class="text-muted d-block">Quantité Totale</small>
                        <span class="fs-5 fw-bold text-dark">{{ stockDetails.totalQuantite }}</span>
                      </div>
                    </div>
                    <div class="col-md-3">
                      <div class="p-3 bg-white rounded border">
                        <small class="text-muted d-block">Valeur Totale</small>
                        <span class="fs-5 fw-bold text-dark">{{ formatCurrency(stockDetails.totalValeur) }}</span>
                      </div>
                    </div>
                    <div class="col-md-3">
                      <div class="p-3 bg-white rounded border">
                        <small class="text-muted d-block">Méthode</small>
                        <span class="badge bg-primary fs-7">{{ stockDetails.methode }}</span>
                      </div>
                    </div>
                    <div class="col-md-3">
                      <div class="p-3 bg-white rounded border">
                        <small class="text-muted d-block">Dernière MAJ</small>
                        <span class="text-dark">{{ formatDate(stockDetails.lastMaj) }}</span>
                      </div>
                    </div>
                  </div>

                  <!-- Historique des entrées -->
                  <h6 class="mb-2 fs-3 fw-semibold">Historique détaillé des entrées</h6>
                  <div class="table-responsive bg-white rounded border mb-4">
                    <table class="table table-sm table-hover mb-0">
                      <thead class="table-light">
                        <tr>
                          <th>Date Entrée/MAJ</th>
                          <th class="text-end">Prix d'Achat</th>
                          <th class="text-end">Qté Dispo</th>
                          <th class="text-end">Valeur</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr v-for="s in stockDetails.history" :key="s.id">
                          <td>{{ formatDate(s.dateMaj) }}</td>
                          <td class="text-end">{{ formatCurrency(s.coutUnitaire) }}</td>
                          <td class="text-end">{{ s.quantite }}</td>
                          <td class="text-end">{{ formatCurrency(s.valeur) }}</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>

                  <!-- Simulation de consommation -->
                  <div v-if="stockDetails.simulation && stockDetails.simulation.length > 0" class="alert alert-warning mb-0 border-warning">
                    <h6 class="alert-heading fw-bold mb-2">
                      <i class="ti ti-alert-triangle me-1"></i>
                      Simulation de sortie ({{ stockDetails.methode }})
                    </h6>
                    <div class="d-flex flex-wrap gap-2">
                      <div v-for="(sim, idx) in stockDetails.simulation" :key="idx" class="badge bg-white text-dark border p-2">
                        <span class="fw-bold">{{ sim.quantite }}</span> unité(s) à 
                        <span class="text-primary fw-bold">{{ formatCurrency(sim.prix) }}</span>
                        <span class="text-muted ms-1">({{ formatDate(sim.date) }})</span>
                      </div>
                    </div>
                    <div class="mt-2 pt-2 border-top border-warning-subtle small">
                      Total simulé : <strong>{{ formatCurrency(stockDetails.totalSimule) }}</strong>
                    </div>
                  </div>
                  <div v-else-if="lignes[activeLineIndex]?.quantite > stockDetails.totalQuantite" class="alert alert-danger mb-0">
                    Stock insuffisant pour cette sortie.
                  </div>
                </div>
              </div>
            </div>

            <div class="d-flex justify-content-end gap-2 mt-4">
              <router-link to="/stock/mouvements" class="btn btn-secondary">Annuler</router-link>
              <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
                <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                <i v-else class="ti ti-device-floppy me-1"></i>
                {{ isSubmitting ? 'Enregistrement...' : 'Enregistrer' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../../layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'CreateMouvementStock',
  components: { MainLayout },
  data() {
    return {
      depots: [],
      emplacements: [],
      articles: [],
      lots: [],
      lignes: [{ articleId: '', quantite: 1, coutUnitaire: null, lotId: '' }],
      form: {
        type: '',
        depotId: '',
        emplacementId: '',
        depotDestinationId: '',
        emplacementDestinationId: '',
        referenceDocument: '',
        motif: ''
      },
      isSubmitting: false,
      errorMessage: '',
      successMessage: '',
      activeLineIndex: null,
      stockDetails: null // { summary: {}, history: [], simulation: [] }
    };
  },
  computed: {
    emplacementsFiltres() {
      if (!this.form.depotId) return this.emplacements;
      return this.emplacements.filter(e => e.depot?.id === this.form.depotId || e.depotId === this.form.depotId);
    },
    emplacementsDestinationFiltres() {
      if (!this.form.depotDestinationId) return this.emplacements;
      return this.emplacements.filter(e => e.depot?.id === this.form.depotDestinationId || e.depotId === this.form.depotDestinationId);
    }
  },
  watch: {
    'form.type'(newVal) {
      if (newVal !== 'SORTIE') {
        this.stockDetails = null;
      }
      this.updateAllCouts();
    },
    'form.depotId'() {
      this.updateAllCouts();
      if (this.activeLineIndex !== null) {
        this.fetchStockDetails(this.lignes[this.activeLineIndex], this.activeLineIndex);
      }
    },
    'form.emplacementId'() {
      this.updateAllCouts();
      if (this.activeLineIndex !== null) {
        this.fetchStockDetails(this.lignes[this.activeLineIndex], this.activeLineIndex);
      }
    },
    lignes: {
      deep: true,
      handler() {
        if (this.stockDetails && this.activeLineIndex !== null) {
          this.runSimulation();
        }
      }
    }
  },
  mounted() {
    this.loadData();
  },
  methods: {
    addLine() {
      this.lignes.push({ articleId: '', quantite: 1, coutUnitaire: null, lotId: '' });
      this.activeLineIndex = this.lignes.length - 1;
      this.stockDetails = null;
    },
    removeLine(idx) {
      if (this.lignes.length === 1) return;
      this.lignes.splice(idx, 1);
      if (this.activeLineIndex === idx) {
        this.activeLineIndex = null;
        this.stockDetails = null;
      } else if (this.activeLineIndex > idx) {
        this.activeLineIndex--;
      }
    },
    async handleArticleChange(ligne, idx) {
      if (!ligne.articleId) {
        ligne.coutUnitaire = null;
        if (this.activeLineIndex === idx) this.stockDetails = null;
        return;
      }
      
      this.activeLineIndex = idx;

      // Si c'est une entrée, on peut suggérer le prix d'achat par défaut
      if (this.form.type === 'ENTREE') {
        const art = this.articles.find(a => a.id === ligne.articleId);
        if (art) ligne.coutUnitaire = art.prixAchat;
        return;
      }

      // Si c'est une sortie, on affiche les détails du stock
      if (this.form.type === 'SORTIE') {
        await this.fetchStockDetails(ligne, idx);
      }

      // Si c'est une sortie, transfert ou ajustement, on calcule selon la méthode
      await this.updateCoutUnitaire(ligne);
    },
    async fetchStockDetails(ligne, idx) {
      if (!ligne.articleId || !this.form.depotId || this.form.type !== 'SORTIE') return;

      try {
        const params = {
          articleId: ligne.articleId,
          depotId: this.form.depotId
        };
        if (this.form.emplacementId) params.emplacementId = this.form.emplacementId;

        const res = await axios.get('/api/stocks/details', { params });
        const history = res.data || [];
        
        const article = this.articles.find(a => a.id === ligne.articleId);
        const depot = this.depots.find(d => d.id === this.form.depotId);
        const emplacement = this.emplacements.find(e => e.id === this.form.emplacementId);

        this.stockDetails = {
          article,
          depot,
          emplacement,
          history: [...history].sort((a, b) => new Date(a.dateMaj) - new Date(b.dateMaj)), // Par défaut ordre chronologique
          totalQuantite: history.reduce((sum, s) => sum + s.quantite, 0),
          totalValeur: history.reduce((sum, s) => sum + s.valeur, 0),
          methode: article?.methodeValorisation || 'CUMP',
          lastMaj: history.length > 0 ? Math.max(...history.map(s => new Date(s.dateMaj))) : null,
          simulation: [],
          totalSimule: 0
        };

        this.runSimulation();
      } catch (e) {
        console.error('Erreur chargement détails stock:', e);
      }
    },
    runSimulation() {
      if (!this.stockDetails || this.activeLineIndex === null) return;
      
      const targetQty = this.lignes[this.activeLineIndex].quantite || 0;
      if (targetQty <= 0) {
        this.stockDetails.simulation = [];
        this.stockDetails.totalSimule = 0;
        return;
      }

      let simulation = [];
      let remaining = targetQty;
      let historyCopy = [...this.stockDetails.history];

      // Appliquer le tri selon la méthode
      if (this.stockDetails.methode === 'FIFO') {
        historyCopy.sort((a, b) => new Date(a.dateMaj) - new Date(b.dateMaj));
      } else if (this.stockDetails.methode === 'LIFO') {
        historyCopy.sort((a, b) => new Date(b.dateMaj) - new Date(a.dateMaj));
      } else if (this.stockDetails.methode === 'CUMP') {
        // Pour CUMP, on consomme proportionnellement ou on affiche juste le prix moyen
        const avgPrice = this.stockDetails.totalQuantite > 0 
          ? this.stockDetails.totalValeur / this.stockDetails.totalQuantite 
          : 0;
        this.stockDetails.simulation = [{
          quantite: targetQty,
          prix: avgPrice,
          date: new Date()
        }];
        this.stockDetails.totalSimule = targetQty * avgPrice;
        return;
      }

      for (let s of historyCopy) {
        if (remaining <= 0) break;
        let take = Math.min(s.quantite, remaining);
        simulation.push({
          quantite: take,
          prix: s.coutUnitaire,
          date: s.dateMaj
        });
        remaining -= take;
      }

      this.stockDetails.simulation = simulation;
      this.stockDetails.totalSimule = simulation.reduce((sum, s) => sum + (s.quantite * s.prix), 0);
    },
    formatCurrency(val) {
      if (val === null || val === undefined) return '0 Ar';
      return new Intl.NumberFormat('fr-MG', { style: 'currency', currency: 'MGA' }).format(val).replace('MGA', 'Ar');
    },
    formatDate(date) {
      if (!date) return '—';
      return new Date(date).toLocaleString('fr-FR');
    },
    async updateCoutUnitaire(ligne) {
      if (!ligne.articleId || !this.form.depotId || this.form.type === 'ENTREE') return;

      try {
        const params = {
          articleId: ligne.articleId,
          depotId: this.form.depotId
        };
        if (this.form.emplacementId) params.emplacementId = this.form.emplacementId;

        const res = await axios.get('/api/stocks/calculer-cout-unitaire', { params });
        ligne.coutUnitaire = res.data;
      } catch (e) {
        console.error('Erreur calcul coût unitaire:', e);
      }
    },
    updateAllCouts() {
      if (this.form.type === 'ENTREE') return;
      this.lignes.forEach(l => {
        if (l.articleId) this.updateCoutUnitaire(l);
      });
    },
    isTraceableLot(articleId) {
      const a = this.articles.find(x => x.id === articleId);
      return !!a?.traceableLot;
    },
    lotsFiltres(articleId) {
      if (!articleId) return this.lots;
      return this.lots.filter(l => l.article?.id === articleId || l.articleId === articleId);
    },
    async loadData() {
      try {
        const [depotsRes, emplRes, articlesRes, lotsRes] = await Promise.all([
          axios.get('/api/depots'),
          axios.get('/api/emplacements'),
          axios.get('/api/articles'),
          axios.get('/api/lots')
        ]);
        this.depots = depotsRes.data;
        this.emplacements = emplRes.data;
        this.articles = articlesRes.data;
        this.lots = lotsRes.data;
      } catch (e) {
        console.error('Erreur chargement données mouvement:', e);
        this.errorMessage = 'Erreur lors du chargement des données (dépôts, emplacements, articles, lots).';
      }
    },
    buildPayload() {
      const authRaw = localStorage.getItem('user');
      const auth = authRaw ? JSON.parse(authRaw) : null;
      const user = auth?.user;

      const mouvement = {
        type: this.form.type,
        referenceDocument: this.form.referenceDocument || null,
        motif: this.form.motif || null,
        depot: this.form.depotId ? { id: this.form.depotId } : null,
        emplacement: this.form.emplacementId ? { id: this.form.emplacementId } : null,
        depotDestination: this.form.depotDestinationId ? { id: this.form.depotDestinationId } : null,
        emplacementDestination: this.form.emplacementDestinationId ? { id: this.form.emplacementDestinationId } : null,
        utilisateur: user?.id ? { id: user.id } : null,
        lignes: this.lignes.map(l => ({
          article: l.articleId ? { id: l.articleId } : null,
          quantite: l.quantite,
          coutUnitaire: l.coutUnitaire !== null && l.coutUnitaire !== '' ? l.coutUnitaire : null,
          lot: l.lotId ? { id: l.lotId } : null
        }))
      };

      return mouvement;
    },
    validateForm() {
      if (!this.form.type) return 'Type obligatoire.';
      if (!this.form.depotId) return 'Dépôt obligatoire.';
      if (!this.form.emplacementId) return 'Emplacement obligatoire.';
      if (this.form.type === 'TRANSFERT') {
        if (!this.form.depotDestinationId) return 'Dépôt destination obligatoire.';
        if (!this.form.emplacementDestinationId) return 'Emplacement destination obligatoire.';
      }

      if (!this.lignes.length) return 'Au moins une ligne est requise.';
      for (const l of this.lignes) {
        if (!l.articleId) return 'Article obligatoire dans chaque ligne.';
        if (!l.quantite || l.quantite <= 0) return 'Quantité invalide.';
        if (this.isTraceableLot(l.articleId) && !l.lotId) return 'Lot obligatoire pour un article traçable.';
      }

      return null;
    },
    async handleSubmit() {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.successMessage = '';

      const err = this.validateForm();
      if (err) {
        this.errorMessage = err;
        this.isSubmitting = false;
        return;
      }

      try {
        const payload = this.buildPayload();
        const res = await axios.post('/api/mouvements-stock', payload);
        this.successMessage = 'Mouvement créé avec succès.';

        setTimeout(() => {
          this.$router.push(`/stock/mouvements/${res.data.id}`);
        }, 800);
      } catch (e) {
        console.error('Erreur création mouvement:', e);
        this.errorMessage = 'Erreur lors de la création du mouvement.';
      } finally {
        this.isSubmitting = false;
      }
    }
  }
};
</script>

<style scoped>
.form-label {
  font-weight: 500;
}

.text-danger {
  color: #dc3545;
}

.gap-2 {
  gap: 0.5rem;
}

.bg-light-info {
  background-color: #e8f7ff !important;
}

.border-info {
  border-color: #5d87ff !important;
}

.text-info {
  color: #5d87ff !important;
}

.animate-slide-in {
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.table-info {
  background-color: #e8f7ff !important;
}

.fs-7 {
  font-size: 0.8rem;
}
</style>
