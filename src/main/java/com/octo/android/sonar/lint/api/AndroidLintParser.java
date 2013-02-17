package com.octo.android.sonar.lint.api;

import com.android.tools.lint.detector.api.*;
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
 * @author jva
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
    public void parse(final File lintxmlFile) {

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
            String errorLine2 = issue.getAttrValue("errorLine2");
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
