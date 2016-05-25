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
import org.fest.assertions.Fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputPath;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AndroidLintProcessorTest {

  private ResourcePerspectives perspectives;
  private DefaultFileSystem fs;
  private RulesProfile rulesProfile;

  @Before
  public void setUp() throws Exception {
    // Setup mocks
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
  public void process_empty_report() throws Exception {
    // Process report
    try {
      new AndroidLintProcessor(rulesProfile, perspectives, fs).process(new File("src/test/resources/lint-report-empty.xml"));
    } catch (Exception e) {
      Fail.fail();
    }
    verify(rulesProfile, never()).getActiveRule(anyString(), anyString());
  }

  @Test
  public void process_report_with_relative_path() throws Exception {
    // Process report
    new AndroidLintProcessor(rulesProfile, perspectives, fs).process(new File("src/test/resources/lint-report.xml"));

    // Check we raise 30 issues on 21 different rules
    verify(rulesProfile, times(21)).getActiveRule(anyString(), anyString());
    verify(perspectives, times(30)).as(any(Class.class), any(InputPath.class));
  }

  @Test
  public void process_report_with_absolute_path() throws Exception {
    // Process report
    new AndroidLintProcessor(rulesProfile, perspectives, fs).process(new File("src/test/resources/lint-results_absolute_path.xml"));

    // Check we raise 8 issues on 8 different rules
    verify(rulesProfile, times(8)).getActiveRule(anyString(), anyString());
    verify(perspectives, times(8)).as(any(Class.class), any(InputPath.class));
  }

  @Test
  public void should_handle_bad_xml_results() throws Exception {
    new AndroidLintProcessor(rulesProfile, perspectives, fs).process(new File("src/test/resources/lint-bad-report.xml"));
    verify(rulesProfile, never()).getActiveRule(anyString(), anyString());
    verify(perspectives, never()).as(any(Class.class), any(InputPath.class));
  }

  @Test
  public void issuable_call() throws Exception {
    Issuable issuable = mock(Issuable.class);
    when(perspectives.as(any(Class.class), any(InputPath.class))).thenReturn(issuable);
    when(issuable.newIssueBuilder()).thenReturn( mock(Issuable.IssueBuilder.class, new SelfReturningAnswer()));

    new AndroidLintProcessor(rulesProfile, perspectives, fs).process(new File("src/test/resources/lint-report.xml"));

    verify(perspectives, times(30)).as(any(Class.class), any(InputPath.class));
    verify(issuable, times(30)).addIssue(any(Issue.class));
  }


  @Test
  public void unknown_issue_should_not_be_reported() throws Exception {
    when(rulesProfile.getActiveRule(anyString(), anyString())).thenReturn(null);
    new AndroidLintProcessor(rulesProfile, perspectives, fs).process(new File("src/test/resources/lint-unknown-rule-report.xml"));

    verify(perspectives, never()).as(any(Class.class), any(InputPath.class));

  }

  public class SelfReturningAnswer implements Answer<Object> {
    public Object answer(InvocationOnMock invocation) throws Throwable {
      Object mock = invocation.getMock();
      if( invocation.getMethod().getReturnType().isInstance( mock )){
        return mock;
      }
      else{
        return RETURNS_DEFAULTS.answer(invocation);
      }
    }
  }



}
