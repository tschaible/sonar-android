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

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;

public class AndroidLintSensor implements Sensor {

  private RulesProfile profile;

  private AndroidLintExecutor executor;

  public AndroidLintSensor(RulesProfile profile, AndroidLintExecutor executor) {
    this.profile = profile;
    this.executor = executor;
  }

  @Override
  public void analyse(Project project, SensorContext sensorContext) {
    executor.execute(sensorContext);
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return Java.KEY.equals(project.getLanguageKey())
      && !profile.getActiveRulesByRepository(AndroidLintRuleRepository.REPOSITORY_KEY).isEmpty();
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
