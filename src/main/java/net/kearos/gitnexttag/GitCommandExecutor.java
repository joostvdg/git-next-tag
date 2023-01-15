package net.kearos.gitnexttag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class GitCommandExecutor {

    private static final Logger logger  = LoggerFactory.getLogger(GitCommandExecutor.class);
    // https://www.baeldung.com/run-shell-command-in-java
    public String executeGitCommand(GitCommand gitCommand, boolean verbose) {
//        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        var executionPath = new File(gitCommand.path());
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(gitCommand.args()).directory(executionPath);
        logger.info("Executing command: {}", gitCommand.toString());
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Process Info: {}", process.info());

        StringBuilder output = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder();
        BufferedReader reader = process.inputReader();
        BufferedReader errorReader = process.errorReader();
        String lastFoundTag = "";

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                output.append(line + "\n");
                if (!line.isEmpty()) {
                    lastFoundTag = line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.out.println(line);
                errorOutput.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (verbose) {
            logger.info("Last Found Tag: \"{}\"", lastFoundTag);
            logger.info("Output: \"{}\"", output.toString());
            logger.info("ErrorOutput: \"{}\"", errorOutput.toString());
            if (exitCode == 0) {
                logger.info("Success!");
            } else {
                logger.error("Failed to parse git response: {}", exitCode);
            }
        }

        return lastFoundTag;
    }
}
