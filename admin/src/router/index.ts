// import { createRouter, createWebHistory } from 'vue-router';
// import LoginView from '@/views/login/index.vue';
// import RegisterView from '@/views/login/register.vue';
// import Overview from '@/components/admin/Overview.vue';
// import ConsultationList from '@/components/admin/ConsultationList.vue';
// import ChatView from '@/views/chat/index.vue';
// import SportsView from '@/views/sports/index.vue';
// import KnowledgeView from '@/views/build/index.vue';
// import aichat from '@/views/admin/ai-chat/index.vue';
// import students from '@/views/admin/students/index.vue';
// import AdminLayout from '@/components/AdminLayout.vue';
// import CompleteProfile from '@/views/admin/authentica.vue';
//
// // 使用动态导入实现懒加载
// const lazyLoad = (view) => () => import(`@/views/${view}.vue`);
// const lazyComponent = (component) => () => import(`@/components/${component}.vue`);
//
// const router = createRouter({
//   history: createWebHistory(import.meta.env.BASE_URL || '/'),
//   routes: [
//     {
//       path: '/',
//       name: 'dashboard',
//       component: () => import('@/views/admin/dashboard/index.vue'),
//     },
//     {
//       path: '/login',
//       name: 'login',
//       component: LoginView,
//     },
//     {
//       path: '/register',
//       name: 'register',
//       component: RegisterView,
//     },
//     {
//       path: '/complete-profile',
//       name: 'complete-profile',
//       component: CompleteProfile,
//       meta: { requiresAuth: true },
//     },
//     {
//       path: '/admin',
//       component: AdminLayout,
//       meta: { requiresAuth: true, requiresProfile: true },
//       children: [
//         {
//           path: '',
//           name: 'admin-overview',
//           component: Overview,
//         },
//         {
//           path: 'consultations',
//           name: 'admin-consultations',
//           component: ConsultationList,
//         },
//         {
//           path: 'chat',
//           name: 'admin-chat',
//           component: ChatView,
//         },
//         {
//           path: 'ai-chat',
//           name: 'ai-chat',
//           component: aichat,
//         },
//         {
//           path: 'students',
//           name: 'students',
//           component: students,
//         },
//         {
//           path: 'sports',
//           name: 'admin-sports',
//           component: SportsView,
//         },
//         {
//           path: 'knowledge',
//           name: 'admin-knowledge',
//           component: KnowledgeView,
//         },
//         {
//           path: 'evaluations',
//           component: () => import('@/views/admin/evaluations/index.vue'),
//           meta: { title: '测评记录查看' },
//         },
//       ],
//     },
//     {
//       path: '/:pathMatch(.*)*',
//       redirect: '/admin',
//     },
//   ],
// });
//
// // 全局路由守卫
// router.beforeEach((to, from, next) => {
//   const user = JSON.parse(localStorage.getItem('user') || 'null'); // 从 localStorage 获取用户数据
//   const isAuthenticated = !!user; // 检查是否登录
//   const isProfileComplete = isAuthenticated && !!user?.schoolName; // 检查个人信息是否完整
//   const isProfileCompleteInStorage = localStorage.getItem('isProfileComplete') === 'true'; // 从 localStorage 获取是否完善信息的状态
//
//   // 如果在路由跳转时 localStorage 还未更新，可以从此值做进一步校验
//   console.log('[路由守卫] 认证状态:', {
//     isAuthenticated,
//     isProfileComplete,
//     isProfileCompleteInStorage,
//     user,
//     targetPath: to.path,
//   });
//
//   // 需要登录的页面
//   if (to.matched.some((record) => record.meta.requiresAuth)) {
//     if (!isAuthenticated) {
//       next({
//         path: '/login',
//         query: { redirect: to.fullPath },
//       });
//     } else if (to.matched.some((record) => record.meta.requiresProfile)) {
//       // 需要完善个人信息的页面
//       if (!isProfileComplete && !isProfileCompleteInStorage) {
//         next({
//           path: '/complete-profile',
//           query: { redirect: to.fullPath },
//         });
//       } else {
//         next(); // 如果个人信息已完整，继续跳转
//       }
//     } else {
//       next(); // 登录了的用户且不需要完整个人信息时继续跳转
//     }
//   } else {
//     next(); // 不需要认证的页面直接跳转
//   }
// });
//
//
// // 重置路由的替代方案
// export function resetRouter() {
//   const newRouter = createRouter({
//     history: createWebHistory(import.meta.env.BASE_URL || '/'),
//     routes: router.getRoutes(),
//   });
//
//   router.getRoutes().forEach((route) => {
//     router.removeRoute(route.name);
//   });
//
//   newRouter.getRoutes().forEach((route) => {
//     router.addRoute(route);
//   });
// }
//
// export default router;

