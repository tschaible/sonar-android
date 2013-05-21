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

import com.android.tools.lint.detector.api.Severity;

/**
 * This class tests the Severity class
 *
 * @author Florian Roncari
 *
 */
public class SeverityTest {

  @Test
  public void testLocation() throws Exception {

    String mDisplay = "Fatal";
    Severity sev = Severity.fromString(mDisplay);

    Assert.assertEquals("Fatal", sev.getDescription());
  }
}
