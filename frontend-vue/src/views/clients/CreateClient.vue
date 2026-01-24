<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">
              {{ isEdit ? 'Modifier le Client' : 'Ajouter un Nouveau Client' }}
            </h5>
            <button @click="$router.back()" class="btn btn-outline-secondary">
              <i class="ti ti-arrow-left"></i> Retour
            </button>
          </div>

          <div v-if="errorMessage" class="alert alert-danger" role="alert">
            {{ errorMessage }}
          </div>

          <form @submit.prevent="saveClient">
            <div class="row">
              <div class="col-md-12 mb-4">
                <h6 class="fw-bold border-bottom pb-2">Informations Générales</h6>
              </div>
              
              <div class="col-md-6 mb-3">
                <label for="nom" class="form-label">Nom Complet / Raison Sociale <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  id="nom" 
                  class="form-control" 
                  v-model="form.nom" 
                  required
                  placeholder="Ex: Jean Dupont ou Société XYZ"
                >
              </div>

              <div class="col-md-6 mb-3">
                <label for="email" class="form-label">Adresse Email</label>
                <input 
                  type="email" 
                  id="email" 
                  class="form-control" 
                  v-model="form.email" 
                  placeholder="client@exemple.com"
                >
              </div>

              <div class="col-md-6 mb-3">
                <label for="telephone" class="form-label">Téléphone</label>
                <input 
                  type="text" 
                  id="telephone" 
                  class="form-control" 
                  v-model="form.telephone" 
                  placeholder="+261 34 00 000 00"
                >
              </div>

              <div class="col-md-6 mb-3">
                <label for="adresse" class="form-label">Adresse Physique</label>
                <textarea 
                  id="adresse" 
                  class="form-control" 
                  v-model="form.adresse" 
                  rows="2"
                  placeholder="Rue, Quartier, Ville"
                ></textarea>
              </div>

              <div class="col-md-12 mb-4 mt-3">
                <h6 class="fw-bold border-bottom pb-2">Notes et Historique Manuel</h6>
              </div>

              <div class="col-md-12 mb-3">
                <label for="historique" class="form-label">Notes complémentaires / Historique</label>
                <textarea 
                  id="historique" 
                  class="form-control" 
                  v-model="form.historique" 
                  rows="4"
                  placeholder="Informations particulières sur le client, préférences, etc."
                ></textarea>
              </div>

              <div class="col-md-12 mt-4 text-end">
                <button type="button" @click="$router.back()" class="btn btn-light me-2">Annuler</button>
                <button type="submit" class="btn btn-primary" :disabled="isSaving">
                  <span v-if="isSaving" class="spinner-border spinner-border-sm me-1"></span>
                  {{ isEdit ? 'Enregistrer les modifications' : 'Ajouter le client' }}
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '../../layouts/MainLayout.vue';

export default {
  name: 'CreateClient',
  components: {
    MainLayout
  },
  props: {
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isSaving: false,
      errorMessage: '',
      currentUser: null,
      form: {
        id: 0,
        nom: '',
        email: '',
        telephone: '',
        adresse: '',
        historique: '',
        actif: true
      }
    };
  },
  async mounted() {
    const authData = JSON.parse(localStorage.getItem('user'));
    if (authData) {
      this.currentUser = authData.user || authData;
    }

    if (this.isEdit && this.$route.params.id) {
      this.loadClient(this.$route.params.id);
    }
  },
  methods: {
    async loadClient(id) {
      try {
        const response = await fetch(`/api/clients/${id}`);
        if (response.ok) {
          this.form = await response.json();
        }
      } catch (error) {
        this.errorMessage = 'Erreur lors du chargement du client';
      }
    },
    async saveClient() {
      if (!this.currentUser) return;
      
      this.isSaving = true;
      this.errorMessage = '';

      try {
        const response = await fetch(`/api/clients?utilisateurId=${this.currentUser.id}`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.form)
        });

        if (response.ok) {
          this.$router.push('/clients');
        } else {
          const error = await response.text();
          this.errorMessage = 'Erreur: ' + error;
        }
      } catch (error) {
        this.errorMessage = 'Erreur de connexion';
      } finally {
        this.isSaving = false;
      }
    }
  }
};
</script>