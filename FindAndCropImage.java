/*
 * Copyright (c) 2009, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import static java.nio.file.FileVisitOption.*;
import java.util.*;
import java.awt.Graphics;
import java.awt.Image;
//import java.awt.image.BufferedImage;
import java.awt.image.BufferedImage;
//import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


//import java.io.*;

/**
 * Sample code that finds files that
 * match the specified glob pattern.
 * For more information on what
 * constitutes a glob pattern, see
 * http://docs.oracle.com/javase/javatutorials/tutorial/essential/io/fileOps.html#glob
 *
 * The file or directories that match
 * the pattern are printed to
 * standard out.  The number of
 * matches is also printed.
 *
 * When executing this application,
 * you must put the glob pattern
 * in quotes, so the shell will not
 * expand any wild cards:
 *     java Find . -name "*.java"
 */

public class FindAndCropImage {

    /**
     * A {@code FileVisitor} that finds
     * all files that match the
     * specified pattern.
     */
    BufferedImage img;
    //int x-coord, y-coord, img_width, img_height;
    public static class Finder
        extends SimpleFileVisitor<Path> {

        private final PathMatcher matcher;
        private int numMatches = 0;

        // add additional Attributes

        int x_coord;
        int y_coord;
        int img_width;
        int img_height;

        Finder(String pattern) {
            matcher = FileSystems.getDefault()
                    .getPathMatcher("glob:" + pattern);
        }

        Finder(String pattern, int x, int y, int w, int h) {
            matcher = FileSystems.getDefault()
                    .getPathMatcher("glob:" + pattern);

                    x_coord = x;
                    y_coord = y;
                    img_width = w;
                    img_height = h;

        }

        // Compares the glob pattern against
        // the file or directory name.
        void find(Path file) {
            Path name = file.getFileName();
            if (name != null && matcher.matches(name)) {
                numMatches++;
                File inputfile = file.toFile();
                String fileParent = file.getParent().toString();
                FindAndCropImage curImg = new FindAndCropImage();
                curImg.cropImage(inputfile, 1100, 100, 550, 600);
                //curImg.cropImage(inputfile, x_coord, y_coord, img_width, img_height);
                System.out.println(fileParent + name);
            }
        }

        void find(Path file, int x, int y, int w, int h) {
            Path name = file.getFileName();
            if (name != null && matcher.matches(name)) {
                numMatches++;
                File inputfile = file.toFile();
                String fileParent = file.getParent().toString();
                FindAndCropImage curImg = new FindAndCropImage();
                //curImg.cropImage(inputfile, 1100, 100, 550, 600);
                curImg.cropImage(inputfile, x, y, w, h);
                System.out.println(fileParent + name);
            }
        }

        // Prints the total number of
        // matches to standard out.
        void done() {
            System.out.println("Matched: "
                + numMatches);
        }

        // Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult visitFile(Path file,
                BasicFileAttributes attrs) {
            find(file, x_coord, y_coord, img_width, img_height);
            return CONTINUE;
        }

        // Invoke the pattern matching
        // method on each directory.
        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) {
            find(dir);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file,
                IOException exc) {
            System.err.println(exc);
            return CONTINUE;
        }
    }

    public void cropImage(File src, int x, int y, int w, int h)
    {
      try
      {
        // the line that reads the image file
        img = ImageIO.read(src);
        BufferedImage dest = img.getSubimage(x, y, w, h);

        File outputfile = new File(src.getParent() + "crop-" + src.getName());
        ImageIO.write(dest, "jpg", outputfile);
        //return dest;

        // work with the image here ...
      }
      catch (IOException e)
      {
        // log the exception
        // re-throw if desired

      }

    }



    static void usage() {
        System.err.println("java FindAndCropImage <path>" +
            " -name \"<glob_pattern>\" x-coord y-coord img_width img_height ");
        System.exit(-1);
    }

    public static void main(String[] args)
        throws IOException {

        if (args.length < 7 || !args[1].equals("-name"))
            usage();

        Path startingDir = Paths.get(args[0]);
        String pattern = args[2];

        int x_coord = Integer.parseInt(args[3]);
        int y_coord = Integer.parseInt(args[4]);
        int img_width = Integer.parseInt(args[5]);
        int img_height = Integer.parseInt(args[6]);


        Finder finder = new Finder(pattern, x_coord, y_coord, img_width, img_height);
        Files.walkFileTree(startingDir, finder);
        finder.done();
    }
}
