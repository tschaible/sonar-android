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

import com.android.tools.lint.detector.api.Severity;
import org.apache.commons.lang.StringUtils;
import org.codehaus.staxmate.in.SMEvent;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.resources.Java;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.api.utils.XmlParserException;

import javax.xml.stream.XMLStreamException;
import java.io.Reader;
import java.text.ParseException;
import java.util.List;

/**
 * Importer of Android Lint profile. By default contains all rules.
 *
 * @author Jerome Van Der Linden
 */
public class AndroidLintProfileImporter extends ProfileImporter {

    // ----------------------------------
    // CONSTANTS
    // ----------------------------------

    private static final Logger logger = LoggerFactory.getLogger(AndroidLintRuleParser.class);

    // ----------------------------------
    // ATTRIBUTES
    // ----------------------------------

    private RuleFinder ruleFinder;
    private RulesProfile profile;
    private XMLProfileParser xmlProfileParser;

    // ----------------------------------
    // CONSTRUCTORS
    // ----------------------------------

    public AndroidLintProfileImporter(RuleFinder ruleFinder, XMLProfileParser xmlProfileParser) {
        super(AndroidLintConstants.REPOSITORY_KEY, AndroidLintConstants.PLUGIN_NAME);
        setSupportedLanguages(Java.KEY);
        this.ruleFinder = ruleFinder;
        this.xmlProfileParser = xmlProfileParser;
    }

    // ----------------------------------
    // PUBLIC
    // ----------------------------------

    @Override
    public RulesProfile importProfile(Reader reader, ValidationMessages messages) {
        // import default profile with all rules
        profile = xmlProfileParser.parseResource(getClass().getClassLoader(), "org/sonar/plugins/android/lint/profile-android-lint.xml", messages);

        if (profile == null) {
            messages.addErrorText("Unable to load default profile");
            return null;
        }

        // filter with the input profile from reader
        parse(reader, messages);

        return profile;
    }

    public void parse(Reader reader, final ValidationMessages messages) {
        try {
            StaxParser parser = new StaxParser(new StaxParser.XmlStreamHandler() {

                @Override
                public void stream(SMHierarchicCursor rootCursor) throws XMLStreamException {
                    try {
                        rootCursor.advance();
                        collectRules(rootCursor.descendantElementCursor("issue"), messages);
                    } catch (ParseException e) {
                        throw new XMLStreamException(e);
                    }
                }
            });

            parser.parse(reader);

        } catch (XMLStreamException e) {
            throw new XmlParserException(e);
        }
    }

    // ----------------------------------
    // PRIVATE
    // ----------------------------------

    private void collectRules(SMInputCursor issue, ValidationMessages messages) throws ParseException, XMLStreamException {
        logger.info("Collecting rules");

        List<ActiveRule> activeRules = profile.getActiveRules();

        SMEvent next;
        while ((next = issue.getNext()) != null) {

            if (SMEvent.START_ELEMENT.equals(next) == false) {
                continue;
            }

            String id = issue.getAttrValue("id");
            Rule rule = ruleFinder.findByKey(AndroidLintConstants.REPOSITORY_KEY, id);

            if (rule == null) {
                messages.addWarningText("Rule " + id + " not found for " + AndroidLintConstants.REPOSITORY_KEY);
            }

            String severityStr = issue.getAttrValue("severity");
            Severity newSeverity = null;
            if (StringUtils.isNotEmpty(severityStr)) {
                newSeverity = Severity.fromString(severityStr);
            }

            boolean ruleFound = false;
            int ruleIndex = 0;
            while (!ruleFound && ruleIndex < activeRules.size()) {
                Rule defaultRule = activeRules.get(ruleIndex).getRule();
                if (rule.equals(defaultRule)) {
                    ruleFound = true;
                } else {
                    ruleIndex++;
                }
            }

            if (ruleFound) {
                if (Severity.IGNORE.equals(newSeverity)) {

                    logger.info("Rule " + id + " ignored");
                    profile.removeActiveRule(profile.getActiveRules().get(ruleIndex));

                } else if (newSeverity != null) {

                    logger.info("Rule " + id + " priority changed:");
                    RulePriority defaultRulePriority = rule.getSeverity();
                    RulePriority newRulePriority = AndroidLintUtils.getSonarSeverityFromLintSeverity(newSeverity);
                    if (defaultRulePriority != newRulePriority) {
                        logger.info(defaultRulePriority + " -> " + newRulePriority);
                        profile.getActiveRules().get(ruleIndex).setSeverity(newRulePriority);
                    }

                }
            } else {
                messages.addInfoText("Rule " + id + " not found");
            }
        }
    }


}
