<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4 no-print">
            <h5 class="card-title fw-semibold mb-0">Détails Bon de Commande Fournisseur</h5>
            <div class="d-flex gap-2">
              <button class="btn btn-outline-info" @click="exportToPDF">
                <i class="ti ti-file-export me-1"></i> PDF
              </button>
              <router-link to="/commandes-achat" class="btn btn-secondary">
                <i class="ti ti-arrow-left"></i> Retour
              </router-link>
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

          <div v-else-if="bc">
            <div class="row mb-4">
              <div class="col-md-6">
                <div class="mb-3">
                  <label class="form-label fw-bold">Référence</label>
                  <p class="form-control-plaintext">{{ bc.reference }}</p>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-bold">Fournisseur</label>
                  <p class="form-control-plaintext">
                    <span v-if="bc.fournisseur">{{ bc.fournisseur.nom }} ({{ bc.fournisseur.code }})</span>
                    <span v-else class="text-muted italic">Non défini</span>
                  </p>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-bold">Acheteur</label>
                  <p class="form-control-plaintext">{{ bc.utilisateur?.prenom }} {{ bc.utilisateur?.nom }}</p>
                </div>
              </div>
              <div class="col-md-6">
                <div class="mb-3">
                  <label class="form-label fw-bold">Date de commande</label>
                  <p class="form-control-plaintext">{{ formatDate(bc.dateCommande) }}</p>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-bold">Statut</label>
                  <p class="form-control-plaintext">
                    <span :class="getStatutClass(bc.statut)">{{ formatStatut(bc.statut) }}</span>
                  </p>
                </div>
                <div v-if="bc.demandeAchat" class="mb-3">
                  <label class="form-label fw-bold">DA Source</label>
                  <p class="form-control-plaintext">
                    <router-link :to="`/achats/${bc.demandeAchat.id}`">
                      {{ bc.demandeAchat.reference }}
                    </router-link>
                  </p>
                </div>
              </div>
            </div>

            <div class="card bg-light mb-4">
              <div class="card-body">
                <h6 class="card-title mb-3">Articles commandés</h6>
                <div class="table-responsive">
                  <table class="table table-sm">
                    <thead>
                      <tr>
                        <th>Article</th>
                        <th class="text-center">Quantité</th>
                        <th class="text-end">Prix Unitaire</th>
                        <th class="text-end">Remise (%)</th>
                        <th class="text-end">Total</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="ligne in lignes" :key="ligne.id">
                        <td>{{ ligne.article?.code }} - {{ ligne.article?.nom }}</td>
                        <td class="text-center">{{ ligne.quantite }}</td>
                        <td class="text-end">{{ formatCurrency(ligne.prixUnitaire) }}</td>
                        <td class="text-end">{{ ligne.remise }}%</td>
                        <td class="text-end">{{ formatCurrency(calculerLigneTotal(ligne)) }}</td>
                      </tr>
                    </tbody>
                    <tfoot>
                      <tr class="fw-bold">
                        <td colspan="4" class="text-end">Montant Total:</td>
                        <td class="text-end text-primary">{{ formatCurrency(bc.montantTotal) }}</td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
              </div>
            </div>

            <div class="d-flex justify-content-end gap-2 no-print">
              <!-- Acheteur : Modifier/Négocier -->
              <button 
                v-if="bc.statut === 'brouillon' && (hasRole('Acheteur') || hasRole('Administrateur'))"
                class="btn btn-warning" 
                @click="modifier"
              >
                <i class="ti ti-edit"></i> Modifier / Négocier
              </button>

              <!-- Acheteur : Soumettre -->
              <button 
                v-if="bc.statut === 'brouillon' && (hasRole('Acheteur') || hasRole('Administrateur'))"
                class="btn btn-primary" 
                @click="valider('Soumettre ce BC pour validation ?')"
              >
                <i class="ti ti-send"></i> Soumettre pour validation
              </button>

              <!-- Responsable Achats : Valider -->
              <button 
                v-if="bc.statut === 'attente_validation' && (hasRole('Responsable Achats') || hasRole('Administrateur'))"
                class="btn btn-success" 
                @click="valider('Valider ce Bon de Commande ?')"
              >
                <i class="ti ti-check"></i> Valider le BC
              </button>

              <!-- Responsable Achats : Signaler Litige -->
              <button 
                v-if="(bc.statut === 'attente_validation' || bc.statut === 'attente_approbation_finale') && (hasRole('Responsable Achats') || hasRole('Administrateur'))"
                class="btn btn-danger" 
                @click="signalerLitige"
              >
                <i class="ti ti-alert-triangle"></i> Signaler un Litige
              </button>

              <!-- Responsable Achats : Lever Litige -->
              <button 
                v-if="bc.statut === 'litige' && (hasRole('Responsable Achats') || hasRole('Administrateur'))"
                class="btn btn-info" 
                @click="leverLitige"
              >
                <i class="ti ti-check"></i> Lever le Litige
              </button>

              <!-- DAF / DG : Approbation finale -->
              <button 
                v-if="bc.statut === 'attente_approbation_finale' && (hasRole('DAF') || hasRole('DG') || hasRole('Administrateur'))"
                class="btn btn-success" 
                @click="valider('Approuver ce Bon de Commande (Approbation finale) ?')"
              >
                <i class="ti ti-discount-check"></i> Approbation finale
              </button>

              <!-- Acheteur : Envoyer au Fournisseur -->
              <button 
                v-if="bc.statut === 'valide' && (hasRole('Acheteur') || hasRole('Administrateur'))"
                class="btn btn-primary" 
                @click="envoyerAuFournisseur"
              >
                <i class="ti ti-mail"></i> Envoyer au Fournisseur
              </button>
            </div>

            <!-- Traçabilité / Journal d'audit -->
            <div v-if="audits.length > 0" class="card mt-4 no-print">
              <div class="card-body">
                <h6 class="card-title mb-3">Traçabilité & Suivi</h6>
                <div class="table-responsive">
                  <table class="table table-sm">
                    <thead>
                      <tr>
                        <th>Date</th>
                        <th>Utilisateur</th>
                        <th>Action</th>
                        <th>Détails</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="audit in audits" :key="audit.id">
                        <td>{{ formatDate(audit.dateAction) }}</td>
                        <td>{{ audit.utilisateur?.prenom }} {{ audit.utilisateur?.nom }}</td>
                        <td><span class="badge bg-light text-dark">{{ audit.action }}</span></td>
                        <td>{{ audit.details }}</td>
                      </tr>
                    </tbody>
                  </table>
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
import axios from 'axios';
import MainLayout from '../../layouts/MainLayout.vue';

