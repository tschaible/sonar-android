package com.octo.android.sonar.lint;

import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.Logs;

import java.io.File;

/**
 * @author jva
 */
public class AndroidLintSensor implements Sensor {

    @Override
    public void analyse(Project project, SensorContext sensorContext) {
        String path = (String) project.getProperty(AndroidLintPlugin.ANDROID_LINT_REPORT_PATH_PROPERTY);

        if (path == null) {
            // wasn't configured - skip
            return;
        }

        File report = project.getFileSystem().resolvePath(path);
        if (!report.exists() || !report.isFile()) {
            Logs.INFO.warn("Android Lint report not found at {}", report);
            return;
        }
        parseReport(project, report, sensorContext);

    }

    private void parseReport(Project project, File report, SensorContext sensorContext) {
        LoggerFactory.getLogger(AndroidLintSensor.class).info("parsing {}", report);


    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return false;
    }
}
