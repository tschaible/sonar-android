/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.octo.android.sonar.lint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.ServerComponent;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleParam;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RulesCategory;
import org.sonar.api.utils.SonarException;
import org.sonar.check.Cardinality;

import com.google.common.io.Closeables;

/**
 * @since 2.3
 */
public final class TXTRuleParser implements ServerComponent {

    public List<Rule> parse(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(file), CharEncoding.UTF_8));
            return parse(reader);

        } catch (IOException e) {
            throw new SonarException("Fail to load the file: " + file, e);

        } finally {
            Closeables.closeQuietly(reader);
        }
    }

    /**
     * Warning : the input stream is closed in this method
     */
    public List<Rule> parse(InputStream input) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(input, CharEncoding.UTF_8));
            return parse(reader);

        } catch (IOException e) {
            throw new SonarException("Fail to load the xml stream", e);

        } finally {
            Closeables.closeQuietly(reader);
        }
    }

    @SuppressWarnings("deprecation")
    public List<Rule> parse(BufferedReader reader) {

        try {
            List<Rule> rules = new ArrayList<Rule>();

            List<String> listLines = IOUtils.readLines(reader);

            String previousLine = null;
            Rule rule = null;
            boolean inSummary = false;
            for (ListIterator<String> iterator = listLines.listIterator(); iterator.hasNext();) {
                String line = iterator.next();

                RulesCategory rulesCategory = new RulesCategory();

                if (line.matches("\\=.*")) {
                    System.out.println("Rule category found :" + previousLine);
                    rulesCategory.setName(previousLine);
                } else if (line.matches("\\-.*")) {
                    System.out.println("Rule found :" + previousLine);
                    rule = Rule.create();
                    rules.add(rule);
                    rule.setName(previousLine);
                    rule.setRulesCategory(rulesCategory);
                } else if (line.matches("Summary:.*")) {
                    inSummary = true;
                    System.out.println("Rule summary found :" + line);
                    rule.setDescription(line.substring(line.indexOf(':') + 1));
                } else if (line.matches("Priority:.*")) {
                    inSummary = false;
                } else if (line.matches("Category:.*")) {
                    inSummary = false;
                } else if (line.matches("Severity:.*")) {
                    inSummary = false;
                    String severity = line.substring("Severity: ".length());
                    RulePriority rulePriority = RulePriority.INFO;
                    if ("Fatal".equals(severity)) {
                        rulePriority = RulePriority.BLOCKER;
                    } else if ("Error".equals(severity)) {
                        rulePriority = RulePriority.CRITICAL;
                    }
                    rule.setSeverity(rulePriority);
                    System.out.println("Rule severity found :" + severity);
                } else {
                    if (inSummary) {
                        rule.setDescription(rule.getDescription() + " " + line);
                    }
                }

                previousLine = line;
            }
            // <rule>
            // Rule rule = Rule.create();
            // rules.add(rule);
            return rules;

        } catch (IOException e) {
            throw new SonarException("XML is not valid", e);
        }
    }

    private static void processRule(Rule rule, SMInputCursor ruleC) throws XMLStreamException {
        /* BACKWARD COMPATIBILITY WITH DEPRECATED FORMAT */
        String keyAttribute = ruleC.getAttrValue("key");
        if (StringUtils.isNotBlank(keyAttribute)) {
            rule.setKey(StringUtils.trim(keyAttribute));
        }

        /* BACKWARD COMPATIBILITY WITH DEPRECATED FORMAT */
        String priorityAttribute = ruleC.getAttrValue("priority");
        if (StringUtils.isNotBlank(priorityAttribute)) {
            rule.setSeverity(RulePriority.valueOf(StringUtils.trim(priorityAttribute)));
        }

        SMInputCursor cursor = ruleC.childElementCursor();

        while (cursor.getNext() != null) {
            String nodeName = cursor.getLocalName();

            if (StringUtils.equalsIgnoreCase("name", nodeName)) {
                rule.setName(StringUtils.trim(cursor.collectDescendantText(false)));

            } else if (StringUtils.equalsIgnoreCase("description", nodeName)) {
                rule.setDescription(StringUtils.trim(cursor.collectDescendantText(false)));

            } else if (StringUtils.equalsIgnoreCase("key", nodeName)) {
                rule.setKey(StringUtils.trim(cursor.collectDescendantText(false)));

            } else if (StringUtils.equalsIgnoreCase("configKey", nodeName)) {
                rule.setConfigKey(StringUtils.trim(cursor.collectDescendantText(false)));

            } else if (StringUtils.equalsIgnoreCase("priority", nodeName)) {
                rule.setSeverity(RulePriority.valueOf(StringUtils.trim(cursor.collectDescendantText(false))));

            } else if (StringUtils.equalsIgnoreCase("cardinality", nodeName)) {
                rule.setCardinality(Cardinality.valueOf(StringUtils.trim(cursor.collectDescendantText(false))));

            } else if (StringUtils.equalsIgnoreCase("param", nodeName)) {
                processParameter(rule, cursor);
            }
        }
        if (StringUtils.isEmpty(rule.getKey())) {
            throw new SonarException("Node <key> is missing in <rule>");
        }
    }

    private static void processParameter(Rule rule, SMInputCursor ruleC) throws XMLStreamException {
        RuleParam param = rule.createParameter();

        String keyAttribute = ruleC.getAttrValue("key");
        if (StringUtils.isNotBlank(keyAttribute)) {
            /* BACKWARD COMPATIBILITY WITH DEPRECATED FORMAT */
            param.setKey(StringUtils.trim(keyAttribute));
        }

        String typeAttribute = ruleC.getAttrValue("type");
        if (StringUtils.isNotBlank(typeAttribute)) {
            /* BACKWARD COMPATIBILITY WITH DEPRECATED FORMAT */
            // param.setType(type(StringUtils.trim(typeAttribute)));
        }

        SMInputCursor paramC = ruleC.childElementCursor();
        while (paramC.getNext() != null) {
            String propNodeName = paramC.getLocalName();
            String propText = StringUtils.trim(paramC.collectDescendantText(false));
            if (StringUtils.equalsIgnoreCase("key", propNodeName)) {
                param.setKey(propText);

            } else if (StringUtils.equalsIgnoreCase("description", propNodeName)) {
                param.setDescription(propText);

            } else if (StringUtils.equalsIgnoreCase("type", propNodeName)) {
                // param.setType(type(propText));

            } else if (StringUtils.equalsIgnoreCase("defaultValue", propNodeName)) {
                param.setDefaultValue(propText);
            }
        }
        if (StringUtils.isEmpty(param.getKey())) {
            throw new SonarException("Node <key> is missing in <param>");
        }
    }
}
