package lab_1;

import java.util.List;

//import VigulearJFITExample1.org.example.StudentView;
public class StudentController {
   private List<Student> model;
   private StudentView view;

   public StudentController(List<Student> model, StudentView view){
      this.model = model;
      this.view = view;
   }

   public List<Student> getModel() {
      return model;
   }

   public void setModel(List<Student> model) {
      this.model = model;
   }

   public StudentView getView() {
      return view;
   }

   public void setView(StudentView view) {
      this.view = view;
   }

   public void updateView(){

      view.printStudentDetailsInTable(model);
   }	
}