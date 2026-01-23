<template>
  <MainLayout>
    <div class="container-fluid">
      <!--  Row 1 -->
      <div class="row">
        <div class="col-lg-8 d-flex align-items-stretch">
          <div class="card w-100">
            <div class="card-body">
              <div class="d-sm-flex d-block align-items-center justify-content-between mb-9">
                <div class="mb-3 mb-sm-0">
                  <h5 class="card-title fw-semibold">Aperçu des Demandes d'Achat</h5>
                </div>
              </div>
              <div class="table-responsive">
                <table class="table text-nowrap mb-0 align-middle">
                  <thead class="text-dark fs-4">
                    <tr>
                      <th class="border-bottom-0">
                        <h6 class="fw-semibold mb-0">Réf</h6>
                      </th>
                      <th class="border-bottom-0">
                        <h6 class="fw-semibold mb-0">Demandeur</h6>
                      </th>
                      <th class="border-bottom-0">
                        <h6 class="fw-semibold mb-0">Total</h6>
                      </th>
                      <th class="border-bottom-0">
                        <h6 class="fw-semibold mb-0">Statut</h6>
                      </th>
                      <th class="border-bottom-0">
                        <h6 class="fw-semibold mb-0">Date</h6>
                      </th>
                      <th class="border-bottom-0">
                        <h6 class="fw-semibold mb-0">Actions</h6>
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="demande in recentesDemandes" :key="demande.id">
                      <td class="border-bottom-0">
                        <h6 class="fw-semibold mb-0">{{ demande.reference }}</h6>
                      </td>
                      <td class="border-bottom-0">
                        <p class="mb-0 fw-normal">{{ demande.demandeur?.prenom }} {{ demande.demandeur?.nom }}</p>
                      </td>
                      <td class="border-bottom-0">
                        <p class="mb-0 fw-bold text-primary">{{ formatCurrency(calculerTotal(demande)) }}</p>
                      </td>
                      <td class="border-bottom-0">
                        <div class="d-flex align-items-center gap-2">
                          <span :class="getStatutClass(demande.statut)">{{ demande.statut }}</span>
                        </div>
                      </td>
                      <td class="border-bottom-0">
                        <p class="mb-0 fw-normal">{{ formatDate(demande.dateCreation) }}</p>
                      </td>
                      <td class="border-bottom-0">
                        <div class="d-flex gap-1">
                          <button 
                            class="btn btn-sm btn-light-primary" 
                            title="Détails"
                            @click="$router.push(`/achats/${demande.id}`)"
                          >
                            <i class="ti ti-eye"></i>
                          </button>
                          <button 
                            v-if="canVerifyFonds(demande)"
                            class="btn btn-sm btn-info" 
                            title="Vérifier les fonds"
                            @click="verifierFonds(demande.id)"
                          >
                            <i class="ti ti-coin"></i>
                          </button>
                          <button 
                            v-if="canApprove(demande)"
                            class="btn btn-sm btn-success" 
                            title="Approuver"
                            @click="approuver(demande.id)"
                          >
                            <i class="ti ti-check"></i>
                          </button>
                        </div>
                      </td>
                    </tr>
                    <tr v-if="recentesDemandes.length === 0">
                      <td colspan="4" class="text-center py-4">Aucune demande récente</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
        <div class="col-lg-4">
          <div class="row">
            <div class="col-lg-12">
              <!-- Yearly Breakup -->
              <div class="card overflow-hidden">
                <div class="card-body p-4">
                  <h5 class="card-title mb-9 fw-semibold">Statistiques Rapides</h5>
                  <div class="row align-items-center">
                    <div class="col-8">
                      <h4 class="fw-semibold mb-3">{{ statistiques.totalDemandes }}</h4>
                      <p class="text-muted mb-0">Total Demandes d'Achat</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-lg-12">
              <!-- Monthly Earnings -->
              <div class="card">
                <div class="card-body">
                  <div class="row aligh-items-start">
                    <div class="col-8">
                      <h5 class="card-title mb-9 fw-semibold"> Budgets </h5>
                      <h4 class="fw-semibold mb-3">{{ formatCurrency(statistiques.totalBudget) }}</h4>
                      <div class="d-flex align-items-center pb-1">
                        <span
                          class="me-2 rounded-circle bg-light-success round-20 d-flex align-items-center justify-content-center">
                          <i class="ti ti-arrow-up-left text-success"></i>
                        </span>
                        <p class="text-dark me-1 fs-3 mb-0">Disponibles</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../layouts/MainLayout.vue';

