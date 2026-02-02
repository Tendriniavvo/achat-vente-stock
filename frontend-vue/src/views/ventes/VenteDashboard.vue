<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="row mb-4">
        <div class="col-12">
          <div class="d-flex align-items-center justify-content-between">
            <h4 class="fw-bold mb-0">Tableau de Bord des Ventes</h4>
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
        <div class="col-md-4">
          <div class="card border-start border-primary border-4 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1 small uppercase fw-bold">Chiffre d'Affaires Total</p>
                  <h4 class="mb-0 fw-bold">{{ formatCurrency(statistiques.montantTotalVentes) }}</h4>
                </div>
                <div class="ms-auto text-primary p-2 bg-light-primary rounded-3">
                  <i class="ti ti-currency-dollar fs-7"></i>
                </div>
              </div>
              <div class="mt-2 small text-muted">
                Cumul historique des ventes
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card border-start border-success border-4 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1 small uppercase fw-bold">Nombre de Commandes</p>
                  <h4 class="mb-0 fw-bold">{{ statistiques.totalVentes }}</h4>
                </div>
                <div class="ms-auto text-success p-2 bg-light-success rounded-3">
                  <i class="ti ti-shopping-cart fs-7"></i>
                </div>
              </div>
              <div class="mt-2 small text-muted">
                Commandes clients enregistrées
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card border-start border-info border-4 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center">
                <div>
                  <p class="text-muted mb-1 small uppercase fw-bold">Marge Moyenne Top Articles</p>
                  <h4 class="mb-0 fw-bold">{{ averageMargin }}%</h4>
                </div>
                <div class="ms-auto text-info p-2 bg-light-info rounded-3">
                  <i class="ti ti-trending-up fs-7"></i>
                </div>
              </div>
              <div class="mt-2 small text-muted">
                Basé sur les 10 meilleures marges
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row g-4">
        <!-- 1. Tendance des Ventes (Area Chart) -->
        <div class="col-12 col-lg-8">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-body">
              <h5 class="fw-bold mb-4">Tendance du Chiffre d'Affaires</h5>
              <apexchart 
                type="area" 
                height="350" 
                :options="chartOptions" 
                :series="chartSeries"
              ></apexchart>
            </div>
          </div>
        </div>

        <!-- 2. Top Articles par Quantité (Bar Chart) -->
        <div class="col-12 col-lg-4">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-body">
              <h5 class="fw-bold mb-4">Top 5 Articles Vendus</h5>
              <apexchart 
                type="bar" 
                height="350" 
                :options="barOptions" 
                :series="barSeries"
              ></apexchart>
            </div>
          </div>
        </div>

        <!-- 3. Analyse des Marges (Table) -->
        <div class="col-12">
          <div class="card border-0 shadow-sm">
            <div class="card-body">
              <h5 class="fw-bold mb-4">Analyse des Marges par Article</h5>
              <div class="table-responsive">
                <table class="table align-middle">
                  <thead>
                    <tr class="text-muted small">
                      <th>Référence</th>
                      <th>Désignation</th>
                      <th>Prix de Vente</th>
                      <th>Marge Brute (%)</th>
                      <th>Méthode Valorisation</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="margin in statistiques.articleMargins" :key="margin.reference">
                      <td><span class="badge bg-light-primary text-primary">{{ margin.reference }}</span></td>
                      <td class="fw-bold">{{ margin.nom }}</td>
                      <td>{{ formatCurrency(margin.prixVente) }}</td>
                      <td>
                        <div class="d-flex align-items-center">
                          <span class="me-2 fw-bold" :class="margin.margeBrute > 20 ? 'text-success' : 'text-warning'">
                            {{ margin.margeBrute }}%
                          </span>
                          <div class="progress flex-grow-1" style="height: 6px;">
                            <div class="progress-bar" 
                              :class="margin.margeBrute > 20 ? 'bg-success' : 'bg-warning'"
                              :style="{ width: margin.margeBrute + '%' }"></div>
                          </div>
                        </div>
                      </td>
                      <td>{{ margin.methode }}</td>
                    </tr>
                    <tr v-if="!statistiques.articleMargins || statistiques.articleMargins.length === 0">
                      <td colspan="5" class="text-center py-4 text-muted">Aucune donnée de marge disponible</td>
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
import MainLayout from '@/layouts/MainLayout.vue';
import VueApexCharts from "vue3-apexcharts";
import axios from 'axios';

export default {
  name: 'VenteDashboard',
  components: {
    MainLayout,
    apexchart: VueApexCharts,
  },
  data() {
    return {
      statistiques: {
        totalVentes: 0,
        montantTotalVentes: 0,
        topArticlesVendus: [],
        articleMargins: [],
        salesTrend: []
      },
      chartSeries: [],
      chartOptions: {
        chart: { height: 350, type: 'area', toolbar: { show: false }, fontFamily: 'inherit' },
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 2 },
        colors: ['#5D87FF'],
        xaxis: { categories: [] },
        tooltip: { theme: 'light' },
        fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.4, opacityTo: 0.1, stops: [0, 90, 100] } }
      },
      barSeries: [],
      barOptions: {
        chart: { type: 'bar', toolbar: { show: false }, fontFamily: 'inherit' },
        plotOptions: { bar: { borderRadius: 4, horizontal: false, columnWidth: '40%' } },
        dataLabels: { enabled: false },
        xaxis: { categories: [] },
        colors: ['#13DEB9'],
        tooltip: { theme: 'light' }
      }
    };
  },
  computed: {
    averageMargin() {
      if (!this.statistiques.articleMargins || this.statistiques.articleMargins.length === 0) return 0;
      const sum = this.statistiques.articleMargins.reduce((acc, curr) => acc + curr.margeBrute, 0);
      return (sum / this.statistiques.articleMargins.length).toFixed(1);
    }
  },
  mounted() {
    this.loadStats();
  },
  methods: {
    async loadStats() {
      try {
        const res = await axios.get('/api/dashboard/ventes');
        this.statistiques = res.data;
        
        // Update Area Chart (Trend)
        if (this.statistiques.salesTrend) {
          this.chartSeries = [{ name: "Chiffre d'Affaires", data: this.statistiques.salesTrend.map(d => d.amount) }];
          this.chartOptions = { ...this.chartOptions, xaxis: { categories: this.statistiques.salesTrend.map(d => d.month) } };
        }

        // Update Bar Chart (Top Articles)
        if (this.statistiques.topArticlesVendus) {
          this.barSeries = [{ name: "Quantité Vendue", data: this.statistiques.topArticlesVendus.map(a => a.quantite) }];
          this.barOptions = { ...this.barOptions, xaxis: { categories: this.statistiques.topArticlesVendus.map(a => a.nom) } };
        }
      } catch (err) {
        console.error('Erreur chargement stats vente:', err);
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
.bg-light-primary { background-color: rgba(93, 135, 255, 0.1) !important; }
.bg-light-success { background-color: rgba(19, 222, 185, 0.1) !important; }
.bg-light-info { background-color: rgba(73, 190, 255, 0.1) !important; }

.card {
  transition: transform 0.2s;
}
.card:hover {
  transform: translateY(-5px);
}
</style>
