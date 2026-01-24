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
                  <p class="form-control-plaintext">{{ demande.demandeur?.prenom }} {{ demande.demandeur?.nom }}</p>
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
                        <td>{{ ligne.article?.code }} - {{ ligne.article?.nom }}</td>
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
              <!-- Actions pour le Demandeur -->
              <router-link 
                v-if="isStatut('brouillon')"
                :to="`/achats/${demande.id}/edit`" 
                class="btn btn-warning"
              >
                <i class="ti ti-edit"></i> Modifier
              </router-link>
              <button 
                v-if="isStatut('brouillon')"
                class="btn btn-success" 
                @click="soumettre"
              >
                <i class="ti ti-send"></i> Soumettre à validation
              </button>

              <!-- Actions d'Approbation Multi-niveaux -->
              
              <!-- N1: Chef de département -->
              <button 
                v-if="isStatut('en attente') && (hasRole('Acheteur') || hasRole('Administrateur')) && !isDemandeur()"
                class="btn btn-success" 
                @click="approuver"
              >
                <i class="ti ti-check"></i> Approuver (N1 - Chef)
              </button>

              <!-- Vérification des fonds (Finance) -->
              <button 
                v-if="isStatut('attente_finance') && (hasRole('Comptable') || hasRole('Administrateur'))"
                class="btn btn-info" 
                @click="verifierFonds"
              >
                <i class="ti ti-coin"></i> Vérifier les fonds
              </button>

              <!-- N2: Finance (après vérification des fonds) -->
              <button 
                v-if="isStatut('fonds_confirmés') && (hasRole('Comptable') || hasRole('Administrateur')) && !isDemandeur()"
                class="btn btn-success" 
                @click="approuver"
              >
                <i class="ti ti-check"></i> Approuver (N2 - Finance)
              </button>

              <!-- N3: Admin / DG -->
              <button 
                v-if="isStatut('attente_admin') && hasRole('Administrateur') && !isDemandeur()"
                class="btn btn-success" 
                @click="approuver"
              >
                <i class="ti ti-check"></i> Approuver (N3 - Admin)
              </button>

              <button 
                v-if="(isStatut('en attente') || isStatut('attente_finance') || isStatut('attente_admin') || isStatut('fonds_confirmés')) && (hasRole('Administrateur') || hasRole('Acheteur') || hasRole('Comptable')) && !isDemandeur()"
                class="btn btn-danger" 
                data-bs-toggle="modal" 
                data-bs-target="#rejetModal"
              >
                <i class="ti ti-x"></i> Rejeter
              </button>

              <!-- Action d'annulation -->
              <button 
                v-if="!isStatut('approuvé') && !isStatut('approuve') && !isStatut('annulé') && !isStatut('annule') && !isStatut('rejeté') && !isStatut('rejete') && !isStatut('transformé')"
                class="btn btn-outline-danger" 
                @click="annuler"
              >
                <i class="ti ti-ban"></i> Annuler
              </button>

              <!-- ÉTAPE 4: Transformation en Bon de Commande -->
              <button 
                v-if="isStatut('approuvé') && (hasRole('Acheteur') || hasRole('Administrateur'))"
                class="btn btn-primary" 
                data-bs-toggle="modal" 
                data-bs-target="#bcModal"
                @click="loadFournisseurs"
              >
                <i class="ti ti-file-description"></i> Transformer en BC
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

      <!-- Modal de transformation en BC -->
      <div class="modal fade" id="bcModal" tabindex="-1" aria-labelledby="bcModalLabel" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="bcModalLabel">Transformer en Bon de Commande</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label for="fournisseurId" class="form-label">Sélectionner un fournisseur (Optionnel)</label>
                <select id="fournisseurId" class="form-select" v-model="selectedFournisseurId">
                  <option :value="null">-- Choisir un fournisseur --</option>
                  <option v-for="f in fournisseurs" :key="f.id" :value="f.id">
                    {{ f.nom }} ({{ f.code }})
                  </option>
                </select>
                <div class="form-text">Vous pourrez négocier et modifier le fournisseur plus tard sur le BC.</div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
              <button type="button" class="btn btn-primary" @click="confirmerTransformation">
                <i class="ti ti-check"></i> Confirmer la transformation
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
import { Modal } from 'bootstrap';

