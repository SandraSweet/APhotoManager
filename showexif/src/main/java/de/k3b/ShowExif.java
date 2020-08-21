/*
 * Copyright (c) 2017-2020 by k3b.
 *
 * This file is part of AndroFotoFinder / #APhotoManager.
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
package de.k3b;

import java.io.IOException;

import de.k3b.io.filefacade.FileFacade;
import de.k3b.io.filefacade.IFile;
import de.k3b.media.ExifInterfaceEx;
import de.k3b.media.IPhotoProperties;
import de.k3b.media.MediaFormatter;
import de.k3b.media.PhotoPropertiesFormatter;
import de.k3b.media.PhotoPropertyFileReader;

/** simple commandline tool to show  */
public class ShowExif {
    private static final String usage = "usage java -jar ShowExif.jar [-d(ebug)] [file.jpg [file.jpg] ..]";
    private static final String dbg_context = "ShowExif";

    public static void main(String[] args) {
        boolean debug = false;
        if (args.length == 0) {
            System.out.println(usage);
            System.exit(-1);
        }
        for (String fileName : args) {
            if (fileName.toLowerCase().startsWith("-d")) {
                debug = true;
            } else {
                show(fileName, debug);
            }
        }
        System.exit(0);
    }

    private static void show(String fileName, boolean debug) {
        System.out.println("------");
        System.out.println(fileName);

        final PhotoPropertyFileReader photoPropertyFileReader = new PhotoPropertyFileReader();
        try {
            final IFile file = FileFacade.convert(dbg_context, fileName);
            IPhotoProperties jpg = photoPropertyFileReader.load(file, null, dbg_context);

            IPhotoProperties exif = ExifInterfaceEx.create(file, null, photoPropertyFileReader.getXmp(), dbg_context);

            // PhotoPropertiesImageReader jpg = new PhotoPropertiesImageReader().load(fileName, xmp, dbg_context);
            show(jpg, debug);
            show(exif, debug);
            show(photoPropertyFileReader.getXmp(), debug);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void show(IPhotoProperties item, boolean debug) {
        if (item != null) {
            if (debug) System.out.println("######## " + item.getClass().getName() + " #########");
            System.out.println(PhotoPropertiesFormatter.format(item, false, null, MediaFormatter.FieldID.path));
            if (debug) System.out.println(item.toString());
        }
    }
}
