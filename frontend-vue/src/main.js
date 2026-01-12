import { createApp } from 'vue'
import './assets/scss/styles.scss'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import App from './App.vue'
import router from './router'

createApp(App)
  .use(router)
  .mount('#app')
