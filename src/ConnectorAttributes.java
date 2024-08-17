import java.awt.*;
import java.io.Serializable;

public class ConnectorAttributes implements Serializable {
    public boolean isInput;
    public String name;
    public int connectorId;

    public ConnectorAttributes(String name, boolean isInput, int connectorId){
        this.name = name;
        this.isInput = isInput;
        this.connectorId = connectorId;
    }

}
