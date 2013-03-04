package org.sonar.plugins.android.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Severity;
import junit.framework.Assert;
import org.junit.Test;
import org.sonar.plugins.android.lint.AndroidLintParser;

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
