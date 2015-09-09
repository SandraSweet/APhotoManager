/*
 * Copyright (c) 2015 by k3b.
 *
 * This file is part of AndroFotoFinder.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

package de.k3b.android.androFotoFinder.queries;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayManager;

import de.k3b.android.androFotoFinder.Global;
import de.k3b.android.androFotoFinder.R;
import de.k3b.android.osmdroid.DefaultResourceProxyImplEx;
import de.k3b.android.osmdroid.IconFactory;
import de.k3b.database.QueryParameter;
import de.k3b.database.SelectedItems;

public abstract class SqlUpdateTask  extends AsyncTask<QueryParameter, Integer, SelectedItems> {
    // every 500 items the progress indicator is advanced
    private static final int PROGRESS_INCREMENT = 500;

    private final Activity mContext;
    protected final String mDebugPrefix;
    protected SelectedItems mSelectedItems;
    protected StringBuffer mStatus = null;
    private int mStatisticsRecycled = 0;

    public SqlUpdateTask(Activity context, String debugPrefix, SelectedItems selectedItems) {
        if (Global.debugEnabledSql || Global.debugEnabled) {
            mStatus = new StringBuffer().append(debugPrefix);
        }

        Global.debugMemory(debugPrefix, "ctor");
        this.mContext = context;
        this.mDebugPrefix = debugPrefix;
        this.mSelectedItems = new SelectedItems();
        this.mSelectedItems.addAll(selectedItems);
    }

    @Override
    protected SelectedItems doInBackground(QueryParameter... queryParameter) {
        if (queryParameter.length != 1) throw new IllegalArgumentException();

        QueryParameter queryParameters = queryParameter[0];

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(Uri.parse(queryParameters.toFrom()), queryParameters.toColumns(),
                    queryParameters.toAndroidWhere(), queryParameters.toAndroidParameters(), queryParameters.toOrderBy());

            int itemCount = cursor.getCount();
            final int expectedCount = itemCount + itemCount;

            publishProgress(itemCount, expectedCount);
            if (this.mStatus != null) {
                this.mStatus.append("'").append(itemCount).append("' rows found for query \n\t").append(queryParameters.toSqlString());
            }

            // long startTime = SystemClock.currentThreadTimeMillis();
            int colIconID = cursor.getColumnIndex(FotoSql.SQL_COL_PK);

            int increment = PROGRESS_INCREMENT;
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(colIconID);

                doInBackground(id);

                itemCount++;
                if ((--increment) <= 0) {
                    publishProgress(itemCount, expectedCount);
                    increment = PROGRESS_INCREMENT;

                    // Escape early if cancel() is called
                    if (isCancelled()) break;
                }
            }
            if (this.mStatus != null) {
                this.mStatus.append("\n\tRecycled : ").append(mStatisticsRecycled);
                // Log.i(Global.LOG_CONTEXT, debugPrefix + itemCount + this.mStatus);
            }

            return this.mSelectedItems;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    abstract protected void doInBackground(Long id);
}