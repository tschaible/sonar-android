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

import org.junit.Before;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Resource;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Severity;
import junit.framework.Assert;
import org.junit.Test;
import org.sonar.api.resources.Project;
//import org.sonar.plugins.android.lint.TestUtils;

import java.io.File;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class test the AndroidLintParser class, which is responsible for reading
 * the lint-results.xml
 *
 * @author jva
 * @author Thomas Bores
 *
 */
public class AndroidLintParserTest {

  SensorContext context;
  Project project;
  Settings settings;

  @Before
  public void setUp() {
    project = TestUtils.mockProject();
    settings = new Settings();
    context = mock(SensorContext.class);
    Resource resourceMock = mock(Resource.class);
    when(context.getResource((Resource)anyObject())).thenReturn(resourceMock);
  }

  @Test
  public void testParser() throws Exception {

    AndroidLintParser parser = new AndroidLintParser(project);

    // when
    File filetoanalyze = new File(this.getClass().getResource("/lint-report.xml").toURI());
    parser.parse(filetoanalyze);
    List<Issue> issues = parser.getIssues();

    // then
    Assert.assertNotNull(issues);
    Assert.assertEquals(21, issues.size());

    // Test the first issue
    Issue firstIssue = issues.get(0);
    Assert.assertNotNull(firstIssue);
    Assert.assertEquals(Severity.ERROR, firstIssue.getSeverity());
    Assert.assertEquals(Category.CORRECTNESS, firstIssue.getCategory());
    Assert.assertEquals(8, firstIssue.getPriority());
    Assert.assertEquals("MissingRegistered", firstIssue.getId());

    List<Location> locations = firstIssue.getLocations();
    Assert.assertNotNull(locations);
    Assert.assertEquals(1, locations.size());
    Location location = locations.get(0);
    Assert.assertNotNull(location);
    Assert.assertEquals("AndroidManifest.xml", location.getFile().getName());
    Assert.assertEquals(64, location.getStart().getLine());
    Assert.assertEquals(9, location.getStart().getColumn());

    // Test the 11th issue, that has 2 locations
    Issue eleventhIssue = issues.get(10);
    Assert.assertNotNull(eleventhIssue);
    Assert.assertEquals(Severity.WARNING, eleventhIssue.getSeverity());
    Assert.assertEquals(Category.ICONS, eleventhIssue.getCategory());
    Assert.assertEquals(5, eleventhIssue.getPriority());
    Assert.assertEquals("IconDipSize", eleventhIssue.getId());

    locations = null;
    locations = eleventhIssue.getLocations();
    Assert.assertNotNull(locations);
    Assert.assertEquals(2, locations.size());

    location = locations.get(0);
    Assert.assertEquals("grey_background_pattern_tile.png", location
        .getFile().getName());

    location = locations.get(1);
    Assert.assertEquals("grey_background_pattern_tile.png", location
        .getFile().getName());

    // Test the 21th issue, that is a java violation
    Issue twentyfirst = issues.get(20);
    Assert.assertNotNull(twentyfirst);
    Assert.assertEquals(Severity.WARNING, twentyfirst.getSeverity());
    Assert.assertEquals(Category.CORRECTNESS, twentyfirst.getCategory());
    Assert.assertEquals(6, twentyfirst.getPriority());
    Assert.assertEquals("DefaultLocale", twentyfirst.getId());

    locations = null;
    locations = twentyfirst.getLocations();
    Assert.assertNotNull(locations);
    Assert.assertEquals(1, locations.size());

    location = locations.get(0);
    Assert.assertEquals("src\\com\\michaelnovakjr\\numberpicker\\NumberPicker.java", location.getFile().getName());
  }
}
