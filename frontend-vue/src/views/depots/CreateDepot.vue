<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex align-items-center mb-4">
            <button @click="$router.back()" class="btn btn-outline-secondary me-3">
              <i class="ti ti-arrow-left"></i>
            </button>
            <h5 class="card-title fw-semibold mb-0">Nouveau Dépôt</h5>
          </div>

          <form @submit.prevent="handleSubmit">
            <div class="row">
              <!-- Informations essentielles -->
              <div class="col-md-6 mb-3">
                <label class="form-label fw-bold">Nom du Dépôt <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="form.nom" 
                  required 
                  placeholder="Ex: Entrepôt Central"
                >
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label fw-bold">Code Identification <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="form.code" 
                  required 
                  placeholder="Ex: DEP-001"
                >
              </div>

              <!-- Responsable et Adresse -->
              <div class="col-md-6 mb-3">
                <label class="form-label fw-bold">Responsable du site</label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="form.responsable" 
                  placeholder="Nom du responsable"
                >
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label fw-bold">Statut</label>
                <select class="form-select" v-model="form.actif">
                  <option :value="true">Actif</option>
                  <option :value="false">Inactif</option>
                </select>
              </div>

              <div class="col-12 mb-3">
                <label class="form-label fw-bold">Adresse Physique Complète</label>
                <textarea 
                  class="form-control" 
                  v-model="form.adresse" 
                  rows="2" 
                  placeholder="Adresse complète du dépôt"
                ></textarea>
              </div>

              <!-- Informations complémentaires -->
              <div class="col-md-4 mb-3">
                <label class="form-label fw-bold">Capacité (m³)</label>
                <input 
                  type="number" 
                  class="form-control" 
                  v-model="form.capacite" 
                  placeholder="Capacité en mètres cubes"
                >
              </div>
              <div class="col-md-4 mb-3">
                <label class="form-label fw-bold">Type d'entreposage</label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="form.typeEntreposage" 
                  placeholder="Ex: Frigorifique, Sec, etc."
                >
              </div>
              <div class="col-md-4 mb-3">
                <label class="form-label fw-bold">Horaires d'ouverture</label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="form.horairesOuverture" 
                  placeholder="Ex: Lun-Ven 08:00-18:00"
                >
              </div>
            </div>

            <div v-if="error" class="alert alert-danger mb-3">
              {{ error }}
            </div>

            <div class="d-flex justify-content-end gap-2 mt-4">
              <button type="button" @click="$router.back()" class="btn btn-outline-secondary">Annuler</button>
              <button type="submit" class="btn btn-primary" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-1"></span>
                Créer le Dépôt
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import MainLayout from '../../layouts/MainLayout.vue';
import axios from 'axios';

const router = useRouter();
const isLoading = ref(false);
const error = ref('');

const form = ref({
  nom: '',
  code: '',
  adresse: '',
  responsable: '',
  capacite: null,
  typeEntreposage: '',
  horairesOuverture: '',
  actif: true
});

const handleSubmit = async () => {
  isLoading.value = true;
  error.value = '';
  
  try {
    await axios.post('/api/depots', form.value);
    router.push('/depots');
  } catch (err) {
    error.value = err.response?.data?.message || "Une erreur est survenue lors de la création du dépôt.";
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};
</script>