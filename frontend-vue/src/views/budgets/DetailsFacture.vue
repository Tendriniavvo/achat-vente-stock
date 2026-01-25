<template>
  <MainLayout>
    <div class="container-fluid">
      <div v-if="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Chargement...</span>
        </div>
      </div>

      <div v-else-if="facture" id="printable-area">
        <!-- Header & Actions -->
        <div class="card mb-4 no-print">
          <div class="card-body py-3">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h5 class="card-title fw-semibold mb-0">Détails Facture : {{ facture.reference }}</h5>
              </div>
              <div class="d-flex gap-2">
                <router-link to="/factures" class="btn btn-outline-secondary">
                  <i class="ti ti-arrow-left me-1"></i> Retour
                </router-link>
                <button class="btn btn-outline-primary" @click="exportToPDF">
                  <i class="ti ti-file-export me-1"></i> PDF
                </button>
                <button 
                  v-if="(facture.statut === 'attente' || facture.statut === 'bloquee') && canRapprocher"
                  class="btn btn-primary"
                  @click="rapprocher"
                  :disabled="isProcessing"
                >
                  <span v-if="isProcessing" class="spinner-border spinner-border-sm me-1"></span>
                  <i v-else class="ti ti-check me-1"></i>
                  Rapprocher
                </button>
                <button 
                  v-if="facture.statut === 'validee' && canPay"
                  class="btn btn-success"
                  @click="payer"
                  :disabled="isProcessing"
                >
                  <span v-if="isProcessing" class="spinner-border spinner-border-sm me-1"></span>
                  <i v-else class="ti ti-cash me-1"></i>
                  Payer
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="row">
          <!-- General Info -->
          <div class="col-md-4 mb-4">
            <div class="card h-100">
              <div class="card-body">
                <h5 class="card-title fw-semibold mb-4">Informations Générales</h5>
                <div class="mb-3">
                  <label class="text-muted mb-1">Référence Facture</label>
                  <div class="fw-bold">{{ facture.reference }}</div>
                </div>
                <div class="mb-3">
                  <label class="text-muted mb-1">Fournisseur</label>
                  <div class="fw-bold">{{ facture.fournisseur?.nom }}</div>
                </div>
                <div class="mb-3">
                  <label class="text-muted mb-1">Date Facture</label>
                  <div>{{ formatDate(facture.dateFacture) }}</div>
                </div>
                <div class="mb-3">
                  <label class="text-muted mb-1">Montant Total</label>
                  <div class="h4 text-primary fw-bold">{{ formatMontant(facture.montantTotal) }} MGA</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Linked Documents -->
          <div class="col-md-4 mb-4">
            <div class="card h-100">
              <div class="card-body">
                <h5 class="card-title fw-semibold mb-4">Documents Liés</h5>
                <div class="mb-3">
                  <label class="text-muted mb-1">Bon de Commande</label>
                  <div>
                    <router-link v-if="facture.bonCommande" :to="'/commandes-achat/' + facture.bonCommande.id" class="fw-bold text-decoration-none">
                      {{ facture.bonCommande.reference }}
                    </router-link>
                    <span v-else class="text-muted">Aucun</span>
                  </div>
                </div>
                <div class="mb-3">
                  <label class="text-muted mb-1">Bon de Réception</label>
                  <div>
                    <router-link v-if="facture.reception" :to="'/receptions/' + facture.reception.id" class="fw-bold text-decoration-none">
                      {{ facture.reception.reference }}
                    </router-link>
                    <span v-else class="text-muted">Aucun</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Status & Audit -->
          <div class="col-md-4 mb-4">
            <div class="card h-100">
              <div class="card-body">
                <h5 class="card-title fw-semibold mb-4">Statut & Validation</h5>
                <div class="mb-4">
                  <label class="text-muted mb-1">Statut Rapprochement</label>
                  <div class="mt-1">
                    <span :class="getStatusBadgeClass(facture.statut)" class="fs-4">
                      {{ getStatusLabel(facture.statut) }}
                    </span>
                  </div>
                </div>
                <div v-if="facture.ecarts" class="mb-3">
                  <label class="text-muted mb-1 text-danger">Écarts détectés</label>
                  <div class="alert alert-light-danger text-danger border-0 small">
                    {{ facture.ecarts }}
                  </div>
                </div>
                <div v-if="facture.utilisateurValidation" class="mb-3">
                  <label class="text-muted mb-1">Validé par</label>
                  <div>{{ facture.utilisateurValidation.nom }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Line Details Table -->
        <div class="card">
          <div class="card-body">
            <h5 class="card-title fw-semibold mb-4">Détail des Lignes & Comparaison (3-way match)</h5>
            <div class="table-responsive">
              <table class="table table-bordered align-middle">
                <thead class="table-light">
                  <tr>
                    <th rowspan="2" class="align-middle">Article</th>
                    <th colspan="2" class="text-center text-primary py-2">Facture</th>
                    <th colspan="2" class="text-center text-success py-2">Bon de Commande</th>
                    <th class="text-center text-info py-2">Réception</th>
                    <th rowspan="2" class="text-center align-middle">Écart</th>
                  </tr>
                  <tr>
                    <th class="text-center small py-1">Qté</th>
                    <th class="text-center small py-1">P.U</th>
                    <th class="text-center small py-1">Qté</th>
                    <th class="text-center small py-1">P.U</th>
                    <th class="text-center small py-1">Qté Recue</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="ligne in factureLignes" :key="ligne.id">
                    <td>
                      <div class="fw-semibold">{{ ligne.article?.code }}</div>
                      <small class="text-muted">{{ ligne.article?.nom }}</small>
                    </td>
                    <td class="text-center">{{ ligne.quantite }}</td>
                    <td class="text-end">{{ formatMontant(ligne.prixUnitaire) }}</td>
                    <td class="text-center bg-light">{{ getLigneBC(ligne.article.id)?.quantite || 0 }}</td>
                    <td class="text-end bg-light">{{ formatMontant(getLigneBC(ligne.article.id)?.prixUnitaire || 0) }}</td>
                    <td class="text-center bg-light">{{ getLigneReception(ligne.article.id)?.quantiteRecue || 0 }}</td>
                    <td class="text-center">
                      <span v-if="hasEcart(ligne)" class="badge bg-light-danger text-danger">
                        <i class="ti ti-alert-triangle me-1"></i> Écart
                      </span>
                      <span v-else class="badge bg-light-success text-success">
                        <i class="ti ti-check me-1"></i> OK
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="alert alert-warning">
        Facture non trouvée.
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '@/layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'DetailsFacture',
  components: {
    MainLayout
  },
  data() {
    return {
      facture: null,
      factureLignes: [],
      bcLignes: [],
      receptionLignes: [],
      isLoading: true,
      isProcessing: false,
      error: '',
      currentUser: null
    }
  },
  computed: {
    canRapprocher() {
      if (!this.currentUser) return false;
      return this.currentUser.roles.some(r => 
        r.nom.toUpperCase() === 'FINANCE' || r.nom.toUpperCase() === 'ADMINISTRATEUR'
      );
    },
    canPay() {
      if (!this.currentUser) return false;
      return this.currentUser.roles.some(r => 
        r.nom.toUpperCase() === 'FINANCE' || 
        r.nom.toUpperCase() === 'DAF' || 
        r.nom.toUpperCase() === 'ADMINISTRATEUR'
      );
    }
  },
  async mounted() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const parsed = JSON.parse(userStr);
      this.currentUser = parsed.user || parsed;
    }
    await this.loadDetails();
  },
  methods: {
    async loadDetails() {
      const id = this.$route.params.id;
      this.isLoading = true;
      try {
        const response = await axios.get(`/api/factures-fournisseur/${id}`);
        this.facture = response.data;

        // Charger les lignes
        const lignesRes = await axios.get(`/api/factures-fournisseur/${id}/lignes`);
        this.factureLignes = lignesRes.data;

        if (this.facture.bonCommande) {
          const bcRes = await axios.get(`/api/bons-commande-fournisseur/${this.facture.bonCommande.id}/lignes`);
          this.bcLignes = bcRes.data;
        }

        if (this.facture.reception) {
          const recRes = await axios.get(`/api/receptions/${this.facture.reception.id}/lignes`);
          this.receptionLignes = recRes.data;
        }
      } catch (err) {
        console.error('Erreur chargement détails facture:', err);
        this.error = 'Erreur lors du chargement des détails';
      } finally {
        this.isLoading = false;
      }
    },
    getLigneBC(articleId) {
      return this.bcLignes.find(l => l.article.id === articleId);
    },
    getLigneReception(articleId) {
      return this.receptionLignes.find(l => l.article.id === articleId);
    },
    hasEcart(ligneFacture) {
      const bcLigne = this.getLigneBC(ligneFacture.article.id);
      const recLigne = this.getLigneReception(ligneFacture.article.id);
      
      if (!bcLigne) return true;
      if (ligneFacture.quantite > bcLigne.quantite) return true;
      if (parseFloat(ligneFacture.prixUnitaire) !== parseFloat(bcLigne.prixUnitaire)) return true;
      
      if (recLigne && ligneFacture.quantite > recLigne.quantiteRecue) return true;
      
      return false;
    },
    async rapprocher() {
      if (!confirm(`Voulez-vous lancer le rapprochement pour la facture ${this.facture.reference} ?`)) return;
      
      const userStr = localStorage.getItem('user');
      if (!userStr) {
        alert('Utilisateur non connecté');
        return;
      }
      const user = JSON.parse(userStr);
      const utilisateurId = user.user.id;

      this.isProcessing = true;
      try {
        const response = await axios.post(`/api/factures-fournisseur/${this.facture.id}/rapprocher?utilisateurId=${utilisateurId}`);
        
        if (response.data.statut === 'validee') {
          alert('Rapprochement réussi ! La facture est validée.');
        } else {
          alert('Rapprochement terminé avec des écarts. La facture est bloquée.');
        }
        
        await this.loadDetails();
      } catch (err) {
        const msg = err.response?.data || 'Erreur lors du rapprochement';
        alert(msg);
        console.error(err);
      } finally {
        this.isProcessing = false;
      }
    },
    async payer() {
      if (!confirm(`Confirmez-vous le paiement de ${this.formatMontant(this.facture.montantTotal)} MGA pour la facture ${this.facture.reference} ?`)) return;
      
      const utilisateurId = this.currentUser.id;

      this.isProcessing = true;
      try {
        await axios.post(`/api/factures-fournisseur/${this.facture.id}/payer?utilisateurId=${utilisateurId}`);
        alert('Paiement effectué avec succès !');
        await this.loadDetails();
      } catch (err) {
        const msg = err.response?.data || 'Erreur lors du paiement';
        alert(msg);
        console.error(err);
      } finally {
        this.isProcessing = false;
      }
    },
    exportToPDF() {
      window.print();
    },
    formatDate(dateStr) {
      if (!dateStr) return 'N/A';
      return new Date(dateStr).toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    formatMontant(montant) {
      if (montant === null || montant === undefined) return '0';
      return new Intl.NumberFormat('fr-FR').format(montant);
    },
    getStatusBadgeClass(statut) {
      switch (statut) {
        case 'attente': return 'badge bg-light-warning text-warning';
        case 'validee': return 'badge bg-light-success text-success';
        case 'bloquee': return 'badge bg-light-danger text-danger';
        case 'payee': return 'badge bg-light-info text-info';
        default: return 'badge bg-light-secondary text-secondary';
      }
    },
    getStatusLabel(statut) {
      switch (statut) {
        case 'attente': return 'En attente';
        case 'validee': return 'Validée';
        case 'bloquee': return 'Bloquée (Écarts)';
        case 'payee': return 'Payée';
        default: return statut;
      }
    }
  }
}
</script>

<style scoped>
.bg-light-warning { background-color: #fef5e5 !important; }
.bg-light-success { background-color: #e6fffa !important; }
.bg-light-danger { background-color: #fdede8 !important; }
.bg-light-info { background-color: #e8f7ff !important; }
.bg-light-secondary { background-color: #f6f9fc !important; }

@media print {
  .no-print {
    display: none !important;
  }
  .card {
    border: 1px solid #ddd !important;
    box-shadow: none !important;
  }
  .container-fluid {
    padding: 0 !important;
  }
  body {
    background: white !important;
  }
}
</style>
