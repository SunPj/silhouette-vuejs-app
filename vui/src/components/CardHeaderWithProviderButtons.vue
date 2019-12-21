<template>
    <header class="card-header">
        <div class="card-header-title">{{label}}</div>
        <o-auth-button title="Facebook" :redirect-to="redirectTo" provider="facebook" icon="facebook-square"></o-auth-button>
        <o-auth-button title="Google" :redirect-to="redirectTo" provider="google" icon="google-plus-square"></o-auth-button>
        <o-auth-button title="Twitter" :redirect-to="redirectTo" provider="twitter" icon="twitter-square"></o-auth-button>
    </header>
</template>

<script>
    import OAuthButton from "./OAuthButton";
    export default {
        name: 'CardHeaderWithProviderButtons',
        components: {OAuthButton},
        props: ['label', 'redirectTo']
    }
</script>