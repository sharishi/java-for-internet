package lab_2;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.sql.Date; // Импортируйте класс Date из java.sql
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class DatabaseModel {
    private final String url = "jdbc:postgresql://localhost:5432/java";
    private final String user = "vika";
    private final String password = "a1r2c3d4";

    public Connection connect() throws SQLException {

        return DriverManager.getConnection(url, user, password);
    }



    public void insertData(String tableName, Map<String, Object> data) throws SQLException {
        StringBuilder fields = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        for (String field : data.keySet()) {
            if (fields.length() > 0) {
                fields.append(", ");
                placeholders.append(", ");
            }
            fields.append(field);
            placeholders.append("?");
        }

        String query = String.format("INSERT INTO %s(%s) VALUES(%s)", tableName, fields, placeholders);
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (Object value : data.values()) {
                stmt.setObject(index++, value);
            }
            stmt.executeUpdate();
        }
    }

    public List<Map<String, Object>> getAllData(String tableName) throws SQLException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + tableName;

        try (Connection conn = connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(metaData.getColumnName(i), rs.getObject(i));
                }
                dataList.add(rowData);
            }
        }
        return dataList;
    }

    public void loadDataFromFile(String tableName, String filePath) throws SQLException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/" + filePath))) {
                String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Предполагаем, что значения разделены запятыми
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }

                // Проверка на корректность введённых данных
                Map<String, Object> data = new HashMap<>();
                switch (tableName.toLowerCase()) {
                    case "employees":
                        if (values.length != 8) {
                            throw new IllegalArgumentException("Invalid number of fields for employees. Expected 8 fields.");
                        }
                        data.put("first_name", values[0]);
                        data.put("last_name", values[1]);
                        data.put("contact_information", values[2]);

                        // Преобразование hire_date и date_of_birth в тип date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Формат даты
                        try {
                            Date hireDate = new Date(dateFormat.parse(values[3]).getTime());
                            data.put("hire_date", hireDate);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException("Invalid hire date format: " + values[3]);
                        }

                        data.put("post", values[4]);

                        // Преобразование department в Integer
                        try {
                            data.put("department", Integer.parseInt(values[5]));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid department ID: " + values[5]);
                        }

                        // Преобразование date_of_birth в тип date
                        try {
                            Date dateOfBirth = new Date(dateFormat.parse(values[6]).getTime());
                            data.put("date_of_birth", dateOfBirth);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException("Invalid date of birth format: " + values[6]);
                        }

                        // Преобразование salary в BigDecimal
                        try {
                            BigDecimal salary = new BigDecimal(values[7].replace("$", "").replace(",", "").trim());
                            data.put("salary", salary);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid salary value: " + values[7]);
                        }
                        break;
                    case "tasks":
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        // Пример для таблицы departments
                        if (values.length != 6) {
                            throw new IllegalArgumentException("Invalid number of fields for tasks.txt. Expected 6 field.");
                        }
                        data.put("task_description", values[0]);
                        try {
                            Date start_date = new Date(dateFormat1.parse(values[1]).getTime());
                            data.put("start_date", start_date);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException("Invalid start_date format: " + values[1]);
                        }
                        try {
                            Date end_date = new Date(dateFormat1.parse(values[2]).getTime());
                            data.put("end_date", end_date);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException("Invalid end_date format: " + values[2]);
                        }

                        try {
                            data.put("priority", Integer.parseInt(values[3]));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid priority: " + values[3]);
                        }
                        try {
                            data.put("project", Integer.parseInt(values[4]));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid project: " + values[4]);
                        }
                        try {
                            data.put("assigned_employee_id", Integer.parseInt(values[5]));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid assigned_employee_id: " + values[5]);
                        }
                        break;
                    // нужно добавить другие случаи
                    default:
                        throw new IllegalArgumentException("Unknown table name: " + tableName);
                }
                insertData(tableName, data);
            }
        }
    }
    public List<Map<String, Object>> getDataByCriteria(String tableName, String columnName, Object value) throws SQLException {
        // Проверка на допустимость имени таблицы и колонки
        if (!isValidTableName(tableName) || !isValidColumnName(tableName, columnName)) {
            throw new IllegalArgumentException("Invalid table or column name");
        }

        // Приведение значения к правильному типу в зависимости от колонки
        if (isIntegerColumn(tableName, columnName) && value instanceof String) {
            try {
                value = Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value for integer column", e);
            }
        } else if (isDateColumn(tableName, columnName) && value instanceof String) {
            try {
                value = java.sql.Date.valueOf((String) value); // преобразование строки в дату
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid value for date column", e);
            }
        }
        // Добавьте другие преобразования для типов данных (например, money), если это необходимо

        List<Map<String, Object>> dataList = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE %s = ?", tableName, columnName);

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> rowData = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    dataList.add(rowData);
                }
            }
        }
        return dataList;
    }

    // Пример проверки на валидность таблицы и колонки
    private boolean isValidTableName(String tableName) {
        return Arrays.asList("employees", "departments", "clients", "projects", "tasks.txt", "skills", "employeeskills", "employeeprojects", "taskstatement", "projectclients")
                .contains(tableName);
    }

    private boolean isValidColumnName(String tableName, String columnName) {
        // Определите допустимые колонки для каждой таблицы
        switch (tableName) {
            case "employees":
                return Arrays.asList("first_name", "last_name", "contact_information", "hire_date", "post", "department", "date_of_birth", "salary", "employee_id").contains(columnName);
            case "departments":
                return Arrays.asList("department_id", "department_name").contains(columnName);
            case "clients":
                return Arrays.asList("client_id", "client_name", "contact_information").contains(columnName);
            case "projects":
                return Arrays.asList("project_id", "project_name", "description", "start_date", "end_date", "status", "client", "leader_id").contains(columnName);
            case "tasks.txt":
                return Arrays.asList("task_id", "task_description", "start_date", "end_date", "priority", "project", "assigned_employee_id").contains(columnName);
            // Добавьте остальные таблицы и их колонки
            default:
                return false;
        }
    }

    // Пример проверок типов колонок
    private boolean isIntegerColumn(String tableName, String columnName) {
        // Определите, какие колонки являются integer
        switch (tableName) {
            case "employees":
                return Arrays.asList("employee_id", "department", "salary").contains(columnName);
            case "departments":
                return "department_id".equals(columnName);
            case "projects":
                return Arrays.asList("project_id", "client", "leader_id").contains(columnName);
            // Добавьте остальные таблицы
            default:
                return false;
        }
    }

    private boolean isDateColumn(String tableName, String columnName) {
        // Определите, какие колонки являются date
        switch (tableName) {
            case "employees":
                return Arrays.asList("hire_date", "date_of_birth").contains(columnName);
            case "projects":
                return Arrays.asList("start_date", "end_date").contains(columnName);
            case "tasks.txt":
                return Arrays.asList("start_date", "end_date").contains(columnName);
            // Добавьте остальные таблицы
            default:
                return false;
        }
    }

    // Метод для получения отсортированных данных из базы данных
    public List<Map<String, Object>> getSortedData(String tableName, String column, String order) throws SQLException {
        String query = String.format("SELECT * FROM %s ORDER BY %s %s", tableName, column, order);
        List<Map<String, Object>> data = new ArrayList<>();

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            // Проходим по результатам запроса и сохраняем их в список
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                data.add(row);
            }
        }

        return data;
    }

}

