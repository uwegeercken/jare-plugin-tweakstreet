const ID = "com.twineworks.tweakstreet.plugins.jare.steps.jare";

const extension = {
  id: ID,
  type: "step",
  stepType: "passthrough",
  name: "JaRE - Java Rule Engine",
  category: "Utility",
  description: "Passes data through the Java Rule Engine - JaRE",
  icon: "steps/jare/icon.svg",
  docs: "steps/jare/docs.html",
  colorCode: "computation",
  settings: [
    {
      id: "rules",
      type: "file",
      label: "Rules",
      hoverHtml: "<p>The zip file or directory to read rules from</p><p><i>Evaluated for each input row</i></p>",
      value: "./rules.zip"
    },
    {
      id: "input",
      type: "dict",
      label: "Input",
      hoverHtml: "<p>Key value pairs to use as the input record</p><p><i>Evaluated for each input row</i></p>",
      value: []
    },
    {
      id: "collectResults",
      type: "boolean",
      label: "Collect Result Details",
      hoverHtml: "<p>If checked, collects details of the execution of each rule</p><p><i>Evaluated for each input row</i></p>",
      value: false
    }
  ],
  results: [
    { type: "dict", name: "output", label: "engine output" },
    { type: "list", name: "summary", label: "result summary" },
    { type: "list", name: "results", label: "result details" },
  ],
  outputFields: {
    initial: [
      { type: "dict", name: "output", value: { type: "result", name: "output" }},
    ]
  }
};

module.exports.extension = extension;
