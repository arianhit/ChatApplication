package MultiThreadedChatApp;

// compile: javac -cp json-simple-1.1.1.jar;. ReadRequest.java

import org.json.simple.*;  // required for JSON encoding and decoding

public class GetRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            GetRequest.class.getSimpleName();

    private String channelName;
    // Constructor; no arguments as there are no instance fields
    public GetRequest(String channel) {
        if (channel == null)
            throw new NullPointerException();

        this.channelName = channel;
    }
    public String GetChannelName() {return channelName;}
    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("channel", channelName);
        return obj;
    }

    // Tries to deserialize a ReadRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static GetRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // construct the new object to return
            String channel      = (String)obj.get("channel");
            return new GetRequest(channel);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}