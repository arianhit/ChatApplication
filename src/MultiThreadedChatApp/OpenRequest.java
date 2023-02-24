package MultiThreadedChatApp;

import org.json.simple.JSONObject;

public class OpenRequest extends Request {

    private static final String _class =
            OpenRequest.class.getSimpleName();
    private String identity;
    private String channelName;




    public OpenRequest(String identity, String chanel ){
        if (identity == null)
            throw new NullPointerException();
        if (chanel == null)
            throw new NullPointerException();
        this.identity = identity;
        this.channelName = chanel;
    }
    public String GetIdentity() {return identity;}
    public String GetChannelName() {return channelName;}

    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("identity", identity);
        obj.put("channel", channelName);
        return obj;
    }

    public static OpenRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // construct the new object to return
            String identity      = (String)obj.get("identity");
            String channel      = (String)obj.get("channel");
            return new OpenRequest(identity,channel);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
