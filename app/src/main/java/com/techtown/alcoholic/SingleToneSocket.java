package com.techtown.alcoholic;

import java.net.Socket;

public class SingleToneSocket extends Socket {
    private static Socket socket = null;

    public static Socket getInstance() {
        if(socket == null) {
            socket = new Socket();
        }
        return socket;
    }
}
