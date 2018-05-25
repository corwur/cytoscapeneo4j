package nl.corwur.cytoscape.neo4j.internal.ui.importgraph.all;

import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;

import javax.swing.*;
import java.awt.*;

public class ImportAllNodesAndEdgesDialog extends JDialog { //NOSONAR, hierarchy > 5

    private boolean executeQuery;
    private final String[] visualStyles;
    private String network;
    private String visualStyleTitle;
    private boolean explainQuery;

    public ImportAllNodesAndEdgesDialog(Frame owner, String[] visualStyles) {
        super(owner);
        this.visualStyles = visualStyles;
        this.network = "Network";
    }

    public ImportAllNodesAndEdgesDialog(Frame owner, String[] visualStyles, String cypherQuery, String network) {
        super(owner);
        this.visualStyles = visualStyles;
        this.network = network;
    }

    public void showDialog() {

        setTitle("Execute Cypher Query");


        JComboBox visualStyleComboBox = new JComboBox(visualStyles);
        JLabel visualStyleLabel = new JLabel("Visual Style");
        JTextField networkNameField = new JTextField(network, 30);
        JLabel networkNameLabel = new JLabel("Network, (all neo4j nodes with this label)");
        JLabel warning = new JLabel("This could result in a very large graph!");


        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            executeQuery = false;
            ImportAllNodesAndEdgesDialog.this.dispose();
        });
        JButton executButton = new JButton("Import");
        executButton.addActionListener(e -> {
            executeQuery = true;
            network = networkNameField.getText();
            visualStyleTitle = (String) visualStyleComboBox.getSelectedItem();
            ImportAllNodesAndEdgesDialog.this.dispose();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel buttonPanel = new JPanel();
        topPanel.add(networkNameLabel);
        topPanel.add(networkNameField);
        topPanel.add(visualStyleLabel);
        topPanel.add(visualStyleComboBox);
        buttonPanel.add(cancelButton);
        //TODO: buttonPanel.add(explainButton);
        buttonPanel.add(executButton);
        buttonPanel.add(warning);
        add(topPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        DialogMethods.center(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(true);
        pack();
        setVisible(true);
    }

    public boolean isExecuteQuery() {
        return executeQuery;
    }

    public String getNetwork() {
        return network;
    }

    public String getVisualStyleTitle() {
        return visualStyleTitle;
    }

    public static void main(String[] args) {
        ImportAllNodesAndEdgesDialog dialog = new ImportAllNodesAndEdgesDialog(null, new String[]{"v1", "v2"});
        dialog.showDialog();
    }
}
