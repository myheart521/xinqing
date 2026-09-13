import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/configuration',
      name: 'configuration',
      component: () => import('@/views/digital-human/configuration-index.vue')
    }
  ],
})

export default router
