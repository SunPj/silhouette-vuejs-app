import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../components/Home.vue'
import ErrorPage from '../components/ErrorPage.vue'
import SignIn from '../components/SignIn.vue'
import OAuth from '../components/OAuth'
import SignUp from '../components/SignUp.vue'
import ChangePasswordPage from '../components/ChangePasswordPage.vue'
import ForgotPasswordPage from '../components/ForgotPasswordPage.vue'
import ResetPasswordPage from '../components/ResetPasswordPage.vue'
import UserProfile from '../components/UserProfile.vue'
import AdminLayout from '../components/AdminLayout.vue'
import UserManager from '../components/UserManager.vue'
import OtherAdminPage from '../components/OtherAdminPage.vue'
import Todo from '../components/Todo.vue'
import Changelog from '../components/Changelog.vue'
import About from '../components/About.vue'
import SiteLayout from '../components/SiteLayout.vue'
import store from '../store'

Vue.use(VueRouter)

const routes = [
    {
        path: '/oauth/:provider',
        name: 'oauth',
        component: OAuth,
        props: (route) => ({
            provider: route.params.provider
        }),
    },
    {
        path: '/admin',
        name: 'admin',
        component: AdminLayout,
        meta: {
            requiresAuth: true,
            userRole: 'Admin'
        },
        children: [
            {
                path: 'user',
                component: UserManager
            },
            {
                path: 'other',
                component: OtherAdminPage
            },
        ]
    },
    {
        path: '/',
        name: 'SiteLayout',
        component: SiteLayout,
        children: [
            {
                path: '',
                name: 'home',
                component: Home
            },
            {
                path: 'error',
                name: 'error',
                props: (route) => ({
                    message : route.query.message
                }),
                component: ErrorPage
            },
            {
                path: 'todo',
                name: 'todo',
                component: Todo
            },
            {
                path: 'changelog',
                name: 'changelog',
                component: Changelog
            },
            {
                path: 'change-password',
                name: 'change-password',
                component: ChangePasswordPage,
                meta: {
                    requiresAuth: true
                }
            },
            {
                path: 'reset-password',
                name: 'reset-password',
                props: (route) => ({
                    token: route.query.token
                }),
                component: ResetPasswordPage
            },
            {
                path: 'forgot-password',
                name: 'forgot-password',
                component: ForgotPasswordPage
            },
            {
                path: 'about',
                name: 'about',
                component: About
            },
            {
                path: 'signin',
                name: 'signin',
                component: SignIn,
                props: (route) => ({
                    redirectTo: route.query.redirectTo,
                    message : route.query.message
                }),
                meta: {
                    guest: true
                }
            },
            {
                path: 'signup',
                name: 'SignUp',
                component: SignUp,
                meta: {
                    guest: true
                }
            },
            {
                path: 'profile',
                name: 'profile',
                component: UserProfile,
                meta: {
                    requiresAuth: true
                }
            },
        ]
    }
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

router.beforeEach((to, from, next) => {
    const isAuthorized = store.getters["user/isAuthorized"]
    const userRole = store.getters["user/getRole"]
    const brokenRule = to.matched.some(record => (record.meta.requiresAuth && !isAuthorized) ||
        (record.meta.userRole && userRole !== record.meta.userRole))

    if (brokenRule) {
        next({
            path: '/signin',
            query: {message: 'authenticationRequired', redirectTo: to.fullPath},
            params: {nextUrl: to.fullPath}
        })
    } else {
        next()
    }
})

export default router
