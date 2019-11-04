<template>
  <nav class="navbar" role="navigation" aria-label="main navigation">
    <div class="navbar-brand">
      <a class="navbar-item" href="/">
        <img src="https://bulma.io/images/bulma-logo.png" width="112" height="28">
      </a>

      <a role="button" class="navbar-burger burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample">
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
      </a>
    </div>

    <div id="navbarBasicExample" class="navbar-menu">
      <div class="navbar-start">
        <router-link to="/done" class="navbar-item">Done</router-link>
        <router-link to="/todo" class="navbar-item">Todo</router-link>
      </div>

      <login-buttons></login-buttons>
    </div>
  </nav>
</template>

<script>
    import LoginButtons from './LoginButtons.vue'
    export default {
      name: "NavBar",
      components:{
        LoginButtons
      }
    }
</script>

<style scoped>  

</style>
