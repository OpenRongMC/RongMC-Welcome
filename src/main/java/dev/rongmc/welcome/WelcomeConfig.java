package dev.rongmc.welcome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public final class WelcomeConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("rongmc-welcome.json");
    private static Data data = new Data();

    public static final class Data {
        public boolean welcomeCompleted = false;
        public Set<String> enabledOptionalMods = new LinkedHashSet<>();
    }

    private WelcomeConfig() {}

    public static Data get() {
        return data;
    }

    public static void load() {
        if (!Files.isRegularFile(PATH)) {
            data = new Data();
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(PATH)) {
            Data loaded = GSON.fromJson(reader, Data.class);
            data = loaded != null ? loaded : new Data();
            if (data.enabledOptionalMods == null) {
                data.enabledOptionalMods = new LinkedHashSet<>();
            }
        } catch (IOException e) {
            RongMcWelcomeClient.LOGGER.warn("Failed to read welcome config", e);
            data = new Data();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            RongMcWelcomeClient.LOGGER.warn("Failed to save welcome config", e);
        }
    }

    public static void markWelcomeCompleted() {
        data.welcomeCompleted = true;
        save();
    }
}
