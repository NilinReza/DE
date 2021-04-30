import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class DE {

    public static void main(String[] args) throws Exception {
        JFrame tableFrame = new JFrame();
        MyDatabase2 db = new MyDatabase2();
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a command:");
        String s = input.nextLine();
        while (!s.toLowerCase().contains("exit") || !s.contains("^D")) {
            if (s.toLowerCase().contains("add") || s.toLowerCase().contains("create") || s.toLowerCase().contains("make") || s.toLowerCase().contains("new")) {
                System.out.println("Type only the ID of the new user that you wish to create");
                s = input.nextLine();
                db.addUser(s);

            }
            if (s.toLowerCase().contains("display") || s.toLowerCase().contains("show") || s.toLowerCase().contains("all")) {
                db.displayAll(s);
            }
            if (s.toLowerCase().contains("manual") || s.toLowerCase().contains("sql input")) {
                db.manualSQL();
            }
            if (s.toLowerCase().contains("due")) {
                db.due();
            }
            if (s.toLowerCase().contains("table")) {
                db.makeTable(s, tableFrame);
            }

            System.out.println("Enter the next command:");
            s = input.nextLine();
        }
        //db.terminate();


    }


}

class MyDatabase2 {
    private Connection connection;
    private int numberOfClients;
    private int numberOfItems;
    private int numberOfMemos;
    private String clients = "create table clients ( clientID integer ,name VARCHAR(100),totalAmount integer,totalPaid integer,primary key(clientID))";

    private String items = "create table items ( " +
            " itemID integer," +               //String itemID,String name,String price, String rackID, String quantity
            " itemName VARCHAR(100)," +            //TODO ID, name, price, rackID, quantity
            " price integer," +
            " rackID integer," +
            " quantity integer," +
            " primary key(itemID)" +
            ")";

    private String memos = "create table memos ( " +
            " memoID integer," +
            " clientID integer," +
            " itemID integer," +        //String memoID,String clientID,String itemID, String date
            " dateProcessed VARCHAR(100)," +     //TODO amountPaid,quantity+-,item+-, print+-
            " amountPaid integer," +
            " quantity integer," +   //remember to subtract the amount from item
            " primary key(memoID,clientID,itemID)," +
            " foreign key(clientID) references clients(clientID)," +
            "foreign key (itemID) references items(itemID)" +
            ")";
    private File clientsTXT = new File("clients.txt");
    private File itemsTXT = new File("items.txt");
    private File memosTXT = new File("memos.txt");
    private File updatedClientsTXT = new File("updatedClients.txt");
    private File updatedItemsTXT = new File("updatedItemsTXT.txt");
    private File updatedMemosTXT = new File("updatedMemosTXT.txt");
    private String firstLineClients = "";
    private String firstLineItems = "";
    private String firstLineMemo = "";
    private boolean isClientUpdated=false;
    private boolean isItemUpdated=false;
    private boolean isMemoUpdated=false;

