<template>
  <MainLayout>
    <div class="container-fluid">
      <div class="card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="card-title fw-semibold mb-0">Réception des articles</h5>
          </div>

          <!-- Tabs -->
          <ul class="nav nav-tabs mb-4" id="receptionTabs" role="tablist">
            <li class="nav-item" role="presentation">
              <button class="nav-link active" id="to-receive-tab" data-bs-toggle="tab" data-bs-target="#to-receive" type="button" role="tab">
                Commandes à réceptionner
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" id="receptions-tab" data-bs-toggle="tab" data-bs-target="#receptions" type="button" role="tab">
                Bons de Réception (BR)
              </button>
            </li>
          </ul>

          <div class="tab-content" id="receptionTabsContent">
            <!-- Tab: Commandes à réceptionner -->
            <div class="tab-pane fade show active" id="to-receive" role="tabpanel">
              <div v-if="isLoadingBC" class="text-center py-4">
                <div class="spinner-border text-primary" role="status"></div>
              </div>
              <div v-else class="table-responsive">
                <table class="table table-hover align-middle">
                  <thead>
                    <tr>
                      <th>Référence BC</th>
                      <th>Date</th>
                      <th>Fournisseur</th>
                      <th>Statut</th>
                      <th class="text-center">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="bc in bonsAwaitingReception" :key="bc.id">
                      <td class="fw-bold">{{ bc.reference }}</td>
                      <td>{{ formatDate(bc.dateCommande) }}</td>
                      <td>{{ bc.fournisseur ? bc.fournisseur.nom : '-' }}</td>
                      <td>
                        <span :class="getStatutBCClass(bc.statut)">{{ formatStatut(bc.statut) }}</span>
                      </td>
                      <td class="text-center">
                        <button class="btn btn-sm btn-primary" @click="ouvrirReception(bc.id)">
                          <i class="ti ti-package-import me-1"></i> Réceptionner
                        </button>
                      </td>
                    </tr>
                    <tr v-if="bonsAwaitingReception.length === 0">
                      <td colspan="5" class="text-center py-4 text-muted">Aucune commande en attente de réception</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- Tab: Bons de Réception effectués -->
            <div class="tab-pane fade" id="receptions" role="tabpanel">
              <div v-if="isLoadingBR" class="text-center py-4">
                <div class="spinner-border text-primary" role="status"></div>
              </div>
              <div v-else class="table-responsive">
                <table class="table table-hover align-middle">
                  <thead>
                    <tr>
                      <th>Référence BR</th>
                      <th>Date</th>
                      <th>BC Source</th>
                      <th>Dépôt</th>
                      <th>Magasinier</th>
                      <th>Statut</th>
                      <th class="text-center">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="br in receptions" :key="br.id">
                      <td class="fw-bold text-primary">{{ br.reference }}</td>
                      <td>{{ formatDate(br.dateReception) }}</td>
                      <td>{{ br.bonCommande ? br.bonCommande.reference : '-' }}</td>
                      <td>{{ br.depot ? br.depot.nom : '-' }}</td>
                      <td>{{ br.utilisateur ? br.utilisateur.nom : '-' }}</td>
                      <td>
                        <span :class="getStatutBRClass(br.statut)">{{ formatStatut(br.statut) }}</span>
                      </td>
                      <td class="text-center">
                        <button class="btn btn-sm btn-outline-info" @click="voirDetailsBR(br.id)">
                          <i class="ti ti-eye"></i>
                        </button>
                      </td>
                    </tr>
                    <tr v-if="receptions.length === 0">
                      <td colspan="7" class="text-center py-4 text-muted">Aucun bon de réception trouvé</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Détails BR -->
    <div class="modal fade" id="modalDetailsBR" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content" v-if="selectedBR">
          <div class="modal-header">
            <h5 class="modal-title">Détails du Bon de Réception : {{ selectedBR.reference }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="row mb-4">
              <div class="col-md-6">
                <p><strong>BC Source:</strong> {{ selectedBR.bonCommande?.reference }}</p>
                <p><strong>Date Réception:</strong> {{ formatDate(selectedBR.dateReception) }}</p>
              </div>
              <div class="col-md-6 text-md-end">
                <p><strong>Dépôt:</strong> {{ selectedBR.depot?.nom }}</p>
                <p><strong>Magasinier:</strong> {{ selectedBR.utilisateur?.nom }}</p>
              </div>
            </div>
            
            <table class="table table-sm">
              <thead class="table-light">
                <tr>
                  <th>Article</th>
                  <th class="text-center">Qté Reçue</th>
                  <th>Lot</th>
                  <th>Expiration</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="ligne in selectedBRLignes" :key="ligne.id">
                  <td>{{ ligne.article?.nom }}</td>
                  <td class="text-center fw-bold">{{ ligne.quantiteRecue }}</td>
                  <td>{{ ligne.lot?.numeroLot || '-' }}</td>
                  <td>{{ ligne.lot?.dateExpiration ? formatDate(ligne.lot.dateExpiration) : '-' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script>
import MainLayout from '@/layouts/MainLayout.vue';
import * as bootstrap from 'bootstrap';

export default {
  name: 'Receptions',
  components: {
    MainLayout
  },
  data() {
    return {
      bonsAwaitingReception: [],
      receptions: [],
      isLoadingBC: false,
      isLoadingBR: false,
      selectedBR: null,
      selectedBRLignes: [],
      modalBR: null
    };
  },
  mounted() {
    this.loadBonsAwaitingReception();
    this.loadReceptions();
    this.modalBR = new bootstrap.Modal(document.getElementById('modalDetailsBR'));
  },
  methods: {
    async loadBonsAwaitingReception() {
      this.isLoadingBC = true;
      try {
        const response = await fetch('/api/bons-commande-fournisseur');
        if (response.ok) {
          const allBC = await response.json();
          // Filtrer les BC envoyés ou en réception partielle
          this.bonsAwaitingReception = allBC.filter(bc => 
            ['envoye', 'reception_partielle'].includes(bc.statut.toLowerCase())
          );
        }
      } catch (error) {
        console.error('Erreur chargement BC:', error);
      } finally {
        this.isLoadingBC = false;
      }
    },
    async loadReceptions() {
      this.isLoadingBR = true;
      try {
        const response = await fetch('/api/receptions');
        if (response.ok) {
          this.receptions = await response.json();
        }
      } catch (error) {
        console.error('Erreur chargement BR:', error);
      } finally {
        this.isLoadingBR = false;
      }
    },
    ouvrirReception(bcId) {
      this.$router.push(`/receptions/enregistrer/${bcId}`);
    },
    async voirDetailsBR(brId) {
      try {
        const resBR = await fetch(`/api/receptions/${brId}`);
        const resLignes = await fetch(`/api/receptions/${brId}/lignes`);
        
        if (resBR.ok && resLignes.ok) {
          this.selectedBR = await resBR.json();
          this.selectedBRLignes = await resLignes.json();
          this.modalBR.show();
        }
      } catch (error) {
        console.error('Erreur chargement détails BR:', error);
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '-';
      return new Date(dateStr).toLocaleString('fr-FR');
    },
    formatStatut(statut) {
      if (!statut) return '-';
      const map = {
        'envoye': 'Envoyé',
        'receptionne': 'Réceptionné',
        'reception_partielle': 'Réception Partielle',
        'complete': 'Complet',
        'partielle': 'Partiel'
      };
      return map[statut.toLowerCase()] || statut;
    },
    getStatutBCClass(statut) {
      const s = statut.toLowerCase();
      if (s === 'envoye') return 'badge bg-primary';
      if (s === 'reception_partielle') return 'badge bg-warning';
      return 'badge bg-secondary';
    },
    getStatutBRClass(statut) {
      const s = statut.toLowerCase();
      if (s === 'complete') return 'badge bg-success';
      if (s === 'partielle') return 'badge bg-warning';
      return 'badge bg-secondary';
    }
  }
}
</script>