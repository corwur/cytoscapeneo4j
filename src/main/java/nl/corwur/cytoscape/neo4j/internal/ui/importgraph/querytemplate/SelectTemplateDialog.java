package nl.corwur.cytoscape.neo4j.internal.ui.importgraph.querytemplate;

import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.mapping.CopyAllMappingStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.CypherQueryTemplate;
import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.provider.CypherQueryTemplateDirectoryProvider;
import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SelectTemplateDialog extends JDialog {  //NOSONAR, hierarchy > 5


    private static final class TemplateQueryListEntry {
        final String id;
        final String name;

        private TemplateQueryListEntry(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final String[] visualStyles;
    private final transient CypherQueryTemplateDirectoryProvider provider;
    private String templateDir;
    private final transient Consumer<String> templateDirectoryListener;
    private boolean ok;
    private String networkName;
    private String visualStyle;
    private String cypherQueryTemplateId;

    public SelectTemplateDialog(JFrame jFrame, String[] visualStyles, String templateDir, Consumer<String> templateDirectoryListener) {
        super(jFrame);
        this.visualStyles = visualStyles;
        this.templateDir = templateDir;
        this.templateDirectoryListener = templateDirectoryListener;
        this.provider = CypherQueryTemplateDirectoryProvider.create();
    }

    public void showDialog() {

        setTitle("Select Query");

        TemplateQueryListEntry[] templateQueryListEntries = getQueryTemplatesFromDir();
        if(templateQueryListEntries == null) {
            this.dispose();
            return;
        }


        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");
        JTextField networkNameField = new JTextField();
        JLabel networkNameLabel = new JLabel("Networkname");
        JComboBox visualStyleComboBox = new JComboBox(visualStyles);
        JLabel visualStyleLabel = new JLabel("Visual Style");
        SelectQueryPanel queryListPanel = new SelectQueryPanel(templateQueryListEntries);
        JLabel queryListLabel = new JLabel("Queries");

        okButton.addActionListener(e -> {
            networkName = networkNameField.getText();
            if (visualStyleComboBox.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "No visualstyle selected", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            visualStyle = visualStyleComboBox.getSelectedItem().toString();
            if (queryListPanel.getQueryList().getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "No query selected", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cypherQueryTemplateId = ((TemplateQueryListEntry) queryListPanel.getQueryList().getSelectedValue()).id;
            ok = true;
            SelectTemplateDialog.this.dispose();
        });

        cancelButton.addActionListener(e -> {
            ok = false;
            SelectTemplateDialog.this.dispose();
        });

        JPanel topPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(networkNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        topPanel.add(networkNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(visualStyleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        topPanel.add(visualStyleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(queryListLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        topPanel.add(queryListPanel, gbc);

        add(topPanel);
        add(buttonPanel, BorderLayout.SOUTH);

        DialogMethods.centerAndShow(this);
    }


    public boolean isOk() {
        return ok;
    }

    public String getNetworkName() {
        return networkName;
    }

    public String getVisualStyle() {
        return visualStyle;
    }

    public CypherQueryTemplate getCypherQueryTemplate() {
        return provider.getCypherQueryTemplate(Long.valueOf(cypherQueryTemplateId)).orElse(null);
    }

    public String getTemplateDir() {
        return templateDir;
    }

    private TemplateQueryListEntry[] getQueryTemplatesFromDir() {

        if (templateDir == null || templateDir.isEmpty()) {
            templateDir = selectQueryFolder();
        }
        if (templateDir == null || templateDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No template directory selected");
            return null;
        }
        Path templateDirectory = Paths.get(templateDir);
        provider.readDirectory(templateDirectory);

        TemplateQueryListEntry[] items = getAllTemplates();
        if (items == null || items.length == 0) {
            JOptionPane.showMessageDialog(this, "No queries found in the directory");
            return new TemplateQueryListEntry[]{};
        }
        if (templateDirectoryListener != null) {
            templateDirectoryListener.accept(templateDir);
        }
        return items;
    }

    private String selectQueryFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();

        } else {
            return null;
        }
    }

    private TemplateQueryListEntry[] getAllTemplates() {
        return provider.getCypherQueryTemplateMap().entrySet()
                .stream()
                .map(entry -> new TemplateQueryListEntry(String.valueOf(entry.getKey()), entry.getValue().getName()))
                .collect(Collectors.toList())
                .toArray(new TemplateQueryListEntry[0]);
    }

    private final class SelectQueryPanel extends JPanel {
        JList queryList;

        public SelectQueryPanel(TemplateQueryListEntry[] templateQueryListEntries) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JScrollPane queryListPane = new JScrollPane();
            queryList = new JList(templateQueryListEntries);
            queryList.setFixedCellWidth(200);
            queryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            queryListPane.setViewportView(queryList);
            JPanel queryListButtonPanel = new JPanel();

            JButton newQueryButton = new JButton("New");
            newQueryButton.addActionListener(e -> newQueryAction());

            JButton editQueryButton = new JButton("Edit");
            editQueryButton.addActionListener(e -> editQueryAction());

            JButton selectFolderButton = new JButton("Select Folder");
            selectFolderButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
            selectFolderButton.addActionListener(e -> selectFolderAction());

            add(queryListPane);
//            queryListButtonPanel.add(newQueryButton);
//            queryListButtonPanel.add(editQueryButton);
            queryListButtonPanel.add(selectFolderButton);
            add(queryListButtonPanel);
        }

        private void selectFolderAction() {
            templateDir = selectQueryFolder();
            TemplateQueryListEntry[] items = getQueryTemplatesFromDir();
            queryList.setListData(items);
        }

        private void editQueryAction() {
            int selected = queryList.getSelectedIndex();
            if (selected >= 0) {
                TemplateQueryListEntry templateQueryListEntry = (TemplateQueryListEntry) queryList.getSelectedValue();
                CypherQueryTemplate cypherQueryTemplate = provider.getCypherQueryTemplate(Long.valueOf(templateQueryListEntry.id)).orElse(null);
                if (cypherQueryTemplate != null && (cypherQueryTemplate.getMapping() instanceof CopyAllMappingStrategy)) {
                        CopyAllMappingStrategy copyAllMappingStrategy = (CopyAllMappingStrategy) cypherQueryTemplate.getMapping();
                        EditQueryTemplateDialog editQueryTemplateDialog = new EditQueryTemplateDialog(
                                cypherQueryTemplate.getName(),
                                cypherQueryTemplate.getCypherQuery(),
                                cypherQueryTemplate.getParameterTypes(),
                                copyAllMappingStrategy.getReferenceColumn(),
                                copyAllMappingStrategy.getNetworkName()
                        );
                        editQueryTemplateDialog.showDialog();
                        if (editQueryTemplateDialog.isOk()) {
                            updateCypherQueryTemplate(templateQueryListEntry, editQueryTemplateDialog);
                        }

                    } else {
                        JOptionPane.showMessageDialog(this, "The mapping strategy is not supported by the editor, use a text editor to edit this query");
                    }
                }
            }

        private void updateCypherQueryTemplate(TemplateQueryListEntry templateQueryListEntry, EditQueryTemplateDialog editQueryTemplateDialog) {
            try {
                provider.putCypherQueryTemplate(
                        Long.valueOf(templateQueryListEntry.id),
                        editQueryTemplateDialog.createCypherQuery()
                );
                reloadList();
            } catch (IOException | JAXBException ex) {
                JOptionPane.showMessageDialog(this, "Error saving query", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void newQueryAction() {
            EditQueryTemplateDialog editQueryTemplateDialog = new EditQueryTemplateDialog();
            editQueryTemplateDialog.showDialog();

            if (editQueryTemplateDialog.isOk()) {
                CypherQueryTemplate cypherQueryTemplate = editQueryTemplateDialog.createCypherQuery();

                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        provider.addCypherQueryTemplate(
                                file.toPath(), cypherQueryTemplate
                        );
                        reloadList();
                    } catch (IOException | JAXBException ex) {
                        JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        JList getQueryList() {
            return queryList;
        }

        void reloadList() {
            TemplateQueryListEntry[] list = SelectTemplateDialog.this.getQueryTemplatesFromDir();
            queryList.setListData(list);
        }

    }

    public static void main(String[] args) {
        SelectTemplateDialog dialog = new SelectTemplateDialog(null,
                new String[]{"v1", "v2"},
                null,
                System.out::println //NOSONAR, ignore 'Standard outputs should not be used directly to log anything'
        );
        dialog.showDialog();
    }
}
