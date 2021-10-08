package org.kie.kogito.cep;

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.api.time.SessionPseudoClock;
import org.kie.kogito.rules.RuleUnitInstance;
import org.kie.kogito.rules.units.AbstractRuleUnitInstance;

@Path("/stocks/{ruleUnitId}")
public class StockTickEndpoint {

    @javax.inject.Inject
    RuleUnitRegistry ruleUnitRegistry;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public int insert(@PathParam("ruleUnitId") String ruleUnitId, StockTick stockTick) {
        RuleUnitInstance<StockUnit> instance = ruleUnitRegistry.lookup(ruleUnitId);
        // TODO: It should be possible to access to the access the RuleUnitData from the RuleUnitInstance via public API
        // TODO: also we probably need a better name than 'workingMemory' ?
        ((AbstractRuleUnitInstance<StockUnit>) instance).workingMemory().getStockTicks().append(stockTick);

        return instance.fire();
    }

    @POST()
    @Path("/batch")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public int batchInsert(@PathParam("ruleUnitId") String ruleUnitId, List<StockTick> stockTicks) {
        RuleUnitInstance<StockUnit> instance = ruleUnitRegistry.lookup(ruleUnitId);

        for (StockTick stockTick : stockTicks) {
            ((AbstractRuleUnitInstance<StockUnit>) instance).workingMemory().getStockTicks().append(stockTick);
        }

        return instance.fire();
    }

    @GET()
    @Path("/highestDrop/{company}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ValueDrop findHighestDrop(@PathParam("ruleUnitId") String ruleUnitId, @PathParam("company") String company) {
        RuleUnitInstance<StockUnit> instance = ruleUnitRegistry.lookup(ruleUnitId);

        // TODO: at the moment it isn't possible to pass arguments to a query
        // instance.executeQuery("highestValueDrop", ...)

        return ((AbstractRuleUnitInstance<StockUnit>) instance).workingMemory().getHighestDropsByCompany().get(company);
     }
}
