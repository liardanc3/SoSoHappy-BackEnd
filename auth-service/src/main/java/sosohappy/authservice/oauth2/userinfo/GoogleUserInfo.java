package sosohappy.authservice.oauth2.userinfo;


import java.util.Map;

public class GoogleUserInfo extends UserInfo{

    public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("name");
    }

}
