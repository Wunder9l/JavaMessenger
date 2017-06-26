package track.messenger.server.utils;

import java.nio.ByteBuffer;

/**
 * Created by jjenkov on 16-10-2015.
 */
public class NetMessage {

    private MessageBuffer messageBuffer = null;

    public long socketId = 0; // the id of source socket or destination socket, depending on whether is going in or out.

    public byte[] sharedArray = null;
    public int offset = 0; //offset into sharedArray where this message data starts.
    public int capacity = 0; //the size of the section in the sharedArray allocated to this message.
    public int length = 0; //the number of bytes used of the allocated section.

    public track.messenger.messages.Message processedMessage = null;

    public NetMessage(MessageBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    /**
     * Writes data from the ByteBuffer into this message - meaning into the buffer backing this message.
     *
     * @param byteBuffer The ByteBuffer containing the message data to write.
     * @return
     */
    public int writeToMessage(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();

        while (this.length + remaining > capacity) {
            if (!this.messageBuffer.expandNetMessage(this)) {
                return -1;
            }
        }

        int bytesToCopy = Math.min(remaining, this.capacity - this.length);
        byteBuffer.get(this.sharedArray, this.offset + this.length, bytesToCopy);
        this.length += bytesToCopy;

        return bytesToCopy;
    }


    /**
     * Writes data from the byte array into ththis.serverSocketthis.serverSocketis message - meaning into the buffer backing this message.
     *
     * @param byteArray The byte array containing the message data to write.
     * @return
     */
    public int writeToMessage(byte[] byteArray) {
        return writeToMessage(byteArray, 0, byteArray.length);
    }


    /**
     * Writes data from the byte array into this message - meaning into the buffer backing this message.
     *
     * @param byteArray The byte array containing the message data to write.
     * @return
     */
    public int writeToMessage(byte[] byteArray, int offset, int length) {
        int remaining = length;

        while (this.length + remaining > capacity) {
            if (!this.messageBuffer.expandNetMessage(this)) {
                return -1;
            }
        }

        int bytesToCopy = Math.min(remaining, this.capacity - this.length);
        System.arraycopy(byteArray, offset, this.sharedArray, this.offset + this.length, bytesToCopy);
        this.length += bytesToCopy;
        return bytesToCopy;
    }


    /**
     * In case the buffer backing the nextMessage contains more than one HTTP netMessage, move all data after the first
     * netMessage to a new NetMessage object.
     *
     * @param netMessage  The netMessage containing the partial netMessage (after the first netMessage).
     * @param endIndex The end index of the first netMessage in the buffer of the netMessage given as parameter.
     */
    public void writePartialMessageToMessage(NetMessage netMessage, int endIndex) {
        int startIndexOfPartialMessage = netMessage.offset + endIndex;
        int lengthOfPartialMessage = (netMessage.offset + netMessage.length) - endIndex;

        System.arraycopy(netMessage.sharedArray, startIndexOfPartialMessage, this.sharedArray, this.offset, lengthOfPartialMessage);
    }

    public int writeToByteBuffer(ByteBuffer byteBuffer) {
        return 0;
    }


}
