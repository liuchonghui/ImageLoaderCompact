package tool.imageloadercompact.glide;

import java.util.LinkedList;

/**
 * Controller in sync mode.
 *
 * @author liu_chonghui
 */
public class PacketCollector {

    private static final int MAX_PACKETS = 65536;

    private LinkedList<tool.imageloadercompact.glide.Packet> resultQueue;

    public PacketCollector() {
        this.resultQueue = new LinkedList<tool.imageloadercompact.glide.Packet>();
    }

    public synchronized tool.imageloadercompact.glide.Packet pollResult() {
        if (resultQueue.isEmpty()) {
            return null;
        } else {
            return resultQueue.removeLast();
        }
    }

    public synchronized tool.imageloadercompact.glide.Packet nextResult() {
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
