package lab_1;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.util.List;

public class StudentView {
   public void printStudentDetailsInTable(List<Student> model){
      class InfoTable {
         JFrame frame;

         InfoTable(List<Student> model) {
            frame = new JFrame();
            frame.setTitle("org.example.Student Info");
            Object[] columnNames = {
                    "Index",
                    "Name",
                    "Roll Number"};

            Object[][] tableData = new Object[model.size()][3];
            for (int i = 0; i < model.size(); i++) {
               tableData[i][0] = i + 1;
               tableData[i][1] = model.get(i).getName();
               tableData[i][2] = model.get(i).getRollNo();
            }

            JTable studentInfoTable = new JTable(tableData, columnNames);
            studentInfoTable.setBounds(30, 40, 200, 300);


            JScrollPane scrollPane = new JScrollPane(studentInfoTable);
            frame.add(scrollPane);
            frame.setSize(500, 200);
            frame.setVisible(true);
         }
      }
      new InfoTable(model);
   }
}
