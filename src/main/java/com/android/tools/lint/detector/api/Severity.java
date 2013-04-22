/*
 * Sonar Android Plugin
 * Copyright (C) 2013 Jerome Van Der Linden, Stephane Nicolas and SonarSource
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
package com.android.tools.lint.detector.api;

/**
 * Severity of an issue found by lint
 * <p/>
 * <b>NOTE: This is not a public or final API; if you rely on this be prepared
 * to adjust your code for the next tools release.</b>
 */
public enum Severity {
    /**
     * Fatal: Use sparingly because a warning marked as fatal will be
     * considered critical and will abort Export APK etc in ADT
     */
    FATAL("Fatal"),

    /**
     * Errors: The issue is known to be a real error that must be addressed.
     */
    ERROR("Error"),

    /**
     * Warning: Probably a problem.
     */
    WARNING("Warning"),

    /**
     * Information only: Might not be a problem, but the check has found
     * something interesting to say about the code.
     */
    INFORMATIONAL("Information"),

    /**
     * Ignore: The user doesn't want to see this issue
     */
    IGNORE("Ignore");

    private final String mDisplay;

    private Severity(String display) {
        mDisplay = display;
    }

    /**
     * Returns a description of this severity suitable for display to the user
     *
     * @return a description of the severity
     */
    public String getDescription() {
        return mDisplay;
    }

    public static Severity fromString(String severityStr) {
        if (severityStr != null) {
            for (Severity severity : Severity.values()) {
                if (severity.getDescription().equalsIgnoreCase(severityStr)) {
                    return severity;
                }
            }
        }
        return null;
    }
}