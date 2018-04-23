package nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping.values;

import org.apache.log4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This class is used to evaluate javascriptcode.
 * @param <T>
 * @param <V>
 */
public abstract class ValueScriptExpression<T,V> implements ValueExpression<T,V> {

    private Logger logger = Logger.getLogger(ValueScriptExpression.class);

    private final String script;
    private final String varName;
    private final Class<V> type;

    public ValueScriptExpression(String script, String varName, Class<V> type) {
        this.script = script;
        this.varName = varName;
        this.type = type;
    }

    @Override
    public V eval(T val) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.put(varName, val);
        try {
            return type.cast(engine.eval(script));
        } catch (ScriptException | ClassCastException e) {
            logger.error(e);
            return null;
        }
    }

    public String getScript() {
        return script;
    }

    public String getVarName() {
        return varName;
    }

    public Class<V> getType() {
        return type;
    }
}
