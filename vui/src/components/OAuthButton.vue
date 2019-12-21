<template>
    <a href="#" @click.stop.prevent="authorize('/authenticate/' + provider)" class="card-header-icon has-text-primary" aria-label="more options" style="padding: 0.75rem 0.2rem">
        <font-awesome-icon :title="title" :icon="['fab', icon]" size="2x"/>
    </a>
</template>

<script>
    import {mapActions} from 'vuex'
    export default {
        name: 'OAuthButton',
        props: ['provider', 'redirectTo', 'icon', 'title'],
        data () {
            return {
                w: null
            }
        },
        methods: {
            ...mapActions('user', ['setUser']),
            authorize: function (url) {
                if (this.w) {
                    this.w.close();
                }

                const w = 500;
                const h = 500;
                const dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : window.screenX;
                const dualScreenTop = window.screenTop != undefined ? window.screenTop : window.screenY;

                const width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
                const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

                const systemZoom = width / window.screen.availWidth;
                const left = (width - w) / 2 / systemZoom + dualScreenLeft;
                const top = (height - h) / 2 / systemZoom + dualScreenTop;
                this.w = window.open(url, this.title + " Authentication", 'scrollbars=yes, width=' + w / systemZoom + ', height=' + h / systemZoom + ', top=' + top + ', left=' + left);
            }
        },
        beforeMount () {
            if (window.socialProviderCallbackTick) {
                window.socialProviderCallbackTick += 1
            } else {
                const vi = this;
                window.socialProviderCallback = function(result) {
                    if (result.success) {
                        vi.setUser(result.userdata)
                        const nextTo = vi.redirectTo ? vi.redirectTo : '/profile'
                        vi.$router.push(nextTo)
                    } else {
                        console.log("Error on social auth")
                        console.log(result.error)
                    }
                };

                window.socialProviderCallbackTick = 1
            }
        },
        beforeDestroy () {
            if (window.socialProviderCallbackTick) {
                if (window.socialProviderCallbackTick === 1) {
                    window.socialProviderCallback = null;
                    window.socialProviderCallbackTick = 0;
                } else {
                    window.socialProviderCallbackTick -= 1;
                }
            }
        }
    }
</script>