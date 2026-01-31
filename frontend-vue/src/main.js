import { createApp } from 'vue'
import './assets/scss/styles.scss'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import VueApexCharts from "vue3-apexcharts";
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(router)
app.use(VueApexCharts)
app.mount('#app')
