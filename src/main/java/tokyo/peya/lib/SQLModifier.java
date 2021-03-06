package tokyo.peya.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SQLのUtil
 */
public class SQLModifier
{
    /**
     * インサート
     * @param connection SQLコネクション
     * @param database データベース名
     * @param value 値
     * @throws SQLException エラー
     */
    public static void insert(Connection connection, String database, Object... value) throws SQLException
    {
        StringBuilder p = new StringBuilder();

        Arrays.stream(value)
                .forEach(s -> {
                    if (!p.toString().isEmpty())
                        p.append(", ");
                    p.append("?");
                });

        String sql = "INSERT INTO " + database + " VALUES (" +
                p.toString() +
                ")";

        try (PreparedStatement s = connection.prepareStatement(sql))
        {
            int[] count = {0};
            Arrays.stream(value)
                    .forEachOrdered(o -> {
                        try
                        {
                            s.setObject(++count[0], o);
                        }
                        catch (SQLException e)
                        {
                            System.out.println("An exception has occurred.");
                            e.printStackTrace();
                        }
                    });
            s.execute();
        }
    }

    /**
     * 削除
     * @param connection コネクション
     * @param database データベース名
     * @param map 条件
     * @throws SQLException エラー
     */
    public static void delete(Connection connection, String database, HashMap<String, ?> map) throws SQLException
    {
        StringBuilder p = new StringBuilder();

        map.keySet()
                .forEach(s -> {
                    if (!p.toString().isEmpty())
                        p.append(", ");
                    p.append(s).append("=?");
                });

        String sql = "DELETE FROM " + database + " WHERE " + p.toString();

        if (p.toString().isEmpty())
            sql = "DELETE FROM " + database;

        try (PreparedStatement s = connection.prepareStatement(sql))
        {
            AtomicInteger count = new AtomicInteger();
            map.values()
                    .forEach(o -> {
                        try
                        {
                            s.setObject(count.incrementAndGet(), o);
                        }
                        catch (SQLException e)
                        {
                            System.out.println("An exception has occurred.");
                            e.printStackTrace();
                        }
                    });
            s.execute();
        }
    }

    /**
     * 実行
     * @param connection コネクション
     * @param sql SQL文
     * @param values 値
     * @throws SQLException エラー
     */
    public static void exec(Connection connection, String sql, Object... values) throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            AtomicReference<SQLException> exception = new AtomicReference<>();
            AtomicInteger integer = new AtomicInteger();

            Arrays.stream(values)
                    .forEachOrdered(o -> {
                        try
                        {
                            statement.setObject(integer.getAndIncrement(), o);
                        }
                        catch (SQLException throwable)
                        {
                            exception.set(throwable);
                        }
                    });

            if (exception.get() != null)
                throw exception.get();

            statement.execute();

        }
    }
}
