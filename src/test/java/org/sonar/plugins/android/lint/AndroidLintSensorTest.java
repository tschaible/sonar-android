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


import java.net.URISyntaxException;
import org.sonar.api.rules.RuleFinder;

import org.sonar.api.profiles.RulesProfile;

import org.junit.Test;
import org.junit.Before;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AndroidLintSensorTest {
//  SensorContext context;
//  Project project;
//  Settings settings;
//  AndroidLintSensor sensor;
//  AndroidLintConfiguration config;
//  RuleFinder ruleFinder;
//
//  @Before
//  public void setUp() throws URISyntaxException {
//    project = TestUtils.mockProject();
//
//    settings = new Settings();
//    settings.setProperty(AndroidLintConfiguration.ANDROID_LINT_REPORT_PATH_PROPERTY, "lint-report.xml");
//    settings.setProperty(AndroidLintConfiguration.ANDROID_LINT_PATH, "c:\\adt-bundle-windows-x86_64\\sdk\\tools\\lint.bat");
//
//    context = mock(SensorContext.class);
//    Resource resourceMock = mock(Resource.class);
//    when(context.getResource((Resource)anyObject())).thenReturn(resourceMock);
//    RulesProfile ruleProfileForTest = RulesProfile.create();
//
//    ruleFinder = mock(RuleFinder.class);
//    config = new AndroidLintConfiguration(settings);
//    sensor = new AndroidLintSensor(ruleFinder, config, ruleProfileForTest);
//
//  }
//
//  @Test
//  public void testSensor() throws Exception {
//    // sensor.analyse(project, context);
//  }
//

}
