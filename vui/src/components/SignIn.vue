<template>
    <div class="columns card">
        <div class="column col-lg-offset-3 col-lg-6">
            <h3 class="title is-3">Sign in</h3>
            <form>
                <div class="field">
                    <label class="label">Email</label>
                    <input class="input"
                           :class="{'is-danger': $v.email.$dirty && $v.email.$invalid, 'is-success':  $v.email.$dirty && !$v.email.$invalid}"
                           type="text"
                           placeholder="Email input"
                           v-model.trim="$v.email.$model">
                    <p class="help is-danger" v-if="$v.email.$dirty && !$v.email.required">Field is required</p>
                    <p class="help is-danger" v-if="$v.email.$dirty && !$v.email.email">Wrong email address</p>
                </div>

                <div class="field">
                    <label class="label">Password</label>
                    <input class="input"
                           type="password"
                           :class="{'is-danger': $v.password.$dirty && $v.password.$invalid, 'is-success':  $v.password.$dirty && !$v.password.$invalid}"
                           placeholder="Password"
                           v-model="$v.password.$model">
                    <p class="help is-danger" v-if="$v.password.$dirty && !$v.password.required">Field is required</p>
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
                        <button class="button is-success" :disabled="$v.$invalid" @click="handleSubmit">Submit</button>
                    </p>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
    import {mapActions} from 'vuex'
    import { required, email } from 'vuelidate/lib/validators'

    export default {
        data() {
            return {
                email: "",
                password: "",
                rememberMe: false
            }
        },
        validations: {
            email: {
                required,
                email
            },
            password: {
                required
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