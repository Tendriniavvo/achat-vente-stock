
<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Paiements Fournisseurs (Finance / DAF)</h5>
            <div class="d-flex gap-2">
              <button class="btn btn-outline-secondary btn-sm" @click="loadFactures">
                <i class="ti ti-refresh me-1"></i> Actualiser
              </button>
            </div>
          </div>

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="text-dark fs-4">
                <tr>
                  <th class="border-bottom-0">Référence</th>
                  <th class="border-bottom-0">Fournisseur</th>
                  <th class="border-bottom-0">Date Facture</th>
                  <th class="border-bottom-0 text-end">Montant</th>
                  <th class="border-bottom-0 text-center">Statut 3-Way Match</th>
                  <th class="border-bottom-0 text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="facture in filteredFactures" :key="facture.id">
                  <td class="border-bottom-0">
                    <span class="fw-semibold">{{ facture.reference }}</span>
                  </td>
                  <td class="border-bottom-0">{{ facture.fournisseur?.nom }}</td>
                  <td class="border-bottom-0">{{ formatDate(facture.dateFacture) }}</td>
                  <td class="border-bottom-0 text-end fw-bold">{{ formatMontant(facture.montantTotal) }} MGA</td>
                  <td class="border-bottom-0 text-center">
                    <span :class="getStatusBadgeClass(facture.statut)">
                      {{ getStatusLabel(facture.statut) }}
                    </span>
                  </td>
                  <td class="border-bottom-0 text-center">
                    <div class="d-flex justify-content-center gap-2">
                      <router-link :to="'/factures/' + facture.id" class="btn btn-light btn-sm" title="Voir détails">
                        <i class="ti ti-eye"></i>
                      </router-link>
                      <button 
                        v-if="facture.statut === 'validee' && canPay" 
                        class="btn btn-success btn-sm"
                        @click="payerFacture(facture)"
                        title="Effectuer le paiement"
                      >
                        <i class="ti ti-cash"></i> Payer
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="filteredFactures.length === 0">
                  <td colspan="6" class="text-center py-4 text-muted">
                    Aucune facture en attente de paiement ou payée.
                  </td>
                </tr>
              </tbody>
            </table>
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
  name: 'Paiements',
  components: {
    MainLayout
  },
  data() {
    return {
      factures: [],
      isLoading: true,
      currentUser: null
    }
  },
  computed: {
    filteredFactures() {
      // On n'affiche que les factures validées (prêtes à payer) ou déjà payées
      return this.factures.filter(f => f.statut === 'validee' || f.statut === 'payee');
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
    await this.loadFactures();
  },
  methods: {
    async loadFactures() {
      this.isLoading = true;
      try {
        const response = await axios.get('/api/factures-fournisseur');
        this.factures = response.data;
      } catch (err) {
        console.error('Erreur chargement factures:', err);
      } finally {
        this.isLoading = false;
      }
    },
    async payerFacture(facture) {
      if (!confirm(`Confirmez-vous le paiement de ${this.formatMontant(facture.montantTotal)} MGA pour la facture ${facture.reference} ?`)) return;
      
      try {
        await axios.post(`/api/factures-fournisseur/${facture.id}/payer?utilisateurId=${this.currentUser.id}`);
        alert('Paiement effectué avec succès !');
        await this.loadFactures();
      } catch (err) {
        const msg = err.response?.data || 'Erreur lors du paiement';
        alert(msg);
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return 'N/A';
      return new Date(dateStr).toLocaleDateString('fr-FR');
    },
    formatMontant(montant) {
      if (montant === null || montant === undefined) return '0';
      return new Intl.NumberFormat('fr-FR').format(montant);
    },
    getStatusBadgeClass(statut) {
      switch (statut) {
        case 'validee': return 'badge bg-light-success text-success';
        case 'payee': return 'badge bg-light-info text-info';
        default: return 'badge bg-light-secondary text-secondary';
      }
    },
    getStatusLabel(statut) {
      switch (statut) {
        case 'validee': return 'Prêt pour paiement';
        case 'payee': return 'Payée';
        default: return statut;
      }
    }
  }
}
</script>

<style scoped>
.bg-light-info { background-color: #e8f7ff !important; }
.bg-light-success { background-color: #e6fffa !important; }
.bg-light-secondary { background-color: #f6f9fc !important; }
</style>
