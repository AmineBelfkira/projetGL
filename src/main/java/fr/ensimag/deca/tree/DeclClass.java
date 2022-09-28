package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelException;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

import org.apache.commons.lang.Validate;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Declaration of a class (<code>class name extends superClass {members}</code>).
 * 
 * @author gl19
 * @date 01/01/2022
 */
public class DeclClass extends AbstractDeclClass {
    private AbstractIdentifier name;
    private AbstractIdentifier superCls;
    private ListDeclField listField ;
    private ListDeclMethod listMethod ;

    private static Label methodFin;

    public static Label getmethodFin(){
        return methodFin;
    }

    public DeclClass(AbstractIdentifier name, AbstractIdentifier superCls,
                     ListDeclField listField, ListDeclMethod listMethod) {
        Validate.notNull(name);
        Validate.notNull(superCls);
        Validate.notNull(listField);
        Validate.notNull(listMethod);
        this.name = name;
        this.superCls = superCls;
        this.listField = listField;
        this.listMethod = listMethod;
    }

    @Override
    public void codeGenClassTable(DecacCompiler compiler) {
        
        int indexSupClass = superCls.getClassDefinition().getClassGBOffset();
        
        compiler.addComment("Construction de la table de méthode de " + name.getName() + ":");
        compiler.addInstruction(new LEA(new RegisterOffset(indexSupClass,  Register.GB), Register.R0));
        name.getClassDefinition().setClassGBOffset(compiler.getRegManager().getGB());
        int indexCurrentClass = compiler.getRegManager().getGB();
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(indexCurrentClass, Register.GB)));
        compiler.getRegManager().incrementeGB();

        //mettre relation dans lienClass;key = parent, valeur =current;
        compiler.getRegManager().setClassParent(indexSupClass, indexCurrentClass);

        // creation d'une Tree Map 
        Map<Integer,MethodDefinition> methods = new HashMap<>();

        ClassDefinition superDefinition = name.getClassDefinition();
        while(superDefinition!=null){
            
            for (Map.Entry<Symbol, ExpDefinition> entry : superDefinition.getMembers().getStruct().entrySet()) {
                if(!entry.getValue().isMethod()){
                    continue;
                }
                try {
                    if (!(methods.containsKey(entry.getValue().asMethodDefinition("this should be a method", getLocation()).getIndex()))){
                        methods.put(entry.getValue().asMethodDefinition("this should be a method", getLocation()).getIndex(),
                                    entry.getValue().asMethodDefinition("this should be a method", getLocation()));
                    }
                } catch (ContextualError e) {
                    System.err.println(entry.getKey());
                    System.err.println("this shouldn't be raised inside codeGenClassTable");
                }

            }
            superDefinition = superDefinition.getSuperClass();
        }

        for(int i = 1; i<=name.getClassDefinition().getNumberOfMethods();i++){
            compiler.addInstruction(new LOAD(new LabelOperand(((MethodDefinition)methods.get(i)).getLabel()),Register.R0));
            compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(compiler.getRegManager().getGB(), Register.GB)));
            compiler.getRegManager().incrementeGB();
        }
    }

    @Override
    public void codeGenClass(DecacCompiler compiler) {
        // Initialisation des champs
        compiler.addLabel(new Label("init." + name.getName()));
  
        if(listField.getList().size() != 0)
        {
            int nbrField = ((DeclField)listField.getList().get(listField.getList().size()-1)).getField().getFieldDefinition().getIndex();
            compiler.addInstruction(new TSTO(nbrField));
            compiler.addInstruction(new BOV(LabelException.stack_overflow));
        }

        // lappelle du super class init
        if (!superCls.getName().toString().equals("Object")) {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
            compiler.addInstruction(new PUSH(Register.R0));
            compiler.addInstruction(new BSR(new Label("init." + superCls.getName())));
            compiler.addInstruction(new SUBSP(1));
        }

        // Traitement des champs
        listField.codeGenListField(compiler);

        // Retour
        compiler.addInstruction(new RTS());

        // Traitement des méthodes
        for (AbstractDeclMethod method : listMethod.getList()) {
            compiler.addLabel(new Label("code." + name.getName() + "." + method.getIdentName().getName()));
            methodFin = new Label("fin."+ name.getName() + "." + method.getIdentName().getName());
            method.codeGenMethod(compiler);
            if (!(method.getIdentName().getType().isVoid())){
                compiler.addInstruction(new WSTR("Erreur : sortie de la methode "+
                                                name.getName() + "." + 
                                                method.getIdentName().getName() +
                                                " sans return"));
                compiler.addInstruction(new WNL());
                compiler.addInstruction(new ERROR());
            }
            compiler.addLabel(methodFin);
            method.codeGenRestore(compiler);
        }
    }
    
    public int getNbSP(){
        int nbSP = 1;//addresse de class
        nbSP += name.getClassDefinition().getNumberOfMethods();
        return nbSP;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        name.decompile(s);
        s.print(" extends ");
        superCls.decompile(s);
        s.println(" {");
        s.indent();
        listField.decompile(s);
        listMethod.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError{
        // check if type is well defined
        Type type = superCls.verifyType(compiler);
        // check if type is a classType
        if(!type.isClass()){
            throw new ContextualError( type.toString() + " is not defined as a Class type " , getLocation());
        }
        ClassDefinition cls = (new ClassType(name.getName(),getLocation(),superCls.getClassDefinition())).getDefinition();
        try {
            // define a new class : name-> class(super,{},Class_type(name))
            compiler.getEnvironmentType().declare(name.getName(), cls);
            name.setDefinition(cls);
            name.setType(cls.getType());
        } catch (Exception e){
            throw new ContextualError(cls.getType().toString()+ " is already defined " , getLocation());
        }
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        EnvironmentExp envFields = listField.verifyClassMembers(compiler,superCls.getClassDefinition(),name.getClassDefinition());
        EnvironmentExp envMethods = listMethod.verifyClassMembers(compiler,superCls.getClassDefinition());
        EnvironmentExp env_exp_super = name.getClassDefinition().getMembers();
        // dijoint union of fields
        env_exp_super.add(envFields);
        //init number_of_methods of current class ie name
        name.getClassDefinition().setNumberOfMethods(superCls.getClassDefinition().getNumberOfMethods());
        // disjoint union of methods
        env_exp_super.addMethods(envMethods,name.getClassDefinition(), superCls.getClassDefinition());
        env_exp_super.setParentEnvironment(superCls.getClassDefinition().getMembers());
    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        EnvironmentExp localEnv = name.getClassDefinition().getMembers();
        listField.verifyClassBody(compiler,localEnv,name.getClassDefinition());
        listMethod.verifyClassBody(compiler,localEnv,name.getClassDefinition());
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s,prefix,false);
        superCls.prettyPrint(s,prefix,false);
        listField.prettyPrint(s,prefix,false);
        listMethod.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        name.iter(f);
        superCls.iter(f);
        listField.iter(f);
        listMethod.iter(f);
    }

}
