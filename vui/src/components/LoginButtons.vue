<template>
  <div class="navbar-end">
    <div v-if="authorized">
      <router-link to="/profile" class="nd-user-link"><span class="icon"><font-awesome-icon icon="user"/></span></router-link>
      <a class="button is-primary" @click="logout()">Logout</a>
    </div>
    <div v-else>
      <router-link to="/signup" class="button is-primary">SignUp</router-link>
      <router-link to="/signin" class="button is-light">Login</router-link>
    </div>
  </div>
</template>

<script>
  import { mapActions, mapGetters } from 'vuex'
  export default {
    name: 'LoginButtons',
    methods: {
      ...mapGetters('user', ['isAuthorized']),
      ...mapActions('user', ['setUser']),
      logout: function() {
        this.$http.get('/signOut')
        this.setUser({})
        this.$router.push('/')
      }
    },
    computed: {
      authorized: function() {
        return this.isAuthorized();
      }
    }
  }
</script>

<style>
</style>
