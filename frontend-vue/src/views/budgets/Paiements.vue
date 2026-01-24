
<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title fw-semibold mb-4">Paiements Fournisseurs</h5>
          <div class="alert alert-info">
            Cette fonctionnalité (Étape 9 du processus : Finance/DAF) est en cours de développement.
          </div>
          <p>Ici, le service Finance/DAF pourra valider et effectuer les paiements une fois le rapprochement confirmé.</p>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '@/layouts/MainLayout.vue';

export default {
  name: 'Paiements',
  components: {
    MainLayout
  }
}
</script>
