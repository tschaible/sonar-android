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

import com.android.tools.lint.detector.api.Severity;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RuleQuery;

import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class AndroidLintProfileExporter extends ProfileExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(AndroidLintProfileExporter.class);

  private Collection<Rule> rules;

  /**
   * Constructor to be used on batch side as ProfileExporter is a batch extension and thus might not
   * be injected RuleFinder.
   */
  public AndroidLintProfileExporter() {
    super(AndroidLintRulesDefinition.REPOSITORY_KEY, AndroidLintRulesDefinition.REPOSITORY_NAME);
    rules = Lists.newArrayList();
  }

  public AndroidLintProfileExporter(RuleFinder ruleFinder) {
    super(AndroidLintRulesDefinition.REPOSITORY_KEY, AndroidLintRulesDefinition.REPOSITORY_NAME);
    rules = Lists.newArrayList();
    for (Rule rule : ruleFinder.findAll(RuleQuery.create().withRepositoryKey(AndroidLintRulesDefinition.REPOSITORY_KEY))) {
      if (!RuleStatus.REMOVED.equals(RuleStatus.valueOf(rule.getStatus()))) {
        rules.add(rule);
      }
    }
  }

  @Override
  public void exportProfile(RulesProfile profile, Writer writer) {
    Serializer serializer = new Persister();
    try {
      serializer.write(createLintProfile(profile.getActiveRules()), writer);
    } catch (Exception e) {
      LOGGER.error("Could not export lint profile", e);
    }
  }

  private LintProfile createLintProfile(List<ActiveRule> activeRules) {
    LintProfile profile = new LintProfile();
    Set<String> activeKeys = Sets.newHashSet();
    List<LintIssue> issues = Lists.newArrayList();
    for (ActiveRule rule : activeRules) {
      activeKeys.add(rule.getRuleKey());
    }
    for (Rule rule : rules) {
      String lintSeverity = getLintSeverity(rule.getSeverity(), activeKeys.contains(rule.getKey()));
      issues.add(new LintIssue(rule.getKey(), lintSeverity));
    }
    // ensure order of issues in output, sort by key.
    Collections.sort(issues, new Comparator<LintIssue>() {
      @Override
      public int compare(LintIssue o1, LintIssue o2) {
        return o1.id.compareTo(o2.id);
      }
    });
    profile.issues = issues;
    return profile;
  }

  @Root(name = "lint", strict = false)
  private static class LintProfile {
    @ElementList(inline = true)
    List<LintIssue> issues;
  }
  @Root(name = "issue", strict = false)
  private static class LintIssue {
    @Attribute
    String id;
    @Attribute
    String severity;

    public LintIssue(String ruleKey, String severity) {
      this.id = ruleKey;
      this.severity = severity;
    }
  }

  private String getLintSeverity(RulePriority severity, boolean isActive) {
    if(!isActive) {
      return Severity.IGNORE.getDescription();
    }
    String lintSeverity = "";
    if(severity.equals(RulePriority.BLOCKER)) {
      lintSeverity = Severity.FATAL.getDescription();
    }
    if(severity.equals(RulePriority.CRITICAL)) {
      lintSeverity = Severity.ERROR.getDescription();
    }
    if(severity.equals(RulePriority.MAJOR)) {
      lintSeverity = Severity.ERROR.getDescription();
    }
    if(severity.equals(RulePriority.MINOR)) {
      lintSeverity = Severity.WARNING.getDescription();
    }
    if(severity.equals(RulePriority.INFO)) {
      lintSeverity = Severity.INFORMATIONAL.getDescription();
    }

    return lintSeverity;
  }

}
