Playframework authentication && user management sample 
=====================================
The goal of this project is to have starter project that covers basic authentication && user management functionality.

This is a Scala Playframework application that utilizes Silhouette as authentication library and Vuejs for frontend.
 
## Demo app
See [Demo on Heroku](https://vuejs-slihouette.herokuapp.com/). Loading can take a while because it uses Heroku free dyno which is turned on/off every time 
if there is no activity for some time. 

## Brief description of functionality:
* Sign up
* Sign in
* Email activation
* Reset password
* Change password
* Authentication via social providers Facebook, Google, Twitter
* SPA
* Basic user management functionality (admin page, user roles) 
* ReCaptcha integration (signup form)
* Password brute force protection (Default is 5 tries per 30 minutes, but can be configured)

## Backend technical details:
* PlayFramework (Scala)
* Silhouette authentication library
* PlayFramework + VueJs integration (dev hot reload && prod static assets)
* Sending emails via SendGrid
* Postgres DB
* Ready to deploy to Heroku

## Frontend technical details:
* VueJs
* Vue JWT integration
* Vue page access restriction for routes
* Vue validation using vuelidate
* Bulma
* Buefy
* Vuex for storing user data in local storage

Although this project uses VueJs on frontend it's easy to port to Angular/ReactJs/etc because frontend part it fully 
decoupled. Just replace code in `vue` directory for anything you like and it will work. 

### VueJs && PlayFramework integration [also applicable to Angular/React/ect]
On running application in `prod` mode, all frontend sources are compiled and moved to `public` directory where PlayFramework treats them as static assets.

On running application in `dev` mode, all frontend sources are proxied from Webpack server by PlayFramework, so hot reloading and other stuff work without magic.

## How to run the project locally in dev mode

1. Make sure you have `sbt` and `npm` installed

2. Create `devEnv.conf` file in `<project_root>/conf` directory and specify there your keys. See example below 

```
play.filters.headers.contentSecurityPolicy = "*"

silhouette {
  csrfStateItemHandler.signer.key="..."
  oauth1TokenSecretProvider.signer.key="..."
  oauth1TokenSecretProvider.crypter.key="..."
  authenticator.sharedSecret="..."
}

sendgrid.api.key = "..."

play.crypto.secret="..."

recaptcha.secretKey = "..."

```
3. Run backend `sbt run`

4. Make sure you have RECAPTCHA_SITEKEY environment variable or export it `export RECAPTCHA_SITEKEY="..."`

5. Install frontend dependencies and run frontend `cd vui && npm install && npm run serve`

6. Open your http://localhost:9000/ in your browser. That's PlayFramework bakend server which proxies all frontend assets so 
Hot

### Running in production
1. Build frontend code `npm run build`
2. Compile play application for production `sbt compile stage` (frontend resources will be copied) 
