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

import com.google.common.collect.Lists;
import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;

import java.io.File;
import java.util.List;

/**
 * Repository for Android Lint rules (using {@link AndroidLintRuleParser}
 *
 * @author Stephane Nicolas
 * @author Thomas Bores
 */
public final class AndroidLintRuleRepository extends RuleRepository {
  public static final String REPOSITORY_KEY = "AndroidLint";
  public static final String REPOSITORY_NAME = REPOSITORY_KEY;

  private static final String RULES_FILE = "/org/sonar/plugins/android/lint/rules.txt";

  private final ServerFileSystem fileSystem;
  private AndroidLintRuleParser androidLintRuleParser;

  public AndroidLintRuleRepository(ServerFileSystem fileSystem) {
    super(AndroidLintRuleRepository.REPOSITORY_KEY, Java.KEY);
    setName(AndroidLintRuleRepository.REPOSITORY_NAME);
    this.fileSystem = fileSystem;
    this.androidLintRuleParser = new AndroidLintRuleParser();
  }

  @Override
  public List<Rule> createRules() {
    List<Rule> rules = Lists.newArrayList();
    rules.addAll(androidLintRuleParser.parse(getClass().getResourceAsStream(RULES_FILE)));
//    for (File userExtensionXml : fileSystem.getExtensions(AndroidLintRuleRepository.REPOSITORY_KEY, "txt")) {
//      rules.addAll(androidLintRuleParser.parse(userExtensionXml));
//    }
    return rules;
  }
}
