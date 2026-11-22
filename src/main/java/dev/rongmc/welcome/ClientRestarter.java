package dev.rongmc.welcome;

import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Relaunches the current JVM with the same command line, then stops Minecraft.
 * Used after optional-mod jar toggles so Fabric reloads the new set.
 */
public final class ClientRestarter {
    private ClientRestarter() {}

    public static boolean canRelaunch() {
        return ProcessHandle.current().info().command().isPresent()
                && ProcessHandle.current().info().arguments().isPresent();
    }

    /**
     * Starts a clone of this process (after a short delay on POSIX), then quits.
     * @return false if relaunch command could not be determined (caller should fall back to hint)
     */
    public static boolean restartNow() {
        Optional<List<String>> cmd = buildCommand();
        if (cmd.isEmpty()) {
            RongMcWelcomeClient.LOGGER.warn("Cannot relaunch: process command line unavailable");
            return false;
        }
        try {
            spawnDetached(cmd.get());
        } catch (IOException e) {
            RongMcWelcomeClient.LOGGER.error("Failed to spawn restarted client", e);
            return false;
        }
        Minecraft mc = RongMcWelcomeClient.mc();
        if (mc != null) {
            mc.execute(mc::stop);
        }
        return true;
    }

    private static Optional<List<String>> buildCommand() {
        Optional<String> command = ProcessHandle.current().info().command();
        Optional<String[]> arguments = ProcessHandle.current().info().arguments();
        if (command.isEmpty() || arguments.isEmpty()) {
            return Optional.empty();
        }
        List<String> cmd = new ArrayList<>();
        cmd.add(command.get());
        for (String arg : arguments.get()) {
            cmd.add(arg);
        }
        return Optional.of(cmd);
    }

    private static void spawnDetached(List<String> cmd) throws IOException {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        ProcessBuilder pb;
        if (os.contains("win")) {
            // Wait for this JVM to release jar locks, then relaunch.
            List<String> bat = new ArrayList<>();
            bat.add("cmd.exe");
            bat.add("/c");
            StringBuilder body = new StringBuilder("timeout /t 1 /nobreak >nul & ");
            for (int i = 0; i < cmd.size(); i++) {
                if (i > 0) {
                    body.append(' ');
                }
                body.append('"').append(cmd.get(i).replace("\"", "\\\"")).append('"');
            }
            bat.add(body.toString());
            pb = new ProcessBuilder(bat);
        } else {
            List<String> shell = new ArrayList<>();
            shell.add("bash");
            shell.add("-c");
            StringBuilder body = new StringBuilder("sleep 1; exec");
            for (String part : cmd) {
                body.append(' ').append(shellQuote(part));
            }
            shell.add(body.toString());
            pb = new ProcessBuilder(shell);
        }
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        pb.start();
        RongMcWelcomeClient.LOGGER.info("Scheduled client relaunch ({} args)", cmd.size());
    }

    private static String shellQuote(String value) {
        return "'" + value.replace("'", "'\"'\"'") + "'";
    }
}
