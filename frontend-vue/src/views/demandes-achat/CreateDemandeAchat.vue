<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Nouvelle Demande d'Achat</h5>
            <router-link to="/achats" class="btn btn-secondary">
              <i class="ti ti-arrow-left"></i> Retour
            </router-link>
          </div>

          <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ errorMessage }}
            <button type="button" class="btn-close" @click="errorMessage = ''"></button>
          </div>

          <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
            {{ successMessage }}
            <button type="button" class="btn-close" @click="successMessage = ''"></button>
          </div>

          <form @submit.prevent="handleSubmit">
            <!-- Informations générales -->
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="reference" class="form-label">Référence <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="reference" 
                  v-model="form.reference" 
                  placeholder="Ex: DA-2026-001"
                  required
                >
              </div>
              <div class="col-md-6 mb-3">
                <label for="demandeur" class="form-label">Demandeur <span class="text-danger">*</span></label>
                <select 
                  class="form-select" 
                  id="demandeur" 
                  v-model="form.demandeurId" 
                  required
                >
                  <option value="">Sélectionnez un demandeur</option>
                  <option v-for="utilisateur in utilisateurs" :key="utilisateur.id" :value="utilisateur.id">
                    {{ utilisateur.prenom }} {{ utilisateur.nom }} ({{ utilisateur.email }})
                  </option>
                </select>
              </div>
            </div>

            <!-- Section Lignes de demande -->
            <div class="card bg-light mb-4">
              <div class="card-body">
                <h6 class="card-title mb-3">Articles demandés</h6>
                
                <!-- Lignes existantes -->
                <div v-for="(ligne, index) in form.lignes" :key="index" class="row mb-3 align-items-end">
                  <div class="col-md-4">
                    <label class="form-label">Article <span class="text-danger">*</span></label>
                    <select 
                      class="form-select" 
                      v-model="ligne.articleId" 
                      required
                      @change="updatePrixEstime(index)"
                    >
                      <option value="">Sélectionnez un article</option>
                      <option v-for="article in articles" :key="article.id" :value="article.id">
                        {{ article.code }} - {{ article.nom }}
                      </option>
                    </select>
                  </div>
                  <div class="col-md-3">
                    <label class="form-label">Quantité <span class="text-danger">*</span></label>
                    <input 
                      type="number" 
                      class="form-control" 
                      v-model.number="ligne.quantite" 
                      min="1"
                      required
                    >
                  </div>
                  <div class="col-md-3">
                    <label class="form-label">Prix Estimé (Ar)</label>
                    <input 
                      type="number" 
                      class="form-control" 
                      v-model.number="ligne.prixEstime" 
                      step="0.01"
                      min="0"
                    >
                  </div>
                  <div class="col-md-2">
                    <button 
                      type="button" 
                      class="btn btn-danger w-100" 
                      @click="removeLigne(index)"
                      :disabled="form.lignes.length === 1"
                    >
                      <i class="ti ti-trash"></i> Supprimer
                    </button>
                  </div>
                </div>

                <!-- Bouton Ajouter ligne -->
                <button 
                  type="button" 
                  class="btn btn-outline-primary btn-sm" 
                  @click="addLigne"
                >
                  <i class="ti ti-plus"></i> Ajouter un article
                </button>
              </div>
            </div>

            <!-- Total estimé -->
            <div class="row mb-4">
              <div class="col-md-12 text-end">
                <h5 class="mb-0">
                  Total Estimé: <span class="text-primary">{{ formatCurrency(totalEstime) }}</span>
                </h5>
              </div>
            </div>

            <!-- Boutons d'action -->
            <div class="d-flex justify-content-end gap-2">
              <router-link to="/achats" class="btn btn-light">Annuler</router-link>
              <button type="submit" class="btn btn-primary" :disabled="isLoading || form.lignes.length === 0">
                <span v-if="isLoading">
                  <span class="spinner-border spinner-border-sm me-2" role="status"></span>
                  Enregistrement...
                </span>
                <span v-else>
                  <i class="ti ti-device-floppy"></i> Enregistrer
                </span>
              </button>
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
  name: 'CreateDemandeAchat',
  components: {
    MainLayout
  },
  data() {
    return {
      form: {
        reference: '',
        demandeurId: null,
        lignes: [
          {
            articleId: '',
            quantite: 1,
            prixEstime: 0
          }
        ]
      },
      articles: [],
      utilisateurs: [],
      user: null,
      isLoading: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  computed: {
    totalEstime() {
      return this.form.lignes.reduce((total, ligne) => {
        const prix = ligne.prixEstime || 0;
        const quantite = ligne.quantite || 0;
        return total + (prix * quantite);
      }, 0);
    }
  },
  mounted() {
    this.loadUserData();
    this.loadUtilisateurs();
    this.loadArticles();
    this.generateReference();
  },
  methods: {
    loadUserData() {
      const userData = localStorage.getItem('user');
      if (userData) {
        this.user = JSON.parse(userData);
        // Pré-sélectionner l'utilisateur connecté par défaut
        this.form.demandeurId = this.user.id;
      } else {
        this.$router.push('/login');
      }
    },
    async loadUtilisateurs() {
      console.log('Chargement des utilisateurs...');
      try {
        const response = await fetch('http://localhost:8080/api/utilisateurs');
        console.log('Réponse utilisateurs:', response.status);
        if (response.ok) {
          this.utilisateurs = await response.json();
          console.log('Utilisateurs chargés:', this.utilisateurs.length);
        } else {
          console.error('Erreur HTTP:', response.status);
          this.errorMessage = 'Erreur lors du chargement des utilisateurs';
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur pour les utilisateurs';
      }
    },
    async loadArticles() {
      try {
        const response = await fetch('http://localhost:8080/api/articles');
        if (response.ok) {
          this.articles = await response.json();
        } else {
          this.errorMessage = 'Erreur lors du chargement des articles';
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur';
      }
    },
    generateReference() {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      const seconds = String(now.getSeconds()).padStart(2, '0');
      this.form.reference = `DA-${year}${month}${day}-${hours}${minutes}${seconds}`;
    },
    addLigne() {
      this.form.lignes.push({
        articleId: '',
        quantite: 1,
        prixEstime: 0
      });
    },
    removeLigne(index) {
      if (this.form.lignes.length > 1) {
        this.form.lignes.splice(index, 1);
      }
    },
    updatePrixEstime(index) {
      const ligne = this.form.lignes[index];
      if (ligne.articleId) {
        const article = this.articles.find(a => a.id === parseInt(ligne.articleId));
        if (article && article.prixAchat) {
          ligne.prixEstime = article.prixAchat;
        }
      }
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'MGA',
        minimumFractionDigits: 0
      }).format(value);
    },
    async handleSubmit() {
      // Validation
      if (!this.form.reference.trim()) {
        this.errorMessage = 'La référence est obligatoire';
        return;
      }

      if (this.form.lignes.length === 0) {
        this.errorMessage = 'Ajoutez au moins un article';
        return;
      }

      // Vérifier que tous les articles sont sélectionnés
      const missingArticle = this.form.lignes.some(ligne => !ligne.articleId);
      if (missingArticle) {
        this.errorMessage = 'Veuillez sélectionner un article pour chaque ligne';
        return;
      }

      // Vérifier les quantités
      const invalidQuantite = this.form.lignes.some(ligne => !ligne.quantite || ligne.quantite < 1);
      if (invalidQuantite) {
        this.errorMessage = 'Les quantités doivent être supérieures à 0';
        return;
      }

      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      try {
        const response = await fetch('http://localhost:8080/api/demandes-achat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.form)
        });

        if (response.ok) {
          const data = await response.json();
          this.successMessage = `Demande d'achat ${data.reference} créée avec succès!`;
          
          // Rediriger vers la liste après 2 secondes
          setTimeout(() => {
            this.$router.push('/achats');
          }, 2000);
        } else {
          const errorData = await response.text();
          this.errorMessage = `Erreur lors de la création: ${errorData}`;
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur';
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.card {
  border: none;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.bg-light {
  background-color: #f8f9fa !important;
}

.form-label {
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.text-danger {
  color: #dc3545 !important;
}

.btn-outline-primary {
  border-color: #5d87ff;
  color: #5d87ff;
}

.btn-outline-primary:hover {
  background-color: #5d87ff;
  color: white;
}
</style>
