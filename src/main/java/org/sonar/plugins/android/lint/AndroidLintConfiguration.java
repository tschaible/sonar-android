/*
 * Sonar Android Plugin
 * Copyright (C) 2013 Jerome Van Der Linden, Stephane Nicolas, Florian Roncari, Thomas Bores and SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.android.lint;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

import java.io.File;

/**
 * This class contains the configuration of the plugin.
 * It implements the sonar properties dedicated to the plugin.
 *
 * @author Thomas Bores
 */
@Properties({
  @Property(
    key = AndroidLintConfiguration.ANDROID_LINT_PATH,
    defaultValue = "c:\\adt-bundle-windows-x86_64\\sdk\\tools\\lint.bat",
    name = "Android Lint executable",
    description = "Path to the Android Lint executable to use in Android Lint analysis. Set to empty to use the default one.",
    global = true,
    project = true),
  @Property(
    key = AndroidLintConfiguration.ANDROID_LINT_REPORT_PATH_PROPERTY,
    defaultValue = "lint-results.xml",
    name = "Android Lint report path",
    description = "Path (absolute or relative) to Android lint xml report file.",
    global = true,
    project = true),
})
public class AndroidLintConfiguration implements BatchExtension {
  public static final String ANDROID_LINT_PATH = "sonar.androidlint.lintPath";
  public static final String ANDROID_LINT_REPORT_PATH_PROPERTY = "sonar.androidlint.reportPath";
  public static final String ANDROID_LINT_PROFILE = "Android Lint";

  /**
   * This variable should be updated after each new version of Android Lint
   */
  public static final String ANDROID_LINT_VERSION = "r17";

  private final Settings conf;

  public AndroidLintConfiguration(Settings conf) {
    this.conf = conf;
  }

  /**
   * Return the value of the sonar property sonar.androidlint.reportPath
   *
   * @param project
   * @return string object containing the path to the android lint report
   */
  public String getAndroidlintReportPath(Project project) {
    String configPath = conf.getString(AndroidLintConfiguration.ANDROID_LINT_REPORT_PATH_PROPERTY);
    if (StringUtils.isEmpty(configPath)) {
      return null;
    }
    File configFile = new File(configPath);
    if (!configFile.isAbsolute()) {
      File projectRoot = project.getFileSystem().getBasedir();
      configFile = new File(projectRoot.getPath(), configPath);
    }
    return configFile.getAbsolutePath();
  }

  /**
   * Return the value of the sonar property sonar.androidlint.lintPath
   * @return string object containing the path to the lint executable
   */
  public String getAndroidLintPath() {
    return conf.getString(AndroidLintConfiguration.ANDROID_LINT_PATH);
  }
}
