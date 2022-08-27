package com.pbharti.r64sniffer.traffic;

public class TCPLog {

    /**
     * see {@link IP.DIRECT}
     */
    public byte direct;

    public int size;
    public int flag;
    public int seq;
    public int ack;
    public long time;

    public TCPLog(byte direct, int size, int flag, int seq, int ack) {
        this.direct = direct;
        this.size = size;
        this.flag = flag;
        this.seq = seq;
        this.ack = ack;

        this.time = System.currentTimeMillis();
    }

}
