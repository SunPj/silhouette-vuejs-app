<template>
    <div class="columns card">
        <div class="column col-lg-offset-3 col-lg-6">
            <h3 class="title is-3">Reset password</h3>
            <div class="notification is-success" v-if="sent">
                If we found an account associated with that email, we've sent password reset instructions to the email address on the account.
            </div>
            <form v-else>
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
                    <p class="control">
                        <button class="button is-success" :disabled="$v.$invalid" @click.prevent.stop="handleSubmit">Submit</button>
                    </p>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
    import { required, email } from 'vuelidate/lib/validators'

    export default {
        data() {
            return {
                email: "",
                sent: false
            }
        },
        validations: {
            email: {
                required,
                email
            }
        },
        methods: {
            handleSubmit() {
                this.$http.post('/password/forgot', {email: this.email}).then(() => {
                    this.sent = true;
                }).catch(function (error) {
                    console.error(error);
                });
            }
        }
    }
</script>