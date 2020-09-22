### jare-plugin-tweakstreet

JaRE - Java Rule Engine - plugin for the Tweakstreet Data Integration tool (available at https://tweakstreet.io).

The plugin enables the ETL developer to avoid hardcoded business logic in the ETL process. If the business logic (the rules) changes,
the data processing pipeline does not have to change. This enhances quality, as the ETL will be simpler and avoids duplicating logic across multiple ETL flows. It also enhances transparency: the business user typically has no understanding of ETL processes: If there is business logic mixed with IT code of any kind, then the business user will simply not understand it. With the Web application for the maintenance of the business rules, the user has a central place to define the complete business logic, without the need for hardcoding it.

To manage your business logic there is a web application available at: https://github.com/uwegeercken/rule_maintenance_war. It requires a running MySQL or MariaDb instance.

To build the Tweakstreet plugin/step run `mvn package`. - you will need the [api jar](https://github.com/twineworks/tweakstreet-api) corresponding to your version of Tweakstreet.

This will create a folder `target/tweakstreet-plugin-jare-<version>`. Copy this folder to your `$HOME/.tweakstreet/plugins/` directory.
Restart tweakstreet and you can start using the contained steps.

More detailed information about the ruleengine and the business rules maintenance web application here: https://github.com/uwegeercken/rule_maintenance_documentation

Last update: 2020-09-22
