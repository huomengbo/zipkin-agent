/**
 * Copyright 2016-2017 The OpenZipkin Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.jfbank.zipkin.agent.util;

import zipkin2.Span;
import zipkin2.codec.SpanBytesEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * span数据的加密，摘自zipkin源码里
 */
public class BytesEncoderUtil {

    /**
     * 转换成最终的数组格式
     *
     * @param spanList
     * @return
     */
    public static byte[] encode(List<zipkin2.Span> spanList) {

        ArrayList<byte[]> nextMessage = new ArrayList<>();
        for (zipkin2.Span span : spanList) {
            byte[] first = BytesEncoderUtil.encodeSpan(span);
            nextMessage.add(first);
        }
        return BytesEncoderUtil.encodeSpanList(nextMessage);
    }

    /**
     * 转换成最终的数组格式
     *
     * @return
     */
    public static byte[] encode(zipkin2.Span span) {

        ArrayList<byte[]> nextMessage = new ArrayList<>();
        byte[] spanByte = BytesEncoderUtil.encodeSpan(span);
        nextMessage.add(spanByte);
        return BytesEncoderUtil.encodeSpanList(nextMessage);
    }


    /**
     * 第一次加密
     *
     * @param span
     * @return
     */
    private static byte[] encodeSpan(Span span) {
        return SpanBytesEncoder.JSON_V2.encode(span);
    }


    /**
     * 第二次加密
     *
     * @param values
     * @return
     */
    private static byte[] encodeSpanList(List<byte[]> values) {
        int sizeOfArray = 2;
        int length = values.size();
        for (int i = 0; i < length; ) {
            sizeOfArray += values.get(i++).length;
            if (i < length) sizeOfArray++;
        }

        byte[] buf = new byte[sizeOfArray];
        int pos = 0;
        buf[pos++] = '[';
        for (int i = 0; i < length; ) {
            byte[] v = values.get(i++);
            System.arraycopy(v, 0, buf, pos, v.length);
            pos += v.length;
            if (i < length) buf[pos++] = ',';
        }
        buf[pos] = ']';
        return buf;
    }

}
