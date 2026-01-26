<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title fw-semibold mb-4">Gestion des Habilitations</h5>
          <p class="mb-4">Configurez les accès aux différents modules de l'application pour chaque rôle.</p>

          <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-bordered table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th style="width: 250px;">Module / Section</th>
                  <th v-for="role in roles" :key="role.id" class="text-center">
                    {{ role.nom }}
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="section in sections" :key="section.path">
                  <td class="fw-bold">
                    {{ section.label }}
                    <br>
                    <small class="text-muted fw-normal">{{ section.path }}</small>
                  </td>
                  <td v-for="role in roles" :key="role.id" class="text-center">
                    <div class="form-check d-flex justify-content-center">
                      <input 
                        class="form-check-input" 
                        type="checkbox" 
                        :checked="hasPermission(role.id, section.path)"
                        @change="togglePermission(role.id, section)"
                      >
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="d-flex justify-content-end mt-4">
            <button @click="saveChanges" class="btn btn-primary" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              Enregistrer les modifications
            </button>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import MainLayout from '../../layouts/MainLayout.vue';

const roles = ref([]);
const permissions = ref([]); // Toutes les permissions existantes
const loading = ref(true);
const saving = ref(false);

// Liste des sections de l'application (basée sur router/index.js)
const sections = [
  { label: 'Tableau de Bord', path: '/dashboard', module: 'DASHBOARD' },
  { label: 'Gestion du Stock (Dashboard)', path: '/dashboard/stock', module: 'DASHBOARD' },
  { label: 'Consommation Budgétaire (Dashboard)', path: '/dashboard/budget', module: 'DASHBOARD' },
  { label: 'Efficacité Approbation (Dashboard)', path: '/dashboard/achats', module: 'DASHBOARD' },
  { label: 'Gestion des Achats', path: '/achats', module: 'ACHATS' },
  { label: 'Nouvelle Demande d\'Achat', path: '/achats/create', module: 'ACHATS' },
  { label: 'Bons de Commande Fournisseur', path: '/commandes-achat', module: 'ACHATS' },
  { label: 'Gestion des Fournisseurs', path: '/fournisseurs', module: 'PARTENAIRES' },
  { label: 'Gestion des Articles', path: '/articles', module: 'STOCK' },
  { label: 'Vue d\'ensemble du Stock', path: '/stock/niveaux', module: 'STOCK' },
  { label: 'Mouvements de Stock', path: '/stock/mouvements', module: 'STOCK' },
  { label: 'Gestion des Stocks (Détails)', path: '/stock/stocks', module: 'STOCK' },
  { label: 'Gestion des Lots', path: '/stock/lots', module: 'STOCK' },
  { label: 'Audits de Stock', path: '/stock/audits', module: 'STOCK' },
  { label: 'Ajustements de Stock', path: '/stock/ajustements', module: 'STOCK' },
  { label: 'Inventaires de Stock', path: '/stock/inventaires', module: 'STOCK' },
  { label: 'Réceptions de Marchandises', path: '/receptions', module: 'STOCK' },
  { label: 'Gestion des Dépôts', path: '/depots', module: 'STOCK' },
  { label: 'Emplacements de Stock', path: '/emplacements', module: 'STOCK' },
  { label: 'Devis Clients', path: '/devis', module: 'VENTES' },
  { label: 'Commandes Client', path: '/commandes-client', module: 'VENTES' },
  { label: 'Livraisons', path: '/livraisons', module: 'VENTES' },
  { label: 'Factures Clients', path: '/factures-client', module: 'VENTES' },
  { label: 'Encaissements', path: '/encaissements', module: 'VENTES' },
  { label: 'Gestion des Clients', path: '/clients', module: 'PARTENAIRES' },
  { label: 'Gestion des Budgets', path: '/budgets', module: 'FINANCES' },
  { label: 'Factures Fournisseurs', path: '/factures', module: 'FINANCES' },
  { label: 'Paiements', path: '/paiements', module: 'FINANCES' },
  { label: 'Gestion des Utilisateurs', path: '/utilisateurs', module: 'SYSTEME' },
  { label: 'Gestion des Rôles', path: '/roles', module: 'SYSTEME' },
  { label: 'Configuration des Habilitations', path: '/habilitations', module: 'SYSTEME' },
];

const fetchData = async () => {
  loading.value = true;
  try {
    const [rolesRes, permsRes] = await Promise.all([
      axios.get('/api/roles'),
      axios.get('/api/permissions')
    ]);
    roles.value = rolesRes.data;
    permissions.value = permsRes.data;
  } catch (error) {
    console.error('Erreur lors du chargement des données:', error);
    alert('Erreur lors du chargement des données');
  } finally {
    loading.value = false;
  }
};

onMounted(fetchData);

const hasPermission = (roleId, path) => {
  return permissions.value.some(p => p.role.id === roleId && p.path === path);
};

const togglePermission = (roleId, section) => {
  const index = permissions.value.findIndex(p => p.role.id === roleId && p.path === section.path);
  
  if (index > -1) {
    // Supprimer la permission si elle existe
    permissions.value.splice(index, 1);
  } else {
    // Ajouter la permission
    permissions.value.push({
      role: { id: roleId },
      module: section.module,
      action: 'ACCESS',
      path: section.path
    });
  }
};

const saveChanges = async () => {
  saving.value = true;
  try {
    // Pour chaque rôle, envoyer ses nouvelles permissions au backend
    const savePromises = roles.value.map(role => {
      const rolePerms = permissions.value.filter(p => p.role.id === role.id);
      return axios.post(`/api/permissions/role/${role.id}/bulk`, rolePerms);
    });
    
    await Promise.all(savePromises);
    alert('Habilitations enregistrées avec succès !');
    await fetchData(); // Recharger pour avoir les IDs générés par le backend
  } catch (error) {
    console.error('Erreur lors de l\'enregistrement:', error);
    alert('Erreur lors de l\'enregistrement des modifications');
  } finally {
    saving.value = false;
  }
};
</script>

<style scoped>
.table th, .table td {
  padding: 1rem;
}
.form-check-input {
  width: 1.5em;
  height: 1.5em;
  cursor: pointer;
}
</style>
