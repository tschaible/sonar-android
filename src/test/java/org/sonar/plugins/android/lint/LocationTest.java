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

import org.sonar.api.resources.Project;

import org.junit.Before;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Resource;

import java.io.File;

import static org.junit.Assert.*;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import junit.framework.Assert;

import org.junit.Test;

import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Position;

/**
 * This class tests the Location class
 *
 * @author Florian Roncari
 *
 */
public class LocationTest {

  SensorContext context;
  Project project;
  Settings settings;


  @Test
    public void testLocation() throws Exception {

      File mFile = null;
      String mJavaKey = "key";
      Position mStart = null;
      Position mEnd = null;;

      Location loc = Location.create(mFile,mStart, mEnd);
      Assert.assertEquals(null,loc.getFile());
      Assert.assertEquals(null,loc.getStart());
      Assert.assertEquals(null,loc.getEnd());

      Location loc2 = Location.create(mFile, mStart, mEnd, mJavaKey);
      Assert.assertEquals("key",loc2.getJavaKey());
      loc.setSecondary(loc2);
      Assert.assertEquals(loc2,loc.getSecondary());
      loc.setMessage("Test");
      Assert.assertEquals("Test",loc.getMessage());
      Assert.assertEquals("Location [file=" + mFile + ", start=" + mStart + ", end=" + mEnd + ", message="
          + "Test" + "]",loc.toString());

      File filetoanalyze = new File(this.getClass().getResource("/lint-report.xml").toURI());
      Location loc3 = Location.create(filetoanalyze);
      Location loc4 = Location.create(filetoanalyze,"test",0,10);

      try {
        Location loc5 = Location.create(filetoanalyze,"test",-2,10);
        fail("Invalid offsets");
        } catch (IllegalArgumentException e) {
        //Do nothing
        }

      try {
        Location loc6 = Location.create(filetoanalyze,"test",8,4);
        fail("Invalid offsets");
        } catch (IllegalArgumentException e) {
        //Do nothing
        }
        Location loc6 = Location.create(filetoanalyze,null,0,4);
        Location loc7 = Location.create(filetoanalyze,"Test \n Tests",0,10);
  }



}
