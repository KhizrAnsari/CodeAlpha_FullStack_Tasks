import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Student Grade Tracker
 * - Add students and enter their grades (subject-wise marks out of 100)
 * - Average, highest and lowest score for every student
 * - Detailed student report and a class summary
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Student> students = new ArrayList<Student>();

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("     STUDENT GRADE TRACKER");
        System.out.println("================================");

        try {
            runMenu();
        } catch (NoSuchElementException e) {
            // Input stream closed (for example Ctrl+D / Ctrl+Z)
            System.out.println("\nInput ended. Exiting program.");
        }
    }

    // Main menu loop
    private static void runMenu() {

        while (true) {

            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Add Student");
            System.out.println("2. Enter Grades");
            System.out.println("3. Student Report");
            System.out.println("4. Class Summary");
            System.out.println("5. View All Students");
            System.out.println("6. Exit");

            int choice = readInt("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    enterGrades();
                    break;
                case 3:
                    studentReport();
                    break;
                case 4:
                    classSummary();
                    break;
                case 5:
                    listStudents();
                    break;
                case 6:
                    System.out.println("\nThank you for using Student Grade Tracker!");
                    return;
            }
        }
    }

    // ---------- Menu actions ----------

    private static void addStudent() {

        String name = readText("\nEnter student name: ");

        if (findByName(name) != null) {
            System.out.println("A student with this name already exists.");
            return;
        }

        students.add(new Student(name));
        System.out.println("Student \"" + name + "\" added successfully.");
    }

    private static void enterGrades() {

        Student student = chooseStudent();
        if (student == null) {
            return;
        }

        do {
            String subject = readText("\nEnter subject name: ");

            if (student.hasSubject(subject)) {
                System.out.println("Grade for " + subject + " already entered.");
                continue;
            }

            double marks = readDouble("Enter marks for " + subject + " (0-100): ", 0, 100);
            student.addGrade(subject, marks);
            System.out.println("Grade saved: " + subject + " = " + marks);

        } while (readYesNo("Add another grade for " + student.getName() + "? (yes/no): "));
    }

    private static void studentReport() {

        Student student = chooseStudent();
        if (student == null) {
            return;
        }

        System.out.println("\n========== STUDENT REPORT ==========");
        System.out.println("Name: " + student.getName());

        if (!student.hasGrades()) {
            System.out.println("No grades entered yet.");
            return;
        }

        System.out.println("--------------------------------");
        System.out.printf("%-15s %8s %7s%n", "Subject", "Marks", "Grade");
        System.out.println("--------------------------------");

        for (Grade g : student.getGrades()) {
            System.out.printf("%-15s %8.2f %7s%n", g.getSubject(), g.getMarks(),
                    Student.letterGrade(g.getMarks()));
        }

        System.out.println("--------------------------------");
        System.out.println("Total Subjects : " + student.getGrades().size());
        System.out.printf("Total Marks    : %.2f%n", student.getTotal());
        System.out.printf("Average        : %.2f%n", student.getAverage());
        System.out.println("Highest Score  : " + student.getHighest().getMarks()
                + " (" + student.getHighest().getSubject() + ")");
        System.out.println("Lowest Score   : " + student.getLowest().getMarks()
                + " (" + student.getLowest().getSubject() + ")");
        System.out.println("Overall Grade  : " + Student.letterGrade(student.getAverage()));
        System.out.println("Result         : " + (student.getAverage() >= 40 ? "PASS" : "FAIL"));
    }

    private static void classSummary() {

        ArrayList<Student> graded = new ArrayList<Student>();
        for (Student s : students) {
            if (s.hasGrades()) {
                graded.add(s);
            }
        }

        if (graded.isEmpty()) {
            System.out.println("\nNo grades available yet. Enter grades first.");
            return;
        }

        System.out.println("\n========== CLASS SUMMARY ==========");
        System.out.printf("%-15s %9s %7s%n", "Student", "Average", "Grade");
        System.out.println("-----------------------------------");

        double sumOfAverages = 0;
        Student topper = graded.get(0);
        Student lowest = graded.get(0);
        Student highestScoreOwner = graded.get(0);
        Student lowestScoreOwner = graded.get(0);

        for (Student s : graded) {
            System.out.printf("%-15s %9.2f %7s%n", s.getName(), s.getAverage(),
                    Student.letterGrade(s.getAverage()));

            sumOfAverages += s.getAverage();

            if (s.getAverage() > topper.getAverage()) {
                topper = s;
            }
            if (s.getAverage() < lowest.getAverage()) {
                lowest = s;
            }
            if (s.getHighest().getMarks() > highestScoreOwner.getHighest().getMarks()) {
                highestScoreOwner = s;
            }
            if (s.getLowest().getMarks() < lowestScoreOwner.getLowest().getMarks()) {
                lowestScoreOwner = s;
            }
        }

        System.out.println("-----------------------------------");
        System.out.printf("Class Average       : %.2f%n", sumOfAverages / graded.size());
        System.out.printf("Topper              : %s (%.2f)%n", topper.getName(), topper.getAverage());
        System.out.printf("Lowest Average      : %s (%.2f)%n", lowest.getName(), lowest.getAverage());
        System.out.println("Highest Single Score: " + highestScoreOwner.getHighest().getMarks()
                + " - " + highestScoreOwner.getName()
                + " (" + highestScoreOwner.getHighest().getSubject() + ")");
        System.out.println("Lowest Single Score : " + lowestScoreOwner.getLowest().getMarks()
                + " - " + lowestScoreOwner.getName()
                + " (" + lowestScoreOwner.getLowest().getSubject() + ")");
    }

    private static void listStudents() {

        if (students.isEmpty()) {
            System.out.println("\nNo students added yet.");
            return;
        }

        System.out.println("\n========== STUDENTS ==========");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.println((i + 1) + ". " + s.getName()
                    + " (" + s.getGrades().size() + " subject(s))");
        }
    }

    // ---------- Helpers ----------

    // Shows the student list and lets the user pick one. Returns null if there are none.
    private static Student chooseStudent() {

        if (students.isEmpty()) {
            System.out.println("\nNo students added yet. Add a student first.");
            return null;
        }

        listStudents();
        int number = readInt("Select student number: ", 1, students.size());
        return students.get(number - 1);
    }

    private static Student findByName(String name) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    // Keeps asking until the user types some text
    private static String readText(String prompt) {

        while (true) {
            System.out.print(prompt);
            String text = sc.nextLine().trim();

            if (text.isEmpty()) {
                System.out.println("This field cannot be empty.");
            } else {
                return text;
            }
        }
    }

    // Keeps asking until the user enters a whole number in [min, max]
    private static int readInt(String prompt, int min, int max) {

        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a whole number.");
            }
        }
    }

    // Keeps asking until the user enters a number in [min, max]
    private static double readDouble(String prompt, double min, double max) {

        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                if (Double.isNaN(value) || Double.isInfinite(value) || value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    private static boolean readYesNo(String prompt) {

        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toLowerCase();

            if (input.equals("yes") || input.equals("y")) {
                return true;
            }
            if (input.equals("no") || input.equals("n")) {
                return false;
            }
            System.out.println("Please type yes or no.");
        }
    }
}

