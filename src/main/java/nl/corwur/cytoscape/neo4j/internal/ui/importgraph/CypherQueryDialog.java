package nl.corwur.cytoscape.neo4j.internal.ui.importgraph;

import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;

import javax.swing.*;
import java.awt.*;

public class CypherQueryDialog extends JDialog { //NOSONAR, hierarchy > 5

    private static final String INITIAL_QUERY = "match (n)-[r]->(m) return n,r,m LIMIT 25";
    private String cypherQuery;
    private boolean executeQuery;
    private final String[] visualStyles;
    private String network;
    private String visualStyleTitle;

    public CypherQueryDialog(Frame owner, String[] visualStyles) {
        super(owner);
        this.visualStyles = visualStyles;
    }

    public void showDialog() {

        setTitle("Execute Cypher Query");
        JTextArea queryText = new JTextArea(20,80);
        queryText.setText(INITIAL_QUERY);
        JComboBox visualStyleComboBox = new JComboBox(visualStyles);
        JLabel visualStyleLabel = new JLabel("Visual Style");
        JTextField networkNameField = new JTextField("",30);
        JLabel networkNameLabel = new JLabel("network");


        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            executeQuery = false;
            CypherQueryDialog.this.dispose();
        });
        JButton executButton = new JButton("Execute Query");
        executButton.addActionListener(e ->{
            executeQuery = true;
            cypherQuery = queryText.getText();
            network = networkNameField.getText();
            visualStyleTitle = (String) visualStyleComboBox.getSelectedItem();
            CypherQueryDialog.this.dispose();
        });

        JPanel topPanel =new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel queryPanel =new JPanel();
        JPanel buttonPanel =new JPanel();
        topPanel.add(networkNameLabel);
        topPanel.add(networkNameField);
        topPanel.add(visualStyleLabel);
        topPanel.add(visualStyleComboBox);
        queryPanel.add(queryText);
        buttonPanel.add(cancelButton);
        buttonPanel.add(executButton);
        add(topPanel, BorderLayout.NORTH);
        add(queryPanel);
        add(buttonPanel,  BorderLayout.SOUTH);

        DialogMethods.center(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(true);
        pack();
        setVisible(true);
    }

    public String getCypherQuery() {
        return cypherQuery;
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
        CypherQueryDialog dialog = new CypherQueryDialog(null, new String[]{ "v1" , "v2"});
        dialog.showDialog();
    }
}
