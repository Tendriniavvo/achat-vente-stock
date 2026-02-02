<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Commandes Clients</h5>
            <div class="d-flex gap-2">
              <button class="btn btn-outline-success" @click="exportToExcel" :disabled="isLoading || commandes.length === 0">
                <i class="ti ti-file-spreadsheet me-1"></i>
                Excel
              </button>
              <button class="btn btn-outline-primary" @click="fetchCommandes" :disabled="isLoading">
                <i class="ti ti-refresh me-1" :class="{ 'spinner-border spinner-border-sm': isLoading }"></i>
                Actualiser
              </button>
            </div>
          </div>

          <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ errorMessage }}
            <button type="button" class="btn-close" @click="errorMessage = ''"></button>
          </div>

          <div class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light text-nowrap">
                <tr>
                  <th>Référence</th>
                  <th>Client</th>
                  <th>Devis Source</th>
                  <th>Date Commande</th>
                  <th class="text-end">Montant Total</th>
                  <th class="text-center">Statut</th>
                  <th class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="isLoading">
                  <td colspan="7" class="text-center py-5">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Chargement...</span>
                    </div>
                  </td>
                </tr>
                <tr v-else-if="commandes.length === 0">
                  <td colspan="7" class="text-center py-5 text-muted">
                    Aucune commande client trouvée.
                  </td>
                </tr>
                <tr v-for="commande in commandes" :key="commande.id">
                  <td>
                    <router-link :to="'/commandes-client/' + commande.id" class="fw-bold text-primary">
                      {{ commande.reference }}
                    </router-link>
                  </td>
                  <td>
                    <div>
                      <h6 class="mb-0 fw-semibold">{{ commande.client?.nom }}</h6>
                      <small class="text-muted">{{ commande.client?.code }}</small>
                    </div>
                  </td>
                  <td>
                    <router-link v-if="commande.devis" :to="'/devis/' + commande.devis.id" class="text-info">
                      <i class="ti ti-file-description me-1"></i>
                      {{ commande.devis.reference }}
                    </router-link>
                    <span v-else class="text-muted small">Sans devis</span>
                  </td>
                  <td>{{ formatDate(commande.dateCommande) }}</td>
                  <td class="text-end fw-bold">{{ formatCurrency(commande.montantTotal) }}</td>
                  <td class="text-center">
                    <span :class="getStatutClass(commande.statut)">{{ commande.statut }}</span>
                  </td>
                  <td class="text-center">
                    <div class="d-flex justify-content-center gap-1">
                      <router-link :to="'/commandes-client/' + commande.id" class="btn btn-sm btn-light-primary text-primary" title="Voir Détails">
                        <i class="ti ti-eye"></i>
                      </router-link>
                      <button class="btn btn-sm btn-light-info text-info" @click="exportToPDF(commande.id)" title="PDF / Imprimer">
                        <i class="ti ti-file-export"></i>
                      </button>
                      <button v-if="(commande.statut === 'confirmee' || commande.statut === 'reservee') && isMagasinier" 
                              class="btn btn-sm btn-light-success text-success" 
                              @click="preparerLivraison(commande)" 
                              title="Préparer Livraison">
                        <i class="ti ti-truck-delivery"></i>
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
  name: 'CommandesClient',
  components: { MainLayout },
  data() {
    return {
      commandes: [],
      isLoading: false,
      errorMessage: ''
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
    this.fetchCommandes();
  },
  methods: {
    async fetchCommandes() {
      this.isLoading = true;
      this.errorMessage = '';
      try {
        const response = await axios.get('/api/commandes-client');
        this.commandes = response.data.sort((a, b) => new Date(b.dateCommande) - new Date(a.dateCommande));
      } catch (error) {
        console.error('Erreur fetching commandes:', error);
        this.errorMessage = 'Erreur lors du chargement des commandes.';
      } finally {
        this.isLoading = false;
      }
    },
    async preparerLivraison(commande) {
      if (confirm(`Voulez-vous initier la préparation du bon de livraison pour la commande ${commande.reference} ?`)) {
        try {
          await axios.post(`/api/commandes-client/${commande.id}/preparer-livraison`, {
            utilisateurId: this.currentUser.id
          });
          alert('Bon de livraison créé avec succès. La commande est maintenant en cours de préparation.');
          this.fetchCommandes();
        } catch (error) {
          const msg = error.response?.data || 'Erreur lors de la préparation';
          alert(typeof msg === 'string' ? msg : 'Erreur lors de la création du bon de livraison.');
        }
      }
    },
    formatDate(date) {
      if (!date) return '-';
      return new Date(date).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-MG', { style: 'currency', currency: 'MGA' }).format(value);
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
    exportToPDF(id) {
      this.$router.push(`/commandes-client/${id}`);
      setTimeout(() => {
        window.print();
      }, 500);
    },
    exportToExcel() {
      // Export simple au format CSV (lisible par Excel)
      let csvContent = "data:text/csv;charset=utf-8,";
      csvContent += "Reference;Client;Devis Source;Date;Montant Total;Statut\n";
      
      this.commandes.forEach(cmd => {
        const row = [
          cmd.reference,
          cmd.client?.nom || '',
          cmd.devis?.reference || 'N/A',
          this.formatDate(cmd.dateCommande),
          cmd.montantTotal,
          cmd.statut
        ].join(";");
        csvContent += row + "\n";
      });
      
      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      link.setAttribute("href", encodedUri);
      link.setAttribute("download", `liste_commandes_${new Date().toISOString().slice(0,10)}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
};
</script>
