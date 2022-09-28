package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Declaration of a field
 * 
 * @author gl19
 * @date 17/01/2022
 */
public class DeclField extends AbstractDeclField{
    private AbstractIdentifier type ;
    private AbstractIdentifier field ;
    private AbstractInitialization init ;

    public DeclField(AbstractIdentifier type, AbstractIdentifier field, AbstractInitialization init,Visibility visibility) {
        Validate.notNull(type);
        Validate.notNull(field);
        Validate.notNull(init);
        this.type = type;
        this.field = field;
        this.init = init;
        this.visibility =visibility;
    }
    private Visibility visibility ;

    /**
     * getter pour la visibilité du field
     * @return
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * setter pour la visibilité du field
     * @param visibility
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public AbstractIdentifier getField() {
        return this.field;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        if (this.visibility == Visibility.PROTECTED){
            s.print(visibilityString());
            s.print(" ");
        }
        type.decompile(s);
        s.print(" ");
        field.decompile(s);
        init.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,false);
        field.prettyPrint(s,prefix,false);
        init.prettyPrint(s,prefix,true);
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler, ClassDefinition superCls, ClassDefinition name,EnvironmentExp envExp)
            throws ContextualError {
        Type t= type.verifyType(compiler);
        if(t.isVoid()) {
            throw new ContextualError("field can't be Void", getLocation());
        }
        if(t.isString())
        {
            throw new ContextualError("Can't declar "+field.getName().toString() +" with type String",getLocation());
        }
        ExpDefinition def = superCls.getMembers().get(field.getName());
        if(def!=null){;
            if(!def.isField())
                throw new ContextualError(field.getName().toString()+" must be a field", getLocation());
        }
        name.setNumberOfFields(superCls.getNumberOfFields());
        name.incNumberOfFields();
        ExpDefinition exp =new FieldDefinition(t,getLocation(),visibility,name,name.getNumberOfFields());
        try{
            envExp.declare(field.getName(), exp);
            field.setDefinition(exp);
        }catch(Exception e){
            throw new ContextualError("error : double definition of "+field.getName().toString(),getLocation());
        }
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler,EnvironmentExp localEnv,ClassDefinition cls)
            throws ContextualError {
        Type t= type.verifyType(compiler);
        init.verifyInitialization(compiler,t,localEnv,cls);
    }

    @Override
    protected void codeGenField(DecacCompiler compiler) {
        // valeur d'initialization
        if (init instanceof NoInitialization) {  
                DVal value;
                Type fieldType = compiler.getEnvironmentType().get(type.getName()).getType(); 
                if (fieldType.isInt() || fieldType.isBoolean()) {
                        value = new ImmediateInteger(0);
                }
                else if (fieldType.isFloat()) {
                        value = new ImmediateFloat(0);
                } 
                else {
                    value = new NullOperand();
                }
                compiler.addInstruction(new LOAD(value, Register.R0));
        }
        else if (init instanceof Initialization) {
                DVal reg = ((Initialization)init).getExpression().codeGenReg(compiler);                                 
                compiler.addInstruction(new LOAD(reg, Register.R0));
                compiler.getRegManager().ReinitRegOccup();
        }
        // initialization des champs
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(this.getField().getFieldDefinition().getIndex(), Register.R1)));
    
}


    protected String visibilityString(){
        if(this.visibility ==Visibility.PROTECTED)
           return "protected";
        else
           return "public";
    }

    @Override
    protected String prettyPrintNode(){
        return "[Visibility : " + visibilityString().toUpperCase()+"]"+" DeclField";
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        field.iter(f);
        init.iter(f);
    }
}
