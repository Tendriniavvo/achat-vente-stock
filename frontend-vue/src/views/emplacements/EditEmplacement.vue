<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex align-items-center mb-4">
            <button @click="$router.back()" class="btn btn-outline-secondary me-3">
              <i class="ti ti-arrow-left"></i>
            </button>
            <h5 class="card-title fw-semibold mb-0">Modifier l'Emplacement : {{ form.code }}</h5>
          </div>

          <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
          </div>

          <form v-else @submit.prevent="updateEmplacement">
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">Dépôt</label>
                <input type="text" class="form-control" :value="form.depot?.nom" disabled>
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">Emplacement Parent (Optionnel)</label>
                <select v-model="form.parent" class="form-select">
                  <option :value="null">Aucun (Niveau Racine)</option>
                  <option v-for="emp in potentialParents" :key="emp.id" :value="emp" :disabled="emp.id === form.id">
                    {{ emp.code }} ({{ emp.typeEmplacement?.libelle }})
                  </option>
                </select>
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">Type d'emplacement <span class="text-danger">*</span></label>
                <select v-model="form.typeEmplacement" class="form-select" required>
                  <option v-for="type in typesEmplacement" :key="type.id" :value="type">
                    {{ type.libelle }}
                  </option>
                </select>
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">Code de l'emplacement <span class="text-danger">*</span></label>
                <input type="text" v-model="form.code" class="form-control" required>
              </div>

              <div class="col-md-12 mb-3">
                <label class="form-label fw-semibold">Description</label>
                <textarea v-model="form.description" class="form-control" rows="2"></textarea>
              </div>

              <div class="col-md-4 mb-3">
                <label class="form-label fw-semibold">Capacité maximale (m³)</label>
                <input type="number" v-model="form.capacite" class="form-control" step="0.01">
              </div>

              <div class="col-md-8 mb-3">
                <label class="form-label fw-semibold">Types de produits acceptés</label>
                <input type="text" v-model="form.typesProduitsAcceptes" class="form-control">
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">Caractéristiques spécifiques</label>
                <textarea v-model="form.caracteristiques" class="form-control" rows="2"></textarea>
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">Conditions de stockage requises</label>
                <textarea v-model="form.conditionsStockage" class="form-control" rows="2"></textarea>
              </div>
            </div>

            <div class="mt-4 text-end">
              <button type="button" @click="$router.back()" class="btn btn-light me-2">Annuler</button>
              <button type="submit" class="btn btn-primary" :disabled="isSaving">
                <i v-if="isSaving" class="spinner-border spinner-border-sm me-1"></i>
                Enregistrer les modifications
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import MainLayout from '@/layouts/MainLayout.vue';

const route = useRoute();
const router = useRouter();
const id = route.params.id;

const isLoading = ref(true);
const isSaving = ref(false);
const potentialParents = ref([]);
const typesEmplacement = ref([]);

const form = ref({
  id: null,
  depot: null,
  parent: null,
  code: '',
  typeEmplacement: null,
  description: '',
  capacite: null,
  caracteristiques: '',
  conditionsStockage: '',
  typesProduitsAcceptes: ''
});

const fetchTypesEmplacement = async () => {
  try {
    const response = await axios.get('/api/types-emplacement');
    typesEmplacement.value = response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des types:', error);
  }
};

const fetchEmplacement = async () => {
  try {
    const response = await axios.get(`/api/emplacements/${id}`);
    form.value = response.data;
    if (form.value.depot) {
      fetchPotentialParents(form.value.depot.id);
    }
  } catch (error) {
    console.error('Erreur lors de la récupération:', error);
    alert('Emplacement non trouvé.');
    router.back();
  } finally {
    isLoading.value = false;
  }
};

const fetchPotentialParents = async (depotId) => {
  try {
    const response = await axios.get(`/api/emplacements/depot/${depotId}`);
    potentialParents.value = response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des parents:', error);
  }
};

const updateEmplacement = async () => {
  isSaving.value = true;
  try {
    await axios.put(`/api/emplacements/${id}`, form.value);
    router.push(`/emplacements?depotId=${form.value.depot.id}`);
  } catch (error) {
    console.error('Erreur lors de la mise à jour:', error);
    alert('Une erreur est survenue lors de la mise à jour.');
  } finally {
    isSaving.value = false;
  }
};

const compareObjects = (a, b) => {
  if (!a || !b) return a === b;
  return a.id === b.id;
};

onMounted(async () => {
  if (id && id !== 'undefined') {
    await fetchTypesEmplacement();
    await fetchEmplacement();
  } else {
    router.push('/depots');
  }
});
</script>