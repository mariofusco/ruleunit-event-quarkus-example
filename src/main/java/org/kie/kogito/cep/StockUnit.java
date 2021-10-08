package org.kie.kogito.cep;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.RuleUnitData;

public class StockUnit implements RuleUnitData {

    private DataStream<StockTick> stockTicks;
    private DataStore<ValueDrop> valueDrops;

    private Map<String, ValueDrop> highestDropsByCompany = new HashMap<>();

    public StockUnit() {
        this(EventListDataStream.create(), DataSource.createStore());
    }

    public StockUnit(DataStream<StockTick> stockTicks, DataStore<ValueDrop> valueDrops) {
        this.stockTicks = stockTicks;
        this.valueDrops = valueDrops;
    }

    public DataStream<StockTick> getStockTicks() {
        return stockTicks;
    }

    public DataStore<ValueDrop> getValueDrops() {
        return valueDrops;
    }

    public Map<String, ValueDrop> getHighestDropsByCompany() {
        return highestDropsByCompany;
    }
}
