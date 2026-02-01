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
            Bonjour ! Je suis votre assistant expert en stratégie financière. J'ai analysé vos données budgétaires et je suis prêt à vous aider à optimiser vos ressources.
          </div>
        </div>

        <!-- Suggestions de questions -->
        <div v-if="chatHistory.length === 0 && !isLoading && !analysisResult" class="px-3 mb-3">
          <p class="x-small text-muted mb-2 fw-bold text-uppercase">Questions suggérées :</p>
          <div class="d-flex flex-wrap gap-2">
            <button @click="askQuestion('Quels départements concentrent la majorité du budget et pourquoi ?')" class="btn btn-outline-primary btn-sm rounded-pill x-small">
              <i class="ti ti-chart-bar me-1"></i> Concentration budgétaire ?
            </button>
            <button @click="askQuestion('La répartition budgétaire actuelle est-elle équilibrée ou déséquilibrée ?')" class="btn btn-outline-primary btn-sm rounded-pill x-small">
              <i class="ti ti-scale me-1"></i> Équilibre ?
            </button>
            <button @click="askQuestion('Quels départements semblent sous-financés au regard de leur rôle opérationnel ?')" class="btn btn-outline-primary btn-sm rounded-pill x-small">
              <i class="ti ti-trending-down me-1"></i> Sous-financement ?
            </button>
            <button @click="askQuestion('Quel est le risque organisationnel d’une forte concentration budgétaire sur la Finance ?')" class="btn btn-outline-primary btn-sm rounded-pill x-small">
              <i class="ti ti-alert-triangle me-1"></i> Risques Finance ?
            </button>
          </div>
        </div>

        <!-- Messages de discussion -->
        <div v-for="(msg, idx) in chatHistory" :key="'chat-'+idx" 
             class="chat-message" :class="[msg.role === 'user' ? 'user-message' : 'bot-message', msg.isSection ? 'section-fade-in' : '']"
             :style="msg.isSection ? { animationDelay: (msg.sectionIndex * 0.15) + 's' } : {}">
          
          <div v-if="msg.isSection" class="section-icon">
            <i :class="getSectionIcon(msg.sectionIndex)"></i>
          </div>

          <div class="message-content shadow-sm">
            <div v-if="msg.isSection" class="d-flex justify-content-end mb-1">
              <button @click="copyToClipboard(msg.content, msg.sectionIndex)" class="btn-copy" :title="copyStatus[msg.sectionIndex] ? 'Copié !' : 'Copier'">
                <i :class="copyStatus[msg.sectionIndex] ? 'ti ti-check text-success' : 'ti ti-copy'"></i>
              </button>
            </div>
            <div v-html="renderMarkdown(msg.content)" class="markdown-text"></div>
          </div>
        </div>

        <!-- État de chargement -->
        <div v-if="isLoading || isLoadingChat" class="chat-message bot-message">
          <div class="message-content">
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
            {{ isLoading ? "Analyse budgétaire en cours..." : "L'assistant réfléchit..." }}
          </div>
        </div>

        <div v-if="isCached" class="text-center my-2">
          <span class="badge bg-light-info text-info rounded-pill x-small">
            <i class="ti ti-history me-1"></i> Analyse mise en cache
          </span>
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
    <div class="chart-section p-4 position-relative">
      <div class="card border-0 shadow-sm rounded-4 bg-white h-100">
        <div class="card-body d-flex flex-column align-items-center justify-content-center">
          <div class="d-flex justify-content-between align-items-center w-100 mb-4">
            <h5 class="fw-bold mb-0">Répartition du budget total</h5>
            <button 
              @click="analyzeWithAI" 
              class="btn btn-ai-gradient px-4 py-2"
              :disabled="isLoading || pieSeries.length === 0"
            >
              <i class="ti ti-sparkles me-2"></i>
              Analyser avec IA
            </button>
          </div>
          
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
      this.analysisResult = ''; // Still used for internal state but not template display
      
      // Gestion du cache
      if (this.lastDataHash === currentHash && this.cachedAnalysis) {
        setTimeout(() => {
          this.isCached = true;
          this.isLoading = false;
          this.pushAnalysisToChat(this.cachedAnalysis);
          this.scrollToBottom();
        }, 800);
        return;
      }

      this.isCached = false;

      try {
        console.log("Appel API IA:", `${this.apiUrl}/analyze-chart`, this.budgetData);
        const response = await axios.post(`${this.apiUrl}/analyze-chart`, {
          data: this.budgetData
        });

        if (response.data.status === 'success') {
          const result = response.data.analysis;
          this.cachedAnalysis = result;
          this.lastDataHash = currentHash;
          this.pushAnalysisToChat(result);
        } else {
          this.chatHistory.push({ role: 'assistant', content: "### ⚠️ Erreur\n" + response.data.analysis });
        }
      } catch (err) {
        console.error('Erreur IA Frontend:', err);
        let msg = "Impossible de contacter le service d'analyse IA.";
        if (err.response) msg = err.response.data.analysis || "Erreur serveur (500)";
        else if (err.request) msg = "Le backend ne répond pas. Vérifiez qu'il est lancé sur le port 8080.";
        this.chatHistory.push({ role: 'assistant', content: "### ❌ Erreur\n" + msg });
      } finally {
        this.isLoading = false;
        this.scrollToBottom();
      }
    },
    pushAnalysisToChat(analysisText) {
      if (!analysisText) return;
      
      let sections = [];
      if (!/\d\.\s\*\*/.test(analysisText)) {
        sections = [analysisText];
      } else {
        sections = analysisText.split(/(?=\d\.\s\*\*)/).filter(p => p.trim().length > 0).map(p => p.trim());
      }

      sections.forEach((section, index) => {
        this.chatHistory.push({
          role: 'assistant',
          content: section,
          isSection: true,
          sectionIndex: index
        });
      });
    },
    async askQuestion(question) {
      this.userMessage = question;
      await this.sendMessage();
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
        console.log("Envoi message chat:", messageText);
        const response = await axios.post(`${this.apiUrl}/chat`, {
          message: messageText,
          budgetData: this.budgetData,
          history: this.chatHistory.slice(0, -1).map(m => ({
            role: m.role === 'assistant' ? 'assistant' : 'user',
            content: m.content
          }))
        });

        if (response.data.status === 'success') {
          this.chatHistory.push({ role: 'assistant', content: response.data.analysis });
        } else {
          this.chatHistory.push({ role: 'assistant', content: "### ⚠️ Attention\n" + response.data.analysis });
        }
      } catch (err) {
        console.error('Erreur Chat Frontend:', err);
        let errorMsg = "L'assistant IA ne répond pas. Vérifiez que le serveur backend est bien lancé sur le port 8080.";
        if (err.response && err.response.data && err.response.data.analysis) {
          errorMsg = err.response.data.analysis;
        }
        this.chatHistory.push({ role: 'assistant', content: "### ❌ Erreur\n" + errorMsg });
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
  width: 550px;
  border-right-width: 1px;
}

