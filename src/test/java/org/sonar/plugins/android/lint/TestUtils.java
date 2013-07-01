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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.sonar.api.profiles.RulesProfile;

import org.sonar.api.rules.RuleFinder;

import org.mockito.Mockito;
import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.rules.RuleRepository;

import com.google.common.collect.ImmutableList;

import org.sonar.api.resources.InputFileUtils;

import org.sonar.api.resources.Project;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.ProjectFileSystem;

/**
 * This class is copied from the sonar-python-plugin and has been modified
 * for testing sonar android plugin classes.
 * It creates a new Project object from a simple Android Hello World project
 * presents in the resources folder of the test.
 *
 * @author Thomas Bores
 *
 */
public class TestUtils{
//  public static File loadResource(String resourceName) {
//    URL resource = TestUtils.class.getResource(resourceName);
//    File resourceAsFile = null;
//    try{
//      resourceAsFile = new File(resource.toURI());
//    } catch (URISyntaxException e) {
//      System.out.println("Cannot load resource: " + resourceName);
//    }
//
//    return resourceAsFile;
//  }
//
//  /**
//   * @return default mock project
//   */
//  public static Project mockProject() {
//    return mockProject(loadResource("/HelloWorld/"));
//  }
//
//  /**
//   * Mock project
//   * @param baseDir projects base directory
//   * @return mocked project
//   */
//  public static Project mockProject(File baseDir) {
//    List<InputFile> mainFiles = new LinkedList<InputFile>();
//
//    ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
//    InputFile inputFile = InputFileUtils.create(
//        new File("src/test/resources/HelloWorld/src/"),
//        new File("src/test/resources/HelloWorld/src/com/mkyong/android/HelloWorldActivity.java"));
//    when(fileSystem.mainFiles(Java.KEY)).thenReturn(ImmutableList.of(inputFile));
//
//    List<InputFile> testFiles = new LinkedList<InputFile>();
//    List<File> baseList = Arrays.asList(baseDir);
//    List<File> srcList = Arrays.asList(new File(baseDir.getPath()+"/src/"));
//
//    when(fileSystem.getBasedir()).thenReturn(baseDir);
//    when(fileSystem.getSourceCharset()).thenReturn(Charset.defaultCharset());
//    when(fileSystem.mainFiles(Java.KEY)).thenReturn(mainFiles);
//    when(fileSystem.testFiles(Java.KEY)).thenReturn(testFiles);
//    when(fileSystem.getSourceDirs()).thenReturn(srcList);
//
//    // Uncomment and adapt the following line to test run AndroidLintSensorTest
////    when(fileSystem.resolvePath("<your_path>\\sonar-android-lint-plugin\\target\\test-classes\\HelloWorld\\lint-report.xml")).thenReturn(new File("<your_path>\\sonar-android-lint-plugin\\target\\test-classes\\HelloWorld\\lint-report.xml"));
//
//    Project project = mock(Project.class);
//    when(project.getFileSystem()).thenReturn(fileSystem);
//    Language lang = mockLanguage();
//    when(project.getLanguage()).thenReturn(lang);
//
//    return project;
//  }
//
//  public static Java mockLanguage(){
//    Java lang = mock(Java.class);
//    return lang;
//  }
}
