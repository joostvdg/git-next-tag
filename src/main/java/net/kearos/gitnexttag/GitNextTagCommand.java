package net.kearos.gitnexttag;

import io.micronaut.configuration.picocli.PicocliRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Paths;

@Command(name = "git-next-tag", description = "...",
        mixinStandardHelpOptions = true)
public class GitNextTagCommand implements Runnable {

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
    String alternativePath;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(GitNextTagCommand.class, args);
    }

    public void run() {
        if (verbose) {
            logger.info("baseTag supplied: {}", baseTag);
            if (alternativePath != null && !alternativePath.isEmpty()) {
                logger.info("execution path supplied: {}", alternativePath);
            }
        }
        var currentPath = Paths.get("")
                .toAbsolutePath()
                .toString();
        var path = currentPath;
        if (alternativePath != null && !alternativePath.isEmpty()) {
            path = alternativePath;
        }

        // git --no-pager tag --sort="version:refname" --list "v2.1.*"
        var gitCommand = new GitCommand(path,"git","--no-pager", "tag","--sort=\"version:refname\"" ,"--list", "\"" + baseTag + "\"");
        var gitCommandExecutor = new GitCommandExecutor();
        var nextGitTag = gitCommandExecutor.executeGitCommand(gitCommand);
        if (verbose) {
            logger.info("next git tag: {}", nextGitTag);
        }
    }
}
