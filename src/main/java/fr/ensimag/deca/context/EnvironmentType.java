package fr.ensimag.deca.context;

import java.util.HashMap;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

/**
 * Dictionary associating identifier's TypeDefinition to their names.
 * @author gl19
 * @date 10/01/2022
 */
public class EnvironmentType {

    private HashMap<Symbol,TypeDefinition> struct = new HashMap<>();

    public EnvironmentType() {
        this.struct = new HashMap<Symbol,TypeDefinition>();
    }

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    public HashMap<Symbol, TypeDefinition> getStruct() {
        return struct;
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */

    // checks if a symbol is already defined
    public TypeDefinition get(Symbol key) {
        return this.struct.get(key);
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary 
     * - or, hides the previous declaration otherwise.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, TypeDefinition def) throws DoubleDefException {
        if (this.struct.containsKey(name)){
            throw new DoubleDefException();
        }
        this.struct.put(name, def);
    }

    public boolean subtype(Type type,Type otherType){
        // subtype(_,null) is false
        if(otherType==null){
            return false ;
        }
        // subtype(null,classType) is true
        if((type==null) && (otherType.isClass())){
            return true ;
        }
        // subtype(type,type) is true
        if (type.sameType(otherType)){
            return true ;
        }
        // if one of the two types is not a class return false
        if(!(type.isClass()) || !(otherType.isClass())){
            return false ;
        }
        // subType(ClassType,Object) is true
        if(otherType.getName().getName().equals("Object")){
            return true;
        }
        // subType(class ,a super class) is true
        if(((ClassType)type).isSubClassOf((ClassType)otherType)){
            return true ;
        }
        return false ;
    }

    /**
     * renvoie true si on peut affecter à un objet de type "type" une valeur de type "otherType"
     * @param type
     * @param otherType
     * @return
     */
    public boolean assign_compatible(Type type ,Type otherType){
        if(type.isFloat()&& otherType.isInt()){
            return true ;
        }
        return subtype(otherType,type);
    }

    /**
     * renvoie true si On peut convertir une valeur de type "otherType" en une valeur de type "type"
     * @param type
     * @param otherType
     * @return
     */
    public boolean cast_compatible(Type type ,Type otherType){
        if (type.isVoid()){
            return false;
        }
        return assign_compatible(type,otherType) || assign_compatible(otherType,type);
    }
}
