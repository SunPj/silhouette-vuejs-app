<template>
    <div class="columns card">
        <div class="column col-lg-offset-3 col-lg-6">
            <h3 class="title is-3">Sign up</h3>
            <form>
                <div class="field">
                    <input class="input" type="text" placeholder="First name" v-model="firstName">
                </div>
                <div class="field">
                    <input class="input" type="text" placeholder="Last name" v-model="lastName">
                </div>
                <div class="field">
                    <input class="input" type="email" placeholder="Email" v-model="email">
                </div>
                <div class="field">
                    <input class="input" type="password" placeholder="Password" v-model="password">
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
    import { mapActions } from 'vuex'
    export default {
        data() {
            return {
                firstName: "",
                lastName: "",
                email: "",
                password: ""
            }
        },
        methods: {
            ...mapActions('user', ['setUser']),
            handleSubmit(e) {
                e.preventDefault();
                this.$http.post("/signUp", {
                    firstName: this.firstName,
                    lastName: this.lastName,
                    email: this.email,
                    password: this.password
                }).then(response => {
                    const userData = response.data
                    this.setUser(userData)
                    this.$emit('loggedIn', userData)
                    this.$router.push('/profile')
                }).catch(error => {
                    console.error(error);
                });
            }
        }
    }
</script>