export default {
  name: 'DetailsDemandeAchat',
  components: {
    MainLayout
  },
  data() {
    return {
      demande: null,
      articles: [],
      fournisseurs: [],
      selectedFournisseurId: null,
      isLoading: false,
      errorMessage: '',
      motifRejet: '',
      currentUser: null
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
    const authData = JSON.parse(localStorage.getItem('user'));
    if (authData) {
      // Gérer les deux formats possibles (direct ou sous la clé user)
      this.currentUser = authData.user || authData;
    }
  },
  methods: {
    hasRole(roleNom) {
      if (!this.currentUser || !this.currentUser.roles) return false;
      return this.currentUser.roles.some(r => r.nom.toUpperCase() === roleNom.toUpperCase());
    },
    isDemandeur() {
      if (!this.demande || !this.currentUser) return false;
      return this.demande.demandeur?.id === this.currentUser.id;
    },
    isStatut(statutTarget) {
      if (!this.demande || !this.demande.statut) return false;
      const currentStatut = this.demande.statut.toLowerCase();
      const target = statutTarget.toLowerCase();
      
      // Mappages pour les nouveaux statuts
      if (target === 'attente_finance') {
        return currentStatut === 'attente_finance';
      }
      if (target === 'attente_admin') {
        return currentStatut === 'attente_admin';
      }

      // Gérer les variantes avec/sans accents pour fonds_confirmés
      if (target === 'fonds_confirmés' || target === 'fonds_confirmes') {
        return currentStatut === 'fonds_confirmés' || currentStatut === 'fonds_confirmes';
      }
      // Gérer les variantes pour approuvé
      if (target === 'approuvé' || target === 'approuve') {
        return currentStatut === 'approuvé' || currentStatut === 'approuve';
      }
      // Gérer les variantes pour rejeté
      if (target === 'rejeté' || target === 'rejete') {
        return currentStatut === 'rejeté' || currentStatut === 'rejete';
      }
      // Gérer les variantes pour annulé
      if (target === 'annulé' || target === 'annule') {
        return currentStatut === 'annulé' || currentStatut === 'annule';
      }
      
      return currentStatut === target;
    },
    async loadDemande() {
      this.isLoading = true;
      const id = this.$route.params.id;

      try {
        const response = await fetch(`/api/demandes-achat/${id}`);
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
        const response = await fetch('/api/articles');
        if (response.ok) {
          this.articles = await response.json();
        }
      } catch (error) {
        console.error('Erreur chargement articles:', error);
      }
    },
    async loadFournisseurs() {
      try {
        const response = await fetch('/api/fournisseurs');
        if (response.ok) {
          this.fournisseurs = await response.json();
        }
      } catch (error) {
        console.error('Erreur chargement fournisseurs:', error);
      }
    },
    async confirmerTransformation() {
      if (!this.currentUser) {
        alert('Utilisateur non connecté');
        return;
      }

      try {
        const response = await fetch('/api/bons-commande-fournisseur/transformer', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            demandeAchatId: this.demande.id,
            acheteurId: this.currentUser.id,
            fournisseurId: this.selectedFournisseurId
          })
        });

        if (response.ok) {
          const bc = await response.json();
          alert(`Demande transformée avec succès en Bon de Commande ${bc.reference}`);
          
          // Fermer le modal
          const modalElement = document.getElementById('bcModal');
          const modalInstance = Modal.getInstance(modalElement);
          if (modalInstance) modalInstance.hide();
          
          // Recharger la demande pour voir le nouveau statut
          this.loadDemande();
          
          // Rediriger vers la liste des Bons de Commande
          setTimeout(() => {
            this.$router.push('/commandes-achat');
          }, 2000);
        } else {
          const error = await response.text();
          alert('Erreur lors de la transformation: ' + error);
        }
      } catch (error) {
        console.error('Erreur transformation BC:', error);
        alert('Erreur de connexion lors de la transformation');
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
        case 'attente_finance':
          return 'badge bg-primary';
        case 'attente_admin':
          return 'badge bg-indigo';
        case 'fonds_confirmés':
        case 'fonds_confirmes':
          return 'badge bg-info';
        case 'approuvé':
        case 'approuve':
          return 'badge bg-success';
        case 'rejeté':
        case 'rejete':
          return 'badge bg-danger';
        case 'annulé':
        case 'annule':
          return 'badge bg-dark';
        case 'transformé':
        case 'transforme':
          return 'badge bg-primary';
        default:
          return 'badge bg-info';
      }
    },
    async soumettre() {
      if (!confirm('Voulez-vous soumettre cette demande à validation ?')) {
        return;
      }

      try {
        const response = await fetch(`/api/demandes-achat/${this.demande.id}/soumettre`, {
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
    async verifierFonds() {
      if (!confirm('Voulez-vous vérifier la disponibilité des fonds for cette demande ?')) {
        return;
      }

      try {
        const response = await fetch(`/api/demandes-achat/${this.demande.id}/verifier-fonds`, {
          method: 'POST'
        });

        if (response.ok) {
          alert('Disponibilité des fonds confirmée');
          this.loadDemande();
        } else {
          const errorMsg = await response.text();
          alert('Erreur: ' + (errorMsg || 'Fonds insuffisants ou erreur de vérification'));
          this.loadDemande();
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
        const userStr = localStorage.getItem('user');
        if (!userStr) {
          alert('Utilisateur non connecté');
          return;
        }
        const authData = JSON.parse(userStr);
        const user = authData.user || authData;
        const response = await fetch(`/api/demandes-achat/${this.demande.id}/approuver`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            validateurId: user.id
          })
        });

        if (response.ok) {
          const updatedDemande = await response.json();
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
          this.loadDemande();
        } else {
          const errorMsg = await response.text();
          alert('Erreur lors de l\'approbation : ' + errorMsg);
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
        const response = await fetch(`/api/demandes-achat/${this.demande.id}/rejeter`, {
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
        const response = await fetch(`/api/demandes-achat/${this.demande.id}/annuler`, {
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
