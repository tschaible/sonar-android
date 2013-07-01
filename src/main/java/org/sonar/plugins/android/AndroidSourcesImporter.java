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
package org.sonar.plugins.android;

import com.google.common.collect.Lists;
import org.sonar.api.resources.InputFile;

import org.sonar.api.config.Settings;

import org.sonar.api.resources.Project;

import java.io.File;
import java.util.List;

import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.ProjectFileSystem;

public class AndroidSourcesImporter extends AbstractSourceImporter {

  private final Project project;
  private final Settings settings;
  
  public static final String language = Java.NAME;

  public AndroidSourcesImporter(Java java, Project project, Settings settings) {
    super(java);
    this.project = project;
    this.settings = settings;
  }

  public void analyse(ProjectFileSystem fileSystem, SensorContext context) {
	List<File> files = toFiles(fileSystem.mainFiles(language));
	  List<File> dirs = fileSystem.getSourceDirs();
	  parseDirs(context, files, dirs, false, fileSystem.getSourceCharset());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  /**
   * Conversion from InputFile to File. Allows to provide backward compatibility.
   */
  private static List<File> toFiles(List<InputFile> files) {
    List<File> result = Lists.newArrayList();
    for (InputFile file : files) {
      result.add(file.getFile());
    }
    return result;
  }
}
