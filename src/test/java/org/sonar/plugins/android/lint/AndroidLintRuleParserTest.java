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

import junit.framework.Assert;
import org.junit.Test;
import org.sonar.api.rules.Rule;

import java.io.File;
import java.util.List;

/**
 * @author jva
 * @author Thomas Bores
 */
public class AndroidLintRuleParserTest {

    @Test
    public void testParseTextFile() throws Exception {
        // given
        AndroidLintRuleParser parser = new AndroidLintRuleParser();

        // when
        List<Rule> rules = parser.parse(new File(this.getClass().getResource("/org/sonar/plugins/android/lint/rules.txt").toURI()));

        // then
        Assert.assertNotNull(rules);
        // Update the number of rules after each Android Lint Update
        // because the number of rules change.
        Assert.assertEquals(140, rules.size());
    }
}
