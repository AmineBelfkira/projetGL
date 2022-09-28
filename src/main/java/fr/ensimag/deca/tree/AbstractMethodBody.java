package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Method body
 *
 * @author gl19
 * @date 17/01/2022
 */
public abstract class AbstractMethodBody extends Tree{


    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the method body are OK.
     */
    protected abstract void verifyClassBody(DecacCompiler compiler, EnvironmentExp envExp, EnvironmentExp params, ClassDefinition currentClass, Type ret)
            throws ContextualError ;

    /**
     * Generate assembly code of the method body 
     * @param compiler
     */
    protected abstract void codeGenInst(DecacCompiler compiler);
}
