<template>
    <div class="field">
        <div class="control">
            <div class="select is-primary is-fullwidth">
                <select @change="changeRole" v-model="currentRole">
                    <option v-for="r in roles" :value="r" :key="r.id">{{r.name}}</option>
                </select>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'UserRoleEdit',
        props: ['currentRoleId', 'userId', 'roles'],
        data() {
            return {
                currentRole: null
            }
        },
        methods: {
            changeRole: function () {
                this.$http.post(`/api/user/${this.userId}/role`, {roleId: this.currentRole.id}).then(() => {
                    this.$buefy.toast.open({
                        message: `The role has been changed to ${this.currentRole.name}`,
                        type: "is-success"
                    });
                    this.$emit('roleChange', this.currentRole.id)
                })
            }
        },
        mounted: function () {
            this.currentRole = this.roles.find(r => r.id === this.currentRoleId);
        }
    }
</script>
