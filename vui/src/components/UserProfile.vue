<template>
    <div class="card">

        <header class="card-header">
            <div class="card-header-title">Profile page</div>
        </header>

        <div class="card-content">
            <div class="content">
                <div class="columns">
                    <div class="column">
                        <p>
                            <strong>First name:</strong> {{user.firstName}}
                        </p>
                        <p>
                            <strong>Last name:</strong> {{user.lastName}}
                        </p>
                        <p>
                            <strong>Email:</strong> {{user.email}}
                        </p>
                        <p>Here you can <router-link to="/change-password">change your password</router-link></p>
                        <p v-if="adminUser">You are an admin user. Admin page is <router-link to="/admin/user">here</router-link></p>
                    </div>
                    <div class="column is-4">
                        <p class="has-text-centered">Link your social accounts</p>

                        <p v-for="(v, k) in linkedAccounts" :key="k">
                            <button class="button is-medium is-fullwidth" @click="linkAccount(k)" :class="{'is-success': linkedAccounts[k]}" :disabled="v">
                                <span class="icon is-pulled-right" v-if="v">
                                    <font-awesome-icon title="Linked" icon="check-circle"/>
                                </span>
                                <span class="icon">
                                    <font-awesome-icon title="Google" :icon="['fab', k]"/>
                                </span>
                                <span class="is-capitalized">{{k}}</span>
                            </button>
                        </p>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>
<script>
    import { mapGetters } from 'vuex'
    import {openAuthenticationWindow} from './services/oauth.js'
    export default {
        name: 'UserProfile',
        data() {
            return {
                socialAccounts: [],
            }
        },
        methods: {
            ...mapGetters('user', ['getUser', 'isAdmin']),
            linkAccount (providerId) {
                const vi = this;
                const authenticateInitUrl = `/authenticate/${providerId}`;
                const linkAccountBackendUrl = `/link-account/${providerId}`;
                openAuthenticationWindow(authenticateInitUrl, linkAccountBackendUrl, function() {
                    vi.socialAccounts.push(providerId)

                    vi.$buefy.toast.open({
                        duration: 5000,
                        queue: false,
                        message: `${providerId} account has been linked`,
                        position: 'is-bottom',
                        type: 'is-success'
                    })
                });
            }
        },
        created() {
            this.$http.get(`/api/user/${this.user.id}/linked-accounts`).then(response => {
                this.socialAccounts = response.data
            });
        },
        computed: {
            linkedAccounts: function() {
                return {
                    facebook: this.socialAccounts.includes('facebook'),
                    google: this.socialAccounts.includes('google'),
                    twitter: this.socialAccounts.includes('twitter')
                }
            },
            user: function() {
                return this.getUser();
            },
            adminUser: function() {
                return this.isAdmin();
            }
        }
    }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
