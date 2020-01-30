<template>
    <header class="card-header">
        <div class="card-header-title">{{label}}</div>
        <o-auth-button title="Facebook" @error="showNotification" :redirect-to="redirectTo" provider="facebook" icon="facebook-square"></o-auth-button>
        <o-auth-button title="Google" @error="showNotification" :redirect-to="redirectTo" provider="google" icon="google-plus-square"></o-auth-button>
        <o-auth-button title="Twitter" @error="showNotification" :redirect-to="redirectTo" provider="twitter" icon="twitter-square"></o-auth-button>
    </header>
</template>

<script>
    import OAuthButton from "./OAuthButton";
    export default {
        name: 'CardHeaderWithProviderButtons',
        components: {OAuthButton},
        props: ['label', 'redirectTo'],
        methods: {
            showNotification: function (error) {
                let message = ''
                if (error.code === 'NoEmail') {
                    message = 'Authentication provider has empty email. Please sign up using email and attach your social account in profile page to be able to use it for further sign in\'s'
                } else if (error.code === 'EmailIsBeingUsed' && error.providers) {
                    let providersTextPart = '';
                    if (error.providers.length === 1) {
                        providersTextPart = `Please sign up using your ${error.providers[0]}`
                    } else {
                        providersTextPart = `Please sign up using one of your ${error.providers.join(" or ")}`
                    }
                    message = `There is an existing account for this email. ${providersTextPart} and attach your social account in profile page to be able to use it for further sign in's`
                } else {
                    message = `Something went wrong`
                }

                this.$buefy.toast.open({
                    duration: 8000,
                    queue: false,
                    message: message,
                    position: 'is-bottom',
                    type: 'is-danger'
                })
            }
        }
    }
</script>