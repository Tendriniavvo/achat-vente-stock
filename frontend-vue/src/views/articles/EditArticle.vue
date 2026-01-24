<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">
              Modifier l'Article
            </h5>
            <button @click="$router.back()" class="btn btn-outline-secondary">
              <i class="ti ti-arrow-left"></i> Retour
            </button>
          </div>

          <div v-if="errorMessage" class="alert alert-danger" role="alert">
            {{ errorMessage }}
          </div>

          <form @submit.prevent="saveArticle">
            <div class="row">
              <!-- Informations Générales -->
              <div class="col-md-12 mb-4">
                <h6 class="fw-bold border-bottom pb-2">Informations Générales</h6>
              </div>
              
              <div class="col-md-3 mb-3">
                <label for="code" class="form-label">Code Article <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  id="code" 
                  class="form-control" 
                  v-model="form.code" 
                  required 
                  disabled
                  placeholder="Ex: ART-001"
                >
              </div>

              <div class="col-md-6 mb-3">
                <label for="nom" class="form-label">Désignation <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  id="nom" 
                  class="form-control" 
                  v-model="form.nom" 
                  required
                  placeholder="Nom de l'article"
                >
              </div>

              <div class="col-md-3 mb-3">
                <label for="categorie" class="form-label">Catégorie <span class="text-danger">*</span></label>
                <select id="categorie" class="form-select" v-model="form.categorieId" required>
                  <option value="" disabled>Choisir...</option>
                  <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                    {{ cat.nom }}
                  </option>
                </select>
              </div>

              <div class="col-md-12 mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea 
                  id="description" 
                  class="form-control" 
                  v-model="form.description" 
                  rows="3"
                  placeholder="Description détaillée de l'article..."
                ></textarea>
              </div>

              <!-- Prix et Configuration -->
              <div class="col-md-12 mb-4 mt-3">
                <h6 class="fw-bold border-bottom pb-2">Prix et Configuration</h6>
              </div>

              <div class="col-md-3 mb-3">
                <label for="prixAchat" class="form-label">Prix d'Achat (MGA)</label>
                <input 
                  type="number" 
                  id="prixAchat" 
                  class="form-control" 
                  v-model="form.prixAchat" 
                  min="0"
                >
              </div>

              <div class="col-md-3 mb-3">
                <label for="prixVente" class="form-label">Prix de Vente (MGA)</label>
                <input 
                  type="number" 
                  id="prixVente" 
                  class="form-control" 
                  v-model="form.prixVente" 
                  min="0"
                >
              </div>

              <div class="col-md-3 mb-3">
                <label for="unite" class="form-label">Unité de mesure <span class="text-danger">*</span></label>
                <select id="unite" class="form-select" v-model="form.uniteId" required>
                  <option value="" disabled>Choisir...</option>
                  <option v-for="u in unites" :key="u.id" :value="u.id">
                    {{ u.nom }} ({{ u.symbole }})
                  </option>
                </select>
              </div>

              <div class="col-md-3 mb-3">
                <label for="taxe" class="form-label">Taxe applicable</label>
                <select id="taxe" class="form-select" v-model="form.taxeId">
                  <option :value="null">Aucune</option>
                  <option v-for="t in taxes" :key="t.id" :value="t.id">
                    {{ t.nom }} ({{ t.taux }}%)
                  </option>
                </select>
              </div>

              <!-- Paramètres de Stock -->
              <div class="col-md-12 mb-4 mt-3">
                <h6 class="fw-bold border-bottom pb-2">Paramètres de Stock</h6>
              </div>

              <div class="col-md-3 mb-3">
                <label for="stockMin" class="form-label">Stock Minimum</label>
                <input 
                  type="number" 
                  id="stockMin" 
                  class="form-control" 
                  v-model="form.stockMin" 
                  min="0"
                >
                <div class="form-text">Alerte de réapprovisionnement</div>
              </div>

              <div class="col-md-3 mb-3">
                <label for="stockMax" class="form-label">Stock Maximum</label>
                <input 
                  type="number" 
                  id="stockMax" 
                  class="form-control" 
                  v-model="form.stockMax" 
                  min="0"
                >
                <div class="form-text">Capacité de stockage maximale</div>
              </div>

              <div class="col-md-3 mb-3">
                <label for="valorisation" class="form-label">Méthode de Valorisation</label>
                <select id="valorisation" class="form-select" v-model="form.methodeValorisation">
                  <option value="CUMP">CUMP (Coût Unitaire Moyen Pondéré)</option>
                  <option value="FIFO">FIFO (First In, First Out)</option>
                  <option value="LIFO">LIFO (Last In, First Out)</option>
                </select>
              </div>

              <div class="col-md-3 mb-3 d-flex align-items-end">
                <div class="form-check mb-2">
                  <input 
                    class="form-check-input" 
                    type="checkbox" 
                    id="traceable" 
                    v-model="form.traceableLot"
                  >
                  <label class="form-check-label" for="traceable">
                    Traçabilité par lot
                  </label>
                </div>
              </div>

              <div class="col-md-12 mt-4 text-end">
                <button type="button" @click="$router.back()" class="btn btn-light me-2">Annuler</button>
                <button type="submit" class="btn btn-primary" :disabled="isSaving">
                  <span v-if="isSaving" class="spinner-border spinner-border-sm me-1"></span>
                  Enregistrer les modifications
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../../layouts/MainLayout.vue';

