<template>
    <div>
        <h1 class="title is-2 has-text-centered">This is Silhouette VueJs app sample</h1>

        <div class="content">
            <h3 class="is-3">Brief description of functionality:</h3>
            <ul>
                <li>Sign up</li>
                <li>Sign in</li>
                <li>Email activation</li>
                <li>Reset password</li>
                <li>Change password</li>
                <li>SPA</li>
            </ul>

            <h3 class="is-3">Backend technical details:</h3>
            <ul>
                <li><a target="_blank" href="https://www.playframework.com/">PlayFramework (Scala)</a></li>
                <li><a target="_blank" href="https://www.silhouette.rocks/">Silhouette authentication library</a></li>
                <li>PlayFramework + VueJs integration (dev hot reload && prod static assets)</li>
                <li>Sending emails via <a target="_blank" href="https://sendgrid.com/">SendGrid</a></li>
                <li><a target="_blank" href="https://www.postgresql.org/">Postgres DB</a></li>
                <li>Ready to deploy to <a target="_blank" href="https://heroku.com/">Heroku</a></li>
            </ul>

            <h3 class="is-3">Frontend technical details:</h3>
            <ul>
                <li><a target="_blank" href="https://vuejs.org/">VueJs</a></li>
                <li>Vue JWT integration</li>
                <li>Vue page access restriction for routes</li>
                <li>Vue validation using <a target="_blank" href="https://github.com/vuelidate/vuelidate">vuelidate</a></li>
                <li><a target="_blank" href="https://bulma.io/">Bulma</a></li>
                <li><a target="_blank" href="https://buefy.org/">Buefy</a></li>
                <li><a target="_blank" href="https://vuex.vuejs.org/guide/">Vuex</a> for storing user data in local storage</li>
            </ul>
        </div>
    </div>
</template>

<script>
    export default {
    }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>