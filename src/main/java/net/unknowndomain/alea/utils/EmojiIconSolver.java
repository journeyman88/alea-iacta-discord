/*
 * Copyright 2022 journeyman.
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
package net.unknowndomain.alea.utils;

import java.util.Optional;
import net.unknowndomain.alea.icon.AleaIcon;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.CustomEmoji;

/**
 *
 * @author journeyman
 */
public class EmojiIconSolver
{
    
    private static EmojiIconSolver INSTANCE = null;
    
    private final DiscordApi api;
    
    private EmojiIconSolver(DiscordApi api)
    {
        this.api = api;
    }
    
    public static synchronized void build(DiscordApi api)
    {
        if (INSTANCE == null)
        {
            INSTANCE = new EmojiIconSolver(api);
        }
    }
    
    public static EmojiIconSolver getInstance()
    {
        return INSTANCE;
    }
    
    public Optional<CustomEmoji> solveIcon(AleaIcon icon)
    {
        Optional<CustomEmoji> retVal = Optional.empty();
        for (CustomEmoji emoji : api.getCustomEmojisByName(icon.getNamespace() + "_" + icon.getIconId()))
        {
            retVal = Optional.ofNullable(emoji);
        }
        return retVal;
    }
    
}
