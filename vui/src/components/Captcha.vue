<template>
    <vue-recaptcha
        v-if="recaptchaSitekey"
        :sitekey="recaptchaSitekey"
        :loadRecaptchaScript="true"
        @error="error"
        @expired="expired"
        @verify="verify"
        @render="render"
    ></vue-recaptcha>
</template>

<script>
    import VueRecaptcha from 'vue-recaptcha';
    export default {
        name: 'Captcha',
        components: { VueRecaptcha },
        data() {
            return {
                recaptchaSitekey: process.conf.RECAPTCHA_SITEKEY
            }
        },
        methods: {
            error() {
                this.$emit('change', {type: 'error'})
            },
            expired() {
                this.$emit('change', {type: 'expired'})
            },
            verify(response) {
                this.$emit('change', {type: 'verify', response: response})
            },
            render() {
                this.$emit('change', {type: 'rendered'})
            }
        }
    };
</script>
