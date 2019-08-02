package dev.itseternity.chatlogger.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DatabaseConstants {

    public static final String TABLE_NAME = "chat_logs";
    public static final String UUID = "uuid";
    public static final String USERNAME = "name";
    public static final String SERVER_NAME = "server_name";
    public static final String LOG_TYPE = "log_type";
    public static final String CREATED_AT = "created_at";
    public static final String MESSAGE = "message";

    public static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            UUID + " VARCHAR(36) NOT NULL," +
            USERNAME + " VARCHAR(16) NOT NULL," +
            SERVER_NAME + " VARCHAR(10) NOT NULL," +
            LOG_TYPE + " VARCHAR(10) NOT NULL," +
            CREATED_AT + " BIGINT NOT NULL," +
            MESSAGE + " TEXT NOT NULL" +
            ") ENGINE=InnoDB";

    public static final String INSERT_CHATLOG_QUERY = "INSERT INTO " + TABLE_NAME + "(" +
            UUID + "," +
            USERNAME + "," +
            SERVER_NAME + "," +
            LOG_TYPE + "," +
            CREATED_AT + "," +
            MESSAGE +
            ") VALUES (?, ?, ?, ?, ?, ?);";

    public static final String SELECT_CHATLOG_UUID = "SELECT * FROM " + TABLE_NAME + " WHERE " + UUID + " = ?;";

}
