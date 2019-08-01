package com.test.protocol.packet;

import com.test.protocol.Command;
import lombok.Data;

/**
 * @Author: shanying
 * @Date: 2019-08-01 15:28
 */
@Data
public class MessageRequestPacket extends Packet {

    private String message;

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_REQUEST;
    }

    @Override
    public Byte getVersion() {
        return 1;
    }
}
