package org.sonar.plugins.android.lint;

import org.apache.commons.collections.keyvalue.DefaultMapEntry;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.XmlParserException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.text.ParseException;
import java.util.Map;

/**
 * @author jva
 */
public class AndroidLintParserOld {

    public void parse(final Project project, final File xmlFile, final SensorContext sensorContext) {
        try {
            StaxParser parser = new StaxParser(new StaxParser.XmlStreamHandler() {

                @Override
                public void stream(SMHierarchicCursor rootCursor) throws XMLStreamException {
                    try {
                        rootCursor.advance();
                        collectIssues(project, rootCursor.descendantElementCursor("issue"), sensorContext);
                    } catch (ParseException e) {
                        throw new XMLStreamException(e);
                    }
                }
            });
            parser.parse(xmlFile);
        } catch (XMLStreamException e) {
            throw new XmlParserException(e);
        }
    }

    private void collectIssues(Project project, SMInputCursor issue, SensorContext sensorContext) throws ParseException, XMLStreamException {
        while (issue.getNext() != null) {

            Rule rule = evaluateRule(issue, sensorContext);

            Map.Entry<Resource, Integer> source = collectResource(project, issue.descendantElementCursor("location"));

            Violation violation = Violation.create(rule, source.getKey());
            violation.setLineId(source.getValue());

            sensorContext.saveViolation(violation);
        }
    }

    private Rule evaluateRule(SMInputCursor issue, SensorContext sensorContext) {
        return null;
    }

    private Map.Entry<Resource, Integer> collectResource(Project project, SMInputCursor location) throws XMLStreamException {

        String filename = location.getAttrValue("file");
        Integer line = location.getAttrIntValue(1); // may be null

        org.sonar.api.resources.File file = org.sonar.api.resources.File.fromIOFile(new File(filename), project);

        if (filename.endsWith("java")) {
            return new DefaultMapEntry(new JavaFile(filename), line);
        }

        return new DefaultMapEntry(file, line);
    }

}
