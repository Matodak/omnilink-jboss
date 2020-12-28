package client;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class GeneratedClientFrame extends JFrame {
    private JTabbedPane jTabbedPane1 = new JTabbedPane();
    private JPanel unitPanel = new JPanel();
    private JScrollPane jScrollPane1 = new JScrollPane();
    protected JTable zoneTable = new JTable();
    private BorderLayout borderLayout1 = new BorderLayout();
    private JPanel Units = new JPanel();
    private BorderLayout borderLayout2 = new BorderLayout();
    private JScrollPane jScrollPane2 = new JScrollPane();
    protected JTable unitTable = new JTable();
    private JPanel tempPanel = new JPanel();
    private BorderLayout borderLayout3 = new BorderLayout();
    private JScrollPane jScrollPane3 = new JScrollPane();
    protected JTable tempTable = new JTable();
    private JPanel jPanel1 = new JPanel();
    private BorderLayout borderLayout4 = new BorderLayout();
    private JScrollPane jScrollPane4 = new JScrollPane();
    protected JTable messageTable = new JTable();
    private JPanel SystemPanel = new JPanel();
    private JScrollPane jScrollPane5 = new JScrollPane();
    private BorderLayout borderLayout5 = new BorderLayout();
    protected JTable systemTable = new JTable();
    private JPanel buttonPanel = new JPanel();
    private JScrollPane jScrollPane6 = new JScrollPane();
    private BorderLayout borderLayout6 = new BorderLayout();
    protected JTable buttonTable = new JTable();
    private JPanel codePanel = new JPanel();
    private JScrollPane jScrollPane7 = new JScrollPane();
    private BorderLayout borderLayout7 = new BorderLayout();
    protected JTable codeTable = new JTable();
    private JPanel eventLogPanel = new JPanel();
    private JScrollPane jScrollPane8 = new JScrollPane();
    private BorderLayout borderLayout8 = new BorderLayout();
    protected JTable eventLogTable = new JTable();
    
    public GeneratedClientFrame() {
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        unitPanel.setLayout(borderLayout1);
        this.setTitle("Aegis Client");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                this_windowClosing(e);
            }
        });
        Units.setLayout(borderLayout2);
        tempPanel.setLayout(borderLayout3);
        jPanel1.setLayout(borderLayout4);
        SystemPanel.setLayout(borderLayout5);
        buttonPanel.setLayout(borderLayout6);
        codePanel.setLayout(borderLayout7);
        eventLogPanel.setLayout(borderLayout8);
        unitPanel.add(jScrollPane1, BorderLayout.CENTER);
        jTabbedPane1.add(SystemPanel,  "System");
        jTabbedPane1.add(tempPanel,  "Temperature");
        jTabbedPane1.add(jPanel1,  "Messages");        
        jTabbedPane1.add(buttonPanel,  "Buttons");
        jTabbedPane1.add(Units,  "Units");
        jTabbedPane1.add(unitPanel,   "Zones");
        jTabbedPane1.add(eventLogPanel,  "Event Log");
        jTabbedPane1.add(codePanel,  "Codes");
        jPanel1.add(jScrollPane4, BorderLayout.NORTH);
        jScrollPane4.getViewport().add(messageTable, null);
        Units.add(jScrollPane2, BorderLayout.NORTH);
        jScrollPane2.getViewport().add(unitTable, null);
        jScrollPane1.getViewport().add(zoneTable, null);
        this.getContentPane().add(jTabbedPane1, BorderLayout.CENTER);
        tempPanel.add(jScrollPane3, BorderLayout.NORTH);
        jScrollPane3.getViewport().add(tempTable, null);
        SystemPanel.add(jScrollPane5, BorderLayout.NORTH);
        jScrollPane5.getViewport().add(systemTable, null);
        buttonPanel.add(jScrollPane6, BorderLayout.NORTH);
        jScrollPane6.getViewport().add(buttonTable, null);
        codePanel.add(jScrollPane7, BorderLayout.NORTH);
        jScrollPane7.getViewport().add(codeTable, null);
        eventLogPanel.add(jScrollPane8, BorderLayout.NORTH);
        jScrollPane8.getViewport().add(eventLogTable, null);
    }

    void this_windowClosing(WindowEvent e) {
        System.exit(0);
    }
}