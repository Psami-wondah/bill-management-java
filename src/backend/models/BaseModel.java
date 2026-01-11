package backend.models;

import java.io.Serializable;

public interface BaseModel extends Serializable {

    public String getId();

    public void save();
}