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
                  <tr v-for="(l, idx) in lignes" :key="idx">
                    <td>
                      <select class="form-select" v-model.number="l.articleId" required>
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
      successMessage: ''
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
  mounted() {
    this.loadData();
  },
  methods: {
    addLine() {
      this.lignes.push({ articleId: '', quantite: 1, coutUnitaire: null, lotId: '' });
    },
    removeLine(idx) {
      if (this.lignes.length === 1) return;
      this.lignes.splice(idx, 1);
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
</style>
