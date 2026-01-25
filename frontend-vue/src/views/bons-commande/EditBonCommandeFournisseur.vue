<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Modifier / Négocier le Bon de Commande</h5>
            <router-link :to="`/commandes-achat/${bcId}`" class="btn btn-secondary">
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

          <form v-else-if="bc" @submit.prevent="saveBC">
            <div class="row mb-4">
              <div class="col-md-6">
                <div class="mb-3">
                  <label class="form-label fw-bold">Référence</label>
                  <p class="form-control-plaintext">{{ bc.reference }}</p>
                </div>
                <div class="mb-3">
                  <label for="fournisseur" class="form-label fw-bold">Fournisseur</label>
                  <select id="fournisseur" class="form-select" v-model="bc.fournisseurId">
                    <option :value="null">-- Sélectionner un fournisseur --</option>
                    <option v-for="f in fournisseurs" :key="f.id" :value="f.id">
                      {{ f.nom }} ({{ f.code }})
                    </option>
                  </select>
                </div>
              </div>
              <div class="col-md-6">
                <div class="mb-3">
                  <label class="form-label fw-bold">Date de commande</label>
                  <p class="form-control-plaintext">{{ formatDate(bc.dateCommande) }}</p>
                </div>
                <div class="mb-3">
                  <label for="dateLivraison" class="form-label fw-bold">Date de livraison prévue</label>
                  <input type="datetime-local" id="dateLivraison" class="form-control" v-model="bc.dateLivraisonPrevue">
                </div>
              </div>
            </div>

            <div class="card bg-light mb-4">
              <div class="card-body">
                <h6 class="card-title mb-3">Articles et Négociation</h6>
                <div class="table-responsive">
                  <table class="table table-sm align-middle">
                    <thead>
                      <tr>
                        <th>Article</th>
                        <th class="text-center" style="width: 120px;">Quantité</th>
                        <th class="text-end" style="width: 180px;">Prix Unitaire (MGA)</th>
                        <th class="text-end" style="width: 120px;">Remise (%)</th>
                        <th class="text-end" style="width: 180px;">Total</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(ligne, index) in lignes" :key="index">
                        <td>{{ ligne.article?.code }} - {{ ligne.article?.nom }}</td>
                        <td>
                          <input type="number" class="form-control form-control-sm text-center" v-model.number="ligne.quantite" min="1" @input="updateTotal">
                        </td>
                        <td>
                          <input type="number" class="form-control form-control-sm text-end" v-model.number="ligne.prixUnitaire" min="0" @input="updateTotal">
                        </td>
                        <td>
                          <input type="number" class="form-control form-control-sm text-end" v-model.number="ligne.remise" min="0" max="100" @input="updateTotal">
                        </td>
                        <td class="text-end fw-bold">{{ formatCurrency(calculerLigneTotal(ligne)) }}</td>
                      </tr>
                    </tbody>
                    <tfoot>
                      <tr class="fw-bold">
                        <td colspan="4" class="text-end">Montant Total:</td>
                        <td class="text-end text-primary" style="font-size: 1.2rem;">{{ formatCurrency(totalBC) }}</td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
              </div>
            </div>

            <div class="d-flex justify-content-end gap-2">
              <button type="button" class="btn btn-secondary" @click="$router.push(`/commandes-achat/${bcId}`)">Annuler</button>
              <button type="submit" class="btn btn-primary" :disabled="isSaving">
                <span v-if="isSaving" class="spinner-border spinner-border-sm me-1"></span>
                <i class="ti ti-device-floppy"></i> Enregistrer les modifications
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import axios from 'axios';
import MainLayout from '../../layouts/MainLayout.vue';

export default {
  name: 'EditBonCommandeFournisseur',
  components: {
    MainLayout
  },
  data() {
    return {
      bcId: null,
      bc: null,
      lignes: [],
      fournisseurs: [],
      isLoading: false,
      isSaving: false,
      errorMessage: '',
      totalBC: 0
    };
  },
  mounted() {
    this.bcId = this.$route.params.id;
    this.loadData();
  },
  methods: {
    async loadData() {
      this.isLoading = true;
      try {
        const [bcRes, fournisseursRes] = await Promise.all([
          axios.get(`/api/bons-commande-fournisseur/${this.bcId}`),
          axios.get('/api/fournisseurs')
        ]);
        
        this.bc = bcRes.data;

        if (this.bc.statut !== 'brouillon') {
          this.errorMessage = 'Ce bon de commande ne peut plus être modifié (statut: ' + this.bc.statut + ')';
          this.bc = null;
          return;
        }

        // Adapter les données pour le formulaire
        if (this.bc.fournisseur) {
          this.bc.fournisseurId = this.bc.fournisseur.id;
        }
        if (this.bc.dateLivraisonPrevue) {
          // Garder le format ISO (YYYY-MM-DDTHH:mm) pour datetime-local
          const date = new Date(this.bc.dateLivraisonPrevue);
          const year = date.getFullYear();
          const month = String(date.getMonth() + 1).padStart(2, '0');
          const day = String(date.getDate()).padStart(2, '0');
          const hours = String(date.getHours()).padStart(2, '0');
          const minutes = String(date.getMinutes()).padStart(2, '0');
          this.bc.dateLivraisonPrevue = `${year}-${month}-${day}T${hours}:${minutes}`;
        }

        this.fournisseurs = fournisseursRes.data;
        
        await this.loadLignes();
        this.updateTotal();
      } catch (error) {
        console.error('Erreur chargement données:', error);
        this.errorMessage = 'Erreur lors du chargement des données';
      } finally {
        this.isLoading = false;
      }
    },
    async loadLignes() {
      const response = await axios.get(`/api/bons-commande-fournisseur/${this.bcId}/lignes`);
      this.lignes = response.data;
    },
    formatDate(dateString) {
      if (!dateString) return '';
      return new Date(dateString).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'MGA',
        minimumFractionDigits: 0
      }).format(value);
    },
    calculerLigneTotal(ligne) {
      const base = (ligne.prixUnitaire || 0) * (ligne.quantite || 0);
      const reduction = base * ((ligne.remise || 0) / 100);
      return base - reduction;
    },
    updateTotal() {
      this.totalBC = this.lignes.reduce((sum, ligne) => sum + this.calculerLigneTotal(ligne), 0);
    },
    async saveBC() {
      this.isSaving = true;
      this.errorMessage = '';
      try {
        // 1. Sauvegarder le BC (fournisseur, date livraison, montant total)
        await axios.put(`/api/bons-commande-fournisseur/${this.bcId}`, {
          fournisseurId: this.bc.fournisseurId,
          dateLivraisonPrevue: this.bc.dateLivraisonPrevue,
          montantTotal: this.totalBC
        });

        // 2. Sauvegarder les lignes (négociation prix/quantité/remise)
        await axios.put(`/api/bons-commande-fournisseur/${this.bcId}/lignes`, this.lignes);

        alert('Modifications enregistrées avec succès');
        this.$router.push(`/commandes-achat/${this.bcId}`);
      } catch (error) {
        console.error('Erreur sauvegarde:', error);
        this.errorMessage = 'Erreur lors de la sauvegarde : ' + (error.response?.data || error.message);
      } finally {
        this.isSaving = false;
      }
    }
  }
};
</script>
