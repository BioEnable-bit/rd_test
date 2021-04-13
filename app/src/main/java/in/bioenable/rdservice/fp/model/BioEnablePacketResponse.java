package in.bioenable.rdservice.fp.model;

/**
 * Created by RND on 3/21/2018.
 */

public class BioEnablePacketResponse {

    private String is_registered;
    private String is_need_to_update;
    private String NewVersPath;
    private String md5;
    private String key_validity;
    private String signed_device_public_key;
    private String newCerPath;
    private String uuid_value;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

    private static final BioEnablePacketResponse instance = new BioEnablePacketResponse();

    private BioEnablePacketResponse(){};

    public static BioEnablePacketResponse getInstance() {
        return instance;
    }

    public String getIs_registered() {
        return is_registered;
    }

    public BioEnablePacketResponse setIs_registered(String is_registered) {
        this.is_registered = is_registered;
        return this;
    }

    public String getIs_need_to_update() {
        return is_need_to_update;
    }

    public BioEnablePacketResponse setIs_need_to_update(String is_need_to_update) {
        this.is_need_to_update = is_need_to_update;
        return this;
    }

    public String getNewVersPath() {
        return NewVersPath;
    }

    public BioEnablePacketResponse setNewVersPath(String newVersPath) {
        NewVersPath = newVersPath;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public BioEnablePacketResponse setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getKey_validity() {
        return key_validity;
    }

    public BioEnablePacketResponse setKey_validity(String key_validity) {
        this.key_validity = key_validity;
        return this;
    }

    public String getSigned_device_public_key() {
        return signed_device_public_key;
    }

    public BioEnablePacketResponse setSigned_device_public_key(String signed_device_public_key) {
        this.signed_device_public_key = signed_device_public_key;
        return this;
    }

    public String getNewCerPath() {
        return newCerPath;
    }

    public BioEnablePacketResponse setNewCerPath(String newCerPath) {
        this.newCerPath = newCerPath;
        return this;
    }

    public String getUuid_value() {
        return uuid_value;
    }

    public BioEnablePacketResponse setUuid_value(String uuid_value) {
        this.uuid_value = uuid_value;
        return this;
    }

    @Override
    public String toString() {
        return  "BioEnablePacketResponse: \n" +
                "is_registered: " + is_registered + "\n" +
                "is_need_to_update: " + is_need_to_update + "\n" +
                "NewVersPath: " + NewVersPath + "\n" +
                "md5: " + md5 + "\n" +
                "key_validity: " + key_validity + "\n" +
                "signed_device_public_key: " + signed_device_public_key + "\n" +
                "newCerPath: " + newCerPath + "\n" +
                "token: "+token+"\n"+
                "uuid_value: " + uuid_value + "\n";
    }
}
