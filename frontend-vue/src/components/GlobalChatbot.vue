<template>
  <div class="chatbot-container" :class="{ 'open': isOpen }">
    <!-- Bouton flottant -->
    <button @click="toggleChat" class="chat-toggle-btn shadow-lg" :class="{ 'pulse': !isOpen && !hasInteracted }">
      <i v-if="!isOpen" class="ti ti-message-chatbot fs-2"></i>
      <i v-else class="ti ti-x fs-3"></i>
    </button>

    <!-- Fenêtre de Chat -->
    <div v-if="isOpen" class="chat-window shadow-xl border-0 overflow-hidden d-flex flex-column">
      <div class="chat-header p-3 d-flex align-items-center justify-content-between text-white">
        <div class="d-flex align-items-center">
          <div class="bg-white rounded-circle p-2 me-2 shadow-sm d-flex align-items-center justify-content-center" style="width: 40px; height: 40px;">
            <i class="ti ti-robot text-primary fs-5"></i>
          </div>
          <div>
            <h6 class="mb-0 fw-bold">Assistant IA</h6>
            <div class="d-flex align-items-center">
              <span class="status-dot me-1"></span>
              <small class="text-white-50">En ligne</small>
            </div>
          </div>
        </div>
        <button @click="clearChat" class="btn btn-link text-white p-0 opacity-75 hover-opacity-100" title="Effacer la conversation">
          <i class="ti ti-trash fs-4"></i>
        </button>
      </div>

      <div class="chat-messages p-3 flex-grow-1 overflow-auto bg-light-subtle" ref="messageContainer">
        <div v-for="(msg, index) in messages" :key="index" class="mb-4 animate-fade-in">
          <div :class="['d-flex', msg.role === 'user' ? 'justify-content-end' : 'justify-content-start align-items-end']">
            <div v-if="msg.role === 'assistant'" class="avatar-sm me-2 mb-1">
              <div class="bg-primary-subtle text-primary rounded-circle d-flex align-items-center justify-content-center" style="width: 28px; height: 28px;">
                <i class="ti ti-robot fs-2"></i>
              </div>
            </div>
            <div :class="['message-bubble p-3', msg.role === 'user' ? 'user-bubble' : 'assistant-bubble shadow-sm']">
              <div class="markdown-content" v-html="renderMarkdown(msg.content)"></div>
            </div>
          </div>
        </div>
        <div v-if="isLoading" class="d-flex justify-content-start mb-3 animate-pulse">
          <div class="message-bubble assistant-bubble p-3 shadow-sm">
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input p-3 border-top bg-white">
        <div class="input-group">
          <input 
            v-model="userInput" 
            @keyup.enter="sendMessage"
            type="text" 
            class="form-control border-0 bg-light rounded-start-pill px-3" 
            placeholder="Posez votre question..."
            :disabled="isLoading"
          >
          <button @click="sendMessage" class="btn btn-primary rounded-end-pill px-4" :disabled="isLoading || !userInput.trim()">
            <i class="ti ti-send"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue';
import axios from 'axios';
import { marked } from 'marked';

const isOpen = ref(false);
const isLoading = ref(false);
const userInput = ref('');
const hasInteracted = ref(false);
const messageContainer = ref(null);
const messages = ref([
  { role: 'assistant', content: 'Bonjour ! Je suis votre assistant IA. Je peux vous renseigner sur les stocks, les budgets ou les performances de votre entreprise. Que souhaitez-vous savoir ?' }
]);

const clearChat = () => {
  messages.value = [
    { role: 'assistant', content: 'Conversation effacée. Comment puis-je vous aider à nouveau ?' }
  ];
};

const toggleChat = () => {
  isOpen.value = !isOpen.value;
  hasInteracted.value = true;
  if (isOpen.value) {
    scrollToBottom();
  }
};

const renderMarkdown = (text) => {
  return marked(text);
};

const scrollToBottom = async () => {
  await nextTick();
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
  }
};

