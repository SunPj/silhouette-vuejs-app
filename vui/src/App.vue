<template>
    <router-view/>
</template>

<style lang="scss">
</style>

<script>
    import {mapActions} from 'vuex'

    export default {
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