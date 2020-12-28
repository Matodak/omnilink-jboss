package client;

import java.awt.Component;
import java.awt.event.WindowEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class ClientFrame extends GeneratedClientFrame
{
	private DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			String text = value.toString().replace('\n' , ' ');
			return super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
		}
	};
	
    public ClientFrame(TableModel unitModel, TableModel zoneModel, TableModel tempModel, TableModel messageModel, TableModel systemModel, TableModel buttonModel, TableModel codeModel, TableModel eventLogModel)
    {
        setSize(600, 400);
        if (unitModel != null)
            unitTable.setModel(unitModel);
        if (zoneModel != null)
            zoneTable.setModel(zoneModel);
        if (tempModel != null)
            tempTable.setModel(tempModel);
        if (messageModel != null)
            messageTable.setModel(messageModel);
        if (systemModel != null) {
        	systemTable.setDefaultRenderer(Object.class, renderer);
            systemTable.setModel(systemModel);
        }
        if (buttonModel != null)
            buttonTable.setModel(buttonModel);
        if (codeModel != null)
            codeTable.setModel(codeModel);
        if (eventLogModel != null) {
        	eventLogTable.setDefaultRenderer(Object.class, renderer);
        	eventLogTable.setModel(eventLogModel);
        }
    }

    void this_windowClosing(WindowEvent e)
    {
        System.exit(0);
    }

}