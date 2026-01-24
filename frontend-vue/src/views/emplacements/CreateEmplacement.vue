<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex align-items-center mb-4">
            <button @click="$router.back()" class="btn btn-outline-secondary me-3">
              <i class="ti ti-arrow-left"></i>
            </button>
            <h5 class="card-title fw-semibold mb-0">Nouvel Emplacement</h5>
          </div>

          <form @submit.prevent="saveEmplacement">
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">Dépôt</label>
                <input type="text" class="form-control" :value="depot?.nom" disabled>
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">Emplacement Parent (Optionnel)</label>
                <select v-model="form.parent" class="form-select">
                  <option :value="null">Aucun (Niveau Racine)</option>
                  <option v-for="emp in potentialParents" :key="emp.id" :value="emp">
                    {{ emp.code }} ({{ emp.typeEmplacement?.libelle }})
                  </option>
                </select>
                <div class="form-text">Laissez vide si c'est une zone principale.</div>
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
                <label class="form-label fw-semibold">Code de l'emplacement</label>
                <input type="text" v-model="form.code" class="form-control" placeholder="Laisser vide pour auto-génération">
                <div class="form-text">Ex: Z1-A1-R2. Si vide, le système générera un code.</div>
              </div>

              <div class="col-md-12 mb-3">
                <label class="form-label fw-semibold">Description</label>
                <textarea v-model="form.description" class="form-control" rows="2" placeholder="Description de l'emplacement..."></textarea>
              </div>

              <div class="col-md-4 mb-3">
                <label class="form-label fw-semibold">Capacité maximale (m³)</label>
                <input type="number" v-model="form.capacite" class="form-control" step="0.01">
              </div>

              <div class="col-md-8 mb-3">
                <label class="form-label fw-semibold">Types de produits acceptés</label>
                <input type="text" v-model="form.typesProduitsAcceptes" class="form-control" placeholder="Ex: Produits frais, Sec, Dangereux...">
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">Caractéristiques spécifiques</label>
                <textarea v-model="form.caracteristiques" class="form-control" rows="2" placeholder="Ex: Température contrôlée, Étagères renforcées..."></textarea>
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">Conditions de stockage requises</label>
                <textarea v-model="form.conditionsStockage" class="form-control" rows="2" placeholder="Ex: Humidité < 50%, Pas d'exposition directe au soleil..."></textarea>
              </div>
            </div>

            <div class="mt-4 text-end">
              <button type="button" @click="$router.back()" class="btn btn-light me-2">Annuler</button>
              <button type="submit" class="btn btn-primary" :disabled="isSaving">
                <i v-if="isSaving" class="spinner-border spinner-border-sm me-1"></i>
                Enregistrer l'emplacement
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
const depotId = route.query.depotId;

const depot = ref(null);
const potentialParents = ref([]);
const typesEmplacement = ref([]);
const isSaving = ref(false);

const form = ref({
  depot: { id: parseInt(depotId) },
  parent: null,
  code: '',
  typeEmplacement: null,
  description: '',
  capacite: null,
  caracteristiques: '',
  conditionsStockage: '',
  typesProduitsAcceptes: ''
});

const fetchDepot = async () => {
  try {
    const response = await axios.get(`/api/depots/${depotId}`);
    depot.value = response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération du dépôt:', error);
  }
};

const fetchTypesEmplacement = async () => {
  try {
    const response = await axios.get('/api/types-emplacement');
    typesEmplacement.value = response.data;
    if (typesEmplacement.value.length > 0) {
      form.value.typeEmplacement = typesEmplacement.value[0];
    }
  } catch (error) {
    console.error('Erreur lors de la récupération des types:', error);
  }
};

const fetchPotentialParents = async () => {
  try {
    const response = await axios.get(`/api/emplacements/depot/${depotId}`);
    potentialParents.value = response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des parents potentiels:', error);
  }
};

const saveEmplacement = async () => {
  isSaving.value = true;
  try {
    await axios.post('/api/emplacements', form.value);
    router.push(`/emplacements?depotId=${depotId}`);
  } catch (error) {
    console.error('Erreur lors de l\'enregistrement:', error);
    alert('Une erreur est survenue lors de l\'enregistrement de l\'emplacement.');
  } finally {
    isSaving.value = false;
  }
};

onMounted(() => {
  if (depotId && depotId !== 'undefined') {
    fetchDepot();
    fetchTypesEmplacement();
    fetchPotentialParents();
  } else {
    router.push('/depots');
  }
});
</script>