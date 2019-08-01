package com.test.protocol;

import com.test.protocol.packet.*;
import com.test.protocol.serialize.JsonSerializer;
import com.test.protocol.serialize.Serializer;
import com.test.protocol.serialize.SerializerAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: shanying
 * Date: 2019-08-01 14:23
 */
public class PacketCodec {

    //怎么获取当前的命令类型， 序列化算法？
    private static final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private static final Map<Byte, Serializer> serializerMap;

    static {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(Command.LOGIN_REQUEST, LoginRequestPacket.class);  //指令类型，反序列化后的指令class
        packetTypeMap.put(Command.LOGIN_RESPONSE, LoginResponsePacket.class);  //指令类型，反序列化后的指令class
        packetTypeMap.put(Command.MESSAGE_REQUEST, MessageRequestPacket.class);  //指令类型，反序列化后的指令class
        packetTypeMap.put(Command.MESSAGE_RESPONSE, MessageResponsePacket.class);  //指令类型，反序列化后的指令class

        serializerMap = new HashMap<>();
        Serializer serializer = new JsonSerializer();
        serializerMap.put(serializer.getSerializerAlgorithm(), serializer);
    }

    private static final int MAGIC_NUMBER = 0x12345678;

    //编码Packet 让其变成ByteBuf
    public ByteBuf encode(Packet packet) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.ioBuffer();

        byte[] bytes = SerializerAlgorithm.DEFAULT.serialize(packet);

        buf.writeInt(MAGIC_NUMBER); //todo 第一个字节写入魔数，四个字节长度
        buf.writeByte(packet.getVersion()); //一个字节表示版本
        buf.writeByte(SerializerAlgorithm.DEFAULT.getSerializerAlgorithm()); //一个字节序列化算法
        buf.writeByte(packet.getCommand()); //具体的指令
        buf.writeInt(bytes.length); //总的数据长度
        buf.writeBytes(bytes); //packet序列化之后的数据
        return buf;
    }

    public Packet decode(ByteBuf buf) {
        buf.skipBytes(4); //跳过魔数，4个字节
        buf.skipBytes(1); //跳过协议版本，一个字节

        byte serializeAlgorithm = buf.readByte();

        // 指令
        byte command = buf.readByte();

        // 数据包长度
        int length = buf.readInt();

        byte[] bytes = new byte[length];
        buf.readBytes(bytes); //读出序列化之后的数据长度

        Serializer serializer = getSerializer(serializeAlgorithm);

        Class<? extends Packet> requestType = getRequestType(command);

        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }
        return null;
    }

    Serializer getSerializer(Byte serializeAlgorithm) {
        return serializerMap.get(serializeAlgorithm);
    }

    //根据指令找出反序列化的class类型
    public Class<? extends Packet> getRequestType(Byte command) {
        return packetTypeMap.get(command);
    }
}
