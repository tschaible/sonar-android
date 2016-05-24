/*
 * Android :: IT
 * Copyright (C) 2013-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.it.android;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.locator.FileLocation;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.io.File;

@RunWith(Suite.class)
@SuiteClasses({AndroidTest.class})
public class AndroidTestSuite {

  private static final String PLUGIN_KEY = "android";

  @ClassRule
  public static final Orchestrator ORCHESTRATOR = Orchestrator.builderEnv()
    .addPlugin("java")
    .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../sonar-android-plugin/target"), "sonar-android-plugin-*.jar"))
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
