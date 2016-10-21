module.exports = {
  //extends: "eslint:recommended",
  env: {
    browser: true,
    node: true
  },
  parserOptions: {
    ecmaVersion: 6,
    sourceType: "module", 
    ecmaFeatures: {
      jsx: true,
      experimentalObjectRestSpread: true,
      //modules: true
    },  
  },
  plugins: [
      "react"
  ],
  rules: {
    "no-undef": "warn"
  },
  globals: {
    "Promise": true,
    "Map": true,
    "Symbol": true,
    "properties": true,
    "_": true,
  }
};
