package com.test.protocol.serialize;

/**
 * @Author: shanying
 * @Date: 2019-08-01 14:15
 */
public interface SerializerAlgorithm {

    byte JSON = 1;

    Serializer DEFAULT = new JsonSerializer();

}
