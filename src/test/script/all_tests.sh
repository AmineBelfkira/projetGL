
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

GREEN="\e[1;32m"
RED="\e[31m"


reussi=0
echec=0

test_lex_invalide () {
    # $1 = premier argument.
    if test_lex "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${GREEN} "Echec attendu pour test_lex sur $filename" 
        reussi=$(($reussi + 1))
    else
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${RED} "Succes inattendu de test_lex sur $filename"
        echec=$(($echec + 1))
        
    fi
}     
test_lex_valide () {
    # $1 = premier argument.
    if test_lex "$1" 2<&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${RED} "Echec inattendu de test_lex sur $filename"
        echec=$(($echec + 1))
    else
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${GREEN} "Succes attendu pour test_lex sur $filename" 
        reussi=$(($reussi + 1))
        
        
    fi
} 
test_synt_invalide () {
    # $1 = premier argument.
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        filename=$(echo ${cas_de_test} | xargs basename)
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${GREEN} "Echec attendu pour test_synt sur $filename" 
        reussi=$(($reussi + 1))
    else
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${RED} "Succes inattendu de test_synt sur $filename"
        echec=$(($echec + 1))
        
    fi
}     
test_synt_valide () {
    # $1 = premier argument.
    if test_synt "$1" 2<&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        filename=$(echo ${cas_de_test} | xargs basename)
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${RED} "Echec inattendu de test_synt sur $filename"
        echec=$(($echec + 1))
    else
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${GREEN} "Succes attendu pour test_synt sur $filename" 
        reussi=$(($reussi + 1))
        
        
    fi
} 
test_context_invalide () {
    # $1 = premier argument.
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${GREEN} "Echec attendu pour test_context sur $filename" 
        reussi=$(($reussi + 1))
    else
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${RED} "Succes inattendu de test_context sur $filename"
        echec=$(($echec + 1))
        
    fi
}  
test_context_valide () {
    # $1 = premier argument.
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${RED} "Echec inattendu de test_context sur $filename"
        echec=$(($echec + 1))
    else
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${GREEN} "Succes attendu pour test_context sur $filename" 
        reussi=$(($reussi + 1))
        
        
    fi
}
test_context_invalideErr() {
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${RED} "Succes inattendu de test_context sur $filename"
        echec=$(($echec + 1))
    else
        filename=$(echo ${cas_de_test} | xargs basename)
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${GREEN} "Echec attendu pour test_context sur $filename" 
        reussi=$(($reussi + 1))
    fi
}

