package com.ssvnormandy.wowsync;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jbassett on 8/12/15.
 */
public class LinkCreator {
    public void createLinks(File src, File dest) {
        if (src == null || !src.isDirectory()) {
            throw new IllegalArgumentException("src is not a directory.");
        }
        if (dest == null) {
            throw new IllegalArgumentException("dest cannot be null.");
        } else if (!dest.exists()) {
            try {
                Files.createDirectories(dest.toPath());
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not create the destination directory.", e);
            }
        }

        cleanupBadSymbolicLinks(dest);

        Path link, source;
        for (File file : src.listFiles(p -> !p.getName().startsWith("."))) {
            link = Paths.get(dest.toPath().toString(), file.getName());
            source = file.toPath();
            if (Files.isSymbolicLink(link)) {
                System.out.printf("File (%s) is already symbolic link, not doing anything.\n", link);
                continue;
            }
            if (Files.exists(link)) {
                System.out.printf("File (%s) is already in your folder, not doing anything.\n", link);
                continue;
            }
            System.out.printf("Attempting to link source file (%s) to destination link (%s)\n", source, link);
            try {
                Files.createSymbolicLink(link, source);
            } catch (IOException e) {
                System.err.println("Issue linking!");
                e.printStackTrace();
            }
        }
    }

    private void cleanupBadSymbolicLinks(File loc) {
        for (File file : loc.listFiles(pathname -> Files.isSymbolicLink(pathname.toPath()) && Files.notExists(pathname.toPath()))) {
            System.out.printf("Deleting a symlink (%s) that no longer exists.\n", file);
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                System.err.println("Could not delete the bad symlink...");
                e.printStackTrace();
            }
        }
    }
}
