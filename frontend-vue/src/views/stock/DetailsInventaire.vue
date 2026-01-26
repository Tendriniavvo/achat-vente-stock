<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Détails Inventaire</h5>
            <router-link to="/stock/inventaires" class="btn btn-secondary">
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

          <div v-else-if="!inventaire" class="text-muted">Inventaire introuvable.</div>

          <div v-else>
            <div class="row mb-3">
              <div class="col-md-4"><strong>Référence:</strong> {{ inventaire.reference || ('#' + inventaire.id) }}</div>
              <div class="col-md-4"><strong>Type:</strong> {{ inventaire.type || '—' }}</div>
              <div class="col-md-4">
                <strong>Statut:</strong>
                <span :class="badgeClass(inventaire.statut)">{{ inventaire.statut || 'en_cours' }}</span>
              </div>
            </div>

            <div class="row mb-3">
              <div class="col-md-6"><strong>Dépôt:</strong> {{ inventaire.depot?.nom || '—' }}</div>
              <div class="col-md-6"><strong>Lancé par:</strong> {{ userLabel(inventaire.utilisateurLancement) }}</div>
            </div>

            <div class="row mb-4">
              <div class="col-md-6"><strong>Date début:</strong> {{ formatDate(inventaire.dateDebut) }}</div>
              <div class="col-md-6"><strong>Date fin:</strong> {{ formatDate(inventaire.dateFin) }}</div>
            </div>

            <div class="d-flex flex-wrap justify-content-end gap-2 mb-3">
              <button class="btn btn-outline-primary" @click="genererLignes" :disabled="isActionLoading">
                <span v-if="isActionLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                Générer lignes
              </button>
              <button class="btn btn-outline-secondary" @click="terminer" :disabled="isActionLoading || (inventaire.statut || '').toLowerCase() !== 'en_cours'">
                Terminer
              </button>
              <button class="btn btn-outline-warning" @click="genererAjustements" :disabled="isActionLoading">
                Générer ajustements
              </button>
              <button class="btn btn-outline-dark" @click="refresh" :disabled="isActionLoading">Rafraîchir</button>
            </div>

            <h6>Lignes d'inventaire</h6>
            <div class="table-responsive">
              <table class="table table-bordered align-middle">
                <thead class="table-light">
                  <tr>
                    <th>Article</th>
                    <th>Emplacement</th>
                    <th class="text-end">Théorique</th>
                    <th style="width: 180px">Physique</th>
                    <th class="text-end">Écart</th>
                    <th>Motif</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="l in lignes" :key="l.id">
                    <td>
                      <strong>{{ l.article?.code || '—' }}</strong>
                      <div class="text-muted small">{{ l.article?.nom || '' }}</div>
                    </td>
                    <td>{{ l.emplacement?.code || '—' }}</td>
                    <td class="text-end">{{ l.quantiteTheorique ?? 0 }}</td>
                    <td>
                      <input
                        type="number"
                        class="form-control form-control-sm"
                        min="0"
                        v-model.number="editPhysique[l.id]"
                      />
                    </td>
                    <td class="text-end">{{ calcEcart(l) }}</td>
                    <td>
                      <input class="form-control form-control-sm" v-model="editMotif[l.id]" placeholder="Motif écart" />
                    </td>
                    <td>
                      <button class="btn btn-sm btn-outline-success" @click="savePhysique(l)" :disabled="isActionLoading">
                        <i class="ti ti-device-floppy"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div v-if="lignes.length === 0" class="text-center text-muted py-4">
                Aucune ligne. Clique sur "Générer lignes".
              </div>
            </div>

            <h6 class="mt-4">Ajustements</h6>
            <div class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                  <tr>
                    <th>Référence</th>
                    <th>Article</th>
                    <th>Emplacement</th>
                    <th>Lot</th>
                    <th>Type</th>
                    <th class="text-end">Quantité</th>
                    <th>Statut</th>
                    <th>Motif</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="a in ajustements" :key="a.id">
                    <td><strong>{{ a.reference || ('#' + a.id) }}</strong></td>
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
                    <td><span :class="badgeClassAjust(a.statut)">{{ a.statut || 'propose' }}</span></td>
                    <td>{{ a.motif || '—' }}</td>
                    <td>
                      <button
                        class="btn btn-sm btn-outline-success"
                        @click="validerAjustement(a)"
                        :disabled="isActionLoading || (a.statut || '').toLowerCase() === 'valide'"
                        title="Valider"
                      >
                        <i class="ti ti-check"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div v-if="ajustements.length === 0" class="text-center text-muted py-4">
                Aucun ajustement (génère-les depuis les écarts).
              </div>
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
  name: 'DetailsInventaire',
  components: { MainLayout },
  data() {
    return {
      inventaire: null,
      lignes: [],
      ajustements: [],
      lots: [],
      selectedLot: {},
      editPhysique: {},
      editMotif: {},
      isLoading: false,
      isActionLoading: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  mounted() {
    this.refresh();
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
    userLabel(u) {
      if (!u) return '—';
      const nom = `${u.nom || ''} ${u.prenom || ''}`.trim();
      return nom || u.email || `#${u.id}`;
    },
    badgeClass(statut) {
      const s = (statut || 'en_cours').toLowerCase();
      if (s === 'valide') return 'badge bg-success';
      if (s === 'termine') return 'badge bg-secondary';
      return 'badge bg-warning';
    },
    badgeClassAjust(statut) {
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
    calcEcart(l) {
      const theo = Number(l.quantiteTheorique || 0);
      const phys = this.editPhysique[l.id];
      if (phys === null || phys === undefined || phys === '') {
        return l.ecart ?? '—';
      }
      return Number(phys) - theo;
    },
    async refresh() {
      this.isLoading = true;
      this.errorMessage = '';
      try {
        const id = this.$route.params.id;
        const [invRes, lignesRes, ajRes] = await Promise.all([
          axios.get(`/api/inventaires/${id}`),
          axios.get(`/api/lignes-inventaires?inventaireId=${id}`),
          axios.get(`/api/ajustements-stock?inventaireId=${id}`)
        ]);
        this.inventaire = invRes.data;
        this.lignes = lignesRes.data || [];
        this.ajustements = ajRes.data || [];

        const sel = { ...(this.selectedLot || {}) };
        for (const a of this.ajustements) {
          if (a?.id && a?.lot?.id && sel[a.id] == null) {
            sel[a.id] = a.lot.id;
          }
        }
        this.selectedLot = sel;

        const phys = {};
        const motifs = {};
        for (const l of this.lignes) {
          phys[l.id] = l.quantitePhysique ?? l.quantiteTheorique ?? 0;
          motifs[l.id] = l.motifEcart || '';
        }
        this.editPhysique = phys;
        this.editMotif = motifs;
      } catch (e) {
        console.error('Erreur chargement inventaire:', e);
        this.errorMessage = 'Erreur lors du chargement de l\'inventaire.';
      } finally {
        this.isLoading = false;
      }
    },
    async genererLignes() {
      if (!this.inventaire?.id) return;
      this.isActionLoading = true;
      this.errorMessage = '';
      try {
        await axios.post(`/api/inventaires/${this.inventaire.id}/generer-lignes`);
        this.successMessage = 'Lignes générées.';
        await this.refresh();
        setTimeout(() => (this.successMessage = ''), 2500);
      } catch (e) {
        console.error('Erreur génération lignes:', e);
        const msg = e?.response?.headers?.['x-error-message'];
        this.errorMessage = msg ? `Impossible: ${msg}` : 'Impossible de générer les lignes.';
      } finally {
        this.isActionLoading = false;
      }
    },
    async savePhysique(ligne) {
      if (!ligne?.id) return;
      this.isActionLoading = true;
      this.errorMessage = '';
      try {
        const payload = {
          quantitePhysique: this.editPhysique[ligne.id],
          motifEcart: this.editMotif[ligne.id]
        };
        await axios.put(`/api/lignes-inventaires/${ligne.id}/physique`, payload);
        this.successMessage = 'Quantité physique enregistrée.';
        await this.refresh();
        setTimeout(() => (this.successMessage = ''), 2000);
      } catch (e) {
        console.error('Erreur saisie physique:', e);
        const msg = e?.response?.headers?.['x-error-message'];
        this.errorMessage = msg ? `Impossible: ${msg}` : 'Impossible d\'enregistrer la quantité physique.';
      } finally {
        this.isActionLoading = false;
      }
    },
    async terminer() {
      if (!this.inventaire?.id) return;
      this.isActionLoading = true;
      this.errorMessage = '';
      try {
        await axios.post(`/api/inventaires/${this.inventaire.id}/terminer`);
        this.successMessage = 'Inventaire terminé.';
        await this.refresh();
        setTimeout(() => (this.successMessage = ''), 2500);
      } catch (e) {
        console.error('Erreur terminer inventaire:', e);
        const msg = e?.response?.headers?.['x-error-message'];
        this.errorMessage = msg ? `Impossible: ${msg}` : 'Impossible de terminer l\'inventaire.';
      } finally {
        this.isActionLoading = false;
      }
    },
    async genererAjustements() {
      if (!this.inventaire?.id) return;
      this.isActionLoading = true;
      this.errorMessage = '';
      try {
        const userId = this.currentUserId();
        await axios.post(`/api/inventaires/${this.inventaire.id}/generer-ajustements`, {
          utilisateurPropositionId: userId,
          motifParDefaut: 'Ajustement inventaire'
        });
        this.successMessage = 'Ajustements générés.';
        await this.refresh();
        setTimeout(() => (this.successMessage = ''), 2500);
      } catch (e) {
        console.error('Erreur génération ajustements:', e);
        const msg = e?.response?.headers?.['x-error-message'];
        this.errorMessage = msg ? `Impossible: ${msg}` : 'Impossible de générer les ajustements.';
      } finally {
        this.isActionLoading = false;
      }
    },
    async validerAjustement(a) {
      if (!a?.id) return;
      this.isActionLoading = true;
      this.errorMessage = '';
      try {
        const userId = this.currentUserId();
        if (a.article?.traceableLot && !this.selectedLot[a.id] && (a.lot?.id == null)) {
          this.errorMessage = `Lot obligatoire pour l'article traçable: ${a.article?.code || ''}`;
          return;
        }
        await axios.post(`/api/ajustements-stock/${a.id}/valider`, {
          utilisateurValidationId: userId,
          lotId: this.selectedLot[a.id] || null
        });
        this.successMessage = `Ajustement ${a.reference || ('#' + a.id)} validé.`;
        await this.refresh();
        setTimeout(() => (this.successMessage = ''), 2500);
      } catch (e) {
        console.error('Erreur validation ajustement:', e);
        const msg = e?.response?.headers?.['x-error-message'];
        this.errorMessage = msg ? `Impossible: ${msg}` : 'Impossible de valider l\'ajustement.';
      } finally {
        this.isActionLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.table-responsive {
  margin-top: 1rem;
}
.gap-2 {
  gap: 0.5rem;
}
</style>
