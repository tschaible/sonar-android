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

import org.sonar.api.rules.RulePriority;

/**
 * This utility class contains the global project constants and other utility methods
 *
 * @author Jerome Van Der Linden
 * @author Thomas Bores
 */
public final class AndroidLintUtils {

  /**
   * Global constants, name of the plugin
   */
  public static final String PLUGIN_NAME = "AndroidLint";

  /**
   * private constructor for util class
   */
  private AndroidLintUtils() {
    // Do nothing
  }

  /**
   * Convert Android Lint {@link Severity} to Sonar {@link RulePriority}
   *
   * Default mapping:
   * |------------------------|
   * | Android     | Sonar    |
   * |-------------|----------|
   * | N/A       	 | BLOCKER  |
   * | FATAL       | CRITICAL |
   * | ERROR	     | MAJOR    |
   * | WARNING     | MINOR    |
   * | INFORMATION | INFO     |
   * | IGNORE		   | N/A	  	|
   * |------------------------|
   *
   * @param severityLint Android Lint Severity
   * @return Sonar Severity
   */
  public static RulePriority getSonarSeverityFromLintSeverity(String severityLint) {
    RulePriority severity;

    if (severityLint.equalsIgnoreCase("FATAL"))
    {
      severity = RulePriority.CRITICAL;
    }
    else if (severityLint.equalsIgnoreCase("ERROR"))
    {
      severity = RulePriority.MAJOR;
    }
    else if (severityLint.equalsIgnoreCase("WARNING"))
    {
      severity = RulePriority.MINOR;
    }
    else if (severityLint.equalsIgnoreCase("INFORMATION"))
    {
      severity = RulePriority.INFO;
    }
    else
    {
      severity = null;
    }
    return severity;
  }
}
