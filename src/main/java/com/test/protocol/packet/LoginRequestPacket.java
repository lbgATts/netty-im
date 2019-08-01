package com.test.protocol.packet;

import com.test.protocol.Command;
import lombok.Data;

/**
 * @Author: shanying
 * @Date: 2019-08-01 11:51
 */
@Data
public class LoginRequestPacket extends Packet {

    private String userId;

    private String username;

    private String password;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_REQUEST;
    }

    @Override
    public Byte getVersion() {
        return 1;
    }
}
