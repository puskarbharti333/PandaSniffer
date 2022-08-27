package com.pbharti.r64sniffer.traffic;

import java.util.ArrayList;
import java.util.List;



public class ConnInfo {
    public int id;
    public int uid;
    public byte protocol;
    public byte state;

    public long accept = 0l;
    public long back = 0l;
    public long sent = 0l;
    public long recv = 0l;

    public String dest;
    public int destPort;

    public long born_time;
    public boolean alive;

    public List<TCPLog> tcp_logs = new ArrayList<>();

}
