package MultiThreadedChatApp;

import org.json.simple.*;  // required for JSON encoding and decoding

// Abstract super class for all requests
public abstract class Request implements JSONAware {
    // Serializes this object into a JSONObject
    public abstract Object toJSON();

    // Serializes this object and returns the JSON as a string
    public String toJSONString() { return toJSON().toString(); }

    // Prints this object in JSON representation
    public String toString() { return toJSONString(); }
}