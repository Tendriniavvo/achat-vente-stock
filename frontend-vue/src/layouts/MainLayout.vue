<template>
  <div class="page-wrapper" id="main-wrapper" data-layout="vertical" data-navbarbg="skin6" data-sidebartype="full"
    data-sidebar-position="fixed" data-header-position="fixed">
    
    <!-- Sidebar Start -->
    <Sidebar :currentUser="currentUser" />
    <!--  Sidebar End -->
    
    <!--  Main wrapper -->
    <div class="body-wrapper">
      <!-- Header Start -->
      <Header :currentUser="currentUser" @logout="logout" />
      <!-- Header End -->
      
      <div style="padding-left: 15px !important; padding-right: 15px !important; padding-top: 80px !important;">
        <!-- Slot pour le contenu de la page -->
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import Sidebar from '@/components/layout/Sidebar.vue';
import Header from '@/components/layout/Header.vue';

const router = useRouter();
const authData = ref(null);

// Accéder à l'objet utilisateur imbriqué dans l'AuthResponse
const currentUser = computed(() => authData.value?.user || null);

onMounted(() => {
  // Récupérer les informations d'authentification depuis localStorage
  const userData = localStorage.getItem('user');
  if (userData) {
    authData.value = JSON.parse(userData);
  } else {
    // Si pas d'utilisateur connecté, rediriger vers login
    router.push('/login');
  }
});

const logout = () => {
  localStorage.removeItem('user');
  router.push('/login');
};
</script>

<style>
.container-fluid.content-wrapper {
  padding-left: 15px !important;
  padding-right: 15px !important;
}
</style>
