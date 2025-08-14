package vallegrande.edu.pe;

import vallegrande.edu.pe.controller.ContactController;
import vallegrande.edu.pe.model.InMemoryContactRepository;
import vallegrande.edu.pe.view.ContactView;

import javax.swing.SwingUtilities;

/**
 * Clase principal que inicia la aplicación.
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var repo = new InMemoryContactRepository();
            var controller = new ContactController(repo);
            var view = new ContactView(controller);
            view.setVisible(true);
        });
    }
}