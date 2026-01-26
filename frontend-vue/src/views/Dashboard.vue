<template>
  <MainLayout>
    <div class="container-fluid">
      <!-- KPI Row -->
      <div class="row mb-4">
        <div v-if="hasRole('Administrateur') || hasRole('Acheteur') || hasRole('Utilisateur')" class="col-md-3">
          <div class="card border-start border-primary border-4">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1">Demandes d'Achat</p>
                  <h4 class="mb-0">{{ statistiques.totalDemandesAchat }}</h4>
                </div>
                <div class="ms-auto text-primary">
                  <i class="ti ti-shopping-cart fs-7"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="hasRole('Administrateur') || hasRole('Comptable') || hasRole('Commercial')" class="col-md-3">
          <div class="card border-start border-success border-4">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1">Ventes Totales</p>
                  <h4 class="mb-0">{{ formatCurrency(statistiques.montantTotalVentes) }}</h4>
                </div>
                <div class="ms-auto text-success">
                  <i class="ti ti-currency-dollar fs-7"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="hasRole('Administrateur') || hasRole('Magasinier')" class="col-md-3">
          <div class="card border-start border-warning border-4">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1">Alertes Stock</p>
                  <h4 class="mb-0 text-warning">{{ statistiques.stockAlertsCount }}</h4>
                </div>
                <div class="ms-auto text-warning">
                  <i class="ti ti-alert-triangle fs-7"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="hasRole('Administrateur') || hasRole('Comptable')" class="col-md-3">
          <div class="card border-start border-info border-4">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1">Budget Dispo.</p>
                  <h4 class="mb-0">{{ formatCurrency(statistiques.totalBudgetDisponible) }}</h4>
                </div>
                <div class="ms-auto text-info">
                  <i class="ti ti-wallet fs-7"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!--  Row 1: Performance Chart -->
      <div class="row">
        <div class="col-lg-8 d-flex align-items-stretch">
          <div class="card w-100">
            <div class="card-body">
              <h5 class="card-title fw-semibold mb-4">Performance Globale (Achats vs Ventes)</h5>
              <apexchart 
                type="area" 
                height="350" 
                :options="chartOptions" 
                :series="chartSeries"
              ></apexchart>
            </div>
          </div>
        </div>
        <div class="col-lg-4 d-flex align-items-stretch">
          <div class="card w-100">
            <div class="card-body">
              <h5 class="card-title fw-semibold mb-4">Alertes de Stock</h5>
              <div class="list-group list-group-flush">
                <div v-for="alert in statistiques.stockAlerts" :key="alert.id" class="list-group-item px-0">
                  <div class="d-flex align-items-center">
                    <div class="me-3">
                      <span class="badge bg-light-warning text-warning p-2">
                        <i class="ti ti-package fs-5"></i>
                      </span>
                    </div>
                    <div class="flex-grow-1">
                      <h6 class="mb-1 fw-semibold">{{ alert.article?.nom }}</h6>
                      <p class="mb-0 text-muted fs-2">Dépôt: {{ alert.depot?.nom }}</p>
                    </div>
                    <div class="text-end">
                      <span class="text-danger fw-bold">{{ alert.quantite }}</span>
                      <p class="mb-0 text-muted fs-2">Min: {{ alert.article?.stockMin }}</p>
                    </div>
                  </div>
                </div>
                <div v-if="!statistiques.stockAlerts || statistiques.stockAlerts.length === 0" class="text-center py-4">
                  <p class="text-muted">Aucune alerte de stock</p>
                </div>
              </div>
              <button v-if="statistiques.stockAlertsCount > 5" class="btn btn-outline-primary w-100 mt-3 btn-sm">
                Voir toutes les alertes
              </button>
            </div>
          </div>
        </div>
      </div>

      

      <!-- Row 2: Gestion des Achats (New Section) -->
      <div class="row g-3 g-md-4 mt-1 mb-4">
        <!-- 1. Répartition des Demandes par Statut (Donut Chart) -->
        <div class="col-12 col-xl-4">
          <div class="card achat-alert-card h-100 border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between mb-3">
                <div>
                  <div class="fw-bold fs-5">Demandes par Statut</div>
                  <div class="text-muted small">Répartition globale</div>
                </div>
                <i class="ti ti-chart-donut fs-2 text-primary"></i>
              </div>
              <apexchart 
                type="donut" 
                height="250" 
                :options="donutOptions" 
                :series="donutSeries"
              ></apexchart>
            </div>
          </div>
        </div>

        <!-- 2. Top 5 Fournisseurs (Bar Chart) -->
        <div class="col-12 col-xl-4">
          <div class="card achat-alert-card h-100 border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between mb-3">
                <div>
                  <div class="fw-bold fs-5">Top Fournisseurs</div>
                  <div class="text-muted small">Par volume d'achat (MGA)</div>
                </div>
                <i class="ti ti-building-store fs-2 text-success"></i>
              </div>
              <apexchart 
                type="bar" 
                height="250" 
                :options="barOptions" 
                :series="barSeries"
              ></apexchart>
            </div>
          </div>
        </div>

        <!-- 3. Efficacité Approbation (KPI Réel) -->
        <div class="col-12 col-xl-4">
          <div class="card achat-alert-card achat-alert-performance h-100 border-0 shadow-sm bg-light-primary">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between mb-4">
                <div>
                  <div class="fw-bold fs-5">Efficacité Approbation</div>
                  <div class="text-muted small">Délai moyen réel</div>
                </div>
                <i class="ti ti-clock-play fs-2 text-primary"></i>
              </div>
              
              <div class="text-center py-4">
                <h1 class="display-4 fw-bold text-primary mb-2">{{ statistiques.delaiMoyenApprobation || '0.0 jours' }}</h1>
                <p class="text-muted">Temps moyen entre soumission et validation finale</p>
              </div>

              <div class="mt-4 pt-2 border-top">
                <div class="d-flex justify-content-between align-items-center">
                  <span class="text-muted small">Objectif cible</span>
                  <span class="badge rounded-pill" 
                    :class="`bg-${statistiques.efficaciteColor || 'success'}-subtle text-${statistiques.efficaciteColor || 'success'}`">
                    {{ statistiques.efficaciteStatus || 'Sous contrôle' }}
                  </span>
                </div>
                <div class="progress mt-2" style="height: 6px;">
                  <div class="progress-bar" 
                    :class="`bg-${statistiques.efficaciteColor || 'primary'}`"
                    :style="{ width: (statistiques.efficaciteProgress || 0) + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Row 3: Gestion des Ventes -->
      <div class="row g-3 g-md-4 mt-1 mb-4">
        <div class="col-12">
          <div class="card achat-alert-card border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between mb-3">
                <div>
                  <div class="fw-bold fs-5">Gestion des Ventes</div>
                  <div class="text-muted small">Top 5 Articles les plus vendus (Volume)</div>
                </div>
                <i class="ti ti-shopping-cart fs-2 text-primary"></i>
              </div>
              <apexchart 
                type="bar" 
                height="300" 
                :options="topArticlesOptions" 
                :series="topArticlesSeries"
              ></apexchart>
            </div>
          </div>
        </div>
      </div>

      <!-- Row 4: Recent Demandes d'Achat -->
      <div class="row">
        <div class="col-lg-12 d-flex align-items-stretch">
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
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../layouts/MainLayout.vue';
import VueApexCharts from "vue3-apexcharts";
import axios from 'axios';

