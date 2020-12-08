/*
 * Copyright 2020 journeyman.
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

import java.util.HashSet;
import java.util.Set;
import net.unknowndomain.alea.messages.MsgFilePart;
import net.unknowndomain.alea.messages.MsgTextPart;
import net.unknowndomain.alea.messages.MsgPart;
import net.unknowndomain.alea.messages.MsgStyle;
import net.unknowndomain.alea.messages.MsgUrlPart;
import net.unknowndomain.alea.messages.ReturnMsg;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;

/**
 *
 * @author journeyman
 */
public class MsgFormatter
{
    
    public static MessageBuilder formatMessage(ReturnMsg message)
    {
        MessageBuilder msgBuilder = new MessageBuilder();
        appendMessage(msgBuilder, message);
        return msgBuilder;
    }
    
    public static void appendMessage(MessageBuilder msgBuilder, ReturnMsg message)
    {
        for (MsgPart msgPart : message.getParts())
        {
            if (msgPart instanceof MsgTextPart)
            {
                MsgTextPart part = (MsgTextPart) msgPart;
                formatTextPart(msgBuilder, part);
            }
            else if (msgPart instanceof MsgFilePart)
            {
                MsgFilePart part = (MsgFilePart) msgPart;
                msgBuilder.addAttachment(part.getData(), part.getFileName());
            }
            else if (msgPart instanceof MsgUrlPart)
            {
                MsgUrlPart part = (MsgUrlPart) msgPart;
                msgBuilder.addAttachment(part.getUrl());
            }
        }
    }
    
    private static void formatTextPart(MessageBuilder msgBuilder, MsgTextPart part)
    {
        Set<MessageDecoration> decor = new HashSet<>();
        String text = part.getMsgText();
        if (part.getMsgStyle().contains(MsgStyle.BOLD))
        {
            decor.add(MessageDecoration.BOLD);
        }
        if (part.getMsgStyle().contains(MsgStyle.ITALIC))
        {
            decor.add(MessageDecoration.ITALICS);
        }
        if (part.getMsgStyle().contains(MsgStyle.UNDERLINE))
        {
            decor.add(MessageDecoration.UNDERLINE);
        }
        if (part.getMsgStyle().contains(MsgStyle.STRIKEOUT))
        {
            decor.add(MessageDecoration.STRIKEOUT);
        }
        if (part.getMsgStyle().contains(MsgStyle.SPOILER))
        {
            decor.add(MessageDecoration.SPOILER);
        }
        if (part.getMsgStyle().contains(MsgStyle.CODE))
        {
            if (text.contains("\n") || text.length() > 120)
            {
                decor.add(MessageDecoration.CODE_LONG);
            }
            else
            {
                decor.add(MessageDecoration.CODE_SIMPLE);
            }
        }
        MessageDecoration [] tmp = new MessageDecoration[decor.size()];
        msgBuilder.append(text, decor.toArray(tmp));
    }
}
