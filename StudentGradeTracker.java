import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class Student {
    int id;
    String name;
    double marks;

    Student(int id, String name, double marks) {
        this.id = id;
        this.name = name;
        this.marks = marks;
    }

    String getGrade() {
        if (marks >= 90) return "A+";
        else if (marks >= 80) return "A";
        else if (marks >= 70) return "B";
        else if (marks >= 60) return "C";
        else if (marks >= 50) return "D";
        else return "F";
    }
}

class BackgroundPanel extends JPanel {
    private final Image backgroundImage;

    public BackgroundPanel(Image image) {
        this.backgroundImage = image;
        if (image == null) {
            setBackground(Color.LIGHT_GRAY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

public class StudentGradeTracker extends JFrame {

    JTextField idField, nameField, marksField, searchField;
    JButton addBtn, deleteBtn, updateBtn, searchBtn, summaryBtn, saveBtn;

    JTable table;
    DefaultTableModel model;

    ArrayList<Student> students = new ArrayList<>();

    public StudentGradeTracker() {

        setTitle("Advanced Student Grade Tracker");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.GRAY);

        // ================= TOP PANEL =================
        JPanel topPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        topPanel.setOpaque(true);
        topPanel.setBackground(Color.YELLOW);

        idField = new JTextField();
        nameField = new JTextField();
        marksField = new JTextField();
        searchField = new JTextField();

        searchBtn = new JButton("Search");

        topPanel.add(new JLabel("Student ID"));
        topPanel.add(new JLabel("Student Name"));
        topPanel.add(new JLabel("Marks"));
        topPanel.add(new JLabel("Search by ID"));
        topPanel.add(new JLabel(""));

        topPanel.add(idField);
        topPanel.add(nameField);
        topPanel.add(marksField);
        topPanel.add(searchField);
        // searchBtn moved to buttonPanel

        // ================= TABLE =================
        String[] columns = {
                "ID", "Name", "Marks", "Grade"
        };

        model = new DefaultTableModel(columns, 0);

        table = new JTable(model);
        table.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // ================= BUTTON PANEL =================
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        addBtn = new JButton("Add");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        summaryBtn = new JButton("Summary");
        saveBtn = new JButton("Save File");

        // Set button colors
        addBtn.setBackground(Color.GREEN);
        updateBtn.setBackground(Color.BLUE);
        deleteBtn.setBackground(Color.YELLOW);
        searchBtn.setBackground(Color.YELLOW);
        summaryBtn.setBackground(Color.ORANGE);
        saveBtn.setBackground(Color.CYAN);

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(summaryBtn);
        buttonPanel.add(saveBtn);

        // ================= LAYOUT =================
        try {
            Image bgImage = ImageIO.read(new File("2.png"));
            BackgroundPanel bgPanel = new BackgroundPanel(bgImage);
            bgPanel.setLayout(new BorderLayout(10, 10));
            bgPanel.add(topPanel, BorderLayout.NORTH);
            bgPanel.add(scrollPane, BorderLayout.CENTER);
            bgPanel.add(buttonPanel, BorderLayout.SOUTH);
            setContentPane(bgPanel);
        } catch (IOException e) {
            setLayout(new BorderLayout(10, 10));
            add(topPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        // ================= BUTTON ACTIONS =================

        // ADD STUDENT
        addBtn.addActionListener(e -> addStudent());

        // DELETE STUDENT
        deleteBtn.addActionListener(e -> deleteStudent());

        // UPDATE STUDENT
        updateBtn.addActionListener(e -> updateStudent());

        // SEARCH STUDENT
        searchBtn.addActionListener(e -> searchStudent());

        // SHOW SUMMARY
        summaryBtn.addActionListener(e -> showSummary());

        // SAVE FILE
        saveBtn.addActionListener(e -> saveToFile());

        // TABLE CLICK EVENT
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int row = table.getSelectedRow();

                idField.setText(model.getValueAt(row, 0).toString());
                nameField.setText(model.getValueAt(row, 1).toString());
                marksField.setText(model.getValueAt(row, 2).toString());
            }
        });

        setVisible(true);
    }

    // ================= ADD =================
    void addStudent() {

        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            double marks = Double.parseDouble(marksField.getText());

            Student s = new Student(id, name, marks);

            students.add(s);

            model.addRow(new Object[]{
                    s.id,
                    s.name,
                    s.marks,
                    s.getGrade()
            });

            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "<html><b>Invalid Input!</b></html>");
        }
    }

    // ================= DELETE =================
    void deleteStudent() {

        int row = table.getSelectedRow();

        if (row >= 0) {
            students.remove(row);
            model.removeRow(row);
        } else {
            JOptionPane.showMessageDialog(this,
                    "<html><b>Select a row first!</b></html>");
        }
    }

    // ================= UPDATE =================
    void updateStudent() {

        int row = table.getSelectedRow();

        if (row >= 0) {

            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            double marks = Double.parseDouble(marksField.getText());

            Student s = students.get(row);

            s.id = id;
            s.name = name;
            s.marks = marks;

            model.setValueAt(id, row, 0);
            model.setValueAt(name, row, 1);
            model.setValueAt(marks, row, 2);
            model.setValueAt(s.getGrade(), row, 3);

            clearFields();

        } else {
            JOptionPane.showMessageDialog(this,
                    "<html><b>Select a row first!</b></html>");
        }
    }

    // ================= SEARCH =================
    void searchStudent() {

        String keyword = searchField.getText().trim();

        for (int i = 0; i < model.getRowCount(); i++) {

            String id =
                    model.getValueAt(i, 0).toString();

            if (id.equals(keyword)) {

                table.setRowSelectionInterval(i, i);

                JOptionPane.showMessageDialog(this,
                        "<html><b>Student Found!</b></html>");

                return;
            }
        }

        JOptionPane.showMessageDialog(this,
                "<html><b>Student Not Found!</b></html>");
    }

    // ================= SUMMARY =================
    void showSummary() {

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "<html><b>No student data!</b></html>");
            return;
        }

        double total = 0;
        double highest = students.get(0).marks;
        double lowest = students.get(0).marks;

        String highName = students.get(0).name;
        String lowName = students.get(0).name;

        for (Student s : students) {

            total += s.marks;

            if (s.marks > highest) {
                highest = s.marks;
                highName = s.name;
            }

            if (s.marks < lowest) {
                lowest = s.marks;
                lowName = s.name;
            }
        }

        double average = total / students.size();

        String report = """
                ===== SUMMARY REPORT =====

                Total Students : %d
                Average Marks  : %.2f
                Highest Marks  : %.2f (%s)
                Lowest Marks   : %.2f (%s)
                """.formatted(students.size(), average, highest, highName, lowest, lowName);

        String htmlReport = "<html><b>" + report.replace("\n", "<br>") + "</b></html>";
        JOptionPane.showMessageDialog(this, htmlReport);
    }

    // ================= SAVE FILE =================
    void saveToFile() {

        try (FileWriter writer = new FileWriter("students.txt")) {

            for (Student s : students) {

                writer.write(
                        s.id + "," +
                        s.name + "," +
                        s.marks + "," +
                        s.getGrade() + "\n"
                );
            }

            JOptionPane.showMessageDialog(this,
                    "<html><b>Data Saved Successfully!</b></html>");

        } catch (IOException e) {

            JOptionPane.showMessageDialog(this,
                    "<html><b>Error Saving File!</b></html>");
        }
    }

    // ================= CLEAR FIELDS =================
    void clearFields() {

        idField.setText("");
        nameField.setText("");
        marksField.setText("");
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        new StudentGradeTracker();
    }
}