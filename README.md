Play Silhouette VueJs Project
=====================================

The VueJs sample project which can be extended to fit your needs. 

## Demo app
See [Demo on Heroku](https://vuejs-slihouette.herokuapp.com/). Loading can take a while because it uses Heroku free dyno which is turned on/off every time 
if there is no activity for some time. 

## Brief description of functionality:
* Sign up
* Sign in
* Email activation
* Reset password
* Change password
* SPA
* Basic user management functionality (admin page, user roles) 

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

### VueJs && PlayFramework integration 
On running application in `prod` mode, all VueJs sources are compiled and moved to `public` directory where PlayFramework treats them as static assets.
On running application in `dev` mode, all VueJs sources are proxied from Webpack server by PlayFramework, so hot reloading and other stuff work without magic.

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
```

3. Run backend `sbt run`

4. Install frontend dependencies and run frontend `cd vui && npm install && npm run serve`

5. Open your http://localhost:9000/ in your browser. That's PlayFramework bakend server which proxies all frontend assets so 
Hot

### Running in production
1. Build frontend code `npm run build`
2. Compile play application for production `sbt compile stage` (frontend resources will be copied) 
