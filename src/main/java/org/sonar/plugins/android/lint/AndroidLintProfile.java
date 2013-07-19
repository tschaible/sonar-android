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

import com.google.common.io.Closeables;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.utils.ValidationMessages;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Android Lint default profile with all rules activated
 *
 * @author Jerome Van Der Linden
 * @author Thomas Bores
 */
public class AndroidLintProfile extends ProfileDefinition {

  private AndroidLintProfileImporter androidProfilImporter;

  public AndroidLintProfile(AndroidLintProfileImporter androidProfilImporter) {
    this.androidProfilImporter = androidProfilImporter;
  }

  @Override
  public RulesProfile createProfile(ValidationMessages messages) {
    Reader config = null;

    try {
      config = new InputStreamReader(this.getClass().getResourceAsStream("/org/sonar/plugins/android/lint/profile-android-lint.xml"));
      RulesProfile profile = this.androidProfilImporter.importProfile(config, messages);
      profile.setName(AndroidLintConstants.ANDROID_LINT_PROFILE);
      profile.setLanguage(Java.KEY);

      return profile;
    } finally
    {
      Closeables.closeQuietly(config);
    }
  }
}
