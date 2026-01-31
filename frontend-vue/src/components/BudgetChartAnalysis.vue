<template>
  <div class="budget-analysis-wrapper">
    <!-- Section Gauche : Assistant IA (Sidebar) -->
    <div class="assistant-sidebar" :class="{ 'active': showAnalysis }">
      <div class="assistant-header d-flex align-items-center justify-content-between">
        <div class="d-flex align-items-center">
          <div class="avatar-ia me-2">
            <i class="ti ti-robot fs-5"></i>
          </div>
          <div>
            <h6 class="fw-bold mb-0">Assistant d'Analyse</h6>
            <span class="status-online">En ligne</span>
          </div>
        </div>
        <button @click="closeAnalysis" class="btn-close-custom">
          <i class="ti ti-x"></i>
        </button>
      </div>

      <div class="assistant-body custom-scrollbar" ref="chatBody">
        <!-- Message d'accueil -->
        <div class="chat-message bot-message">
          <div class="message-content">
            Bonjour ! Je suis votre assistant intelligent. Cliquez sur le bouton "Analyser avec IA" pour que j'étudie la répartition budgétaire de votre entreprise ou posez-moi vos questions directement ci-dessous.
          </div>
        </div>

        <!-- Messages de discussion -->
        <div v-for="(msg, idx) in chatHistory" :key="'chat-'+idx" 
             class="chat-message" :class="msg.role === 'user' ? 'user-message' : 'bot-message'">
          <div class="message-content shadow-sm">
            <div v-html="renderMarkdown(msg.content)" class="markdown-text"></div>
          </div>
        </div>

        <!-- État de chargement -->
        <div v-if="isLoading" class="chat-message bot-message">
          <div class="message-content">
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
            Analyse en cours... J'étudie vos données budgétaires.
          </div>
        </div>

        <!-- Résultat de l'analyse -->
        <div v-if="analysisResult" class="analysis-results">
          <div v-for="(section, index) in parsedSections" :key="index" 
               class="chat-message bot-message section-fade-in"
               :style="{ animationDelay: (index * 0.3) + 's' }">
            <div class="section-icon">
              <i :class="getSectionIcon(index)"></i>
            </div>
            <div class="message-content shadow-sm">
              <div class="d-flex justify-content-end mb-1">
                <button @click="copyToClipboard(section, index)" class="btn-copy" :title="copyStatus[index] ? 'Copié !' : 'Copier'">
                  <i :class="copyStatus[index] ? 'ti ti-check text-success' : 'ti ti-copy'"></i>
                </button>
              </div>
              <div v-html="renderMarkdown(section)" class="markdown-text"></div>
            </div>
          </div>
          
          <div v-if="isCached" class="text-center my-2">
            <span class="badge bg-light-info text-info rounded-pill x-small">
              <i class="ti ti-history me-1"></i> Analyse mise en cache
            </span>
          </div>
        </div>
      </div>

      <div class="assistant-footer">
        <form @submit.prevent="sendMessage" class="input-group">
          <input 
            v-model="userMessage" 
            type="text" 
            class="form-control border-0 bg-light" 
            placeholder="Posez une question sur le budget..." 
            :disabled="isLoadingChat"
          >
          <button 
            type="submit" 
            class="btn btn-primary btn-icon" 
            :disabled="isLoadingChat || !userMessage.trim()"
          >
            <span v-if="isLoadingChat" class="spinner-border spinner-border-sm"></span>
            <i v-else class="ti ti-send"></i>
          </button>
        </form>
      </div>
    </div>

    <!-- Section Droite : Graphique (Main Content) -->
    <div class="chart-section p-4">
      <div class="card border-0 shadow-sm rounded-4 bg-white h-100">
        <div class="card-body d-flex flex-column align-items-center justify-content-center">
          <h5 class="fw-bold mb-4">Répartition du budget total</h5>
          
          <div v-if="pieSeries.length > 0" class="w-100 d-flex justify-content-center">
            <apexchart 
              type="pie" 
              height="400" 
              width="100%"
              :options="pieOptions" 
              :series="pieSeries"
            ></apexchart>
          </div>
          <div v-else class="text-center py-5">
            <div class="spinner-border text-primary" role="status"></div>
            <p class="text-muted mt-2">Chargement des données...</p>
          </div>

          <div class="mt-auto pt-4">
            <button 
              @click="analyzeWithAI" 
              class="btn btn-ai-gradient px-4 py-2"
              :disabled="isLoading || pieSeries.length === 0"
            >
              <i class="ti ti-sparkles me-2"></i>
              Analyser avec IA
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Overlay pour mobile -->
    <div v-if="showAnalysis" class="sidebar-overlay d-md-none" @click="closeAnalysis"></div>
  </div>
</template>

<script>
import axios from 'axios';
import { marked } from 'marked';

