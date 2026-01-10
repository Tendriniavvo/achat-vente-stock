<template>
  <MainLayout>
    <!--  Row 1 -->
    <div class="row">
      <div class="col-lg-12">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title fw-semibold mb-4">Bienvenue sur le Dashboard</h5>
            <p>Système de Gestion Achat-Vente-Stock</p>
          </div>
        </div>
      </div>
    </div>
    
    <!--  Row 2 -->
    <div class="row">
      <div class="col-lg-3 col-md-6">
        <div class="card">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="me-3">
                <span class="round-40 bg-light-primary rounded-circle d-flex align-items-center justify-content-center">
                  <i class="ti ti-shopping-cart text-primary fs-6"></i>
                </span>
              </div>
              <div>
                <p class="text-dark mb-0 fs-3">Achats</p>
                <h5 class="fw-semibold mb-0">0</h5>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-lg-3 col-md-6">
        <div class="card">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="me-3">
                <span class="round-40 bg-light-success rounded-circle d-flex align-items-center justify-content-center">
                  <i class="ti ti-coin text-success fs-6"></i>
                </span>
              </div>
              <div>
                <p class="text-dark mb-0 fs-3">Ventes</p>
                <h5 class="fw-semibold mb-0">0</h5>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-lg-3 col-md-6">
        <div class="card">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="me-3">
                <span class="round-40 bg-light-warning rounded-circle d-flex align-items-center justify-content-center">
                  <i class="ti ti-package text-warning fs-6"></i>
                </span>
              </div>
              <div>
                <p class="text-dark mb-0 fs-3">Articles</p>
                <h5 class="fw-semibold mb-0">0</h5>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-lg-3 col-md-6">
        <div class="card">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="me-3">
                <span class="round-40 bg-light-danger rounded-circle d-flex align-items-center justify-content-center">
                  <i class="ti ti-users text-danger fs-6"></i>
                </span>
              </div>
              <div>
                <p class="text-dark mb-0 fs-3">Partenaires</p>
                <h5 class="fw-semibold mb-0">0</h5>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import MainLayout from '@/layouts/MainLayout.vue';
</script>

<style scoped>
.round-40 {
  width: 40px;
  height: 40px;
}
</style>
