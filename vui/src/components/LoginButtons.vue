<template>
  <div class="navbar-end">
    <div class="navbar-item">
      <div class="field is-grouped" v-if="authorized">
        <p class="control">
          <a class="bd-tw-button button" @click="logout()">Logout</a>
        </p>
        <p class="control" >
          <router-link to="/profile"><span class="icon"><font-awesome-icon icon="user"/></span></router-link>
        </p>
      </div>
      <div class="field is-grouped" v-else>
        <p class="control">
          <router-link to="/signup" class="button is-primary">SignUp</router-link>
        </p>
        <p class="control" >
          <router-link to="/signin" class="button is-light">SignIn</router-link>
        </p>
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
