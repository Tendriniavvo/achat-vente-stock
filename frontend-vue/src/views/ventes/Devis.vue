<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Gestion des Devis Clients</h5>
            <div class="d-flex gap-2">
              <button class="btn btn-outline-primary" @click="fetchDevis" :disabled="isLoading">
                <i class="ti ti-refresh me-1" :class="{ 'spinner-border spinner-border-sm': isLoading }"></i>
                Actualiser
              </button>
              <router-link to="/devis/create" class="btn btn-primary">
                <i class="ti ti-plus me-1"></i> Nouveau Devis
              </router-link>
            </div>
          </div>

          <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ errorMessage }}
            <button type="button" class="btn-close" @click="errorMessage = ''"></button>
          </div>

          <div class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light text-nowrap">
                <tr>
                  <th>Référence</th>
                  <th>Client</th>
                  <th>Date</th>
                  <th>Validité</th>
                  <th class="text-end">Montant Total</th>
                  <th class="text-center">Statut</th>
                  <th class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="isLoading">
                  <td colspan="7" class="text-center py-5">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Chargement...</span>
                    </div>
                  </td>
                </tr>
                <tr v-else-if="devisList.length === 0">
                  <td colspan="7" class="text-center py-5 text-muted">
                    Aucun devis trouvé.
                  </td>
                </tr>
                <tr v-for="devis in devisList" :key="devis.id">
                  <td>
                    <router-link :to="'/devis/' + devis.id" class="fw-bold text-primary">
                      {{ devis.reference }}
                    </router-link>
                  </td>
                  <td>
                    <div class="d-flex align-items-center">
                      <div>
                        <h6 class="mb-0 fw-semibold">{{ devis.client?.nom }}</h6>
                        <small class="text-muted">{{ devis.client?.code }}</small>
                      </div>
                    </div>
                  </td>
                  <td>{{ formatDate(devis.dateDevis) }}</td>
                  <td>
                    <span :class="{'text-danger fw-bold': isExpired(devis.dateValidite)}">
                      {{ formatDate(devis.dateValidite) }}
                    </span>
                  </td>
                  <td class="text-end fw-bold">{{ formatCurrency(devis.montantTotal) }}</td>
                  <td class="text-center">
                    <span :class="getStatutClass(devis.statut)">{{ devis.statut }}</span>
                    <i v-if="devis.remiseExceptionnelle || devis.notes" class="ti ti-alert-triangle text-warning ms-1" :title="devis.remiseExceptionnelle ? 'Remise > 10%' : 'Conditions particulières'"></i>
                  </td>
                  <td class="text-center">
                    <div class="d-flex justify-content-center gap-1">
                      <router-link :to="'/devis/' + devis.id" class="btn btn-sm btn-light-primary text-primary" title="Voir Détails">
                        <i class="ti ti-eye"></i>
                      </router-link>
                      <button v-if="devis.statut === 'brouillon' && (!devis.remiseExceptionnelle && !devis.notes || isResponsableVentes)" 
                              class="btn btn-sm btn-light-success text-success" 
                              @click="validerDevis(devis.id, devis.remiseExceptionnelle || devis.notes)" 
                              :title="devis.remiseExceptionnelle || devis.notes ? 'Valider le Devis' : 'Confirmer le Devis'">
                        <i class="ti ti-check"></i>
                      </button>
                      <button v-if="devis.statut === 'brouillon'" 
                              class="btn btn-sm btn-light-danger text-danger" 
                              @click="deleteDevis(devis.id)" 
                              title="Supprimer">
                        <i class="ti ti-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
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
  name: 'DevisList',
  components: { MainLayout },
  computed: {
    currentUser() {
      const authData = JSON.parse(localStorage.getItem('user') || '{}');
      return authData.user || null;
    },
    isResponsableVentes() {
      return this.currentUser?.roles?.some(r => 
        r.nom === 'Responsable ventes' || r.nom === 'Administrateur'
      );
    }
  },
  data() {
    return {
      devisList: [],
      isLoading: false,
      errorMessage: ''
    };
  },
  mounted() {
    this.fetchDevis();
  },
  methods: {
    async fetchDevis() {
      this.isLoading = true;
      try {
        const response = await axios.get('/api/devis');
        this.devisList = response.data.sort((a, b) => new Date(b.dateDevis) - new Date(a.dateDevis));
      } catch (error) {
        this.errorMessage = 'Erreur lors du chargement des devis';
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
    isExpired(date) {
      if (!date) return false;
      return new Date(date) < new Date();
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
    async deleteDevis(id) {
      if (confirm('Êtes-vous sûr de vouloir supprimer ce devis ?')) {
        try {
          await axios.delete(`/api/devis/${id}`);
          this.fetchDevis();
        } catch (error) {
          alert('Erreur lors de la suppression');
        }
      }
    },
    async validerDevis(id, needsValidation) {
      const msg = needsValidation 
        ? 'Voulez-vous valider ce devis ? Cela confirmera les remises exceptionnelles.' 
        : 'Voulez-vous confirmer ce devis ? Il pourra ensuite être envoyé au client.';
        
      if (confirm(msg)) {
        try {
          await axios.post(`/api/devis/${id}/valider`, {
            utilisateurId: this.currentUser.id
          });
          this.fetchDevis();
        } catch (error) {
          const errorMsg = error.response?.data || 'Erreur lors de l\'opération';
          alert(typeof errorMsg === 'string' ? errorMsg : 'Erreur lors de l\'opération. Vérifiez vos permissions.');
        }
      }
    }
  }
};
</script>
