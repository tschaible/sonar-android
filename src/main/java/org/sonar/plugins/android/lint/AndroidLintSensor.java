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

import org.sonar.api.utils.command.StreamConsumer;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.utils.command.CommandExecutor;

import org.sonar.api.utils.command.Command;

import org.sonar.api.profiles.RulesProfile;

import org.sonar.api.resources.Java;

import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is the one that will be executed by the sonar client.
 * It is responsible for executing the Android Lint tool and launch the parser on the generated report.
 *
 * @author Jerome Van Der Linden
 * @author Thomas Bores
 */
public class AndroidLintSensor implements Sensor {

  private static final Logger logger = LoggerFactory.getLogger(AndroidLintSensor.class);

  /**
   * TIMEOUT for the lint command.
   * Default is 5 minutes
   */
  private static final long LINT_TIMEOUT = 300000;

  private RuleFinder ruleFinder;
  private AndroidLintConfiguration conf;
  private RulesProfile profile;

  public AndroidLintSensor(RuleFinder ruleFinder, AndroidLintConfiguration conf, RulesProfile profile) {
    this.ruleFinder = ruleFinder;
    this.conf = conf;
    this.profile = profile;
  }

  @Override
  public void analyse(Project project, SensorContext sensorContext) {
    // First of all, run Android-Lint on the project
    runAndroidLint(project);

    // Then parse the Android-Lint results
    String path = conf.getAndroidlintReportPath(project);

    File report = project.getFileSystem().resolvePath(path);
    if (!report.exists() || !report.isFile()) {
      logger.warn("Android Lint report not found at {}", report);
      return;
    }

    // Parse the xml report
    List<Issue> issues = parseReport(project, report);

    // Create the violations in Sonar
    for (Issue issue : issues) {
      Rule rule = ruleFinder.findByKey(AndroidLintRuleRepository.REPOSITORY_KEY, issue.getId());
      if (rule != null)
      {
        if (rule.isEnabled())
        {
          for (Location location : issue.getLocations()) {

            Violation violation = null;

            // The file concerned is a java one
            if (location.getJavaKey() != null)
            {
              JavaFile javaFile = new JavaFile(location.getJavaKey());
              violation = Violation.create(rule, javaFile);
            }
            else
            {
              org.sonar.api.resources.File resFile = new org.sonar.api.resources.File(location.getFile().getName());
              violation = Violation.create(rule, resFile);
            }

            int line = location.getStart().getLine();
            if (line != AndroidLintParser.UNKNOWN_LINE_OR_COLUMN) {
              violation.setLineId(line);
            }

            violation.setMessage(issue.getCategory().getFullName() + "\n" + issue.getDescription());
            sensorContext.saveViolation(violation);
          }
        }
        else
        {
          logger.warn("Android Lint rule '{}' not active in Sonar.", issue.getId());
        }
      }
      else
      {
        // ignore violations from report, if rule not activated in Sonar
        logger.warn("Android Lint rule '{}' is unknown in Sonar", issue.getId());
      }
    }
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return Java.KEY.equals(project.getLanguageKey())
      && !profile.getActiveRulesByRepository(AndroidLintRuleRepository.REPOSITORY_KEY).isEmpty();
  }

  private List<Issue> parseReport(Project project, File report) {
    logger.info("parsing {}", report);
    AndroidLintParser parser = new AndroidLintParser(project);
    return parser.parse(report).getIssues();
  }

  /**
   * This function try to start the external Android-Lint tool and
   * to generate a xml report containing the Android-Lint violations.
   * This function has been inspired by the one in PylintSensor.java
   * in the sonar-python-plugin.
   *
   */
  private void runAndroidLint(Project project)
  {
    String[] lintArgs = {"--xml", conf.getAndroidlintReportPath(project)};

    // The command should look like:
    // lint --xml <path_where_to_save_results>/lint-results.xml <project_root_path>
    Command command = Command.create(conf.getAndroidLintPath()).addArguments(lintArgs).addArgument(project.getFileSystem().getBasedir().getAbsolutePath());

    // Display the command string in sonar-runner
    // It will helps the user in case his parameters aren't right configured.
    logger.info("Calling command: '{}'", command.toString());

    MyStreamConsumer stdOut = new MyStreamConsumer();
    MyStreamConsumer stdErr = new MyStreamConsumer();
    CommandExecutor.create().execute(command, stdOut, stdErr, LINT_TIMEOUT);

    if (stdErr.getData().size() > 1) {
      logger.warn("Output on the error channel detected: this is probably due to a problem on android lint side.");
      logger.warn("Content of the error stream: \n\"{}\"", StringUtils.join(stdErr.getData(), "\n"));
    }
  }

  private static class MyStreamConsumer implements StreamConsumer {
    private List<String> data = new LinkedList<String>();

    public void consumeLine(String line) {
      data.add(line);
    }

    public List<String> getData() {
      return data;
    }
  }
}
