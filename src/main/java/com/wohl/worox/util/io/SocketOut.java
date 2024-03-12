package com.wohl.worox.util.io;

import java.io.*;

public class SocketOut extends BufferedWriter {
    public SocketOut(OutputStream out) {
        super(new OutputStreamWriter(out));
    }
    public void commit() throws IOException {
        this.flush();
    }
    public int send(String msg) throws IOException {
        this.write(msg + "\n");
        this.commit();
        return msg.length() + 1;
    }
}
