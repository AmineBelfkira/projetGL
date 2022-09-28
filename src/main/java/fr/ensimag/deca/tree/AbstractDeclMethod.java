package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Method declaration 
 *
 * @author gl19
 * @date 17/01/2022
 */
public abstract class AbstractDeclMethod extends Tree{
    protected AbstractIdentifier name ;

    public AbstractIdentifier getIdentName() {
        return this.name;
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the method is OK, without looking the method body.
     */
    protected abstract void verifyClassMembers(DecacCompiler compiler, ClassDefinition superCls,EnvironmentExp envExp) throws ContextualError ;


    /**
    * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
    * contained in the method body are OK.
    */
    protected abstract void verifyClassBody(DecacCompiler compiler,EnvironmentExp env,ClassDefinition cls)
            throws ContextualError ;

    /**
     * Genere le code assembleur du debut d'une method 
     * @param compiler
     */
    protected abstract void codeGenMethod(DecacCompiler compiler);

    /**
     * Genere le code assembleur de la fin d'une method 
     * @param compiler
     */
    public abstract void codeGenRestore(DecacCompiler compiler);
}
