sonar-android-lint-plugin
=========================

!!!! IN DEVELOPMENT !!!!
**Contributions are welcome. Join the effort !**

Extension plugin for Android Lint in Sonar.

The idea is to visualize Android Lint errors directly in Sonar, in the same way as findbugs / PMD / checkstyle errors are reported.

This project is part of a larger effort to industrialize Android development.

I -Compiling and Installing the plugin:
---------------------------------------
 - Install maven
 - Clone the repository
 - update your local repository of Maven with the last release of android-lint, currently the r17 (2013.04):
	-> run the .bat file: copy_necessary_jars_in_maven_locally.bat
 - Compile and test the code, then generate the jar:
	-> run "mvn clean install" command in your terminal
 - copy the jar (in the new generated target folder) in <path_to_your_sonar_install>/extensions/plugins folder,
 - restart sonar

II - Running an analyse:
------------------------
1. On a Maven project
 - mvn sonar:sonar -Dsonar.profile="Android Lint" in your project
 - That's it !

2. On another project using sonar-runner
 - Add two properties to your sonar-project.properties
  -> sonar.androidLint.reportPath: to tell Sonar, where it can find the android result xml file.
  -> sonar.androidlint.lintPath: to tell Sonar, where is installed the android lint tool (lint.jar).

III - Updating the plugin for new android-lint release:
-------------------------------------------------------
 - Copy the three new android-lint jar in the scripts folder
	-> lint-rxx.jar
	-> lint-api-rxx.jar
	-> lint-checks-rxx.jar
 - Copy the install_lint_r17_in_maven.bat in a new one named install_lint_rxx_in_maven.bat
 - Adapt the install_lint_rx_in_maven.bat and launch it.
 - Run generate_rules_and_profile.bat to generate the new version for resources:
	-> src\main\resources\org\sonar\plugins\android\lint\rules.txt
	-> src\main\resources\org\sonar\plugins\android\lint\profile-android-lint.xml
 - Change the build properties of the project to point to the new version of jar file in maven
 - Adapt the source code to the new version of android-lint
