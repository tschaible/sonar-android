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

import org.junit.Assert;
import org.junit.Test;

import com.android.tools.lint.detector.api.Category;

/**
 * Test the Category class
 *
 * @author Florian Roncari
 *
 */
public class CategoryTest {
  @Test
    public void testCreate() throws Exception {
      String name = "test";
      String explanation = "";
      int priority = 9;

      Category parent = null;
      Category category_parent = Category.create(parent, name,  explanation, priority);
      Category category = Category.create(category_parent, name,  explanation, priority);
      Category category_other = Category.create(category_parent, name,  explanation, 9);
      Category category_other2 = Category.create(category, name,  explanation, 9);

      Assert.assertEquals(category_parent,category.getParent());
      Assert.assertEquals("test",category.getName());
      Assert.assertEquals(null,category.getExplanation());
      Assert.assertEquals(Category.CORRECTNESS,Category.find("Correctness"));

      Assert.assertEquals("test:test",category.getFullName());
      Assert.assertEquals("test",category_parent.getFullName());
      Assert.assertEquals(1, category.compareTo(category_parent));
      Assert.assertEquals(0, category.compareTo(category_other));
      Assert.assertEquals(-1, category.compareTo(category_other2));
    }

}
