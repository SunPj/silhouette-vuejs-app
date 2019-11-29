const path = require("path");

module.exports = {
    outputDir: path.resolve(__dirname, "../public"),
    devServer: {
        public: 'localhost:8080',
        headers: {
            'Access-Control-Allow-Origin': '*',
        }
    }
}