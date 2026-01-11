<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Détails Demande d'Achat</h5>
            <router-link to="/achats" class="btn btn-secondary">
              <i class="ti ti-arrow-left"></i> Retour
            </router-link>
          </div>

          <div v-if="errorMessage" class="alert alert-danger" role="alert">
            {{ errorMessage }}
          </div>

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else-if="demande">
            <!-- Informations générales -->
            <div class="row mb-4">
              <div class="col-md-6">
                <div class="mb-3">
                  <label class="form-label fw-bold">Référence</label>
                  <p class="form-control-plaintext">{{ demande.reference }}</p>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-bold">Demandeur</label>
                  <p class="form-control-plaintext">{{ demande.demandeurPrenom }} {{ demande.demandeurNom }}</p>
                </div>
              </div>
              <div class="col-md-6">
                <div class="mb-3">
                  <label class="form-label fw-bold">Date de création</label>
                  <p class="form-control-plaintext">{{ formatDate(demande.dateCreation) }}</p>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-bold">Statut</label>
                  <p class="form-control-plaintext">
                    <span :class="getStatutClass(demande.statut)">{{ demande.statut }}</span>
                  </p>
                </div>
              </div>
            </div>

            <!-- Articles -->
            <div class="card bg-light mb-4">
              <div class="card-body">
                <h6 class="card-title mb-3">Articles demandés</h6>
                <div class="table-responsive">
                  <table class="table table-sm">
                    <thead>
                      <tr>
                        <th>Article</th>
                        <th class="text-center">Quantité</th>
                        <th class="text-end">Prix Estimé</th>
                        <th class="text-end">Total</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(ligne, index) in demande.lignes" :key="index">
                        <td>{{ getArticleNom(ligne.articleId) }}</td>
                        <td class="text-center">{{ ligne.quantite }}</td>
                        <td class="text-end">{{ formatCurrency(ligne.prixEstime) }}</td>
                        <td class="text-end">{{ formatCurrency(ligne.prixEstime * ligne.quantite) }}</td>
                      </tr>
                    </tbody>
                    <tfoot>
                      <tr class="fw-bold">
                        <td colspan="3" class="text-end">Total Estimé:</td>
                        <td class="text-end text-primary">{{ formatCurrency(totalEstime) }}</td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
              </div>
            </div>

            <!-- Historique de validation -->
            <div v-if="demande.historiqueValidations" class="card bg-light mb-4">
              <div class="card-body">
                <h6 class="card-title mb-3">Historique de validation</h6>
                <pre class="mb-0" style="white-space: pre-wrap;">{{ demande.historiqueValidations }}</pre>
              </div>
            </div>

            <!-- Boutons d'action -->
            <div class="d-flex justify-content-end gap-2">
              <router-link 
                v-if="demande.statut === 'brouillon'"
                :to="`/achats/${demande.id}/edit`" 
                class="btn btn-warning"
              >
                <i class="ti ti-edit"></i> Modifier
              </router-link>
              <button 
                v-if="demande.statut === 'brouillon'"
                class="btn btn-success" 
                @click="soumettre"
              >
                <i class="ti ti-send"></i> Soumettre à validation
              </button>
              <button 
                v-if="demande.statut === 'en attente'"
                class="btn btn-success me-2" 
                @click="approuver"
              >
                <i class="ti ti-check"></i> Approuver
              </button>
              <button 
                v-if="demande.statut === 'en attente'"
                class="btn btn-danger me-2" 
                data-bs-toggle="modal" 
                data-bs-target="#rejetModal"
              >
                <i class="ti ti-x"></i> Rejeter
              </button>
              <button 
                v-if="demande.statut !== 'approuvé' && demande.statut !== 'approuve' && demande.statut !== 'en attente'"
                class="btn btn-outline-danger" 
                @click="annuler"
              >
                <i class="ti ti-ban"></i> Annuler
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal de rejet -->
      <div class="modal fade" id="rejetModal" tabindex="-1" aria-labelledby="rejetModalLabel" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="rejetModalLabel">Rejeter la demande d'achat</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label for="motifRejet" class="form-label">Motif du rejet <span class="text-danger">*</span></label>
                <textarea 
                  id="motifRejet" 
                  class="form-control" 
                  v-model="motifRejet" 
                  rows="4" 
                  placeholder="Veuillez préciser la raison du rejet..."
                  required
                ></textarea>
                <div class="form-text">Ce motif sera visible dans l'historique de validation</div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
              <button type="button" class="btn btn-danger" @click="confirmerRejet">
                <i class="ti ti-x"></i> Confirmer le rejet
              </button>
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
  name: 'DetailsDemandeAchat',
  components: {
    MainLayout
  },
  data() {
    return {
      demande: null,
      articles: [],
      isLoading: false,
      errorMessage: '',
      motifRejet: ''
    };
  },
  computed: {
    totalEstime() {
      if (!this.demande || !this.demande.lignes) return 0;
      return this.demande.lignes.reduce((total, ligne) => {
        return total + (ligne.prixEstime * ligne.quantite);
      }, 0);
    }
  },
  mounted() {
    this.loadArticles();
    this.loadDemande();
  },
  methods: {
    async loadDemande() {
      this.isLoading = true;
      const id = this.$route.params.id;

      try {
        const response = await fetch(`http://localhost:8080/api/demandes-achat/${id}`);
        if (response.ok) {
          this.demande = await response.json();
        } else {
          this.errorMessage = 'Demande d\'achat non trouvée';
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur';
      } finally {
        this.isLoading = false;
      }
    },
    async loadArticles() {
      try {
        const response = await fetch('http://localhost:8080/api/articles');
        if (response.ok) {
          this.articles = await response.json();
        }
      } catch (error) {
        console.error('Erreur:', error);
      }
    },
    getArticleNom(articleId) {
      const article = this.articles.find(a => a.id === articleId);
      return article ? `${article.code} - ${article.nom}` : 'Article inconnu';
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('fr-FR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date);
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'MGA',
        minimumFractionDigits: 0
      }).format(value);
    },
    getStatutClass(statut) {
      const statutLower = statut?.toLowerCase();
      switch (statutLower) {
        case 'brouillon':
          return 'badge bg-secondary';
        case 'en attente':
          return 'badge bg-warning';
        case 'approuvé':
        case 'approuve':
          return 'badge bg-success';
        case 'rejeté':
        case 'rejete':
          return 'badge bg-danger';
        case 'annulé':
        case 'annule':
          return 'badge bg-dark';
        default:
          return 'badge bg-info';
      }
    },
    async soumettre() {
      if (!confirm('Voulez-vous soumettre cette demande à validation ?')) {
        return;
      }

      try {
        const response = await fetch(`http://localhost:8080/api/demandes-achat/${this.demande.id}/soumettre`, {
          method: 'POST'
        });

        if (response.ok) {
          alert('Demande soumise avec succès');
          this.loadDemande();
        } else {
          alert('Erreur lors de la soumission');
        }
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
      }
    },
    async approuver() {
      if (!confirm('Voulez-vous approuver cette demande d\'achat ?')) {
        return;
      }

      try {
        const user = JSON.parse(localStorage.getItem('user'));
        const response = await fetch(`http://localhost:8080/api/demandes-achat/${this.demande.id}/approuver`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            validateurId: user.id
          })
        });

        if (response.ok) {
          alert('Demande approuvée avec succès');
          this.loadDemande();
        } else {
          alert('Erreur lors de l\'approbation');
        }
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
      }
    },
    async confirmerRejet() {
      if (!this.motifRejet || this.motifRejet.trim() === '') {
        alert('Veuillez saisir un motif de rejet');
        return;
      }

      try {
        const user = JSON.parse(localStorage.getItem('user'));
        const response = await fetch(`http://localhost:8080/api/demandes-achat/${this.demande.id}/rejeter`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            validateurId: user.id,
            motif: this.motifRejet
          })
        });

        if (response.ok) {
          // Fermer le modal avec Bootstrap
          const modalElement = document.getElementById('rejetModal');
          const modal = window.bootstrap?.Modal?.getInstance(modalElement);
          if (modal) {
            modal.hide();
          } else {
            // Fallback: fermer manuellement
            modalElement.classList.remove('show');
            modalElement.style.display = 'none';
            document.body.classList.remove('modal-open');
            const backdrop = document.querySelector('.modal-backdrop');
            if (backdrop) backdrop.remove();
          }
          
          alert('Demande rejetée avec succès');
          this.loadDemande();
        } else {
          alert('Erreur lors du rejet');
        }
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
      }
    },
    async annuler() {
      if (!confirm('Voulez-vous vraiment annuler cette demande ?')) {
        return;
      }

      try {
        const response = await fetch(`http://localhost:8080/api/demandes-achat/${this.demande.id}/annuler`, {
          method: 'POST'
        });

        if (response.ok) {
          alert('Demande annulée avec succès');
          this.loadDemande();
        } else {
          alert('Erreur lors de l\'annulation');
        }
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
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

.form-control-plaintext {
  padding-left: 0;
}

.badge {
  padding: 0.5rem 0.75rem;
  font-size: 0.75rem;
  font-weight: 500;
  text-transform: uppercase;
}
</style>
