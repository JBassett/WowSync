package com.ssvnormandy.wowsync;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by jbassett on 8/11/15.
 */
public class Application {

    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        CmdLineParser cmdLineParser = new CmdLineParser(arguments);
        try {
            cmdLineParser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            cmdLineParser.printUsage(System.err);
            System.exit(-1);
        }
        System.out.printf("Sync Dir: %s\nWow Dir: %s\nAccount Name: %s", arguments.syncDir, arguments.wowDir, arguments.accountName);

        LinkCreator lc = new LinkCreator();
        lc.createLinks(Paths.get(arguments.syncDir.toString(), "AddOns").toFile(),
                Paths.get(arguments.wowDir.toString(), "Interface", "AddOns").toFile(),
                arguments.forceOverride);

        // This creates links for Addon data
        lc.createLinks(Paths.get(arguments.syncDir.toString(), "SavedVariables").toFile(),
                Paths.get(arguments.wowDir.toString(), "WTF", "Account", arguments.accountName, "SavedVariables").toFile(),
                arguments.forceOverride);
    }

    static class Arguments {

        public Arguments() {
            String baseWow;
            if (System.getProperty("os.name").startsWith("Windows")) {
                baseWow = System.getenv("ProgramFiles(X86)");
            } else {
                baseWow = "/Applications/";
            }
            syncDir = Paths.get(System.getProperty("user.home"), "Dropbox", "Game Data", "World of Warcraft").toFile();
            wowDir = Paths.get(baseWow, "World of Warcraft").toFile();

            File[] accounts = Paths.get(wowDir.toString(), "WTF", "Account").toFile().listFiles(p -> !p.isHidden());
            accountName = accounts == null ? null : accounts[0].getName();
        }

        @Option(name = "-s", aliases = {"--sync-dir"}, usage = "Sets the folder that is synced to your cloud storage.")
        public File syncDir;

        @Option(name = "-w", aliases = {"--wow-dir"}, usage = "Sets the install location of your World of Warcraft Client.")
        public File wowDir;

        @Option(name = "-n", aliases = {"--name"}, usage = "The account name that you want to sync.")
        public String accountName;

        @Option(name = "-f", aliases = {"--force"}, usage = "Forces symlinks to be created even if the file already exists.")
        public boolean forceOverride;
    }
}
