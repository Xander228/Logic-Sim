import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class LogicAttributes implements Serializable {
    private Color color;
    private boolean verticalName;
    private String name;
    private int logicId;

    private ArrayList<Connector> inputConnectors;
    private ArrayList<Connector> outputConnectors;

    public LogicAttributes(String name, boolean verticalName, Color color, ArrayList<Connector> inputConnectors, ArrayList<Connector> outputConnectors){
        this.color = color;
        this.verticalName = verticalName;
        this.name = name;
        this.inputConnectors = new ArrayList<Connector>(inputConnectors);
        this.outputConnectors = new ArrayList<Connector>(outputConnectors);
    }


    public LogicAttributes lightClone() {
        ArrayList<Connector> inputConnectorsCopy = new ArrayList<>(inputConnectors);
        ArrayList<Connector> outputConnectorsCopy = new ArrayList<>(outputConnectors);
        return new LogicAttributes(name, verticalName, color, inputConnectorsCopy, outputConnectorsCopy);
    }

    public LogicAttributes deepClone() {
        ArrayList<Connector> inputConnectorsCopy = new ArrayList<>(inputConnectors);
        ArrayList<Connector> outputConnectorsCopy = new ArrayList<>(outputConnectors);
        return new LogicAttributes(name, verticalName, color, inputConnectorsCopy, outputConnectorsCopy);
    }

}
