<template>
    <section>
        <b-sidebar
                type="is-light"
                :fullheight="true"
                :open="true">
            <div class="p-1">
                <img src="https://raw.githubusercontent.com/buefy/buefy/dev/static/img/buefy-logo.png"
                     alt="Lightweight UI components for Vue.js based on Bulma"/>
                <b-menu>
                    <b-menu-list label="Menu">
                        <b-menu-item label="Users" icon="account" tag="router-link" to="/admin/user"></b-menu-item>
                        <b-menu-item label="Other" icon="account" tag="router-link" to="/admin/other"></b-menu-item>
                    </b-menu-list>
                    <b-menu-list label="Actions">
                        <b-menu-item label="Back to site" tag="router-link" :to="{name: 'home'}" exact></b-menu-item>
                    </b-menu-list>
                </b-menu>
            </div>
        </b-sidebar>
        <div class="block">
            <div class="container admin-container">
                <router-view/>
            </div>
        </div>
    </section>
</template>

<script>
    export default {
        name: 'AdminLayout'
    }
</script>

<style>
    .p-1 {
        padding: 1em;
    }

    .admin-container {
        text-align: center;
        padding-top: 20px;
    }
</style>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
