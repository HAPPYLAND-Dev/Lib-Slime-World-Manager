package com.grinderwolf.smw.api;

import com.grinderwolf.smw.api.utils.NibbleArray;

public interface SlimeChunkSection {

    public byte[] getBlocks();

    public NibbleArray getData();
    public NibbleArray getBlockLight();
    public NibbleArray getSkyLight();
}
