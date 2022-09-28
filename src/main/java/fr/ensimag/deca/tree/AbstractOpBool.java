package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl19
 * @date 01/01/2022
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    protected static int nbLabel = 0 ;

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type leftType = super.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType =  super.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if (!leftType.isBoolean() || !rightType.isBoolean()){
            throw new ContextualError("Can not operate on " + leftType.getName() + " and " + rightType.getName(), getLocation());
        }
        setType(leftType);
        return leftType;
    }

}

