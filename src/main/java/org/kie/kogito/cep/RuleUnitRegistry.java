package org.kie.kogito.cep;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;

@javax.inject.Singleton
public class RuleUnitRegistry {

    @javax.inject.Inject
    RuleUnit<StockUnit> ruleUnit;

    private final Map<String, RuleUnitInstance<StockUnit>> registry = new HashMap();

    public RuleUnitInstance<StockUnit> lookup(String id) {
        return registry.computeIfAbsent(id, k -> ruleUnit.createInstance(new StockUnit()));
    }
}
