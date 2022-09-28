package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelException;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * declaration d'une methode
 * @author gl19
 * @date 01/01/2022
 */
public class DeclMethod extends AbstractDeclMethod{
    public static int COUNT =0 ;
    private AbstractIdentifier type ;
    private ListDeclParam params ;
    private AbstractMethodBody body ;

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier name, ListDeclParam params, AbstractMethodBody body) {
        Validate.notNull(type);
        Validate.notNull(name);
        Validate.notNull(params);
        Validate.notNull(body);
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    /**
     * renvoie le nombre de parametre de la methode
     * @return
     */
    public int getNbParam()
    {
        return this.params.size();
    }


    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(")");
        body.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,false);
        name.prettyPrint(s,prefix,false);
        params.prettyPrint(s,prefix,false);
        body.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
        params.iter(f);
        body.iter(f);
    }


    @Override
    protected void verifyClassMembers(DecacCompiler compiler, ClassDefinition superCls,EnvironmentExp envExp)
            throws ContextualError {
        Type t= type.verifyType(compiler);
        Signature sig = params.verifyClassMembers(compiler);
        ExpDefinition def = superCls.getMembers().get(name.getName());
        if(def!=null){
            MethodDefinition method=superCls.getMembers().get(name.getName()).asMethodDefinition(name.getName().toString()+" is not a method",getLocation());
            if((!sig.equals(method.getSignature()))||(!compiler.getEnvironmentType().subtype(t,method.getType())))
                throw new ContextualError("Error : method can't be overLoad ", getLocation());
        }
        ExpDefinition exp =new MethodDefinition(t,getLocation(),sig,++COUNT);
        try{
            envExp.declare(name.getName(), exp);
            name.setDefinition(exp);
            name.setType(t);
        }catch(Exception e){
            throw new ContextualError("Error : double definition ",getLocation());
        }
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler,EnvironmentExp localEnv,ClassDefinition cls)
            throws ContextualError {
        Type ret = type.verifyType(compiler);
        EnvironmentExp envParams = params.verifyClassBody(compiler);
        body.verifyClassBody(compiler,localEnv,envParams,cls,ret);
    }


    @Override
    protected void codeGenMethod(DecacCompiler compiler) {
        int paramNbr = ((MethodDefinition)name.getDefinition()).getSignature().size();
        compiler.addInstruction(new TSTO(paramNbr + 1));
        compiler.addInstruction(new BOV(LabelException.stack_overflow));
        if ((body instanceof MethodBody)){
            int localVar = ((MethodBody)body).getDeclVar().getList().size();
            if(localVar!=0){
                compiler.addInstruction(new ADDSP(localVar));
            }
            compiler.addComment("Sauvegarde des registres");
            int nbPush = 1 + paramNbr + 2 * ((MethodBody)body).getDeclVar().getList().size();
            for(int i = 2; i< 2 + nbPush; i++){
                compiler.addInstruction(new PUSH(Register.getR(i)));
            }
            compiler.getRegManager().ReinitRegOccup();
        }
        body.codeGenInst(compiler);
    }


    @Override
    public void codeGenRestore(DecacCompiler compiler) {
        if ((body instanceof MethodBody)){
            int paramNbr = ((MethodDefinition)name.getDefinition()).getSignature().size();
            compiler.addComment("Restauration des registres");
            int nbPush = 1 + paramNbr + 2 * ((MethodBody)body).getDeclVar().getList().size();
            for(int i = nbPush+1; i>1; i--){
                compiler.addInstruction(new POP(Register.getR(i)));
            }
        }
        compiler.addInstruction(new RTS());
    }
}
