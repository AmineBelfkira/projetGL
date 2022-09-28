package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

/**
 * Method call
 * @author gl19
 * @date 17/01/2022
 */
public class MethodCall extends AbstractExpr{
    private AbstractExpr obj ;
    private AbstractIdentifier method ;
    private ListExpr params ;

    public MethodCall(AbstractExpr obj, AbstractIdentifier method,ListExpr param) {
        Validate.notNull(obj);
        Validate.notNull(method);
        Validate.notNull(param);
        this.obj = obj;
        this.method = method ;
        this.params = param ;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        // ADDSP #nb variables locales
        compiler.addInstruction(new ADDSP(1 + params.size()));

        // empile obj
        int numPop = compiler.getRegManager().getNeedPop();
        GPRegister reg =  obj.genRegister(compiler);
        if (obj instanceof This) {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), reg));
        }
        else {
            compiler.addInstruction(new LOAD(new RegisterOffset(compiler.getRegManager().getGBOffset(((Identifier)obj).getName()), Register.GB), reg));
        }
        compiler.addInstruction(new STORE(reg, new RegisterOffset(0, Register.SP)));
        compiler.getRegManager().freeLastOccupiedReg();
        // Pop if push during empile obj
        if (compiler.getRegManager().getNeedPop() > numPop) {
            compiler.addInstruction(new POP(reg));
        }

        // empile params
        if (!params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                numPop = compiler.getRegManager().getNeedPop();
                DVal registerUsed = params.getList().get(i).codeGenReg(compiler);
                GPRegister register ;
                if (registerUsed instanceof GPRegister){
                    register = (GPRegister)registerUsed;
                }else{
                    register = genRegister(compiler);
                    compiler.addInstruction(new LOAD(registerUsed, register));
                }
                compiler.addInstruction(new STORE(register, new RegisterOffset(-(i+1), Register.SP)));
                compiler.getRegManager().freeLastOccupiedReg();
                if (compiler.getRegManager().getNeedPop() > numPop) {
                    compiler.addInstruction(new POP(register));
                }
            }
        }
        numPop = compiler.getRegManager().getNeedPop();
        // Appel de la méthode
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.getR(compiler.getRegManager().getIndexRegistre())));

        
        reg = Register.getR(compiler.getRegManager().getIndexLastRegister());
        // On vérifie que objet est non null. Dans le cas où objet est null,
        // on lève une exception
        compiler.addInstruction(new CMP(new NullOperand(), reg));
        compiler.addInstruction(new BEQ(new Label("dereferencement_null")));

        // adresse de la méthode 'method' de l'objet 'obj'
        compiler.addInstruction(new LOAD(new RegisterOffset(0, reg), reg));
        compiler.addInstruction(new BSR(new RegisterOffset(method.getMethodDefinition().getIndex(), reg)));
        compiler.addInstruction(new SUBSP(1 + params.size()));
        
        // Libère le dernier registre utilisé dans le dernier LOAD
        if (compiler.getRegManager().getNeedPop() > numPop) {
            compiler.addInstruction(new POP(reg));
        }else{
            compiler.getRegManager().freeLastOccupiedReg();
        }
        
        if(!method.getMethodDefinition().getType().isVoid()){
            compiler.addInstruction(new LOAD(Register.R0, genRegister(compiler)));
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        this.codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.R0, Register.R1));
        if (method.getType().isInt()) {
            compiler.addInstruction(new WINT());
        }
        else {
            compiler.addInstruction(new WFLOAT());;
        }
    }

    @Override
    protected void codeGenPrintHexa(DecacCompiler compiler) {
        this.codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.R0, Register.R1));
        if (method.getType().isInt()) {
            compiler.addInstruction(new FLOAT(Register.R1, Register.R1));
        }
        compiler.addInstruction(new WFLOATX());
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        if((obj instanceof This) && currentClass == null){
            throw new ContextualError("Call error",getLocation());
        }
        Type t_class = obj.verifyExpr(compiler,localEnv,currentClass);
        EnvironmentExp env_exp2 = t_class.asClassType("error",getLocation()).getDefinition().getMembers();
        Type retType = method.verifyExpr(compiler,env_exp2,currentClass);
        Signature sig = method.getMethodDefinition().getSignature();
        if(sig.size()!=params.size())
            throw new ContextualError("Wrong number of parameters: expected "+sig.size()+", was given "+params.size(),getLocation());
        if(sig.size()==0)
            return retType;
        List<AbstractExpr> list = params.getList();
        for(int i=0;i<sig.size();i++){
            Type t = list.get(i).verifyExpr(compiler,localEnv,currentClass);
            if(!(compiler.getEnvironmentType().assign_compatible(sig.paramNumber(i),t)))
                throw new ContextualError(" method call failed",getLocation());
        }
        return retType;
    }

    @Override
    protected void checkDecoration() {
        // nothing
    }

    @Override
    public void decompile(IndentPrintStream s) {
        obj.decompile(s);
        if (obj instanceof This){
            if(!obj.isImplicit()){
                s.print(".");
            }
        } else {
            s.print(".");
        }
        method.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        obj.prettyPrint(s, prefix, false);
        method.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        obj.iter(f);
        method.iter(f);
        params.iter(f);
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        // nothing
    }
}
