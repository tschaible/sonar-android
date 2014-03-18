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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.android.AndroidPlugin;

import java.io.File;
import java.net.URL;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class AndroidEmmaSensorTest {

  private Project project;
  private Settings settings;
  private SensorContext context;
  private ProjectFileSystem pfs;

  @Before
  public void setUp() throws Exception {
    project = mock(Project.class);
    pfs = mock(ProjectFileSystem.class);
    settings = new Settings();
    context = mock(SensorContext.class);
  }

  @Test
  public void should_not_execute_if_report_directory_empty() {
    settings.setProperty(AndroidPlugin.EMMA_REPORT_DIR_PROPERTY, "");
    assertThat(new AndroidEmmaSensor(settings, new DefaultFileSystem()).shouldExecuteOnProject(project)).isFalse();
  }

  @Test
  public void should_execute_if_report_directory_not_empty() {
    settings.setProperty(AndroidPlugin.EMMA_REPORT_DIR_PROPERTY, "src/test/resources");
    assertThat(new AndroidEmmaSensor(settings, new DefaultFileSystem()).shouldExecuteOnProject(project)).isTrue();
  }

  @Test
  public void should_do_nothing_if_no_report() {
    when(pfs.resolvePath(anyString())).thenReturn(new File("Foo"));
    when(project.getFileSystem()).thenReturn(pfs);
    settings.setProperty(AndroidPlugin.EMMA_REPORT_DIR_PROPERTY, "src/test/resources");
    new AndroidEmmaSensor(settings, new DefaultFileSystem()).analyse(project, context);
    verifyZeroInteractions(context);
  }

  @Test
  public void should_process_emma_reports() throws Exception {
    when(pfs.resolvePath(anyString())).thenReturn(new File(getClass().getResource("/emma").getFile()));
    when(project.getFileSystem()).thenReturn(pfs);
    settings.setProperty(AndroidPlugin.EMMA_REPORT_DIR_PROPERTY, "src/test/resources");
    DefaultFileSystem fs = new DefaultFileSystem();
    new AndroidEmmaSensor(settings, fs).analyse(project, context);
    verify(context, times(4)).saveMeasure(any(InputFile.class), any(Metric.class), anyDouble());
  }

  @Test
  public void test_toString() {
    assertThat(new AndroidEmmaSensor(new Settings(), new DefaultFileSystem()).toString()).isEqualTo("AndroidEmmaSensor");
  }
}
