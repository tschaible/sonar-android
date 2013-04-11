sonar-android-lint-plugin
=========================

!!!! IN DEVELOPMENT !!!!
**Contributions are welcome. Join the effort !**

Extension plugin for Android Lint in Sonar.

The idea is to visualize Android Lint errors directly in Sonar, in the same way as findbugs / PMD / checkstyle errors are reported.

You can give a try: 
 - clone the repo, 
 - mvn clean install, 
 - copy the jar in your sonar extensions/plugins folder, 
 - restart sonar
 - se the lint mojo of the android-maven-plugin (http://maven-android-plugin-m2site.googlecode.com/svn/lint-mojo.html)
 - mvn sonar:sonar -Dsonar.profile="Android Lint" in your project
 - That's it !

This project is part of a larger effort to industrialize Android development, you can have a look at this other project (https://github.com/stephanenicolas/Quality-Tools-for-Android)

