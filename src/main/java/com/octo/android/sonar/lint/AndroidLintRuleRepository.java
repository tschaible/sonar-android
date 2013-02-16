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
package com.octo.android.sonar.lint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;

import com.google.common.collect.Lists;

public final class AndroidLintRuleRepository extends RuleRepository {
    private final ServerFileSystem fileSystem;
    private TXTRuleParser txtRuleParser;

    public AndroidLintRuleRepository(ServerFileSystem fileSystem, TXTRuleParser txtRuleParser) {
        super(AndroidLintConstants.REPOSITORY_KEY, Java.KEY);
        setName(AndroidLintConstants.REPOSITORY_NAME);
        this.fileSystem = fileSystem;
        this.txtRuleParser = txtRuleParser;
    }

    @Override
    public List<Rule> createRules() {
        List<Rule> rules = Lists.newArrayList();
        rules.addAll(txtRuleParser.parse(getClass().getResourceAsStream("/com/octo/android/sonar/lint/rules.txt")));
        for (File userExtensionXml : fileSystem.getExtensions(AndroidLintConstants.REPOSITORY_KEY, "txt")) {
            rules.addAll(txtRuleParser.parse(userExtensionXml));
        }
        return rules;
    }

    public static void main(String[] args) {
        List<Rule> listRules = new AndroidLintRuleRepository(new ServerFileSystem() {

            @Override
            public File getHomeDir() {
                return null;
            }

            @Override
            public List<File> getExtensions(String dirName, String... suffixes) {
                return new ArrayList<File>();
            }
        }, new TXTRuleParser()).createRules();

        for (Rule rule : listRules) {
            System.out.println(rule);
        }
    }
}
