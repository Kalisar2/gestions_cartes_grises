package views;

import controllers.PossederController;
import models.Posseder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PossederView extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private PossederController possederController = new PossederController();
    private JButton addButton, updateButton, deleteButton, backButton;

    public PossederView() {
        setTitle("Affichage des données POSSEDER");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Nom Propriétaire", "Modèle Véhicule", "Date Début", "Date Fin"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable();

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Ajouter");
        updateButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        backButton = new JButton("Retour en arrière");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField proprietaireField = new JTextField();
                JTextField modeleField = new JTextField();
                JTextField dateDebutField = new JTextField();
                JTextField dateFinField = new JTextField();

                JPanel panel = new JPanel(new GridLayout(4, 2));
                panel.add(new JLabel("Nom Propriétaire:"));
                panel.add(proprietaireField);
                panel.add(new JLabel("Modèle Véhicule:"));
                panel.add(modeleField);
                panel.add(new JLabel("Date Début (yyyy-MM-dd):"));
                panel.add(dateDebutField);
                panel.add(new JLabel("Date Fin (yyyy-MM-dd, optionnel):"));
                panel.add(dateFinField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Ajouter une propriété", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String nomProprietaire = proprietaireField.getText();
                        String nomModele = modeleField.getText();
                        Date dateDebut = new SimpleDateFormat("yyyy-MM-dd").parse(dateDebutField.getText());
                        Date dateFin = dateFinField.getText().isEmpty() ? null : new SimpleDateFormat("yyyy-MM-dd").parse(dateFinField.getText());

                        int idProprietaire = possederController.getIdProprietaire(nomProprietaire);
                        int idVehicule = possederController.getIdVehiculeParModele(nomModele);

                        possederController.addPosseder(new Posseder(idProprietaire, idVehicule, dateDebut, dateFin));
                        refreshTable();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout.");
                    }
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    try {
                        // Identifiants d'origine
                        String originalNomProprietaire = table.getValueAt(selectedRow, 0).toString();
                        String originalNomModele = table.getValueAt(selectedRow, 1).toString();
                        int originalIdProprietaire = possederController.getIdProprietaire(originalNomProprietaire);
                        int originalIdVehicule = possederController.getIdVehiculeParModele(originalNomModele);

                        // Saisie des nouvelles valeurs
                        String nomProprietaire = JOptionPane.showInputDialog("Nouveau nom propriétaire:", originalNomProprietaire);
                        String nomModele = JOptionPane.showInputDialog("Nouveau modèle véhicule:", originalNomModele);
                        String dateDebutStr = JOptionPane.showInputDialog("Nouvelle date début (yyyy-MM-dd):", table.getValueAt(selectedRow, 2).toString());
                        String dateFinStr = JOptionPane.showInputDialog("Nouvelle date fin (yyyy-MM-dd):", table.getValueAt(selectedRow, 3).toString());

                        Date dateDebut = new SimpleDateFormat("yyyy-MM-dd").parse(dateDebutStr);
                        Date dateFin = dateFinStr.equals("N/A") ? null : new SimpleDateFormat("yyyy-MM-dd").parse(dateFinStr);

                        int idProprietaire = possederController.getIdProprietaire(nomProprietaire);
                        int idVehicule = possederController.getIdVehiculeParModele(nomModele);

                        // Mise à jour avec les identifiants d'origine
                        possederController.deletePosseder(originalIdProprietaire, originalIdVehicule);
                        possederController.addPosseder(new Posseder(idProprietaire, idVehicule, dateDebut, dateFin));
                        refreshTable();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erreur lors de la modification.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Veuillez sélectionner une ligne à modifier.");
                }
            }
        });

        backButton.addActionListener(e -> dispose());

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String nomProprietaire = table.getValueAt(selectedRow, 0).toString();
                    String nomModele = table.getValueAt(selectedRow, 1).toString();

                    int idProprietaire = possederController.getIdProprietaire(nomProprietaire);
                    int idVehicule = possederController.getIdVehiculeParModele(nomModele);

                    possederController.deletePosseder(idProprietaire, idVehicule);
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Veuillez sélectionner une ligne à supprimer.");
                }
            }
        });

        setVisible(true);
    }

    private void refreshTable() {
        List<Posseder> possederList = possederController.getAllPosseder();
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Posseder p : possederList) {
            tableModel.addRow(new Object[]{
                possederController.getNomProprietaire(p.getIdProprietaire()),
                possederController.getNomModele(p.getIdVehicule()),
                sdf.format(p.getDateDebutPropriete()),
                p.getDateFinPropriete() != null ? sdf.format(p.getDateFinPropriete()) : "N/A"
            });
        }
    }

    public static void main(String[] args) {
        new PossederView();
    }
}
