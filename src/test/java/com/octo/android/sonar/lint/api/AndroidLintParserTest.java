package com.octo.android.sonar.lint.api;

import com.android.tools.lint.detector.api.Issue;
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
    }

}
