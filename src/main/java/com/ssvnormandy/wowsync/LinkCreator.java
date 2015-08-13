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
    public void createLinks(File src, File dest, boolean forceOverride) {
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
                System.out.printf("File (%s) is already symbolic link, not doing anything.\n", file.getName());
                continue;
            }
            if (Files.exists(link)) {
                if (forceOverride) {
                    System.out.printf("File (%s) is already in your folder, deleting and creating symlink.\n", file.getName());
                    try {
                        Files.delete(link);
                    } catch (IOException e) {
                        System.err.println("Couldn't delete the actual file, skipping!");
                        e.printStackTrace();
                        continue;
                    }
                } else {
                    System.out.printf("File (%s) is already in your folder, not doing anything.\n", file.getName());
                    continue;
                }
            }
            try {
                Files.createSymbolicLink(link, source);
                System.out.printf("Successfully linked file (%s)\n", file.getName());
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
