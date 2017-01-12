package com.woodplantation.werwolf.network;

/**
 * Created by Sebu on 10.01.2017.
 */

public enum NetworkCommandType {
    /**
     * client sends displayname to server
     * handled in network.server.GetDisplaynameTask
     */
    CLIENT_SERVER_DISPLAYNAME,
    /**
     * server sends to client that game has been finished
     * handled in network.client.ReadingIncomingCommandsTask
     */
    SERVER_CLIENT_SHUTDOWN,
    /**
     * server sends to client the latest list of displaynames
     * handled in network.client.ReadingIncomingCommandsTask
     */
    SERVER_CLIENT_DISPLAYNAMES,
    /**
     * server sends to client that he got kicked out of lobby.
     * handled in network.client.ReadingIncomingCommandsTask
     */
    SERVER_CLIENT_KICK
}
