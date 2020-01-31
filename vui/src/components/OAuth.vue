<template>
    <b-loading :active="true"></b-loading>
</template>

<script>
    export default {
        name: 'OAuth',
        props: ['provider'],
        created() {
            window.opener.socialProviderCallback(this.provider, this.$router.app.$route.query);
            window.close();
        }
    }
</script>