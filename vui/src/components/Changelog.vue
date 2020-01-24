<template>
    <div>
        <h1 class="title is-2">Changelog</h1>

        <table class="table is-fullwidth">
            <tbody>
            <tr>
                <td>1/24/20</td>
                <td>Password brute force protection (Default is 5 tries per 30 minutes, but can be configured)</td>
            </tr>
            <tr>
                <td>1/10/20</td>
                <td>Recaptcha integration</td>
            </tr>
            <tr>
                <td>12/21/19</td>
                <td>Facebook, Google, Twitter OAuth authentication</td>
            </tr>
            <tr>
                <td>12/21/19</td>
                <td>Redirect user back to initial page when user logins after session expires</td>
            </tr>
            <tr>
                <td>12/10/19</td>
                <td>Loading indicator for signin/signup pages</td>
            </tr>
            <tr>
                <td>12/9/19</td>
                <td>User management page for admin</td>
            </tr>
            <tr>
                <td>11/14/19</td>
                <td>Postgres DB integration</td>
            </tr>
            <tr>
                <td>11/11/19</td>
                <td>SignIn/SignUp forms validation on frontend</td>
            </tr>
            <tr>
                <td>11/4/19</td>
                <td>Move user data from localstorage to vuex</td>
            </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
    export default {
    }
</script>

<style scoped>
</style>