export default {
  name: 'BudgetChartAnalysis',
  props: {
    pieSeries: {
      type: Array,
      required: true
    },
    pieOptions: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      analysisResult: '',
      showAnalysis: false,
      isLoading: false,
      isLoadingChat: false,
      userMessage: '',
      chatHistory: [],
      isCached: false,
      lastDataHash: '',
      cachedAnalysis: '',
      apiUrl: 'http://localhost:8080/api/budget',
      copyStatus: {},
      sections: [
        "Adéquation du format",
        "Répartition détaillée",
        "Interprétation",
        "Hypothèses d'écarts",
        "Recommandations"
      ]
    };
  },
  computed: {
    budgetData() {
      const total = this.pieSeries.reduce((acc, val) => acc + val, 0);
      return {
        totalBudget: total,
        departments: (this.pieOptions.labels || []).map((label, index) => ({
          name: label,
          budget: this.pieSeries[index],
          percentage: total > 0 ? ((this.pieSeries[index] / total) * 100).toFixed(1) : 0
        }))
      };
    },
    parsedSections() {
      if (!this.analysisResult) return [];
      
      // Si l'analyse ne contient pas de sections numérotées, on l'affiche en un seul bloc
      if (!/\d\.\s\*\*/.test(this.analysisResult)) {
        return [this.analysisResult];
      }

      // Découpage par les titres Markdown des sections (1. , 2. etc)
      // On garde le titre dans le contenu pour le moment
      const parts = this.analysisResult.split(/(?=\d\.\s\*\*)/);
      return parts.filter(p => p.trim().length > 0).map(p => {
        // On nettoie juste le début si nécessaire
        return p.trim();
      });
    }
  },
  methods: {
    generateDataHash() {
      return JSON.stringify(this.pieSeries) + JSON.stringify(this.pieOptions.labels);
    },
    async analyzeWithAI() {
      const currentHash = this.generateDataHash();
      
      this.showAnalysis = true;
      this.isLoading = true;
      this.chatHistory = []; // Reset chat when starting new analysis
      
      // Gestion du cache
      if (this.lastDataHash === currentHash && this.cachedAnalysis) {
        setTimeout(() => {
          this.analysisResult = this.cachedAnalysis;
          this.isCached = true;
          this.isLoading = false;
          this.scrollToBottom();
        }, 800);
        return;
      }

      this.analysisResult = '';
      this.isCached = false;

      try {
        console.log("Appel API IA:", `${this.apiUrl}/analyze-chart`, this.budgetData);
        const response = await axios.post(`${this.apiUrl}/analyze-chart`, {
          data: this.budgetData
        });

        if (response.data.status === 'success') {
          this.analysisResult = response.data.analysis;
          this.cachedAnalysis = this.analysisResult;
          this.lastDataHash = currentHash;
        } else {
          this.analysisResult = "### Erreur\n" + response.data.analysis;
        }
      } catch (err) {
        console.error('Erreur IA Frontend:', err);
        let msg = "Impossible de contacter le service d'analyse IA.";
        if (err.response) msg = err.response.data.analysis || "Erreur serveur (500)";
        else if (err.request) msg = "Le backend ne répond pas. Vérifiez qu'il est lancé sur le port 8080.";
        this.analysisResult = "### Erreur\n" + msg;
      } finally {
        this.isLoading = false;
        this.scrollToBottom();
      }
    },
    async sendMessage() {
      if (!this.userMessage.trim() || this.isLoadingChat) return;

      const messageText = this.userMessage.trim();
      this.userMessage = '';
      this.showAnalysis = true;
      
      // Ajouter le message utilisateur à l'historique local
      this.chatHistory.push({ role: 'user', content: messageText });
      this.isLoadingChat = true;
      this.scrollToBottom();

      try {
        const response = await axios.post(`${this.apiUrl}/chat`, {
          message: messageText,
          budgetData: this.budgetData,
          history: this.chatHistory.slice(0, -1) // Envoyer l'historique sans le dernier message
        });

        if (response.data.status === 'success') {
          this.chatHistory.push({ role: 'assistant', content: response.data.analysis });
        } else {
          this.chatHistory.push({ role: 'assistant', content: "### Erreur\n" + response.data.analysis });
        }
      } catch (err) {
        console.error('Erreur Chat Frontend:', err);
        this.chatHistory.push({ role: 'assistant', content: "### Erreur\nImpossible de joindre l'assistant IA." });
      } finally {
        this.isLoadingChat = false;
        this.scrollToBottom();
      }
    },
    closeAnalysis() {
      this.showAnalysis = false;
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.chatBody;
        if (container) container.scrollTop = container.scrollHeight;
      });
    },
    renderMarkdown(text) {
      if (!text) return '';
      return marked.parse(text);
    },
    getSectionIcon(index) {
      // Si on n'a qu'un bloc, on met l'icône de cerveau
      if (this.parsedSections.length === 1) return 'ti ti-brain';
      
      const icons = [
        'ti ti-chart-pie',
        'ti ti-calculator',
        'ti ti-bulb',
        'ti ti-help-circle',
        'ti ti-checkbox'
      ];
      return icons[index] || 'ti ti-message';
    },
    getSectionTitle(index) {
      return this.sections[index] || 'Analyse';
    },
    async copyToClipboard(text, index) {
      try {
        await navigator.clipboard.writeText(text);
        this.copyStatus = { ...this.copyStatus, [index]: true };
        setTimeout(() => {
          this.copyStatus = { ...this.copyStatus, [index]: false };
        }, 2000);
      } catch (err) {
        console.error('Erreur copie:', err);
      }
    }
  }
};
</script>

