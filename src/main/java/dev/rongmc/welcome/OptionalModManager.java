package dev.rongmc.welcome;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Optional client mods ship as {@code <file>.jar.disabled} under {@code mods/}.
 * Enabling renames to {@code .jar}; disabling renames back. Requires game restart.
 */
public final class OptionalModManager {
    public enum State {
        ENABLED,
        DISABLED,
        MISSING
    }

    private OptionalModManager() {}

    public static Path modsDir() {
        return FabricLoader.getInstance().getGameDir().resolve("mods");
    }

    public static State stateOf(OptionalCatalog.ModEntry entry) {
        Optional<Path> enabled = findJar(entry, false);
        if (enabled.isPresent()) {
            return State.ENABLED;
        }
        Optional<Path> disabled = findJar(entry, true);
        if (disabled.isPresent()) {
            return State.DISABLED;
        }
        return State.MISSING;
    }

    public static boolean setEnabled(OptionalCatalog.ModEntry entry, boolean enabled) throws IOException {
        State state = stateOf(entry);
        if (state == State.MISSING) {
            return false;
        }
        if (enabled && state == State.ENABLED) {
            return true;
        }
        if (!enabled && state == State.DISABLED) {
            return true;
        }
        if (enabled) {
            Path src = findJar(entry, true).orElseThrow();
            Path dst = withSuffix(src, ".jar");
            Files.move(src, dst);
        } else {
            Path src = findJar(entry, false).orElseThrow();
            Path dst = Path.of(src.toString() + ".disabled");
            Files.move(src, dst);
        }
        return true;
    }

    public static void applyPreset(OptionalCatalog.Preset preset) throws IOException {
        Set<String> enable = new LinkedHashSet<>(preset.mods == null ? List.of() : preset.mods);
        for (OptionalCatalog.ModEntry entry : OptionalCatalog.mods()) {
            setEnabled(entry, enable.contains(entry.id));
        }
        WelcomeConfig.get().enabledOptionalMods = new LinkedHashSet<>(enable);
        WelcomeConfig.save();
    }

    public static void syncConfigFromDisk() {
        Set<String> enabled = new LinkedHashSet<>();
        for (OptionalCatalog.ModEntry entry : OptionalCatalog.mods()) {
            if (stateOf(entry) == State.ENABLED) {
                enabled.add(entry.id);
            }
        }
        WelcomeConfig.get().enabledOptionalMods = enabled;
        WelcomeConfig.save();
    }

    public static void applySelection(Collection<String> enableIds) throws IOException {
        Set<String> enable = new LinkedHashSet<>(enableIds);
        for (OptionalCatalog.ModEntry entry : OptionalCatalog.mods()) {
            setEnabled(entry, enable.contains(entry.id));
        }
        WelcomeConfig.get().enabledOptionalMods = enable;
        WelcomeConfig.save();
    }

    private static Optional<Path> findJar(OptionalCatalog.ModEntry entry, boolean disabled) {
        String prefix = entry.filePrefix == null ? entry.id : entry.filePrefix;
        String needle = prefix.toLowerCase(Locale.ROOT);
        Path dir = modsDir();
        if (!Files.isDirectory(dir)) {
            return Optional.empty();
        }
        List<Path> matches = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                if (disabled) {
                    if (!name.endsWith(".jar.disabled")) {
                        continue;
                    }
                } else {
                    if (!name.endsWith(".jar") || name.endsWith(".jar.disabled")) {
                        continue;
                    }
                }
                String base = disabled ? name.substring(0, name.length() - ".jar.disabled".length()) : name.substring(0, name.length() - ".jar".length());
                String normBase = base.replace("-", "").replace("_", "");
                String normNeedle = needle.replace("-", "").replace("_", "");
                if (normBase.startsWith(normNeedle) || normBase.contains(normNeedle)) {
                    matches.add(path);
                }
            }
        } catch (IOException e) {
            RongMcWelcomeClient.LOGGER.warn("Failed scanning mods dir", e);
        }
        if (matches.isEmpty()) {
            return Optional.empty();
        }
        matches.sort((a, b) -> Integer.compare(b.getFileName().toString().length(), a.getFileName().toString().length()));
        return Optional.of(matches.get(0));
    }

    private static Path withSuffix(Path disabledJar, String jarSuffix) {
        String name = disabledJar.getFileName().toString();
        if (name.endsWith(".jar.disabled")) {
            name = name.substring(0, name.length() - ".disabled".length());
        }
        return disabledJar.getParent().resolve(name);
    }
}
