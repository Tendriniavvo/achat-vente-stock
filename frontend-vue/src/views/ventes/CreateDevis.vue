<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Nouveau Devis Client</h5>
            <router-link to="/devis" class="btn btn-outline-secondary">
              <i class="ti ti-arrow-left me-1"></i> Retour
            </router-link>
          </div>

          <form @submit.prevent="submitDevis">
            <div class="row mb-4">
              <div class="col-md-6">
                <label class="form-label fw-bold">Client *</label>
                <select v-model="form.clientId" class="form-select" required>
                  <option value="">Sélectionnez un client</option>
                  <option v-for="client in clients" :key="client.id" :value="client.id">
                    {{ client.nom }} ({{ client.code }})
                  </option>
                </select>
              </div>
              <div class="col-md-6">
                <label class="form-label fw-bold">Date de validité</label>
                <input type="date" v-model="form.dateValidite" class="form-control">
                <small class="text-muted">Par défaut : 30 jours après la création</small>
              </div>
            </div>

            <div class="table-responsive mb-4">
              <table class="table table-bordered align-middle">
                <thead class="table-light">
                  <tr>
                    <th style="width: 40%">Article *</th>
                    <th style="width: 15%">Prix Unit. (MGA)</th>
                    <th style="width: 15%">Quantité *</th>
                    <th style="width: 10%">Remise (%)</th>
                    <th style="width: 15%">Total HT</th>
                    <th style="width: 5%"></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(ligne, index) in form.lignes" :key="index">
                    <td>
                      <select v-model="ligne.articleId" class="form-select" @change="onArticleChange(index)" required>
                        <option value="">Sélectionnez un article</option>
                        <option v-for="article in articles" :key="article.id" :value="article.id">
                          {{ article.nom }} ({{ article.code }}) - Stock: {{ article.stockReel }}
                        </option>
                      </select>
                    </td>
                    <td>
                      <input type="number" v-model.number="ligne.prixUnitaire" class="form-control text-end" step="0.01">
                    </td>
                    <td>
                      <input type="number" v-model.number="ligne.quantite" class="form-control text-center" min="1" required>
                    </td>
                    <td>
                      <input type="number" v-model.number="ligne.remise" class="form-control text-center" min="0" max="100" step="0.1">
                    </td>
                    <td class="text-end fw-bold">
                      {{ formatCurrency(calculerLigneTotal(ligne)) }}
                    </td>
                    <td>
                      <button type="button" class="btn btn-outline-danger btn-sm" @click="removeLigne(index)" :disabled="form.lignes.length === 1">
                        <i class="ti ti-trash"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="4" class="text-end fw-bold fs-5">TOTAL HT :</td>
                    <td class="text-end fw-bold fs-5 text-primary">{{ formatCurrency(totalGeneral) }}</td>
                    <td></td>
                  </tr>
                </tfoot>
              </table>
            </div>

            <div class="mb-4">
              <label class="form-label fw-bold">Notes / Conditions particulières</label>
              <textarea v-model="form.notes" class="form-control" rows="3" placeholder="Ex: Conditions de paiement spécifiques, remise exceptionnelle, etc."></textarea>
              <small class="text-muted">Toute note saisie ici nécessitera une validation par un Responsable Ventes.</small>
            </div>

            <div class="d-flex justify-content-between">
              <button type="button" class="btn btn-outline-primary" @click="addLigne">
                <i class="ti ti-plus me-1"></i> Ajouter une ligne
              </button>
              <button type="submit" class="btn btn-primary px-5" :disabled="isSubmitting">
                <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-1"></span>
                <i v-else class="ti ti-device-floppy me-1"></i>
                Enregistrer le Devis
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '@/layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'CreateDevis',
  components: { MainLayout },
  data() {
    return {
      clients: [],
      articles: [],
      form: {
        clientId: '',
        dateValidite: '',
        notes: '',
        lignes: [
          { articleId: '', quantite: 1, prixUnitaire: 0, remise: 0 }
        ]
      },
      isSubmitting: false,
      user: JSON.parse(localStorage.getItem('user') || '{}')
    };
  },
  computed: {
    currentUser() {
      // Dans MainLayout, on voit que l'objet stocké est une AuthResponse contenant un champ 'user'
      const authData = JSON.parse(localStorage.getItem('user') || '{}');
      return authData.user || null;
    },
    totalGeneral() {
      return this.form.lignes.reduce((acc, ligne) => acc + this.calculerLigneTotal(ligne), 0);
    }
  },
  mounted() {
    this.fetchData();
    // Date validité par défaut : J+30
    const d = new Date();
    d.setDate(d.getDate() + 30);
    this.form.dateValidite = d.toISOString().split('T')[0];
  },
  methods: {
    async fetchData() {
      try {
        const [clientsRes, articlesRes] = await Promise.all([
          axios.get('/api/clients'),
          axios.get('/api/articles')
        ]);
        this.clients = clientsRes.data;
        this.articles = articlesRes.data;
      } catch (error) {
        console.error('Erreur lors du chargement des données', error);
      }
    },
    addLigne() {
      this.form.lignes.push({ articleId: '', quantite: 1, prixUnitaire: 0, remise: 0 });
    },
    removeLigne(index) {
      this.form.lignes.splice(index, 1);
    },
    onArticleChange(index) {
      const articleId = this.form.lignes[index].articleId;
      const article = this.articles.find(a => a.id === articleId);
      if (article) {
        this.form.lignes[index].prixUnitaire = article.prixVente;
      }
    },
    calculerLigneTotal(ligne) {
      const base = (ligne.quantite || 0) * (ligne.prixUnitaire || 0);
      const remise = base * (ligne.remise || 0) / 100;
      return base - remise;
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-MG', { style: 'currency', currency: 'MGA' }).format(value);
    },
    async submitDevis() {
      if (!this.form.clientId) {
        alert('Veuillez sélectionner un client.');
        return;
      }
      if (this.form.lignes.some(l => !l.articleId)) {
        alert('Veuillez sélectionner un article pour chaque ligne.');
        return;
      }
      if (!this.currentUser || !this.currentUser.id) {
        alert('Erreur : Utilisateur non identifié. Veuillez vous reconnecter.');
        return;
      }

      this.isSubmitting = true;
      try {
        const payload = {
          clientId: parseInt(this.form.clientId),
          utilisateurId: this.currentUser.id,
          notes: this.form.notes,
          lignes: this.form.lignes.map(l => ({
            articleId: parseInt(l.articleId),
            quantite: parseInt(l.quantite),
            prixUnitaire: parseFloat(l.prixUnitaire),
            remise: parseFloat(l.remise)
          }))
        };
        await axios.post('/api/devis', payload);
        this.$router.push('/devis');
      } catch (error) {
        console.error('Erreur lors de l\'enregistrement', error);
        alert('Une erreur est survenue lors de l\'enregistrement du devis.');
      } finally {
        this.isSubmitting = false;
      }
    }
  }
};
</script>
