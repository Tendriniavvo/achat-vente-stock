<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Bons de Commande Fournisseur</h5>
            <div class="d-flex gap-2">
              <button class="btn btn-outline-success" @click="exportToExcel" :disabled="isLoading || bonsCommande.length === 0">
                <i class="ti ti-file-spreadsheet me-1"></i>
                Excel
              </button>
            </div>
          </div>

          <div v-if="errorMessage" class="alert alert-danger" role="alert">
            {{ errorMessage }}
          </div>

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead>
                <tr>
                  <th>Référence</th>
                  <th>Date</th>
                  <th>Fournisseur</th>
                  <th>DA Source</th>
                  <th class="text-end">Montant Total</th>
                  <th class="text-center">Statut</th>
                  <th class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="bc in bonsCommande" :key="bc.id">
                  <td class="fw-bold">{{ bc.reference }}</td>
                  <td>{{ formatDate(bc.dateCommande) }}</td>
                  <td>
                    <span v-if="bc.fournisseur">{{ bc.fournisseur.nom }}</span>
                    <span v-else class="text-muted italic">Non défini</span>
                  </td>
                  <td>
                    <router-link v-if="bc.demandeAchat" :to="`/achats/${bc.demandeAchat.id}`">
                      {{ bc.demandeAchat.reference }}
                    </router-link>
                  </td>
                  <td class="text-end fw-bold text-primary">{{ formatCurrency(bc.montantTotal) }}</td>
                  <td class="text-center">
                    <span :class="getStatutClass(bc.statut)">{{ formatStatut(bc.statut) }}</span>
                  </td>
                  <td class="text-center">
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-primary" @click="voirDetails(bc.id)" title="Détails">
                        <i class="ti ti-eye"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-info" @click="exportToPDF(bc.id)" title="Exporter PDF">
                        <i class="ti ti-file-export"></i>
                      </button>
                      <button 
                        v-if="bc.statut === 'brouillon'" 
                        class="btn btn-sm btn-outline-warning" 
                        @click="modifier(bc.id)"
                        title="Modifier / Négocier"
                      >
                        <i class="ti ti-edit"></i>
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="bonsCommande.length === 0">
                  <td colspan="7" class="text-center py-4">Aucun bon de commande trouvé</td>
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
import MainLayout from '../../layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'BonsCommandeFournisseur',
  components: {
    MainLayout
  },
  data() {
    return {
      bonsCommande: [],
      isLoading: false,
      errorMessage: ''
    };
  },
  mounted() {
    this.loadBonsCommande();
  },
  methods: {
    async loadBonsCommande() {
      this.isLoading = true;
      try {
        const response = await axios.get('/api/bons-commande-fournisseur');
        this.bonsCommande = response.data;
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur lors du chargement des bons de commande';
      } finally {
        this.isLoading = false;
      }
    },
    formatDate(dateString) {
      if (!dateString) return '';
      return new Date(dateString).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    formatCurrency(value) {
      if (value === null || value === undefined) return '0 MGA';
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'MGA',
        minimumFractionDigits: 0
      }).format(value);
    },
    formatStatut(statut) {
      if (!statut) return '';
      const s = statut.toLowerCase();
      switch (s) {
        case 'brouillon': return 'Brouillon';
        case 'attente_validation': return 'Attente Validation (Resp. Achats)';
        case 'attente_approbation_finale': return 'Attente Approbation (DAF/DG)';
        case 'valide': return 'Validé';
        case 'envoye': return 'Envoyé au fournisseur';
        case 'recu': return 'Reçu / Livré';
        case 'annule': return 'Annulé';
        default: return statut;
      }
    },
    getStatutClass(statut) {
      const s = statut?.toLowerCase();
      switch (s) {
        case 'brouillon': return 'badge bg-secondary';
        case 'attente_validation': return 'badge bg-warning text-dark';
        case 'attente_approbation_finale': return 'badge bg-warning text-dark';
        case 'valide': return 'badge bg-success';
        case 'envoye': return 'badge bg-primary';
        case 'recu': return 'badge bg-info';
        case 'annule': return 'badge bg-danger';
        default: return 'badge bg-info';
      }
    },
    voirDetails(id) {
      this.$router.push(`/commandes-achat/${id}`);
    },
    modifier(id) {
      this.$router.push(`/commandes-achat/${id}/edit`);
    },
    exportToExcel() {
      // Export simple au format CSV (lisible par Excel)
      let csvContent = "data:text/csv;charset=utf-8,\uFEFF"; // Ajout du BOM pour l'encodage UTF-8
      csvContent += "Reference;Date;Fournisseur;DA Source;Montant Total;Statut\n";
      
      this.bonsCommande.forEach(bc => {
        const row = [
          bc.reference,
          this.formatDate(bc.dateCommande),
          bc.fournisseur?.nom || 'Non défini',
          bc.demandeAchat?.reference || '',
          bc.montantTotal,
          this.formatStatut(bc.statut)
        ].join(";");
        csvContent += row + "\n";
      });
      
      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      link.setAttribute("href", encodedUri);
      link.setAttribute("download", `bons_commande_${new Date().toISOString().slice(0,10)}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    exportToPDF(id) {
      // Navigation vers les détails puis impression
      this.$router.push(`/commandes-achat/${id}`);
      setTimeout(() => {
        window.print();
      }, 500);
    }
  }
};
</script>
