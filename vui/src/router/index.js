import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../components/Home.vue'
import ErrorPage from '../components/ErrorPage.vue'
import SignIn from '../components/SignIn.vue'
import SignUp from '../components/SignUp.vue'
import ChangePasswordPage from '../components/ChangePasswordPage.vue'
import ForgotPasswordPage from '../components/ForgotPasswordPage.vue'
import ResetPasswordPage from '../components/ResetPasswordPage.vue'
import UserProfile from '../components/UserProfile.vue'
import Todo from '../components/Todo.vue'
import About from '../components/About.vue'
import store from '../store'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'home',
        component: Home
    },
    {
        path: '/error',
        name: 'error',
        props: (route) => ({
            message : route.query.message
        }),
        component: ErrorPage
    },
    {
        path: '/todo',
        name: 'todo',
        component: Todo
    },
    {
        path: '/change-password',
        name: 'change-password',
        component: ChangePasswordPage,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/reset-password',
        name: 'reset-password',
        props: (route) => ({
            token: route.query.token
        }),
        component: ResetPasswordPage
    },
    {
        path: '/forgot-password',
        name: 'forgot-password',
        component: ForgotPasswordPage
    },
    {
        path: '/about',
        name: 'about',
        component: About
    },
    {
        path: '/signin',
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
        path: '/signup',
        name: 'SignUp',
        component: SignUp,
        meta: {
            guest: true
        }
    },
    {
        path: '/profile',
        name: 'profile',
        component: UserProfile,
        meta: {
            requiresAuth: true
        }
    }
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        const isAuthorized = store.getters["user/isAuthorized"];
        if (!isAuthorized) {
            next({
                path: '/signin',
                query: {message: 'authenticationRequired', redirectTo: to.fullPath},
                params: {nextUrl: to.fullPath}
            })
        } else {
            next()
        }
    } else {
        next()
    }
})

export default router
