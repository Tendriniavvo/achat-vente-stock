<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="row mb-4">
        <div class="col-12">
          <div class="d-flex align-items-center justify-content-between">
            <h4 class="fw-bold mb-0">Tableau de Bord des Achats</h4>
            <div class="d-flex gap-2">
              <button @click="loadStats" class="btn btn-outline-primary btn-sm">
                <i class="ti ti-refresh me-1"></i> Actualiser
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- KPI Row -->
      <div class="row mb-4">
        <div class="col-md-3">
          <div class="card border-start border-primary border-4 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1 small uppercase fw-bold">Demandes d'Achat</p>
                  <h4 class="mb-0 fw-bold">{{ statistiques.totalDemandesAchat }}</h4>
                </div>
                <div class="ms-auto text-primary p-2 bg-light-primary rounded-3">
                  <i class="ti ti-shopping-cart fs-7"></i>
                </div>
              </div>
              <div class="mt-2 small text-muted">
                <span class="text-warning fw-bold">{{ statistiques.demandesEnAttente }}</span> en attente d'approbation
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card border-start border-info border-4 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1 small uppercase fw-bold">Budget Disponible</p>
                  <h4 class="mb-0 fw-bold">{{ formatCurrency(statistiques.totalBudgetDisponible) }}</h4>
                </div>
                <div class="ms-auto text-info p-2 bg-light-info rounded-3">
                  <i class="ti ti-wallet fs-7"></i>
                </div>
              </div>
              <div class="mt-2 small text-muted">
                Sur {{ statistiques.budgetConsommation?.length || 0 }} départements
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card border-start border-success border-4 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1 small uppercase fw-bold">Efficacité Approbation</p>
                  <h4 class="mb-0 fw-bold">{{ statistiques.delaiMoyenApprobation || '0.0 j' }}</h4>
                </div>
                <div class="ms-auto text-success p-2 bg-light-success rounded-3">
                  <i class="ti ti-clock-play fs-7"></i>
                </div>
              </div>
              <div class="mt-2 small">
                <span :class="`badge bg-${statistiques.efficaciteColor}-subtle text-${statistiques.efficaciteColor} rounded-pill border-0` ">
                  {{ statistiques.efficaciteStatus }}
                </span>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card border-start border-warning border-4 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1 small uppercase fw-bold">Alertes Stock</p>
                  <h4 class="mb-0 fw-bold text-warning">{{ statistiques.stockAlertsCount }}</h4>
                </div>
                <div class="ms-auto text-warning p-2 bg-light-warning rounded-3">
                  <i class="ti ti-alert-triangle fs-7"></i>
                </div>
              </div>
              <div class="mt-2 small text-muted">
                Articles à réapprovisionner
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row g-4">
        <!-- 1. Répartition des Demandes par Statut -->
        <div class="col-12 col-xl-4">
          <div class="card h-100 border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between mb-3">
                <h5 class="fw-bold">Statut des Demandes</h5>
                <i class="ti ti-chart-donut fs-5 text-primary"></i>
              </div>
              <apexchart 
                type="donut" 
                height="280" 
                :options="donutOptions" 
                :series="donutSeries"
              ></apexchart>
            </div>
          </div>
        </div>

        <!-- 2. Top 5 Fournisseurs -->
        <div class="col-12 col-xl-4">
          <div class="card h-100 border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between mb-3">
                <h5 class="fw-bold">Top Fournisseurs</h5>
                <i class="ti ti-building-store fs-5 text-success"></i>
              </div>
              <p class="text-muted small">Par volume d'achat cumulé (MGA)</p>
              <apexchart 
                type="bar" 
                height="250" 
                :options="barOptions" 
                :series="barSeries"
              ></apexchart>
            </div>
          </div>
        </div>

        <!-- 3. Consommation Budgétaire -->
        <div class="col-12 col-xl-4">
          <div class="card h-100 border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between mb-3">
                <h5 class="fw-bold">Utilisation Budget</h5>
                <i class="ti ti-chart-pie fs-5 text-info"></i>
              </div>
              <div class="list-group list-group-flush mt-2">
                <div v-for="(dept, idx) in statistiques.budgetConsommation" :key="idx" class="list-group-item px-0 py-3 border-0">
                  <div class="d-flex justify-content-between mb-1">
                    <span class="fw-semibold small">{{ dept.departement }}</span>
                    <span class="small text-muted">{{ dept.pourcentage }}%</span>
                  </div>
                  <div class="progress" style="height: 6px;">
                    <div class="progress-bar" 
                      :class="{'bg-danger': dept.pourcentage > 90, 'bg-warning': dept.pourcentage > 75, 'bg-success': dept.pourcentage <= 75}"
                      :style="{ width: dept.pourcentage + '%' }"></div>
                  </div>
                  <div class="d-flex justify-content-between x-small mt-1 text-muted">
                    <span>Consommé: {{ formatCurrency(dept.consomme) }}</span>
                    <span>Total: {{ formatCurrency(dept.initial) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 4. Performance Achats vs Ventes (Tendance) -->
        <div class="col-12 col-lg-8">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-body">
              <h5 class="fw-bold mb-4 text-center">Tendance des Achats</h5>
              <apexchart 
                type="area" 
                height="350" 
                :options="chartOptions" 
                :series="purchaseOnlySeries"
              ></apexchart>
            </div>
          </div>
        </div>

        <!-- 5. Alertes Stock Détail -->
        <div class="col-12 col-lg-4">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-body">
              <h5 class="fw-bold mb-4">Besoins de Réapprovisionnement</h5>
              <div class="table-responsive">
                <table class="table align-middle table-sm">
                  <thead>
                    <tr class="text-muted small">
                      <th>Article</th>
                      <th>Stock</th>
                      <th>Min</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="alert in statistiques.stockAlerts" :key="alert.id">
                      <td>
                        <div class="fw-bold small">{{ alert.article?.nom }}</div>
                        <div class="x-small text-muted">{{ alert.depot?.nom }}</div>
                      </td>
                      <td class="text-danger fw-bold">{{ alert.quantite }}</td>
                      <td>{{ alert.article?.stockMin }}</td>
                    </tr>
                    <tr v-if="!statistiques.stockAlerts || statistiques.stockAlerts.length === 0">
                      <td colspan="3" class="text-center py-4 text-muted small">Aucune alerte de stock</td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <router-link to="/stock/niveaux" class="btn btn-light-primary btn-sm w-100 mt-3">
                Voir tout le stock
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '@/layouts/MainLayout.vue';
import VueApexCharts from "vue3-apexcharts";
import axios from 'axios';

export default {
  name: 'AchatDashboard',
  components: {
    MainLayout,
    apexchart: VueApexCharts,
  },
  data() {
    return {
      statistiques: {
        totalDemandesAchat: 0,
        demandesEnAttente: 0,
        totalBudgetDisponible: 0,
        stockAlertsCount: 0,
        stockAlerts: [],
        budgetConsommation: [],
        demandesParStatut: {},
        topFournisseurs: [],
        delaiMoyenApprobation: '0.0 jours',
        efficaciteStatus: 'Chargement...',
        efficaciteColor: 'primary',
        efficaciteProgress: 0
      },
      chartSeries: [],
      chartOptions: {
        chart: { height: 350, type: 'area', toolbar: { show: false }, fontFamily: 'inherit' },
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 2 },
        colors: ['#49BEFF'],
        xaxis: { categories: [] },
        tooltip: { theme: 'light' },
        fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.4, opacityTo: 0.1, stops: [0, 90, 100] } }
      },
      donutSeries: [],
      donutOptions: {
        chart: { type: 'donut', fontFamily: 'inherit' },
        labels: [],
        colors: ['#FFAE1F', '#13DEB9', '#FA896B', '#5D87FF', '#49BEFF', '#7C83C3'],
        plotOptions: {
          pie: { donut: { size: '70%', labels: { show: true, total: { show: true, label: 'Total' } } } }
        },
        dataLabels: { enabled: false },
        legend: { position: 'bottom' }
      },
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
  computed: {
    purchaseOnlySeries() {
      if (!this.chartSeries || this.chartSeries.length < 2) return [];
      // On ne garde que la série des achats (index 1 dans la réponse globale)
      return [this.chartSeries[1]];
    }
  },
  mounted() {
    this.loadStats();
    this.loadPerformance();
  },
  methods: {
    async loadStats() {
      try {
        const res = await axios.get('/api/dashboard/achats');
        this.statistiques = res.data;
        
        // Update Donut Chart
        if (this.statistiques.demandesParStatut) {
          this.donutOptions = { ...this.donutOptions, labels: Object.keys(this.statistiques.demandesParStatut) };
          this.donutSeries = Object.values(this.statistiques.demandesParStatut);
        }

        // Update Bar Chart
        if (this.statistiques.topFournisseurs) {
          this.barOptions = { 
            ...this.barOptions, 
            xaxis: { categories: this.statistiques.topFournisseurs.map(f => f.nom) } 
          };
          this.barSeries = [{ name: 'Volume Achat', data: this.statistiques.topFournisseurs.map(f => f.volume) }];
        }
      } catch (err) {
        console.error('Erreur chargement stats achat:', err);
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
    formatCurrency(value) {
      if (value === undefined || value === null) return '0 MGA';
      return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA', minimumFractionDigits: 0 }).format(value);
    }
  }
};
</script>

<style scoped>
.x-small {
  font-size: 0.7rem;
}
.bg-light-primary { background-color: rgba(93, 135, 255, 0.1) !important; }
.bg-light-success { background-color: rgba(19, 222, 185, 0.1) !important; }
.bg-light-info { background-color: rgba(73, 190, 255, 0.1) !important; }
.bg-light-warning { background-color: rgba(255, 174, 31, 0.1) !important; }

.card {
  transition: transform 0.2s;
}
.card:hover {
  transform: translateY(-5px);
}
</style>
