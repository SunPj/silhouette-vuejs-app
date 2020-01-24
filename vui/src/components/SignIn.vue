<template>
    <div class="card">
        <b-loading :active.sync="loading"></b-loading>

        <card-header-with-provider-buttons label="Sign in" :redirect-to="redirectTo"></card-header-with-provider-buttons>

        <div class="card-content">
            <div class="content">
                <form>
                    <div class="notification" :class="notification.type" v-if="this.notification.text">
                        {{this.notification.text}}
                    </div>

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
                        <p class="help is-danger" v-if="$v.password.$dirty && !$v.password.required">Field is
                            required</p>
                    </div>

                    <div class="field">
                        <div class="control">
                            <label class="checkbox">
                                <input type="checkbox" v-model="rememberMe">
                                Remember me
                            </label>
                        </div>
                    </div>
                    <div class="field is-grouped">
                        <p class="control">
                            <button class="button is-success" :disabled="$v.$invalid" @click="handleSubmit">Submit
                            </button>
                        </p>
                        <div class="has-text-right">
                            <router-link class="button is-warning" to="/forgot-password">I forgot my password
                            </router-link>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script>
    import {mapActions} from 'vuex'
    import {required, email} from 'vuelidate/lib/validators'
    import CardHeaderWithProviderButtons from "./CardHeaderWithProviderButtons";

    export default {
        components: {CardHeaderWithProviderButtons},
        props: ['redirectTo', 'message'],
        data() {
            return {
                loading: false,
                email: "",
                password: "",
                rememberMe: false,
                notification: {
                    type: '',
                    text: ''
                }
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
                    this.loading = true;
                    this.$http.post('/signIn', {
                        email: this.email,
                        password: this.password,
                        rememberMe: this.rememberMe
                    }).then(response => {
                        this.loading = false;
                        const userData = response.data
                        this.setUser(userData)
                        this.$emit('loggedIn', userData)
                        const nextTo = this.redirectTo ? this.redirectTo : '/profile'
                        this.$router.push(nextTo)
                    }).catch(function (error) {
                        this.loading = false;
                        if (error.data && error.data.errorCode) {
                            this.$v.$reset();
                            this.notification.type = 'is-danger';
                            this.notification.text = this.getErrorMessage(error.data);
                        } else {
                            console.error(error);
                        }
                    });
                }
            },
            getErrorMessage(response) {
                if (response.errorCode === 'InvalidPassword') {
                    return `Wrong email or password. Your account will be temporary blocked after ${response.attemptsAllowed} try(s)`
                } else if (response.errorCode === 'NonActivatedUserEmail') {
                    return 'Email has not been confirmed yet'
                } else if (response.errorCode === 'UserNotFound') {
                    return 'There is no user by this email'
                } else if (response.errorCode === 'TooManyRequests') {
                    return `You reached attempts limit. Please try later at ${new Date(response.nextAllowedAttemptTime).toLocaleString()} or get in touch with site admin`
                } else {
                    return 'System error. Please get in touch with site admin to solve this error.'
                }
            }
        },
        created() {
            if (this.message === 'passwordChanged') {
                this.notification.type = 'is-success';
                this.notification.text = 'Password has been changed. Please use your email and new password to sign in'
            } else if (this.message === 'emailVerified') {
                this.notification.type = 'is-success';
                this.notification.text = 'Email has been verified successfully. Please use your email, password to sign in'
            } else if (this.message === 'sessionExpired') {
                this.notification.type = 'is-info';
                this.notification.text = 'Session is expired. Please sign in'
            } else if (this.message === 'activateEmail') {
                this.notification.type = 'is-info';
                this.notification.text = 'Please activate your account using the link we have sent to your email. You can sign in then.'
            } else if (this.message === 'authenticationRequired') {
                this.notification.type = 'is-warning';
                this.notification.text = 'Please sign in to get access to page'
            }
        }
    }
</script>