    public MyDatabase2() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            // creates an in-memory database
            connection = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "Nilin", "kretts123");

            createTables();
            readInData();
        } catch (ClassNotFoundException e) {
            e.printStackTrace(System.out);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }


    private void createTables() {


        try {
            connection.getClientInfo();
            connection.createStatement().executeUpdate(clients);
            connection.createStatement().executeUpdate(items);
            connection.createStatement().executeUpdate(memos);


        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

    }

    public int getAccountForElfName(String elfName) {
        /*
         * To be CORRECTED and completed. Just an example of how this can work. You will have to add more tables to the FROM statement
         */
        int aID = -1;
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "Select accountID from views where viewerName=?;"
            );
            pstmt.setString(1, elfName);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                // at least 1 row (hopefully one row!) exists. Get the ID
                aID = resultSet.getInt("accountID");
                //System.out.println(elfName + " is associated with account " + aID);
            }
            resultSet.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return aID;
    }

    public int getViews(String ID) {
        /*
         * To be CORRECTED and completed. Just an example of how this can work. You will have to add more tables to the FROM statement
         */
        int aID = -1;
        System.out.println("Q3 - views for video with ID " + ID);
        try {
            PreparedStatement pstmt_ = connection.prepareStatement(
                    "Select count(ID) as counter from views where ID=?;"
            );
            PreparedStatement pstmt = connection.prepareStatement(
                    "Select videoName,ID from videos where ID=?;"
            );
            pstmt_.setString(1, ID);
            pstmt.setString(1, ID);
            ResultSet resultSet_ = pstmt_.executeQuery();
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                // at least 1 row (hopefully one row!) exists. Get the ID
                String name = resultSet.getString("videoName");
                int views = 0;
                while (resultSet_.next()) {
                    views = resultSet_.getInt("counter");
                }
                System.out.println(name + "/" + ID + " has " + views + " views");
            }
            resultSet.close();
            resultSet_.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return aID;
    }

    public void getBills(String elfName) {
        /*
         * To be CORRECTED and completed. Just an example of how this can work. You will have to add more tables to the FROM statement
         */
        System.out.println("Q2 - Bills for  " + elfName);
        try {
            //System.out.println("Before connection.prepareStatement");
            PreparedStatement pstmt = connection.prepareStatement(
                    "Select amount,billID from bills where accountID=?;"
            );
            pstmt.setInt(1, getAccountForElfName(elfName));
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                // at least 1 row (hopefully one row!) exists. Get the ID
                int amount = resultSet.getInt("amount");
                int billID = resultSet.getInt("billID");
                System.out.println(elfName + " has bill " + billID + " which is for " + amount + "c");
            }
            resultSet.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public File getClientsTXT() {
        return clientsTXT;
    }

    public File getItemsTXT() {
        return itemsTXT;
    }

    public File getMemosTXT() {
        return memosTXT;
    }

    public String getClients() {
        return clients;
    }

    public String getItems() {
        return items;
    }

    public String getMemos() {
        return memos;
    }

    public void setNumberOfClients(int numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public void setNumberOfMemos(int numberOfMemos) {
        this.numberOfMemos = numberOfMemos;
    }

    public int getNumberOfClients() {
        return numberOfClients;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public int getNumberOfMemos() {
        return numberOfMemos;
    }

    public String getFirstLineClients() {
        return firstLineClients;
    }

    public String getFirstLineItems() {
        return firstLineItems;
    }

    public String getFirstLineMemo() {
        return firstLineMemo;
    }

    private void writeInData(){
        BufferedWriter out;
        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
            if (isClientUpdated) {
                out = new BufferedWriter(new FileWriter(clientsTXT));
                out.append(firstLineClients).append("\n");
                resultSet = statement.executeQuery("SELECT * FROM clients");
                while (resultSet.next()) {
                    //System.out.println(resultSet.getString("clientID")+" is the ID of the client, the name of the client is "+resultSet.getString("name")+" and totalAmount is:"+resultSet.getInt("totalAmount")+" along with the total amount paid:"+resultSet.getInt("totalPaid"));
                    out.append(String.valueOf(resultSet.getInt("clientID"))).append(",").append(resultSet.getString("name")).append(",").append(String.valueOf(resultSet.getInt("totalAmount"))).append(",").append(String.valueOf(resultSet.getInt("totalPaid"))).append("\n");

                }
                resultSet.close();
                out.close();
            } else if (isItemUpdated) {
                out = new BufferedWriter(new FileWriter(itemsTXT));
                out.append(firstLineItems).append("\n");
                resultSet = statement.executeQuery("SELECT * FROM items");
                while (resultSet.next()) {
                    out.append(resultSet.getString("itemID")).append(",").append(resultSet.getString("itemName")).append(",").append(resultSet.getString("price")).append(",").append(resultSet.getString("rackID")).append(",").append(resultSet.getString("quantity"));

                }
                resultSet.close();
                out.close();
            } else if (isMemoUpdated) {
                out = new BufferedWriter(new FileWriter(memosTXT));
                out.append(firstLineMemo).append("\n");
                resultSet = statement.executeQuery("SELECT * FROM memos");
                while (resultSet.next()) {
                    out.append(resultSet.getString("memoID")).append(",").append(resultSet.getString("clientID")).append(",").append(resultSet.getString("itemID")).append(",").append(resultSet.getString("dateProcessed")).append(",").append(resultSet.getString("amountPaid")).append(resultSet.getString("quantity"));

                }
                resultSet.close();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void readInData() {

        BufferedReader in = null;
        try {
            in = new BufferedReader((new FileReader(clientsTXT)));
            firstLineClients = in.readLine();
            String line = in.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3)
                    makeClient(parts[0], parts[1], parts[2], parts[3]);
                if (parts.length >= 1)
                    makeClient(parts[0], parts[1], "0", "0");


                line = in.readLine();
                setNumberOfClients(getNumberOfClients() + 1);

            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in = new BufferedReader((new FileReader(itemsTXT)));

            firstLineItems = in.readLine();

            String line = in.readLine();
            while (line != null) {

                String[] parts = line.split(",");
                if (parts.length >= 4)
                    makeItems(parts[0], parts[1], parts[2], parts[3], parts[4]);

                line = in.readLine();
                setNumberOfItems(getNumberOfItems() + 1);
            }


            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in = new BufferedReader((new FileReader(memosTXT)));

            firstLineMemo = in.readLine();

            String line = in.readLine();
            while (line != null) {

                String[] parts = line.split(",");
                if (parts.length >= 5)
                    makeMemos(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                calculate(parts[1]);

                // get next line
                line = in.readLine();
                setNumberOfMemos(getNumberOfMemos() + 1);
            }
            //makeTable("memos",new JFrame());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void makeClient(String clientID, String name, String totalAmount, String totalPaid) {

        int aID = -1;
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "Select clientID From clients where clientID = ?;"
            );
            pstmt.setInt(1, Integer.parseInt(clientID));

            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                // at least 1 row (hopefully one row!) exists. Get the ID
                aID = resultSet.getInt("clientID");
            } else {
                // no record
                // make the new account
                PreparedStatement addClient = connection.prepareStatement(
                        "insert into clients (clientID, name, totalAmount, totalPaid) values (?, ?, ?, ?);"
                );

                addClient.setInt(1, Integer.parseInt(clientID));
                addClient.setString(2, name);
                addClient.setInt(3, Integer.parseInt(totalAmount));
                addClient.setInt(4, Integer.parseInt(totalPaid));

                int numUpdated = addClient.executeUpdate();

                addClient.close();

                resultSet.close();
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error in " + clientID + " " + name);
            e.printStackTrace(System.out);
        }

    }

    private void makeMemos(String memoID, String clientID, String itemID, String dateProcessed, String amountPaid, String quantity) {
        /*
         * Really make or create account. Return the account ID
         * whether it is new, or give the existing one if it already exists
         */
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "Select memoID, clientID, itemID From memos where memoID=? and clientID=? and itemID=?;"
            );
            pstmt.setInt(1, Integer.parseInt(memoID));
            pstmt.setInt(2, Integer.parseInt(clientID));
            pstmt.setInt(3, Integer.parseInt(itemID));

            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                System.out.println("The memo has already been added");
            } else {
                // no record
                // make the new account
                PreparedStatement addMemos = connection.prepareStatement(
                        "insert into memos (memoID, clientID, itemID, dateProcessed, amountPaid, quantity) values (?, ?, ?, ?, ?, ?);"
                );

                addMemos.setInt(1, Integer.parseInt(memoID));
                addMemos.setInt(2, Integer.parseInt(clientID));
                addMemos.setInt(3, Integer.parseInt(itemID));
                addMemos.setString(4, dateProcessed);
                addMemos.setInt(5, Integer.parseInt(amountPaid));
                addMemos.setInt(6, Integer.parseInt(quantity));
                addMemos.executeUpdate();

                addMemos.close();

                resultSet.close();
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error in " + memoID + " " + clientID + " " + itemID + " " + dateProcessed);
            e.printStackTrace(System.out);
        }


    }


    private void makeItems(String itemID, String itemName, String price, String rackID, String quantity) {
        /*
         * Really make or create account. Return the account ID
         * whether it is new, or give the existing one if it already exists
         */
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "Select itemID, itemName , rackID From items where itemID = ? and itemName=? and rackID=?;"
            );
            pstmt.setInt(1, Integer.parseInt(itemID));
            pstmt.setString(2, itemName);
            pstmt.setInt(3, Integer.parseInt(rackID));

            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                System.out.println("The item with itemID " + resultSet.getString("itemID") + " has already been added");
            } else {
                // no record
                // make the new account
                PreparedStatement addItems = connection.prepareStatement(
                        "insert into items (itemID, itemName, price, rackID, quantity) values (?, ?, ?, ?, ?);"
                );

                addItems.setInt(1, Integer.parseInt(itemID));
                addItems.setString(2, itemName);
                addItems.setInt(3, Integer.parseInt(price));
                addItems.setInt(4, Integer.parseInt(rackID));
                addItems.setInt(5, Integer.parseInt(quantity));
                int numUpdated = addItems.executeUpdate();

                addItems.close();

                resultSet.close();
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error in " + itemID + " " + itemName + " " + price + " " + rackID + " " + quantity);
            System.out.println("Cannot add an item with the same itemID");
        }


    }


    public void displayAll(String s) {
        ResultSet resultSet = null;
        try {
            Statement state = connection.createStatement();
            if (s.toLowerCase().contains("clients") || s.toLowerCase().contains("users") || s.toLowerCase().contains("customers")) {

                resultSet = state.executeQuery("SELECT * FROM clients");
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("clientID") + " " + resultSet.getString("name") + " " + resultSet.getString("totalAmount") + " " + resultSet.getString("totalPaid"));

                }
                resultSet.close();
            } else if (s.toLowerCase().contains("items")) {
                resultSet = state.executeQuery("SELECT * FROM items");
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("itemID") + " " + resultSet.getString("itemName") + " " + resultSet.getString("price") + " " + resultSet.getString("rackID") + " " + resultSet.getString("quantity"));

                }
                resultSet.close();

            }
            //String memoID,String clientID,String itemID, String date
            else if (s.toLowerCase().contains("memo")) {
                resultSet = state.executeQuery("SELECT * FROM memos");
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("memoID") + " " + resultSet.getString("clientID") + " " + resultSet.getString("itemID") + " " + resultSet.getString("dateProcessed") + " " + resultSet.getString("quantity") + " " + resultSet.getString("amountPaid"));

                }
                resultSet.close();
            } else {
                System.out.println("The given command " + s + " is not sufficient");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void save(String s) {
        BufferedWriter out = null;
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            if (s.toLowerCase().contains("clients")) {
                out = new BufferedWriter(new FileWriter(updatedClientsTXT));
                out.append(firstLineClients).append("\n");
                resultSet = statement.executeQuery("SELECT * FROM clients");
                while (resultSet.next()) {
                    out.append(resultSet.getString("clientID")).append(",").append(resultSet.getString("name")).append("\n");

                }
            } else if (s.toLowerCase().contains("items")) {
                out = new BufferedWriter(new FileWriter(updatedItemsTXT));
                out.append(firstLineItems).append("\n");
                resultSet = statement.executeQuery("SELECT * FROM items");
                while (resultSet.next()) {
                    out.append(resultSet.getString("itemID")).append(",").append(resultSet.getString("itemName")).append(",").append(resultSet.getString("price")).append(",").append(resultSet.getString("rackID")).append(",").append(resultSet.getString("quantity"));

                }
            } else if (s.toLowerCase().contains("memo") || s.toLowerCase().contains("memos") || s.toLowerCase().contains("receipt") || s.toLowerCase().contains("receipts")) {
                out = new BufferedWriter(new FileWriter(updatedItemsTXT));
                out.append(firstLineMemo).append("\n");
                resultSet = statement.executeQuery("SELECT * FROM memos");
                while (resultSet.next()) {
                    out.append(resultSet.getString("memoID")).append(",").append(resultSet.getString("clientID")).append(",").append(resultSet.getString("itemID")).append(",").append(resultSet.getString("dateProcessed")).append(",").append(resultSet.getString("amountPaid")).append(resultSet.getString("quantity"));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //TODO addUser works perfectly, and a table can also be made from the new input while the program is running. 
    public void addUser(String s) {
        Scanner input = new Scanner(System.in);
        String name = "";
        String ID = "";
        if (!s.isEmpty()) {
            while (ID.isEmpty()) {
                try {
                    Integer.parseInt(s);
                    ID = s;
                    System.out.println("The given ID " + s + " has been added, now please provide the name ");
                    s = input.nextLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            while (name.isEmpty()) {
                try {
                    Integer.parseInt(s);
                    System.out.println("The given name " + s + " is not a valid name, please enter a name which is not a number:");
                    s = input.nextLine();
                } catch (Exception e) {
                    name = s;
                    makeClient(ID,name,"0","0");
                    isClientUpdated=true;
                    writeInData();
                    isClientUpdated=false;
                    setNumberOfClients(getNumberOfClients()+1);
                }
            }
        }
    }

    public void terminate() {
        System.out.println("Exiting...");
        clientsTXT.delete();
        itemsTXT.delete();
        memosTXT.delete();
        updatedClientsTXT.renameTo(new File("clients.txt"));
        updatedItemsTXT.renameTo(new File("items.txt"));
        updatedClientsTXT.renameTo(new File("memos.txt"));

    }

    public void manualSQL() throws SQLException {
        System.out.println("What sql command would you like to run? Please enter below without any quotations:");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(input);
        while (resultSet.next()) {
            if (input.toLowerCase().contains("memos")) {
                System.out.println(resultSet.getString("memoID") + " " + resultSet.getString("clientID") + " " + resultSet.getString("itemID") + " " + resultSet.getString("dateProcessed") + " " + resultSet.getString("amountPaid") + " " + resultSet.getString("quantity"));
            } else if (input.toLowerCase().contains("items")) {
                System.out.println(resultSet.getString("itemID") + " " + resultSet.getString("itemName") + " " + resultSet.getString("price") + " " + resultSet.getString("rackID") + " " + resultSet.getString("quantity"));
            } else if (input.toLowerCase().contains("clients")) {
                System.out.println(resultSet.getString("clientID") + " " + resultSet.getString("name") + " " + resultSet.getString("totalAmount") + " " + resultSet.getString("totalPaid"));
            }
        }
    }

    public void calculate(String clientID) throws SQLException {
        int totalPaid = 0;
        int quantity = 0;
        int totalDue = 0;
        PreparedStatement statement = connection.prepareStatement("Select quantity,sum(amountPaid) as totalDuePaid,itemID from memos where clientID=? group by quantity,itemID");
        statement.setInt(1, Integer.parseInt(clientID));
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            do {
                quantity = resultSet.getInt("quantity");
                statement = connection.prepareStatement("Select price,sum(?*items.price) as totalPrice from items where itemID=? group by price,itemID");
                statement.setInt(1, quantity);
                statement.setInt(2, resultSet.getInt("itemID"));
                ResultSet resultSet1 = statement.executeQuery();
                if (resultSet1.next()) {
                    do {
                        totalDue += resultSet1.getInt("totalPrice");

                    } while (resultSet1.next());
                }
                totalPaid += resultSet.getInt("totalDuePaid");

            } while (resultSet.next());
            statement = connection.prepareStatement("Select totalAmount,totalPaid from clients where clientID=?");
            statement.setInt(1, Integer.parseInt(clientID));
            ResultSet resultSet1 = statement.executeQuery();
            if (resultSet1.next()) {
                statement = connection.prepareStatement("Update clients set totalAmount=?,totalPaid=? where clientID=?");
                statement.setInt(1, totalDue);
                statement.setInt(2, totalPaid);
                statement.setInt(3, Integer.parseInt(clientID));
                statement.executeUpdate();
                isClientUpdated=true;
                writeInData();
                isClientUpdated=false;

            } else {
                System.out.println("ResultSet1 after while loop is empty");
            }
        }
    }

    public void due() throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println("Enter the clientID for the client to find out how much they owe:");
        Scanner sc = new Scanner(System.in);
        String id = sc.nextLine();
        ResultSet resultSet = statement.executeQuery("Select name,totalAmount,totalPaid from clients where clientID=" + Integer.parseInt(id));

        if (resultSet.next()) {
            int totalAmount = resultSet.getInt("totalAmount");
            int totalPaid = resultSet.getInt("totalPaid");
            System.out.printf("Client with name %s owes %d\n", resultSet.getString("name"), (totalAmount - totalPaid));
        } else {
            System.out.println("A customer with clientID " + id + " is not in the database");
        }
    }


    public JTable makeTable(String s, JFrame newFrame) {
        BufferedReader in;
        String fileName = "";
        String firstLine = "";
        String title = "";
        if (s.toLowerCase().contains("clients") || s.toLowerCase().contains("users") || s.toLowerCase().contains("customers") || s.toLowerCase().contains("customer")) {
            fileName = "clients.txt";
            firstLine = getFirstLineClients();
            title = "Clients";
        }
        if (s.toLowerCase().contains("items") || s.toLowerCase().contains("item") || s.toLowerCase().contains("inventory") || s.toLowerCase().contains("stock")) {

            fileName = "items.txt";
            firstLine = getFirstLineItems();
            title = "Inventory";
        }
        if (s.toLowerCase().contains("memo") || s.toLowerCase().contains("ledgers") || s.toLowerCase().contains("ledger") || s.toLowerCase().contains("memos") || s.toLowerCase().contains("receipts") || s.toLowerCase().contains("receipt")) {
            fileName = "memos.txt";
            firstLine = getFirstLineMemo();
            title = "Memos";
        }

        String[] columns = firstLine.split(",");
        String[][] data = new String[getNumberOfClients()][columns.length];
        try {

            in = new BufferedReader(new FileReader(fileName));
            in.readLine();
            String line = in.readLine();
            int index = 0;
            while (line != null) {
                String[] dataParts = line.split(",");
                data[index] = dataParts;
                line = in.readLine();
                index++;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable clientTable = new JTable(data, columns);
        clientTable.setVisible(true);
        clientTable.setBackground(Color.WHITE);
        clientTable.setGridColor(Color.GRAY);
        clientTable.setFillsViewportHeight(true);
        clientTable.setPreferredScrollableViewportSize(new Dimension(50, 50));
        clientTable.setColumnSelectionAllowed(false);
        clientTable.setOpaque(true);
        clientTable.setRowSelectionAllowed(false);
        clientTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        clientTable.setDragEnabled(false);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newFrame.getContentPane().removeAll();
        newFrame.dispose();
        newFrame.setVisible(true);
        newFrame.setBounds(500, 500, 500, 500);
        newFrame.add(clientTable);
        newFrame.addNotify();
        newFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        newFrame.setAlwaysOnTop(false);
        newFrame.setFocusableWindowState(true);
        newFrame.add(new JScrollPane(clientTable));
        newFrame.setTitle(title);

        return clientTable;
    }

}
