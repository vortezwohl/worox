package com.wohl.worox.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SocketIn extends BufferedReader {
    public SocketIn(InputStream in) {
        super(new InputStreamReader(in));
    }
    public String recv() throws IOException {
        return this.readLine();
    }
}
