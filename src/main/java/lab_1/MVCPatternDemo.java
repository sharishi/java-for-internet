package lab_1;

import java.util.ArrayList;
import java.util.List;

public class MVCPatternDemo {
   public static void main(String[] args) {

      //fetch student record based on his roll no from the database
      List<Student> model  = retrieveStudentFromDatabase0();

      //Create a view : to write student details
      StudentView view = new StudentView();
      StudentController controller = new StudentController(model, view);

      controller.updateView();

      //update model data
      model  = retrieveStudentFromDatabase1();
      controller.setModel(model);

      controller.updateView();
   }



   private static List<Student> retrieveStudentFromDatabase0(){
      List<Student> students = new ArrayList<>();

      for (int i = 0; i < 100; i++) {
         Student student = new Student();
         student.setName("Name" + i);
         student.setRollNo("RollNo" + i);

         students.add(student);
      }

      return students;
   }

   private static List<Student> retrieveStudentFromDatabase1(){
      List<Student> students = new ArrayList<>();

      for (int i = 0; i < 50; i++) {
         Student student = new Student();
         student.setName("Name1" + i);
         student.setRollNo("RollNo2" + i);

         students.add(student);
      }

      return students;
   }
}