/* Ajustement pour les petits écrans */
@media (max-width: 1200px) {
  .assistant-sidebar.active {
    width: 450px;
  }
}

@media (max-width: 768px) {
  .assistant-sidebar.active {
    width: 100%;
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
  }
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
  word-break: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
}

.markdown-text {
  word-break: break-word;
  overflow-wrap: break-word;
}

.markdown-text :deep(p) {
  margin-bottom: 0.8rem;
}

.markdown-text :deep(p:last-child) {
  margin-bottom: 0;
}

.markdown-text :deep(ul), .markdown-text :deep(ol) {
  padding-left: 1.2rem;
  margin-bottom: 0.8rem;
}

.markdown-text :deep(li) {
  margin-bottom: 0.3rem;
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
  word-break: break-word;
  overflow-wrap: break-word;
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

/* Bouton Gradient Amélioré */
.btn-ai-gradient {
  background: linear-gradient(135deg, #5d87ff 0%, #7c4dff 50%, #b06ab3 100%);
  background-size: 200% auto;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  font-size: 0.85rem;
  padding: 12px 28px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 4px 15px rgba(93, 135, 255, 0.3);
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-ai-gradient i {
  transition: transform 0.4s ease;
}

.btn-ai-gradient:hover {
  background-position: right center;
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 12px 25px rgba(124, 77, 255, 0.4);
  color: white;
}

.btn-ai-gradient:hover i {
  transform: rotate(15deg) scale(1.2);
}

.btn-ai-gradient:active {
  transform: translateY(-2px) scale(0.98);
}

/* Effet de brillance (Shimmer) */
.btn-ai-gradient::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    to bottom right,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0) 40%,
    rgba(255, 255, 255, 0.4) 50%,
    rgba(255, 255, 255, 0) 60%,
    rgba(255, 255, 255, 0) 100%
  );
  transform: rotate(45deg);
  transition: none;
  animation: shimmer 3s infinite;
  pointer-events: none;
}

@keyframes shimmer {
  0% { transform: translate(-100%, -100%) rotate(45deg); }
  100% { transform: translate(100%, 100%) rotate(45deg); }
}

.btn-ai-gradient:disabled {
  background: #e2e8f0;
  color: #94a3b8;
  box-shadow: none;
  transform: none;
  cursor: not-allowed;
}

.btn-ai-gradient:disabled::after {
  display: none;
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