const sendMessage = async () => {
  if (!userInput.value.trim() || isLoading.value) return;

  const userMsg = userInput.value;
  messages.value.push({ role: 'user', content: userMsg });
  userInput.value = '';
  isLoading.value = true;
  scrollToBottom();

  try {
    const history = messages.value.slice(1, -1).map(m => ({ role: m.role, content: m.content }));
    const response = await axios.post('http://localhost:8080/api/ai/chat', {
      message: userMsg,
      history: history
    });

    messages.value.push({ role: 'assistant', content: response.data.response });
  } catch (error) {
    console.error('Chat error:', error);
    messages.value.push({ role: 'assistant', content: "Désolé, je rencontre une erreur de connexion. Veuillez réessayer plus tard." });
  } finally {
    isLoading.value = false;
    scrollToBottom();
  }
};
</script>

<style scoped>
.chatbot-container {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 1000;
  font-family: 'Plus Jakarta Sans', sans-serif;
}

.chat-toggle-btn {
  width: 60px;
  height: 60px;
  border-radius: 30px;
  background: linear-gradient(135deg, #5d87ff 0%, #7c4dff 100%);
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.chat-toggle-btn:hover {
  transform: scale(1.1) rotate(5deg);
}

.chat-toggle-btn.pulse {
  animation: pulse-animation 2s infinite;
}

@keyframes pulse-animation {
  0% { box-shadow: 0 0 0 0 rgba(93, 135, 255, 0.7); }
  70% { box-shadow: 0 0 0 15px rgba(93, 135, 255, 0); }
  100% { box-shadow: 0 0 0 0 rgba(93, 135, 255, 0); }
}

.chat-window {
  position: absolute;
  bottom: 80px;
  right: 0;
  width: 380px;
  height: 550px;
  background: white;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  animation: slide-in 0.4s ease-out;
}

@keyframes slide-in {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.chat-header {
  background: linear-gradient(135deg, #5d87ff 0%, #7c4dff 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.status-dot {
  width: 8px;
  height: 8px;
  background-color: #26e7a6;
  border-radius: 50%;
  display: inline-block;
  box-shadow: 0 0 0 2px rgba(38, 231, 166, 0.2);
}

.message-bubble {
  max-width: 85%;
  font-size: 0.92rem;
  line-height: 1.6;
  position: relative;
  transition: all 0.2s ease;
}

.user-bubble {
  background: linear-gradient(135deg, #5d87ff 0%, #4c76f2 100%);
  color: white;
  border-radius: 20px 20px 4px 20px;
  box-shadow: 0 4px 12px rgba(93, 135, 255, 0.2);
}

.assistant-bubble {
  background: white;
  color: #2a3547;
  border-radius: 20px 20px 20px 4px;
  border: 1px solid #e5eaef;
}

.markdown-content :deep(p) {
  margin-bottom: 0.75rem;
}

.markdown-content :deep(p:last-child) {
  margin-bottom: 0;
}

.markdown-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
  font-size: 0.85rem;
  background: #f8fafd;
  border-radius: 8px;
  overflow: hidden;
}

.markdown-content :deep(th), .markdown-content :deep(td) {
  padding: 8px 12px;
  border: 1px solid #e5eaef;
}

.markdown-content :deep(th) {
  background: #f0f5ff;
  font-weight: 600;
  color: #5d87ff;
}

.animate-fade-in {
  animation: fadeIn 0.3s ease-out forwards;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.hover-opacity-100:hover {
  opacity: 1 !important;
}

.typing-indicator span {
  height: 8px;
  width: 8px;
  float: left;
  margin: 0 1px;
  background-color: #9E9E9E;
  display: block;
  border-radius: 50%;
  opacity: 0.4;
}

.typing-indicator span:nth-of-type(1) { animation: 1s blink infinite 0.3333s; }
.typing-indicator span:nth-of-type(2) { animation: 1s blink infinite 0.6666s; }
.typing-indicator span:nth-of-type(3) { animation: 1s blink infinite 0.9999s; }

@keyframes blink {
  50% { opacity: 1; }
}

/* Custom Scrollbar */
.chat-messages::-webkit-scrollbar {
  width: 5px;
}
.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
}
.chat-messages::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 10px;
}
</style>
