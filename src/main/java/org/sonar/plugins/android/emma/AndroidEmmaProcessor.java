/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.android.emma;

import com.vladium.emma.data.CoverageOptionsFactory;
import com.vladium.emma.data.DataFactory;
import com.vladium.emma.data.ICoverageData;
import com.vladium.emma.data.IMergeable;
import com.vladium.emma.data.IMetaData;
import com.vladium.emma.report.AbstractItemVisitor;
import com.vladium.emma.report.AllItem;
import com.vladium.emma.report.IItem;
import com.vladium.emma.report.IReportDataModel;
import com.vladium.emma.report.IReportDataView;
import com.vladium.emma.report.PackageItem;
import com.vladium.emma.report.SrcFileItem;
import com.vladium.util.IntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author Evgeny Mandrikov
 */
public class AndroidEmmaProcessor {

  public static final String META_DATA_SUFFIX = ".em";
  public static final String COVERAGE_DATA_SUFFIX = ".ec";
  private static final Logger LOGGER = LoggerFactory.getLogger(AndroidEmmaProcessor.class);
  private final PropertiesBuilder<Integer, Integer> lineHitsBuilder = new PropertiesBuilder<Integer, Integer>(CoreMetrics.COVERAGE_LINE_HITS_DATA);

  private final SensorContext context;
  private final IReportDataModel model;
  private final FileSystem fileSystem;

  public AndroidEmmaProcessor(File buildDir, FileSystem fileSystem, SensorContext context) {
    try {
      ICoverageData coverageData = mergeCoverageData(buildDir);
      IMetaData metaData = mergeMetadata(buildDir);
      this.model = IReportDataModel.Factory.create(metaData, coverageData);
      this.context = context;
      this.fileSystem = fileSystem;
    } catch (IOException e) {
      throw new SonarException(e);
    }
  }

  private IMetaData mergeMetadata(File buildDir) throws IOException {
    // Merge all files with meta-data extension
    IMetaData metaData = DataFactory.newMetaData(CoverageOptionsFactory.create(new Properties()));
    File[] metaDataFiles = buildDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(META_DATA_SUFFIX);
      }
    });
    if (metaDataFiles != null && metaDataFiles.length > 0) {
      for (File metaDataFile : metaDataFiles) {
        IMergeable[] mergeableMetadata = DataFactory.load(metaDataFile);
        metaData.merge(mergeableMetadata[DataFactory.TYPE_METADATA]);
      }
    } else {
      LOGGER.warn("No metadata (*.em) file found in {}", buildDir);
    }
    return metaData;
  }

  private ICoverageData mergeCoverageData(File buildDir) throws IOException {
    // Merge all files with coverage extension
    ICoverageData coverageData = DataFactory.newCoverageData();
    File[] coverageDataFiles = buildDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(COVERAGE_DATA_SUFFIX);
      }
    });
    if (coverageDataFiles != null && coverageDataFiles.length > 0) {
      for (File coverageDataFile : coverageDataFiles) {
        IMergeable[] mergeableCoverageData = DataFactory.load(coverageDataFile);
        coverageData.merge(mergeableCoverageData[DataFactory.TYPE_COVERAGEDATA]);
      }
    } else {
      LOGGER.warn("No coverage (*.ec) file found in {}", buildDir);
    }
    return coverageData;
  }

  public void process() {
    model.getView(IReportDataView.HIER_SRC_VIEW).getRoot().accept(new MyVisitor(), null);
  }

  class MyVisitor extends AbstractItemVisitor {
    String fullName = "";

    public Object visit(AllItem item, Object o) {
      System.out.println(item.getName());
      work(item, o);
      return o;
    }

    public Object visit(PackageItem item, Object o) {
      String name = item.getName();
      System.out.println(name);
      fullName += name.replace('.', File.separatorChar);
      work(item, o);
      fullName = fullName.substring(fullName.length() - name.length(), fullName.length());
      return o;
    }

    public Object visit(SrcFileItem item, Object o) {
      lineHitsBuilder.clear();
      int lines = 0;
      int coveredLines = 0;

      IntObjectMap map = item.getLineCoverage();
      for (int lineId : map.keys()) {
        SrcFileItem.LineCoverageData lineCoverageData = (SrcFileItem.LineCoverageData) map.get(lineId);

        lines++;
        final int fakeHits;
        if (lineCoverageData.m_coverageStatus == SrcFileItem.LineCoverageData.LINE_COVERAGE_COMPLETE) {
          coveredLines++;
          fakeHits = 1;
        } else {
          fakeHits = 0;
        }
        lineHitsBuilder.add(lineId, fakeHits);
      }
      InputFile resource = fileSystem.inputFile(FilePredicates.hasRelativePath(fullName + File.separator + item.getName()));
      context.saveMeasure(resource, CoreMetrics.LINES_TO_COVER, (double) lines);
      context.saveMeasure(resource, CoreMetrics.UNCOVERED_LINES, (double) lines - coveredLines);
      context.saveMeasure(resource, lineHitsBuilder.build().setPersistenceMode(PersistenceMode.DATABASE));

      return o;
    }

    private void work(IItem item, Object ctx) {
      Iterator iter = item.getChildren();
      while (iter.hasNext()) {
        IItem child = (IItem) iter.next();
        child.accept(this, ctx);
      }
    }
  }
}
