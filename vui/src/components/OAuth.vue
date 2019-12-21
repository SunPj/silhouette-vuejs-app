<template>
    <b-loading :active="true"></b-loading>
</template>

<script>
    export default {
        name: 'OAuth',
        props: ['provider'],
        created() {
            this.$http.get(`/authenticate/${this.provider}`, {params: this.$router.app.$route.query}).then(response => {
                window.opener.socialProviderCallback({success: true, userdata: response.data})
                window.close();
            }).catch(function (error) {
                window.opener.socialProviderCallback({success: false, error: error})
                window.close();
            });
        }
    }
</script>