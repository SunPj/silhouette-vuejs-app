<template>
    <div>
        <div class="container">
            <nav-bar></nav-bar>
        </div>

        <section class="section">
            <div class="container">
                <router-view/>
            </div>
        </section>
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