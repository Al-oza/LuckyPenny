package com.luckypenny;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;

/**
 * Stores Lucky Penny totals outside RuneLite's general configuration file.
 * All file operations run on a dedicated background executor.
 */
@Singleton
@Slf4j
public class LuckyPennyStorage
{
    private static final Path STORAGE_FILE = RuneLite.RUNELITE_DIR.toPath()
            .resolve("lucky-penny")
            .resolve("saved-counts.json");

    private final Gson gson;
    private final ClientThread clientThread;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public LuckyPennyStorage(Gson gson, ClientThread clientThread)
    {
        this.gson = gson;
        this.clientThread = clientThread;
    }

    /**
     * Loads totals asynchronously. The supplied callback always runs on the
     * RuneLite client thread.
     */
    public void load(Consumer<Map<String, Integer>> onLoaded)
    {
        executor.execute(() ->
        {
            Map<String, Integer> savedCounts = read();
            clientThread.invoke(() -> onLoaded.accept(savedCounts));
        });
    }

    /**
     * Queues a save of an immutable snapshot so callers never perform file I/O.
     */
    public void save(Map<String, Integer> savedCounts)
    {
        Map<String, Integer> snapshot = new LinkedHashMap<>(savedCounts);
        executor.execute(() -> write(snapshot));
    }

    /**
     * Stops queued work without blocking RuneLite shutdown.
     */
    public void shutDown()
    {
        executor.shutdownNow();
    }

    private Map<String, Integer> read()
    {
        if (!Files.exists(STORAGE_FILE))
        {
            return new LinkedHashMap<>();
        }

        try
        {
            JsonObject json = new JsonParser().parse(Files.readString(STORAGE_FILE))
                    .getAsJsonObject();
            Map<String, Integer> savedCounts = new LinkedHashMap<>();

            for (Map.Entry<String, JsonElement> entry : json.entrySet())
            {
                int count = entry.getValue().getAsInt();
                if (count > 0)
                {
                    savedCounts.put(entry.getKey(), count);
                }
            }

            return savedCounts;
        }
        catch (IOException | RuntimeException e)
        {
            log.debug("Unable to load Lucky Penny saved counts", e);
            return new LinkedHashMap<>();
        }
    }

    private void write(Map<String, Integer> savedCounts)
    {
        Path directory = STORAGE_FILE.getParent();
        Path temporaryFile = null;

        try
        {
            Files.createDirectories(directory);
            temporaryFile = Files.createTempFile(directory, "saved-counts-", ".json");
            Files.writeString(
                    temporaryFile,
                    gson.toJson(savedCounts),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            try
            {
                Files.move(
                        temporaryFile,
                        STORAGE_FILE,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
            catch (AtomicMoveNotSupportedException e)
            {
                Files.move(temporaryFile, STORAGE_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e)
        {
            log.debug("Unable to save Lucky Penny saved counts", e);
        }
        finally
        {
            if (temporaryFile != null)
            {
                try
                {
                    Files.deleteIfExists(temporaryFile);
                }
                catch (IOException e)
                {
                    log.debug("Unable to clean up Lucky Penny temporary save file", e);
                }
            }
        }
    }
}
