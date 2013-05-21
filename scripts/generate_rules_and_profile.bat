@echo off
REM Generate rules.txt from Lint
REM This file will be used by the sonar plugin
echo Generate rules.txt...
call lint --show > ../src/main/resources/org/sonar/plugins/android/lint/rules.txt
echo [DONE] rules.txt generated.

REM Generate the rules list in order to create the profile-android-lint.xml
REM This file android_lint_rules_list.txt will be then removed
REM as it is not needed by the sonar plugin.
echo Generate android_lint_rules_list.txt
call lint --list > android_lint_rules_list.txt
echo [DONE] android_lint_rules_list.txt generated.

REM Call java program to generate the xml from the android_lint_rules_list.txt
call java -jar AndroidLintProfileGenerator.jar android_lint_rules_list.txt ../src/main/resources/org/sonar/plugins/android/lint/
echo Delete android_lint_rules_list.txt
del android_lint_rules_list.txt
echo Exit
