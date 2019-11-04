<template>
    <div class="container" id="app">
        <nav-bar></nav-bar>
        <router-view/>
    </div>
</template>

<style lang="scss">
</style>

<script>
    import NavBar from "./components/NavBar";
    import {mapActions} from 'vuex'

    export default {
        components: {NavBar},
        methods: {
            ...mapActions('csrf', ['setToken']),
        },
        created: function () {
            const bodyHtmlTag = document.getElementsByTagName("body")[0];
            const csrfToken = bodyHtmlTag.getAttribute('csrf-token-value');
            this.setToken(csrfToken);
        },
    }
</script>