
<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Rapprochement Factures Fournisseurs (3-way match)</h5>
            <button class="btn btn-outline-primary" @click="loadFactures" :disabled="isLoading">
              <i class="ti ti-refresh me-1" :class="{ 'spinner-border spinner-border-sm': isLoading }"></i>
              Actualiser
            </button>
          </div>

          <div v-if="error" class="alert alert-danger mb-4">
            {{ error }}
          </div>

          <div class="table-responsive">
            <table class="table text-nowrap mb-0 align-middle">
              <thead class="text-dark fs-4">
                <tr>
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Référence</h6></th>
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Fournisseur</h6></th>
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Date Facture</h6></th>
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Montant Total</h6></th>
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Statut</h6></th>
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Actions</h6></th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="factures.length === 0 && !isLoading">
                  <td colspan="6" class="text-center py-4">Aucune facture trouvée.</td>
                </tr>
                <tr v-for="facture in factures" :key="facture.id">
                  <td class="border-bottom-0">
                    <h6 class="fw-semibold mb-1">{{ facture.reference }}</h6>
                    <small class="text-muted">BC: {{ facture.bonCommande?.reference || 'N/A' }}</small>
                  </td>
                  <td class="border-bottom-0">
                    <p class="mb-0 fw-normal">{{ facture.fournisseur?.nom || 'N/A' }}</p>
                  </td>
                  <td class="border-bottom-0">
                    <p class="mb-0 fw-normal">{{ formatDate(facture.dateFacture) }}</p>
                  </td>
                  <td class="border-bottom-0">
                    <h6 class="fw-semibold mb-0">{{ formatMontant(facture.montantTotal) }} MGA</h6>
                  </td>
                  <td class="border-bottom-0">
                    <span :class="getStatusBadgeClass(facture.statut)">
                      {{ getStatusLabel(facture.statut) }}
                    </span>
                    <div v-if="facture.ecarts" class="mt-1">
                      <small class="text-danger d-block" style="max-width: 200px; white-space: normal;">
                        {{ facture.ecarts }}
                      </small>
                    </div>
                  </td>
                  <td class="border-bottom-0">
                    <div class="d-flex align-items-center gap-2">
                      <button 
                        v-if="facture.statut === 'attente' || facture.statut === 'bloquee'"
                        class="btn btn-primary btn-sm"
                        @click="rapprocher(facture)"
                        :disabled="isProcessing === facture.id"
                      >
                        <span v-if="isProcessing === facture.id" class="spinner-border spinner-border-sm me-1"></span>
                        <i v-else class="ti ti-check me-1"></i>
                        Rapprocher
                      </button>
                      <router-link :to="'/factures/' + facture.id" class="btn btn-light btn-sm">
                        <i class="ti ti-eye"></i>
                      </router-link>
                    </div>
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
  name: 'Factures',
  components: {
    MainLayout
  },
  data() {
    return {
      factures: [],
      isLoading: false,
      isProcessing: null,
      error: '',
    }
  },
  mounted() {
    this.loadFactures();
  },
  methods: {
    async loadFactures() {
      this.isLoading = true;
      this.error = '';
      try {
        const response = await axios.get('/api/factures-fournisseur');
        this.factures = response.data;
      } catch (err) {
        this.error = 'Erreur lors du chargement des factures';
        console.error(err);
      } finally {
        this.isLoading = false;
      }
    },
    async rapprocher(facture) {
      if (!confirm(`Voulez-vous lancer le rapprochement pour la facture ${facture.reference} ?`)) return;
      
      const userStr = localStorage.getItem('user');
      if (!userStr) {
        alert('Utilisateur non connecté');
        return;
      }
      const user = JSON.parse(userStr);
      const utilisateurId = user.user.id;

      this.isProcessing = facture.id;
      try {
        const response = await axios.post(`/api/factures-fournisseur/${facture.id}/rapprocher?utilisateurId=${utilisateurId}`);
        
        if (response.data.statut === 'validee') {
          alert('Rapprochement réussi ! La facture est validée.');
        } else {
          alert('Rapprochement terminé avec des écarts. La facture est bloquée.');
        }
        
        await this.loadFactures();
      } catch (err) {
        const msg = err.response?.data || 'Erreur lors du rapprochement';
        alert(msg);
        console.error(err);
      } finally {
        this.isProcessing = null;
      }
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
</style>

