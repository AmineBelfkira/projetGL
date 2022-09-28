package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Parametre declaration 
 *
 * @author gl19
 * @date 17/01/2022
 */
public abstract class AbstractDeclParam extends Tree {

        /**
        * Pass 2 of [SyntaxeContextuelle]. Verify that the method parametre are OK, without looking the method body.
        */
        protected abstract Type verifyClassMembers(DecacCompiler compiler)
            throws ContextualError ;

        /**
        * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
        * contained in the method body are OK.
        */
        protected abstract void verifyClassBody(DecacCompiler compiler, EnvironmentExp env)
            throws ContextualError ;
}
