<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Enregistrer une Réception</h5>
            <router-link to="/receptions" class="btn btn-outline-secondary btn-sm">
              <i class="ti ti-arrow-left me-1"></i> Retour
            </router-link>
          </div>

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status"></div>
            <p class="mt-2 text-muted">Chargement des données...</p>
          </div>

          <div v-else-if="errorMessage" class="alert alert-danger mb-4">
            <i class="ti ti-alert-circle me-2 fs-4"></i>
            {{ errorMessage }}
            <button class="btn btn-sm btn-outline-danger ms-3" @click="loadBC">Réessayer</button>
          </div>

          <div v-else-if="bc" class="row">
            <!-- DEBUG INFO (Temporaire) -->
            <div class="col-12 mb-3" v-if="$route.query.debug">
              <div class="card bg-dark text-white p-3">
                <h6>Debug Info</h6>
                <pre style="max-height: 200px; overflow: auto;">BC: {{ JSON.stringify(bc, null, 2) }}</pre>
                <pre style="max-height: 200px; overflow: auto;">Items: {{ JSON.stringify(items, null, 2) }}</pre>
              </div>
            </div>

            <!-- Informations BC -->
            <div class="col-md-12 mb-4">
              <div class="p-3 bg-light rounded border">
                <div class="row">
                  <div class="col-md-3">
                    <p class="mb-1 text-muted small">Référence BC</p>
                    <p class="fw-bold mb-0">{{ bc.reference }} (ID: {{ bc.id }})</p>
                  </div>
                  <div class="col-md-3">
                    <p class="mb-1 text-muted small">Fournisseur</p>
                    <p class="fw-bold mb-0">{{ bc.fournisseur?.nom }}</p>
                  </div>
                  <div class="col-md-3">
                    <p class="mb-1 text-muted small">Date Commande</p>
                    <p class="fw-bold mb-0">{{ formatDate(bc.dateCommande) }}</p>
                  </div>
                  <div class="col-md-3">
                    <p class="mb-1 text-muted small">Statut Actuel</p>
                    <p class="fw-bold mb-0">{{ formatStatut(bc.statut) }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Formulaire Réception -->
            <div class="col-md-12">
              <div class="row mb-4">
                <div class="col-md-6">
                  <label class="form-label fw-bold">Dépôt de réception <span class="text-danger">*</span></label>
                  <select v-model="selectedDepotId" class="form-select border-primary" @change="loadEmplacements">
                    <option value="">Sélectionner un dépôt</option>
                    <option v-for="depot in depots" :key="depot.id" :value="depot.id">{{ depot.nom }}</option>
                  </select>
                </div>
                <div class="col-md-6 d-flex align-items-end">
                  <div class="alert alert-info mb-0 py-2 w-100">
                    <i class="ti ti-info-circle me-1"></i>
                    Saisissez les quantités reçues et les informations de lot dans les colonnes en bleu.
                  </div>
                </div>
              </div>

              <div v-if="items.length === 0" class="alert alert-warning mb-4">
                <i class="ti ti-alert-triangle me-2 fs-4"></i>
                Aucun article trouvé pour ce Bon de Commande (ID: {{ bc.id }}). 
                Veuillez vérifier que le BC contient bien des lignes de commande.
              </div>

              <div v-else class="table-responsive">
                <table class="table table-bordered align-middle">
                  <thead class="table-light">
                    <tr>
                      <th style="width: 250px;">Article</th>
                      <th class="text-center">Qté Commandée</th>
                      <th class="text-center">Déjà Reçue</th>
                      <th class="text-center">Reste à recevoir</th>
                      <th class="text-center" style="width: 120px; background-color: #f8f9fa;">Qté Reçue <span class="text-danger">*</span></th>
                      <th style="width: 180px; background-color: #f8f9fa;">Lot / Série <span class="text-danger">*</span></th>
                      <th style="width: 180px; background-color: #f8f9fa;">Expiration</th>
                      <th style="background-color: #f8f9fa;">Emplacement</th>
                      <th style="background-color: #f8f9fa;">Observations</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(item, index) in items" :key="index">
                      <td>
                        <div class="fw-bold">{{ item.articleNom }}</div>
                        <small class="text-muted">{{ item.articleCode }}</small>
                      </td>
                      <td class="text-center">{{ item.qteCommandee }}</td>
                      <td class="text-center text-success">{{ item.qteDejaRecue }}</td>
                      <td class="text-center text-primary fw-bold">{{ item.qteRestante }}</td>
                      <td>
                        <input 
                          type="number" 
                          v-model.number="item.quantiteRecue" 
                          class="form-control text-center border-primary" 
                          :max="item.qteRestante"
                          min="0"
                          placeholder="0"
                        >
                      </td>
                      <td>
                        <input type="text" v-model="item.numeroLot" class="form-control border-primary" placeholder="Numéro de lot">
                      </td>
                      <td>
                        <input type="date" v-model="item.dateExpiration" class="form-control">
                      </td>
                      <td>
                        <select v-model="item.emplacementId" class="form-select border-primary">
                          <option value="">Sélectionner un emplacement</option>
                          <option v-for="emp in emplacements" :key="emp.id" :value="emp.id">
                            {{ emp.code }} ({{ emp.typeEmplacement?.libelle || 'N/A' }}) {{ emp.description ? '- ' + emp.description : '' }}
                          </option>
                        </select>
                      </td>
                      <td>
                        <input type="text" v-model="item.observations" class="form-control" placeholder="RAS, défectueux, etc.">
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <div class="d-flex justify-content-end mt-4">
                <button 
                  class="btn btn-primary px-4 py-2" 
                  @click="enregistrer" 
                  :disabled="isSaving || !isValid"
                >
                  <span v-if="isSaving" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="ti ti-check me-2"></i> Enregistrer la réception
                </button>
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
import MainLayout from '@/layouts/MainLayout.vue';

export default {
  name: 'EnregistrerReception',
  components: {
    MainLayout
  },
  data() {
    return {
      bcId: this.$route.params.bcId,
      bc: null,
      items: [],
      depots: [],
      emplacements: [],
      selectedDepotId: '',
      isLoading: false,
      isSaving: false,
      errorMessage: ''
    };
  },
  computed: {
    isValid() {
      if (!this.selectedDepotId) return false;
      const totalRecu = this.items.reduce((sum, item) => sum + (item.quantiteRecue || 0), 0);
      return totalRecu > 0;
    }
  },
  async mounted() {
    await this.loadBC();
    await this.loadDepots();
  },
  methods: {
    async loadBC() {
      this.isLoading = true;
      this.errorMessage = '';
      try {
        console.log('Tentative de chargement du BC ID:', this.bcId);
        
        // 1. Charger le BC et ses lignes
        const [bcRes, linesRes] = await Promise.all([
          axios.get(`/api/bons-commande-fournisseur/${this.bcId}`),
          axios.get(`/api/bons-commande-fournisseur/${this.bcId}/lignes`)
        ]);

        this.bc = bcRes.data;
        const lines = linesRes.data;
        
        console.log('Réponse BC:', this.bc);
        console.log('Réponse Lignes:', lines);

        if (!Array.isArray(lines)) {
          console.error('Lignes n\'est pas un tableau:', lines);
          this.items = [];
        } else {
          this.items = lines.map(line => ({
            articleId: line.article?.id,
            articleNom: line.article?.nom || 'Article inconnu',
            articleCode: line.article?.code || '-',
            qteCommandee: line.quantite,
            qteDejaRecue: 0, // Temporairement à 0 pour débogage
            qteRestante: line.quantite,
            quantiteRecue: 0,
            numeroLot: '',
            dateExpiration: '',
            emplacementId: '',
            observations: ''
          }));
        }
        
        // 2. Tenter de charger les réceptions séparément (non bloquant)
        this.chargerDejaRecu(lines);

      } catch (error) {
        console.error('Erreur lors du chargement:', error);
        this.errorMessage = 'Erreur de chargement: ' + (error.response?.data?.message || error.message);
      } finally {
        this.isLoading = false;
      }
    },
    async chargerDejaRecu(lines) {
      try {
        const receptionsRes = await axios.get('/api/receptions');
        const bcReceptions = (receptionsRes.data || []).filter(r => r.bonCommande?.id == this.bcId);
        
        const dejaRecuMap = {};
        for (const r of bcReceptions) {
          const lrRes = await axios.get(`/api/receptions/${r.id}/lignes`);
          (lrRes.data || []).forEach(l => {
            if (l.article) {
              dejaRecuMap[l.article.id] = (dejaRecuMap[l.article.id] || 0) + l.quantiteRecue;
            }
          });
        }

        // Mettre à jour les items avec les vraies quantités déjà reçues
        this.items = this.items.map(item => {
          const dejaRecu = dejaRecuMap[item.articleId] || 0;
          return {
            ...item,
            qteDejaRecue: dejaRecu,
            qteRestante: Math.max(0, item.qteCommandee - dejaRecu)
          };
        });
      } catch (err) {
        console.warn('Erreur lors de la mise à jour des quantités déjà reçues:', err);
      }
    },
    async loadDepots() {
      try {
        const response = await axios.get('/api/depots');
        this.depots = response.data;
        if (this.depots.length > 0) {
          this.selectedDepotId = this.depots[0].id;
          this.loadEmplacements();
        }
      } catch (error) {
        console.error('Erreur chargement dépôts:', error);
      }
    },
    async loadEmplacements() {
      if (!this.selectedDepotId) {
        this.emplacements = [];
        return;
      }
      try {
        console.log('Chargement des emplacements pour le dépôt:', this.selectedDepotId);
        const response = await axios.get(`/api/emplacements/depot/${this.selectedDepotId}`);
        this.emplacements = response.data || [];
        console.log('Emplacements chargés:', this.emplacements);
      } catch (error) {
        console.error('Erreur chargement emplacements:', error);
      }
    },
    async enregistrer() {
      if (!this.isValid) return;
      
      this.isSaving = true;
      try {
        const user = JSON.parse(localStorage.getItem('user'));
        const userData = user.user || user; // Gérer les deux formats possibles
        
        const payload = {
          bcId: parseInt(this.bcId),
          utilisateurId: userData.id,
          depotId: parseInt(this.selectedDepotId),
          items: this.items
            .filter(item => item.quantiteRecue > 0)
            .map(item => ({
              articleId: item.articleId,
              quantiteRecue: item.quantiteRecue,
              numeroLot: item.numeroLot,
              dateExpiration: item.dateExpiration ? item.dateExpiration + 'T00:00:00' : null,
              emplacementId: item.emplacementId ? parseInt(item.emplacementId) : null,
              ecart: item.observations
            }))
        };

        const response = await axios.post('/api/receptions/enregistrer', payload);

        if (response.status === 200 || response.status === 201) {
          this.$router.push('/receptions');
        }
      } catch (error) {
        console.error('Erreur enregistrement:', error);
        const msg = error.response?.data?.message || 'Une erreur est survenue lors de l\'enregistrement';
        alert('Erreur: ' + msg);
      } finally {
        this.isSaving = false;
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '-';
      return new Date(dateStr).toLocaleDateString('fr-FR');
    },
    formatStatut(statut) {
      if (!statut) return '-';
      const map = {
        'envoye': 'Envoyé au fournisseur',
        'reception_partielle': 'Réception Partielle',
        'receptionne': 'Réceptionné'
      };
      return map[statut.toLowerCase()] || statut;
    }
  }
}
</script>