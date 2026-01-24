<template>
  <MainLayout>
    <div class="container-fluid">
      <div v-if="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Chargement...</span>
        </div>
      </div>

      <div v-else-if="!fournisseur" class="alert alert-warning">
        Fournisseur non trouvé. <router-link to="/fournisseurs">Retour à la liste</router-link>
      </div>

      <div v-else>
        <!-- En-tête -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h4 class="fw-bold mb-0">{{ fournisseur.nom }}</h4>
            <div class="d-flex align-items-center gap-2">
              <span :class="fournisseur.actif ? 'badge bg-success' : 'badge bg-danger'">
                {{ fournisseur.actif ? 'Actif' : 'Inactif' }}
              </span>
              <span v-if="stats.isStrategic" class="badge bg-primary">
                <i class="ti ti-star me-1"></i>Partenaire Stratégique
              </span>
            </div>
          </div>
          <div class="d-flex gap-2">
            <button @click="$router.back()" class="btn btn-outline-secondary">
              <i class="ti ti-arrow-left"></i> Retour
            </button>
            <router-link :to="`/fournisseurs/${fournisseur.id}/edit`" class="btn btn-warning">
              <i class="ti ti-edit"></i> Modifier
            </router-link>
          </div>
        </div>

        <div class="row">
          <!-- Informations et Stats -->
          <div class="col-lg-8">
            <!-- Fiche détaillée -->
            <div class="card shadow-none border mb-4">
              <div class="card-body">
                <h5 class="card-title fw-semibold mb-4 border-bottom pb-2">Informations Générales</h5>
                <div class="row g-4">
                  <div class="col-md-6">
                    <label class="small text-muted mb-1 d-block">Email</label>
                    <span class="fw-bold"><i class="ti ti-mail me-2"></i>{{ fournisseur.email || 'Non renseigné' }}</span>
                  </div>
                  <div class="col-md-6">
                    <label class="small text-muted mb-1 d-block">Téléphone</label>
                    <span class="fw-bold"><i class="ti ti-phone me-2"></i>{{ fournisseur.telephone || 'Non renseigné' }}</span>
                  </div>
                  <div class="col-md-12">
                    <label class="small text-muted mb-1 d-block">Adresse</label>
                    <p class="mb-0"><i class="ti ti-map-pin me-2"></i>{{ fournisseur.adresse || 'Aucune adresse' }}</p>
                  </div>
                  <div class="col-md-12">
                    <label class="small text-muted mb-1 d-block">Conditions Commerciales</label>
                    <div class="bg-light p-3 rounded">
                      {{ fournisseur.conditions || 'Aucune condition spécifique renseignée' }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Historique des commandes -->
            <div class="card shadow-none border">
              <div class="card-body">
                <h5 class="card-title fw-semibold mb-4 border-bottom pb-2">Historique des Commandes</h5>
                <div v-if="orders.length === 0" class="text-center py-4">
                  <i class="ti ti-shopping-cart-x fs-1 text-muted"></i>
                  <p class="text-muted mt-2">Aucune commande passée avec ce fournisseur</p>
                </div>
                <div v-else class="table-responsive">
                  <table class="table table-hover align-middle">
                    <thead>
                      <tr>
                        <th>Référence</th>
                        <th>Date</th>
                        <th>Montant</th>
                        <th class="text-center">Statut</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="order in orders" :key="order.id">
                        <td><span class="fw-bold">{{ order.reference }}</span></td>
                        <td>{{ formatDate(order.dateCommande) }}</td>
                        <td class="fw-semibold text-primary">{{ formatCurrency(order.montantTotal) }}</td>
                        <td class="text-center">
                          <span :class="getStatusBadgeClass(order.statut)">{{ order.statut }}</span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>

          <!-- Sidebar: Stats & Audit -->
          <div class="col-lg-4">
            <!-- Analyse de performance -->
            <div class="card shadow-none border mb-4 bg-light-primary border-primary">
              <div class="card-body">
                <h5 class="card-title fw-semibold mb-3">Analyse Performance</h5>
                <div class="mb-3">
                  <label class="small text-muted d-block">Volume d'affaires total</label>
                  <h3 class="fw-bold text-primary mb-0">{{ formatCurrency(stats.totalAmount) }}</h3>
                </div>
                <div class="mb-0">
                  <label class="small text-muted d-block">Nombre de commandes</label>
                  <h4 class="fw-bold mb-0">{{ stats.totalOrders }}</h4>
                </div>
                <hr>
                <div class="d-flex align-items-center">
                  <div v-if="stats.isStrategic" class="text-success">
                    <i class="ti ti-trending-up me-2 fs-5"></i>
                    <span class="fw-bold">Fournisseur Stratégique</span>
                  </div>
                  <div v-else-if="stats.totalOrders > 0" class="text-warning">
                    <i class="ti ti-alert-circle me-2 fs-5"></i>
                    <span class="fw-bold">Partenaire Standard</span>
                  </div>
                  <div v-else class="text-muted">
                    <i class="ti ti-clock me-2 fs-5"></i>
                    <span class="fw-bold">Nouveau Partenaire</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Journal d'audit -->
            <div class="card shadow-none border">
              <div class="card-body">
                <h5 class="card-title fw-semibold mb-4 border-bottom pb-2">Journal d'Audit</h5>
                <div v-if="history.length === 0" class="text-center py-3">
                  <p class="text-muted small">Aucun historique d'audit</p>
                </div>
                <div v-else class="timeline">
                  <div v-for="log in history" :key="log.id" class="timeline-item d-flex mb-3">
                    <div class="timeline-badge-wrapper me-3">
                      <div class="timeline-badge" :class="getAuditBadgeClass(log.action)"></div>
                      <div class="timeline-line"></div>
                    </div>
                    <div class="timeline-content">
                      <div class="d-flex justify-content-between align-items-center">
                        <span class="fw-bold small">{{ log.action }}</span>
                        <small class="text-muted" style="font-size: 0.7rem;">{{ formatDate(log.dateAction) }}</small>
                      </div>
                      <p class="small mb-0 text-muted">{{ log.details }}</p>
                      <small class="text-primary" style="font-size: 0.7rem;">Par: {{ log.utilisateur?.prenom }}</small>
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
import MainLayout from '../../layouts/MainLayout.vue';

export default {
  name: 'DetailsFournisseur',
  components: {
    MainLayout
  },
  data() {
    return {
      fournisseur: null,
      orders: [],
      history: [],
      stats: {
        totalOrders: 0,
        totalAmount: 0,
        isStrategic: false
      },
      isLoading: false
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    async loadData() {
      this.isLoading = true;
      const id = this.$route.params.id;
      try {
        const [fournisseurRes, ordersRes, historyRes, statsRes] = await Promise.all([
          fetch(`/api/fournisseurs/${id}`),
          fetch(`/api/fournisseurs/${id}/orders`),
          fetch(`/api/fournisseurs/${id}/history`),
          fetch(`/api/fournisseurs/${id}/stats`)
        ]);

        if (fournisseurRes.ok) this.fournisseur = await fournisseurRes.json();
        if (ordersRes.ok) this.orders = await ordersRes.json();
        if (historyRes.ok) this.history = await historyRes.json();
        if (statsRes.ok) this.stats = await statsRes.json();
      } catch (error) {
        console.error('Erreur chargement données fournisseur:', error);
      } finally {
        this.isLoading = false;
      }
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'MGA',
        minimumFractionDigits: 0
      }).format(value || 0);
    },
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      return new Intl.DateTimeFormat('fr-FR', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }).format(new Date(dateString));
    },
    getStatusBadgeClass(statut) {
      const classes = {
        'brouillon': 'badge bg-light-secondary text-secondary',
        'valide': 'badge bg-light-info text-info',
        'envoye': 'badge bg-light-primary text-primary',
        'recu': 'badge bg-light-success text-success',
        'annule': 'badge bg-light-danger text-danger'
      };
      return classes[statut] || 'badge bg-light text-dark';
    },
    getAuditBadgeClass(action) {
      switch (action) {
        case 'CRÉATION': return 'bg-success';
        case 'MODIFICATION': return 'bg-warning';
        case 'DÉSACTIVATION': return 'bg-danger';
        case 'ACTIVATION': return 'bg-success';
        default: return 'bg-primary';
      }
    }
  }
};
</script>

<style scoped>
.bg-light-primary { background-color: #ecf2ff; }
.timeline-item { position: relative; }
.timeline-badge-wrapper { display: flex; flex-direction: column; align-items: center; }
.timeline-badge { width: 10px; height: 10px; border-radius: 50%; z-index: 1; }
.timeline-line { flex-grow: 1; width: 2px; background-color: #ebf1f6; margin-top: 4px; }
.timeline-item:last-child .timeline-line { display: none; }
.badge.bg-light-primary { background-color: #ecf2ff; color: #5d87ff; }
.badge.bg-light-success { background-color: #e6fffa; color: #13deb9; }
.badge.bg-light-danger { background-color: #fef5e5; color: #fa896b; }
.badge.bg-light-info { background-color: #e8f7ff; color: #0085db; }
.badge.bg-light-secondary { background-color: #ebf1f6; color: #5a6a85; }
</style>
