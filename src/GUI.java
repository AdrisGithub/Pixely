import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUI extends JDialog {
    private JPanel mainPane;
    private JButton searchButton;
    private JComboBox<Integer> comboBox;
    private JTextField textField;
    private JPanel drawPane;
    private BufferedImage img;
    private JButton[][] matrix;

    public GUI() {

        setContentPane(mainPane);
        setModal(true);
        getRootPane().setDefaultButton(searchButton);

        searchButton.addActionListener(e -> onOK());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        mainPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        try{
            img = ImageIO.read(new File(textField.getText()));
        }catch(IOException io){
            textField.setText("Couldn't find the Image");
        }
        initMatrix();
    }

    private void initMatrix() {
        matrix = new JButton[img.getWidth()][img.getHeight()];
        for (int i=0;i<matrix.length;i++){
            for (int j=0;j<matrix[0].length;j++){
                matrix[i][j] = new JButton();
                int finalI = i;
                int finalJ = j;
                matrix[i][j].addActionListener(e -> matrix[finalI][finalJ].setBackground(new Color(img.getRGB(finalI, finalJ))));
            }
        }
        fillDrawPane();
    }

    private void fillDrawPane() {
        drawPane.setLayout(new GridLayout(img.getWidth(),img.getHeight()));
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                drawPane.add(matrix[j][i]);
            }
        }
        drawPane.revalidate();
        setSize(600,600);
    }
    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        GUI dialog = new GUI();
        dialog.setSize(600,600);
        dialog.setVisible(true);
        dialog.setResizable(false);
    }

    private void createUIComponents() {
        drawPane = new JPanel();
        drawPane.setBackground(Color.BLACK);
    }
}
