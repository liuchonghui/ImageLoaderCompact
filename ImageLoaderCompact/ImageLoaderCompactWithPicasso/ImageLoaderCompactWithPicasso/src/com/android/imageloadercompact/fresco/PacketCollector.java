package com.android.imageloadercompact.fresco;

import java.util.LinkedList;

/**
 * Controller in sync mode.
 *
 * @author liu_chonghui
 */
public class PacketCollector {

    private static final int MAX_PACKETS = 65536;

    private LinkedList<Packet> resultQueue;

    public PacketCollector() {
        this.resultQueue = new LinkedList<Packet>();
    }

    public synchronized Packet pollResult() {
        if (resultQueue.isEmpty()) {
            return null;
        } else {
            return resultQueue.removeLast();
        }
    }

    public synchronized Packet nextResult() {
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
