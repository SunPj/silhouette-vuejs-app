<template>
    <div class="columns card">
        <div class="column col-lg-offset-3 col-lg-6">
            <h3 class="title is-3">Set new password</h3>
            <form>
                <div class="notification is-danger" v-if="error">{{error}}</div>

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
                    <p class="control">
                        <button class="button is-success" :disabled="$v.$invalid" @click.prevent.stop="handleSubmit">Reset password</button>
                    </p>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
    import { required, minLength } from 'vuelidate/lib/validators'

    export default {
        props: ['token'],
        data() {
            return {
                password: "",
                error: ""
            }
        },
        validations: {
            password: {
                required,
                minLength: minLength(5)
            }
        },
        methods: {
            handleSubmit() {
                this.$http.post('/password/reset/' + this.token, {password: this.password}).then(() => {
                    this.$router.push('/signIn?message=passwordChanged')
                }).catch(function (error) {
                    if (error.data && error.data.message) {
                        this.error = error.data.message;
                    } else {
                        console.error(error);
                    }
                });
            }
        }
    }
</script>