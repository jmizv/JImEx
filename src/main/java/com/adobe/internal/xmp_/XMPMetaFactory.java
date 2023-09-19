package com.adobe.internal.xmp_;

import com.adobe.internal.xmp_.options.ParseOptions;

public class XMPMetaFactory {
    // left empty on purpose

    com.adobe.internal.xmp_.XMPMeta parseFromBuffer(byte[] input, ParseOptions options) {
        return new XMPMeta() {
            public void dumpObject() {
                System.out.println("dumpObject: " + this);
            }
        };
    }
}
