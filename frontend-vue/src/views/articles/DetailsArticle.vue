<template>
  <MainLayout>
    <div class="container-fluid">
      <div v-if="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Chargement...</span>
        </div>
      </div>

      <div v-else-if="!article" class="alert alert-warning">
        Article non trouvé. <router-link to="/articles">Retour à la liste</router-link>
      </div>

      <div v-else>
        <!-- En-tête -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h4 class="fw-bold mb-0">{{ article.nom }}</h4>
            <span class="text-muted">Code: {{ article.code }}</span>
          </div>
          <div class="d-flex gap-2">
            <button @click="$router.back()" class="btn btn-outline-secondary">
              <i class="ti ti-arrow-left"></i> Retour
            </button>
            <router-link :to="`/articles/${article.id}/edit`" class="btn btn-warning">
              <i class="ti ti-edit"></i> Modifier
            </router-link>
          </div>
        </div>

        <div class="row">
          <!-- Détails -->
          <div class="col-lg-8">
            <div class="card shadow-none border">
              <div class="card-body">
                <h5 class="card-title fw-semibold mb-4 border-bottom pb-2">Détails de l'Article</h5>
                <div class="row g-4">
                  <div class="col-md-6">
                    <label class="small text-muted mb-1 d-block">Catégorie</label>
                    <span class="badge bg-light-primary text-primary fs-3">{{ article.categorie?.nom || 'N/A' }}</span>
                  </div>
                  <div class="col-md-6">
                    <label class="small text-muted mb-1 d-block">Unité</label>
                    <span class="fw-bold">{{ article.unite?.nom }} ({{ article.unite?.symbole }})</span>
                  </div>
                  <div class="col-md-12">
                    <label class="small text-muted mb-1 d-block">Description</label>
                    <p class="mb-0">{{ article.description || 'Aucune description' }}</p>
                  </div>
                  
                  <div class="col-md-12 mt-4">
                    <h6 class="fw-bold border-bottom pb-2">Configuration Financière & Stock</h6>
                  </div>

                  <div class="col-md-4">
                    <label class="small text-muted mb-1 d-block">Prix d'Achat</label>
                    <span class="fw-bold fs-4 text-primary">{{ formatCurrency(article.prixAchat) }}</span>
                  </div>
                  <div class="col-md-4">
                    <label class="small text-muted mb-1 d-block">Prix de Vente</label>
                    <span class="fw-bold fs-4 text-success">{{ formatCurrency(article.prixVente) }}</span>
                  </div>
                  <div class="col-md-4">
                    <label class="small text-muted mb-1 d-block">Taxe</label>
                    <span>{{ article.taxe ? `${article.taxe.nom} (${article.taxe.taux}%)` : 'Aucune' }}</span>
                  </div>

                  <div class="col-md-4">
                    <label class="small text-muted mb-1 d-block">Stock Minimum</label>
                    <span class="badge bg-light-warning text-warning fs-3">{{ article.stockMin || 0 }}</span>
                  </div>
                  <div class="col-md-4">
                    <label class="small text-muted mb-1 d-block">Stock Maximum</label>
                    <span class="badge bg-light-info text-info fs-3">{{ article.stockMax || 'Illimité' }}</span>
                  </div>
                  <div class="col-md-4">
                    <label class="small text-muted mb-1 d-block">Valorisation</label>
                    <span class="fw-semibold">{{ article.methodeValorisation }}</span>
                  </div>

                  <div class="col-md-4">
                    <label class="small text-muted mb-1 d-block">Traçabilité</label>
                    <span :class="article.traceableLot ? 'text-success' : 'text-muted'">
                      <i :class="article.traceableLot ? 'ti ti-check' : 'ti ti-x'"></i>
                      Lot / Numéro de série
                    </span>
                  </div>
                  <div class="col-md-4">
                    <label class="small text-muted mb-1 d-block">Statut</label>
                    <span :class="article.actif ? 'badge bg-success' : 'badge bg-danger'">
                      {{ article.actif ? 'Actif' : 'Inactif' }}
                    </span>
                  </div>
                  <div class="col-md-4">
                    <label class="small text-muted mb-1 d-block">Date de création</label>
                    <span>{{ formatDate(article.dateCreation) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Historique -->
          <div class="col-lg-4">
            <div class="card shadow-none border">
              <div class="card-body">
                <h5 class="card-title fw-semibold mb-4 border-bottom pb-2">Historique / Audit</h5>
                <div v-if="history.length === 0" class="text-center py-4">
                  <p class="text-muted small">Aucun historique disponible</p>
                </div>
                <div v-else class="timeline">
                  <div v-for="log in history" :key="log.id" class="timeline-item d-flex mb-3">
                    <div class="timeline-badge-wrapper me-3">
                      <div class="timeline-badge" :class="getBadgeClass(log.action)"></div>
                      <div class="timeline-line"></div>
                    </div>
                    <div class="timeline-content">
                      <div class="d-flex justify-content-between align-items-center">
                        <span class="fw-bold small">{{ log.action }}</span>
                        <small class="text-muted">{{ formatDate(log.dateAction) }}</small>
                      </div>
                      <p class="small mb-1">{{ log.details }}</p>
                      <small class="text-primary">Par: {{ log.utilisateur?.prenom }} {{ log.utilisateur?.nom }}</small>
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
  name: 'DetailsArticle',
  components: {
    MainLayout
  },
  data() {
    return {
      article: null,
      history: [],
      isLoading: false,
      errorMessage: ''
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
        const articleRes = await fetch(`/api/articles/${id}`);
        if (articleRes.ok) {
          this.article = await articleRes.json();
          this.loadHistory(this.article.code);
        }
      } catch (error) {
        console.error(error);
      } finally {
        this.isLoading = false;
      }
    },
    async loadHistory(code) {
      try {
        const res = await fetch(`/api/articles/${code}/history`);
        if (res.ok) {
          this.history = await res.json();
        }
      } catch (error) {
        console.error(error);
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
      if (!dateString) return '';
      return new Intl.DateTimeFormat('fr-FR', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }).format(new Date(dateString));
    },
    getBadgeClass(action) {
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
.bg-light-warning { background-color: #fef5e5; }
.bg-light-info { background-color: #e8f7ff; }

.timeline-item {
  position: relative;
}
.timeline-badge-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.timeline-badge {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  z-index: 1;
}
.timeline-line {
  flex-grow: 1;
  width: 2px;
  background-color: #ebf1f6;
  margin-top: 4px;
}
.timeline-item:last-child .timeline-line {
  display: none;
}
</style>