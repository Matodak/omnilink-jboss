package mbean;

public class Code extends NumberedComponentMBeanSupport implements CodeMBean
{
    public Code(Integer number, String name)
    {
        super(number, name);
    }

    public void updateStatus()
    {
        // Nothing to do for now
    }
}