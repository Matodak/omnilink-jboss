package mbean;

public abstract class NumberedComponentMBeanSupport extends ComponentMBeanSupport
{
    private int number = -1;
    private String name = null;

    public NumberedComponentMBeanSupport(Integer numberInt, String zoneName)
    {
        this.number = numberInt.intValue();
        this.name = zoneName;
    }

    public int getNumber()
    {
        return number;
    }

    public String getName()
    {
        return name;
    }
}