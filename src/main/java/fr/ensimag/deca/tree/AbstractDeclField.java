package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Field declaration 
 *
 * @author gl19
 * @date 17/01/2022
 */
public abstract class AbstractDeclField extends Tree{

        /**
         * Pass 2 of [SyntaxeContextuelle]. Verify that field is OK, without looking at the field initialization.
         */
        protected abstract void verifyClassMembers(DecacCompiler compiler, ClassDefinition superCls, ClassDefinition currentclass,EnvironmentExp envExp)
                throws ContextualError ;

        /**
         * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
         * contained in the field declaration are OK.
         */
        protected abstract void verifyClassBody(DecacCompiler compiler,EnvironmentExp env,ClassDefinition cls)
                throws ContextualError ;


        /**
         * Genere le code assembleur de declaration d'un field
         * @param compiler
         */
        protected abstract void codeGenField(DecacCompiler compiler);

}
