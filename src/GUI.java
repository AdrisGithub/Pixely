import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GUI extends JDialog {
    private JPanel mainPane;
    private JButton searchButton;
    private JComboBox<Integer> comboBox;
    private JTextField textField;
    private JPanel drawPane;
    private BufferedImage img;
    private JButton[][] matrix;
    private final List<Color> colors;

    public static boolean mousePressed = false;

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
        colors = new ArrayList<>();
        mainPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        drawPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                System.out.println("Mouse gedrückt");
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                System.out.println("Mouse nicht gedrückt");
            }
        });
    }

    private void onOK() {
        try{
            img = ImageIO.read(new File(textField.getText()));
            initMatrix();
        }catch(IOException io){
            textField.setText("Couldn't find the Image");
        }
    }

    private void initMatrix() {
        matrix = new JButton[img.getWidth()][img.getHeight()];
        for (int i=0;i<matrix.length;i++){
            for (int j=0;j<matrix[0].length;j++){
                int finalI = i,finalJ = j;
                matrix[i][j] = new JButton(addColor(i,j));
                matrix[i][j].setBackground(Color.gray);
                matrix[i][j].addActionListener(e -> changeBackground(finalI,finalJ,matrix[finalI][finalJ]));
                matrix[i][j].addMouseMotionListener(new MouseMotionListener() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        changeBackground(finalI,finalJ,matrix[finalI][finalJ]);
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if(mousePressed) changeBackground(finalI,finalJ,matrix[finalI][finalJ]);
                    }
                });
            }
        }
        fillComboBox();
        fillDrawPane();
    }

    private void fillComboBox() {
        for (int i=0;i<colors.size()+1;i++){
            comboBox.addItem(i+1);
        }
    }

    private String addColor(int i, int j) {
        Color temp = new Color(img.getRGB(i,j));
        if(colors.contains(temp)){
            return colors.indexOf(temp)+1+"";
        }
        colors.add(temp);
        return colors.size()+1+"";
    }


    private void changeBackground(int xPos,int yPos,JButton button){
        if(button.getText().equals("")||comboBox.getSelectedItem()==null)return;
        String colorName = button.getText();
        if(Integer.parseInt(comboBox.getSelectedItem().toString()) == Integer.parseInt(colorName)){
            button.setBackground(new Color(img.getRGB(xPos,yPos)));
            button.setText("");
            if(lastColor(colorName)) comboBox.removeItem(Integer.parseInt(colorName));
        }else{
            button.setBackground(Color.DARK_GRAY);
        }
        drawPane.revalidate();
        button.revalidate();
    }

    private boolean lastColor(String title) {
        return Arrays.stream(matrix).noneMatch(jButtons -> Arrays.stream(jButtons).anyMatch(jButton -> jButton.getText().equals(title)));
    }

    private void fillDrawPane() {
        drawPane.setLayout(new GridLayout(img.getWidth(),img.getHeight()));
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                drawPane.add(matrix[j][i]);
            }
        }
        drawPane.revalidate();
        setSize(img.getWidth()*56+20,img.getHeight()*32+50);
    }
    private void onCancel() {
        System.out.println(getHeight()+" "+getWidth());
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
