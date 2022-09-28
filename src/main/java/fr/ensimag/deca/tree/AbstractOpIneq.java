package fr.ensimag.deca.tree;


import fr.ensimag.deca.tools.SymbolTable;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

/**
 *
 * @author gl19
 * @date 01/01/2022
 */
public abstract class AbstractOpIneq extends AbstractOpCmp {

    public AbstractOpIneq(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
}
