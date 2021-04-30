import javafx.event.ActionEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class test {
    private static final boolean hasData = false;
    private static final JButton clients = new JButton("Show all clients");
    private static final JButton items = new JButton("Show all items");
    private static final JButton memos = new JButton("Show all memos");
    private static boolean isSelected = false;
    private static JFrame tableFrame = new JFrame();
    private static JFrame clientFrame = new JFrame();
    private static JFrame itemFrame = new JFrame();
    private static JFrame memoFrame = new JFrame();
    private static JCheckBox preference = new JCheckBox("One frame", false);
    private static final MyDatabase2 de = new MyDatabase2();
    private static ActionEvent e;

    public static void main(String[] args) {
        clients.setName("clients");
        items.setName("items");
        memos.setName("memos");
        String password = "asdisadojwiwjio";
        String loginName = "asdiwjdwijdwij";
        int start=JOptionPane.showConfirmDialog(null,"Begin login?","",JOptionPane.YES_NO_CANCEL_OPTION);
        if(start==0){
            while ( !loginName.toLowerCase().equals("maksumulh") || !password.toLowerCase().equals("kretts123")) {
                loginName = JOptionPane.showInputDialog("Please enter your name:");
                if (loginName != null) {
                    int option = JOptionPane.showConfirmDialog(null, "Is the name " + loginName + " the name you want to login with?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (!loginName.isEmpty() && option==0) {
                        System.out.println("User clicked on " + option);
                        if (!loginName.toLowerCase().equals("maksumulh")) {
                            JOptionPane.showMessageDialog(null, "Login name is not the administrator's name", "ERROR", JOptionPane.ERROR_MESSAGE);
                        } else {
                            while (!password.toLowerCase().equals("kretts123")) {
                                password = JOptionPane.showInputDialog("Please enter your password:");

                                if (password != null) {
                                    option = JOptionPane.showConfirmDialog(null, "Continue with password?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                                    if (!password.isEmpty() && option==0) {
                                        if (password.toLowerCase().equals("kretts123")) {
                                            JOptionPane.showMessageDialog(null, "Welcome Maksumul", "Welcome!", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    }
                                    else if(option==2){
                                        option = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel?", "Confirm", JOptionPane.YES_NO_OPTION);
                                        if(option==0){
                                            break;
                                        }
                                    }
                                }

                            }


                        }
                    }
                    if(option==2){
                        option = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if(option==0){
                            break;
                        }
                    }
                }


            }
        }





        //DARK_GRAY is a great color as a background
        if(loginName.toLowerCase().equals("maksumulh") && password.toLowerCase().equals("kretts123")){
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JPanel panel2 = new JPanel();
        frame.setVisible(true);
        panel.setVisible(true);
        panel2.setVisible(true);
        panel2.setBackground(Color.BLACK);
        panel2.setOpaque(true);
        panel.setOpaque(true);
        frame.add(panel);
        frame.add(panel2);
        frame.setTitle("Dewan Enterprise Database");
        frame.setBounds(0, 0, 487, 100);
        //panel.setBounds(450, 250, 50, 50);
        panel2.setLayout(new GridBagLayout());
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));
        frame.setBackground(Color.DARK_GRAY);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton newUser = new JButton("Add a new user");
        JButton newItem = new JButton("Add a new item");
        JButton newMemo = new JButton("Add a new memo");
        preference.setToolTipText("Only one frame of either clients, items or memos can be viewed at once");
        checkBox(preference);
        panel2.add(preference);
        panel2.add(newUser);
        panel2.add(newItem);
        panel2.add(newMemo);
        panel.add(clients);
        panel.add(items);
        panel.add(memos);
        addActionListener(clients);
        addActionListener(items);
        addActionListener(memos);
        addActionListener(newUser);
        addActionListener(newItem);
        addActionListener(newMemo);


         }


    }

    public static void checkBox(JCheckBox b) {
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int option;
                if (e.getSource() == b) {
                    if (b.isSelected()) {
                        option = JOptionPane.showConfirmDialog(null, "Are you sure you want to switch to three frames? If you click yes, any tables you have open will be discarded", "Continue?", JOptionPane.YES_NO_CANCEL_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            tableFrame.dispose();
                            b.setText("Three frames");
                            b.setToolTipText("A frame of clients, items and memos can be viewed together at once");
                            isSelected = true;

                        } else {
                            isSelected = false;
                            b.setSelected(false);
                        }
                    } else {
                        option = JOptionPane.showConfirmDialog(null, "Are you sure you want to switch to one frame? If you click yes, any tables you have open will be discarded", "Continue?", JOptionPane.YES_NO_CANCEL_OPTION);
                        if (option == 0) {
                            clientFrame.dispose();
                            itemFrame.dispose();
                            memoFrame.dispose();
                            b.setText("One frame");
                            b.setToolTipText("Only one frame of either clients, items or memos can be viewed at once");
                            isSelected = false;
                            b.setSelected(false);
                        } else {
                            isSelected = true;
                            b.setSelected(true);
                        }

                    }
                }
            }
        });
    }


    public static void addActionListener(JButton b) {

        b.addActionListener(new ActionListener() {


            @Override

            public void actionPerformed(java.awt.event.ActionEvent e) {

                if (e.getSource() == b && !b.getText().contains("Add") && !isSelected) {
                    de.makeTable(b.getName(), tableFrame);

                }
                if (e.getSource() == b && isSelected && !b.getText().contains("Add")) {

                    if (b.getText().contains("clients")) {
                        de.makeTable(b.getText(), clientFrame);
                    }
                    if (b.getText().contains("items")) {
                        de.makeTable(b.getText(), itemFrame);
                    }
                    if (b.getText().contains("memos")) {
                        de.makeTable(b.getText(), memoFrame);
                    }
                }
                if (e.getSource() == b && b.getText().toLowerCase().contains("add")) {
                    if (b.getText().toLowerCase().contains("user")) {
                        String input = JOptionPane.showInputDialog("Add the clientID and the name of the customer and be sure to put a comma ' , ' ");
                        int option = JOptionPane.showConfirmDialog(null, "Continue?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                        if (input != null && option == 0) {
                            if (!input.isEmpty()) {
                                if (input.contains(" ") && !input.contains(",")) {
                                    String[] dataParts = input.split(" ");
                                    if (dataParts.length >= 4) {


                                    } else {
                                        JOptionPane.showMessageDialog(null, "A new user with no transactions will be created. Continue?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                                    }
                                    System.out.println("ID is " + dataParts[0] + " and name is " + dataParts[1]);
                                }
                                if (input.contains(",")) {
                                    String[] dataParts = input.split(",");
                                    if (dataParts.length >= 4) {
                                        JOptionPane.showMessageDialog(null, "A user with existing transactions will be added. Continue?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);

                                    } else {
                                        JOptionPane.showMessageDialog(null, "A new user with no transactions will be created. Continue?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                                    }
                                    System.out.println("Given ID is " + dataParts[0] + " and given name is " + dataParts[1]);

                                }
                            }
                        } else if (option == 2) {
                            JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel?", "", JOptionPane.YES_NO_CANCEL_OPTION);

                        }

                    }
                }


            }


        });
    }

}

