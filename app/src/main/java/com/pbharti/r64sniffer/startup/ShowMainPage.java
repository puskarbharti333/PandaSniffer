package com.pbharti.r64sniffer.startup;

import com.pbharti.r64sniffer.ContextMgr;
import com.pbharti.r64sniffer.message.Messege;
import com.pbharti.r64sniffer.message.MsgDispatcher;
import com.pbharti.r64sniffer.window.TrafficCtrlWindow;


public class ShowMainPage extends Starter.Task {

    @Override
    protected int start() {
        MsgDispatcher.get().dispatchSync(Messege.PUSH_WINDOW, new TrafficCtrlWindow(ContextMgr.getContext()));
        return 0;
    }


}
