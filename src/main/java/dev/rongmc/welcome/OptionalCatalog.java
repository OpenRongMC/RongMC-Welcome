package dev.rongmc.welcome;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public final class OptionalCatalog {
    private static final Gson GSON = new Gson();
    private static Root root = new Root();

    public static final class Root {
        public String welcomeTitle = "欢迎来到 RongMC 建筑服";
        public List<String> welcomeBody = List.of();
        public List<Preset> presets = List.of();
        public List<ModEntry> mods = List.of();
    }

    public static final class Preset {
        public String id;
        public String nameKey;
        public String description;
        public List<String> mods = List.of();
    }

    public static final class ModEntry {
        public String id;
        public String name;
        public String description;
        public String filePrefix;
        @SerializedName("modrinth")
        public String modrinth;
        public String side;
    }

    private OptionalCatalog() {}

    public static void load() {
        try (InputStream in = OptionalCatalog.class.getClassLoader().getResourceAsStream("assets/rongmc_welcome/optional_mods.json")) {
            if (in == null) {
                RongMcWelcomeClient.LOGGER.warn("optional_mods.json missing");
                return;
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                Root loaded = GSON.fromJson(reader, Root.class);
                if (loaded != null) {
                    root = loaded;
                }
            }
        } catch (Exception e) {
            RongMcWelcomeClient.LOGGER.warn("Failed to load optional mods catalog", e);
        }
    }

    public static Root root() {
        return root;
    }

    public static List<ModEntry> mods() {
        return root.mods == null ? Collections.emptyList() : root.mods;
    }

    public static List<Preset> presets() {
        return root.presets == null ? Collections.emptyList() : root.presets;
    }

    public static PathHolder gameDir() {
        return new PathHolder(FabricLoader.getInstance().getGameDir());
    }

    public record PathHolder(java.nio.file.Path path) {}
}
