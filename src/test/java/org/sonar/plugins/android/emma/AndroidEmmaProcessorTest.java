/*
 * SonarQube Android Plugin
 * Copyright (C) 2013 SonarSource and Jerome Van Der Linden, Stephane Nicolas, Florian Roncari, Thomas Bores
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
package org.sonar.plugins.android.emma;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.measures.CoreMetrics;

import java.io.File;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AndroidEmmaProcessorTest {

  @Test
  public void process_should_read_emma_files() throws Exception {
    File dir = new File(getClass().getResource("/").getFile());

    SensorContext context = mock(SensorContext.class);
    DefaultFileSystem fs = new DefaultFileSystem();
    DefaultInputFile buildConfig = new DefaultInputFile("org/example/BuildConfig.java");
    buildConfig.setLanguage("java");
    fs.add(buildConfig);
    DefaultInputFile exampleActivity = new DefaultInputFile("org/example/ExampleActivity.java");
    exampleActivity.setLanguage("java");
    fs.add(exampleActivity);
    new AndroidEmmaProcessor(dir, fs, context).process();

    verify(context).saveMeasure(argThat(new MatchInputFile(exampleActivity.relativePath())),
        eq(CoreMetrics.LINES_TO_COVER),
        eq(7d));
    verify(context).saveMeasure(argThat(new MatchInputFile(exampleActivity.relativePath())),
        eq(CoreMetrics.UNCOVERED_LINES),
        eq(1d));
    verify(context).saveMeasure(argThat(new MatchInputFile(buildConfig.relativePath())),
        eq(CoreMetrics.LINES_TO_COVER),
        eq(1d));
    verify(context).saveMeasure(argThat(new MatchInputFile(buildConfig.relativePath())),
        eq(CoreMetrics.UNCOVERED_LINES),
        eq(1d));


  }


  private static class MatchInputFile extends BaseMatcher<InputFile> {

    private String relativePath;

    private MatchInputFile(String relativePath) {
      this.relativePath = relativePath;
    }

    @Override
    public boolean matches(Object o) {
      if (o instanceof InputFile) {
        return ((InputFile) o).relativePath().equals(relativePath);
      }
      return false;
    }

    @Override
    public void describeTo(Description description) {

    }
  }
}
