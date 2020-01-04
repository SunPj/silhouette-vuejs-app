const path = require("path");
const webpack = require('webpack')

module.exports = {
    outputDir: path.resolve(__dirname, "../public/ui"),
    assetsDir: process.env.NODE_ENV === 'production' ? 'static':'',
    devServer: {
        public: 'localhost:8080',
        headers: {
            'Access-Control-Allow-Origin': '*',
        }
    },
    configureWebpack: {
        plugins: [
            new webpack.DefinePlugin({
                'process.conf': {
                    'RECAPTCHA_SITEKEY': JSON.stringify(process.env.RECAPTCHA_SITEKEY)
                }
            })
        ]
    }
}