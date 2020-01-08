package at.htl.gotjdbcrepository.control;

import at.htl.gotjdbcrepository.entity.Person;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class PersonRepository implements Repository {
    public static final String USERNAME = "app";
    public static final String PASSWORD = "app";
    public static final String DATABASE = "db";
    public static final String URL = "jdbc:derby://localhost:1527/" + DATABASE + ";create=true";
    public static final String TABLE_NAME = "person";

    private static PersonRepository instance;

    private PersonRepository() {
    }

    public static synchronized PersonRepository getInstance() {

        return null;
    }

    private void createTable() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            try (Statement stmt = conn.createStatement()) {
                String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                        "id INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT " + TABLE_NAME + "_pk PRIMARY KEY," +
                        "name VARCHAR(255)," +
                        "city VARCHAR(255)," +
                        "house VARCHAR(255)," +
                        "CONSTRAINT " + TABLE_NAME + "_uq UNIQUE (name, city, house)" +
                        ")";
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            //System.err.format("SQL State: %s - %s\n", e.getSQLState(), e.getMessage());
        }
    }

    public void deleteAll() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)){
            try (Statement stmt = conn.createStatement()){
                String sql = "DELETE FROM " + TABLE_NAME;
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                final int rowsAffected =preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Hat newPerson eine ID (id != null) so in der Tabelle die entsprechende Person gesucht und upgedated
     * Hat newPerson keine ID wird ein neuer Datensatz eingefügt.
     *
     * Wie man die generierte ID zurück erhält: https://stackoverflow.com/a/1915197
     *
     * Falls ein Fehler auftritt, wird nur die Fehlermeldung ausgegeben, der Programmlauf nicht abgebrochen
     *
     * Verwenden sie hier die privaten MEthoden update() und insert()
     *
     * @param newPerson
     * @return die gespeicherte Person mit der (neuen) id
     */
    @Override
    public Person save(Person newPerson) {

        return insert(new Person());
    }

    /**
     *
     * Wie man die generierte ID erhält: https://stackoverflow.com/a/1915197
     *
     * @param personToSave
     * @return Rückgabe der Person inklusive der neu generierten ID
     */
    private Person insert(Person personToSave) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO APP.PERSON (name, city, house) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            {
                preparedStatement.setString(1, personToSave.getName());
                preparedStatement.setString(2, personToSave.getCity());
                preparedStatement.setString(3, personToSave.getHouse());

                if (preparedStatement.executeUpdate() == 0) {
                    throw new SQLException("Creating user failed, no rows affected");
                }

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        personToSave.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return personToSave;
    }

    /**
     *
     * @param personToSave
     * @return wenn erfolgreich --> Anzahl der eingefügten Zeilen, also 1
     *         wenn nicht erfolgreich --> -1
     */
    private int update(Person personToSave) {

        return -1;
    }

    @Override
    public void delete(long id) {
    }

    /**
     *
     * Finden Sie eine Person anhand Ihrer ID
     *
     * @param id
     * @return die gefundene Person oder wenn nicht gefunden wird null zurückgegeben
     */
    public Person find(long id) {
        try (
                Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM APP.PERSON WHERE ID=?");
        ) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                Person person = new Person(resultSet.getString("name"), resultSet.getString("city"),
                        resultSet.getString("house"));
                person.setId(resultSet.getLong("id"));

                return person;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param house Name des Hauses
     * @return Liste aller Personen des gegebenen Hauses
     */
    public List<Person> findByHouse(String house) {

        return null;
    }


}
