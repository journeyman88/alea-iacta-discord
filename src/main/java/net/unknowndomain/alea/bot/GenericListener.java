/*
 * Copyright 2021 m.bignami.
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
package net.unknowndomain.alea.bot;

import com.fasterxml.uuid.Generators;
import java.util.Optional;
import java.util.UUID;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;

/**
 *
 * @author m.bignami
 */
public abstract class GenericListener
{
    private final UUID namespace;

    public GenericListener(UUID namespace)
    {
        this.namespace = namespace;
    }
    
    protected Optional<Long> readUserId(MessageAuthor author)
    {
        Optional<Long> retVal = Optional.empty();
        if (author.isUser() && author.asUser().isPresent())
        {
            User discordUser = author.asUser().get();
            retVal = Optional.of(discordUser.getId());
        }
        return retVal;
    }
    
    protected Optional<UUID> buildCallerId(MessageAuthor author)
    {
        Optional<UUID> retVal = Optional.empty();
        Optional<Long> userId = readUserId(author);
        if (userId.isPresent())
        {
            UUID callerUuid = Generators.nameBasedGenerator(namespace).generate(userId.get() + "L");
            retVal = Optional.of(callerUuid);
        }
        return retVal;
    }
    
}
