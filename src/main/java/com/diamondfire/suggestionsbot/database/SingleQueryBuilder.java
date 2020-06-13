package com.diamondfire.suggestionsbot.database;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * <h1>SingleQueryBuilder</h1>
 * The SingleQueryBuilder class allows you to initialize a prepared query within lambda statements.
 *
 * Here is an example of a basic implementation
 *
 * <pre>{@code
 *   new SingleQueryBuilder()
 *                 .query("SELECT * FROM players WHERE uuid = ?;", (statement) -> {
 *                     statement.setString(1, playerUUID);
 *                 })
 *                 .onQuery((resultTable) -> {
 *                 System.out.println(resultTable.getString("player_name"));
 *                 })
 *                 .onNotFound(() -> {
 *                     System.out.println("Player was not found!");
 *                 }).execute();
 *                 }
 * </pre>
 * <p>
 * As you can see, "SELECT * FROM players WHERE uuid = ?" is queried after given its values from {@link PreparedStatementManager}.
 * Then, if the query result is not empty it will execute .onQuery(), otherwise it will execute .onNotFound() if it was specified.
 *
 * @author  Owen1212055
 * @version 1.0
 */
public class SingleQueryBuilder {

    String query;
    PreparedStatementManager preparedStatement;
    ResultSetManager onQuery;
    Runnable onFail;

    public SingleQueryBuilder query(@NotNull @Language("SQL") String query, @NotNull PreparedStatementManager statement) {
        this.query = query;
        this.preparedStatement = statement;
        return this;
    }

    public SingleQueryBuilder query(@NotNull @Language("SQL") String query) {
        this.query = query;
        return this;
    }

    /**
     * Because onQuery is only executed if TableSet#next() is true, when iterating through you must use a do while loop.
     * This is because TableSet#next() shifts the table forward, and using a while loop will cause it do shift again when we start accessing the data.
     * However, using a do while loop will ensure that the first time it is iterated through that the data isn't shifted.
     * <pre>
     *     if (table.next() {
     *
     *     // Bad
     *     while (table.next()) {
     *
     *     }
     *
     *     // Good
     *     do {
     *
     *     } while(table.next());
     *
     *     }
     * </pre>
     */
    public SingleQueryBuilder onQuery(@NotNull ResultSetManager onQuery) {
        this.onQuery = onQuery;
        return this;
    }

    public SingleQueryBuilder onNotFound(@Nullable Runnable onFail) {
        this.onFail = onFail;
        return this;
    }

    public void execute() {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            if (preparedStatement != null) {
                preparedStatement.run(statement);
            }

            ResultSet set = statement.executeQuery();
            if (set.next()) {
                onQuery.run(set);
            } else {
               if (onFail != null) {
                   onFail.run();
               }
            }
            set.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