export default {
  name: 'Dashboard',
  components: {
    MainLayout,
    apexchart: VueApexCharts,
  },
  data() {
    return {
      recentesDemandes: [],
      statistiques: {
        totalDemandesAchat: 0,
        demandesEnAttente: 0,
        totalVentes: 0,
        montantTotalVentes: 0,
        stockAlertsCount: 0,
        stockAlerts: [],
        totalBudgetDisponible: 0
      },
      chartSeries: [
        { name: "Ventes", data: [] },
        { name: "Achats", data: [] }
      ],
      chartOptions: {
        chart: { height: 350, type: 'area', toolbar: { show: false }, fontFamily: 'inherit' },
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 2 },
        colors: ['#5D87FF', '#49BEFF'],
        xaxis: { categories: [] },
        tooltip: { theme: 'light' },
        fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.4, opacityTo: 0.1, stops: [0, 90, 100] } }
      },
      // Options Donut Chart
      donutSeries: [],
      donutOptions: {
        chart: { type: 'donut', fontFamily: 'inherit' },
        labels: [],
        colors: ['#FFAE1F', '#13DEB9', '#FA896B', '#5D87FF', '#49BEFF'],
        plotOptions: {
          pie: { donut: { size: '70%', labels: { show: true, total: { show: true, label: 'Total' } } } }
        },
        dataLabels: { enabled: false },
        legend: { position: 'bottom' }
      },
      // Options Bar Chart
      barSeries: [],
      barOptions: {
        chart: { type: 'bar', toolbar: { show: false }, fontFamily: 'inherit' },
        plotOptions: { bar: { borderRadius: 4, horizontal: true, barHeight: '50%' } },
        dataLabels: { enabled: false },
        xaxis: { categories: [] },
        colors: ['#13DEB9'],
        tooltip: {
          y: { formatter: function (val) { return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA' }).format(val) } }
        }
      }
    };
  },
  mounted() {
    this.loadData();
    this.loadStats();
    this.loadPerformance();
  },
  methods: {
    async loadData() {
      try {
        const res = await axios.get('/api/demandes-achat');
        this.recentesDemandes = res.data.slice(0, 5);
      } catch (err) {
        console.error('Erreur chargement demandes:', err);
      }
    },
    async loadStats() {
      try {
        const res = await axios.get('/api/dashboard/stats');
        this.statistiques = res.data;
        
        // Mise à jour Donut Chart (Statut Demandes)
        if (this.statistiques.demandesParStatut) {
          this.donutOptions = { ...this.donutOptions, labels: Object.keys(this.statistiques.demandesParStatut) };
          this.donutSeries = Object.values(this.statistiques.demandesParStatut);
        }

        // Mise à jour Bar Chart (Top Fournisseurs)
        if (this.statistiques.topFournisseurs) {
          this.barOptions = { 
            ...this.barOptions, 
            xaxis: { categories: this.statistiques.topFournisseurs.map(f => f.nom) } 
          };
          this.barSeries = [{ name: 'Volume Achat', data: this.statistiques.topFournisseurs.map(f => f.volume) }];
        }

      } catch (err) {
        console.error('Erreur chargement stats:', err);
      }
    },
    async loadPerformance() {
      try {
        const res = await axios.get('/api/dashboard/performance');
        const data = res.data;
        this.chartSeries = [
          { name: "Ventes", data: data.sales.map(d => d.amount) },
          { name: "Achats", data: data.purchases.map(d => d.amount) }
        ];
        this.chartOptions = {
          ...this.chartOptions,
          xaxis: { categories: data.sales.map(d => d.month) }
        };
      } catch (err) {
        console.error('Erreur chargement performance:', err);
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
      if (!demande.lignes || !Array.isArray(demande.lignes)) return 0;
      return demande.lignes.reduce((total, ligne) => {
        const prix = ligne.prixEstime || 0;
        const qte = ligne.quantite || 0;
        return total + (prix * qte);
      }, 0);
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
      return (statut === 'attente_finance') && (this.hasRole('Comptable') || this.hasRole('Administrateur'));
    },
    async verifierFonds(id) {
      if (!confirm('Voulez-vous vérifier la disponibilité des fonds ?')) return;
      try {
        await axios.post(`/api/demandes-achat/${id}/verifier-fonds`);
        alert('Disponibilité des fonds confirmée');
        this.loadData();
      } catch (error) {
        console.error('Erreur:', error);
        const errorMsg = error.response?.data || 'Fonds insuffisants';
        alert('Erreur: ' + errorMsg);
        this.loadData();
      }
    },
    canApprove(demande) {
      const userStr = localStorage.getItem('user');
      if (!userStr) return false;
      const authData = JSON.parse(userStr);
      const user = authData.user || authData;

      if (demande.demandeur && demande.demandeur.id === user.id) return false;

      const statut = demande.statut?.toLowerCase();
      if (statut === 'en attente') return this.hasRole('Acheteur') || this.hasRole('Administrateur');
      if (statut === 'fonds_confirmés' || statut === 'fonds_confirmes') return this.hasRole('Comptable') || this.hasRole('Administrateur');
      if (statut === 'attente_admin') return this.hasRole('Administrateur');
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
        const response = await axios.post(`/api/demandes-achat/${id}/approuver`, {
          validateurId: user.id
        });
        
        const updatedDemande = response.data;
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
      } catch (error) {
        console.error('Erreur:', error);
        const errorMsg = error.response?.data || 'Erreur de connexion au serveur';
        alert('Erreur lors de l\'approbation : ' + errorMsg);
      }
    },
    getStatutClass(statut) {
      const s = statut?.toLowerCase();
      if (s === 'approuvé' || s === 'approuve' || s === 'validee' || s === 'validée') return 'badge bg-success rounded-3 fw-semibold';
      if (s === 'en attente' || s === 'soumise') return 'badge bg-warning rounded-3 fw-semibold';
      if (s === 'attente_finance') return 'badge bg-primary rounded-3 fw-semibold';
      if (s === 'attente_admin') return 'badge bg-indigo rounded-3 fw-semibold';
      if (s === 'rejeté' || s === 'rejete' || s === 'rejetee') return 'badge bg-danger rounded-3 fw-semibold';
      if (s === 'transformé' || s === 'transforme') return 'badge bg-primary rounded-3 fw-semibold';
      return 'badge bg-secondary rounded-3 fw-semibold';
    }
  }
};
</script>

<style scoped>
.achat-alert-card {
  transition: transform 0.2s;
}
.achat-alert-card:hover {
  transform: translateY(-5px);
}
.bg-light-primary {
  background-color: #ecf2ff !important;
}
.display-4 {
  font-size: 2.5rem;
}
@media (min-width: 1200px) {
  .display-4 {
    font-size: 3.5rem;
  }
}
</style>
