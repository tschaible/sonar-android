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
 * Information about a position in a file/document.
 * <p/>
 * <b>NOTE: This is not a public or final API; if you rely on this be prepared
 * to adjust your code for the next tools release.</b>
 */
public abstract class Position {
    /**
     * Returns the line number (0-based where the first line is line 0)
     *
     * @return the 0-based line number
     */
    public abstract int getLine();

    /**
     * The character offset
     *
     * @return the 0-based character offset
     */
    public abstract int getOffset();

    /**
     * Returns the column number (where the first character on the line is 0),
     * or -1 if unknown
     *
     * @return the 0-based column number
     */
    public abstract int getColumn();
}
