<template>
    <div>
        <div class="container">
            <nav-bar></nav-bar>
        </div>

        <section class="section">
            <div class="container">
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