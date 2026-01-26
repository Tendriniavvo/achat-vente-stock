<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Nouvel Inventaire</h5>
            <router-link to="/stock/inventaires" class="btn btn-secondary">
              <i class="ti ti-arrow-left"></i> Retour
            </router-link>
          </div>

          <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ errorMessage }}
            <button type="button" class="btn-close" @click="errorMessage = ''"></button>
          </div>

          <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
            {{ successMessage }}
            <button type="button" class="btn-close" @click="successMessage = ''"></button>
          </div>

          <form @submit.prevent="handleSubmit">
            <div class="row">
              <div class="col-md-4 mb-3">
                <label class="form-label">Référence <span class="text-danger">*</span></label>
                <input class="form-control" v-model="form.reference" required />
              </div>
              <div class="col-md-4 mb-3">
                <label class="form-label">Type</label>
                <select class="form-select" v-model="form.type">
                  <option value="annuel">Annuel</option>
                  <option value="tournant">Tournant</option>
                </select>
              </div>
              <div class="col-md-4 mb-3">
                <label class="form-label">Dépôt <span class="text-danger">*</span></label>
                <select class="form-select" v-model.number="form.depotId" required>
                  <option value="">-- Choisir --</option>
                  <option v-for="d in depots" :key="d.id" :value="d.id">{{ d.nom }}</option>
                </select>
              </div>
            </div>

            <div class="d-flex justify-content-end gap-2 mt-4">
              <router-link to="/stock/inventaires" class="btn btn-secondary">Annuler</router-link>
              <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
                <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                <i v-else class="ti ti-device-floppy me-1"></i>
                {{ isSubmitting ? 'Enregistrement...' : 'Enregistrer' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../../layouts/MainLayout.vue';
import axios from 'axios';

export default {
  name: 'CreateInventaire',
  components: { MainLayout },
  data() {
    return {
      depots: [],
      isSubmitting: false,
      errorMessage: '',
      successMessage: '',
      form: {
        reference: this.defaultReference(),
        type: 'annuel',
        depotId: ''
      }
    };
  },
  mounted() {
    this.loadDepots();
  },
  methods: {
    defaultReference() {
      const d = new Date();
      const pad = n => String(n).padStart(2, '0');
      const ref = `INV-${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}-${pad(d.getHours())}${pad(d.getMinutes())}${pad(d.getSeconds())}`;
      return ref;
    },
    currentUserId() {
      try {
        const auth = JSON.parse(localStorage.getItem('user') || 'null');
        return auth?.user?.id || null;
      } catch {
        return null;
      }
    },
    async loadDepots() {
      try {
        const res = await axios.get('/api/depots');
        this.depots = res.data || [];
      } catch (e) {
        console.error('Erreur chargement depots:', e);
        this.errorMessage = 'Erreur lors du chargement des dépôts.';
      }
    },
    async handleSubmit() {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.successMessage = '';
      try {
        const userId = this.currentUserId();
        const payload = {
          reference: this.form.reference,
          type: this.form.type,
          statut: 'en_cours',
          dateDebut: new Date().toISOString(),
          depot: { id: this.form.depotId },
          utilisateurLancement: userId ? { id: userId } : null
        };
        const res = await axios.post('/api/inventaires', payload);
        this.successMessage = 'Inventaire créé avec succès.';
        setTimeout(() => {
          this.$router.push(`/stock/inventaires/${res.data.id}`);
        }, 800);
      } catch (e) {
        console.error('Erreur création inventaire:', e);
        this.errorMessage = 'Erreur lors de la création de l\'inventaire.';
      } finally {
        this.isSubmitting = false;
      }
    }
  }
};
</script>

<style scoped>
.gap-2 {
  gap: 0.5rem;
}
</style>
