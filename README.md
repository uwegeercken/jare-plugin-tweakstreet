### jare-plugin-tweakstreet

JaRE - Java Rule Engine plugin for the Tweakstreet Data Integration tool. T
This plugin enables the ETL developer to get rid of the hardcoded business logic in the ETL process. If the rules change, 
the code does not need to be touched. This enhances quality and also transparency: the business user usually has no understanding of ETL processes; with the Web application for the maintenance of the rules and the complex rule logic the user has a central place to work with the rules without the need for coding rules and without the need for IT to change the ETL process.

To manage your business logic there is a web application available at: https://github.com/uwegeercken/rule_maintenance_war. It requires a running MySQL or MariaDb instance. More detailed information is available here: https://github.com/uwegeercken/rule_maintenance_documentation

You will need the [api jar](https://github.com/twineworks/tweakstreet-api) corresponding to your version of Tweakstreet. 

To build the Tweakstreet plugin/step run `mvn package`

This will create a folder `target/tweakstreet-plugin-jare-<version>`. Copy this folder to your `$HOME/.tweakstreet/plugins/` directory. 
Restart tweakstreet and you can start using the contained steps.

Last update: 2020-09-16
