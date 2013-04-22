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
 * A simple offset-based position *
 * <p/>
 * <b>NOTE: This is not a public or final API; if you rely on this be prepared
 * to adjust your code for the next tools release.</b>
 */
public class DefaultPosition extends Position {
    /**
     * The line number (0-based where the first line is line 0)
     */
    private final int mLine;

    /**
     * The column number (where the first character on the line is 0), or -1 if
     * unknown
     */
    private final int mColumn;

    /**
     * The character offset
     */
    private final int mOffset;

    /**
     * Creates a new {@link com.android.tools.lint.detector.api.DefaultPosition}
     *
     * @param line   the 0-based line number, or -1 if unknown
     * @param column the 0-based column number, or -1 if unknown
     * @param offset the offset, or -1 if unknown
     */
    public DefaultPosition(int line, int column, int offset) {
        this.mLine = line;
        this.mColumn = column;
        this.mOffset = offset;
    }

    @Override
    public int getLine() {
        return mLine;
    }

    @Override
    public int getOffset() {
        return mOffset;
    }

    @Override
    public int getColumn() {
        return mColumn;
    }
}