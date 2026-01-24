<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Bons de Commande Fournisseur</h5>
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
                    <span :class="getStatutClass(bc.statut)">{{ bc.statut }}</span>
                  </td>
                  <td class="text-center">
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-primary" @click="voirDetails(bc.id)" title="Détails">
                        <i class="ti ti-eye"></i>
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
        const response = await fetch('/api/bons-commande-fournisseur');
        if (response.ok) {
          this.bonsCommande = await response.json();
        } else {
          this.errorMessage = 'Erreur lors du chargement des bons de commande';
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur';
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
    getStatutClass(statut) {
      const s = statut?.toLowerCase();
      switch (s) {
        case 'brouillon': return 'badge bg-secondary';
        case 'valide': return 'badge bg-info';
        case 'envoye': return 'badge bg-primary';
        case 'recu': return 'badge bg-success';
        case 'annule': return 'badge bg-danger';
        default: return 'badge bg-info';
      }
    },
    voirDetails(id) {
      // Pour l'instant on redirige vers le dashboard ou on affiche une alerte
      alert('Détails du BC ID: ' + id + ' (En cours de développement)');
    },
    modifier(id) {
      alert('Modification du BC ID: ' + id + ' (En cours de développement)');
    }
  }
};
</script>
