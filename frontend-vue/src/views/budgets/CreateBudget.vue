<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex align-items-center mb-4">
            <button class="btn btn-outline-secondary me-3" @click="$router.back()">
              <i class="ti ti-arrow-left"></i>
            </button>
            <h5 class="card-title fw-semibold mb-0">Ajouter un Budget</h5>
          </div>

          <form @submit.prevent="saveBudget" class="max-width-600">
            <div v-if="errorMessage" class="alert alert-danger" role="alert">
              {{ errorMessage }}
            </div>

            <div class="mb-3">
              <label class="form-label">Département</label>
              <select v-model="budget.departementId" class="form-select" required>
                <option value="">Sélectionner un département</option>
                <option v-for="dept in departements" :key="dept.id" :value="dept.id">
                  {{ dept.nom }}
                </option>
              </select>
            </div>

            <div class="mb-3">
              <label class="form-label">Année</label>
              <input type="number" v-model="budget.annee" class="form-control" required>
            </div>

            <div class="mb-3">
              <label class="form-label">Montant Initial (MGA)</label>
              <input type="number" v-model="budget.montantInitial" class="form-control" step="0.01" required>
            </div>

            <div class="mt-4">
              <button type="submit" class="btn btn-primary" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                Enregistrer le Budget
              </button>
              <button type="button" class="btn btn-outline-secondary ms-2" @click="$router.push('/budgets')">
                Annuler
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../../layouts/MainLayout.vue';

export default {
  name: 'CreateBudget',
  components: {
    MainLayout
  },
  data() {
    return {
      budget: {
        departementId: '',
        annee: new Date().getFullYear(),
        montantInitial: 0
      },
      departements: [],
      isLoading: false,
      errorMessage: '',
      currentUser: null
    };
  },
  mounted() {
    this.loadDepartements();
    const authData = JSON.parse(localStorage.getItem('user'));
    if (authData && authData.user) {
      this.currentUser = authData.user;
    }
  },
  methods: {
    async loadDepartements() {
      try {
        const response = await fetch('/api/departements');
        if (response.ok) {
          this.departements = await response.json();
        }
      } catch (error) {
        console.error('Erreur:', error);
      }
    },
    async saveBudget() {
      this.isLoading = true;
      this.errorMessage = '';
      
      // Validation côté client
      if (!this.budget.departementId) {
        this.errorMessage = 'Veuillez sélectionner un département';
        this.isLoading = false;
        return;
      }
      if (!this.budget.annee || this.budget.annee < 2000) {
        this.errorMessage = 'Veuillez saisir une année valide';
        this.isLoading = false;
        return;
      }
      if (this.budget.montantInitial <= 0) {
        this.errorMessage = 'Le montant initial doit être supérieur à 0';
        this.isLoading = false;
        return;
      }

      try {
        const response = await fetch('/api/budgets', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            departement: { id: parseInt(this.budget.departementId) },
            annee: parseInt(this.budget.annee),
            montantInitial: parseFloat(this.budget.montantInitial),
            montantConsomme: 0
          })
        });

        if (response.ok) {
          alert('Budget enregistré avec succès');
          this.$router.push('/budgets');
        } else {
          const errorMsg = await response.text();
          this.errorMessage = errorMsg || 'Erreur lors de l\'enregistrement';
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur';
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.max-width-600 {
  max-width: 600px;
}
.card {
  border: none;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
</style>
