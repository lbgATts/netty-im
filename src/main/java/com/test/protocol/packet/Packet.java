package com.test.protocol.packet;

/**
 * @Author: shanying
 * @Date: 2019-08-01 11:48
 */
public abstract class Packet {
    private Byte version = 1;

    public abstract Byte getCommand();

    public abstract Byte getVersion();
}


//协议结构
