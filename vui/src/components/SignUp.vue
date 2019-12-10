<template>
    <div class="columns card">
        <b-loading :active.sync="loading"></b-loading>

        <div class="column col-lg-offset-3 col-lg-6">
            <h3 class="title is-3">Sign up</h3>
            <form>
                <div class="notification is-danger" v-if="backendError !== null && !$v.$anyDirty">
                    {{backendError}}
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
                    <p class="control">
                        <button class="button is-success" :disabled="$v.$invalid" @click="handleSubmit">Submit</button>
                    </p>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
    import { mapActions } from 'vuex'
    import { required, minLength, email } from 'vuelidate/lib/validators'
    export default {
        data() {
            return {
                loading: false,
                firstName: "",
                lastName: "",
                email: "",
                password: "",
                backendError: null
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
            }
        },
        methods: {
            ...mapActions('user', ['setUser']),
            handleSubmit(e) {
                e.preventDefault();
                this.loading = true;
                this.$http.post("/signUp", {
                    firstName: this.firstName,
                    lastName: this.lastName,
                    email: this.email,
                    password: this.password
                }).then(response => {
                    this.loading = false;
                    const userData = response.data
                    this.setUser(userData)
                    this.$emit('loggedIn', userData)
                    this.$router.push({ path: '/signin', query: {message: 'activateEmail'} })
                }).catch(error => {
                    this.loading = false;
                    if (error.data && error.data.message) {
                        this.$v.$reset();
                        this.backendError = error.data.message;
                    } else {
                        console.error(error);
                    }
                });
            }
        }
    }
</script>