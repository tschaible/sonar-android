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

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Severity;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @author jva
 */
public class AndroidLintParserTest {

    @Test
    public void testParser() throws Exception{
        // given
        AndroidLintParser parser = new AndroidLintParser();

        // when
        parser.parse(new File(this.getClass().getResource("/lint-report.xml").toURI()));
        List<Issue> issues = parser.getIssues();

        // then
        Assert.assertNotNull(issues);
        Assert.assertEquals(20, issues.size());

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

    }

}
