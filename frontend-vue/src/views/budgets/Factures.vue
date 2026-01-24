
<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title fw-semibold mb-4">Rapprochement des Factures</h5>
          <div class="alert alert-info">
            Cette fonctionnalité (Étape 8 du processus : Finance) est en cours de développement.
          </div>
          <p>Ici, le service Finance pourra rapprocher les factures avec les réceptions et les bons de commande (3-way match).</p>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '@/layouts/MainLayout.vue';

export default {
  name: 'Factures',
  components: {
    MainLayout
  }
}
</script>
