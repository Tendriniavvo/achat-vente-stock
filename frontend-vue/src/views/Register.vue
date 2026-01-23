<template>
  <div class="page-wrapper" id="main-wrapper" data-layout="vertical" data-navbarbg="skin6" data-sidebartype="full"
    data-sidebar-position="fixed" data-header-position="fixed">
    <div class="position-relative overflow-hidden radial-gradient min-vh-100 d-flex align-items-center justify-content-center">
      <div class="d-flex align-items-center justify-content-center w-100">
        <div class="row justify-content-center w-100">
          <div class="col-md-8 col-lg-6 col-xxl-3">
            <div class="card mb-0">
              <div class="card-body">
                <a href="#" class="text-nowrap logo-img text-center d-block py-3 w-100">
                  <img src="@/assets/images/logos/dark-logo.svg" width="180" alt="">
                </a>
                <p class="text-center">Gestion Achat-Vente-Stock</p>
                <div v-if="errorMessage" class="alert alert-danger" role="alert">
                  {{ errorMessage }}
                </div>
                <div v-if="successMessage" class="alert alert-success" role="alert">
                  {{ successMessage }}
                </div>
                <form @submit.prevent="handleRegister">
                  <div class="mb-3">
                    <label for="nom" class="form-label">Nom</label>
                    <input type="text" class="form-control" id="nom" v-model="nom" required>
                  </div>
                  <div class="mb-3">
                    <label for="prenom" class="form-label">Prénom</label>
                    <input type="text" class="form-control" id="prenom" v-model="prenom" required>
                  </div>
                  <div class="mb-3">
                    <label for="email" class="form-label">Adresse email</label>
                    <input type="email" class="form-control" id="email" v-model="email" required>
                  </div>
                  <div class="mb-3">
                    <label for="role" class="form-label">Rôle</label>
                    <select class="form-select" id="role" v-model="selectedRole" required>
                      <option value="" disabled>Sélectionnez un rôle</option>
                      <option v-for="role in roles" :key="role.id" :value="role.id">
                        {{ role.nom }} - {{ role.description }}
                      </option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label for="departement" class="form-label">Département</label>
                    <select class="form-select" id="departement" v-model="selectedDepartement">
                      <option value="">Aucun département</option>
                      <option v-for="dept in departements" :key="dept.id" :value="dept.id">
                        (ID: {{ dept.id }}) {{ dept.nom }} ({{ dept.code }})
                      </option>
                    </select>
                  </div>
                  <div class="mb-4">
                    <label for="password" class="form-label">Mot de passe</label>
                    <input type="password" class="form-control" id="password" v-model="password" required>
                  </div>
                  <button type="submit" class="btn btn-primary w-100 py-8 fs-4 mb-4 rounded-2" :disabled="isLoading">
                    <span v-if="isLoading">Inscription en cours...</span>
                    <span v-else>S'inscrire</span>
                  </button>
                  <div class="d-flex align-items-center justify-content-center">
                    <p class="fs-4 mb-0 fw-bold">Vous avez déjà un compte?</p>
                    <router-link class="text-primary fw-bold ms-2" to="/login">Se connecter</router-link>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const nom = ref('');
const prenom = ref('');
const email = ref('');
const password = ref('');
const roles = ref([]);
const selectedRole = ref('');
const departements = ref([]);
const selectedDepartement = ref('');
const errorMessage = ref('');
const successMessage = ref('');
const isLoading = ref(false);

// Charger les rôles disponibles
const loadRoles = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/roles');
    roles.value = response.data;
  } catch (error) {
    console.error('Erreur lors du chargement des rôles:', error);
  }
};

// Charger les départements disponibles
const loadDepartements = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/departements');
    departements.value = response.data;
  } catch (error) {
    console.error('Erreur lors du chargement des départements:', error);
  }
};

onMounted(() => {
  loadRoles();
  loadDepartements();
});

const handleRegister = async () => {
  errorMessage.value = '';
  successMessage.value = '';
  isLoading.value = true;
  
  try {
    const user = {
      nom: nom.value,
      prenom: prenom.value,
      email: email.value,
      motDePasse: password.value,
      roleIds: (selectedRole.value !== null && selectedRole.value !== '') ? [parseInt(selectedRole.value)] : [],
      departementId: (selectedDepartement.value !== null && selectedDepartement.value !== '') ? parseInt(selectedDepartement.value) : null
    };
    
    console.log('Données d\'inscription envoyées:', user);
    
    const response = await axios.post('http://localhost:8080/api/auth/register', user);
    
    if (response.data && response.data.user) {
      successMessage.value = 'Inscription réussie ! Redirection vers la page de connexion...';
      setTimeout(() => {
        router.push('/login');
      }, 2000);
    } else {
      errorMessage.value = 'Erreur lors de l\'inscription';
    }
  } catch (error) {
    console.error('Erreur d\'inscription:', error);
    errorMessage.value = error.response?.data || 'Erreur lors de l\'inscription';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.radial-gradient {
  background: radial-gradient(circle, rgba(94,114,228,0.1) 0%, rgba(255,255,255,1) 100%);
}
</style>
