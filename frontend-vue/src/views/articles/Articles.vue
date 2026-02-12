<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Gestion des Articles</h5>
            <router-link to="/articles/create" class="btn btn-primary">
              <i class="ti ti-plus"></i> Nouvel Article
            </router-link>
          </div>

          <!-- Filtres et Recherche -->
          <div class="row mb-4 g-3">
            <div class="col-md-4">
              <div class="input-group">
                <span class="input-group-text bg-transparent border-end-0">
                  <i class="ti ti-search text-muted"></i>
                </span>
                <input 
                  type="text" 
                  class="form-control border-start-0" 
                  placeholder="Rechercher par code ou nom..." 
                  v-model="searchQuery"
                >
              </div>
            </div>
            <div class="col-md-3">
              <select class="form-select" v-model="filterCategorie">
                <option value="">Toutes les catégories</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.nom }}
                </option>
              </select>
            </div>
            <div class="col-md-3">
              <select class="form-select" v-model="filterStatut">
                <option value="tous">Tous les statuts</option>
                <option value="actif">Actifs uniquement</option>
                <option value="inactif">Inactifs uniquement</option>
              </select>
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

          <div v-else-if="filteredArticles.length === 0" class="text-center py-5">
            <i class="ti ti-package-off fs-1 text-muted"></i>
            <p class="mt-3 text-muted">Aucun article trouvé</p>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Code</th>
                  <th>Désignation</th>
                  <th>Catégorie</th>
                  <th class="text-center">Valorisation</th>
                  <th class="text-end">Prix Achat</th>
                  <th class="text-end">Prix Vente</th>
                  <th class="text-center">Stock Min/Max</th>
                  <th class="text-center">Statut</th>
                  <th class="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="article in filteredArticles" :key="article.id">
                  <td>
                    <span class="fw-bold text-primary">{{ article.code }}</span>
                  </td>
                  <td>
                    <div class="d-flex flex-column">
                      <span class="fw-semibold">{{ article.nom }}</span>
                      <small class="text-muted text-truncate" style="max-width: 200px;">
                        {{ article.description || 'Pas de description' }}
                      </small>
                    </div>
                  </td>
                  <td>
                    <span class="badge bg-light-primary text-primary rounded-pill px-3">
                      {{ article.categorie?.nom || 'Sans catégorie' }}
                    </span>
                  </td>
                  <td class="text-center">
                    <span :class="getValorisationClass(article.methodeValorisation)">
                      {{ article.methodeValorisation || 'N/A' }}
                    </span>
                  </td>
                  <td class="text-end">{{ formatCurrency(article.prixAchat) }}</td>
                  <td class="text-end">{{ formatCurrency(article.prixVente) }}</td>
                  <td class="text-center">
                    <span class="text-muted small">
                      Min: {{ article.stockMin || 0 }} / Max: {{ article.stockMax || '∞' }}
                    </span>
                  </td>
                  <td class="text-center">
                    <span :class="article.actif ? 'badge bg-success' : 'badge bg-danger'">
                      {{ article.actif ? 'Actif' : 'Inactif' }}
                    </span>
                  </td>
                  <td class="text-end">
                    <div class="btn-group">
                      <router-link 
                        :to="`/articles/${article.id}`" 
                        class="btn btn-sm btn-outline-info"
                        title="Voir détails"
                      >
                        <i class="ti ti-eye"></i>
                      </router-link>
                      <router-link 
                        :to="`/articles/${article.id}/edit`" 
                        class="btn btn-sm btn-outline-warning"
                        title="Modifier"
                      >
                        <i class="ti ti-edit"></i>
                      </router-link>
                      <button 
                        class="btn btn-sm" 
                        :class="article.actif ? 'btn-outline-danger' : 'btn-outline-success'"
                        :title="article.actif ? 'Désactiver' : 'Activer'"
                        @click="toggleStatus(article)"
                      >
                        <i :class="article.actif ? 'ti ti-player-pause' : 'ti ti-player-play'"></i>
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
import MainLayout from '../../layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'Articles',
  components: {
    MainLayout
  },
  data() {
    return {
      articles: [],
      categories: [],
      searchQuery: '',
      filterCategorie: '',
      filterStatut: 'tous',
      isLoading: false,
      errorMessage: '',
      currentUser: null
    };
  },
  computed: {
    filteredArticles() {
      return this.articles.filter(article => {
        const matchesSearch = 
          article.nom.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          article.code.toLowerCase().includes(this.searchQuery.toLowerCase());
        
        const matchesCategorie = !this.filterCategorie || article.categorie?.id === parseInt(this.filterCategorie);
        
        const matchesStatut = 
          this.filterStatut === 'tous' || 
          (this.filterStatut === 'actif' && article.actif) || 
          (this.filterStatut === 'inactif' && !article.actif);
        
        return matchesSearch && matchesCategorie && matchesStatut;
      });
    }
  },
  mounted() {
    this.loadArticles();
    this.loadCategories();
    const authData = JSON.parse(localStorage.getItem('user'));
    if (authData) {
      this.currentUser = authData.user || authData;
    }
  },
  methods: {
    async loadArticles() {
      this.isLoading = true;
      try {
        const response = await axios.get('/api/articles');
        this.articles = response.data;
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur lors du chargement des articles';
      } finally {
        this.isLoading = false;
      }
    },
    async loadCategories() {
      try {
        const response = await axios.get('/api/categories-article');
        this.categories = response.data;
      } catch (error) {
        console.error('Erreur chargement catégories:', error);
      }
    },
    async toggleStatus(article) {
      if (!this.currentUser) return;
      
      const action = article.actif ? 'désactiver' : 'activer';
      if (!confirm(`Voulez-vous vraiment ${action} cet article ?`)) return;

      try {
        await axios.patch(`/api/articles/${article.id}/toggle-status`, null, {
          params: { utilisateurId: this.currentUser.id }
        });
        article.actif = !article.actif;
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur lors de la modification du statut');
      }
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'MGA',
        minimumFractionDigits: 0
      }).format(value || 0);
    },
    getValorisationClass(methode) {
      const base = 'badge rounded-pill px-3 ';
      switch (methode) {
        case 'FIFO': return base + 'bg-light-success text-success';
        case 'LIFO': return base + 'bg-light-warning text-warning';
        case 'CUMP': return base + 'bg-light-info text-info';
        default: return base + 'bg-light-secondary text-secondary';
      }
    }
  }
};
</script>

<style scoped>
.bg-light-primary {
  background-color: #ecf2ff;
}
.bg-light-success {
  background-color: #e6fffa;
}
.bg-light-warning {
  background-color: #fef5e5;
}
.bg-light-info {
  background-color: #e8f7ff;
}
.bg-light-secondary {
  background-color: #f6f9fc;
}
.text-truncate {
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>