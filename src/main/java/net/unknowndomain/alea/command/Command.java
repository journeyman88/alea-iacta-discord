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
package net.unknowndomain.alea.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.javacord.api.entity.message.MessageBuilder;

/**
 *
 * @author journeyman
 */
public abstract class Command
{
    
    protected static final String CMD_NAME = "command";
    protected static final String CMD_PARAMS = "parameters";
    protected static final String CMD_HELP = "help";
    protected static final String CMD_VERBOSE = "verbose";
    protected final Pattern PREFIX = Pattern.compile("^(?<command>" + getCommandRegex() + ")(( )(?<parameters>.*))?$");
    
    protected abstract String getCommandRegex();
    
    public boolean checkCommand(String cmdLine)
    {
        Matcher matcher = PREFIX.matcher(cmdLine);
        return matcher.matches();
    }
    public abstract MessageBuilder execCommand(String cmdLine);
}
