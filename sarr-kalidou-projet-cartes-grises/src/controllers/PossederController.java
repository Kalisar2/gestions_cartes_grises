package controllers;

import models.Posseder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;

// Cette classe gère les opérations liées à la table "POSSEDER"
public class PossederController {

    // Cette méthode récupère tous les enregistrements de la table "POSSEDER"
    public List<Posseder> getAllPosseder() {
        List<Posseder> possederList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();  // Se connecter à la base de données
             Statement stmt = conn.createStatement()) {
            
            // Requête SQL pour récupérer toutes les données de "POSSEDER"
            String query = "SELECT * FROM POSSEDER";
            ResultSet rs = stmt.executeQuery(query);

            // Boucle pour parcourir tous les résultats et les ajouter à la liste
            while (rs.next()) {
                Posseder posseder = new Posseder(
                    rs.getInt("id_proprietaire"),
                    rs.getInt("id_vehicule"),
                    rs.getDate("date_debut_propriete"),
                    rs.getDate("date_fin_propriete")
                );
                possederList.add(posseder);  // Ajouter l'objet à la liste
            }
        } catch (Exception e) {
            e.printStackTrace();  // Gérer les exceptions en affichant l'erreur
        }
        return possederList;  // Retourner la liste des propriétaires et véhicules
    }

    // Cette méthode ajoute un nouvel enregistrement dans la table "POSSEDER"
    public void addPosseder(Posseder posseder) {
        try (Connection conn = DatabaseConnection.getConnection()) {  // Se connecter à la base de données
            // Requête SQL pour insérer un nouvel enregistrement
            String query = "INSERT INTO POSSEDER (id_proprietaire, id_vehicule, date_debut_propriete, date_fin_propriete) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            // Remplir les valeurs dans la requête
            pstmt.setInt(1, posseder.getIdProprietaire());
            pstmt.setInt(2, posseder.getIdVehicule());
            pstmt.setDate(3, new java.sql.Date(posseder.getDateDebutPropriete().getTime()));
            pstmt.setDate(4, posseder.getDateFinPropriete() != null ? new java.sql.Date(posseder.getDateFinPropriete().getTime()) : null);
            
            // Exécuter la requête
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();  // Gérer les exceptions en affichant l'erreur
        }
    }

    // Cette méthode met à jour un enregistrement existant dans la table "POSSEDER"
    public void updatePosseder(Posseder posseder) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Vérifier si l'enregistrement existe déjà
            String checkQuery = "SELECT COUNT(*) FROM POSSEDER WHERE id_proprietaire = ? AND id_vehicule = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, posseder.getIdProprietaire());
            checkStmt.setInt(2, posseder.getIdVehicule());
            ResultSet rs = checkStmt.executeQuery();
    
            if (rs.next() && rs.getInt(1) > 0) {  // Si l'enregistrement existe
                // Requête SQL pour mettre à jour l'enregistrement
                String query = "UPDATE POSSEDER SET date_debut_propriete = ?, date_fin_propriete = ? WHERE id_proprietaire = ? AND id_vehicule = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setDate(1, new java.sql.Date(posseder.getDateDebutPropriete().getTime()));
                pstmt.setDate(2, posseder.getDateFinPropriete() != null ? new java.sql.Date(posseder.getDateFinPropriete().getTime()) : null);
                pstmt.setInt(3, posseder.getIdProprietaire());
                pstmt.setInt(4, posseder.getIdVehicule());
                
                // Exécuter la mise à jour
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated == 0) {
                    System.out.println("⚠ Aucune mise à jour effectuée !");
                } else {
                    System.out.println("Mise à jour réussie pour id_proprietaire: " + posseder.getIdProprietaire() + " et id_vehicule: " + posseder.getIdVehicule());
                }
            } else {
                System.out.println("Erreur : L'entrée à mettre à jour n'existe pas !");
            }
        } catch (Exception e) {
            e.printStackTrace();  // Gérer les exceptions en affichant l'erreur
        }
    }
    
    // Cette méthode supprime un enregistrement de la table "POSSEDER"
    public void deletePosseder(int idProprietaire, int idVehicule) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Requête SQL pour supprimer l'enregistrement
            String query = "DELETE FROM POSSEDER WHERE id_proprietaire = ? AND id_vehicule = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idProprietaire);
            pstmt.setInt(2, idVehicule);
            pstmt.executeUpdate();  // Exécuter la suppression
        } catch (Exception e) {
            e.printStackTrace();  // Gérer les exceptions en affichant l'erreur
        }
    }

    // Cette méthode récupère l'ID d'un propriétaire à partir de son nom
    public int getIdProprietaire(String nomProprietaire) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Requête SQL pour récupérer l'ID d'un propriétaire en fonction de son nom
            String query = "SELECT id_proprietaire FROM PROPRIETAIRE WHERE nom = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nomProprietaire);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_proprietaire");  // Retourner l'ID du propriétaire
            }
        } catch (Exception e) {
            e.printStackTrace();  // Gérer les exceptions en affichant l'erreur
        }
        return -1;  // Retourner -1 si aucun propriétaire n'a été trouvé
    }

    // Cette méthode récupère l'ID d'un véhicule à partir du modèle du véhicule
    public int getIdVehiculeParModele(String nomModele) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Requête SQL pour récupérer l'ID du véhicule en fonction du modèle
            String query = "SELECT v.id_vehicule FROM VEHICULE v JOIN MODELE m ON v.id_modele = m.id_modele WHERE m.nom_modele = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nomModele);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_vehicule");  // Retourner l'ID du véhicule
            }
        } catch (Exception e) {
            e.printStackTrace();  // Gérer les exceptions en affichant l'erreur
        }
        return -1;  // Retourner -1 si aucun véhicule n'a été trouvé
    }

    // Cette méthode récupère le nom d'un propriétaire à partir de son ID
    public String getNomProprietaire(int idProprietaire) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Requête SQL pour récupérer le nom du propriétaire en fonction de son ID
            String query = "SELECT nom FROM PROPRIETAIRE WHERE id_proprietaire = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idProprietaire);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nom");  // Retourner le nom du propriétaire
            }
        } catch (Exception e) {
            e.printStackTrace();  // Gérer les exceptions en affichant l'erreur
        }
        return "Inconnu";  // Retourner "Inconnu" si le propriétaire n'est pas trouvé
    }

    // Cette méthode récupère le nom du modèle de véhicule à partir de l'ID du véhicule
    public String getNomModele(int idVehicule) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Requête SQL pour récupérer le nom du modèle du véhicule en fonction de son ID
            String query = "SELECT m.nom_modele FROM MODELE m JOIN VEHICULE v ON m.id_modele = v.id_modele WHERE v.id_vehicule = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idVehicule);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nom_modele");  // Retourner le nom du modèle
            }
        } catch (Exception e) {
            e.printStackTrace();  // Gérer les exceptions en affichant l'erreur
        }
        return "Inconnu";  // Retourner "Inconnu" si le modèle n'est pas trouvé
    }
}
