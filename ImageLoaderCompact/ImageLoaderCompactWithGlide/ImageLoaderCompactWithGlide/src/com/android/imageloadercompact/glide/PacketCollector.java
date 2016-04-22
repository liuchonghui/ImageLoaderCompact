package com.android.imageloadercompact.glide;

import java.util.LinkedList;

/**
 * Controller in sync mode.
 *
 * @author liu_chonghui
 */
public class PacketCollector {

    private static final int MAX_PACKETS = 65536;

    private LinkedList<com.android.imageloadercompact.glide.Packet> resultQueue;

    public PacketCollector() {
        this.resultQueue = new LinkedList<com.android.imageloadercompact.glide.Packet>();
    }

    public synchronized com.android.imageloadercompact.glide.Packet pollResult() {
        if (resultQueue.isEmpty()) {
            return null;
        } else {
            return resultQueue.removeLast();
        }
    }

    public synchronized com.android.imageloadercompact.glide.Packet nextResult() {
        while (resultQueue.isEmpty()) {
            try {
                wait();
            } catch (Exception ie) {
            }
        }
        return resultQueue.removeLast();
    }

    protected synchronized void processPacket(Packet packet) {
        if (packet == null) {
            return;
        }
        if (resultQueue.size() == MAX_PACKETS) {
            resultQueue.removeLast();
        }
        resultQueue.addFirst(packet);
        notifyAll();
    }
}
