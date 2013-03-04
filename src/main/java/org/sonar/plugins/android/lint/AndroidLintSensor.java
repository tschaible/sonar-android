package org.sonar.plugins.android.lint;

import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Directory;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;

import java.io.File;
import java.util.List;

/**
 * @author jva
 */
public class AndroidLintSensor implements Sensor {

    // ----------------------------------
    // CONSTANTS
    // ----------------------------------
    private static final Logger logger = LoggerFactory.getLogger(AndroidLintSensor.class);

    // ----------------------------------
    // ATTRIBUTES
    // ----------------------------------
    private RuleFinder ruleFinder;

    // ----------------------------------
    // CONSTRUCTOR
    // ----------------------------------
    public AndroidLintSensor(RuleFinder ruleFinder) {
        this.ruleFinder = ruleFinder;
    }

    // ----------------------------------
    // PUBLIC
    // ----------------------------------
    @Override
    public void analyse(Project project, SensorContext sensorContext) {
        String path = (String) project.getProperty(AndroidLintConstants.ANDROID_LINT_REPORT_PATH_PROPERTY);

        if (path == null) {
            // wasn't configured - skip
            return;
        }

        File report = project.getFileSystem().resolvePath(path);
        if (!report.exists() || !report.isFile()) {
            logger.warn("Android Lint report not found at {}", report);
            return;
        }

        List<Issue> issues = parseReport(report);

        for (Issue issue : issues) {

            Rule rule = ruleFinder.findByKey(AndroidLintConstants.REPOSITORY_KEY, issue.getId());
            if (rule == null) {
                // ignore violations from report, if rule not activated in Sonar
                logger.warn("Android Lint rule '{}' not active in Sonar.", rule.getId());
                continue;
            }

            for (Location location : issue.getLocations()) {
                File file = location.getFile();
                Resource resource;

                if (file.isDirectory()) {
                    resource = new Directory(file.getPath());
                } else if (file.getName().endsWith("java")) {
                    resource = new JavaFile(file.getName(), false);
                } else {
                    resource = new org.sonar.api.resources.File(file.getName());
                }

                Violation violation = Violation.create(rule, resource);
                int line = location.getStart().getLine();
                violation.setLineId(line == AndroidLintParser.UNKNOWN_LINE_OR_COLUMN ? null : line);
                violation.setMessage(issue.getDescription());

                sensorContext.saveViolation(violation);
            }
        }
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return true;
    }

    // ----------------------------------
    // PRIVATE
    // ----------------------------------

    private List<Issue> parseReport(File report) {
        logger.info("parsing {}", report);
        AndroidLintParser parser = new AndroidLintParser();
        parser.parse(report);
        return parser.getIssues();
    }

    private RulePriority getSonarSeverityFromLintSeverity(Severity severityLint) {
        RulePriority severity;
        switch (severityLint) {
            case FATAL:
                severity = RulePriority.BLOCKER;
                break;
            case ERROR:
                severity = RulePriority.CRITICAL;
                break;
            case WARNING:
                severity = RulePriority.MAJOR;
                break;
            case INFORMATIONAL:
                severity = RulePriority.MINOR;
                break;
            case IGNORE:
                severity = RulePriority.INFO;
                break;
            default:
                severity = RulePriority.MAJOR;
        }
        return severity;
    }


}
