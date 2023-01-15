package net.kearos.gitnexttag;

import java.util.Arrays;

public record GitCommand(String path, String... args) {
    @Override
    public String toString() {
        return "GitCommand{" +
                "path='" + path + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
