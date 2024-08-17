import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class LogicAttributes implements Serializable {
    public Color color;
    public boolean verticalName;
    public String name;
    public int logicId;

    public ArrayList<ConnectorAttributes> inputAttributes;
    public ArrayList<ConnectorAttributes> outputAttributes;

    public LogicAttributes(int logicId, String name, boolean verticalName, Color color, ArrayList<Connector> inputConnectors, ArrayList<Connector> outputConnectors){
        this.logicId = logicId;
        this.name = name;
        this.verticalName = verticalName;
        this.color = color;
        this.inputAttributes = new ArrayList<Connector>(inputConnectors);
        this.outputAttributes = new ArrayList<Connector>(outputConnectors);
    }


    public LogicAttributes lightClone() {
        //ArrayList<Connector> inputConnectorsCopy = new ArrayList<>(inputConnectors);
        //ArrayList<Connector> outputConnectorsCopy = new ArrayList<>(outputConnectors);
        //return new LogicAttributes(name, verticalName, color, inputConnectorsCopy, outputConnectorsCopy);
        return null;
    }

}
