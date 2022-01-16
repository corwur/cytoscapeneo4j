package nl.corwur.cytoscape.neo4j.internal.ui.connect;

import nl.corwur.cytoscape.neo4j.internal.neo4j.ConnectionParameter;
import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;

import javax.swing.*;
import java.awt.*;
import java.util.function.Predicate;

class ConnectDialog extends JDialog { //NOSONAR , hierarchy level > 5

    private static final String CANCEL_CMD = "cancel";
    private static final String OK_CMD = "ok";
    private JTextField usernameField = new JTextField("neo4j");
    private JTextField hostnameField = new JTextField("localhost");
    private JTextField databaseField = new JTextField("database");
    private JPasswordField password = new JPasswordField();
    private boolean ok = false;
    private final transient Predicate<ConnectionParameter> connectionCheck;

    ConnectDialog(JFrame jFrame, Predicate<ConnectionParameter> connectionCheck, String hostname, String username, String database) {
        super(jFrame);
        this.connectionCheck = connectionCheck;
        usernameField.setText(username);
        hostnameField.setText(hostname);
        databaseField.setText(database);
    }

    void showConnectDialog() {
        init();
        DialogMethods.center(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(true);
        pack();
        setSize(380, 240);
        setVisible(true);
    }

    boolean isOk() {
        return ok;
    }

    void setHostname(String hostname) {
        hostnameField.setText(hostname);
    }

    void setUsernameField(String username) {
        usernameField.setText(username);
    }

    void setDatabaseField(String database){
        databaseField.setText(database);
    }

    private ConnectionParameter getParameters() {
        return new ConnectionParameter(hostnameField.getText(), usernameField.getText(), password.getPassword(), databaseField.getText());
    }

    private void init() {

        setTitle("Connect to Neo4J");

        JPanel topPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton okButton = new JButton("Connect");
        okButton.addActionListener(e -> ok());
        okButton.setActionCommand(OK_CMD);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(ae -> dispose());
        cancelButton.setActionCommand(CANCEL_CMD);

        JLabel hostnameLabel = new JLabel("Hostname");
        JLabel databaseLabel = new JLabel("Database");
        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");
        password = new JPasswordField();

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        //Hostname
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        topPanel.add(hostnameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        topPanel.add(hostnameField, gbc);

        //Database name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(databaseLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        topPanel.add(databaseField, gbc);

        //Username
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        topPanel.add(usernameField, gbc);

        //Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        topPanel.add(password, gbc);

        this.add(topPanel);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.getRootPane().setDefaultButton(okButton);

    }

    private void ok() {
        if (connectionCheck.test(getParameters())) {
            ok = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Cannot connect to Neo4j");
        }
    }

    public static void main(String[] args) {
        ConnectDialog connectDialog = new ConnectDialog(null, connectionParameter -> true, "localhost", "neo4j", "neo4j");
        connectDialog.showConnectDialog();

    }

    public String getHostname() {
        return hostnameField.getText();
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getDatabase() {
        return databaseField.getText();
    }
}
