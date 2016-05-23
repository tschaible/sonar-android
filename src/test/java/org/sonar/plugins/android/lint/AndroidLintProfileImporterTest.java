/*
 * SonarQube Android Lint Plugin
 * Copyright (C) 2013-2016 SonarSource SA  and Jerome Van Der Linden, Stephane Nicolas, Florian Roncari, Thomas Bores
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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AndroidLintProfileImporterTest {

  private ValidationMessages messages;
  private RuleFinder ruleFinder;

  @Before
  public void setUp() throws Exception {
    ruleFinder = Mockito.mock(RuleFinder.class);
    when(ruleFinder.findByKey(any(RuleKey.class))).then(new Answer<Rule>() {
      @Override
      public Rule answer(InvocationOnMock invocationOnMock) throws Throwable {
        RuleKey ruleKey = (RuleKey) invocationOnMock.getArguments()[0];
        return Rule.create(ruleKey.repository(), ruleKey.rule(), ruleKey.rule());
      }
    });
    messages = ValidationMessages.create();
  }

  @Test
  public void should_import_profile() throws Exception {
    RulesProfile rulesProfile = createRuleProfile("src/test/resources/importer/lint.xml");
    assertThat(messages.hasErrors()).as("No error messages expected : " + messages.getErrors()).isFalse();
    assertThat(messages.hasWarnings()).isFalse();
    assertThat(messages.hasInfos()).isFalse();
    assertThat(rulesProfile.getActiveRules()).hasSize(13);
  }

  @Test
  public void should_report_errors_on_bad_profiles() throws Exception {
    RulesProfile rulesProfile = createRuleProfile("src/test/resources/importer/lint-severity.xml");
    assertThat(messages.hasErrors()).as("No error messages expected : " + messages.getErrors()).isFalse();
    assertThat(messages.getWarnings()).hasSize(2);
    assertThat(messages.hasInfos()).isFalse();
    assertThat(rulesProfile.getActiveRules()).hasSize(5);
  }

  @Test
  public void bad_lint_xml_should_not_work() throws Exception {
    RulesProfile rulesProfile = createRuleProfile("src/test/resources/importer/bad-lint.xml");
    assertThat(messages.getErrors()).hasSize(1);
    assertThat(messages.getWarnings()).isEmpty();
    assertThat(messages.hasInfos()).isFalse();
    assertThat(rulesProfile.getActiveRules()).isEmpty();
  }

  @Test
  public void should_handle_unknown_issues() throws Exception {
    RuleFinder mockFinder = mock(RuleFinder.class);
    when(mockFinder.findByKey(any(RuleKey.class))).thenReturn(null);
    RulesProfile rulesProfile = new AndroidLintProfileImporter(mockFinder).importProfile(new FileReader("src/test/resources/importer/lint-unknown-rule.xml"), messages);
    assertThat(messages.getWarnings()).containsExactly("Rule !FooBarUnknown! is unknown and has been skipped");
    assertThat(messages.hasErrors()).isFalse();
    assertThat(messages.hasInfos()).isFalse();
    assertThat(rulesProfile.getActiveRules()).isEmpty();
  }

  private RulesProfile createRuleProfile(String lintFileName) throws FileNotFoundException {
    Reader reader = new FileReader(lintFileName);
    return new AndroidLintProfileImporter(ruleFinder).importProfile(reader, messages);
  }
}