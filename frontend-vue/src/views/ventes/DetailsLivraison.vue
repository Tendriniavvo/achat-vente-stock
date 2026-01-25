<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="row">
        <div class="col-lg-8">
          <div class="card">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-center mb-4">
                <h5 class="card-title fw-semibold mb-0">Bon de Livraison : {{ livraison.reference }}</h5>
                <div class="d-flex gap-2 no-print">
                  <button class="btn btn-outline-secondary" @click="$router.push('/livraisons')">
                    <i class="ti ti-arrow-left me-1"></i>Retour
                  </button>
                  <button class="btn btn-info" @click="exportToPDF">
                    <i class="ti ti-printer me-1"></i>Imprimer
                  </button>
                  <button 
                    v-if="livraison.statut === 'preparation'" 
                    class="btn btn-success" 
                    @click="expedierLivraison"
                  >
                    <i class="ti ti-truck me-1"></i>Expédier
                  </button>
                </div>
              </div>

              <div class="row mb-4">
                <div class="col-md-6">
                  <label class="text-muted small d-block">Client</label>
                  <p class="mb-0 fw-bold fs-5" v-if="livraison.commandeClient?.client">
                    {{ livraison.commandeClient.client.nom }}
                  </p>
                  <p class="text-muted small mb-0" v-if="livraison.commandeClient?.client?.adresse">
                    {{ livraison.commandeClient.client.adresse }}
                  </p>
                </div>
                <div class="col-md-6 text-md-end">
                  <label class="text-muted small d-block">Statut</label>
                  <span :class="getStatutClass(livraison.statut)" class="fs-4">{{ livraison.statut }}</span>
                </div>
              </div>

              <div class="table-responsive">
                <table class="table table-bordered align-middle">
                  <thead class="table-light">
                    <tr>
                      <th>Article</th>
                      <th class="text-center">Quantité</th>
                      <th>Dépôt</th>
                      <th>Emplacement</th>
                      <th>Lot</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="ligne in lignes" :key="ligne.id">
                      <td>
                        <div class="fw-bold">{{ ligne.article?.nom }}</div>
                        <div class="text-muted small">{{ ligne.article?.code }}</div>
                      </td>
                      <td class="text-center fw-bold">{{ ligne.quantiteLivree }}</td>
                      <td>{{ ligne.emplacement?.depot?.nom || livraison.depot?.nom || '-' }}</td>
                      <td>{{ ligne.emplacement?.code || '-' }}</td>
                      <td>{{ ligne.lot?.numeroLot || '-' }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title fw-semibold mb-4">Informations Générales</h5>
              
              <div class="mb-4">
                <label class="text-muted small d-block">Référence Commande</label>
                <router-link v-if="livraison.commandeClient" :to="'/commandes-client/' + livraison.commandeClient.id" class="fw-bold text-primary">
                  {{ livraison.commandeClient.reference }}
                </router-link>
              </div>

              <div class="mb-4">
                <label class="text-muted small d-block">Date Création</label>
                <p class="mb-0 fw-bold">{{ formatDate(livraison.dateLivraison) }}</p>
              </div>

              <div class="mb-4">
                <label class="text-muted small d-block">Préparé par</label>
                <p class="mb-0 fw-bold">{{ livraison.utilisateur?.prenom }} {{ livraison.utilisateur?.nom }}</p>
              </div>

              <div class="mb-4" v-if="livraison.depot">
                <label class="text-muted small d-block">Dépôt d'expédition</label>
                <p class="mb-0 fw-bold">{{ livraison.depot.nom }}</p>
                <p class="text-muted small mb-0">{{ livraison.depot.adresse }}</p>
              </div>
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
  name: 'DetailsLivraison',
  components: { MainLayout },
  data() {
    return {
      livraison: {},
      lignes: [],
      isLoading: false
    };
  },
  computed: {
    currentUser() {
      const authData = JSON.parse(localStorage.getItem('user') || '{}');
      return authData.user || null;
    }
  },
  mounted() {
    this.fetchDetails();
  },
  methods: {
    async fetchDetails() {
      const id = this.$route.params.id;
      this.isLoading = true;
      try {
        const response = await axios.get(`/api/livraisons/${id}`);
        this.livraison = response.data;
        const lignesRes = await axios.get(`/api/livraisons/${id}/lignes`);
        this.lignes = lignesRes.data;
      } catch (error) {
        console.error('Erreur chargement livraison', error);
      } finally {
        this.isLoading = false;
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
      if (s === 'preparation') return 'badge bg-info';
      if (s === 'expediee') return 'badge bg-success';
      return 'badge bg-light text-dark';
    },
    exportToPDF() {
      window.print();
    },
    async expedierLivraison() {
      if (!this.currentUser) {
        alert('Erreur: Utilisateur non connecté');
        return;
      }

      if (confirm(`Confirmer l'expédition de la livraison ${this.livraison.reference} ? Le stock sera déduit définitivement.`)) {
        try {
          await axios.post(`/api/livraisons/${this.livraison.id}/expedier`, {
            utilisateurId: this.currentUser.id
          });
          alert('Livraison expédiée avec succès. Le stock a été mis à jour.');
          this.fetchDetails();
        } catch (error) {
          const msg = error.response?.data || 'Erreur lors de l\'expédition';
          alert(typeof msg === 'string' ? msg : 'Erreur lors de l\'expédition.');
        }
      }
    }
  }
};
</script>

<style scoped>
@media print {
  .no-print {
    display: none !important;
  }
  .container-fluid {
    padding: 0;
  }
  .card {
    border: none !important;
  }
}
</style>
