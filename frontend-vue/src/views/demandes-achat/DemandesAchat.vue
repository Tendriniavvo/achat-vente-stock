<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Demandes d'Achat</h5>
            <router-link to="/achats/create" class="btn btn-primary">
              <i class="ti ti-plus"></i> Nouvelle Demande
            </router-link>
          </div>

          <!-- Filtre par statut -->
          <div class="row mb-3">
            <div class="col-md-3">
              <label for="filtreStatut" class="form-label">Filtrer par statut :</label>
              <select 
                id="filtreStatut" 
                class="form-select" 
                v-model="filtreStatut"
              >
                <option value="tous">Tous</option>
                <option value="brouillon">Brouillon</option>
                <option value="en attente">En attente</option>
                <option value="approuvé">Approuvé</option>
                <option value="rejeté">Rejeté</option>
                <option value="annulé">Annulé</option>
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

          <div v-else-if="demandesFiltrees.length === 0" class="text-center py-5">
            <i class="ti ti-file-x" style="font-size: 3rem; color: #ccc;"></i>
            <p class="text-muted mt-3">
              {{ filtreStatut === 'tous' ? 'Aucune demande d\'achat' : `Aucune demande d'achat ${filtreStatut}` }}
            </p>
            <router-link to="/achats/create" class="btn btn-primary btn-sm">
              <i class="ti ti-plus"></i> Créer la première demande
            </router-link>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Référence</th>
                  <th>Demandeur</th>
                  <th>Date</th>
                  <th>Statut</th>
                  <th>Articles</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="demande in demandesFiltrees" :key="demande.id">
                  <td>
                    <strong>{{ demande.reference }}</strong>
                  </td>
                  <td>{{ demande.demandeurPrenom }} {{ demande.demandeurNom }}</td>
                  <td>{{ formatDate(demande.dateCreation) }}</td>
                  <td>
                    <span :class="getStatutClass(demande.statut)">
                      {{ demande.statut }}
                    </span>
                  </td>
                  <td>{{ demande.lignes ? demande.lignes.length : 0 }} article(s)</td>
                  <td>
                    <button 
                      class="btn btn-sm btn-outline-primary me-1" 
                      title="Voir détails"
                      @click="voirDetails(demande.id)"
                    >
                      <i class="ti ti-eye"></i>
                    </button>
                    <button 
                      v-if="demande.statut === 'brouillon'"
                      class="btn btn-sm btn-outline-warning me-1" 
                      title="Modifier"
                      @click="modifier(demande.id)"
                    >
                      <i class="ti ti-edit"></i>
                    </button>
                    <button 
                      v-if="demande.statut === 'brouillon'"
                      class="btn btn-sm btn-outline-success me-1" 
                      title="Soumettre à validation"
                      @click="soumettre(demande.id)"
                    >
                      <i class="ti ti-send"></i>
                    </button>
                    <button 
                      v-if="demande.statut === 'en attente'"
                      class="btn btn-sm btn-success me-1" 
                      title="Approuver"
                      @click="approuver(demande.id)"
                    >
                      <i class="ti ti-check"></i>
                    </button>
                    <button 
                      v-if="demande.statut === 'en attente'"
                      class="btn btn-sm btn-danger me-1" 
                      title="Rejeter"
                      data-bs-toggle="modal" 
                      data-bs-target="#rejetModal"
                      @click="prepareRejet(demande)"
                    >
                      <i class="ti ti-x"></i>
                    </button>
                    <button 
                      v-if="demande.statut !== 'approuvé' && demande.statut !== 'approuve' && demande.statut !== 'en attente'"
                      class="btn btn-sm btn-outline-danger" 
                      title="Annuler"
                      @click="annuler(demande.id)"
                    >
                      <i class="ti ti-ban"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
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
              <p><strong>Référence:</strong> {{ demandeARejeter?.reference }}</p>
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
  name: 'DemandesAchat',
  components: {
    MainLayout
  },
  data() {
    return {
      demandes: [],
      isLoading: false,
      errorMessage: '',
      filtreStatut: 'tous',
      motifRejet: '',
      demandeARejeter: null
    };
  },
  computed: {
    demandesFiltrees() {
      if (this.filtreStatut === 'tous') {
        return this.demandes;
      }
      return this.demandes.filter(demande => {
        const statutLower = demande.statut?.toLowerCase();
        const filtreLower = this.filtreStatut.toLowerCase();
        
        // Gérer les variations d'orthographe
        if (filtreLower === 'approuvé') {
          return statutLower === 'approuvé' || statutLower === 'approuve';
        }
        if (filtreLower === 'rejeté') {
          return statutLower === 'rejeté' || statutLower === 'rejete';
        }
        if (filtreLower === 'annulé') {
          return statutLower === 'annulé' || statutLower === 'annule';
        }
        
        return statutLower === filtreLower;
      });
    }
  },
  mounted() {
    this.loadDemandes();
  },
  methods: {
    async loadDemandes() {
      this.isLoading = true;
      this.errorMessage = '';
      
      try {
        const response = await fetch('http://localhost:8080/api/demandes-achat');
        if (response.ok) {
          this.demandes = await response.json();
        } else {
          this.errorMessage = 'Erreur lors du chargement des demandes';
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur';
      } finally {
        this.isLoading = false;
      }
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
        default:
          return 'badge bg-info';
      }
    },
    voirDetails(id) {
      this.$router.push(`/achats/${id}`);
    },
    modifier(id) {
      this.$router.push(`/achats/${id}/edit`);
    },
    async soumettre(id) {
      if (!confirm('Voulez-vous soumettre cette demande à validation ?')) {
        return;
      }

      try {
        const response = await fetch(`http://localhost:8080/api/demandes-achat/${id}/soumettre`, {
          method: 'POST'
        });

        if (response.ok) {
          alert('Demande soumise avec succès');
          this.loadDemandes();
        } else {
          alert('Erreur lors de la soumission');
        }
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
      }
    },
    async approuver(id) {
      if (!confirm('Voulez-vous approuver cette demande d\'achat ?')) {
        return;
      }

      try {
        const user = JSON.parse(localStorage.getItem('user'));
        const response = await fetch(`http://localhost:8080/api/demandes-achat/${id}/approuver`, {
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
          this.loadDemandes();
        } else {
          alert('Erreur lors de l\'approbation');
        }
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
      }
    },
    prepareRejet(demande) {
      // Préparer les données pour le rejet
      this.demandeARejeter = demande;
      this.motifRejet = '';
    },
    async confirmerRejet() {
      if (!this.motifRejet || this.motifRejet.trim() === '') {
        alert('Veuillez saisir un motif de rejet');
        return;
      }

      try {
        const user = JSON.parse(localStorage.getItem('user'));
        const response = await fetch(`http://localhost:8080/api/demandes-achat/${this.demandeARejeter.id}/rejeter`, {
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
          this.loadDemandes();
        } else {
          alert('Erreur lors du rejet');
        }
      } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur de connexion au serveur');
      }
    },
    async annuler(id) {
      if (!confirm('Voulez-vous vraiment annuler cette demande ?')) {
        return;
      }

      try {
        const response = await fetch(`http://localhost:8080/api/demandes-achat/${id}/annuler`, {
          method: 'POST'
        });

        if (response.ok) {
          alert('Demande annulée avec succès');
          this.loadDemandes();
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

.table-responsive {
  margin-top: 1rem;
}

.badge {
  padding: 0.5rem 0.75rem;
  font-size: 0.75rem;
  font-weight: 500;
  text-transform: uppercase;
}
</style>
