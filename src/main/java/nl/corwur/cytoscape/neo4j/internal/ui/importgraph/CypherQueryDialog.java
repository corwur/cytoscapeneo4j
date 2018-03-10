package nl.corwur.cytoscape.neo4j.internal.ui.importgraph;

import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class CypherQueryDialog extends JDialog { //NOSONAR, hierarchy > 5

    private static final String INITIAL_QUERY = "match (n)-[r]->(m) return n,r,m LIMIT 25";
    private String cypherQuery;
    private boolean executeQuery;
    private final String[] visualStyles;
    private String network;
    private String visualStyleTitle;
    private boolean explainQuery;

    public CypherQueryDialog(Frame owner, String[] visualStyles) {
        super(owner);
        this.visualStyles = visualStyles;
        this.cypherQuery = INITIAL_QUERY;
        this.network = "Network";
    }

    public CypherQueryDialog(Frame owner, String[] visualStyles, String cypherQuery, String network) {
        super(owner);
        this.visualStyles = visualStyles;
        this.cypherQuery = cypherQuery;
        this.network = network;
    }

    public void showDialog() {

        setTitle("Execute Cypher Query");

        JEditorPane  queryText = new JEditorPane ();
        queryText.setText(cypherQuery);
        JScrollPane queryTextScrollPane = new JScrollPane(queryText);
        queryTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JComboBox visualStyleComboBox = new JComboBox(visualStyles);
        JLabel visualStyleLabel = new JLabel("Visual Style");
        JTextField networkNameField = new JTextField(network,30);
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

        JButton explainButton = new JButton("Explain");
        executButton.addActionListener(e ->{
            explainQuery = true;
            cypherQuery = queryText.getText();
            network = networkNameField.getText();
            visualStyleTitle = (String) visualStyleComboBox.getSelectedItem();
            CypherQueryDialog.this.dispose();
        });

        JPanel topPanel =new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel queryPanel =new JPanel();
        queryPanel.setLayout(new BorderLayout());
        JPanel buttonPanel =new JPanel();
        topPanel.add(networkNameLabel);
        topPanel.add(networkNameField);
        topPanel.add(visualStyleLabel);
        topPanel.add(visualStyleComboBox);
        queryPanel.add(queryTextScrollPane, BorderLayout.CENTER);
        buttonPanel.add(cancelButton);
        //TODO: buttonPanel.add(explainButton);
        buttonPanel.add(executButton);

        add(topPanel, BorderLayout.NORTH);
        add(queryPanel);
        add(buttonPanel,  BorderLayout.SOUTH);

        setMinimumSize(new Dimension(400,300));
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

    public boolean isExplainQuery() {
        return explainQuery;
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
