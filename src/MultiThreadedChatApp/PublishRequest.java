package MultiThreadedChatApp;

import org.json.simple.JSONObject;

public class PublishRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            PublishRequest.class.getSimpleName();

    private Message message;
    private String identity;

    // Constructor; throws NullPointerException if message is null.
    public PublishRequest(String identity,Message message) {
        // check for null
        if (message == null)
            throw new NullPointerException();
        this.message = message;
        this.identity = identity;
    }

    Message getMessage() { return message; }
    String getIdentity(){return  identity;}

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("identity", identity);
        obj.put("body",      message.getBody());
        obj.put("author",    message.getAuthor());
        obj.put("timestamp", message.getTimestamp());
        return obj;
    }

    // Tries to deserialize a PostRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static PublishRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize posted message

            String identity      = (String)obj.get("identity");
            String body      = (String)obj.get("body");
            String author    = (String)obj.get("author");
            long   timestamp = (long)obj.get("timestamp");
            // construct the object to return (checking for nulls)
            return new PublishRequest(identity,new Message(body,author,timestamp));
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}