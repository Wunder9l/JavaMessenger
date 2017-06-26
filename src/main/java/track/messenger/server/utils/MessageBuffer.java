package track.messenger.server.utils;

/**
 * A shared buffer which can contain many messages inside. A message gets a section of the buffer to use. If the
 * message outgrows the section in size, the message requests a larger section and the message is copied to that
 * larger section. The smaller section is then freed again.
 * <p>
 * <p>
 * Created by jjenkov on 18-10-2015.
 * Modified by artem on 12.04.2017.
 */
public class MessageBuffer {

    public static final int KB = 1024;
    public static final int MB = 1024 * KB;

    private static final int CAPACITY_SMALL = 4 * KB;
    private static final int CAPACITY_MEDIUM = 128 * KB;
    private static final int CAPACITY_LARGE = 1024 * KB;

    //package scope (default) - so they can be accessed from unit tests.
    byte[] smallMessageBuffer = new byte[1024 * 4 * KB];   //1024 x   4KB messages =  4MB.
    byte[] mediumMessageBuffer = new byte[128 * 128 * KB];   // 128 x 128KB messages = 16MB.
    byte[] largeMessageBuffer = new byte[16 * 1 * MB];   //  16 *   1MB messages = 16MB.

    QueueIntFlip smallMessageBufferFreeBlocks = new QueueIntFlip(1024); // 1024 free sections
    QueueIntFlip mediumMessageBufferFreeBlocks = new QueueIntFlip(128);  // 128  free sections
    QueueIntFlip largeMessageBufferFreeBlocks = new QueueIntFlip(16);   // 16   free sections

    //todo make all message buffer capacities and block sizes configurable
    //todo calculate free block queue sizes based on capacity and block size of buffers.

    public MessageBuffer() {
        //add all free sections to all free section queues.
        for (int i = 0; i < smallMessageBuffer.length; i += CAPACITY_SMALL) {
            this.smallMessageBufferFreeBlocks.put(i);
        }
        for (int i = 0; i < mediumMessageBuffer.length; i += CAPACITY_MEDIUM) {
            this.mediumMessageBufferFreeBlocks.put(i);
        }
        for (int i = 0; i < largeMessageBuffer.length; i += CAPACITY_LARGE) {
            this.largeMessageBufferFreeBlocks.put(i);
        }
    }

    public NetMessage getNetMessage() {
        int nextFreeSmallBlock = this.smallMessageBufferFreeBlocks.take();

        if (nextFreeSmallBlock == -1) {
            return null;
        }

        NetMessage netMessage = new NetMessage(this);       //todo get from NetMessage pool - caps memory usage.

        netMessage.sharedArray = this.smallMessageBuffer;
        netMessage.capacity = CAPACITY_SMALL;
        netMessage.offset = nextFreeSmallBlock;
        netMessage.length = 0;

        return netMessage;
    }

    public boolean expandNetMessage(NetMessage netMessage) {
        if (netMessage.capacity == CAPACITY_SMALL) {
            return moveNetMessage(netMessage,
                    this.smallMessageBufferFreeBlocks,
                    this.mediumMessageBufferFreeBlocks,
                    this.mediumMessageBuffer,
                    CAPACITY_MEDIUM);
        } else if (netMessage.capacity == CAPACITY_MEDIUM) {
            return moveNetMessage(netMessage,
                    this.mediumMessageBufferFreeBlocks,
                    this.largeMessageBufferFreeBlocks,
                    this.largeMessageBuffer,
                    CAPACITY_LARGE);
        } else {
            return false;
        }
    }

    private boolean moveNetMessage(NetMessage netMessage,
                                   QueueIntFlip srcBlockQueue,
                                   QueueIntFlip destBlockQueue,
                                   byte[] dest,
                                   int newCapacity) {
        int nextFreeBlock = destBlockQueue.take();
        if (nextFreeBlock == -1) {
            return false;
        }

        System.arraycopy(netMessage.sharedArray, netMessage.offset, dest, nextFreeBlock, netMessage.length);

        srcBlockQueue.put(netMessage.offset); //free smaller block after copy

        netMessage.sharedArray = dest;
        netMessage.offset = nextFreeBlock;
        netMessage.capacity = newCapacity;
        return true;
    }


}
