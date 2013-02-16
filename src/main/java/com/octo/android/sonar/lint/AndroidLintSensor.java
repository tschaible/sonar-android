package com.octo.android.sonar.lint;

import com.android.tools.lint.detector.api.Issue;
import com.octo.android.sonar.lint.api.AndroidLintParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RuleFinder;

import java.io.File;
import java.util.List;

/**
 * @author jva
 */
public class AndroidLintSensor implements Sensor {

    private static final Logger logger = LoggerFactory.getLogger(AndroidLintSensor.class);

    private RuleFinder ruleFinder;


    public AndroidLintSensor(RuleFinder ruleFinder) {
        this.ruleFinder = ruleFinder;
    }

    @Override
    public void analyse(Project project, SensorContext sensorContext) {
        String path = (String) project.getProperty(AndroidLintPlugin.ANDROID_LINT_REPORT_PATH_PROPERTY);

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

        // TODO : create Violations from Issues (see AndroidLintParserOld) and Rules (voir comment fonctionne le RuleFinder)
    }

    private List<Issue> parseReport(File report) {
        logger.info("parsing {}", report);
        AndroidLintParser parser = new AndroidLintParser();
        parser.parse(report);
        return parser.getIssues();
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return true;
    }
}
