/*
 * Copyright 2020 Marco Bignami.
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
package net.unknowndomain.alea;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import java.util.UUID;

/**
 *
 * @author journeyman
 */
public class AleaConfig
{
    private final String discordToken;
    private final boolean systemListener;
    private final String settingsDir;
    private final UUID namespace;
    private final boolean enableInteractions;
    private final String commandPrefix;
    
    public AleaConfig(String discordToken, boolean systemListener, String settingsDir, boolean enableInteractions, String commandPrefix)
    {
        this(
                discordToken,
                systemListener,
                settingsDir,
                enableInteractions, 
                commandPrefix,
                Generators.timeBasedGenerator(EthernetAddress.fromInterface()).generate()
        );
    }
    
    public AleaConfig(String discordToken, boolean systemListener, String settingsDir, boolean enableInteractions, String commandPrefix, UUID namespace)
    {
        this.discordToken = discordToken;
        this.systemListener = systemListener;
        this.settingsDir = settingsDir;
        this.namespace = namespace;
        this.enableInteractions = enableInteractions;
        this.commandPrefix = commandPrefix;
    }

    public String getDiscordToken()
    {
        return discordToken;
    }

    public boolean isSystemListener()
    {
        return systemListener;
    }

    public String getSettingsDir()
    {
        return settingsDir;
    }

    public UUID getNamespace()
    {
        return namespace;
    }

    public String getCommandPrefix()
    {
        return commandPrefix;
    }

    public boolean isEnableInteractions()
    {
        return enableInteractions;
    }
}
