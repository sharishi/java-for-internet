package lab_2;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseController {
    private DatabaseModel model;
    private DatabaseView view;

    public DatabaseController(DatabaseModel model, DatabaseView view) {
        this.model = model;
        this.view = view;
        this.view.addActionListener(new ButtonClickListener());
    }

    public void run() {
        // GUI уже отображается, основной поток управления передан в обработчик событий
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String command = e.getActionCommand();
                String tableName = view.getTableName();

                switch (command) {
                    case "LOAD":
                        String filePath = view.getFilePath();
                        model.loadDataFromFile(tableName, filePath);
                        view.setOutput("Данные загружены из файла.");
                        break;

                    case "INSERT":
                        // Открываем диалог для ввода данных
                        String data = view.showInsertDataDialog();

                        // Разделяем строку на отдельные значения
                        String[] values = data.split(",");

                        // Сохраняем данные в файл
                        String fileName = "data_for_insertion.txt";
                        try (FileWriter writer = new FileWriter("src/main/resources/" + fileName)) {
                            for (String value : values) {
                                writer.write(value.trim() + ",");  // Записываем данные в файл через запятую
                            }
                            writer.write("\n");  // Новая строка для следующей записи
                        } catch (IOException ex) {
                            view.setOutput("Ошибка при записи данных в файл: " + ex.getMessage());
                            break;
                        }

                        // Передаем имя файла в модель для дальнейшей обработки
                        model.loadDataFromFile(tableName, fileName);
                        view.setOutput("Данные сохранены в файл и переданы для вставки.");
                        break;

                    case "SHOW_ALL":
                        List<Map<String, Object>> allData = model.getAllData(tableName);
                        view.setOutput(allData.toString());
                        break;

                    case "FILTER":
                        String criteriaColumn = view.getCriteriaColumn();
                        String criteriaValue = view.getCriteriaValue();
                        List<Map<String, Object>> filteredData = model.getDataByCriteria(tableName, criteriaColumn, criteriaValue);
                        view.setOutput(filteredData.toString());
                        break;

                    case "SORT":
                        String sort_data = view.showInsertDataDialog();
                        String[] sort_values = sort_data.split(",");

                        List<Map<String, Object>> sortedData = model.getSortedData(tableName, sort_values[0], sort_values[1]);
                        view.setOutput(sortedData.toString());
                        break;

                }
            } catch (SQLException | IOException ex) {
                view.setOutput("Error: " + ex.getMessage());
            }
        }
    }
}
