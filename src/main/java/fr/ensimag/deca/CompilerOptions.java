package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl19
 * @date 01/01/2022
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }

    public boolean getParse(){
        return parse;
    }

    public boolean getJVMBytecode(){
        return bytecode;
    }

    public boolean getVerify(){
        return verify;
    }

    public int getRegMaxNbr(){
        return regMaxNbr;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    public boolean getCheck(){
        return this.check;
    }

    private int regMaxNbr = 16;
    private boolean parse = false;
    private boolean bytecode =false ;
    private boolean verify = false;
    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean check = true;
    private List<File> sourceFiles = new ArrayList<File>();

    
    public void parseArgs(String[] args) throws CLIException {
        int pos = 0;
        if (args.length==0) {
            throw new CLIException("No parameters given");
        }
        else if (args.length==1 && args[0].equals("-b")) {
            /* (banner) : affiche une bannière indiquant le nom de l'équipe
            */
            printBanner = true;
        }
        else {
            while(pos<args.length){
                if (args[pos].equals("-p")){
                    // Les options '-p' et '-v' sont incompatibles
                    parse = true;
                    pos ++;
                    
                } else if (args[pos].equals("-v")){
                    /* (verification) : arrête decac après l'étape de vérifications
                     *                  (ne produit aucune sortie en l'absence d'erreur)
                     */
                    verify = true;
                    pos ++;
                } else if (args[pos].equals("-j")){
                    // genere le fichier .class correspondant
                    bytecode = true;
                    pos ++;
                }else if (args[pos].equals("-n")){
                    /*   (no check) : supprime les tests à l'exécution spécifiés dans
                     *               les points 11.1 et 11.3 de la sémantique de Deca.
                     */
                    this.check = false;
                    pos ++;
                }else if (args[pos].equals("-r")){
                    /* -r X (registers) : limite les registres banalisés disponibles à 
                     *                     R0 ... R{X-1}, avec 4 <= X <= 16
                     */
                    pos ++;
                    try {
                        Integer X = Integer.parseInt(args[pos]);
                        if(X>=4 && X<=16){
                            regMaxNbr = X;
                        }
                    } catch (NumberFormatException e) {
                        throw new CLIException("Bad value for number of max register");
                    }
                    pos ++;
                }else if (args[pos].equals("-d")){
                    debug ++;
                    pos ++;
                }else if (args[pos].equals("-P")){
                    /*(parallel) : s'il y a plusieurs fichiers sources,
                     *   lance la compilation des fichiers en
                     *   parallèle (pour accélérer la compilation)
                     */
                    parallel = true;
                    pos ++;
                }else if (args[pos].contains(".deca")){
                    sourceFiles.add(new File(args[pos]));
                    pos ++;
                }else {
                    throw new CLIException("Incorrect commande");
                }
            }
        }
        

        if (verify && parse){
            throw new CLIException("Les options '-p' et '-v' sont incompatibles.");
        }

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }

    protected void displayUsage() {
        System.out.println("-b (banner) : affiche une bannière indiquant le nom de l'équipe");
        System.out.println("-p (parse) : arrête decac après l'étape de construction de\n"+
                                    "\tl'arbre, et affiche la décompilation de ce dernier\n"+
                                    "\t(i.e. s'il n'y a qu'un fichier source à\n"+
                                    "\tcompiler, la sortie doit être un programme\n"+
                                    "\tdeca syntaxiquement correct)");
        System.out.println("-v (verification) : arrête decac après l'étape de vérifications\n"+
                                    "\t(ne produit aucune sortie en l'absence d'erreur)");
        System.out.println(" -n (no check) : supprime les tests à l'exécution spécifiés dans\n"+
                                    "\tles points 11.1 et 11.3 de la sémantique de Deca.");
        System.out.println("-r X (registers) : limite les registres banalisés disponibles à\n"+
                                    "\tR0 ... R{X-1}, avec 4 <= X <= 16");
        System.out.println("-d (debug) : active les traces de debug. Répéter\n"+
                                    "\tl'option plusieurs fois pour avoir plus de\n"+
                                    "\ttraces.");
        System.out.println(" -P (parallel) : s'il y a plusieurs fichiers sources,\n"+
                                    "\tlance la compilation des fichiers en\n"+
                                    "\tparallèle (pour accélérer la compilation)");
        System.out.println("-j (JVM Bytecode) : démarre la compilation pour la JVM");
    }
}
