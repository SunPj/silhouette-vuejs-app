import { ToastProgrammatic as Toast } from 'buefy'
import Vue from 'vue'

var existingWindow = null;

function showNotification(response) {
    let message = ''

    if (response.status === 409) {
        let providersTextPart = '';
        if (response.data.providers.length === 1) {
            providersTextPart = `Please sign up using your ${response.data.providers[0]}`
        } else {
            providersTextPart = `Please sign up using one of your ${response.data.providers.join(" or ")}`
        }
        message = `There is an existing account for this email. ${providersTextPart} and attach your social account in profile page to be able to use it for further sign in's`
    } else if (response.status === 400) {
        message = 'Authentication provider has empty email. Please sign up using email and attach your social account in profile page to be able to use it for further sign in\'s'
    } else {
        message = `Something went wrong`
    }

    Toast.open({
        duration: 8000,
        queue: false,
        message: message,
        position: 'is-bottom',
        type: 'is-danger'
    })
}

function openAuthenticationWindow(authenticationInitUrl, backendHandlerUrl, successHandler) {

    window.socialProviderCallback = function(provider, queryParams) {
        Vue.http.get(backendHandlerUrl, {params: queryParams}).then(response => {
            successHandler(response.data)
        }).catch(function (response) {
            console.log("Error on social auth")
            console.log(response)
            showNotification(response)
        });
    };

    if (existingWindow) {
        existingWindow.close();
    }

    const w = 500;
    const h = 500;
    const dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : window.screenX;
    const dualScreenTop = window.screenTop != undefined ? window.screenTop : window.screenY;

    const width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    const systemZoom = width / window.screen.availWidth;
    const left = (width - w) / 2 / systemZoom + dualScreenLeft;
    const top = (height - h) / 2 / systemZoom + dualScreenTop;
    existingWindow = window.open(authenticationInitUrl, "Authentication", 'scrollbars=yes, width=' + w / systemZoom + ', height=' + h / systemZoom + ', top=' + top + ', left=' + left);
}

export {openAuthenticationWindow}