import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class StudentManagementSystem {

    // JDBC URL for SQLite (you can change this to MySQL or another database if needed)
    private static final String URL = "jdbc:sqlite:student.db";  // SQLite database

    // Student Model Class
    static class Student {
        private int studentID;
        private String name;
        private String department;
        private int marks;

        public Student(int studentID, String name, String department, int marks) {
            this.studentID = studentID;
            this.name = name;
            this.department = department;
            this.marks = marks;
        }

        public int getStudentID() {
            return studentID;
        }

        public void setStudentID(int studentID) {
            this.studentID = studentID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public int getMarks() {
            return marks;
        }

        public void setMarks(int marks) {
            this.marks = marks;
        }

        @Override
        public String toString() {
            return "ID: " + studentID + " | Name: " + name + " | Department: " + department + " | Marks: " + marks;
        }
    }

    // Controller Class for Database Operations
    static class StudentController {
        // Create the table if it doesn't exist
        public static void createTable() {
            String sql = "CREATE TABLE IF NOT EXISTS Student (" +
                         "StudentID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "Name TEXT NOT NULL, " +
                         "Department TEXT NOT NULL, " +
                         "Marks INTEGER NOT NULL);";
            try (Connection conn = DriverManager.getConnection(URL);
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Table created or already exists.");
            } catch (SQLException e) {
                System.out.println("Error creating table: " + e.getMessage());
            }
        }

        // Insert a new student into the database
        public static void createStudent(Student student) {
            String sql = "INSERT INTO Student (Name, Department, Marks) VALUES (?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getDepartment());
                pstmt.setInt(3, student.getMarks());
                pstmt.executeUpdate();
                System.out.println("Student added successfully!");
            } catch (SQLException e) {
                System.out.println("Error inserting student: " + e.getMessage());
            }
        }

        // Retrieve all students from the database
        public static List<Student> getAllStudents() {
            List<Student> students = new ArrayList<>();
            String sql = "SELECT * FROM Student";
            try (Connection conn = DriverManager.getConnection(URL);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int studentID = rs.getInt("StudentID");
                    String name = rs.getString("Name");
                    String department = rs.getString("Department");
                    int marks = rs.getInt("Marks");
                    students.add(new Student(studentID, name, department, marks));
                }
            } catch (SQLException e) {
                System.out.println("Error reading students: " + e.getMessage());
            }
            return students;
        }

        // Update a student's details in the database
        public static void updateStudent(Student student) {
            String sql = "UPDATE Student SET Name = ?, Department = ?, Marks = ? WHERE StudentID = ?";
            try (Connection conn = DriverManager.getConnection(URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getDepartment());
                pstmt.setInt(3, student.getMarks());
                pstmt.setInt(4, student.getStudentID());
                pstmt.executeUpdate();
                System.out.println("Student updated successfully!");
            } catch (SQLException e) {
                System.out.println("Error updating student: " + e.getMessage());
            }
        }

        // Delete a student from the database
        public static void deleteStudent(int studentID) {
            String sql = "DELETE FROM Student WHERE StudentID = ?";
            try (Connection conn = DriverManager.getConnection(URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentID);
                pstmt.executeUpdate();
                System.out.println("Student deleted successfully!");
            } catch (SQLException e) {
                System.out.println("Error deleting student: " + e.getMessage());
            }
        }
    }

    // View Class for User Interaction
    static class StudentView {

        public static void printMenu() {
            System.out.println("\nMenu:");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
        }

        // Collect student data for CRUD operations
        public static Student getStudentDataFromUser() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter student name: ");
            String name = scanner.nextLine();
            System.out.print("Enter department: ");
            String department = scanner.nextLine();
            System.out.print("Enter marks: ");
            int marks = scanner.nextInt();
            return new Student(0, name, department, marks);  // ID will be auto-generated
        }

        // Collect student ID for updating or deleting
        public static int getStudentIDFromUser() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter student ID: ");
            return scanner.nextInt();
        }

        // Display all students
        public static void displayAllStudents(List<Student> students) {
            if (students.isEmpty()) {
                System.out.println("No students found.");
            } else {
                System.out.println("\nStudentID | Name | Department | Marks");
                System.out.println("----------------------------------------");
                for (Student student : students) {
                    System.out.println(student);
                }
            }
        }
    }

    // Main Application Class (Driver Class)
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Create the table if not exists
        StudentController.createTable();

        boolean exit = false;

        while (!exit) {
            // Display the menu to the user
            StudentView.printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1: {
                    // Add new student
                    Student newStudent = StudentView.getStudentDataFromUser();
                    StudentController.createStudent(newStudent);
                    break;
                }
                case 2: {
                    // View all students
                    List<Student> students = StudentController.getAllStudents();
                    StudentView.displayAllStudents(students);
                    break;
                }
                case 3: {
                    // Update student data
                    int studentID = StudentView.getStudentIDFromUser();
                    Student existingStudent = null;
                    List<Student> students = StudentController.getAllStudents();
                    for (Student student : students) {
                        if (student.getStudentID() == studentID) {
                            existingStudent = student;
                            break;
                        }
                    }

                    if (existingStudent != null) {
                        System.out.println("Current details: " + existingStudent);
                        Student updatedStudent = StudentView.getStudentDataFromUser();
                        updatedStudent.setStudentID(studentID);
                        StudentController.updateStudent(updatedStudent);
                    } else {
                        System.out.println("Student not found.");
                    }
                    break;
                }
                case 4: {
                    // Delete student
                    int studentID = StudentView.getStudentIDFromUser();
                    StudentController.deleteStudent(studentID);
                    break;
                }
                case 5: {
                    // Exit
                    exit = true;
                    System.out.println("Exiting program.");
                    break;
                }
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}
