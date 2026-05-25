import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/tides-ai',
      name: 'TidesAI',
      component: () => import('../views/TidesAi.vue')
    },
    {
      path: '/tides-rag',
      name: 'SmartRag',
      component: () => import('../views/SmartRag.vue')
    },
    {
      path: '/tides-analysis',
      name: 'TidesAnalysis',
      component: () => import('../views/TidesAnalysis.vue')
    },
    {
      path: '/ai-observability',
      name: 'AiObservability',
      component: () => import('../views/AiObservability.vue')
    }
  ],
})

export default router
