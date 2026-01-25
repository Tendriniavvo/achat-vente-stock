<template>
  <MainLayout>
    <div class="container-fluid">
      <!-- Header & Actions -->
      <div class="card mb-4 no-print">
        <div class="card-body py-3">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h5 class="card-title fw-semibold mb-0">Détails Commande Client : {{ commande.reference }}</h5>
            </div>
            <div class="d-flex gap-2">
              <router-link to="/commandes-client" class="btn btn-outline-secondary">
                <i class="ti ti-arrow-left me-1"></i> Retour
              </router-link>
              
              <!-- Action Buttons in Header -->
              <div class="d-flex gap-2">
                <button v-if="commande.statut === 'en_attente' && isCommercial" 
                        class="btn btn-primary" @click="confirmerCommande">
                  <i class="ti ti-check me-1"></i> Confirmer
                </button>
                <button v-if="(commande.statut === 'confirmee' || commande.statut === 'reservee') && isMagasinier" 
                        class="btn btn-success" @click="preparerLivraison">
                  <i class="ti ti-truck-delivery me-1"></i> Préparer Livraison
                </button>
                <button v-if="commande.statut !== 'annulee' && commande.statut !== 'livree' && isCommercial" 
                        class="btn btn-danger" @click="annulerCommande">
                  <i class="ti ti-x me-1"></i> Annuler
                </button>
                <button class="btn btn-outline-info" @click="exportToPDF">
                  <i class="ti ti-file-export me-1"></i> PDF
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <!-- Main Content -->
        <div class="col-lg-8">
          <div class="card">
            <div class="card-body">
              <div class="d-flex justify-content-between mb-4">
                <div>
                  <h6 class="fw-bold mb-1">Client :</h6>
                  <h5 class="text-primary mb-0">{{ commande.client?.nom }}</h5>
                  <p class="mb-0 text-muted">{{ commande.client?.code }}</p>
                  <p class="mb-0 text-muted">{{ commande.client?.adresse }}</p>
                </div>
                <div class="text-end">
                  <h6 class="fw-bold mb-1">Informations Commande :</h6>
                  <p class="mb-0">Date : <strong>{{ formatDate(commande.dateCommande) }}</strong></p>
                  <p class="mb-0">Statut : <span :class="getStatutClass(commande.statut)">{{ commande.statut }}</span></p>
                </div>
              </div>

              <div class="table-responsive">
                <table class="table table-bordered">
                  <thead class="table-light">
                    <tr>
                      <th>Article</th>
                      <th class="text-center">Quantité</th>
                      <th class="text-end">P.U. (MGA)</th>
                      <th class="text-end">Remise (%)</th>
                      <th class="text-end">Total HT</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="ligne in lignes" :key="ligne.id">
                      <td>
                        <strong>{{ ligne.article?.nom }}</strong><br>
                        <small class="text-muted">{{ ligne.article?.code }}</small>
                      </td>
                      <td class="text-center">{{ ligne.quantite }}</td>
                      <td class="text-end">{{ formatCurrency(ligne.prixUnitaire) }}</td>
                      <td class="text-end">{{ ligne.remise }}%</td>
                      <td class="text-end fw-bold">{{ formatCurrency(calculerLigneTotal(ligne)) }}</td>
                    </tr>
                  </tbody>
                  <tfoot>
                    <tr class="fs-5">
                      <td colspan="4" class="text-end fw-bold">TOTAL GÉNÉRAL :</td>
                      <td class="text-end fw-bold text-primary">{{ formatCurrency(commande.montantTotal) }}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar Info -->
        <div class="col-lg-4 no-print">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title fw-semibold mb-3">Suivi & Logistique</h5>
              
              <div class="mb-4">
                <label class="text-muted small d-block">Vendeur responsable</label>
                <p class="mb-0 fw-bold">{{ commande.utilisateur?.prenom }} {{ commande.utilisateur?.nom }}</p>
              </div>

              <div class="mb-4" v-if="commande.devis">
                <label class="text-muted small d-block">Devis source</label>
                <router-link :to="'/devis/' + commande.devis.id" class="fw-bold text-info">
                  <i class="ti ti-file-description me-1"></i>
                  {{ commande.devis.reference }}
                </router-link>
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
  name: 'DetailsCommandeClient',
  components: { MainLayout },
  data() {
    return {
      commande: {},
      lignes: [],
      isLoading: false
    };
  },
  computed: {
    currentUser() {
      const authData = JSON.parse(localStorage.getItem('user') || '{}');
      return authData.user || null;
    },
    isMagasinier() {
      return this.currentUser?.roles?.some(r => r.nom === 'Magasinier' || r.nom === 'Administrateur');
    },
    isCommercial() {
      return this.currentUser?.roles?.some(r => r.nom === 'Commercial' || r.nom === 'Responsable ventes' || r.nom === 'Administrateur');
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
        const response = await axios.get(`/api/commandes-client/${id}`);
        this.commande = response.data;
        // In a real app, you'd fetch lines from a specific endpoint
        // For now, let's assume they are either in the object or fetchable
        const lignesRes = await axios.get(`/api/commandes-client/${id}/lignes`);
        this.lignes = lignesRes.data;
      } catch (error) {
        console.error('Erreur chargement commande', error);
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
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-MG', { style: 'currency', currency: 'MGA' }).format(value);
    },
    calculerLigneTotal(ligne) {
      const base = ligne.quantite * (ligne.prixUnitaire || 0);
      const remise = base * (ligne.remise || 0) / 100;
      return base - remise;
    },
    getStatutClass(statut) {
      if (!statut) return 'badge bg-light text-dark';
      const s = statut.toLowerCase();
      if (s === 'en_attente') return 'badge bg-warning';
      if (s === 'confirmee') return 'badge bg-info';
      if (s === 'reservee') return 'badge bg-primary';
      if (s === 'livraison_en_cours') return 'badge bg-info text-white';
      if (s === 'livree') return 'badge bg-success';
      if (s === 'annulee') return 'badge bg-danger';
      return 'badge bg-light text-dark';
    },
    exportToPDF() {
      window.print();
    },
    async confirmerCommande() {
      if (confirm('Confirmer cette commande ?')) {
        try {
          await axios.put(`/api/commandes-client/${this.commande.id}/statut`, { statut: 'confirmee' });
          this.fetchDetails();
        } catch (error) {
          alert('Erreur lors de la confirmation');
        }
      }
    },
    async preparerLivraison() {
       if (confirm('Voulez-vous initier la préparation du bon de livraison pour cette commande ?')) {
         try {
           await axios.post(`/api/commandes-client/${this.commande.id}/preparer-livraison`, {
             utilisateurId: this.currentUser.id
           });
           alert('Bon de livraison créé avec succès. La commande est maintenant en cours de préparation.');
           this.fetchDetails();
         } catch (error) {
           const msg = error.response?.data || 'Erreur lors de la préparation';
           alert(typeof msg === 'string' ? msg : 'Erreur lors de la création du bon de livraison.');
         }
       }
     },
    async annulerCommande() {
      if (confirm('Annuler cette commande ?')) {
        try {
          await axios.put(`/api/commandes-client/${this.commande.id}/statut`, { statut: 'annulee' });
          this.fetchDetails();
        } catch (error) {
          alert('Erreur lors de l\'annulation');
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
}
</style>
