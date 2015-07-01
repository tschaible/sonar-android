/*
 * Copyright (C) 2013-2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.it.android;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.locator.FileLocation;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({AndroidTest.class})
public class AndroidTestSuite {

  private static final String PLUGIN_KEY = "android";

  @ClassRule
  public static final Orchestrator ORCHESTRATOR = Orchestrator.builderEnv()
    .setOrchestratorProperty("javaVersion", "DEV")
    .addPlugin("java")
    .addPlugin(PLUGIN_KEY)
    .setMainPluginKey(PLUGIN_KEY)
    .restoreProfileAtStartup(FileLocation.ofClasspath("/Android_Lint_java.xml"))
    .restoreProfileAtStartup(FileLocation.ofClasspath("/lint-without-unused-id.xml"))
    .build();

  public static boolean sonarqube_version_is_after_5_1() {
    return ORCHESTRATOR.getServer().version().isGreaterThanOrEquals("5.1");
  }
  public static boolean sonarqube_version_is_after_5_2() {
    return ORCHESTRATOR.getServer().version().isGreaterThanOrEquals("5.2");
  }

  private static boolean is_after_plugin(String version) {
    return ORCHESTRATOR.getConfiguration().getPluginVersion(PLUGIN_KEY).isGreaterThanOrEquals(version);
  }

  public static boolean isAtLeastPlugin1_1() {
    return is_after_plugin("1.1");
  }

}
