<template>
  <div class="navbar-end">
    <div class="navbar-item">
      <div class="buttons" v-if="authorized">
        <router-link to="/profile" class="is-link"><span class="icon"><font-awesome-icon icon="user"/></span></router-link>
        <a class="bd-tw-button button" @click="logout()">Logout</a>
      </div>
      <div class="buttons" v-else>
        <router-link to="/signup" class="button is-primary">SignUp</router-link>
        <router-link to="/signin" class="button is-light">SignIn</router-link>
      </div>
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
