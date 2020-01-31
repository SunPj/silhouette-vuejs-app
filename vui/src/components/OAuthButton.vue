<template>
    <a href="#" @click.stop.prevent="authorize('/authenticate/' + provider)" class="card-header-icon has-text-primary" aria-label="more options" style="padding: 0.75rem 0.2rem">
        <font-awesome-icon :title="title" :icon="['fab', icon]" size="2x"/>
    </a>
</template>

<script>
    import {mapActions} from 'vuex'
    import {openAuthenticationWindow} from './services/oauth.js'
    export default {
        name: 'OAuthButton',
        props: ['provider', 'redirectTo', 'icon', 'title'],
        methods: {
            ...mapActions('user', ['setUser']),
            authorize: function () {
                const vi = this;
                const url = `/authenticate/${this.provider}`;
                openAuthenticationWindow(url, url, function(result) {
                    vi.setUser(result)
                    const nextTo = vi.redirectTo ? vi.redirectTo : '/profile'
                    vi.$router.push(nextTo)
                });
            }
        }
    }
</script>