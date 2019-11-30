Play Silhouette VueJs Project
=====================================

## Demo app
See [Demo on Heroku](https://vuejs-slihouette.herokuapp.com/). Loading can take a while because it uses Heroku free dyno which is turned on/off every time 
if there is no activity for some time. 

## How to run the project

1. Run backend 
`sbt run`

## Documentation
Backend and frontend parts of app are completely separate. 

### VueJs 
On running application in `prod` mode, all VueJs sources are compiled and moved to `public` directory where PlayFramework treats them as static assets.
On running application in `dev` mode, all VueJs sources are proxied from Webpack server by PlayFramework, so hot reloading and other stuff work without magic.

### Backend
To run backend you should have `sbt` installed. 

#### Running in dev mode
1. Create `devEnv.conf` file in `conf` directory to set up dev configuration

2. Disable by contentSecurityPolicy by adding following conf param to `devEnv.conf` 
```conf
play.filters.headers.contentSecurityPolicy = ""
```
 
3. Execute `sbt run` on the main folder to run the backend app.

#### Running in production

### Frontend

#### Project setup
```
npm install
```

#### Compiles and hot-reloads for development
```
npm run serve
```

#### Compiles and minifies for production
```
npm run build
```

#### Lints and fixes files
```
npm run lint
```

#### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
