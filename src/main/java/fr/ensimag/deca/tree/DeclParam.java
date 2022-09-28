package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * declaration d'un parametre
 * @author gl19
 * @date 01/01/2022
 */
public class DeclParam extends AbstractDeclParam{
    private AbstractIdentifier type;
    private AbstractIdentifier name ;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier name) {
        Validate.notNull(type);
        Validate.notNull(name);
        this.type = type;
        this.name = name;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
    }
    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,false);
        name.prettyPrint(s,prefix,true);
    }



    @Override
    protected Type verifyClassMembers(DecacCompiler compiler) throws ContextualError {
        Type t =type.verifyType(compiler);
        if(t.isVoid())
            throw new ContextualError("parameter can't be void",getLocation());
        return t;
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler,EnvironmentExp env)
            throws ContextualError {
        Type t = type.verifyType(compiler);
        ParamDefinition def = new ParamDefinition(t,getLocation());
        try {
            env.declare(name.getName(), def);
            name.setDefinition(def);
        }catch(Exception e){
            throw new ContextualError("Error : double definition of "+name.getName().toString(),getLocation());
        }
    }
}
