package server.mine.servertest;

import java.io.Serializable;

public class ReturnMsg implements Serializable {
    Integer code;
    String objectType;
    Object object;

    @Override
    public String toString() {
        return "ReturnMsg{" +
                "code=" + code +
                ", objectType='" + objectType + '\'' +
                ", object=" + object +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
