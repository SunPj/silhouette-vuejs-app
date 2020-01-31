<template>
    <div>
        <nav-bar></nav-bar>

        <section class="section">
            <div class="container is-fluid">
                <router-view/>
            </div>
        </section>
    </div>
</template>

<style lang="scss">
</style>

<script>
    import NavBar from "./NavBar";

    export default {
        components: {NavBar},
    }
</script>