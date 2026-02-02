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
      <div v-if="hasPermission('/dashboard/achats')" class="row g-3 g-md-4 mt-1 mb-4">
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
                  <div class="text-muted small">Délai moyen réel (Base de données)</div>
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

      <!-- Section: Gestion du Stock (Nouvelle) -->
      <div v-if="hasPermission('/dashboard/stock')" class="row g-3 g-md-4 mt-1 mb-4">
        <div class="col-12">
          <h5 class="fw-semibold mb-3 d-flex align-items-center">
            <i class="ti ti-package me-2 text-primary"></i> Gestion du Stock
          </h5>
        </div>
        
        <!-- 1. Valeur Totale du Stock -->
        <div class="col-12 col-md-6 col-xl-3">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-body">
              <div class="d-flex align-items-center justify-content-between mb-3">
                <div class="p-3 bg-light-primary rounded-3">
                  <i class="ti ti-database-dollar fs-6 text-primary"></i>
                </div>
                <div class="text-end">
                  <span class="text-muted small">Valeur Totale</span>
                  <div class="d-flex align-items-center justify-content-end text-success small">
                    <i class="ti ti-trending-up me-1"></i>
                    <span>{{ statistiques.stockTrend || '+5.2%' }}</span>
                  </div>
                </div>
              </div>
              <h3 class="fw-bold mb-1">{{ formatCurrency(statistiques.valeurTotaleStock || 0) }}</h3>
              <p class="text-muted small mb-0">Somme valorisée du stock</p>
            </div>
          </div>
        </div>

        <!-- 3. Rotation des Stocks -->
        <div class="col-12 col-md-6 col-xl-3">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-body">
              <div class="d-flex align-items-center justify-content-between mb-3">
                <div class="p-3 rounded-3" :class="`bg-light-${statistiques.rotationStatus || 'success'}`">
                  <i class="ti ti-refresh fs-6" :class="`text-${statistiques.rotationStatus || 'success'}`"></i>
                </div>
                <div class="text-end">
                  <span class="text-muted small">Rotation</span>
                  <span :class="`badge bg-${statistiques.rotationStatus || 'success'}-subtle text-${statistiques.rotationStatus || 'success'} rounded-pill ms-2`">
                    {{ getRotationLabel(statistiques.rotationStatus) }}
                  </span>
                </div>
              </div>
              <div class="d-flex align-items-baseline">
                <h3 class="fw-bold mb-1">{{ statistiques.rotationStock || '0.0' }}</h3>
                <span class="ms-2 text-muted small">fois par an</span>
              </div>
              <div class="mt-2">
                <div class="progress" style="height: 4px;">
                  <div class="progress-bar" 
                    :class="`bg-${statistiques.rotationStatus || 'success'}`" 
                    :style="{ width: getRotationProgress(statistiques.rotationStock) + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 2. Répartition du Stock par Dépôt (Horizontal Stacked Bar) -->
        <div class="col-12 col-xl-6">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-body">
              <div class="d-flex align-items-center justify-content-between mb-4">
                <h6 class="fw-bold mb-0">Répartition par Dépôt (MGA)</h6>
                <i class="ti ti-building-warehouse text-muted"></i>
              </div>
              <apexchart 
                type="bar" 
                height="150" 
                :options="stockDepotOptions" 
                :series="stockDepotSeries"
              ></apexchart>
              <div class="mt-3 d-flex flex-wrap gap-3 justify-content-center small text-muted">
                <div v-for="(depot, idx) in stockDepotLabels" :key="idx" class="d-flex align-items-center">
                  <span class="d-inline-block rounded-circle me-1" :style="{width: '8px', height: '8px', backgroundColor: stockDepotColors[idx]}"></span>
                  {{ depot }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 4. Consommation Budgétaire (Répartition & Détails) -->
        <div v-if="hasPermission('/dashboard/budget')" class="col-12">
          <div class="card border-0 shadow-sm">
            <div class="card-body">
              <div class="d-flex align-items-center justify-content-between mb-4">
                <div>
                  <h5 class="fw-bold mb-0">Consommation Budgétaire par Département</h5>
                  <p class="text-muted small mb-0">Analyse de l'utilisation des budgets annuels</p>
                </div>
                <i class="ti ti-chart-pie text-primary fs-6"></i>
              </div>
              
              <div class="row g-4">
                <!-- ZONE GAUCHE : Composant d'Analyse IA -->
                <div class="col-12 col-xl-12">
                  <BudgetChartAnalysis 
                    :pieSeries="pieBudgetSeries" 
                    :pieOptions="pieBudgetOptions"
                  />
                </div>

                <!-- ZONE DROITE : Jauges de consommation (Encadrée) -->
                <div class="col-12 col-xl-12 mt-4">
                  <div class="p-4 border rounded-4 bg-light-subtle h-100 border-2">
                    <h6 class="fw-semibold mb-4 text-center">Détail par département</h6>
                    <div class="row g-3">
                      <div v-for="(dept, idx) in statistiques.budgetConsommation" :key="idx" class="col-12 col-md-6 col-xxl-4">
                        <div class="text-center p-3 border rounded-3 bg-white shadow-sm h-100">
                          <h6 class="fw-semibold mb-2 small">{{ dept.departement }}</h6>
                          <apexchart 
                            type="radialBar" 
                            height="180" 
                            :options="getGaugeOptions(dept.pourcentage)" 
                            :series="[dept.pourcentage]"
                          ></apexchart>
                          <div class="mt-2 border-top pt-2">
                            <div class="d-flex justify-content-between x-small mb-1">
                              <span class="text-muted">Consommé:</span>
                              <span class="fw-bold text-primary">{{ formatCurrency(dept.consomme) }}</span>
                            </div>
                            <div class="d-flex justify-content-between x-small">
                              <span class="text-muted">Total:</span>
                              <span class="fw-bold">{{ formatCurrency(dept.initial) }}</span>
                            </div>
                            <div class="text-center mt-1">
                              <span class="badge bg-light-primary text-primary rounded-pill x-small">
                                {{ dept.pourcentage.toFixed(1) }}% utilisé
                              </span>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div v-if="!statistiques.budgetConsommation || statistiques.budgetConsommation.length === 0" class="col-12 text-center py-4">
                        <p class="text-muted">Aucune donnée budgétaire disponible</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 5. Analyse des Marges par Article (Nouveau Diagramme) -->
        <div v-if="hasPermission('/dashboard/margins')" class="col-12 mt-4">
          <div class="row g-4">
            <div class="col-12 col-xl-8">
              <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                  <div class="d-flex align-items-center justify-content-between mb-4">
                    <div>
                      <h5 class="fw-bold mb-0">Analyse des Marges par Article</h5>
                      <p class="text-muted small mb-0">Comparaison Marge Brute (%) et Prix de Vente (MGA)</p>
                    </div>
                    <div class="p-2 bg-light-success rounded-3">
                      <i class="ti ti-chart-bar fs-6 text-success"></i>
                    </div>
                  </div>
                  <apexchart 
                    type="bar" 
                    height="350" 
                    :options="marginChartOptions" 
                    :series="marginChartSeries"
                  ></apexchart>
                </div>
              </div>
            </div>

            <!-- 6. Répartition des Méthodes de Valorisation -->
            <div class="col-12 col-xl-4">
              <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                  <div class="d-flex align-items-center justify-content-between mb-4">
                    <div>
                      <h5 class="fw-bold mb-0">Méthodes de Valorisation</h5>
                      <p class="text-muted small mb-0">Répartition par type de calcul</p>
                    </div>
                    <div class="p-2 bg-light-info rounded-3">
                      <i class="ti ti-calculator fs-6 text-info"></i>
                    </div>
                  </div>
                  <apexchart 
                    type="pie" 
                    height="300" 
                    :options="valuationChartOptions" 
                    :series="valuationChartSeries"
                  ></apexchart>
                  <div class="mt-4">
                    <div v-for="(count, method) in statistiques.repartitionMethodeValorisation" :key="method" class="d-flex justify-content-between align-items-center mb-2 p-2 bg-light rounded-2">
                      <span class="fw-semibold">{{ method }}</span>
                      <span class="badge bg-primary rounded-pill">{{ count }} articles</span>
                    </div>
                  </div>
                </div>
              </div>
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
import BudgetChartAnalysis from '../components/BudgetChartAnalysis.vue';

export default {
  name: 'Dashboard',
  components: {
    MainLayout,
    apexchart: VueApexCharts,
    BudgetChartAnalysis,
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
      },
      // Options Stock par Dépôt
      stockDepotSeries: [],
      stockDepotLabels: [],
      stockDepotColors: ['#5D87FF', '#13DEB9', '#FFAE1F', '#FA896B', '#49BEFF'],
      stockDepotOptions: {
        chart: { type: 'bar', stacked: true, stackType: '100%', toolbar: { show: false }, fontFamily: 'inherit' },
        plotOptions: { bar: { horizontal: true, barHeight: '35%', borderRadius: 0 } },
        stroke: { width: 1, colors: ['#fff'] },
        xaxis: { categories: ['Valeur Stock'], labels: { show: false }, axisBorder: { show: false } },
        yaxis: { labels: { show: false } },
        tooltip: {
          y: { formatter: function (val) { return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA' }).format(val) } }
        },
        fill: { opacity: 1 },
        legend: { show: false }
      },
      // Options Pie Chart Budget
      pieBudgetSeries: [],
      pieBudgetOptions: {
        chart: { type: 'pie', fontFamily: 'inherit' },
        labels: [],
        colors: ['#5D87FF', '#13DEB9', '#FFAE1F', '#FA896B', '#49BEFF'],
        legend: { position: 'bottom' },
        dataLabels: {
          enabled: true,
          formatter: function (val) {
            return val.toFixed(1) + "%"
          }
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA', minimumFractionDigits: 0 }).format(val)
            }
          }
        }
      },
      // Options Marges par Article
      marginChartSeries: [],
      marginChartOptions: {
        chart: { type: 'bar', height: 350, toolbar: { show: true }, fontFamily: 'inherit' },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '55%',
            endingShape: 'rounded',
            dataLabels: { position: 'top' }
          },
        },
        dataLabels: {
          enabled: true,
          formatter: function (val, { seriesIndex }) {
            if (seriesIndex === 0) return val + "%";
            return new Intl.NumberFormat('fr-FR').format(val);
          },
          offsetY: -20,
          style: { fontSize: '12px', colors: ["#304758"] }
        },
        stroke: { show: true, width: 2, colors: ['transparent'] },
        xaxis: {
          categories: [],
          labels: { rotate: -45, trim: true, maxHeight: 120 }
        },
        yaxis: [
          {
            title: { text: 'Marge Brute (%)' },
            labels: { formatter: (val) => val + "%" },
            max: 100
          },
          {
            opposite: true,
            title: { text: 'Prix de Vente (MGA)' },
            labels: {
              formatter: (val) => new Intl.NumberFormat('fr-FR').format(val)
            }
          }
        ],
        fill: { opacity: 1 },
        tooltip: {
          shared: true,
          intersect: false,
          y: {
            formatter: function (val, { seriesIndex }) {
              if (seriesIndex === 0) return val + "%";
              return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA' }).format(val);
            }
          }
        },
        colors: ['#13DEB9', '#5D87FF'],
        legend: { position: 'top' }
      },
      // Options Méthodes de Valorisation
      valuationChartSeries: [],
      valuationChartOptions: {
        chart: { type: 'pie', fontFamily: 'inherit' },
        labels: [],
        colors: ['#5D87FF', '#13DEB9', '#FFAE1F', '#FA896B', '#49BEFF'],
        legend: { position: 'bottom' },
        dataLabels: { enabled: true },
        tooltip: {
          y: {
            formatter: function (val) {
              return val + " articles";
            }
          }
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

        // Mise à jour Stock par Dépôt
        if (this.statistiques.repartitionStockDepot) {
          this.stockDepotLabels = Object.keys(this.statistiques.repartitionStockDepot);
          this.stockDepotSeries = this.stockDepotLabels.map(depotName => {
            const categories = this.statistiques.repartitionStockDepot[depotName];
            const totalDepot = Object.values(categories).reduce((sum, val) => sum + val, 0);
            return {
              name: depotName,
              data: [totalDepot]
            };
          });
        }

        // Mise à jour Pie Chart Budget (Répartition)
        if (this.statistiques.budgetConsommation) {
          const distribution = {};
          this.statistiques.budgetConsommation.forEach(dept => {
            const name = dept.departement;
            distribution[name] = (distribution[name] || 0) + dept.initial;
          });
          this.pieBudgetOptions = { ...this.pieBudgetOptions, labels: Object.keys(distribution) };
          this.pieBudgetSeries = Object.values(distribution);
        }

        // Mise à jour Marges par Article
        if (this.statistiques.articleMargins) {
          this.marginChartOptions = {
            ...this.marginChartOptions,
            xaxis: { 
              categories: this.statistiques.articleMargins.map(a => `${a.nom} (${a.reference})`) 
            }
          };
          this.marginChartSeries = [
            {
              name: 'Marge Brute (%)',
              data: this.statistiques.articleMargins.map(a => a.margeBrute)
            },
            {
              name: 'Prix de Vente (MGA)',
              data: this.statistiques.articleMargins.map(a => a.prixVente)
            }
          ];
        }

        // Mise à jour Méthodes de Valorisation
        if (this.statistiques.repartitionMethodeValorisation) {
          this.valuationChartOptions = { 
            ...this.valuationChartOptions, 
            labels: Object.keys(this.statistiques.repartitionMethodeValorisation) 
          };
          this.valuationChartSeries = Object.values(this.statistiques.repartitionMethodeValorisation);
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
    hasPermission(path) {
      const userStr = localStorage.getItem('user');
      if (!userStr) return false;
      const authData = JSON.parse(userStr);
      const user = authData.user || authData;
      
      // Les administrateurs ont toutes les permissions
      if (user.roles?.some(r => r.id === 1 || r.nom.toUpperCase() === 'ADMINISTRATEUR')) {
        return true;
      }

      const permissionsStr = localStorage.getItem('permissions');
      if (!permissionsStr) return false;
      const permissions = JSON.parse(permissionsStr);
      
      return permissions.some(p => {
        if (p.path === path) return true;
        if (p.path !== '/' && path.startsWith(p.path + '/')) return true;
        return false;
      });
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
    },
    getRotationLabel(status) {
      if (status === 'success') return 'Optimal';
      if (status === 'warning') return 'Moyen';
      if (status === 'danger') return 'Faible';
      return 'N/A';
    },
    getRotationProgress(value) {
      if (!value) return 0;
      const val = parseFloat(value);
      return Math.min(100, val * 25); // 4.0 = 100%
    },
    getGaugeOptions(pourcentage) {
      let color = '#5D87FF'; // Primary
      if (pourcentage >= 90) color = '#FA896B'; // Danger
      else if (pourcentage >= 75) color = '#FFAE1F'; // Warning
      else color = '#13DEB9'; // Success

      return {
        chart: { type: 'radialBar', sparkline: { enabled: true } },
        plotOptions: {
          radialBar: {
            startAngle: -90,
            endAngle: 90,
            hollow: { size: '70%' },
            track: { background: "#e7e7e7", strokeWidth: '97%', margin: 5 },
            dataLabels: {
              name: { show: false },
              value: { offsetY: -2, fontSize: '16px', fontWeight: 'bold' }
            }
          }
        },
        grid: { padding: { top: -10 } },
        fill: { colors: [color] },
        labels: ['Consommation'],
      };
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
.bg-light-success {
  background-color: #e6fffa !important;
}
.bg-light-warning {
  background-color: #fef5e5 !important;
}
.bg-light-danger {
  background-color: #fdede8 !important;
}
.text-success {
  color: #13deb9 !important;
}
.text-warning {
  color: #ffae1f !important;
}
.text-danger {
  color: #fa896b !important;
}
  .bg-light-primary {
    background-color: #ecf2ff !important;
  }
  .display-4 {
  font-size: 2.5rem;
}
.x-small {
  font-size: 0.75rem;
}
.rounded-4 {
  border-radius: 1rem !important;
}
@media (min-width: 1200px) {
  .display-4 {
    font-size: 3.5rem;
  }
}
</style>
