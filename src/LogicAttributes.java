import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class LogicAttributes implements Serializable {
    public Color color;
    public String name;
    public int logicId;

    public ArrayList<ConnectorAttributes> inputAttributes;
    public ArrayList<ConnectorAttributes> outputAttributes;

    public LogicAttributes(int logicId, String name, boolean verticalName, Color color, ArrayList<ConnectorAttributes> inputAttributes, ArrayList<ConnectorAttributes> outputAttributes){
        this.logicId = logicId;
        this.name = name;
        this.color = color;
        this.inputAttributes = new ArrayList<>(inputAttributes);
        this.outputAttributes = new ArrayList<>(outputAttributes);
    }


    public LogicAttributes lightClone() {
        //ArrayList<Connector> inputConnectorsCopy = new ArrayList<>(inputConnectors);
        //ArrayList<Connector> outputConnectorsCopy = new ArrayList<>(outputConnectors);
        //return new LogicAttributes(name, verticalName, color, inputConnectorsCopy, outputConnectorsCopy);
        return null;
    }

}
