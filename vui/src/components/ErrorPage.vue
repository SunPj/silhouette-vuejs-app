<template>
    <div class="columns card">
        <div class="column col-lg-offset-3 col-lg-6">
            <div class="notification" :class="notification.type" v-if="notification.text">
                {{notification.text}}
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        props: ['message'],
        data() {
            return {
                notification: {
                    type: '',
                    text: ''
                }
            }
        },
        created() {
            if (this.message === 'socialAuthFailed') {
                this.notification.type = 'is-success';
                this.notification.text = 'System error. Please contact to site admin to fix that.'
            } else if (this.message === 'activationTokenInvalid') {
                this.notification.type = 'is-danger';
                this.notification.text = 'Activation token is either invalid or has expired. Please sign up to create an account'
            }
        }
    }
</script>