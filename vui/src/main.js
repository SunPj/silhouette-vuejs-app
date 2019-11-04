import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import VueResource from 'vue-resource'
import Buefy from 'buefy'
import 'buefy/dist/buefy.css'
import { library } from '@fortawesome/fontawesome-svg-core'
import { fas } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

library.add(fas)
Vue.component('font-awesome-icon', FontAwesomeIcon)

Vue.config.productionTip = false

Vue.use(VueResource);
Vue.use(Buefy, {
    defaultIconPack: 'fa'
});

Vue.http.interceptors.push(function(request) {
    // modify headers
    const csrfToken = store.getters["csrf/getToken"];
    request.headers.set('Csrf-Token', csrfToken);

    const jwt = store.getters["user/getToken"];
    if (jwt) {
        request.headers.set('X-Auth', jwt);
    }

    return function(response){
        if (response.status === 401) {
            this.$store.dispatch('user/setUser', {});
            this.$router.push({ path: '/signin', query: {redirectTo: this.$router.path, message: 'sessionExpired'} })
        }
    };
});

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
