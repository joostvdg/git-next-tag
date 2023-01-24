package net.kearos.gitnexttag;

import ch.qos.logback.classic.Level;
import io.micronaut.configuration.picocli.PicocliRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Command(name = "git-next-tag", description = "...",
        mixinStandardHelpOptions = true)
public class GitNextTagCommand implements Runnable {

    final static String SEMVER_REGEX_PATTERN = "^(v)(\\d+)\\.(\\d+)\\.(\\d+)(-?\\w*)";
    private static final Logger logger  = LoggerFactory.getLogger(GitNextTagCommand.class);

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    @Option(names ={"-b", "--baseTag"},
            description = "the base of tag version to amend (e.g., 1.10, to find the next patch 1.10.X)",
            required = true
    )
    String baseTag;

    @Option(names={"-p", "--path"},
            description = "path where to execute the git tag command",
            required = false
    )
    String alternateSourcePath;
    
    @Option(names={"-o", "--outputPath"},
            description = "path (file) where to output result to, replaces output to STDOUT",
            required = false
    )
    String outputPath;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(GitNextTagCommand.class, args);
    }

    public void run() {
        if (verbose) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.toLevel("info"));
        }


        logger.info("baseTag supplied: {}", baseTag);
        if (alternateSourcePath != null && !alternateSourcePath.isEmpty()) {
            logger.info("execution path supplied: {}", alternateSourcePath);
        }

        var currentPath = Paths.get("")
                .toAbsolutePath()
                .toString();
        var path = currentPath;
        if (alternateSourcePath != null && !alternateSourcePath.isEmpty()) {
            path = alternateSourcePath;
        }

        // git --no-pager tag --sort="version:refname" --list "v2.1.*"
        var gitCommand = new GitCommand(path,"git","--no-pager", "tag","--sort", "version:refname" ,"--list", baseTag );
        var gitCommandExecutor = new GitCommandExecutor();
        var foundTag = gitCommandExecutor.executeGitCommand(gitCommand, verbose);
        var nextGitTag = determineNextTag(baseTag, foundTag, verbose);

        logger.info("next git tag: {}", nextGitTag);
        
        if (outputPath == null || outputPath.isEmpty()) {
            System.out.println(nextGitTag);
        } else {
            logger.info("output path set, attempting to write output to file: {}", outputPath);
            try {
                writeOutputToFile(outputPath, nextGitTag);
            } catch (IOException e) {
                logger.error("Could not write output to outputPath ({}): {}", outputPath, e);
                throw new RuntimeException(e);
            }
        }
    }

    private void writeOutputToFile(String outputPath, String nextGitTag) throws IOException {
        Path path = Paths.get(outputPath);
        byte[] strToBytes = nextGitTag.getBytes();

        Files.write(path, strToBytes);

        String read = Files.readAllLines(path).get(0);
        if (!read.equals(nextGitTag)) {
            var errorMessage = String.format("Could not verify if next tag %s is written to output path %s", nextGitTag, outputPath);
            throw new IOException(errorMessage);
        }
    }

    private String determineNextTag(String baseTag, String foundTag, boolean verbose) {
        logger.info("determineNextTag - baseTag: \"{}\", foundTag: \"{}\"",
              baseTag, foundTag);

        var tag = baseTag.replace("*", "");

        // either foundTag is empty, so we return `baseTag` + 0
        // or foundTag is not empty, and we parse it to determine the last number,
        //  and return `foundTag`, with the patch (the Z, of x.y.z) with z+1
        if (baseTag == null || baseTag.isEmpty()) {
            // panic
            throw new RuntimeException("Cannot run without valid base tag");
        }

        if (foundTag == null || foundTag.isEmpty()) {
            return  tag + "0";
        }



        // find the last number of the major . minor . patch
        // 'v' and the whole string are also a group, so index of 3rd digit is 4
        var regexPattern = Pattern.compile(SEMVER_REGEX_PATTERN);
        var regexMatcher = regexPattern.matcher(foundTag);

        // validate foundTag; is it semver?
        // https://www.freeformatter.com/java-regex-tester.html#before-output
        // https://www.jrebel.com/blog/java-regular-expressions-cheat-sheet
        // ^(v)([\d+])\.([\d+])\.([\d+])(-?\w*)
        boolean isValidSemVer = regexMatcher.matches();
        if (!isValidSemVer) {
            var message = String.format("Found invalid tag, not adhering to SemVer: %s", foundTag);
            logger.error(message);
            throw new RuntimeException(message);
        }

        if (verbose) {
            logger.info("Group 1: {}",  regexMatcher.group(1));
            logger.info("Group 2: {}",  regexMatcher.group(2));
            logger.info("Group 3: {}",  regexMatcher.group(3));
            logger.info("Group 4: {}",  regexMatcher.group(4));
            logger.info("Group 5: {}",  regexMatcher.group(5));
        }
        var patchValue = regexMatcher.group(4);
        int patch = Integer.parseInt(patchValue);
        patch++;
        return String.format("%s%s", tag, patch);
    }
}
