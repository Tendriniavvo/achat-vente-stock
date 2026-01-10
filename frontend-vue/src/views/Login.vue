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
                <form @submit.prevent="handleLogin">
                  <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" v-model="email" required>
                  </div>
                  <div class="mb-4">
                    <label for="password" class="form-label">Mot de passe</label>
                    <input type="password" class="form-control" id="password" v-model="password" required>
                  </div>
                  <div class="d-flex align-items-center justify-content-between mb-4">
                    <div class="form-check">
                      <input class="form-check-input primary" type="checkbox" value="" id="flexCheckChecked" v-model="rememberMe">
                      <label class="form-check-label text-dark" for="flexCheckChecked">
                        Se souvenir de moi
                      </label>
                    </div>
                    <a class="text-primary fw-bold" href="#">Mot de passe oublié ?</a>
                  </div>
                  <button type="submit" class="btn btn-primary w-100 py-8 fs-4 mb-4 rounded-2" :disabled="isLoading">
                    <span v-if="isLoading">Connexion en cours...</span>
                    <span v-else>Se connecter</span>
                  </button>
                  <div class="d-flex align-items-center justify-content-center">
                    <p class="fs-4 mb-0 fw-bold">Nouveau sur Modernize?</p>
                    <router-link class="text-primary fw-bold ms-2" to="/register">Créer un compte</router-link>
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
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const email = ref('');
const password = ref('');
const rememberMe = ref(false);
const errorMessage = ref('');
const isLoading = ref(false);

const handleLogin = async () => {
  errorMessage.value = '';
  isLoading.value = true;
  
  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: email.value,
        motDePasse: password.value,
        rememberMe: rememberMe.value
      })
    });
    
    const data = await response.json();
    
    if (data.success) {
      // Stocker les informations de l'utilisateur
      localStorage.setItem('user', JSON.stringify(data));
      // Redirection vers le dashboard
      router.push('/');
    } else {
      errorMessage.value = data.message;
    }
  } catch (error) {
    console.error('Erreur de connexion:', error);
    errorMessage.value = 'Erreur de connexion au serveur';
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
