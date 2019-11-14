<template>
    <div class="columns card">
        <div class="column col-lg-offset-3 col-lg-6">
            <h3 class="title is-3">Change password</h3>
            <div class="notification is-success" v-if="updated">Password has been changed</div>
            <form v-else>
                <div class="notification is-danger" v-if="error">{{error}}</div>

                <div class="field">
                    <label class="label">Current password</label>
                    <input class="input"
                           type="password"
                           :class="{'is-danger': $v.currentPassword.$dirty && $v.currentPassword.$invalid, 'is-success':  $v.currentPassword.$dirty && !$v.currentPassword.$invalid}"
                           placeholder="Current password"
                           v-model="$v.currentPassword.$model">
                    <p class="help is-danger" v-if="$v.currentPassword.$dirty && !$v.currentPassword.required">Field is required</p>
                </div>

                <div class="field">
                    <label class="label">New password</label>
                    <input class="input"
                           type="password"
                           :class="{'is-danger': $v.newPassword.$dirty && $v.newPassword.$invalid, 'is-success':  $v.newPassword.$dirty && !$v.newPassword.$invalid}"
                           placeholder="Password"
                           v-model="$v.newPassword.$model">
                    <p class="help is-danger" v-if="$v.newPassword.$dirty && !$v.newPassword.required">Field is required</p>
                    <p class="help is-danger" v-if="$v.newPassword.$dirty && !$v.newPassword.minLength">Password must have at least {{$v.newPassword.$params.minLength.min}} letters.</p>
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
    import {mapActions} from 'vuex'
    import { required, minLength } from 'vuelidate/lib/validators'

    export default {
        props: ['redirectTo', 'message'],
        data() {
            return {
                currentPassword: "",
                newPassword: "",
                updated: false,
                error: ""
            }
        },
        validations: {
            currentPassword: {
                required
            },
            newPassword: {
                required,
                minLength: minLength(5)
            }
        },
        methods: {
            ...mapActions('user', ['setUser']),
            handleSubmit() {
                this.$http.post('/password/change', {
                    'current-password': this.currentPassword,
                    'new-password': this.newPassword
                }).then(() => {
                    this.updated = true;
                }).catch(function (error) {
                    if (error.data && error.data.message) {
                        this.$v.$reset();
                        this.error = error.data.message;
                    } else {
                        console.error(error);
                    }
                });
            }
        }
    }
</script>