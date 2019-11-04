import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../components/Home.vue'
import SignIn from '../components/SignIn.vue'
import SignUp from '../components/SignUp.vue'
import UserProfile from '../components/UserProfile.vue'
import Todo from '../components/Todo.vue'
import Done from '../components/Done.vue'
import store from '../store'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'home',
        component: Home
    },
    {
        path: '/todo',
        name: 'todo',
        component: Todo
    },
    {
        path: '/done',
        name: 'done',
        component: Done
    },
    {
        path: '/signin',
        name: 'signin',
        component: SignIn,
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
