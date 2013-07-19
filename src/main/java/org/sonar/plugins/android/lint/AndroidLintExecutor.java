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

import com.android.tools.lint.LintCliXmlParser;
import com.android.tools.lint.LombokParser;
import com.android.tools.lint.checks.BuiltinIssueRegistry;
import com.android.tools.lint.client.api.IDomParser;
import com.android.tools.lint.client.api.IJavaParser;
import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.client.api.LintClient;
import com.android.tools.lint.client.api.LintDriver;
import com.android.tools.lint.client.api.LintRequest;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Issue.OutputFormat;
import com.android.tools.lint.detector.api.LintUtils;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Severity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.JavaPackage;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AndroidLintExecutor extends LintClient implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(AndroidLintExecutor.class);
  private ProjectFileSystem fs;
  private SensorContext sensorContext;
  private RuleFinder ruleFinder;

  public AndroidLintExecutor(RuleFinder ruleFinder, ProjectFileSystem fs) {
    this.ruleFinder = ruleFinder;
    this.fs = fs;
  }

  public void execute(SensorContext sensorContext) {
    this.sensorContext = sensorContext;
    IssueRegistry registry = new BuiltinIssueRegistry();
    LintDriver driver = new LintDriver(registry, this);

    driver.analyze(new LintRequest(this, Arrays.asList(fs.getBasedir())));
  }

  @Override
  public void report(Context context, Issue issue, Severity severity, Location location, String message, Object data) {
    Rule rule = ruleFinder.findByKey(AndroidLintRuleRepository.REPOSITORY_KEY, issue.getId());
    if (rule != null) {
      if (rule.isEnabled()) {
        Violation violation = null;

        Resource<?> r = fs.toResource(location.getFile());
        if (r != null) {
          if (r instanceof org.sonar.api.resources.File && r.getKey().endsWith(".java")) {
            // The file concerned is a java one
            JavaFile javaFile = new JavaFile(StringUtils.removeEnd(r.getKey(), ".java").replaceAll("/", "."));
            violation = Violation.create(rule, javaFile);
          }
          else if (r instanceof org.sonar.api.resources.Directory) {
            // The folder concerned is a java package
            JavaPackage javaPackage = new JavaPackage(r.getKey().replaceAll("/", "."));
            violation = Violation.create(rule, javaPackage);
          }
          else {
            // Any other file located in sonar.sources folder
            violation = Violation.create(rule, r);
          }
        }
        else {
          // Any other file/folder located outside sonar.sources folder
          org.sonar.api.resources.File resFile = new org.sonar.api.resources.File(location.getFile().getName());
          violation = Violation.create(rule, resFile);
        }

        int line = location.getStart() != null ? location.getStart().getLine() + 1 : 0;
        if (line > 0) {
          violation.setLineId(line);
        }

        violation.setMessage(issue.getBriefDescription(OutputFormat.TEXT));
        sensorContext.saveViolation(violation);
      }
      else {
        LOG.warn("Android Lint rule '{}' not active in Sonar.", issue.getId());
      }
    }
    else {
      // ignore violations from report, if rule not activated in Sonar
      LOG.warn("Android Lint rule '{}' is unknown in Sonar", issue.getId());
    }
  }

  @Override
  public void log(Severity severity, Throwable exception, String format, Object... args) {
    String msg = null;
    if (format != null) {
      msg = String.format(format, args);
    }
    switch (severity) {
      case FATAL:
      case ERROR:
        LOG.error(msg, exception);
        break;
      case WARNING:
        LOG.warn(msg, exception);
        break;
      case INFORMATIONAL:
        LOG.info(msg, exception);
        break;
      case IGNORE:
        // Do nothing
    }

  }

  @Override
  public IDomParser getDomParser() {
    return new LintCliXmlParser();
  }

  @Override
  public IJavaParser getJavaParser() {
    return new LombokParser();
  }

  @Override
  public String readFile(File file) {
    try {
      return LintUtils.getEncodedString(this, file);
    } catch (IOException e) {
      return ""; //$NON-NLS-1$
    }
  }
}
