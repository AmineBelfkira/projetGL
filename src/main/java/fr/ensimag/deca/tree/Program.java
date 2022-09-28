package fr.ensimag.deca.tree;
import org.objectweb.asm.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelException;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl19
 * @date 01/01/2022
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        try {
            setBuiltInTypes(compiler);
        } catch (Exception e) {
            throw new ContextualError("Double definition in BuiltIn types", getLocation());
        }

        classes.verifyListClass(compiler);
        classes.verifyListClassMembers(compiler);
        classes.verifyListClassBody(compiler);

        main.verifyMain(compiler);
        LOG.debug("verify program: end");
    }

    void setBuiltInTypes(DecacCompiler compiler) throws EnvironmentType.DoubleDefException, EnvironmentExp.DoubleDefException {
        EnvironmentType envType = compiler.getEnvironmentType();
        SymbolTable sTable = compiler.getSymbolTable();
        envType.declare(sTable.create("int"), new TypeDefinition(new IntType(sTable.create("int")), Location.BUILTIN));
        envType.declare(sTable.create("String"), new TypeDefinition(new StringType(sTable.create("String")), Location.BUILTIN));
        envType.declare(sTable.create("float"), new TypeDefinition(new FloatType(sTable.create("float")), Location.BUILTIN));
        envType.declare(sTable.create("boolean"), new TypeDefinition(new BooleanType(sTable.create("boolean")), Location.BUILTIN));
        envType.declare(sTable.create("void"), new TypeDefinition(new VoidType(sTable.create("void")), Location.BUILTIN));
        envType.declare(sTable.create("null"),new TypeDefinition(new NullType(sTable.create("null")),Location.BUILTIN));
        ClassDefinition def = (new ClassType(sTable.create("Object"),Location.BUILTIN,null)).getDefinition();
        def.setClassGBOffset(1);

        MethodDefinition methodDef = new MethodDefinition(envType.get(sTable.create("boolean")).getType(),Location.BUILTIN,new Signature(),1); 
        methodDef.setLabel(new Label("code.Object.equals"));
        def.getMembers().declare(sTable.create("equals"),methodDef);
        def.incNumberOfMethods();
        envType.declare(sTable.create("Object"),def);
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        //TSTO AND OVERFLOW and addsp
        int addSP = compiler.getRegManager().getSP(this);
        compiler.addInstruction(new TSTO(new ImmediateInteger(addSP + ListDeclMethod.nbMaxPara + 3)));
        compiler.addInstruction(new BOV(LabelException.stack_overflow));
        compiler.addInstruction(new ADDSP(new ImmediateInteger(addSP)));
        
        //construction des tables
        codeGenTable(compiler);

        compiler.addComment("Main program");
        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());

        //construction des classes
        codeGenObject(compiler);
        classes.codeGenClasses(compiler);
    }

    protected void codeGenTable(DecacCompiler compiler){
        // Construction de la table des methodes de Object
        compiler.addComment("Construction des tables des méthodes de Object:");
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(1, Register.GB)));
        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")),Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(2, Register.GB)));
        compiler.getRegManager().incrementeGB();
        //  Construction de la table des methodes de toutes les classes
        for (AbstractDeclClass classe: classes.getList()) {
            classe.codeGenClassTable(compiler);
        }
    }

    protected void codeGenObject(DecacCompiler compiler){
        // Initialisation des champs de Object
        compiler.addLabel(new Label("init.Object"));
        compiler.addInstruction(new RTS());
        // Code de la methode equals
        compiler.addLabel(new Label("code.Object.equals"));
        compiler.addInstruction(new TSTO(2));
        compiler.addInstruction(new BOV(LabelException.stack_overflow));
        GPRegister reg2 = Register.getR(2);
        GPRegister reg3 = Register.getR(3);
        compiler.addInstruction(new PUSH(reg2));
        compiler.addInstruction(new PUSH(reg3));
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), reg2));
        compiler.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), reg3));
        compiler.addInstruction(new CMP(reg2, reg3));
        compiler.addInstruction(new SEQ(Register.R0));
        compiler.addInstruction(new POP(reg3));
        compiler.addInstruction(new POP(reg2));
        compiler.addInstruction(new RTS());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        main.genByte(method,indexTable);
    }
    public void genBytecode(String prefix){
        String classname = prefix.substring(0,prefix.length()-5);
        // creates a ClassWriter for the Main public class,
        // which inherits from Object
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_1, ACC_PUBLIC, classname, null, "java/lang/Object", null);

        // creates a MethodWriter for the (implicit) constructor
        MethodVisitor mainVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null,
                null);
        mainVisitor.visitCode();
        // pushes the 'this' variable
        mainVisitor.visitVarInsn(ALOAD, 0);

        // invokes the super class constructor
        mainVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V",false);
        mainVisitor.visitInsn(RETURN);

        // this code uses a maximum of one stack element and one local variable
        mainVisitor.visitMaxs(1, 1);
        mainVisitor.visitEnd();

        // creates a MethodWriter for the 'main' method
        mainVisitor = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
                "([Ljava/lang/String;)V", null, null);
        mainVisitor.visitCode();

        //  ->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        HashMap<SymbolTable.Symbol,Integer> indexTable =new HashMap<>();
        this.genByte(mainVisitor,indexTable);

        //  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<-

        mainVisitor.visitInsn(RETURN);
        mainVisitor.visitMaxs(-1, -1);
        mainVisitor.visitEnd();
        cw.visitEnd();

        // gets the bytecode of the Example class, and loads it dynamically
        byte[] code = cw.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(classname+".class");
            fos.write(code);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
