package nl.corwur.cytoscape.neo4j.internal.ui.importgraph.querytemplate;

import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.CypherQueryTemplate;
import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.mapping.CopyAllMappingStrategy;
import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_END;
import static java.awt.BorderLayout.PAGE_START;

public class EditQueryTemplateDialog extends JDialog { //NOSONAR, hierarchy > 5

    public static final String STRING = "String";
    public static final String INTEGER = "Integer";
    public static final String LONG = "Long";
    public static final String DOUBLE = "Double";
    private final Map<String, Class<?>> parameterTypes;
    private String networkName;
    private String referenceColumn;
    private String name;
    private String cypherQuery;
    private AttributesPanel attributesPanel;
    private EditQueryPanel editQueryPanel;
    private ParametersPanel parametersPanel;
    private boolean ok = false;

    EditQueryTemplateDialog() {
        this.cypherQuery = null;
        name = "New query";
        parameterTypes = new HashMap<>();
        referenceColumn = "refId";
        networkName = "Network";

    }

    public EditQueryTemplateDialog(String name, String cypherQuery, Map<String, Class<?>> parameterTypes, String referenceColumn, String networkName) {
        this.name = name;
        this.referenceColumn = referenceColumn;
        this.networkName = networkName;
        this.cypherQuery = cypherQuery;
        this.parameterTypes = parameterTypes;

    }

    public void showDialog() {
        setTitle("Edit Query");

        attributesPanel = new AttributesPanel(name, referenceColumn);
        editQueryPanel = new EditQueryPanel(cypherQuery);

        parametersPanel = new ParametersPanel(parameterTypes);
        ButtonPanel buttonPanel = new ButtonPanel(ev -> setOk(), ev -> this.dispose());

        JPanel panel = new JPanel();
        setLayout(new FlowLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(attributesPanel);
        panel.add(editQueryPanel);
        panel.add(parametersPanel);
        panel.add(buttonPanel);
        add(panel);
        DialogMethods.centerAndShow(this);
    }

    private void setOk() {
        ok = true;
        this.dispose();
    }

    public CypherQueryTemplate createCypherQuery() {
        CypherQueryTemplate.Builder builder = CypherQueryTemplate.builder();
        builder.setQueryTemplate(editQueryPanel.textArea.getText());
        builder.setName(attributesPanel.nameField.getText());
        builder.addMappingStrategy(new CopyAllMappingStrategy(attributesPanel.referenceColumnField.getText(), networkName));

        for (int i = 1; i < parametersPanel.getRowCount(); i++) {
            String parameter = parametersPanel.getParameter(i);
            Class<?> parameterType = parametersPanel.getParameterType(i);
            if (parameter != null && !parameter.isEmpty() && parameterType != null) {
                builder.addParameter(parameter, parameterType);
            }
        }
        return builder.build();
    }

    public boolean isOk() {
        return ok;
    }

    private final class ButtonPanel extends JPanel {

        ButtonPanel(ActionListener okHandler, ActionListener cancelHandler) {
            JButton cancelButton = new JButton("Cancel");
            JButton okButton = new JButton("Ok");
            okButton.addActionListener(okHandler);
            cancelButton.addActionListener(cancelHandler);
            add(cancelButton);
            add(okButton);
        }
    }

    private final class AttributesPanel extends JPanel {
        JLabel nameLabel;
        JTextField nameField;

        JLabel referenceColumnLabel;
        JTextField referenceColumnField;

        AttributesPanel(String name, String referenceColumn) {
            nameLabel = new JLabel("Name");
            nameField = new JTextField(name, 20);
            referenceColumnLabel = new JLabel("Reference column");
            referenceColumnField = new JTextField(referenceColumn, 20);

            setLayout(new FlowLayout());
            add(nameLabel);
            add(nameField);
            add(referenceColumnLabel);
            add(referenceColumnField);
        }

    }

    private final class EditQueryPanel extends JPanel {
        JTextArea textArea;

        EditQueryPanel(String cypherQuery) {
            textArea = new JTextArea(20, 40);
            textArea.setText(cypherQuery);
            JScrollPane textAreaScrollPane = new JScrollPane(textArea);
            JLabel label = new JLabel("Cypher Query");
            setLayout(new BorderLayout());
            add(label, PAGE_START);
            add(textAreaScrollPane, CENTER);
        }

    }

    private final class ParametersPanel extends JPanel {

        private ParameterTableModel parameterTableModel = new ParameterTableModel();

        public ParametersPanel(Map<String, Class<?>> parameterTypes) {

            for (Map.Entry<String, Class<?>> entry : parameterTypes.entrySet()) {
                parameterTableModel.addRow(entry.getKey(), entry.getValue());
            }

            JTable jTable = new JTable();
            jTable.setModel(parameterTableModel);
            jTable.getModel().setValueAt("Parametername", 0, 0);
            jTable.getModel().setValueAt("Type", 0, 1);

            TableColumn column = jTable.getColumnModel().getColumn(1);
            JComboBox comboBox = new JComboBox();
            comboBox.addItem(STRING);
            comboBox.addItem(INTEGER);
            comboBox.addItem(LONG);
            comboBox.addItem(DOUBLE);
            column.setCellEditor(new DefaultCellEditor(comboBox));

            JLabel label = new JLabel("Parameters");
            JButton addButton = new JButton("Add parameter");
            addButton.addActionListener(e -> {
                parameterTableModel.addRow("new parameter", String.class);
                EditQueryTemplateDialog.this.pack();
            });
            JButton removeButton = new JButton("Remove");
            removeButton.addActionListener(e -> {
                int[] selected = jTable.getSelectedRows();
                for (int index : selected) {
                    if (index != 0) {
                        parameterTableModel.removeRow(index);
                    }
                }
                EditQueryTemplateDialog.this.pack();
            });
            JPanel buttons = new JPanel();
            buttons.add(addButton);
            buttons.add(removeButton);
            setLayout(new BorderLayout());
            add(label, PAGE_START);
            add(jTable, CENTER);
            add(buttons, PAGE_END);
        }

        private final class ParameterTableModel extends DefaultTableModel {

            public ParameterTableModel() {
                super(new String[]{"Parameter", "Type"}, 1);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return (row > 0) && super.isCellEditable(row, column);
            }


            public void addRow(String key, Class<?> value) {
                super.addRow(new Object[]{key, getTypeAsString(value)});
            }

            private String getTypeAsString(Class<?> clz) {
                if (clz.equals(String.class)) return STRING;
                if (clz.equals(Integer.class)) return INTEGER;
                if (clz.equals(Long.class)) return LONG;
                if (clz.equals(Double.class)) return DOUBLE;
                return null;
            }
        }

        private int getRowCount() {
            return parameterTableModel.getRowCount();
        }

        private String getParameter(int row) {
            return (String) parameterTableModel.getValueAt(row, 0);
        }

        private Class<?> getParameterType(int row) {
            return getType((String) parameterTableModel.getValueAt(row, 1));
        }

        private Class<?> getType(String type) {
            if (type == null) return null;
            switch (type) {
                case STRING:
                    return String.class;
                case INTEGER:
                    return Integer.class;
                case LONG:
                    return Long.class;
                case DOUBLE:
                    return Double.class;
                default:
                    return null;
            }
        }
    }

    public static void main(String[] args) {
        EditQueryTemplateDialog dialog = new EditQueryTemplateDialog();
        dialog.showDialog();
    }
}
