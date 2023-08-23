package sosohappy.authservice.oauth2.userinfo;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public abstract class UserInfo {

    protected Map<String, Object> attributes;

    public abstract String getProviderId();
    public abstract String getNickname();

}
