
<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Encaissements Clients (Comptable / Finance)</h5>
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
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Client</h6></th>
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Date</h6></th>
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Montant Total</h6></th>
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Statut</h6></th>
                  <th class="border-bottom-0"><h6 class="fw-semibold mb-0">Actions</h6></th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="filteredFactures.length === 0 && !isLoading">
                  <td colspan="6" class="text-center py-4">Aucune facture en attente d'encaissement.</td>
                </tr>
                <tr v-for="facture in filteredFactures" :key="facture.id">
                  <td class="border-bottom-0">
                    <h6 class="fw-semibold mb-1">{{ facture.reference }}</h6>
                    <small class="text-muted">{{ facture.reference.startsWith('AVOIR-') ? 'Avoir' : 'Facture' }}</small>
                  </td>
                  <td class="border-bottom-0">
                    <p class="mb-0 fw-normal">{{ facture.client?.nom || 'N/A' }}</p>
                  </td>
                  <td class="border-bottom-0">
                    <p class="mb-0 fw-normal">{{ formatDate(facture.dateFacture) }}</p>
                  </td>
                  <td class="border-bottom-0">
                    <h6 class="fw-semibold mb-0" :class="{'text-danger': facture.montantTotal < 0}">
                      {{ formatMontant(facture.montantTotal) }} MGA
                    </h6>
                  </td>
                  <td class="border-bottom-0">
                    <span :class="getStatusBadgeClass(facture.statut)">
                      {{ getStatusLabel(facture.statut) }}
                    </span>
                  </td>
                  <td class="border-bottom-0">
                    <div class="d-flex align-items-center gap-2">
                      <router-link :to="'/factures-client/' + facture.id" class="btn btn-light btn-sm">
                        <i class="ti ti-eye me-1"></i> Détails
                      </router-link>
                      <button 
                        v-if="facture.statut === 'validee'"
                        class="btn btn-success btn-sm"
                        @click="encaisserFacture(facture)"
                        :disabled="isProcessing === facture.id || isBlocked(facture.id)"
                        :title="isBlocked(facture.id) ? getBlockReason(facture.id) : ''"
                      >
                        <span v-if="isProcessing === facture.id" class="spinner-border spinner-border-sm me-1"></span>
                        <i v-else class="ti ti-cash me-1"></i>
                        Encaisser
                      </button>
                      <small v-if="isBlocked(facture.id)" class="text-danger d-block mt-1" style="font-size: 0.7rem;">
                        <i class="ti ti-alert-triangle me-1"></i>
                        {{ getBlockReason(facture.id) }}
                      </small>
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
  name: 'Encaissements',
  components: {
    MainLayout
  },
  data() {
    return {
      factures: [],
      isLoading: false,
      isProcessing: null,
      error: '',
      userValidationStatus: {}, // { factureId: boolean }
      userCreationStatus: {} // { factureId: boolean }
    }
  },
  computed: {
    filteredFactures() {
      // On n'affiche que les factures validées ou déjà payées (pour historique)
      return this.factures.filter(f => f.statut === 'validee' || f.statut === 'payee');
    }
  },
  mounted() {
    this.loadFactures();
  },
  methods: {
    isAdmin() {
      const userStr = localStorage.getItem('user');
      if (!userStr) return false;
      const authData = JSON.parse(userStr);
      const user = authData.user || authData;
      return user.roles?.some(r => r.id === 1);
    },
    isBlocked(factureId) {
      if (this.isAdmin()) return false;
      return this.userValidationStatus[factureId] || this.userCreationStatus[factureId];
    },
    getBlockReason(factureId) {
      if (this.userValidationStatus[factureId]) return "Vous avez validé ce document.";
      if (this.userCreationStatus[factureId]) return "Vous avez créé ce client.";
      return "";
    },
    async loadFactures() {
      this.isLoading = true;
      this.error = '';
      try {
        const response = await axios.get('/api/factures-client');
        this.factures = response.data;
        await this.checkSeparationOfDuties();
      } catch (err) {
        this.error = 'Erreur lors du chargement des factures';
        console.error(err);
      } finally {
        this.isLoading = false;
      }
    },
    async checkSeparationOfDuties() {
      const userStr = localStorage.getItem('user');
      if (!userStr) return;
      const authData = JSON.parse(userStr);
      const utilisateurId = authData.user?.id || authData.id;

      for (const facture of this.filteredFactures) {
        if (facture.statut === 'validee') {
          try {
            // 1. Vérifier la validation du document (Facture ou Avoir)
            const responseVentes = await axios.get(`/api/audits/module/VENTES/ref/${facture.reference}`);
            const hasValidated = responseVentes.data.some(a => 
              (a.action === 'VALIDATION_FACTURE' || a.action === 'VALIDATION_AVOIR') && 
              a.utilisateur.id === utilisateurId
            );
            this.userValidationStatus[facture.id] = hasValidated;

            // 2. Vérifier la création du client
            if (facture.client) {
              const responseClient = await axios.get(`/api/audits/module/CLIENT/ref/${facture.client.id}`);
              const hasCreatedClient = responseClient.data.some(a => 
                a.action === 'CRÉATION' && a.utilisateur.id === utilisateurId
              );
              this.userCreationStatus[facture.id] = hasCreatedClient;
            }
          } catch (err) {
            console.error(`Erreur audit pour ${facture.reference}:`, err);
          }
        }
      }
    },
    async encaisserFacture(facture) {
      const type = facture.reference.startsWith('AVOIR-') ? 'l\'avoir' : 'la facture';
      if (!confirm(`Confirmez-vous l'encaissement/remboursement de ${type} ${facture.reference} ?`)) return;
      
      const userStr = localStorage.getItem('user');
      if (!userStr) return;
      const authData = JSON.parse(userStr);
      const utilisateur = authData.user || authData;

      this.isProcessing = facture.id;
      try {
        // Enregistrement de l'encaissement
        const encaissement = {
          factureClient: { id: facture.id },
          montant: facture.montantTotal,
          dateEncaissement: new Date().toISOString(),
          modePaiement: 'VIREMENT', // Par défaut
          utilisateur: { id: utilisateur.id }
        };
        
        await axios.post('/api/encaissements', encaissement);
        
        // Mise à jour du statut de la facture (Ceci devrait idéalement être fait côté serveur lors de saveEncaissement)
        // Mais si ce n'est pas automatique, on le fait ici ou on s'assure que le service le fait.
        // On va supposer que le service EncaissementService.saveEncaissement ne met pas à jour le statut,
        // donc on va ajouter cette logique dans le backend plus tard si nécessaire.
        // Pour l'instant, on rafraîchit la liste.
        
        alert('Action effectuée avec succès !');
        await this.loadFactures();
      } catch (err) {
        const msg = err.response?.data || 'Erreur lors de l\'encaissement';
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
        day: '2-digit'
      });
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
        case 'validee': return 'Prêt pour encaissement';
        case 'payee': return 'Réglé';
        default: return statut;
      }
    }
  }
}
</script>

<style scoped>
.bg-light-success { background-color: #e6fffa !important; }
.bg-light-info { background-color: #e8f7ff !important; }
.bg-light-secondary { background-color: #f6f9fc !important; }
</style>
