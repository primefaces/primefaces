const path = require("path")
const MiniCssExtractPlugin = require("mini-css-extract-plugin")

module.exports = {
  mode: "production",
  // devtool: "sourcemap",
  entry: "./src/main.js",
  resolve: {
    extensions: [".js"]
  },
  externals: {
    "moment": "moment",
    "moment-timezone": "momentTimezone",
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [
          { loader: MiniCssExtractPlugin.loader },
          { loader: "css-loader", options: { importLoaders: 1 } },
          {
            loader: "postcss-loader", options: {
              ident: "postcss",
              plugins: () => [
                require("postcss-custom-properties")({ // for css vars
                  preserve: false, // completely reduce all css vars
                  importFrom: [
                    "src/fullcalendar-vars.css"
                  ]
                })
              ]
            }
          }
        ]
      }
    ]
  },
  output: {
    filename: "fullcalendar.min.js",
    path: path.join(__dirname, ".."),
    libraryTarget: "global",
    library: "FullCalendar"
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: "fullcalendar.min.css",
    })
  ]
}
