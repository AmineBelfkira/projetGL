package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;

/**
 * Definition of a method parameter.
 *
 * @author gl19
 * @date 01/01/2022
 */
public class ParamDefinition extends ExpDefinition {
    private static int ParamsIndex = 1;

    public static void resetParamIndex(){
        ParamsIndex = 1;
    }

    public int getIndex() {
        return index;
    }

    private int index;

    
    public ParamDefinition(Type type, Location location) {
        super(type, location);
        index = ParamsIndex++;
    }

    @Override
    public String getNature() {
        return "parameter";
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public boolean isParam() {
        return true;
    }

}
