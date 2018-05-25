package nl.corwur.cytoscape.neo4j.internal.ui.importgraph.querytemplate;

import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ParameterDialog extends JDialog {  //NOSONAR, hierarchy > 5

    private transient Map<String, Class<?>> parameterTypes;
    private transient Map<String, Object> parameters;
    private boolean ok;

    public ParameterDialog(Frame owner, Map<String, Class<?>> parameterTypes) {
        super(owner);
        this.parameterTypes = parameterTypes;
        this.parameters = new HashMap<>();
    }

    public void showDialog() {

        setTitle("Query parameters");
        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");

        JTable parameterTable = new JTable();
        parameterTable.setBorder(new MatteBorder(1, 1, 0, 0, Color.BLACK));
        parameterTable.setIntercellSpacing(new Dimension(4, 4));
        parameterTable.setRowHeight(24);
        parameterTable.setShowHorizontalLines(true);
        DefaultTableModel model = new DefaultTableModel(getParametersAsRows(), new String[]{"Parameter", "Value"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }
        };
        parameterTable.setModel(model);
        parameterTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        parameterTable.getModel().addTableModelListener(e -> {
            String key = (String) parameterTable.getValueAt(e.getFirstRow(), 0);
            Object value = ((TableModel) e.getSource()).getValueAt(e.getFirstRow(), e.getColumn());
            parameters.put(key, value);
        });

        okButton.addActionListener(e -> {
            ok = true;
            ParameterDialog.this.dispose();
        });
        cancelButton.addActionListener(e -> {
            ok = false;
            ParameterDialog.this.dispose();
        });

        JPanel topPanel = new JPanel();
        topPanel.add(parameterTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        add(topPanel);
        add(buttonPanel, BorderLayout.SOUTH);

        DialogMethods.center(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(true);
        pack();
        setVisible(true);
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    private Object[][] getParametersAsRows() {

        Object[][] rows = new Object[parameterTypes.size()][2];
        int i = 0;
        for (Map.Entry<String, Class<?>> entry : parameterTypes.entrySet()) {
            rows[i][0] = entry.getKey();
            rows[i][1] = "";
            i++;
        }
        return rows;
    }

    public boolean isOk() {
        return ok;
    }

    public static void main(String[] args) {
        Map<String, Class<?>> parameters = new HashMap<>();
        parameters.put("param1", String.class);
        parameters.put("param2", Long.class);
        parameters.put("param3", String.class);

        ParameterDialog dialog = new ParameterDialog(null, parameters);
        dialog.showDialog();
    }
}
