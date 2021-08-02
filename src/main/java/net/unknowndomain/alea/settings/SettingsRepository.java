/*
 * Copyright 2021 Marco Bignami.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.unknowndomain.alea.settings;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author journeyman
 */
public class SettingsRepository
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsRepository.class);
    
    private final Path settingsDir;
    private final Gson gsonSerializer = new Gson();
    
    private Map<Long, GuildSettings> guildsRepo;
    
    public SettingsRepository(String settingsDir) throws IOException
    {
        this(Paths.get(settingsDir));
    }
    
    public SettingsRepository(Path settingsDir) throws IOException
    {
        this.settingsDir = settingsDir;
        if (!Files.exists(settingsDir))
        {
            Files.createDirectories(settingsDir);
        }
        Path guildDir = settingsDir.resolve(GuildSettings.PREFIX);
        if (!Files.exists(guildDir))
        {
            Files.createDirectories(guildDir);
        }
        guildsRepo = new HashMap<>();
        Files.list(guildDir).forEach( singleGuild -> {
            try
            {
                String guildName = singleGuild.getFileName().toString();
                Long guildId = Long.parseLong(guildName.replaceAll(".json", ""));
                byte [] data = Files.readAllBytes(singleGuild);
                GuildSettings settings = gsonSerializer.fromJson(new String(data, StandardCharsets.UTF_8), GuildSettings.class);
                guildsRepo.put(guildId, settings);
            } 
            catch (NumberFormatException | IOException ex)
            {
                LOGGER.error(null, ex);
            }
        });
    }
    
    public Optional<GuildSettings> loadGuildSettings(Long guildId)
    {
        if (!guildsRepo.containsKey(guildId))
        {
            storeGuildSettings(guildId, new GuildSettings());
        }
        return Optional.ofNullable(guildsRepo.get(guildId));
    }
    
    public synchronized void storeGuildSettings(Long guildId, GuildSettings settings)
    {
        try
        {
            Path guildPath = settingsDir.resolve(GuildSettings.PREFIX).resolve(guildId + ".json");
            Files.write(guildPath, gsonSerializer.toJson(settings).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            guildsRepo.put(guildId, settings);
        } 
        catch (IOException ex)
        {
            LOGGER.error(null, ex);
        }
    }
    
    public synchronized void removeGuildSettings(Long guildId)
    {
        try
        {
            Path guildPath = settingsDir.resolve(GuildSettings.PREFIX).resolve(guildId + ".json");
            Files.deleteIfExists(guildPath);
            guildsRepo.remove(guildId);
        } 
        catch (IOException ex)
        {
            LOGGER.error(null, ex);
        }
    }
    
    
}
