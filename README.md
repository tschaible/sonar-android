sonar-android-lint-plugin
=========================

See http://docs.codehaus.org/display/SONAR/Android+Plugin


**Contributions are welcome. Join the effort !**

Extension plugin for Android Lint in Sonar.

The idea is to visualize Android Lint errors directly in Sonar, in the same way as findbugs / PMD / checkstyle errors are reported.

I - Compiling and Installing the plugin:
---------------------------------------
 - Install maven
 - Clone the repository
 - Compile and test the code, then generate the jar:
	-> run "mvn clean install" command in your terminal
 - copy the jar (in the new generated target folder) in <path_to_your_sonar_install>/extensions/plugins folder,
 - restart sonar

II - Running an analyse:
------------------------
1. On a Maven project
 - mvn sonar:sonar -Dsonar.profile="Android Lint r17" in your project
 - That's it !

2. On another project using sonar-runner
 - Add two properties to your sonar-project.properties
  -> sonar.androidLint.reportPath: to tell Sonar, where it can find the android result xml file.
  -> sonar.androidlint.lintPath: to tell Sonar, where is installed the android lint tool (lint.jar).

III - Updating the plugin for new android-lint release:
-------------------------------------------------------
For now (2013.06.28), the jars for the Android Lint tool aren't present on the Central Maven Repository.
That's the reasons why the jars are currently embedded in the Git Repo and you must update the jars manually.
For more information read: https://groups.google.com/forum/#!topic/adt-dev/HvDcynqIuj4

Steps for the update:
 - Copy the three new android-lint jar in the libs folder
	-> lint-rxx.jar
	-> lint-api-rxx.jar
	-> lint-checks-rxx.jar
 - Update the pom.xml to use the new version of the jar files
 - Add AndroidLintProfileGenerator.jar in sonar-android/scripts
You can download this JAR at this link:  <a href="https://github.com/tbores/AndroidLint_SonarProfileGenerator" title="Link to the AndroidLint_SonarProfilegenerator repository">AndroidLint_SonarProfileGenerator</a>
 - Run generate_rules_and_profile.bat to generate the new version for resources:
	-> src\main\resources\org\sonar\plugins\android\lint\rules.txt
	-> src\main\resources\org\sonar\plugins\android\lint\profile-android-lint.xml
 - Update the Java Build Path properties of the project to point to the new version of jar files
 - Adapt the source code (update the Quality Profile name)
