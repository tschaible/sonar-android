/*
 * SonarQube Android Lint Plugin
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
package org.sonar.plugins.android.lint;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputPath;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AndroidLintProcessorTest {

  private ResourcePerspectives perspectives;
  private DefaultFileSystem fs;
  private RulesProfile rulesProfile;

  @Before
  public void setUp() throws Exception {
    //Setup mocks
    rulesProfile = mock(RulesProfile.class);
    ActiveRule activeRule = mock(ActiveRule.class);
    when(activeRule.getRule()).thenReturn(Rule.create("repoKey", "ruleKey"));
    when(rulesProfile.getActiveRule(anyString(), anyString())).thenReturn(activeRule);

    fs = new DefaultFileSystem(new File("")) {
      @Override
      public Iterable<InputFile> inputFiles(FilePredicate predicate) {
        return Lists.<InputFile>newArrayList(new DefaultInputFile("relativePath"));
      }
    };
    perspectives = mock(ResourcePerspectives.class);
  }

  @Test
  public void process_report_with_relative_path() throws Exception {
    //Process report
    new AndroidLintProcessor(rulesProfile, perspectives, fs).process(new File("src/test/resources/lint-report.xml"));

    //Check we raise 30 issues on 21 different rules
    verify(rulesProfile, times(21)).getActiveRule(anyString(), anyString());
    verify(perspectives, times(30)).as(any(Class.class), any(InputPath.class));
  }

  @Test
  public void process_report_with_absolute_path() throws Exception {
    //Process report
    new AndroidLintProcessor(rulesProfile, perspectives, fs).process(new File("src/test/resources/lint-results_absolute_path.xml"));

    //Check we raise 8 issues on 8 different rules
    verify(rulesProfile, times(8)).getActiveRule(anyString(), anyString());
    verify(perspectives, times(8)).as(any(Class.class), any(InputPath.class));
  }
}