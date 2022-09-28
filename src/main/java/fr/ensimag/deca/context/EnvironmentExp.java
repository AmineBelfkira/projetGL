package fr.ensimag.deca.context;

import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.Label;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * 
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * 
 * The dictionary at the head of this list thus corresponds to the "current" 
 * block (eg class).
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary and in the parentEnvironment if it fails. 
 * 
 * Insertion (through method declare) is always done in the "current" dictionary.
 * 
 * @author gl19
 * @date 01/01/2022
 */
public class EnvironmentExp {
    private HashMap<Symbol,ExpDefinition> struct = new HashMap<>();

    public HashMap<Symbol, ExpDefinition> getStruct() {
        return struct;
    }

    public void setParentEnvironment(EnvironmentExp parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
    }

    EnvironmentExp parentEnvironment;
    
    public EnvironmentExp(EnvironmentExp parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
        this.struct = new HashMap<Symbol,ExpDefinition>();
    }

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public ExpDefinition get(Symbol key) {
        ExpDefinition def = this.struct.get(key);
        if(def !=null){
            return def;
        }
        if(parentEnvironment != null)
            return parentEnvironment.get(key);
        return null;
    }
    public ExpDefinition getIfLocal(Symbol key) {
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
    public void declare(Symbol name, ExpDefinition def) throws DoubleDefException {
        if (this.struct.containsKey(name)){
            throw new DoubleDefException();
        }
        this.struct.put(name, def);
    }

    /**
     * add Other Environment content into this environment
     * @param otherEnvironment
     * @throws ContextualError
     */
    public void add(EnvironmentExp otherEnvironment) throws ContextualError {
        for (Map.Entry<Symbol, ExpDefinition> entry : otherEnvironment.getStruct().entrySet()) {
            try{
                this.declare(entry.getKey(),entry.getValue());
            }catch(Exception e){
                throw new ContextualError("Double definition",entry.getValue().getLocation());
            }
        }
    }
    
    /**
     * add methods in otherEnvironment to this environment with the corresponding Index
     * @param otherEnvironment the environment containing the methods
     * @param cls the class definition
     * @param SuperCls the super class definition
     * @throws ContextualError
     */
    public void addMethods(EnvironmentExp otherEnvironment,ClassDefinition cls, ClassDefinition SuperCls) throws ContextualError {
        
        HashMap<Integer,Symbol> map = new HashMap<>();
        MethodDefinition methodDef ;
        for (Map.Entry<Symbol, ExpDefinition> entry : otherEnvironment.getStruct().entrySet()) {
            methodDef = entry.getValue().asMethodDefinition("Error : this should only be called on methods",entry.getValue().getLocation());
            map.put(methodDef.getIndex(),entry.getKey());
        }
        ExpDefinition def ;
        for(int i=1;i<=map.size();i++){
            def =otherEnvironment.get(map.get(i)) ;
            int indexMethod ;
            if(SuperCls.getMembers().get(map.get(i))!=null){
                indexMethod = SuperCls.getMembers().get(map.get(i)).asMethodDefinition("is not method", Location.BUILTIN).getIndex();
            }else{
                cls.incNumberOfMethods();
                indexMethod = cls.getNumberOfMethods();
            }
            try{
                def.asMethodDefinition("Error : this should only be called on methods",def.getLocation()).setIndex(indexMethod);
                def.asMethodDefinition("Error : this should only be called on methods",def.getLocation()).setLabel(new Label("code."+cls.getType().getName()+"."+map.get(i)));
                this.declare(map.get(i),def);
            }catch(Exception e){
                throw new ContextualError("Double definition",def.getLocation());
            }
        }
    }
}
