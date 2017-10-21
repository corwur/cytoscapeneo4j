package nl.corwur.cytoscape.neo4j.internal.ui.connect;

import nl.corwur.cytoscape.neo4j.internal.neo4j.ConnectionParameter;
import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Predicate;

class ConnectDialog extends JDialog {

    private static final String CANCEL_CMD = "cancel";
    private static final String OK_CMD = "ok";
    private JTextField usernameField = new JTextField("neo4j");
    private JTextField hostnameField = new JTextField("localhost");
    private JPasswordField password = new JPasswordField();
    private boolean ok = false;
    private final Predicate<ConnectionParameter> connectionCheck;

    ConnectDialog(JFrame jFrame, Predicate<ConnectionParameter> connectionCheck, String hostname, String username) {
        super(jFrame);
        this.connectionCheck = connectionCheck;
        usernameField.setText(username);
        hostnameField.setText(hostname);
    }

    void showConnectDialog() {
        init();
        DialogMethods.center(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(true);
        pack();
        setSize(380,200);
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

    private ConnectionParameter getParameters() {
        return new ConnectionParameter(hostnameField.getText(), usernameField.getText(), password.getPassword());
    }

    private void init() {

        setTitle("Connect to Neo4J");

        JPanel topPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));


        JButton okButton = new JButton("Connect");
        okButton.addActionListener(this::ok);
        okButton.setActionCommand(OK_CMD);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(ae -> dispose());
        cancelButton.setActionCommand(CANCEL_CMD);

        JLabel hostnameLabel = new JLabel("Hostname");
        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");
        password = new JPasswordField();

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        topPanel.add(hostnameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        topPanel.add(hostnameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        topPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        topPanel.add(password, gbc);

        this.add(topPanel);
        this.add(buttonPanel, BorderLayout.SOUTH);

    }

    private void ok(ActionEvent ae) {
        if(connectionCheck.test(getParameters())) {
            ok = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, " Invalid connection parameters");
        }
    }

    public static void main(String[] args) {
        ConnectDialog connectDialog = new ConnectDialog(null, connectionParameter -> true, "localhost", "neo4j");
        connectDialog.showConnectDialog();

    }

    public String getHostname() {
        return hostnameField.getText();
    }

    public String getUsername() {
        return usernameField.getText();
    }

}