import {createRouter, createWebHistory} from 'vue-router';
import LoginView from '@/views/login/index.vue';
import RegisterView from '@/views/login/register.vue';
import ConsultationList from '@/components/admin/ConsultationList.vue';
import ChatView from '@/views/chat/index.vue';
import SportsView from '@/views/sports/index.vue';
import KnowledgeView from '@/views/build/index.vue';
import aichat from '@/views/admin/ai-chat/index.vue';
import students from '@/views/admin/students/index.vue';
import AdminLayout from '@/components/AdminLayout.vue';
import Warning from '@/views/warning/index.vue'
import Appointment from '@/views/appointment/index.vue'
import WarningDetail from '@/views/warning/components/index.vue'
import digitalHumanIndex from '@/views/digital-human/configuration-index.vue'

// 使用动态导入实现懒加载
const lazyLoad = (view) => () => import(`@/views/${view}.vue`);
const lazyComponent = (component) => () => import(`@/components/${component}.vue`);

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL || '/frontend/'),
    routes: [
        {
            path: '/',
            name: 'dashboard',
            component: () => import('@/views/admin/dashboard/index.vue'),
        },
        {
            path: '/example1',
            name: 'example1',
            component: () => import('@/views/example/vue-file-download-example1.vue'),
        },
        {
            path: '/map-debug',
            name: 'map-debug',
            component: () => import('@/views/example/map-debug.vue'),
        },

        {
            path: '/login',
            name: 'login',
            component: LoginView,
        },
        {
            path: '/register',
            name: 'register',
            component: RegisterView,
        },
        {
            path: '/voice',
            name: 'voice',
            component: () => import('@/views/digital-human/voice.vue'),
        },
        {
            path: '/ai-ppt',
            name: 'ai-ppt',
            component: () => import('@/views/ai-ppt/index.vue'),
            meta: {
              title: 'AI PPT生成器'
            }
        },

        {
            path: '/admin',
            component: AdminLayout,
            meta: {requiresAuth: true},  // 这里只需检查是否登录，不需要检查个人信息
            children: [
                // {
                //     path: '',
                //     name: 'admin-overview',
                //     component: Overview,
                // },
                {
                    path: '',
                    name: 'ai-chat',
                    component: aichat,
                },
                {
                    path: "warning",
                    name: 'warning',
                    component: Warning
                },
                {
                    path: "appointment",
                    name: 'appointment',
                    component: Appointment
                },

                {
                    path: 'consultations',
                    name: 'admin-consultations',
                    component: ConsultationList,
                },
                {
                    path: 'chat',
                    name: 'admin-chat',
                    component: ChatView,
                },
                {
                    path: 'configuration-index',
                    name: 'configuration-index',
                    component: digitalHumanIndex,
                },
                {
                    path: 'digital-human-demo',
                    name: 'digital-human-demo',
                    component: () => import('@/views/example/digital-human-demo.vue'),
                },
                {
                    path: 'students',
                    name: 'students',
                    component: students,
                },
                {
                    path: 'sports',
                    name: 'admin-sports',
                    component: SportsView,
                },
                {
                    path: 'knowledge',
                    name: 'admin-knowledge',
                    component: KnowledgeView,
                },
                {
                    path: 'evaluations',
                    component: () => import('@/views/admin/evaluations/index.vue'),
                    meta: {title: '测评记录查看'},
                },
                {
                    path: '/warning/detail',    // 实际访问路径
                    name: 'WarningIndex', // 路由唯一名称
                    component: WarningDetail
                },
                {
                    path: 'ai-ppt',
                    name: 'admin-ai-ppt',
                    component: () => import('@/views/ai-ppt/index.vue'),
                    meta: {title: 'AI PPT生成器'}
                },
            ],
        },
        {
            path: '/:pathMatch(.*)*',
            redirect: '/admin',
        },
    ],
});

// 全局路由守卫
router.beforeEach((to, from, next) => {
    const user = JSON.parse(localStorage.getItem('user') || 'null'); // 从 localStorage 获取用户数据
    const isAuthenticated = !!user; // 检查是否登录

    // 如果在路由跳转时 localStorage 还未更新，可以从此值做进一步校验
    let accessToken = localStorage.getItem('accessToken');
    console.log('[路由守卫] 认证状态:', {
        accessToken,
        user,
        targetPath: to.path,
    });

    // 需要登录的页面
    if (to.matched.some((record) => record.meta.requiresAuth)) {
        if (!accessToken) {
            next({
                path: '/login',
                query: {redirect: to.fullPath},
            });
        } else {
            next(); // 登录了的用户继续跳转
        }
    } else {
        next(); // 不需要认证的页面直接跳转
    }
});

// 重置路由的替代方案
export function resetRouter() {
    const newRouter = createRouter({
        history: createWebHistory(import.meta.env.BASE_URL || '/'),
        routes: router.getRoutes(),
    });

    router.getRoutes().forEach((route) => {
        router.removeRoute(route.name);
    });

    newRouter.getRoutes().forEach((route) => {
        router.addRoute(route);
    });
}

export default router;

