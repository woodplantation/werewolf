package com.woodplantation.werwolf.network.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Sebu on 10.01.2017.
 */

public class ClientInfo {
    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;
    public String displayname;
    public String id;
}