<style scoped>
.budget-analysis-wrapper {
  display: flex;
  width: 100%;
  height: 600px;
  background: #f8f9fa;
  border-radius: 1rem;
  overflow: hidden;
  position: relative;
}

/* Sidebar Assistant */
.assistant-sidebar {
  width: 0;
  background: white;
  border-right: 0px solid #eee;
  display: flex;
  flex-direction: column;
  transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1), border-width 0.4s ease;
  overflow: hidden;
  z-index: 10;
}

.assistant-sidebar.active {
  width: 400px;
  border-right-width: 1px;
}

/* Section Graphique */
.chart-section {
  flex: 1;
  background: #f4f7fb;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* Header Assistant */
.assistant-header {
  padding: 1.25rem;
  border-bottom: 1px solid #f1f1f1;
  background: white;
}

.avatar-ia {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #5d87ff 0%, #49beff 100%);
  color: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(93, 135, 255, 0.2);
}

.status-online {
  font-size: 0.7rem;
  color: #13deb9;
  display: flex;
  align-items: center;
}

.status-online::before {
  content: '';
  display: inline-block;
  width: 6px;
  height: 6px;
  background: #13deb9;
  border-radius: 50%;
  margin-right: 4px;
}

/* Corps Assistant (Chat) */
.assistant-body {
  flex: 1;
  padding: 1.25rem;
  overflow-y: auto;
  background: #fdfdfd;
}

.chat-message {
  margin-bottom: 1.5rem;
  max-width: 100%;
  display: flex;
}

.bot-message .message-content {
  background: #f1f4f9;
  color: #2a3547;
  padding: 1rem;
  border-radius: 0 1.25rem 1.25rem 1.25rem;
  font-size: 0.9rem;
  position: relative;
}

.user-message {
  justify-content: flex-end;
}

.user-message .message-content {
  background: #5d87ff;
  color: white;
  padding: 0.8rem 1.2rem;
  border-radius: 1.25rem 1.25rem 0 1.25rem;
  font-size: 0.9rem;
  max-width: 85%;
}

.user-message .markdown-text :deep(p) {
  margin-bottom: 0;
}

.analysis-results .bot-message {
  flex-direction: column;
  margin-bottom: 2rem;
}

.section-icon {
  width: 32px;
  height: 32px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5d87ff;
  margin-bottom: -16px;
  margin-left: 10px;
  z-index: 2;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

.analysis-results .message-content {
  background: white;
  border: 1px solid #f1f1f1;
  border-radius: 1rem;
  padding-top: 1.5rem;
}

/* Animations */
.section-fade-in {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.5s forwards;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Bouton Gradient */
.btn-ai-gradient {
  background: linear-gradient(135deg, #5d87ff 0%, #b06ab3 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(93, 135, 255, 0.3);
}

.btn-ai-gradient:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 25px rgba(93, 135, 255, 0.4);
  color: white;
}

/* Footer */
.assistant-footer {
  padding: 1rem;
  border-top: 1px solid #f1f1f1;
}

/* Markdown Styling */
.markdown-text :deep(p) { margin-bottom: 0.5rem; }
.markdown-text :deep(ul) { padding-left: 1.2rem; margin-bottom: 0.5rem; }
.markdown-text :deep(h3) { font-size: 1.1rem; color: #5d87ff; margin-top: 1rem; }

/* Typing Indicator */
.typing-indicator span {
  height: 8px; width: 8px; background: #5d87ff; display: inline-block; border-radius: 50%;
  margin-right: 3px; opacity: 0.4; animation: blink 1s infinite;
}
.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes blink { 0%, 100% { opacity: 0.4; } 50% { opacity: 1; } }

/* Mobile */
@media (max-width: 767.98px) {
  .budget-analysis-wrapper { flex-direction: column; height: auto; }
  .assistant-sidebar {
    position: fixed; top: 0; left: 0; bottom: 0; width: 85%; max-width: 320px;
    transform: translateX(-100%);
  }
  .assistant-sidebar.active { transform: translateX(0); }
  .sidebar-overlay {
    position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 90;
  }
}

.btn-close-custom, .btn-copy {
  background: none; border: none; color: #adb5bd; transition: color 0.2s;
}
.btn-close-custom:hover, .btn-copy:hover { color: #5d87ff; }

.x-small { font-size: 0.7rem; }
</style>
