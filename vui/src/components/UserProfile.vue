<template>
    <div class="content">
        <h1 class="title is-2">Profile page</h1>
        <b>First name:</b> {{user.firstName}} <br/>
        <b>Last name:</b> {{user.lastName}} <br/>
        <b>Email:</b> {{user.email}}<br/><br/>
        <p>Here you can <router-link to="/change-password">change your password</router-link></p>
        <p v-if="adminUser">You are admin user. Admin page is <router-link to="/admin/user">here</router-link></p>
    </div>
</template>
<script>
    import { mapGetters } from 'vuex'
    export default {
        name: 'UserProfile',
        methods: {
            ...mapGetters('user', ['getUser', 'isAdmin'])
        },
        computed: {
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