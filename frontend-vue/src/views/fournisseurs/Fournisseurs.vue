<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Gestion des Fournisseurs</h5>
            <button class="btn btn-primary" @click="nouveauFournisseur">
              <i class="ti ti-plus"></i> Nouveau Fournisseur
            </button>
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
                  <th>Nom</th>
                  <th>Email</th>
                  <th>Téléphone</th>
                  <th>Adresse</th>
                  <th class="text-center">Statut</th>
                  <th class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="f in fournisseurs" :key="f.id">
                  <td class="fw-bold">{{ f.nom }}</td>
                  <td>{{ f.email }}</td>
                  <td>{{ f.telephone }}</td>
                  <td>{{ f.adresse }}</td>
                  <td class="text-center">
                    <span :class="f.actif ? 'badge bg-success' : 'badge bg-danger'">
                      {{ f.actif ? 'Actif' : 'Inactif' }}
                    </span>
                  </td>
                  <td class="text-center">
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-primary" @click="voirDetails(f.id)" title="Détails">
                        <i class="ti ti-eye"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-warning" @click="modifier(f.id)" title="Modifier">
                        <i class="ti ti-edit"></i>
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="fournisseurs.length === 0">
                  <td colspan="6" class="text-center py-4">Aucun fournisseur trouvé</td>
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
  name: 'Fournisseurs',
  components: {
    MainLayout
  },
  data() {
    return {
      fournisseurs: [],
      isLoading: false,
      errorMessage: ''
    };
  },
  mounted() {
    this.loadFournisseurs();
  },
  methods: {
    async loadFournisseurs() {
      this.isLoading = true;
      try {
        const response = await fetch('/api/fournisseurs');
        if (response.ok) {
          this.fournisseurs = await response.json();
        } else {
          this.errorMessage = 'Erreur lors du chargement des fournisseurs';
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.errorMessage = 'Erreur de connexion au serveur';
      } finally {
        this.isLoading = false;
      }
    },
    nouveauFournisseur() {
      alert('Fonctionnalité de création de fournisseur en cours de développement');
    },
    voirDetails(id) {
      alert('Détails du fournisseur ID: ' + id + ' (En cours de développement)');
    },
    modifier(id) {
      alert('Modification du fournisseur ID: ' + id + ' (En cours de développement)');
    }
  }
};
</script>
