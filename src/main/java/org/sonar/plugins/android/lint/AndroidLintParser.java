/*
 * Sonar Java
 * Copyright (C) 2012 SonarSource
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
 * Parser of the lint result xml file
 *
 * @author Jerome Van Der Linden
 */
public class AndroidLintParser {

    // ----------------------------------
    // CONSTANTS
    // ----------------------------------
    public static final int UNKNOWN_OFFSET = -1;
    public static final int UNKNOWN_LINE_OR_COLUMN = 0;

    // ----------------------------------
    // ATTRIBUTES
    // ----------------------------------
    private List<Issue> issues;

    // ----------------------------------
    // PUBLIC METHODS
    // ----------------------------------
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

    // ----------------------------------
    // PRIVATE
    // ----------------------------------

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
                    message + "\n\n" + errorLine1 + "\n\n" + errorLine2,
                    summary + "\n\n" + explanation + "\n\n" + url,
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
        while ((next = locationXml.getNext()) != null) { // TODO : may have several locations, but "while" cause an error
            if (SMEvent.START_ELEMENT.equals(next) == false) {
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

            Location location = Location.create(new File(filename), position, position);

            locations.add(location);
        }
        return locations;
    }
}
