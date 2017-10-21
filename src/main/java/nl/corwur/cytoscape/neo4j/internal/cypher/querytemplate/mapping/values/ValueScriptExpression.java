package nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.mapping.values;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public abstract class ValueScriptExpression<T,V> implements ValueExpression<T,V> {
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
            V result = type.cast(engine.eval(script));
            return result;
        } catch (ScriptException e) {
            return null;
        } catch (ClassCastException e) {
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
