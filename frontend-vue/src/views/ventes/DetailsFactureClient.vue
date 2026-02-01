
<template>
  <MainLayout>
    <div class="container-fluid">
      <div v-if="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Chargement...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger" role="alert">
        {{ error }}
      </div>

      <div v-else-if="facture" class="row">
        <div class="col-lg-8">
          <div class="card">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-center mb-4">
                <h5 class="card-title fw-semibold mb-0">
                  <span v-if="facture.reference.startsWith('AVOIR-')">Avoir</span>
                  <span v-else>Facture</span> 
                  {{ facture.reference }}
                </h5>
                <div>
                  <span :class="getStatusBadgeClass(facture.statut)" class="fs-3">
                    {{ getStatusLabel(facture.statut) }}
                  </span>
                </div>
              </div>

              <div class="row mb-4">
                <div class="col-md-6">
                  <p class="mb-1 text-muted">Client</p>
                  <h6 class="fw-bold">{{ facture.client?.nom }}</h6>
                  <p class="mb-0 small">{{ facture.client?.adresse }}</p>
                  <p class="mb-0 small">{{ facture.client?.email }}</p>
                  <p class="mb-0 small">{{ facture.client?.telephone }}</p>
                </div>
                <div class="col-md-6 text-md-end">
                  <p class="mb-1 text-muted">Date</p>
                  <h6 class="fw-bold">{{ formatDate(facture.dateFacture) }}</h6>
                  <p class="mb-1 text-muted mt-2">Commande associée</p>
                  <router-link :to="'/commandes-client/' + facture.commandeClient?.id" class="text-primary fw-bold">
                    {{ facture.commandeClient?.reference }}
                  </router-link>
                  <p class="mb-1 text-muted mt-2">Livraison associée</p>
                  <router-link :to="'/livraisons/' + facture.livraison?.id" class="text-primary fw-bold">
                    {{ facture.livraison?.reference }}
                  </router-link>
                </div>
              </div>

              <div class="table-responsive mb-4">
                <table class="table table-bordered align-middle">
                  <thead class="table-light">
                    <tr>
                      <th v-if="isSelectingForAvoir" class="text-center" style="width: 50px;">
                        <input type="checkbox" class="form-check-input" @change="toggleSelectAll" :checked="allSelected">
                      </th>
                      <th>Article</th>
                      <th class="text-center">Quantité</th>
                      <th v-if="isSelectingForAvoir" class="text-center" style="width: 150px;">Qté Avoir</th>
                      <th class="text-end">Prix Unit. HT</th>
                      <th class="text-end">Taxe (Taux)</th>
                      <th class="text-end">Total TTC (Est.)</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="ligne in lignes" :key="ligne.id">
                      <td v-if="isSelectingForAvoir" class="text-center">
                        <input type="checkbox" class="form-check-input" v-model="selectedLines" :value="ligne.id">
                      </td>
                      <td>
                        <h6 class="mb-0">{{ ligne.article?.nom }}</h6>
                        <small class="text-muted">{{ ligne.article?.reference }}</small>
                      </td>
                      <td class="text-center">{{ ligne.quantite }}</td>
                      <td v-if="isSelectingForAvoir" class="text-center">
                        <input 
                          type="number" 
                          class="form-control form-control-sm text-center" 
                          v-model.number="avoirQuantities[ligne.id]"
                          :max="ligne.quantite"
                          min="1"
                          :disabled="!selectedLines.includes(ligne.id)"
                        >
                      </td>
                      <td class="text-end">{{ formatMontant(ligne.prixUnitaire) }} MGA</td>
                      <td class="text-end">
                        {{ formatMontant(calculateTaxeAmount(ligne)) }} MGA ({{ ligne.taxe?.taux }}%)
                      </td>
                      <td class="text-end fw-bold">
                        {{ formatMontant(calculateLigneTotalTTC(ligne)) }} MGA
                      </td>
                    </tr>
                  </tbody>
                  <tfoot>
                    <tr>
                      <td :colspan="isSelectingForAvoir ? 6 : 4" class="text-end">Sous-total HT (Est.)</td>
                      <td class="text-end">{{ formatMontant(calculateTotalHT()) }} MGA</td>
                    </tr>
                    <tr>
                      <td :colspan="isSelectingForAvoir ? 6 : 4" class="text-end">TVA (Est.)</td>
                      <td class="text-end">{{ formatMontant(calculateTotalTaxe()) }} MGA</td>
                    </tr>
                    <tr v-if="facture.montantTotal !== calculateTotalTTC()">
                      <td :colspan="isSelectingForAvoir ? 6 : 4" class="text-end text-muted small">Remises & Ajustements (Inclus dans Total)</td>
                      <td class="text-end text-muted small">{{ formatMontant(facture.montantTotal - calculateTotalTTC()) }} MGA</td>
                    </tr>
                    <tr>
                      <td :colspan="isSelectingForAvoir ? 6 : 4" class="text-end fw-bold">Total Général (TTC)</td>
                      <td class="text-end fw-bold text-primary fs-5">
                        {{ formatMontant(facture.montantTotal) }} MGA
                      </td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title fw-semibold mb-4">Actions</h5>
              <div class="d-grid gap-2">
                <button 
                  v-if="facture.statut === 'attente' && (hasRole('Finance') || isAdmin())"
                  class="btn btn-success"
                  @click="validerFacture"
                  :disabled="isProcessing || (isClientCreator && !isAdmin())"
                >
                  <i class="ti ti-check me-1"></i> Valider la Facture
                </button>
                <div v-if="isClientCreator && !isAdmin() && facture.statut === 'attente' && (hasRole('Finance') || isAdmin())" class="alert alert-warning py-2 mb-2 small">
                  <i class="ti ti-alert-triangle me-1"></i>
                  Vous ne pouvez pas valider cette facture car vous avez créé ce client (Séparation des tâches).
                </div>
                
                <button 
                  v-if="facture.statut === 'validee' && !facture.reference.startsWith('AVOIR-') && !isSelectingForAvoir"
                  class="btn btn-warning"
                  @click="isSelectingForAvoir = true"
                  :disabled="isProcessing"
                >
                  <i class="ti ti-receipt-refund me-1"></i> Créer un Avoir
                </button>

                <div v-if="isSelectingForAvoir" class="d-grid gap-2 border p-3 rounded bg-light">
                  <h6 class="fw-bold mb-3">Options de l'Avoir</h6>
                  <button 
                    class="btn btn-warning"
                    @click="showAvoirModal = true"
                    :disabled="selectedLines.length === 0 || isProcessing"
                  >
                    Confirmer la sélection ({{ selectedLines.length }})
                  </button>
                  <button 
                    class="btn btn-outline-secondary"
                    @click="cancelSelection"
                  >
                    Annuler
                  </button>
                </div>

                <button class="btn btn-outline-secondary" @click="$router.back()">
                  <i class="ti ti-arrow-left me-1"></i> Retour
                </button>
              </div>

              <div v-if="facture.utilisateurValidation" class="mt-4 pt-4 border-top">
                <p class="mb-1 text-muted">Validée par</p>
                <h6 class="fw-bold">{{ facture.utilisateurValidation.nom }} {{ facture.utilisateurValidation.prenom }}</h6>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal Motif Avoir -->
      <div v-if="showAvoirModal" class="modal-backdrop fade show"></div>
      <div v-if="showAvoirModal" class="modal fade show d-block" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Créer un Avoir</h5>
              <button type="button" class="btn-close" @click="showAvoirModal = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label for="motif" class="form-label">Motif de l'avoir <span class="text-danger">*</span></label>
                <textarea 
                  id="motif" 
                  class="form-control" 
                  v-model="motifAvoir" 
                  rows="3" 
                  placeholder="Ex: Retour article, erreur prix..."
                ></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-light" @click="showAvoirModal = false">Annuler</button>
              <button 
                type="button" 
                class="btn btn-warning" 
                @click="creerAvoir" 
                :disabled="!motifAvoir || isProcessing"
              >
                <span v-if="isProcessing" class="spinner-border spinner-border-sm me-1"></span>
                Confirmer l'Avoir
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '@/layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'DetailsFactureClient',
  components: {
    MainLayout
  },
  props: {
    livraisonId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      facture: null,
      lignes: [],
      isLoading: true,
      isProcessing: false,
      error: '',
      showAvoirModal: false,
      motifAvoir: '',
      isSelectingForAvoir: false,
      selectedLines: [],
      avoirQuantities: {},
      isClientCreator: false
    }
  },
  computed: {
    allSelected() {
      return this.lignes.length > 0 && this.selectedLines.length === this.lignes.length;
    }
  },
  mounted() {
    this.loadFacture();
  },
  methods: {
    async loadFacture() {
      this.isLoading = true;
      let id = this.$route.params.id;
      try {
        if (this.livraisonId) {
          const res = await axios.get(`/api/factures-client/livraison/${this.livraisonId}`);
          id = res.data.id;
        }
        
        const [factureRes, lignesRes] = await Promise.all([
          axios.get(`/api/factures-client/${id}`),
          axios.get(`/api/factures-client/${id}/lignes`)
        ]);
        this.facture = factureRes.data;
        this.lignes = lignesRes.data;
        
        // Vérifier si l'utilisateur est le créateur du client
        await this.checkClientCreator();

        // Initialiser les quantités d'avoir par défaut
        this.lignes.forEach(l => {
          this.avoirQuantities[l.id] = l.quantite;
        });
      } catch (err) {
        this.error = 'Erreur lors du chargement des détails de la facture';
        console.error(err);
      } finally {
        this.isLoading = false;
      }
    },
    async checkClientCreator() {
      if (!this.facture || !this.facture.client) return;
      
      const userStr = localStorage.getItem('user');
      if (!userStr) return;
      const authData = JSON.parse(userStr);
      const utilisateurId = authData.user?.id || authData.id;

      try {
        // Rechercher l'audit de création du client
        const response = await axios.get(`/api/audits/module/CLIENT/ref/${this.facture.client.id}`);
        const creationAudit = response.data.find(a => a.action === 'CRÉATION');
        if (creationAudit && creationAudit.utilisateur.id === utilisateurId) {
          this.isClientCreator = true;
        }
      } catch (err) {
        console.error('Erreur lors de la vérification du créateur du client:', err);
      }
    },
    hasRole(roleNom) {
      const userStr = localStorage.getItem('user');
      if (!userStr) return false;
      const authData = JSON.parse(userStr);
      const user = authData.user || authData;
      if (!user || !user.roles) return false;
      return user.roles.some(r => r.nom.toUpperCase() === roleNom.toUpperCase());
    },
    isAdmin() {
      const userStr = localStorage.getItem('user');
      if (!userStr) return false;
      const authData = JSON.parse(userStr);
      const user = authData.user || authData;
      if (!user || !user.roles) return false;
      return user.roles.some(r => r.id === 1);
    },
    async validerFacture() {
      if (!confirm('Voulez-vous valider cette facture ?')) return;
      
      const userStr = localStorage.getItem('user');
      if (!userStr) return;
      const authData = JSON.parse(userStr);
      const utilisateurId = authData.user?.id || authData.id;

      this.isProcessing = true;
      try {
        await axios.post(`/api/factures-client/${this.facture.id}/valider?utilisateurId=${utilisateurId}`);
        alert('Facture validée avec succès !');
        await this.loadFacture();
      } catch (err) {
        alert(err.response?.data || 'Erreur lors de la validation');
      } finally {
        this.isProcessing = false;
      }
    },
    async creerAvoir() {
      const userStr = localStorage.getItem('user');
      if (!userStr) return;
      const authData = JSON.parse(userStr);
      const utilisateurId = authData.user?.id || authData.id;

      this.isProcessing = true;
      try {
        const payload = this.selectedLines.map(id => ({
          ligneFactureId: id,
          quantite: this.avoirQuantities[id]
        }));

        const response = await axios.post(`/api/factures-client/${this.facture.id}/avoir?motif=${encodeURIComponent(this.motifAvoir)}&utilisateurId=${utilisateurId}`, payload);
        alert('Avoir créé avec succès !');
        this.showAvoirModal = false;
        this.isSelectingForAvoir = false;
        this.$router.push(`/factures-client/${response.data.id}`);
      } catch (err) {
        alert(err.response?.data || 'Erreur lors de la création de l\'avoir');
      } finally {
        this.isProcessing = false;
      }
    },
    toggleSelectAll() {
      if (this.allSelected) {
        this.selectedLines = [];
      } else {
        this.selectedLines = this.lignes.map(l => l.id);
      }
    },
    cancelSelection() {
      this.isSelectingForAvoir = false;
      this.selectedLines = [];
    },
    formatDate(dateStr) {
      if (!dateStr) return 'N/A';
      return new Date(dateStr).toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    formatMontant(montant) {
      if (montant === null || montant === undefined) return '0';
      return new Intl.NumberFormat('fr-FR').format(montant);
    },
    calculateTaxeAmount(ligne) {
      if (!ligne || !ligne.prixUnitaire || !ligne.quantite) return 0;
      const ht = ligne.prixUnitaire * ligne.quantite;
      const taux = ligne.taxe?.taux || 0;
      return (ht * taux) / 100;
    },
    calculateLigneTotalTTC(ligne) {
      if (!ligne || !ligne.prixUnitaire || !ligne.quantite) return 0;
      const ht = ligne.prixUnitaire * ligne.quantite;
      const taxe = this.calculateTaxeAmount(ligne);
      return ht + taxe;
    },
    calculateTotalHT() {
      return this.lignes.reduce((total, ligne) => total + (ligne.prixUnitaire * ligne.quantite), 0);
    },
    calculateTotalTaxe() {
      return this.lignes.reduce((total, ligne) => total + this.calculateTaxeAmount(ligne), 0);
    },
    calculateTotalTTC() {
      return this.calculateTotalHT() + this.calculateTotalTaxe();
    },
    getStatusBadgeClass(statut) {
      switch (statut) {
        case 'attente': return 'badge bg-light-warning text-warning';
        case 'validee': return 'badge bg-light-success text-success';
        case 'payee': return 'badge bg-light-info text-info';
        case 'annulee': return 'badge bg-light-danger text-danger';
        default: return 'badge bg-light-secondary text-secondary';
      }
    },
    getStatusLabel(statut) {
      switch (statut) {
        case 'attente': return 'En attente';
        case 'validee': return 'Validée';
        case 'payee': return 'Payée';
        case 'annulee': return 'Annulée';
        default: return statut;
      }
    }
  }
}
</script>

<style scoped>
.modal-backdrop {
  background-color: rgba(0,0,0,0.5);
}
.bg-light-warning { background-color: #fef5e5 !important; }
.bg-light-success { background-color: #e6fffa !important; }
.bg-light-danger { background-color: #fdede8 !important; }
.bg-light-info { background-color: #e8f7ff !important; }
.bg-light-secondary { background-color: #f6f9fc !important; }
</style>
