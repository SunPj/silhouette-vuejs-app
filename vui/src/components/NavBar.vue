<template>
  <nav class="navbar" role="navigation" aria-label="main navigation">
    <div class="navbar-brand">
      <a class="navbar-item" href="/">
        <img src="../assets/logo.png">
      </a>

      <a role="button" class="navbar-burger burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample">
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
      </a>
    </div>

    <div id="navbarBasicExample" class="navbar-menu">
      <div class="navbar-start">
        <router-link to="/todo" class="navbar-item">Todo</router-link>
        <router-link to="/about" class="navbar-item">About</router-link>
        <a target="_blank" class="navbar-item" href="https://github.com/SunPj/silhouette-vuejs-app">GitHub</a>
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
