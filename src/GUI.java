import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GUI extends JFrame {
    private JPanel mainPane;
    private JButton searchButton;
    private JComboBox<Integer> comboBox;
    private JTextField textField;
    private JPanel drawPane;
    private JPanel scrollPane;
    private JPanel verticalPane;
    private JScrollBar verticalScroll;
    private JScrollBar horizontalScroll;
    private BufferedImage img;
    private JButton[][] matrix;
    private final List<Color> colors;
    private boolean pickedImage;

    public GUI() throws IOException {
        setTitle("PixelY");
        setIconImage(ImageIO.read(new File("resources/icon.png")));
        setContentPane(mainPane);
        //setModal(true);
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
        comboBox.addActionListener(e -> highlightSameColor());
    }

    private void highlightSameColor() {
        if(comboBox.getSelectedItem()==null)return;
        String currentColor = comboBox.getSelectedItem().toString();
        for (JButton[] buttons:matrix) {
            for (JButton b:buttons) {
                if(b.getText().equals(""))return;
                if(b.getBackground()==Color.LIGHT_GRAY)b.setBackground(Color.gray);
                if(b.getText().equals(currentColor))b.setBackground(Color.LIGHT_GRAY);
            }
        }
    }

    private void onOK() {
        try{
            if(pickedImage)return;
            img = ImageIO.read(new File(textField.getText()));
            pickedImage = true;
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
            }
        }
        fillComboBox();
        fillDrawPane();
        highlightSameColor();
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
        button.setSelected(false);
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
    }
    private void onCancel() {
        System.out.println(getHeight()+" "+getWidth());
        dispose();
    }

    public static void main(String[] args) throws IOException {
        GUI dialog = new GUI();
        dialog.setSize(650,650);
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        drawPane = new JPanel();
        drawPane.setBackground(Color.BLACK);
    }
}
