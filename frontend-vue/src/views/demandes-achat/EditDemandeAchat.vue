<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="row">
        <div class="col-12">
          <div class="card shadow-sm border-0">
            <div class="card-body p-4">
              <div class="d-flex justify-content-between align-items-center mb-4">
                <h5 class="card-title fw-bold mb-0">
                  <i class="ti ti-edit me-2 text-warning"></i>Modifier la Demande d'Achat
                </h5>
                <router-link to="/achats" class="btn btn-outline-secondary btn-sm">
                  <i class="ti ti-arrow-left me-1"></i>Retour
                </router-link>
              </div>

              <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="ti ti-alert-circle me-2"></i>{{ errorMessage }}
                <button type="button" class="btn-close" @click="errorMessage = ''"></button>
              </div>

              <div v-if="isLoading" class="text-center py-5">
                <div class="spinner-border text-primary" role="status">
                  <span class="visually-hidden">Chargement...</span>
                </div>
              </div>

              <form v-else @submit.prevent="updateDemande">
                <!-- Informations Générales -->
                <div class="row mb-4">
                  <div class="col-md-4">
                    <label for="reference" class="form-label fw-semibold">Référence <span class="text-danger">*</span></label>
                    <input 
                      type="text" 
                      class="form-control" 
                      id="reference" 
                      v-model="form.reference" 
                      required
                    >
                  </div>
                  <div class="col-md-4">
                    <label for="demandeur" class="form-label fw-semibold">Demandeur <span class="text-danger">*</span></label>
                    <select 
                      class="form-select" 
                      id="demandeur" 
                      v-model="form.demandeurId" 
                      required
                    >
                      <option value="" disabled>Sélectionner un demandeur</option>
                      <option v-for="user in utilisateurs" :key="user.id" :value="user.id">
                        {{ user.nom }} {{ user.prenom }} ({{ user.email }})
                      </option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label fw-semibold">Statut Actuel</label>
                    <input type="text" class="form-control bg-light" :value="demandeStatut" disabled>
                  </div>
                </div>

                <!-- Section Articles -->
                <div class="card bg-light border-0 mb-4">
                  <div class="card-body p-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                      <h6 class="fw-bold mb-0">Articles et Besoins</h6>
                      <button type="button" class="btn btn-primary btn-sm" @click="addLigne">
                        <i class="ti ti-plus me-1"></i>Ajouter un article
                      </button>
                    </div>

                    <div class="table-responsive">
                      <table class="table table-borderless align-middle mb-0">
                        <thead>
                          <tr class="text-muted small text-uppercase">
                            <th style="width: 40%">Article <span class="text-danger">*</span></th>
                            <th style="width: 15%" class="text-center">Quantité <span class="text-danger">*</span></th>
                            <th style="width: 20%" class="text-end">Prix Estimé (U) <span class="text-danger">*</span></th>
                            <th style="width: 20%" class="text-end">Total</th>
                            <th style="width: 5%"></th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="(ligne, index) in form.lignes" :key="index" class="bg-white rounded-3 mb-2">
                            <td>
                              <select 
                                class="form-select border-0 shadow-none bg-light" 
                                v-model="ligne.articleId" 
                                @change="updatePrixEstime(index)"
                                required
                              >
                                <option value="" disabled>Sélectionner un article</option>
                                <option v-for="article in articles" :key="article.id" :value="article.id">
                                  {{ article.code }} - {{ article.nom }}
                                </option>
                              </select>
                            </td>
                            <td>
                              <input 
                                type="number" 
                                class="form-control border-0 shadow-none bg-light text-center" 
                                v-model.number="ligne.quantite" 
                                min="1" 
                                required
                              >
                            </td>
                            <td>
                              <input 
                                type="number" 
                                class="form-control border-0 shadow-none bg-light text-end" 
                                v-model.number="ligne.prixEstime" 
                                step="0.01" 
                                min="0" 
                                required
                              >
                            </td>
                            <td class="text-end fw-bold text-primary">
                              {{ formatCurrency(ligne.quantite * (ligne.prixEstime || 0)) }}
                            </td>
                            <td class="text-end">
                              <button type="button" class="btn btn-link text-danger p-0" @click="removeLigne(index)">
                                <i class="ti ti-trash fs-5"></i>
                              </button>
                            </td>
                          </tr>
                        </tbody>
                        <tfoot>
                          <tr class="border-top">
                            <td colspan="3" class="text-end fw-bold pt-3">TOTAL ESTIMÉ :</td>
                            <td class="text-end fw-bold text-primary fs-5 pt-3">
                              {{ formatCurrency(totalEstime) }}
                            </td>
                            <td></td>
                          </tr>
                        </tfoot>
                      </table>
                    </div>
                  </div>
                </div>

                <div class="d-flex justify-content-end gap-2">
                  <button type="button" class="btn btn-light px-4" @click="$router.push('/achats')">Annuler</button>
                  <button type="submit" class="btn btn-warning px-4" :disabled="isSaving">
                    <span v-if="isSaving" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="ti ti-device-floppy me-2"></i>Mettre à jour
                  </button>
                </div>
              </form>
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
  name: 'EditDemandeAchat',
  components: {
    MainLayout
  },
  data() {
    return {
      form: {
        reference: '',
        demandeurId: '',
        lignes: []
      },
      demandeStatut: '',
      articles: [],
      utilisateurs: [],
      isLoading: true,
      isSaving: false,
      errorMessage: ''
    };
  },
  computed: {
    totalEstime() {
      return this.form.lignes.reduce((total, ligne) => {
        return total + ((ligne.prixEstime || 0) * (ligne.quantite || 0));
      }, 0);
    }
  },
  mounted() {
    this.loadArticles();
    this.loadUtilisateurs();
    this.loadDemande();
  },
  methods: {
    async loadUtilisateurs() {
      try {
        const response = await fetch('/api/utilisateurs');
        if (response.ok) {
          this.utilisateurs = await response.json();
        }
      } catch (error) {
        console.error('Erreur utilisateurs:', error);
      }
    },
    async loadArticles() {
      try {
        const response = await fetch('/api/articles');
        if (response.ok) {
          this.articles = await response.json();
        }
      } catch (error) {
        console.error('Erreur articles:', error);
      }
    },
    async loadDemande() {
      const id = this.$route.params.id;
      try {
        const response = await fetch(`/api/demandes-achat/${id}`);
        if (response.ok) {
          const data = await response.json();
          if (data.statut !== 'brouillon') {
            this.errorMessage = 'Seules les demandes en brouillon peuvent être modifiées.';
            this.isLoading = false;
            return;
          }
          this.form.reference = data.reference;
          this.form.demandeurId = data.demandeur?.id;
          this.demandeStatut = data.statut;
          this.form.lignes = data.lignes.map(l => ({
            articleId: l.article?.id,
            quantite: l.quantite,
            prixEstime: l.prixEstime
          }));
        } else {
          this.errorMessage = 'Demande d\'achat non trouvée.';
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur';
      } finally {
        this.isLoading = false;
      }
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
      const articleId = this.form.lignes[index].articleId;
      const article = this.articles.find(a => a.id === articleId);
      if (article) {
        this.form.lignes[index].prixEstime = article.prixAchat || 0;
      }
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA' }).format(value);
    },
    async updateDemande() {
      const id = this.$route.params.id;
      this.isSaving = true;
      this.errorMessage = '';

      try {
        const response = await fetch(`/api/demandes-achat/${id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.form)
        });

        if (response.ok) {
          this.$router.push('/achats');
        } else {
          const errorText = await response.text();
          this.errorMessage = errorText || 'Erreur lors de la mise à jour';
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur';
      } finally {
        this.isSaving = false;
      }
    }
  }
};
</script>

<style scoped>
.card {
  border-radius: 15px;
}
.bg-light {
  background-color: #f8f9fa !important;
}
.form-control:focus, .form-select:focus {
  border-color: #ffc107;
  box-shadow: 0 0 0 0.25rem rgba(255, 193, 7, 0.1);
}
.table th {
  border: none;
}
.table td {
  padding: 0.75rem;
}
</style>
