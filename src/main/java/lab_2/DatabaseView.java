package lab_2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DatabaseView {
    private JFrame frame;
    private JEditorPane outputArea;  // Для вывода HTML
    private JTextField tableField;
    private JTextField criteriaField;
    private JTextField valueField;
    private JTextField filePathField;

    private String sortColumn; // Поле для хранения столбца сортировки
    private String sortOrder;  // Поле для хранения порядка сортировки

    public DatabaseView() {
        frame = new JFrame("Database Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Инициализируем JEditorPane для вывода HTML
        outputArea = new JEditorPane();
        outputArea.setContentType("text/html");  // Устанавливаем тип контента
        outputArea.setEditable(false);  // Оставляем поле неизменяемым
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        inputPanel.add(new JLabel("Table Name:"));
        tableField = new JTextField();
        inputPanel.add(tableField);

        inputPanel.add(new JLabel("Criteria Column:"));
        criteriaField = new JTextField();
        inputPanel.add(criteriaField);

        inputPanel.add(new JLabel("Criteria Value:"));
        valueField = new JTextField();
        inputPanel.add(valueField);

        inputPanel.add(new JLabel("File Path:"));
        filePathField = new JTextField();
        inputPanel.add(filePathField);


        JButton loadButton = new JButton("Load Data from File");
        inputPanel.add(loadButton);
        JButton insertButton = new JButton("Insert Data");
        inputPanel.add(insertButton);
        JButton showAllButton = new JButton("Show All Data");
        inputPanel.add(showAllButton);
        JButton filterButton = new JButton("Filter Data");
        inputPanel.add(filterButton);
        JButton sortButton = new JButton("Sort Data");
        inputPanel.add(sortButton);

        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Устанавливаем action-команды для кнопок
        loadButton.setActionCommand("LOAD");
        insertButton.setActionCommand("INSERT");
        showAllButton.setActionCommand("SHOW_ALL");
        filterButton.setActionCommand("FILTER");
        sortButton.setActionCommand("SORT");
    }

    // Метод для отображения диалогового окна для ввода данных
    public String showInsertDataDialog() {
        // Создаем модальный диалог, который блокирует основное окно
        JDialog dialog = new JDialog(frame, "Insert Data", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 200);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Enter data (comma-separated):");
        inputPanel.add(label, BorderLayout.NORTH);

        JTextField dataField = new JTextField();
        inputPanel.add(dataField, BorderLayout.CENTER);

        JButton insertButton = new JButton("Insert");
        inputPanel.add(insertButton, BorderLayout.SOUTH);

        dialog.add(inputPanel);

        // Используем массив для хранения данных, чтобы получить результат в обработчике
        final String[] inputData = new String[1];

        // Обработчик кнопки "Insert"
        insertButton.addActionListener(e -> {
            inputData[0] = dataField.getText();  // Получаем введенные данные
            dialog.dispose();  // Закрываем диалог после нажатия кнопки
        });

        dialog.setVisible(true);  // Делаем диалог видимым и блокируем выполнение дальше

        return inputData[0];  // Возвращаем введенные данные после закрытия диалога
    }
//
//    // Метод для отображения диалога сортировки
//    public void showSortDialog() {
//        // Запрашиваем у пользователя имя столбца для сортировки и порядок сортировки
//        sortColumn = JOptionPane.showInputDialog(frame, "Enter column to sort by:");
//        sortOrder = JOptionPane.showInputDialog(frame, "Enter sort order (ASC/DESC):");
//    }
//
//    // Метод для получения имени столбца сортировки
//    public String getSortColumn() {
//        return sortColumn;
//    }
//
//    // Метод для получения порядка сортировки
//    public String getSortOrder() {
//        return sortOrder;
//    }

    // Метод для вывода данных с использованием HTML-форматирования
    public void setOutput(String text) {
        // Форматирование текста с использованием HTML для списка
        StringBuilder formattedText = new StringBuilder();
        formattedText.append("<html><body style='font-size: 12px; color: green; font-weight: bold;'>");

        // Убираем ненужные символы и парсим как элементы списка
        text = text.replace("[", "").replace("]", "");  // Убираем скобки
        String[] items = text.split(", \\{");  // Разделяем элементы

        // Проходим по каждому элементу и оформляем как пункт списка
        for (String item : items) {
            formattedText.append("<div>").append(item.replace("{", "").replace("}", "")).append("</div>");
        }

        formattedText.append("</body></html>");

        // Установка текста в JEditorPane
        outputArea.setText(formattedText.toString());
    }

    public String getTableName() {
        return tableField.getText();
    }

    public String getCriteriaColumn() {
        return criteriaField.getText();
    }

    public String getCriteriaValue() {
        return valueField.getText();
    }

    public String getFilePath() {
        return filePathField.getText();
    }

    public void addActionListener(ActionListener listener) {
        for (Component component : frame.getContentPane().getComponents()) {
            if (component instanceof JPanel) {
                for (Component btn : ((JPanel) component).getComponents()) {
                    if (btn instanceof JButton) {
                        ((JButton) btn).addActionListener(listener);
                    }
                }
            }
        }
    }
}