echo  '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests Lexique invalides:"
for cas_de_test in ./src/test/deca/syntax/invalid/lex/*.deca
do
    test_lex_invalide "$cas_de_test"
done
echo     

echo  '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests Lexique valides:"
for cas_de_test in ./src/test/deca/syntax/valid/lex/*.deca
do
    test_lex_valide "$cas_de_test"
done

echo

echo  '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests Context invalides:"
for cas_de_test in ./src/test/deca/context/invalid/*.deca
do
    test_context_invalide "$cas_de_test"
done
for cas_de_test in ./src/test/deca/context/invalidErr/*.deca
do
    test_context_invalideErr "$cas_de_test"
done

echo     

echo  '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests Context valides:"
for cas_de_test in ./src/test/deca/context/valid/*.deca
do
    test_context_valide "$cas_de_test"
done

echo

echo  '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests Syntax invalides:"
for cas_de_test in ./src/test/deca/syntax/invalid/synt/*.deca
do
    test_synt_invalide "$cas_de_test"
done

echo     

echo '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests Syntax valides:"
for cas_de_test in ./src/test/deca/syntax/valid/synt/*.deca
do
    test_synt_valide "$cas_de_test"
done



PATH=./src/test/script/launchers:./src/main/bin:"$PATH"


echo
echo  '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests Gencode:"
for cas_de_test in ./src/test/deca/codegen/objet/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/objet/$filename-decompile.ass 2>/dev/null
    rm -f ./src/test/deca/codegen/objet/$filename-decompile.deca 2>/dev/null
    decac -p ./src/test/deca/codegen/objet/$filename.deca  > ./src/test/deca/codegen/objet/$filename-decompile.deca
    decac ./src/test/deca/codegen/objet/$filename-decompile.deca
    if [ ! -f ./src/test/deca/codegen/objet/$filename-decompile.ass ]; then
    echo  "Fichier $filename.ass non généré."
    fi
    resultat=$(ima ./src/test/deca/codegen/$filename-decompile.ass)
    rm -f ./src/test/deca/codegen/objet/$filename-decompile.deca
    rm -f ./src/test/deca/codegen/objet/$filename-decompile.ass 
    
    if [ "$resultat" 2>&1  ]; then
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
        reussi=$(($reussi + 1))
    elif [ -z "$resultat" ]; then
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
        reussi=$(($reussi + 1))
    else
        #echo "Résultat inattendu de ima:"
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${RED}$filename: Résultat inattendu"
        echec=$(($echec + 1))
    fi
done
for cas_de_test in ./src/test/deca/codegen/sansObjet/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/sansObjet/$filename-decompile.ass 2>/dev/null
    rm -f ./src/test/deca/codegen/sansObjet/$filename-decompile.deca 2>/dev/null
    decac -p ./src/test/deca/codegen/sansObjet/$filename.deca  > ./src/test/deca/codegen/sansObjet/$filename-decompile.deca
    decac ./src/test/deca/codegen/sansObjet/$filename-decompile.deca
    if [ ! -f ./src/test/deca/codegen/sansObjet/$filename-decompile.ass ]; then
    echo  "Fichier $filename.ass non généré."
    fi
    resultat=$(ima ./src/test/deca/codegen/$filename-decompile.ass)
    rm -f ./src/test/deca/codegen/sansObjet/$filename-decompile.deca
    rm -f ./src/test/deca/codegen/sansObjet/$filename-decompile.ass 
    
    if [ "$resultat" 2>&1  ]; then
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
        reussi=$(($reussi + 1))
    elif [ -z "$resultat" ]; then
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
        reussi=$(($reussi + 1))
    else
        #echo "Résultat inattendu de ima:"
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${RED}$filename: Résultat inattendu"
        echec=$(($echec + 1))
    fi
done
for cas_de_test in ./src/test/deca/codegen/valid/provided/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/valid/provided/$filename-decompile.ass 2>/dev/null
    rm -f ./src/test/deca/codegen/valid/provided/$filename-decompile.deca 2>/dev/null
    decac -p ./src/test/deca/codegen/valid/provided/$filename.deca  > ./src/test/deca/codegen/valid/provided/$filename-decompile.deca
    decac ./src/test/deca/codegen/valid/provided/$filename-decompile.deca
    if [ ! -f ./src/test/deca/codegen/valid/provided/$filename-decompile.ass ]; then
    echo  "Fichier $filename.ass non généré."
    fi
    resultat=$(ima ./src/test/deca/codegen/valid/provided/$filename-decompile.ass)
    rm -f ./src/test/deca/codegen/valid/provided/$filename-decompile.deca
    rm -f ./src/test/deca/codegen/valid/provided/$filename-decompile.ass 

    
    if [ "$resultat" 2>&1  ]; then
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
        reussi=$(($reussi + 1))
    elif [-z "$resultat"]; then
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
        reussi=$(($reussi + 1))
    else
        #echo "Résultat inattendu de ima:"
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${RED}$filename: Résultat inattendu"
        echec=$(($echec + 1))
    fi
done
for cas_de_test in ./src/test/deca/codegen/perf/provided/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/perf/provided/$filename-decompile.ass 2>/dev/null
    rm -f ./src/test/deca/codegen/perf/provided/$filename-decompile.deca 2>/dev/null
    decac -p ./src/test/deca/codegen/perf/provided/$filename.deca  > ./src/test/deca/codegen/perf/provided/$filename-decompile.deca
    decac ./src/test/deca/codegen/perf/provided/$filename-decompile.deca
    if [ ! -f ./src/test/deca/codegen/perf/provided/$filename-decompile.ass ]; then
    echo  "${RED}Fichier $filename.ass non généré."
    fi
    resultat=$(ima ./src/test/deca/codegen/perf/provided/$filename-decompile.ass)
    rm -f ./src/test/deca/codegen/perf/provided/$filename-decompile.deca
    rm -f ./src/test/deca/codegen/perf/provided/$filename-decompile.ass 

    
    if [ "$resultat" 2>&1  ]; then
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué ${RED}"
        reussi=$(($reussi + 1))
    elif [-z "$resultat"]; then
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué ${RED}"
        reussi=$(($reussi + 1))
    else
        #echo "Résultat inattendu de ima:"
        echo  '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${RED}$filename: Résultat inattendu ${RED}"
        echec=$(($echec + 1))
    fi
    
done

echo
echo '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests Extention Byte:"
for cas_de_test in ./src/test/deca/codegen/sansObjet/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/sansObjet/$filename.class 2>/dev/null
    decac -j ./src/test/deca/codegen/sansObjet/$filename.deca
    if [ ! $filename.class ]; then
    echo  "Fichier $filename.class non généré."
    fi
    resultat=$(java $filename)
    rm -f $filename.class
    
    if [ "$resultat" 2>&1  ]; then
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
        reussi=$(($reussi + 1))
    elif [ -z "$resultat" ]; then
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
        reussi=$(($reussi + 1))
    else
        #echo "Résultat inattendu de Byte:"
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${RED}$filename: Résultat inattendu"
        echec=$(($echec + 1))
    fi
done


echo 
echo  '\e[1;37m'['\e[1;34m'INFO'\e[1;37m']'\e[1;35m'"  Totale tests réussis : $reussi  "
echo  '\e[1;37m'['\e[1;34m'INFO'\e[1;37m']'\e[1;35m'"  Totale tests échoués : $echec  "