export default {
  name: 'DetailsBonCommandeFournisseur',
  components: {
    MainLayout
  },
  data() {
    return {
      bc: null,
      lignes: [],
      audits: [],
      isLoading: false,
      errorMessage: '',
      currentUser: null
    };
  },
  mounted() {
    this.loadBC();
    const authData = JSON.parse(localStorage.getItem('user'));
    if (authData) {
      this.currentUser = authData.user || authData;
    }
  },
  methods: {
    async loadBC() {
      this.isLoading = true;
      const id = this.$route.params.id;
      try {
        const response = await axios.get(`/api/bons-commande-fournisseur/${id}`);
        if (response.status === 200) {
          this.bc = response.data;
          this.loadLignes(id);
          this.loadAudits(this.bc.reference);
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Bon de commande non trouvé';
      } finally {
        this.isLoading = false;
      }
    },
    async loadLignes(bcId) {
      try {
        const response = await axios.get(`/api/bons-commande-fournisseur/${bcId}/lignes`);
        if (response.status === 200) {
          this.lignes = response.data;
        }
      } catch (error) {
        console.error('Erreur chargement lignes:', error);
      }
    },
    hasRole(roleNom) {
      if (!this.currentUser || !this.currentUser.roles) return false;
      return this.currentUser.roles.some(r => r.nom.toUpperCase() === roleNom.toUpperCase());
    },
    formatDate(dateString) {
      if (!dateString) return '';
      return new Date(dateString).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    formatCurrency(value) {
      if (value === null || value === undefined) return '0 MGA';
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'MGA',
        minimumFractionDigits: 0
      }).format(value);
    },
    calculerLigneTotal(ligne) {
      const base = ligne.prixUnitaire * ligne.quantite;
      const reduction = base * (ligne.remise / 100);
      return base - reduction;
    },
    formatStatut(statut) {
      if (!statut) return '';
      const s = statut.toLowerCase();
      switch (s) {
        case 'brouillon': return 'Brouillon';
        case 'attente_validation': return 'Attente Validation (Resp. Achats)';
        case 'attente_approbation_finale': return 'Attente Approbation (DAF/DG)';
        case 'valide': return 'Validé';
        case 'litige': return 'En Litige';
        case 'envoye': return 'Envoyé au fournisseur';
        case 'recu': return 'Reçu / Livré';
        case 'annule': return 'Annulé';
        default: return statut;
      }
    },
    getStatutClass(statut) {
      const s = statut?.toLowerCase();
      switch (s) {
        case 'brouillon': return 'badge bg-secondary';
        case 'attente_validation': return 'badge bg-warning text-dark';
        case 'attente_approbation_finale': return 'badge bg-warning text-dark';
        case 'valide': return 'badge bg-success';
        case 'litige': return 'badge bg-danger';
        case 'envoye': return 'badge bg-primary';
        case 'recu': return 'badge bg-info';
        case 'annule': return 'badge bg-danger';
        default: return 'badge bg-info';
      }
    },
    modifier() {
      this.$router.push(`/commandes-achat/${this.bc.id}/edit`);
    },
    async valider(message) {
      if (!confirm(message || 'Voulez-vous valider cette action ?')) return;
      try {
        const response = await axios.post(`/api/bons-commande-fournisseur/${this.bc.id}/valider`, {
          validateurId: this.currentUser.id
        });
        if (response.status === 200) {
          alert('Action effectuée avec succès');
          this.loadBC();
        }
      } catch (error) {
        console.error('Erreur lors de la validation:', error);
        const errorMsg = error.response?.data?.message || error.response?.data || error.message;
        alert('Erreur : ' + (typeof errorMsg === 'object' ? JSON.stringify(errorMsg) : errorMsg));
      }
    },
    async signalerLitige() {
      if (!confirm('Voulez-vous signaler un litige sur ce Bon de Commande ?')) return;
      try {
        const response = await axios.post(`/api/bons-commande-fournisseur/${this.bc.id}/litige`, {
          utilisateurId: this.currentUser.id
        });
        if (response.status === 200) {
          alert('Litige signalé');
          this.loadBC();
        }
      } catch (error) {
        console.error('Erreur litige:', error);
        const errorMsg = error.response?.data?.message || error.response?.data || error.message;
        alert('Erreur : ' + (typeof errorMsg === 'object' ? JSON.stringify(errorMsg) : errorMsg));
      }
    },
    async leverLitige() {
      if (!confirm('Voulez-vous lever le litige sur ce Bon de Commande ?')) return;
      try {
        const response = await axios.post(`/api/bons-commande-fournisseur/${this.bc.id}/lever-litige`, {
          utilisateurId: this.currentUser.id
        });
        if (response.status === 200) {
          alert('Litige levé. Le BC retourne en attente de validation.');
          this.loadBC();
        }
      } catch (error) {
        console.error('Erreur levée litige:', error);
        const errorMsg = error.response?.data?.message || error.response?.data || error.message;
        alert('Erreur : ' + (typeof errorMsg === 'object' ? JSON.stringify(errorMsg) : errorMsg));
      }
    },
    async loadAudits(reference) {
      try {
        const response = await axios.get(`/api/audits/module/ACHATS/ref/${reference}`);
        if (response.status === 200) {
          this.audits = response.data;
        }
      } catch (error) {
        console.error('Erreur chargement audits:', error);
      }
    },
    async envoyerAuFournisseur() {
      if (!confirm('Voulez-vous envoyer ce Bon de Commande au fournisseur ?')) return;
      try {
        const response = await axios.post(`/api/bons-commande-fournisseur/${this.bc.id}/envoyer`, {
          utilisateurId: this.currentUser.id
        });
        if (response.status === 200) {
          alert('Bon de commande envoyé au fournisseur avec succès');
          this.loadBC();
        }
      } catch (error) {
        console.error('Erreur envoi fournisseur:', error);
        const errorMsg = error.response?.data?.message || error.response?.data || error.message;
        alert('Erreur : ' + (typeof errorMsg === 'object' ? JSON.stringify(errorMsg) : errorMsg));
      }
    },
    modifier() {
      this.$router.push(`/commandes-achat/${this.bc.id}/edit`);
    },
    exportToPDF() {
      window.print();
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
