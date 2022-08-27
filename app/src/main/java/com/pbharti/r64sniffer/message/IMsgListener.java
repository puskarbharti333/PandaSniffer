package com.pbharti.r64sniffer.message;



public interface IMsgListener {
    void onMessage(int msgId, Object arg);

    Object onSyncMessage(int msgId, Object arg);
}
