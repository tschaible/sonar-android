@echo off
call mvn install:install-file -Dfile=./scripts/lint_api-r17.jar -DpomFile=pom.xml -DgroupId=com.android.tools.lint -DartifactId=lint_api -Dversion=r17 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
call mvn install:install-file -Dfile=./scripts/lint-r17.jar -DpomFile=pom.xml -DgroupId=com.android.tools.lint -DartifactId=lint -Dversion=r17 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
call mvn install:install-file -Dfile=./scripts/lint_checks-r17.jar -DpomFile=pom.xml -DgroupId=com.android.tools.lint.checks -DartifactId=lint_checks -Dversion=r17 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
