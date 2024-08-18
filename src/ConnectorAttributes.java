import java.io.Serializable;

public class ConnectorAttributes implements Serializable {
    public boolean isInput;
    public String name;
    public int connectorId;

    public ConnectorAttributes(int connectorId, String name, boolean isInput){
        this.connectorId = connectorId;
        this.name = name;
        this.isInput = isInput;
    }

}
