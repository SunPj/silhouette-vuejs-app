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
            }).catch(function (response) {
                if (response.status === 409) {
                    window.opener.socialProviderCallback({success: false, error: {code: "EmailIsBeingUsed", providers: response.data.providers}})
                } else if (response.status === 400) {
                    window.opener.socialProviderCallback({success: false, error: {code: "NoEmail"}})
                } else {
                    window.opener.socialProviderCallback({success: false, error: response})
                }
                window.close();
            });
        }
    }
</script>