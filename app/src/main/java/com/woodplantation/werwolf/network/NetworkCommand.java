package com.woodplantation.werwolf.network;

import com.woodplantation.werwolf.network.objects.Command;
import com.woodplantation.werwolf.util.Serializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Object for commands sent via network, from server to client or client to server.
 *
 * Created by Sebu on 10.01.2017.
 */

public class NetworkCommand implements Serializable {
    static final long serialVersionUID = -3469314080315513889L;
    private static String TYPE = "type";
    private static String COMMAND = "command";
    public NetworkCommandType type;
    public Command command;

    public NetworkCommand() {
    }

    public NetworkCommand(String json) {
        try {
            JSONObject object = new JSONObject(json);
            type = NetworkCommandType.valueOf(object.getString(TYPE));
            command = (Command) Serializer.deserialize(object.getString(COMMAND));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toJsonString() {
        try {
            JSONObject object = new JSONObject();
            object.put(TYPE, type);
            object.put(COMMAND, Serializer.serialize(command));
            return object.toString() + "\n";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}