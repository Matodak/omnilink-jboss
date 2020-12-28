package mbean;

public interface NumberedComponentMBean extends ComponentMBean
{
    public int getNumber();
    public String getName();
}