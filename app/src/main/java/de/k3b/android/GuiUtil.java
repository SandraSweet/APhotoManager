/*
 * Copyright (c) 2015-2016 by k3b.
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
 
package de.k3b.android;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

import de.k3b.android.androFotoFinder.R;

/**
 * gui utils
 */
public class GuiUtil {
    public static String getAppVersionName(final Context context) {
        try {

            final String versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            return versionName;
        } catch (final NameNotFoundException e) {
        }
        return null;
    }
    public static void setTheme(Activity act) {
        final String theme = PreferenceManager.getDefaultSharedPreferences(act).getString("user_theme", "Light");
        switch (theme) {
            case "Light":
                act.setTheme(R.style.AppTheme_Light);
                break;
            case "Dark":
                act.setTheme(R.style.AppTheme_Dark);
                break;
            default:
                throw new RuntimeException("Invalid theme selected");
        }
    }
}
