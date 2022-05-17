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
package net.unknowndomain.alea.server;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import net.unknowndomain.alea.GenericListener;
import net.unknowndomain.alea.settings.SettingsRepository;
import net.unknowndomain.alea.slash.CommandsHelper;
import net.unknowndomain.alea.systems.RpgSystemCommand;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author m.bignami
 */
public class AleaJoinListener extends GenericListener implements ServerJoinListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AleaJoinListener.class);
    
    private final SettingsRepository settingsRepository;
    
    public AleaJoinListener(SettingsRepository settingsRepository, UUID namespace)
    {
        super(namespace);
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void onServerJoin(ServerJoinEvent event)
    {
        Long guildId = event.getServer().getId();
        List cose = new LinkedList<>();
        for (RpgSystemCommand cmd : RpgSystemCommand.LOADER)
        {
            settingsRepository.initSystem(guildId, cmd.getCommandDesc());
        }
        CommandsHelper.updateGuild(event.getApi(), settingsRepository, guildId);
//        List<ServerSlashCommandPermissionsBuilder> listaUpdate = new LinkedList<>();
//        SlashCommandPermissionsUpdater scu = new SlashCommandPermissionsUpdater(event.getServer());
//        for (Role role : event.getServer().getRoles())
//        {
//            if (role.getPermissions().getState(PermissionType.ADMINISTRATOR) == PermissionState.ALLOWED)
//            {
//                scu.addPermission(SlashCommandPermissions.create(role.getId(), SlashCommandPermissionType.ROLE, true));
//            }
//        }
//        event.getApi().getGlobalSlashCommands().thenAccept( slashCommands -> 
//        {
//            for (SlashCommand sc : slashCommands)
//            {
//                if (sc.getName().equals("guild-config"))
//                {
//                    scu.update(sc.getId()).join();
//                }
//            }
//        });
    }
}