// ===================== GRADE =====================
// One subject and the marks scored in it
class Grade {

    private final String subject;
    private final double marks;

    public Grade(String subject, double marks) {
        this.subject = subject;
        this.marks = marks;
    }

    public String getSubject() {
        return subject;
    }

    public double getMarks() {
        return marks;
    }
}

// ===================== STUDENT =====================
// A student and all of their grades
class Student {

    private final String name;
    private final ArrayList<Grade> grades = new ArrayList<Grade>();

    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Grade> getGrades() {
        return grades;
    }

    public boolean hasGrades() {
        return !grades.isEmpty();
    }

    public boolean hasSubject(String subject) {
        for (Grade g : grades) {
            if (g.getSubject().equalsIgnoreCase(subject)) {
                return true;
            }
        }
        return false;
    }

    public void addGrade(String subject, double marks) {
        grades.add(new Grade(subject, marks));
    }

    public double getTotal() {
        double total = 0;
        for (Grade g : grades) {
            total += g.getMarks();
        }
        return total;
    }

    public double getAverage() {
        return grades.isEmpty() ? 0 : getTotal() / grades.size();
    }

    public Grade getHighest() {
        Grade highest = grades.get(0);
        for (Grade g : grades) {
            if (g.getMarks() > highest.getMarks()) {
                highest = g;
            }
        }
        return highest;
    }

    public Grade getLowest() {
        Grade lowest = grades.get(0);
        for (Grade g : grades) {
            if (g.getMarks() < lowest.getMarks()) {
                lowest = g;
            }
        }
        return lowest;
    }

    // Converts marks (out of 100) into a letter grade
    public static String letterGrade(double marks) {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }
}