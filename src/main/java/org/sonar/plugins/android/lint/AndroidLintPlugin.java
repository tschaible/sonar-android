/*
 * Sonar Android Plugin
 * Copyright (C) 2013 Jerome Van Der Linden, Stephane Nicolas and SonarSource
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

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;


/**
 * Main class of Android Lint Sonar Plugin
 *
 * @author Jerome Van Der Linden
 */
@Properties({
        @Property(
                key = AndroidLintConstants.ANDROID_LINT_REPORT_PATH_PROPERTY,
                name = "Report path",
                description = "Path (absolute or relative) to Android lint xml report file.",
                project = true,
                global = false)
})
public class AndroidLintPlugin extends SonarPlugin {

    @Override
    public List getExtensions() {
        return Arrays.asList(
                AndroidLintSensor.class,
                AndroidLintRuleRepository.class,
                AndroidLintProfile.class,
                AndroidLintProfileImporter.class
        );

    }
}
