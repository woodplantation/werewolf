package com.woodplantation.werwolf.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Object for commands sent via network, from server to client or client to server.
 *
 * Created by Sebu on 10.01.2017.
 */

public class NetworkCommand implements Serializable {
    static final long serialVersionUID = -3469314080315513889L;
    public NetworkCommandType type;
    public String string;

    public NetworkCommand() {
    }

    public NetworkCommand(String json) {
        try {
            JSONObject object = new JSONObject(json);
            type = NetworkCommandType.valueOf(object.getString("type"));
            string = object.getString("string");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toJsonString() {
        try {
            JSONObject object = new JSONObject();
            object.put("type", type);
            object.put("string", string);
            return object.toString() + "\n";
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}