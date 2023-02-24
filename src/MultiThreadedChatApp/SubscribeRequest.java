package MultiThreadedChatApp;

import org.json.simple.JSONObject;


public class SubscribeRequest extends Request {

    private static final String _class =
            SubscribeRequest.class.getSimpleName();
    private String identity;
    private String channelName;


    public String getIdentity() { return identity;}


    public void setIdentity(String identity) {this.identity = identity;}

    public String getChannelName() {return channelName;}


    public SubscribeRequest(String identity, String chanel ){
        if (identity == null)
            throw new NullPointerException();
        if (chanel == null)
            throw new NullPointerException();
        this.identity = identity;
        this.channelName = chanel;
    }


    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("identity", identity);
        obj.put("channel", channelName);
        return obj;
    }

    public static SubscribeRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // construct the new object to return
            String identity      = (String)obj.get("identity");
            String channel      = (String)obj.get("channel");
            return new SubscribeRequest(identity,channel);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}