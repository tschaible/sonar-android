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

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.DefaultPosition;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Position;
import com.android.tools.lint.detector.api.Severity;
import org.codehaus.staxmate.in.SMEvent;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.XmlParserException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for the lint result xml file
 *
 * @author Jerome Van Der Linden
 * @author Thomas Bores
 */
public class AndroidLintParser {

  public static final int UNKNOWN_OFFSET = -1;
  public static final int UNKNOWN_LINE_OR_COLUMN = 0;
  public static final String TWO_NEW_LINES = "\n\n";
  public static final String BACK_SLASH = "\\";

  private List<Issue> issues;

  private Project project;

  AndroidLintParser(Project project)
  {
    this.project = project;
  }

  public AndroidLintParser parse(final File lintxmlFile) {

    try {
      StaxParser parser = new StaxParser(new StaxParser.XmlStreamHandler() {

        @Override
        public void stream(SMHierarchicCursor rootCursor) throws XMLStreamException {
          try {
            rootCursor.advance();
            collectIssues(rootCursor.descendantElementCursor("issue"));
          } catch (ParseException e) {
            throw new XMLStreamException(e);
          }
        }
      });

      parser.parse(lintxmlFile);

    } catch (XMLStreamException e) {
      throw new XmlParserException(e);
    }
    return this;
  }

  public List<Issue> getIssues() {
    return issues;
  }

  private void collectIssues(SMInputCursor issue) throws ParseException, XMLStreamException {

    issues = new ArrayList<Issue>();

    while (issue.getNext() != null) {
      String id = issue.getAttrValue("id");
      String severityStr = issue.getAttrValue("severity");
      String message = issue.getAttrValue("message");
      String errorLine1 = issue.getAttrValue("errorLine1");
      if (errorLine1 == null) {
        errorLine1 = "";
      }
      String errorLine2 = issue.getAttrValue("errorLine2");
      if (errorLine2 == null || errorLine2.contains("~")) {
        errorLine2 = "";
      }
      String categoryStr = issue.getAttrValue("category");
      int priority = Integer.valueOf(issue.getAttrValue("priority"));
      String summary = issue.getAttrValue("summary");
      String explanation = issue.getAttrValue("explanation");
      String url = issue.getAttrValue("url");

      Category category = Category.find(categoryStr);
      Severity severity = Severity.fromString(severityStr);

      List<Location> locations = collectLocations(issue);

      issues.add(Issue.create(id,
          message + TWO_NEW_LINES + errorLine1 + TWO_NEW_LINES + errorLine2,
          summary + TWO_NEW_LINES + explanation + TWO_NEW_LINES + url,
          category,
          priority,
          severity,
          locations));
    }
  }

  private List<Location> collectLocations(SMInputCursor issue) throws XMLStreamException {
    SMInputCursor locationXml = issue.descendantElementCursor("location");
    List<Location> locations = new ArrayList<Location>();

    SMEvent next;
    while ((next = locationXml.getNext()) != null) {
      if (!SMEvent.START_ELEMENT.equals(next)) {
        continue;
      }

      String filename = locationXml.getAttrValue("file");

      int line = UNKNOWN_LINE_OR_COLUMN;
      int column = UNKNOWN_LINE_OR_COLUMN;
      if (locationXml.getAttrCount() == 3) {
        line = locationXml.getAttrIntValue(1);
        column = locationXml.getAttrIntValue(2);
      }
      Position position = new DefaultPosition(line, column, UNKNOWN_OFFSET);

      Location location = null;

      String filenameAbsolutePath = project.getFileSystem().getBasedir() + BACK_SLASH + filename;

      if (filename.contains(".java"))
      {
        // The file is a java file
        // Java filename aren't directly support by the sonar java plugin
        // We shall transform the java file into a JavaKey
        // so the user can drill down the violations in java source code
        String javaKey = "";

        for (File dir : project.getFileSystem().getSourceDirs())
        {
          // Is the file in this source directory?
          if (filenameAbsolutePath.contains(dir.getPath()))
          {
            javaKey = javaFilenameToJavaKey(filename, dir);
          }
        }
        location = Location.create(new File(filename), position, position, javaKey);
      }
      else
      {
        // The file isn't a java file
        for (File dir : project.getFileSystem().getSourceDirs())
        {
          // Is the file in this source directory?
          if (filenameAbsolutePath.contains(dir.getPath()))
          {
            String srcPath = dir.getAbsolutePath().replace(project.getFileSystem().getBasedir().getPath(), "");
            srcPath = srcPath.substring(1) + BACK_SLASH;
            srcPath = filename.replace(srcPath, "");
          }
        }
        location = Location.create(new File(filename), position, position);
      }

      locations.add(location);
    }
    return locations;
  }

  /**
   * This function transform the Java filename into a JavaKey
   * @param filename the current Java filename as String
   * @param sourceDir the current source or test directory read from sonar.sources or sonar.tests
   * @return the javaKey. For instance com.package.MyClass
   */
  private String javaFilenameToJavaKey(String filename, File sourceDir)
  {
    // Get the source absolute path and remove the base path of it.
    String srcPath = sourceDir.getAbsolutePath().replace(project.getFileSystem().getBasedir().getPath(), "");
    // In most cases srcPath at this step srcPath is "\res" or "\src"
    // We change it in "res\" or "src\"
    srcPath = srcPath.substring(1) + BACK_SLASH;
    // Remove the source path from filename to get build the javakey

    return filename.replace(srcPath, "").replace(".java", "").replace(BACK_SLASH, ".");
  }

}
