package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl19
 * @date 01/01/2022
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            System.out.println("Groupe 19");
            System.exit(0);
        }
        if (options.getSourceFiles().isEmpty()) {
            System.err.println("Source File is Empty");
            options.displayUsage();
            System.exit(1);
        }
        if (options.getParallel()) {
            // A FAIRE : instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.
            int nbrParallel = Runtime.getRuntime().availableProcessors();
            ExecutorService workers = Executors.newFixedThreadPool(nbrParallel);
            List<Future<Boolean>> futures = new ArrayList<>(); 
            for (File source : options.getSourceFiles()) {
                futures.add(workers.submit(new DecacCompiler(options, source)));
            }
            for (Future<Boolean> future : futures) {
                    try {
                        if(future.get()){
                            error = true;
                        }
                    } catch (InterruptedException e) {
                        System.err.println("Execution interrupted");
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        System.err.println("Exception raised");
                    }
            }
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
