<template>
  <MainLayout>
    <div class="container-fluid">
      <!-- Header & Actions -->
      <div class="card mb-4 no-print">
        <div class="card-body py-3">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h5 class="card-title fw-semibold mb-0">Détails Devis Client : {{ devis.reference }}</h5>
            </div>
            <div class="d-flex gap-2">
              <router-link to="/devis" class="btn btn-outline-secondary">
                <i class="ti ti-arrow-left me-1"></i> Retour à la liste
              </router-link>
              
              <!-- Action Buttons in Header -->
              <div v-if="isCommercial" class="d-flex gap-2">
                <button v-if="devis.statut === 'brouillon' && !needsValidation || devis.statut === 'valide'" 
                        class="btn btn-primary" @click="envoyerDevis" :disabled="isProcessing">
                  <i class="ti ti-send me-1"></i> Envoyer au Client
                </button>
                <button v-if="devis.statut === 'brouillon' && needsValidation && isResponsableVentes" 
                        class="btn btn-success" @click="validerDevis">
                  <i class="ti ti-shield-check me-1"></i> Valider le Devis
                </button>
                <button v-if="devis.statut === 'envoye'" 
                        class="btn btn-success" @click="accepterDevis">
                  <i class="ti ti-check me-1"></i> Client a Accepté
                </button>
                <button v-if="devis.statut === 'envoye'" 
                        class="btn btn-danger" @click="rejeterDevis">
                  <i class="ti ti-x me-1"></i> Client a Rejeté
                </button>
                <button class="btn btn-outline-info" @click="exportToPDF">
                  <i class="ti ti-file-export me-1"></i> Exporter PDF
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row" id="devis-content">
        <!-- Main Content -->
        <div class="col-lg-8">
          <div class="card">
            <div class="card-body">
              <div class="d-flex justify-content-between mb-4">
                <div>
                  <h6 class="fw-bold mb-1">Émetteur :</h6>
                  <p class="mb-0">Ma Société de Gestion</p>
                  <p class="mb-0 text-muted">Commercial : {{ devis.utilisateur?.prenom }} {{ devis.utilisateur?.nom }}</p>
                </div>
                <div class="text-end">
                  <h6 class="fw-bold mb-1">Destinataire :</h6>
                  <h5 class="text-primary mb-0">{{ devis.client?.nom }}</h5>
                  <p class="mb-0 text-muted">{{ devis.client?.code }}</p>
                </div>
              </div>

              <div class="table-responsive">
                <table class="table table-bordered">
                  <thead class="table-light">
                    <tr>
                      <th>Article</th>
                      <th class="text-center">Quantité</th>
                      <th class="text-end">P.U. (MGA)</th>
                      <th class="text-end">Remise (%)</th>
                      <th class="text-end">Total HT</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="ligne in lignes" :key="ligne.id">
                      <td>
                        <strong>{{ ligne.article?.nom }}</strong><br>
                        <small class="text-muted">{{ ligne.article?.code }}</small>
                      </td>
                      <td class="text-center">{{ ligne.quantite }}</td>
                      <td class="text-end">{{ formatCurrency(ligne.prixUnitaire) }}</td>
                      <td class="text-end">{{ ligne.remise }}%</td>
                      <td class="text-end fw-bold">{{ formatCurrency(calculerLigneTotal(ligne)) }}</td>
                    </tr>
                  </tbody>
                  <tfoot>
                    <tr class="fs-5">
                      <td colspan="4" class="text-end fw-bold">TOTAL GÉNÉRAL :</td>
                      <td class="text-end fw-bold text-primary">{{ formatCurrency(devis.montantTotal) }}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>

              <div class="mt-4 p-3 bg-light rounded">
                <h6 class="fw-bold"><i class="ti ti-info-circle me-1"></i> Conditions de validité</h6>
                <ul class="mb-0 small text-muted">
                  <li>Ce devis est valable jusqu'au {{ formatDate(devis.dateValidite) }}.</li>
                  <li>Les prix sont indiqués Hors Taxes.</li>
                  <li>La réservation du stock n'est effective qu'à la confirmation de la commande.</li>
                </ul>
              </div>

              <div v-if="devis.notes" class="mt-4 p-3 border border-warning rounded">
                <h6 class="fw-bold text-warning"><i class="ti ti-note me-1"></i> Notes & Conditions Particulières</h6>
                <p class="mb-0 small text-muted">{{ devis.notes }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar Info -->
        <div class="col-lg-4 no-print">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title fw-semibold mb-3">Informations</h5>
              
              <div class="mb-3">
                <label class="text-muted small d-block">Statut actuel</label>
                <span :class="getStatutClass(devis.statut)">{{ devis.statut }}</span>
              </div>

              <div class="mb-3">
                <label class="text-muted small d-block">Date de création</label>
                <p class="mb-0 fw-bold">{{ formatDate(devis.dateDevis) }}</p>
              </div>

              <div class="mb-3">
                <label class="text-muted small d-block">Date d'expiration</label>
                <p class="mb-0 fw-bold" :class="{'text-danger': isExpired}">
                  {{ formatDate(devis.dateValidite) }}
                </p>
              </div>

              <hr>

              <div v-if="needsValidation" class="alert alert-warning py-2 small">
                <i class="ti ti-alert-triangle me-1"></i>
                Ce devis contient des remises élevées ou des conditions particulières nécessitant une validation.
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal pour la date de livraison prévue -->
    <div v-if="showDateModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.5)">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Confirmation de Commande</h5>
            <button type="button" class="btn-close" @click="showDateModal = false"></button>
          </div>
          <div class="modal-body">
            <p>Le client a accepté le devis. Veuillez confirmer la <strong>date de livraison prévue</strong> pour réserver le stock.</p>
            <div class="mb-3">
              <label class="form-label">Date de livraison prévue</label>
              <input type="date" class="form-control" v-model="dateLivraisonPrevue" :min="today">
            </div>
            <div class="alert alert-info small">
              <i class="ti ti-info-circle me-1"></i>
              La validation de cette étape réservera automatiquement le stock nécessaire (FIFO/FEFO).
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-outline-secondary" @click="showDateModal = false">Annuler</button>
            <button 
              type="button" 
              class="btn btn-primary" 
              @click="confirmerTransformation" 
              :disabled="!dateLivraisonPrevue || isProcessing"
            >
              <span v-if="isProcessing" class="spinner-border spinner-border-sm me-1"></span>
              Confirmer & Réserver le Stock
            </button>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '@/layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'DetailsDevis',
  components: { MainLayout },
  data() {
    return {
      devis: {},
      lignes: [],
      isLoading: false,
      isProcessing: false,
      errorMessage: '',
      showDateModal: false,
      dateLivraisonPrevue: ''
    };
  },
  computed: {
    today() {
      return new Date().toISOString().split('T')[0];
    },
    currentUser() {
      const authData = JSON.parse(localStorage.getItem('user') || '{}');
      return authData.user || null;
    },
    isResponsableVentes() {
      return this.currentUser?.roles?.some(r => 
        r.nom === 'Responsable ventes' || r.nom === 'Administrateur'
      );
    },
    isCommercial() {
      return this.currentUser?.roles?.some(r => 
        r.nom === 'Commercial' || r.nom === 'Responsable ventes' || r.nom === 'Administrateur'
      );
    },
    isExpired() {
      if (!this.devis.dateValidite) return false;
      return new Date(this.devis.dateValidite) < new Date();
    },
    needsValidation() {
      return this.devis.remiseExceptionnelle || (this.devis.notes && this.devis.notes.trim() !== '');
    }
  },
  mounted() {
    this.fetchDevis();
  },
  methods: {
    async fetchDevis() {
      const id = this.$route.params.id;
      this.isLoading = true;
      try {
        const [devisRes, lignesRes] = await Promise.all([
          axios.get(`/api/devis/${id}`),
          axios.get(`/api/devis/${id}/lignes`)
        ]);
        this.devis = devisRes.data;
        this.lignes = lignesRes.data;
      } catch (error) {
        this.errorMessage = 'Erreur lors du chargement des détails du devis';
        console.error(error);
      } finally {
        this.isLoading = false;
      }
    },
    formatDate(date) {
      if (!date) return '-';
      return new Date(date).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-MG', { style: 'currency', currency: 'MGA' }).format(value);
    },
    calculerLigneTotal(ligne) {
      const base = ligne.quantite * (ligne.prixUnitaire || 0);
      const remise = base * (ligne.remise || 0) / 100;
      return base - remise;
    },
    getStatutClass(statut) {
      if (!statut) return '';
      const s = statut.toLowerCase();
      if (s === 'brouillon') return 'badge bg-secondary';
      if (s === 'valide') return 'badge bg-success';
      if (s === 'envoye') return 'badge bg-info';
      if (s === 'accepte') return 'badge bg-success';
      if (s === 'rejete') return 'badge bg-danger';
      return 'badge bg-light text-dark';
    },
    async validerDevis() {
      if (confirm('Voulez-vous valider ce devis ? Cela permettra son envoi au client.')) {
        this.isProcessing = true;
        try {
          await axios.post(`/api/devis/${this.devis.id}/valider`, {
            utilisateurId: this.currentUser.id
          });
          this.fetchDevis();
        } catch (error) {
          alert('Erreur lors de la validation');
        } finally {
          this.isProcessing = false;
        }
      }
    },
    exportToPDF() {
      window.print();
    },
    async envoyerDevis() {
      if (confirm('Marquer ce devis comme envoyé au client ?')) {
        this.isProcessing = true;
        try {
          await axios.put(`/api/devis/${this.devis.id}/statut`, { 
            statut: 'envoye',
            utilisateurId: this.currentUser.id
          });
          this.fetchDevis();
        } catch (error) {
          const msg = error.response?.data || 'Erreur lors de l\'envoi';
          alert(typeof msg === 'string' ? msg : 'Erreur lors de l\'envoi. Ce devis nécessite peut-être une validation.');
        } finally {
          this.isProcessing = false;
        }
      }
    },
    accepterDevis() {
      this.showDateModal = true;
    },
    async confirmerTransformation() {
      this.isProcessing = true;
      try {
        await axios.post(`/api/devis/${this.devis.id}/transformer`, { 
          utilisateurId: this.currentUser.id,
          dateLivraisonPrevue: this.dateLivraisonPrevue
        });
        this.showDateModal = false;
        this.$router.push('/commandes-client');
      } catch (error) {
        const msg = error.response?.data || 'Erreur lors de la transformation';
        alert(typeof msg === 'string' ? msg : 'Erreur lors de la transformation. Vérifiez le stock disponible.');
      } finally {
        this.isProcessing = false;
      }
    },
    async rejeterDevis() {
      if (confirm('Le client a-t-il rejeté ce devis ?')) {
        this.isProcessing = true;
        try {
          await axios.put(`/api/devis/${this.devis.id}/statut`, { 
            statut: 'rejete',
            utilisateurId: this.currentUser.id
          });
          this.fetchDevis();
        } catch (error) {
          alert('Erreur lors du rejet');
        } finally {
          this.isProcessing = false;
        }
      }
    }
  }
};
</script>

<style scoped>
@media print {
  .no-print {
    display: none !important;
  }
  .card {
    border: none !important;
    box-shadow: none !important;
  }
  .container-fluid {
    padding: 0 !important;
  }
}
</style>
