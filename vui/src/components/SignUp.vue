<template>
    <div class="card">
        <b-loading :active.sync="loading || captcha.status === captchaStatus.LOADING"></b-loading>

        <card-header-with-provider-buttons label="Sign Up"></card-header-with-provider-buttons>

        <div class="card-content">
            <div class="content">
                <form>
                    <div class="notification is-danger" v-if="backendError !== null && !$v.$anyDirty">
                        <div v-if="backendError.data && backendError.data.message">
                            {{backendError.data.message}}
                        </div>
                        <div v-else-if="backendError.status && backendError.status === 409">
                            Please check email your have entered. The email address is already being used. Please <router-link to="/signin">Sign in</router-link> or <router-link to="/forgot-password">reset password</router-link> if you forgot it
                        </div>
                    </div>

                    <div class="field">
                        <label class="label">First name</label>
                        <input class="input"
                               type="text"
                               placeholder="First name"
                               :class="{'is-danger': $v.firstName.$dirty && $v.firstName.$invalid, 'is-success':  $v.firstName.$dirty && !$v.firstName.$invalid}"
                               v-model="$v.firstName.$model">
                        <p class="help is-danger" v-if="$v.firstName.$dirty && !$v.firstName.required">Field is required</p>
                        <p class="help is-danger" v-if="$v.firstName.$dirty && !$v.firstName.minLength">Last name must have at least {{$v.firstName.$params.minLength.min}} letters.</p>
                    </div>
                    <div class="field">
                        <label class="label">Last name</label>
                        <input class="input"
                               type="text"
                               :class="{'is-danger': $v.lastName.$dirty && $v.lastName.$invalid, 'is-success':  $v.lastName.$dirty && !$v.lastName.$invalid}"
                               placeholder="Last name"
                               v-model="$v.lastName.$model">
                        <p class="help is-danger" v-if="$v.lastName.$dirty && !$v.lastName.required">Field is required</p>
                        <p class="help is-danger" v-if="$v.lastName.$dirty && !$v.lastName.minLength">Last name must have at least {{$v.lastName.$params.minLength.min}} letters.</p>
                    </div>

                    <div class="field">
                        <label class="label">Email</label>
                        <input class="input"
                               :class="{'is-danger': $v.email.$dirty && $v.email.$invalid, 'is-success':  $v.email.$dirty && !$v.email.$invalid}"
                               type="text"
                               placeholder="Email input"
                               v-model.trim="$v.email.$model">

                        <p class="help is-danger" v-if="$v.email.$dirty && !$v.email.required">Field is required</p>
                        <p class="help is-danger" v-if="$v.email.$dirty && !$v.email.email">Wrong email</p>
                    </div>

                    <div class="field">
                        <label class="label">Password</label>
                        <input class="input"
                               type="password"
                               :class="{'is-danger': $v.password.$dirty && $v.password.$invalid, 'is-success':  $v.password.$dirty && !$v.password.$invalid}"
                               placeholder="Password"
                               v-model="$v.password.$model">
                        <p class="help is-danger" v-if="$v.password.$dirty && !$v.password.required">Field is required</p>
                        <p class="help is-danger" v-if="$v.password.$dirty && !$v.password.minLength">Password must have at least {{$v.password.$params.minLength.min}} letters.</p>
                    </div>

                    <div class="field">
                        <div class="notification is-danger" v-if="captchaFailed">
                            Captcha error. Please get in touch with site admin to fix this issue.
                        </div>
                        <div v-else>
                            <captcha @change="captchaChange"></captcha>
                            <p class="help is-danger" v-if="$v.$anyDirty && !$v.captcha.mustBeChecked">
                                Please verify that you are human. {{captcha.statusText}}
                            </p>
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
    </div>
</template>

<script>
    import { mapActions } from 'vuex'
    import { required, minLength, email } from 'vuelidate/lib/validators'
    import CardHeaderWithProviderButtons from "./CardHeaderWithProviderButtons";
    import Captcha from "./Captcha";

    export default {
        components: {Captcha, CardHeaderWithProviderButtons},
        data() {
            return {
                loading: false,
                firstName: "",
                lastName: "",
                email: "",
                password: "",
                backendError: null,
                captcha: {
                    status: 'LOADING'
                },
                captchaStatus: {
                    LOADING: 'LOADING',
                    FAILED: 'FAILED',
                    ERROR: 'ERROR',
                    UNCHECKED: 'UNCHECKED',
                    CHECKED: 'CHECKED'
                }
            }
        },
        computed: {
            captchaFailed: function() {
                return this.captcha.status === this.captchaStatus.FAILED
            }
        },
        validations: {
            firstName: {
                required,
                minLength: minLength(4)
            },
            lastName: {
                required,
                minLength: minLength(4)
            },
            email: {
                required,
                email
            },
            password: {
                required,
                minLength: minLength(5)
            },
            captcha: {
                mustBeChecked: (value) => value.status === 'CHECKED'
            }
        },
        methods: {
            ...mapActions('user', ['setUser']),
            captchaChange(change) {
                if (change.type === 'error') {
                    this.captcha.status = this.captchaStatus.FAILED
                } else if (change.type === 'expired') {
                    this.captcha.status = this.captchaStatus.ERROR
                    this.captcha.statusText = 'Captcha expired'
                } else if (change.type === 'rendered') {
                    this.captcha.status = this.captchaStatus.UNCHECKED
                    this.captcha.statusText = 'Captcha is not verified'
                } else if (change.type === 'verify') {
                    this.captcha.status = this.captchaStatus.CHECKED
                    this.captcha.key = change.response
                }
            },
            handleSubmit(e) {
                e.preventDefault();
                this.loading = true;
                this.$http.post("/signUp", {
                    firstName: this.firstName,
                    lastName: this.lastName,
                    email: this.email,
                    password: this.password,
                    captchaResponse: this.captcha.key
                }).then(response => {
                    this.loading = false;
                    const userData = response.data
                    this.setUser(userData)
                    this.$emit('loggedIn', userData)
                    this.$router.push({ path: '/signin', query: {message: 'activateEmail'} })
                }).catch(error => {
                    this.loading = false;
                    this.$v.$reset();
                    this.backendError = error;
                    console.error(error);
                });
            }
        }
    }
</script>