export default {
  name: 'EditArticle',
  components: {
    MainLayout
  },
  data() {
    return {
      isSaving: false,
      errorMessage: '',
      categories: [],
      unites: [],
      taxes: [],
      currentUser: null,
      form: {
        id: 0,
        code: '',
        nom: '',
        description: '',
        categorieId: '',
        uniteId: '',
        taxeId: null,
        prixAchat: 0,
        prixVente: 0,
        methodeValorisation: 'CUMP',
        stockMin: 0,
        stockMax: 0,
        traceableLot: false,
        actif: true
      }
    };
  },
  async mounted() {
    const authData = JSON.parse(localStorage.getItem('user'));
    if (authData) {
      this.currentUser = authData.user || authData;
    }

    await Promise.all([
      this.loadCategories(),
      this.loadUnites(),
      this.loadTaxes()
    ]);

    if (this.$route.params.id) {
      this.loadArticle(this.$route.params.id);
    }
  },
  methods: {
    async loadCategories() {
      try {
        const response = await fetch('/api/categories-article');
        if (response.ok) this.categories = await response.json();
      } catch (error) { console.error(error); }
    },
    async loadUnites() {
      try {
        const response = await fetch('/api/unites');
        if (response.ok) this.unites = await response.json();
      } catch (error) { console.error(error); }
    },
    async loadTaxes() {
      try {
        const response = await fetch('/api/taxes');
        if (response.ok) this.taxes = await response.json();
      } catch (error) { console.error(error); }
    },
    async loadArticle(id) {
      try {
        const response = await fetch(`/api/articles/${id}`);
        if (response.ok) {
          const article = await response.json();
          this.form = {
            ...article,
            categorieId: article.categorie?.id,
            uniteId: article.unite?.id,
            taxeId: article.taxe?.id
          };
        }
      } catch (error) {
        this.errorMessage = 'Erreur lors du chargement de l\'article';
      }
    },
    async saveArticle() {
      if (!this.currentUser) return;
      
      this.isSaving = true;
      this.errorMessage = '';

      try {
        const articleData = {
          ...this.form,
          categorie: this.form.categorieId ? { id: this.form.categorieId } : null,
          unite: this.form.uniteId ? { id: this.form.uniteId } : null,
          taxe: this.form.taxeId ? { id: this.form.taxeId } : null
        };

        const response = await fetch(`/api/articles?utilisateurId=${this.currentUser.id}`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(articleData)
        });

        if (response.ok) {
          this.$router.push('/articles');
        } else {
          const error = await response.text();
          this.errorMessage = 'Erreur: ' + error;
        }
      } catch (error) {
        this.errorMessage = 'Erreur de connexion';
      } finally {
        this.isSaving = false;
      }
    }
  }
};
</script>