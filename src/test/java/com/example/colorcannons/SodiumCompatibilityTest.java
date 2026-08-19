package com.example.colorcannons;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class SodiumCompatibilityTest {
    @Test
    void sourceHasNoSodiumCoupling() throws IOException {
        Path sourceRoot = Path.of("src/main/java");
        try (Stream<Path> files = Files.walk(sourceRoot)) {
            boolean sodiumReference = files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(SodiumCompatibilityTest::read)
                    .anyMatch(text -> text.contains("net.caffeinemc.mods.sodium")
                            || text.contains("sodium.mixins")
                            || text.contains("SodiumClientMod"));
            assertFalse(sodiumReference, "Color Autocannons must not couple to Sodium internals");
        }
    }

    private static String read(Path path) {
        try { return Files.readString(path); }
        catch (IOException e) { throw new RuntimeException(e); }
    }
}
