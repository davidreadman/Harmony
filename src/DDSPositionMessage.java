import java.beans.*;
//https://docs.oracle.com/javase/tutorial/javabeans/writing/properties.html#bound

public class DDSPositionMessage
{
    private String dDSPositionMessage ="";
    private PropertyChangeSupport mPcs = new PropertyChangeSupport(this);

    public String getDDSPositionMessage(){
        return dDSPositionMessage;
    }
    public void setDDSPositionMessage(String PosMes){
        String oldDDSPositionMessage = dDSPositionMessage;
        dDSPositionMessage = PosMes;
        mPcs.firePropertyChange("ddsPositionMessage",oldDDSPositionMessage,PosMes);

    }
    public void
    addPropertyChangeListener(PropertyChangeListener listener) {
        mPcs.addPropertyChangeListener(listener);
    }

    public void
    removePropertyChangeListener(PropertyChangeListener listener) {
        mPcs.removePropertyChangeListener(listener);
    }
}
