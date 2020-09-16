const jare = require("./steps/jare");

const activate = api => {
  api.registerExtension(jare.extension);
};

module.exports.activate = activate;
