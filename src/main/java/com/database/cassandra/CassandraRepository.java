package com.database.cassandra;

import com.consumer.kafka.Stock;
import com.datastax.driver.core.Session;

public class CassandraRepository {
    private static final String TBL_BIDDING_STOCK = "bidding.stock";
    private Session session;

    public CassandraRepository(Session session) {
        this.session = session;
    }

    public void insertStock(Stock stock) {
        StringBuilder sb = new StringBuilder("INSERT INTO ")
                .append(TBL_BIDDING_STOCK).append("(name, bidValue, timestamp) ")
                .append("VALUES (")
                .append("'")
                .append(stock.getName())
                .append("', '")
                .append(stock.getBidValue())
                .append("', '")
                .append(stock.getTimestamp())
                .append("');");

        String query = sb.toString();
        System.out.println(query);
        session.execute(query);
    }
}
