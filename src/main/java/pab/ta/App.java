package pab.ta;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@SpringBootApplication
@RestController
public class App {

    public static void main(String[] args) {
        App app = new App();

        try {
//
//            initDB();
//
//            app.addInventory("HP Laptop", 3, 100);

            app.selectAll();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
//        SpringApplication.run(App.class, args);
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:src/main/resources/signal.db");
    }

    private static void initDB() throws SQLException {

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            String sqlWarehouses = """
                    CREATE TABLE IF NOT EXISTS warehouses (
                        id integer PRIMARY KEY,
                        name text NOT NULL,
                        capacity real
                    )""";

            statement.execute(sqlWarehouses);

            String sqlMaterials = """
                    CREATE TABLE IF NOT EXISTS materials (
                    	id integer PRIMARY KEY,
                    	description text NOT NULL
                    )""";

            statement.execute(sqlMaterials);

            String sqlInventory = """
                    CREATE TABLE IF NOT EXISTS inventory (
                    	warehouse_id integer,
                    	material_id integer,
                    	qty real,
                    	PRIMARY KEY (warehouse_id, material_id),
                    	FOREIGN KEY (warehouse_id) REFERENCES warehouses (id),
                    	FOREIGN KEY (material_id) REFERENCES materials (id)
                    )""";

            statement.execute(sqlInventory);
        }
    }

    public void selectAll() {
        String sql = "select id, name, capacity FROM warehouses";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(String.format("%d %s %f",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("capacity")));
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }


    public void insert(String name, double capacity) {
        String sql = "insert into warehouses(name, capacity) values(?,?)";

        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(int id) {
        int result = 0;

        String sql = "delete from warehouses where id = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            result = statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public int update(int id, String name, double capacity) {
        int result = 0;
        String sql = "update warehouses SET name = ?, capacity = ? where id = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, name);
            statement.setDouble(2, capacity);
            statement.setInt(3, id);

            result = statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return result;
    }

    public void addInventory(String description, int warehouseId, double qty) {

        String sqlMaterial = "insert into materials(description) values(?)";
        String sqlInventory = "insert into inventory(warehouse_id, material_id, qty) values(?,?,?)";
        ResultSet rs = null;

        try (Connection connection = connect();
             PreparedStatement materialStmt = connection.prepareStatement(sqlMaterial, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement inventoryStmt = connection.prepareStatement(sqlInventory);
             Statement statement = connection.createStatement();
        ) {
            connection.setAutoCommit(false);
            materialStmt.setString(1, description);

            if(materialStmt.executeUpdate() != 1){
                connection.rollback();
                return;
            }

            rs = statement.executeQuery("select last_insert_rowid()");
            int materialId;
            if (rs.next()){
                materialId = rs.getInt(1);
            }else {
                connection.rollback();
                return;
            }

            inventoryStmt.setInt(1, warehouseId);
            inventoryStmt.setInt(2, materialId);
            inventoryStmt.setDouble(3, qty);
            inventoryStmt.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null){
                    rs.close();
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello, %s!", name);
    }
}
