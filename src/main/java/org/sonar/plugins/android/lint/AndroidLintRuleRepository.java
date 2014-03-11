/*
 * SonarQube Android Plugin
 * Copyright (C) 2013 SonarSource and Jerome Van Der Linden, Stephane Nicolas, Florian Roncari, Thomas Bores
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

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.rule.RuleDefinitions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Repository for Android Lint rules
 */
public final class AndroidLintRuleRepository implements RuleDefinitions {

  private static final Logger LOG = LoggerFactory.getLogger(AndroidLintRuleRepository.class);
  private NewRepository androidRepo;

  @Override
  public void define(Context context) {
    androidRepo = context.newRepository(AndroidLintConstants.REPOSITORY_KEY, "Java");
    readXmlFile("/org/sonar/plugins/android/lint/rules.xml");
    androidRepo.done();
  }

  private void readXmlFile(String rulesXmlResource) {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document document = db.parse(getClass().getResourceAsStream(rulesXmlResource));
      NodeList rules = document.getElementsByTagName("rule");
      int ruleIndex = 0;
      Node rule = rules.item(ruleIndex);
      while (rule != null) {
        String key = null;
        String severity = null;
        int ruleChildIndex = 0;
        NodeList ruleChilds = rule.getChildNodes();
        Node ruleChild = ruleChilds.item(ruleChildIndex);
        while (ruleChild != null) {
          if ("key".equals(ruleChild.getNodeName())) {
            key = ruleChild.getTextContent();
          } else if ("severity".equals(ruleChild.getNodeName())) {
            severity = ruleChild.getTextContent();
          }
          ruleChildIndex++;
          ruleChild = ruleChilds.item(ruleChildIndex);
        }
        createRule(key, severity);
        ruleIndex++;
        rule = rules.item(ruleIndex);
      }
    } catch (Exception e) {
      LOG.error("An error occured while creating Android Lint rule repository ", e);
    }

  }

  private void createRule(String key, String severity) {
    Preconditions.checkNotNull(key, "A rule is defined without a mandatory key");
    androidRepo.newRule(key)
        .setName(key)
        .setSeverity(severity)
        .setHtmlDescription(getClass().getResource("/org/sonar/l10n/android/rules/android-lint/" + key + ".html"));
  }


}
