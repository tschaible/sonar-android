package org.sonar.plugins.android.lint;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;

/**
 * @author jva
 */
public class AndroidLintProfile extends ProfileDefinition {

    private XMLProfileParser xmlProfileParser;

    public AndroidLintProfile(XMLProfileParser xmlProfileParser) {
        this.xmlProfileParser = xmlProfileParser;
    }

    @Override
    public RulesProfile createProfile(ValidationMessages messages) {
        return xmlProfileParser.parseResource(getClass().getClassLoader(), "org/sonar/plugins/android/lint/profile-android-lint.xml", messages);
    }
}
