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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;

import java.io.File;
import java.util.List;

public class AndroidLintProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AndroidLintProcessor.class);
  private final RulesProfile profile;
  private final ResourcePerspectives perspectives;
  private final FileSystem fs;

  public AndroidLintProcessor(RulesProfile profile, ResourcePerspectives perspectives, FileSystem fs) {
    this.profile = profile;
    this.perspectives = perspectives;
    this.fs = fs;
  }

  public void process(File lintXml) {
    Serializer serializer = new Persister();
    try {
      LOGGER.info("Processing android lint report: "+lintXml.getPath());
      LintIssues lintIssues = serializer.read(LintIssues.class, lintXml);
      for (LintIssue lintIssue : lintIssues.issues) {
        processIssue(lintIssue);
      }
    } catch (Exception e) {
      LOGGER.error("Exception reading " + lintXml.getPath(), e);
    }
  }

  private void processIssue(LintIssue lintIssue) {
    ActiveRule rule = profile.getActiveRule(AndroidLintRulesDefinition.REPOSITORY_KEY, lintIssue.id);
    if (rule != null) {
      LOGGER.debug("Processing Issue: {}", lintIssue.id);
      for (LintLocation lintLocation : lintIssue.locations) {
        processIssueForLocation(rule, lintIssue, lintLocation);
      }
    } else {
      LOGGER.warn("Unable to find rule for {}", lintIssue.id);
    }
  }

  private void processIssueForLocation(ActiveRule rule, LintIssue lintIssue, LintLocation lintLocation) {
    InputFile inputFile = fs.inputFile(fs.predicates().hasPath(lintLocation.file));
    if (inputFile != null) {
      LOGGER.debug("Processing File {} for Issue {}", lintLocation.file, lintIssue.id);
      Issuable issuable = perspectives.as(Issuable.class, inputFile);
      if (issuable != null) {
        Issue issue = issuable.newIssueBuilder()
          .ruleKey(rule.getRule().ruleKey())
            .message(lintIssue.message)
            .line(lintLocation.line)
            .build();
        issuable.addIssue(issue);
        return;
      }
    }
    LOGGER.warn("Unable to find file {} to report issue", lintLocation.file);
  }

  @Root(name = "location", strict = false)
  private static class LintLocation {
    @Attribute
    String file;
    @Attribute(required = false)
    Integer line;
  }

  @Root(name = "issues", strict = false)
  private static class LintIssues {
    @ElementList(required = false, inline = true, empty = false)
    List<LintIssue> issues;
  }

  @Root(name = "issue", strict = false)
  private static class LintIssue {
    @Attribute
    String id;
    @Attribute
    String message;
    @ElementList(inline = true)
    List<LintLocation> locations;
  }

}
