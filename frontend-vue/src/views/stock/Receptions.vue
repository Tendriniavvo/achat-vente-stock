
<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title fw-semibold mb-4">Réception des articles</h5>
          <div class="alert alert-info">
            Cette fonctionnalité (Étape 7 du processus : Magasinier) est en cours de développement.
          </div>
          <p>Ici, le magasinier pourra enregistrer la réception, contrôler les quantités et générer le bon de réception.</p>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '@/layouts/MainLayout.vue';

export default {
  name: 'Receptions',
  components: {
    MainLayout
  }
}
</script>
