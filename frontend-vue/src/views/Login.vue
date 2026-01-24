<template>
  <div class="page-wrapper" id="main-wrapper" data-layout="vertical" data-navbarbg="skin6" data-sidebartype="full"
    data-sidebar-position="fixed" data-header-position="fixed">
    <div
      class="position-relative overflow-hidden radial-gradient min-vh-100 d-flex align-items-center justify-content-center">
      <div class="d-flex align-items-center justify-content-center w-100">
        <div class="row justify-content-center w-100">
          <div class="col-md-8 col-lg-6 col-xxl-3">
            <div class="card mb-0">
              <div class="card-body">
                <router-link to="/" class="text-nowrap logo-img text-center d-block py-3 w-100">
                  <img src="@/assets/images/logos/dark-logo.svg" width="180" alt="" />
                </router-link>
                <p class="text-center">Votre plateforme de gestion d'achats</p>
                <div v-if="error" class="alert alert-danger" role="alert">
                  {{ error }}
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
                      <input class="form-check-input primary" type="checkbox" value="" id="flexCheckChecked" checked>
                      <label class="form-check-label text-dark" for="flexCheckChecked">
                        Se souvenir de moi
                      </label>
                    </div>
                    <a class="text-primary fw-bold" href="./index.html">Mot de passe oublié ?</a>
                  </div>
                  <button type="submit" class="btn btn-primary w-100 py-8 fs-4 mb-4 rounded-2" :disabled="isLoading">
                    <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                    Se connecter
                  </button>
                  <div class="d-flex align-items-center justify-content-center">
                    <p class="fs-4 mb-0 fw-bold">Nouveau ici ?</p>
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

<script>
import axios from 'axios';

export default {
  name: 'Login',
  data() {
    return {
      email: '@gmail.com',
      password: 'test',
      error: '',
      isLoading: false
    }
  },
  methods: {
    async handleLogin() {
      this.isLoading = true;
      this.error = '';
      try {
        const response = await axios.post('/api/auth/login', {
          email: this.email,
          motDePasse: this.password
        });

        if (response.status === 200) {
          const authData = response.data;
          const user = authData.user;
          
          // Récupérer les permissions pour tous les rôles de l'utilisateur
          let allPermissions = [];
          if (user.roles && user.roles.length > 0) {
            const permPromises = user.roles.map(role => 
              axios.get(`/api/permissions/role/${role.id}`)
            );
            const permResponses = await Promise.all(permPromises);
            allPermissions = permResponses.flatMap(res => res.data);
          }
          
          // Stocker l'utilisateur et ses permissions
          localStorage.setItem('user', JSON.stringify(authData));
          localStorage.setItem('permissions', JSON.stringify(allPermissions));
          
          // Rediriger vers le dashboard ou la première page autorisée
          const isAdmin = user.roles?.some(r => r.id === 1);
          const hasDashboard = allPermissions.some(p => p.path === '/dashboard');

          if (isAdmin || hasDashboard) {
            this.$router.push('/dashboard');
          } else if (allPermissions.length > 0) {
            // Rediriger vers le premier module autorisé
            this.$router.push(allPermissions[0].path);
          } else {
            this.$router.push('/dashboard');
          }
        }
      } catch (err) {
        console.error('Login error:', err);
        this.error = err.response?.data?.message || 'Email ou mot de passe incorrect';
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.radial-gradient {
  background: radial-gradient(#f5f7ff 0%, #eef3fe 100%);
}
</style>
