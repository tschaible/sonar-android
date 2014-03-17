/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.android.emma;

import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.android.AndroidPlugin;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class AndroidEmmaSensorTest {

  @Test
  public void should_not_execute_if_report_directory_empty() {
    Project project = mock(Project.class);
    Settings settings = new Settings();
    settings.setProperty(AndroidPlugin.EMMA_REPORT_DIR_PROPERTY, "");
    DefaultFileSystem fs = new DefaultFileSystem();
    assertThat(new AndroidEmmaSensor(settings, fs).shouldExecuteOnProject(project)).isFalse();
  }

  @Test
  public void should_execute_if_report_directory_not_empty() {
    Project project = mock(Project.class);
    Settings settings = new Settings();
    settings.setProperty(AndroidPlugin.EMMA_REPORT_DIR_PROPERTY, "src/test/resources");
    DefaultFileSystem fs = new DefaultFileSystem();
    assertThat(new AndroidEmmaSensor(settings, fs).shouldExecuteOnProject(project)).isTrue();
  }

  @Test
  public void should_do_nothing_if_no_report() {
    Project project = mock(Project.class);
    ProjectFileSystem pfs = mock(ProjectFileSystem.class);
    when(pfs.resolvePath(anyString())).thenReturn(new File("Foo"));
    when(project.getFileSystem()).thenReturn(pfs);
    SensorContext context = mock(SensorContext.class);
    Settings settings = new Settings();
    settings.setProperty(AndroidPlugin.EMMA_REPORT_DIR_PROPERTY, "src/test/resources");
    DefaultFileSystem fs = new DefaultFileSystem();
    new AndroidEmmaSensor(settings, fs).analyse(project, context);
    verifyZeroInteractions(context);
  }

  @Test
  public void test_toString() {
    assertThat(new AndroidEmmaSensor(new Settings(), new DefaultFileSystem()).toString()).isEqualTo("AndroidEmmaSensor");
  }
}