export default {
  name: 'Dashboard',
  components: {
    MainLayout
  },
  data() {
    return {
      recentesDemandes: [],
      statistiques: {
        totalDemandes: 0,
        totalBudget: 0
      }
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    async loadData() {
      try {
        // Charger les demandes récentes
        const resDemandes = await fetch('/api/demandes-achat');
        if (resDemandes.ok) {
          const data = await resDemandes.json();
          this.recentesDemandes = data.slice(0, 5);
          this.statistiques.totalDemandes = data.length;
        }

        // Charger les budgets pour les stats
        const resBudgets = await fetch('/api/budgets');
        if (resBudgets.ok) {
          const budgets = await resBudgets.json();
          this.statistiques.totalBudget = budgets.reduce((total, b) => total + (b.montantDisponible || 0), 0);
        }
      } catch (err) {
        console.error('Erreur chargement dashboard:', err);
      }
    },
    formatDate(dateString) {
      if (!dateString) return '';
      return new Date(dateString).toLocaleDateString('fr-FR');
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA', minimumFractionDigits: 0 }).format(value);
    },
    calculerTotal(demande) {
      if (!demande.lignes) return 0;
      return demande.lignes.reduce((total, ligne) => total + (ligne.prixEstime * ligne.quantite), 0);
    },
    hasRole(roleNom) {
      const userStr = localStorage.getItem('user');
      if (!userStr) return false;
      const authData = JSON.parse(userStr);
      const user = authData.user || authData;
      if (!user.roles) return false;
      return user.roles.some(r => r.nom.toUpperCase() === roleNom.toUpperCase());
    },
    canVerifyFonds(demande) {
      const userStr = localStorage.getItem('user');
      if (!userStr) return false;
      const authData = JSON.parse(userStr);
      const user = authData.user || authData;
      const statut = demande.statut?.toLowerCase();
      return (statut === 'attente_finance') && (this.hasRole('FINANCE') || this.hasRole('ADMIN'));
    },
    async verifierFonds(id) {
      if (!confirm('Voulez-vous vérifier la disponibilité des fonds ?')) return;
      try {
        const response = await fetch(`/api/demandes-achat/${id}/verifier-fonds`, {
          method: 'POST'
        });
        if (response.ok) {
          alert('Disponibilité des fonds confirmée');
          this.loadData();
        } else {
          const errorMsg = await response.text();
          alert('Erreur: ' + (errorMsg || 'Fonds insuffisants'));
          this.loadData();
        }
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
      }
    },
    canApprove(demande) {
      const userStr = localStorage.getItem('user');
      if (!userStr) return false;
      const authData = JSON.parse(userStr);
      const user = authData.user || authData;

      if (demande.demandeur && demande.demandeur.id === user.id) return false;

      const statut = demande.statut?.toLowerCase();
      if (statut === 'en attente') return this.hasRole('CHEF') || this.hasRole('ADMIN');
      if (statut === 'fonds_confirmés' || statut === 'fonds_confirmes') return this.hasRole('FINANCE') || this.hasRole('ADMIN');
      if (statut === 'attente_admin') return this.hasRole('ADMIN');
      return false;
    },
    async approuver(id) {
      if (!confirm('Voulez-vous approuver cette demande d\'achat ?')) return;
      try {
        const userStr = localStorage.getItem('user');
        if (!userStr) {
          alert('Utilisateur non connecté');
          return;
        }
        const authData = JSON.parse(userStr);
        const user = authData.user || authData;
        const response = await fetch(`/api/demandes-achat/${id}/approuver`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ validateurId: user.id })
        });
        if (response.ok) {
          const updatedDemande = await response.json();
          const newStatus = updatedDemande.statut;
          let message = 'Demande approuvée avec succès';
          
          if (newStatus === 'attente_finance') {
            message = 'Approbation N1 réussie. En attente de la Finance.';
          } else if (newStatus === 'attente_admin') {
            message = 'Approbation N2 réussie. En attente de l\'Administration.';
          } else if (newStatus === 'approuvé' || newStatus === 'approuve') {
            message = 'Demande approuvée définitivement.';
          }
          
          alert(message);
          this.loadData();
        } else {
          const errorMsg = await response.text();
          alert('Erreur lors de l\'approbation : ' + errorMsg);
        }
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
      }
    },
    getStatutClass(statut) {
      const s = statut?.toLowerCase();
      if (s === 'approuvé' || s === 'approuve') return 'badge bg-success rounded-3 fw-semibold';
      if (s === 'en attente') return 'badge bg-warning rounded-3 fw-semibold';
      if (s === 'attente_finance') return 'badge bg-primary rounded-3 fw-semibold';
      if (s === 'attente_admin') return 'badge bg-indigo rounded-3 fw-semibold';
      if (s === 'rejeté' || s === 'rejete') return 'badge bg-danger rounded-3 fw-semibold';
      if (s === 'transformé' || s === 'transforme') return 'badge bg-primary rounded-3 fw-semibold';
      return 'badge bg-secondary rounded-3 fw-semibold';
    }
  }
};
</script>
