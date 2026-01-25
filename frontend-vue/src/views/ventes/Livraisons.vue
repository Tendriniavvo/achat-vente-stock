<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Bons de Livraison</h5>
            <button class="btn btn-outline-primary" @click="fetchLivraisons" :disabled="isLoading">
              <i class="ti ti-refresh me-1" :class="{ 'spinner-border spinner-border-sm': isLoading }"></i>
              Actualiser
            </button>
          </div>

          <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ errorMessage }}
            <button type="button" class="btn-close" @click="errorMessage = ''"></button>
          </div>

          <div class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light text-nowrap">
                <tr>
                  <th>Référence BL</th>
                  <th>Commande Client</th>
                  <th>Date Livraison</th>
                  <th>Statut</th>
                  <th>Préparé par</th>
                  <th class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="isLoading">
                  <td colspan="6" class="text-center py-5">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Chargement...</span>
                    </div>
                  </td>
                </tr>
                <tr v-else-if="livraisons.length === 0">
                  <td colspan="6" class="text-center py-5 text-muted">
                    Aucun bon de livraison trouvé.
                  </td>
                </tr>
                <tr v-for="livraison in livraisons" :key="livraison.id">
                  <td>
                    <span class="fw-bold text-primary">{{ livraison.reference }}</span>
                  </td>
                  <td>
                    <router-link v-if="livraison.commandeClient" :to="'/commandes-client/' + livraison.commandeClient.id">
                      {{ livraison.commandeClient.reference }}
                    </router-link>
                  </td>
                  <td>{{ formatDate(livraison.dateLivraison) }}</td>
                  <td>
                    <span :class="getStatutClass(livraison.statut)">{{ livraison.statut }}</span>
                  </td>
                  <td>{{ livraison.utilisateur?.prenom }} {{ livraison.utilisateur?.nom }}</td>
                  <td class="text-center">
                    <div class="d-flex justify-content-center gap-1">
                      <router-link :to="'/livraisons/' + livraison.id" class="btn btn-sm btn-outline-primary" title="Voir détails">
                        <i class="ti ti-eye me-1"></i>Détails
                      </router-link>
                      <button class="btn btn-sm btn-outline-info" @click="printBL(livraison)" title="Imprimer PDF">
                        <i class="ti ti-file-description me-1"></i>PDF
                      </button>
                      <button 
                        v-if="livraison.statut === 'preparation'" 
                        class="btn btn-sm btn-outline-success" 
                        @click="expedierLivraison(livraison)" 
                        title="Expédier au client"
                      >
                        <i class="ti ti-truck me-1"></i>Expédier
                      </button>
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
  name: 'Livraisons',
  components: { MainLayout },
  data() {
    return {
      livraisons: [],
      isLoading: false,
      errorMessage: ''
    };
  },
  computed: {
    currentUser() {
      const authData = JSON.parse(localStorage.getItem('user') || '{}');
      return authData.user || null;
    }
  },
  mounted() {
    this.fetchLivraisons();
  },
  methods: {
    async fetchLivraisons() {
      this.isLoading = true;
      try {
        const response = await axios.get('/api/livraisons');
        this.livraisons = response.data.sort((a, b) => new Date(b.dateLivraison) - new Date(a.dateLivraison));
      } catch (error) {
        this.errorMessage = 'Erreur lors du chargement des livraisons';
        console.error(error);
      } finally {
        this.isLoading = false;
      }
    },
    async expedierLivraison(livraison) {
      if (!this.currentUser) {
        alert('Erreur: Utilisateur non connecté ou ID manquant');
        return;
      }

      if (confirm(`Confirmer l'expédition de la livraison ${livraison.reference} ? Le stock sera déduit définitivement.`)) {
        try {
          await axios.post(`/api/livraisons/${livraison.id}/expedier`, {
            utilisateurId: this.currentUser.id
          });
          alert('Livraison expédiée avec succès. Le stock a été mis à jour.');
          this.fetchLivraisons();
        } catch (error) {
          this.errorMessage = error.response?.data || "Erreur lors de l'expédition";
          console.error(error);
        }
      }
    },
    formatDate(date) {
      if (!date) return '-';
      return new Date(date).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    getStatutClass(statut) {
      if (!statut) return 'badge bg-light text-dark';
      const s = statut.toLowerCase();
      if (s === 'preparation') return 'badge bg-warning';
      if (s === 'expediee') return 'badge bg-success';
      return 'badge bg-light text-dark';
    },
    printBL(livraison) {
      this.$router.push(`/livraisons/${livraison.id}`);
      setTimeout(() => {
        window.print();
      }, 500);
    }
  }
};
</script>
