import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import csrf from './modules/csrf'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {},
    mutations: {},
    actions: {},
    modules: {
        user, csrf
    },
    plugins: [
        createPersistedState({
            paths: ['user'],
        }),
    ],
})
