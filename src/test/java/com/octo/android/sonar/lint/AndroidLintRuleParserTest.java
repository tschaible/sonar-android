package com.octo.android.sonar.lint;

import junit.framework.Assert;
import org.junit.Test;
import org.sonar.api.rules.Rule;

import java.io.File;
import java.util.List;

/**
 * @author jva
 */
public class AndroidLintRuleParserTest {

    @Test
    public void testParseTextFile() throws Exception {
        // given
        AndroidLintRuleParser parser = new AndroidLintRuleParser();

        // when
        List<Rule> rules = parser.parse(new File(this.getClass().getResource("/com/octo/android/sonar/lint/rules.txt").toURI()));

        // then
        Assert.assertNotNull(rules);
        Assert.assertEquals(121, rules.size());
    }
}
