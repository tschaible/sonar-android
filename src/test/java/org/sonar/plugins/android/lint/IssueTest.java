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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Severity;

/**
 * This class tests the Issue class
 *
 * @author Florian Roncari
 *
 */
public class IssueTest {

  @Test
  public void testIssue() throws Exception {
    // given
    String id = "test";
    String description = "Test";
    String explanation = "TEST";
    Category category = null;
    int priority = 9;
    Severity severity = null;
    String moreinfo = "TestIssue";
    List<Location> locations = null;

    Issue issue = Issue.create(id, description, explanation, category, priority, severity, locations);

    Issue issue_test = Issue.create(id, description, explanation, category, priority, severity, locations);
    issue_test.setMoreInfo(moreinfo);

    Issue issue_test2 = Issue.create(id, description, explanation, category, priority, severity, locations);
    issue_test2.setMoreInfo(moreinfo);
    issue_test2.setEnabledByDefault(true);

    // then
    // Assert.assertEquals(issue, Issue.create(id, description, explanation, category, priority, severity, locations));
    Assert.assertEquals("test", issue.getId());
    Assert.assertEquals("Test", issue.getDescription());
    Assert.assertEquals("TEST", issue.getExplanation());
    Assert.assertEquals(null, issue.getCategory());
    Assert.assertEquals(9, issue.getPriority());
    Assert.assertEquals(null, issue.getSeverity());
    Assert.assertEquals(null, issue.getLocations());
    Assert.assertEquals(true, issue_test2.isEnabledByDefault());
    Assert.assertEquals(0, issue_test2.compareTo(issue_test2));
    Assert.assertEquals("test", issue.toString());
    // test on setmoreinfo and getmoreinfo
    // Assert.assertEquals(issue_test,issue.setMoreInfo(moreinfo));
    issue.setMoreInfo(moreinfo);
    Assert.assertEquals("TestIssue", issue_test2.getMoreInfo());

    // Assert.assertEquals(issue_test2,issue.setEnabledByDefault(true));
    issue.setEnabledByDefault(true);
  }
}
