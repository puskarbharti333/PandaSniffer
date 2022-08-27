package com.pbharti.r64sniffer.traffic;

public class IP {

    public static final byte TCP = 6;
    public static final byte UDP = 17;

    public static final String getProtocolName(byte protocol) {
        switch (protocol) {
            case IP.TCP:
                return "TCP";
            case IP.UDP:
                return "UDP";
        }

        return "?";
    }

    public static final String getStateName(byte protocol, byte state) {
        switch (protocol) {
            case IP.TCP: {
                switch (IP.TCP_STATE.values()[state]) {
                    case LISTEN:
                        return "Listen";
                    case SYN_RCVED:
                        return "Syn Recv";
                    case SYN_SENT:
                        return "Syn Sent";
                    case ESTABLISHED:
                        return "Established";
                    case FIN_WAIT1:
                        return "Fin Wait 1";
                    case FIN_WAIT2:
                        return "Fin Wait 2";
                    case CLOSING:
                        return "Closing";
                    case LAST_ACK:
                        return "Last Ack";
                    case CLOSE_WAIT:
                        return "Close Wait";
                    case TIME_WAIT:
                        return "Time Wait";
                    case CLOSED:
                        return "Closed";
                }
                break;
            }

            case IP.UDP: {
                return "";
            }
        }
        return "";
    }

    public enum TCP_STATE {
        LISTEN,
        SYN_SENT,
        SYN_RCVED,
        ESTABLISHED,
        FIN_WAIT1,
        FIN_WAIT2,
        CLOSE_WAIT,
        CLOSING,
        LAST_ACK,
        TIME_WAIT,
        CLOSED
    }

    public static final class DIRECT {
        public static final byte IN = 0;
        public static final byte OUT = 1;
    }

    public static final class TCPF {
        public static final int TCPF_FIN = 1 << 0;
        public static final int TCPF_SYN = 1 << 1;
        public static final int TCPF_RST = 1 << 2;
        public static final int TCPF_PSH = 1 << 3;
        public static final int TCPF_ACK = 1 << 4;
        public static final int TCPF_URG = 1 << 5;
        public static final int TCPF_ECE = 1 << 6;
        public static final int TCPF_CWR = 1 << 7;
    }

}
