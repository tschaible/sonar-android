/*
 * SonarQube Android Lint Plugin
 * Copyright (C) 2013-2016 SonarSource SA and Jerome Van Der Linden, Stephane Nicolas, Florian Roncari, Thomas Bores
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.android.lint;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputPath;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;
import org.sonar.plugins.android.AndroidPlugin;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AndroidLintSensorTest {

  private RulesProfile rulesProfile;
  private Settings settings;
  private ResourcePerspectives perspectives;

  private FileSystem fs;

  @Before
  public void prepare() {
    rulesProfile = mock(RulesProfile.class);
    settings = new Settings();
    fs = new DefaultFileSystem(new File(""));
    perspectives = mock(ResourcePerspectives.class);
  }

  @Test
  public void testShouldExecuteOnProject() throws Exception {
    Project project = mock(Project.class);

    AndroidLintSensor sensor = new AndroidLintSensor(settings, rulesProfile, perspectives, fs);
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    settings.setProperty(AndroidPlugin.LINT_REPORT_PROPERTY, AndroidPlugin.LINT_REPORT_PROPERTY_DEFAULT);
    sensor = new AndroidLintSensor(settings, rulesProfile, perspectives, fs);
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    settings.setProperty(AndroidPlugin.LINT_REPORT_PROPERTY, getClass().getResource("/lint-report.xml").getFile());
    sensor = new AndroidLintSensor(settings, rulesProfile, perspectives, fs);
    assertThat(sensor.shouldExecuteOnProject(project)).isTrue();

    settings.removeProperty(AndroidPlugin.LINT_REPORT_PROPERTY);
  }

  @Test
  public void analyse_should_raise_issue() throws Exception {
    rulesProfile = mock(RulesProfile.class);
    ActiveRule activeRule = mock(ActiveRule.class);
    when(activeRule.getRule()).thenReturn(org.sonar.api.rules.Rule.create("repoKey", "ruleKey"));
    when(rulesProfile.getActiveRule(anyString(), anyString())).thenReturn(activeRule);

    fs = new DefaultFileSystem(new File("")) {
      @Override
      public Iterable<InputFile> inputFiles(FilePredicate predicate) {
        return Lists.<InputFile>newArrayList(new DefaultInputFile("relativePath"));
      }
    };
    settings.setProperty(AndroidPlugin.LINT_REPORT_PROPERTY, "src/test/resources/lint-report.xml");
    AndroidLintSensor sensor = new AndroidLintSensor(settings, rulesProfile, perspectives, fs);
    sensor.analyse(mock(Project.class), mock(SensorContext.class));
    // Check we raise 30 issues on 21 different rules
    verify(rulesProfile, times(21)).getActiveRule(anyString(), anyString());
    verify(perspectives, times(30)).as(any(Class.class), any(InputPath.class));
  }



}
