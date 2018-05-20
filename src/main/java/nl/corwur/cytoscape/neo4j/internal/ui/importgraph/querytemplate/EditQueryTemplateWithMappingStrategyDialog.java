package nl.corwur.cytoscape.neo4j.internal.ui.importgraph.querytemplate;

import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_START;

public class EditQueryTemplateWithMappingStrategyDialog extends JDialog {  //NOSONAR, hierarchy > 5

    public static final String STRING = "String";
    public static final String LONG = "Long";
    public static final String DOUBLE = "Double";

    public void showDialog() {
        setTitle("Edit Query");

        EditQueryPanel editQueryPanel = new EditQueryPanel();
        ParametersPanel parametersPanel = new ParametersPanel();
        NodeMappingPanel nodeMappingPanel = new NodeMappingPanel();
        EdgeMappingPanel edgeMappingPanel = new EdgeMappingPanel();
        ButtonPanel buttonPanel = new ButtonPanel(ev -> this.dispose(), ev -> this.dispose());

        JPanel panel = new JPanel();
        setLayout(new FlowLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(editQueryPanel);
        panel.add(parametersPanel);
        panel.add(nodeMappingPanel);
        panel.add(edgeMappingPanel);
        panel.add(buttonPanel);
        add(panel);
        DialogMethods.centerAndShow(this);
    }

    private final class ButtonPanel extends JPanel {

        ButtonPanel(ActionListener okHandler, ActionListener cancelHandler) {
            JButton cancel = new JButton("Cancel");
            JButton ok = new JButton("Ok");
            ok.addActionListener(okHandler);
            cancel.addActionListener(cancelHandler);
            add(cancel);
            add(ok);
        }
    }

    private final class EditQueryPanel extends JPanel {
        EditQueryPanel() {
            JTextArea textArea = new JTextArea(20,40);
            JLabel label = new JLabel("Cypher Query");
            setLayout(new BorderLayout());
            add(label, PAGE_START);
            add(textArea, CENTER);
        }
    }

    private final class ParametersPanel extends JPanel {
        ParametersPanel() {
            JTable jTable = new JTable();
            jTable.setModel(new ParameterTableModel ());
            jTable.getModel().setValueAt("Parametername", 0, 0);
            jTable.getModel().setValueAt("Type", 0, 1);

            TableColumn column = jTable.getColumnModel().getColumn(1);
            JComboBox comboBox = new JComboBox();
            comboBox.addItem(STRING);
            comboBox.addItem("Integer");
            comboBox.addItem(LONG);
            comboBox.addItem(DOUBLE);
            column.setCellEditor(new DefaultCellEditor(comboBox));

            JLabel label = new JLabel("Parameters");
            setLayout(new BorderLayout());
            add(label, PAGE_START);
            add(jTable, CENTER);
        }
        private final class ParameterTableModel extends DefaultTableModel {

            public ParameterTableModel() {
                super(new String[] {"Parameter","Type"}, 5);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return (row > 0) && super.isCellEditable(row, column);
            }
        }

    }

    private final class NodeMappingPanel extends JPanel {
        NodeMappingPanel() {
            JTable jTable = new JTable();
            jTable.setModel(new DefaultTableModel(5,4));
            jTable.setValueAt("Columnname",0,0);
            jTable.setValueAt("Columntype",0,1);
            jTable.setValueAt("Mappingtype",0,2);
            jTable.setValueAt("Mapping",0,3);

            TableColumn columnTypeColumn = jTable.getColumnModel().getColumn(1);
            JComboBox comboBoxColumnType = new JComboBox();
            comboBoxColumnType.addItem(STRING);
            comboBoxColumnType.addItem(LONG);
            comboBoxColumnType.addItem(DOUBLE);
            columnTypeColumn.setCellEditor(new DefaultCellEditor(comboBoxColumnType));

            TableColumn mappingTypeColumn = jTable.getColumnModel().getColumn(2);
            JComboBox comboBoxMappingType = new JComboBox();
            comboBoxMappingType.addItem("Id");
            comboBoxMappingType.addItem("NamedParameterProperty");
            comboBoxMappingType.addItem("Expression");
            comboBoxMappingType.addItem("Label");
            mappingTypeColumn.setCellEditor(new DefaultCellEditor(comboBoxMappingType));

            JLabel label = new JLabel("Node to column mapping");
            setLayout(new BorderLayout());
            add(label, PAGE_START);
            add(jTable, CENTER);
        }
    }

    private final class EdgeMappingPanel extends JPanel {
        EdgeMappingPanel() {
            JTable jTable = new JTable();
            jTable.setModel(new DefaultTableModel(10, 4));
            jTable.setValueAt("Columnname",0,0);
            jTable.setValueAt("Columntype",0,1);
            jTable.setValueAt("Mappingtype",0,2);
            jTable.setValueAt("Mapping",0,3);

            TableColumn columnTypeColumn = jTable.getColumnModel().getColumn(1);
            JComboBox comboBoxColumnType = new JComboBox();
            comboBoxColumnType.addItem(STRING);
            comboBoxColumnType.addItem(LONG);
            comboBoxColumnType.addItem(DOUBLE);
            columnTypeColumn.setCellEditor(new DefaultCellEditor(comboBoxColumnType));

            TableColumn mappingTypeColumn = jTable.getColumnModel().getColumn(2);
            JComboBox comboBoxMappingType = new JComboBox();
            comboBoxMappingType.addItem("Id");
            comboBoxMappingType.addItem("Expression");
            mappingTypeColumn.setCellEditor(new DefaultCellEditor(comboBoxMappingType));

            JLabel label = new JLabel("Edge to column mapping");
            setLayout(new BorderLayout());
            add(label, PAGE_START);
            add(jTable, CENTER);
        }
    }

    public static void main(String[] args) {
        EditQueryTemplateWithMappingStrategyDialog dialog = new EditQueryTemplateWithMappingStrategyDialog();
        dialog.showDialog();
    }
}
