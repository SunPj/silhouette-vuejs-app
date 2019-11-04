<template>
    <div class="columns card">
        <div class="column col-lg-offset-3 col-lg-6">
            <h3 class="title is-3">Sign in</h3>
            <form>
                <div class="field">
                    <input class="input" type="email" placeholder="Email" v-model="email">
                </div>
                <div class="field">
                    <input class="input" type="password" placeholder="Password" v-model="password">
                </div>
                <div class="field">
                    <div class="control">
                        <label class="checkbox">
                            <input type="checkbox" v-model="rememberMe">
                            Remember me
                        </label>
                    </div>
                </div>
                <div class="field">
                    <p class="control">
                        <button class="button is-success" @click="handleSubmit">Submit</button>
                    </p>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
    import {mapActions} from 'vuex'

    export default {
        data() {
            return {
                email: "",
                password: "",
                rememberMe: false
            }
        },
        methods: {
            ...mapActions('user', ['setUser']),
            handleSubmit(e) {
                e.preventDefault()
                if (this.password.length > 0) {
                    this.$http.post('/signIn', {
                        email: this.email,
                        password: this.password,
                        rememberMe: this.rememberMe
                    }).then(response => {
                        const userData = response.data
                        this.setUser(userData)
                        this.$emit('loggedIn', userData)
                        this.$router.push('/profile')
                    }).catch(function (error) {
                        console.error(error.response);
                    });
                }
            }
        }
    }
</script>