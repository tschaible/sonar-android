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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.resources.Java;

import org.sonar.api.utils.ValidationMessages;

import java.io.Reader;


/**
 * Importer of Android Lint profile. By default contains all rules.
 *
 * @author Jerome Van Der Linden
 * @author Thomas Bores
 */
public class AndroidLintProfileImporter extends ProfileImporter {

  private static final Logger logger = LoggerFactory.getLogger(AndroidLintRuleParser.class);

  private RulesProfile profile;
  private XMLProfileParser xmlProfileParser;

  public AndroidLintProfileImporter(XMLProfileParser xmlProfileParser) {
    super(AndroidLintRuleRepository.REPOSITORY_KEY, AndroidLintUtils.PLUGIN_NAME);
    setSupportedLanguages(Java.KEY);
    this.xmlProfileParser = xmlProfileParser;
  }

  @Override
  public RulesProfile importProfile(Reader reader, ValidationMessages messages) {
    profile = xmlProfileParser.parse(reader, messages);

    if (profile == null) {
      messages.addErrorText("Unable to load default profile");
      logger.error("Unable to load default profile");
      return null;
    }

    return profile;
  }
}
