<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Gestion des Budgets</h5>
            <router-link v-if="hasRole('FINANCE') || hasRole('ADMIN')" 
                    class="btn btn-primary" 
                    to="/budgets/create">
              <i class="ti ti-plus"></i> Nouveau Budget
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

          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead>
                <tr>
                  <th>Département</th>
                  <th>Année</th>
                  <th class="text-end">Montant Initial</th>
                  <th class="text-end">Montant Consommé</th>
                  <th class="text-end">Montant Disponible</th>
                  <th>Progression</th>
                  <th v-if="hasRole('FINANCE') || hasRole('ADMIN')" class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="budget in budgets" :key="budget.id">
                  <td>{{ budget.departement?.nom }}</td>
                  <td>{{ budget.annee }}</td>
                  <td class="text-end">{{ formatCurrency(budget.montantInitial) }}</td>
                  <td class="text-end">{{ formatCurrency(budget.montantConsomme) }}</td>
                  <td class="text-end fw-bold" :class="budget.montantDisponible < 0 ? 'text-danger' : 'text-success'">
                    {{ formatCurrency(budget.montantDisponible) }}
                  </td>
                  <td style="width: 200px;">
                    <div class="progress" style="height: 10px;">
                      <div class="progress-bar" 
                           :class="getProgressClass(budget)" 
                           role="progressbar" 
                           :style="{ width: getProgressPercentage(budget) + '%' }" 
                           :aria-valuenow="getProgressPercentage(budget)" 
                           aria-valuemin="0" 
                           aria-valuemax="100">
                      </div>
                    </div>
                    <small class="text-muted">{{ getProgressPercentage(budget) }}% utilisé</small>
                  </td>
                  <td v-if="hasRole('FINANCE') || hasRole('ADMIN')" class="text-center">
                    <button class="btn btn-sm btn-outline-danger" @click="confirmDelete(budget)">
                      <i class="ti ti-trash"></i>
                    </button>
                  </td>
                </tr>
                <tr v-if="budgets.length === 0">
                  <td :colspan="hasRole('FINANCE') || hasRole('ADMIN') ? 7 : 6" class="text-center py-4">Aucun budget défini</td>
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
import MainLayout from '../../layouts/MainLayout.vue';

export default {
  name: 'Budgets',
  components: {
    MainLayout
  },
  data() {
    return {
      budgets: [],
      departements: [],
      isLoading: false,
      errorMessage: '',
      currentUser: null
    };
  },
  mounted() {
    this.loadBudgets();
    this.loadDepartements();
    const authData = JSON.parse(localStorage.getItem('user'));
    if (authData && authData.user) {
      this.currentUser = authData.user;
    }
  },
  methods: {
    hasRole(roleNom) {
      if (!this.currentUser || !this.currentUser.roles) return false;
      return this.currentUser.roles.some(r => r.nom.toUpperCase() === roleNom.toUpperCase());
    },
    async loadBudgets() {
      this.isLoading = true;
      try {
        const response = await fetch('/api/budgets');
        if (response.ok) {
          this.budgets = await response.json();
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur lors du chargement des budgets';
      } finally {
        this.isLoading = false;
      }
    },
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
    async confirmDelete(budget) {
      if (confirm(`Êtes-vous sûr de vouloir supprimer le budget ${budget.annee} du département ${budget.departement?.nom} ?`)) {
        try {
          const response = await fetch(`/api/budgets/${budget.id}`, {
            method: 'DELETE'
          });
          if (response.ok) {
            this.loadBudgets();
            alert('Budget supprimé avec succès');
          } else {
            alert('Erreur lors de la suppression');
          }
        } catch (error) {
          console.error('Erreur:', error);
          alert('Erreur de connexion');
        }
      }
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'MGA',
        minimumFractionDigits: 0
      }).format(value || 0);
    },
    getProgressPercentage(budget) {
      if (!budget.montantInitial || budget.montantInitial === 0) return 0;
      const percentage = (budget.montantConsomme / budget.montantInitial) * 100;
      return Math.round(percentage);
    },
    getProgressClass(budget) {
      const percentage = this.getProgressPercentage(budget);
      if (percentage < 70) return 'bg-success';
      if (percentage < 90) return 'bg-warning';
      return 'bg-danger';
    }
  }
};
</script>

<style scoped>
.card {
  border: none;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
.progress {
  background-color: #e9ecef;
}
</style>
