package com.octo.android.sonar.lint.api;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Severity;
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

    private List<Issue> issues;

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

    private void collectIssues(SMInputCursor issue) throws ParseException, XMLStreamException {

        issues = new ArrayList<Issue>();

        while (issue.getNext() != null) {
            String id = issue.getAttrValue("id");
            String severityStr = issue.getAttrValue("severity");
            String message = issue.getAttrValue("message");
            String categoryStr = issue.getAttrValue("category");
            int priority = Integer.valueOf(issue.getAttrValue("priority"));
            String summary = issue.getAttrValue("summary");
            String explanation = issue.getAttrValue("explanation");
            String url = issue.getAttrValue("url");

            Category category = Category.create(categoryStr, priority);
            Severity severity = Severity.fromString(severityStr);

            SMInputCursor locationXml = issue.descendantElementCursor("location");
            List<Location> locations = new ArrayList<Location>();
            if (locationXml.getNext() != null) { // TODO : may have several locations, but "while" cause an error
                locations.add(Location.create(new File(locationXml.getAttrValue("file"))));
            }

            issues.add(Issue.create(id, message, summary + "\n" + explanation, category, priority, severity, locations));
        }
    }

    public List<Issue> getIssues() {
        return issues;
    }
}
