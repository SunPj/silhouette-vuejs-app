const path = require("path");

module.exports = {
    outputDir: path.resolve(__dirname, "../public/ui"),
    assetsDir: process.env.NODE_ENV === 'production' ? 'static':'',
    devServer: {
        public: 'localhost:8080',
        headers: {
            'Access-Control-Allow-Origin': '*',
        }
    }
}