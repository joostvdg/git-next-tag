package net.kearos.gitnexttag;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;

import java.io.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GitNextTagCommandTest {

    @Test
    public void testWithCommandLineOption() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {
            String[] args = new String[] { "-v", "-b", "v0.1.*" };
            PicocliRunner.run(GitNextTagCommand.class, ctx, args);

            // git-next-tag
            assertTrue(baos.toString().contains("next git tag: v0.1."));
        }
    }

    @Test
    public void testWithOutputPath() throws Exception {
        try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {
            File tmpFile = File.createTempFile("test", ".tmp");
            System.out.println("Temp file for testing: " + tmpFile.getAbsolutePath());
            String[] args = new String[] { "-v", "-b", "v0.1.*", "-o", tmpFile.getAbsolutePath() };
            PicocliRunner.run(GitNextTagCommand.class, ctx, args);

            BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
            assertTrue(reader.readLine().contains("v0.1."));
            reader.close();
        }
    }
}
