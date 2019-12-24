<template>
    <div class="container">
        <ul class="admin-menu">
            <li>
                <router-link :to="{name: 'home'}">&larr; Back to site</router-link>
            </li>
            <li>
                <router-link to="/admin/user">User management</router-link>
            </li>
            <li>
                <router-link to="/admin/other">Other page sample</router-link>
            </li>
        </ul>
        <router-view/>
    </div>
</template>

<script>
    export default {
        name: 'AdminLayout'
    }
</script>

<style>
    .admin-menu {
        text-align: center;
        padding-bottom: 20px;
    }

    .router-link-active {
        font-weight: bold;
    }

    .admin-menu ul {
        list-style-type: none;
        padding: 0;
    }

    .admin-menu li {
        display: inline-block;
        margin: 0 10px;
    }

    .admin-menu a {
        color: #42b983;
    }
</style>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
