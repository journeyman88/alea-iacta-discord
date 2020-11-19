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

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;

/**
 *
 * @author journeyman
 */
public class HelpWrapper
{
    public static MessageBuilder printHelp(String commandName, Options opts, boolean autoUsage)
    {
        MessageBuilder mb = new MessageBuilder();
        HelpFormatter formatter = new HelpFormatter();
        StringWriter out = new StringWriter();
        PrintWriter pw = new PrintWriter(out);

        formatter.printHelp(pw, 80, commandName, null, opts, formatter.getLeftPadding(), formatter.getDescPadding(), null, autoUsage);
        pw.flush();
        mb.append(out.toString(), MessageDecoration.CODE_LONG);
        return mb;
    }
}
